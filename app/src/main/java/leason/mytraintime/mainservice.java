package leason.mytraintime;

import android.app.Service;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

/**
 * Created by leason on 16/10/2.
 */
public class mainservice extends Service {

    //static String DataUri = "http://162.243.4.139/php/TrainData.db";
    static String DataUri = "http://leason.hol.es/TrainData.db";

    Handler handler;
    File file,HistoryXml;


    String startStation, endStation, time;
    Cursor c;
    SQLiteDatabase trainDB;
    private List<Map<String, Object>> search_results;
    String stationName;
static mainservice instance;
    int type;  //   全部＝1  對號車＝2 非對號車 ＝3
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {


        instance=this;
        //history
        HistoryXml=new File("/data/data/leason.mytraintime/history.xml");
        if(!HistoryXml.exists()){
            try {

                HistoryXml.createNewFile();

                DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
                DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
                Document document =documentBuilder.newDocument();
                Element rootElement =document.createElement("History");
                document.appendChild(rootElement);
                TransformerFactory transformerFactory= TransformerFactory.newInstance();

                Transformer transformer= transformerFactory.newTransformer();
                DOMSource source=new DOMSource(document);
                StreamResult result=new StreamResult(HistoryXml);
                transformer.transform(source,result);

            }


            catch (ParserConfigurationException e) {
                    e.printStackTrace();
                }
            catch (IOException e) {
                e.printStackTrace();
            } catch (TransformerConfigurationException e) {
                e.printStackTrace();
            } catch (TransformerException e) {
                e.printStackTrace();
            }


        }



        //database
        file = new File("/data/data/leason.mytraintime/databases/TrainData.db");

        if (!file.exists()) {


             new Thread(new Runnable() {
                @Override
                public void run() {
                    Message msg=new Message();
                    msg.what=1;
                    MainActivity.instance.handler.sendMessage(msg);
                    getData();
                 msg = new Message();
                    msg.what=2;
                    MainActivity.instance.handler.sendMessage(msg);
                }
            }).start();




        }
        else{
            Message mes=new Message();
            mes.what=3;
            MainActivity.instance.handler.sendMessage(mes);
        }



        return START_REDELIVER_INTENT;
    }
    public void getData() {


        try {


            File dir = new File("/data/data/leason.mytraintime/databases");
            dir.mkdir();
            file.createNewFile();
            URL url = new URL(DataUri);
            url.openConnection();
            InputStream inputStream = url.openStream();

            //file.createNewFile();
            OutputStream outputStream = new FileOutputStream(file);
            byte buf[] = new byte[4096];
            int length;
            while ((length = inputStream.read(buf)) != -1) {

                outputStream.write(buf, 0, length);

            }
            outputStream.flush();
            outputStream.close();
            inputStream.close();


        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }
    void addHistory(String departure,String arrival){
        try {
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
        Document document =documentBuilder.parse(new FileInputStream(HistoryXml));

       Element rootElement =document.getDocumentElement();
            Element message =document.createElement("message");
            rootElement.appendChild(message);

            Element departureElement =document.createElement("departure");
            departureElement.appendChild(document.createTextNode(departure));
            message.appendChild(departureElement);

            Element arrivalElement =document.createElement("arrival");
            arrivalElement.appendChild(document.createTextNode(arrival));
            message.appendChild(arrivalElement);


        TransformerFactory transformerFactory= TransformerFactory.newInstance();
        Transformer  transformer = transformerFactory.newTransformer();
        DOMSource source=new DOMSource(document);
        StreamResult result=new StreamResult(HistoryXml);
        transformer.transform(source,result);



        } catch (TransformerConfigurationException e) {
            e.printStackTrace();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (TransformerException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        }
    };


void searchforResult(Bundle bundle){
     startStation = bundle.getString("startStation");
    endStation = bundle.getString("endStation");
    time = bundle.getString("time");
    type = bundle.getInt("type");
    new Thread(new Runnable() {
        @Override
        public void run() {
        Message mes=new Message();
            mes.obj=search();
            SearchResult.instance.handler.sendMessage(mes);

        }
    }).start();
}
    RecyclerAdapter search() {
        Log.i("timetest","startsearch");
        search_results = new ArrayList<Map<String, Object>>();

         // c =trainDB.rawQuery("SELECT TrainNo,Direction,StopTimes FROM Train", new String[]{"TrainNo", "Direction", "StopTimes"});
            trainDB=MainActivity.instance.trainDB;
        switch (type)

        {
            case 1: //全部
                c = trainDB.query("Train", new String[]{"TrainNo", "StopTimes", "Note","TrainType"}, null, null, null, null, null);
                break;
            case 2://對號
                c = trainDB.query("Train", new String[]{"TrainNo", "StopTimes", "Note","TrainType"}, "TrainType!=4", null, null, null, null);
                break;
            case 3://非對號
                c = trainDB.query("Train", new String[]{"TrainNo", "StopTimes", "Note","TrainType"}, "TrainType=4", null, null, null, null);
                break;

        }
        Log.i("timetest","sql query END");
        int choosehour = Integer.parseInt(time.substring(0, time.indexOf(":")));
        int chooseminute = Integer.parseInt(time.toString().substring(time.indexOf(":") + 1, time.length()));
        Map search_result;
        c.moveToFirst();
        String StopTimeJson, ArrivalTime , DepartureTime;
        List<StopTime> StopTimeList=null;
        ObjectMapper mapper = new ObjectMapper();
        for (int i = 0; i < c.getCount(); i++) {


            StopTimeJson = c.getString(c.getColumnIndex("StopTimes"));
            StopTimeJson=  StopTimeJson.toLowerCase();

            try {
                 StopTimeList = mapper.readValue(StopTimeJson, new TypeReference<List<StopTime>>(){});
            } catch (IOException e) {
                e.printStackTrace();
            }



            for (int arraycount = 0; arraycount < StopTimeList.size(); arraycount++) {

                stationName=StopTimeList.get(arraycount).getstationname().getzh_tw();
                if (stationName.compareTo(startStation) == 0)

                {
                    DepartureTime = StopTimeList.get(arraycount).getdeparturetime();

                    int DepartureHour = Integer.parseInt(DepartureTime.substring(0, DepartureTime.indexOf(":")));
                    int DepartureMinute = Integer.parseInt(DepartureTime.substring(DepartureTime.indexOf(":") + 1, DepartureTime.length()));


                    Boolean check;

                    if (DepartureHour > choosehour) {
                        check = true;
                    } else if (DepartureHour == choosehour && DepartureMinute > chooseminute)
                        check = true;
                    else {
                        check = false;
                    }
                    if (check) {


                        for (int arraycount2 = arraycount; arraycount2 < StopTimeList.size(); arraycount2++) {

                            stationName=StopTimeList.get(arraycount2).getstationname().getzh_tw();

                            if (stationName.compareTo(endStation) == 0) {
                            search_result = new HashMap<String, Object>();
                              int type= c.getInt(c.getColumnIndex("TrainType"));
                                ArrivalTime=StopTimeList.get(arraycount2).getarrivaltime();
                                search_result.put("TrainNo", c.getString(c.getColumnIndex("TrainNo")));
                                search_result.put("Note", c.getString(c.getColumnIndex("Note")));

                                search_result.put("DepartureTime", DepartureTime);
                                search_result.put("DepartureHour", DepartureHour);
                                search_result.put("DepartureMinute", DepartureMinute);

                                int ArrivalHour = Integer.parseInt(ArrivalTime.substring(0, ArrivalTime.indexOf(":")));
                                int ArrivalMinute = Integer.parseInt(ArrivalTime.substring(ArrivalTime.indexOf(":") + 1, ArrivalTime.length()));

                                search_result.put("ArrivalTime", ArrivalTime);
                                search_result.put("ArrivalHour", DepartureHour);
                                search_result.put("ArrivalMinute", DepartureMinute);

                                if((ArrivalMinute-DepartureMinute)<0)
                                {
                                    ArrivalHour--;
                                    ArrivalMinute=ArrivalMinute+60;
                                }

                                search_result.put("hour",ArrivalHour-DepartureHour);
                                search_result.put("minute",ArrivalMinute-DepartureMinute);


                                switch(type){
                                    case 1 :
                                        search_result.put("Trainclass","自強號");
                                        break;
                                    case 2 :
                                        search_result.put("Trainclass","莒光號");
                                        break;
                                    case 3 :
                                        search_result.put("Trainclass","復興號");
                                        break;
                                    case 4 :
                                        search_result.put("Trainclass","區間車");
                                        break;
                                    case  11:
                                        search_result.put("Trainclass","太魯閣號");
                                        break;
                                    case 12:
                                        search_result.put("Trainclass","普悠瑪號");
                                        break;

                                }




                                search_results.add(search_result);
                                break;
                            }

                        }
                    }

                    break;
                }

            }
            c.moveToNext();

        }
//list排序
         Log.i("timetest","search:end sort:start");
        //排序＆計算時間
        for (int j = 0; j < search_results.size(); j++) {
            Collections.sort(search_results, new Comparator<Map<String, Object>>() {
                @Override
                public int compare(Map<String, Object> lhs, Map<String, Object> rhs) {

                    Map<String, Object> tmp1 = lhs;
                    Map<String, Object> tmp2 = rhs;

                    int tmp1hour =(Integer)tmp1.get("DepartureHour"); //Integer.parseInt(tmp1.get("DepartureTime").substring(0, tmp1.get("DepartureTime").indexOf(":")));
                    int tmp1minute = (Integer)tmp1.get("DepartureMinute");//Integer.parseInt(tmp1.get("DepartureTime").substring(tmp1.get("DepartureTime").indexOf(":") + 1, tmp1.get("DepartureTime").length()));
                    int tmp2hour = (Integer)tmp2.get("ArrivalHour");//Integer.parseInt(tmp2.get("DepartureTime").substring(0, tmp2.get("DepartureTime").indexOf(":")));
                    int tmp2minute = (Integer)tmp2.get("ArrivalMinute");//Integer.parseInt(tmp2.get("DepartureTime").substring(tmp2.get("DepartureTime").indexOf(":") + 1, tmp2.get("DepartureTime").length()));

                    int result;
                    if (tmp1hour > tmp2hour) {
                        result = 1;
                    } else if (tmp1hour == tmp2hour && tmp1minute > tmp2minute)
                        result = 1;
                    else {
                        result = -1;
                    }

                    return result;
                }
            });
        }

        //SimpleAdapter simpleAdapter = new SimpleAdapter(getApplicationContext(), search_results, R.layout.search_result_listview, new String[]{"TrainNo", "DepartureTime", "ArrivalTime", "Note"}, new int[]{R.id.TrainNo, R.id.DepartureTime, R.id.Arrivaltime, R.id.note});
        Log.i("timetest","sort:end");
        return     new RecyclerAdapter(getApplicationContext(),search_results);
    }
}
