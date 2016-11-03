package leason.mytraintime;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.service.chooser.ChooserTarget;
import android.support.v4.util.ArrayMap;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutCompat;
import android.util.Log;
import android.view.View;
import android.widget.Adapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.TimePicker;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedHashMap;
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

import static android.database.sqlite.SQLiteDatabase.MAX_SQL_CACHE_SIZE;
import static android.database.sqlite.SQLiteDatabase.openOrCreateDatabase;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    /**
     * 欲呼叫之API網址(此範例為台鐵車站資料)
     */
    static String RAILURLl = "http://ptx.transportdata.tw/MOTC/v2/Rail/TRA/Station?$format=json";
    static String RAILURL2 = "http://ptx.transportdata.tw/MOTC/v2/Rail/TRA/DailyTimetable?$format=json";
    SimpleAdapter simpleAdater;
    ListView listView;
    static MainActivity instance;
    static String stationData, stationName, stationNameZh_tw, response, stationAddress, stationLocation;
    Button startStation, endStation, time, nowtime, search;
    SimpleCursorAdapter testAdapter;
    int year, month, day, minute, hour;
    Calendar calendar;
    SQLiteDatabase trainDB;
    Cursor c;
    RadioGroup radioGroup;
    RadioButton all, express, noexpress;
    Runnable updateTimer;
     Handler handler;
    ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        instance = this;

        Intent intent=new Intent(this,mainservice.class);

handler=new Handler(){

    @Override
    public void handleMessage(Message msg) {
        super.handleMessage(msg);


        switch (msg.what) {

            case 1:
                progressDialog = new ProgressDialog(MainActivity.this);
                progressDialog.setIndeterminate(true);
                progressDialog.setTitle("下載中");
                progressDialog.setMessage("請稍候");
                progressDialog.setCanceledOnTouchOutside(false);
                progressDialog.show();
                break;
            case 2:
                progressDialog.dismiss();
            case 3:
                init();
                break;
        }


    }
};
        startService(intent);

        //下載數據

        //c = trainDB.rawQuery("SELECT count(*) as c FROM Sqlite_master WHERE type='table'", null);
       // c.moveToFirst();
        //if(c.getInt(0)<=0)
        //{

        //}

        //測試用ListView
        //listView = (ListView) findViewById(R.id.listView);
        // listView.setAdapter(simpleCursorAdapter);

    }

    private void init() {
        //讀取資料
        trainDB = openOrCreateDatabase("TrainData.db", MODE_PRIVATE, null);
        //  Cursor c = trainDB.query("Station", new String[]{"_id", "name"}, null, null, null, null, null);
        //simpleCursorAdapter = new SimpleCursorAdapter(this, android.R.layout.simple_list_item_1, c, new String[]{"name"}, new int[]{android.R.id.text1});

        endStation = (Button) findViewById(R.id.end_button);
        startStation = (Button) findViewById(R.id.start_button);

        time = (Button) findViewById(R.id.time);
        nowtime = (Button) findViewById(R.id.nowtime);
        search = (Button) findViewById(R.id.search);

        startStation.setOnClickListener(this);
        endStation.setOnClickListener(this);
        time.setOnClickListener(this);

        nowtime.setOnClickListener(this);
        search.setOnClickListener(this);
        radioGroup = (RadioGroup) findViewById(R.id.radioGroup);
        all = (RadioButton) findViewById(R.id.all);
        express = (RadioButton) findViewById(R.id.express);
        noexpress = (RadioButton) findViewById(R.id.noexpress);

        calendar = Calendar.getInstance();
        listView = (ListView) findViewById(R.id.history);

        listView.setAdapter(initHistory());
        //  Cursor test=trainDB.rawQuery("SELECT StationName,ArrivalTime FROM NO_105",new String[]{"StationName","ArrivalTime"});


    }

    private SimpleAdapter initHistory() {
Boolean edited=false;
List<Map<String,String>> history=new ArrayList<Map<String,String>>();


        DocumentBuilderFactory docbuildfactory=DocumentBuilderFactory.newInstance();
        try {
            DocumentBuilder docbulidr=docbuildfactory.newDocumentBuilder();

        Document doc =docbulidr.parse(new FileInputStream(mainservice.instance.HistoryXml));
        Element rootElement=doc.getDocumentElement();
           NodeList messageNode =rootElement.getElementsByTagName("message");
            //xml最新紀錄會由後面寫入  ，所以從後面倒數取4筆記錄 ,其餘刪除
            for(int i=messageNode.getLength()-1;i>=0;i--){

                if(i>messageNode.getLength()-5) {
                    Element messageELement = (Element) messageNode.item(i);
                    messageELement.getElementsByTagName("departure").item(0).getTextContent();
                    messageELement.getElementsByTagName("arrival").item(0).getTextContent();
                    Map<String, String> map = new HashMap<String, String>();

                    //  map.put("departure",messageELement.getAttribute("departure"));
                    // map.put("arrival",messageELement.getAttribute("arrival"));

                    map.put("departure", messageELement.getElementsByTagName("departure").item(0).getTextContent());
                    map.put("arrival", messageELement.getElementsByTagName("arrival").item(0).getTextContent());

                    history.add(map);
                }
                else {
                    edited=true;


                    rootElement.removeChild(messageNode.item(i));

                }


            }


 simpleAdater =new SimpleAdapter(getApplicationContext(),history,R.layout.history_listview,new String[]{"departure","arrival"},new int[]{R.id.history_departure,R.id.history_arrival});

if(edited==true) {
    TransformerFactory transformerFactory = TransformerFactory.newInstance();
    Transformer transformer = transformerFactory.newTransformer();
    DOMSource source = new DOMSource(doc);
    StreamResult result = new StreamResult(mainservice.instance.HistoryXml);
    transformer.transform(source, result);

}


        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (TransformerConfigurationException e) {
            e.printStackTrace();
        } catch (TransformerException e) {
            e.printStackTrace();
        }
        return simpleAdater;

    }




/*   //old methond
    public void getData() {
        final ProgressDialog[] progressDialog = new ProgressDialog[1];
        final ContentValues contentValues = new ContentValues();
        final ContentValues trainContentValues = new ContentValues();
        final SQLiteDatabase trainDB = openOrCreateDatabase("TrainData.db", MODE_PRIVATE, null);
        trainDB.execSQL("CREATE TABLE IF NOT EXISTS Station(_id INTERGER PRIMARY KEY ,name TEXT,stationLocation TEXT)");

        try {
            new AsyncTask<String, Integer, String>() {

                @Override
                protected void onPreExecute() {
                    //  progressDialog = new ProgressDialog(getBaseContext());

                    progressDialog[0] = new ProgressDialog(MainActivity.this);
                    progressDialog[0].show();
                }

                @Override
                protected String doInBackground(String... params) {

                    try {


                        //response = sendRequest("GET", LOGINURL+"account="+ACCOUNT+"&password="+PASSWORD, user);
                        Map<String, Object> railParams = new LinkedHashMap<>();
                        // -- 取得台鐵車站資料 -- //
                        response = sendRequest("GET", RAILURLl, railParams);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }


                    try {
                        JSONArray array = new JSONArray(response);

                        for (int count = 0; count < array.length(); count++) {

                            stationData = array.getString(count);
                            stationName = new JSONObject(stationData).getString("StationName");
                            stationNameZh_tw = new JSONObject(stationName).getString("Zh_tw");
                            stationAddress = new JSONObject(stationData).getString("StationAddress");
                            Log.i("locate", stationAddress);
                            if (stationAddress.length() < 2) {
                                //廢棄車站無地址
                                continue;
                            }
                            switch (stationAddress.substring(0, 2)) {
                                case "基隆":
                                case "新北":
                                case "臺北":
                                case "桃園":
                                case "新竹":
                                    stationLocation = "北部";
                                    break;
                                case "苗栗":
                                case "臺中":
                                case "彰化":
                                case "南投":
                                case "雲林":
                                    stationLocation = "中部";
                                    break;
                                case "嘉義":
                                case "臺南":
                                case "高雄":
                                case "屏東":
                                    stationLocation = "南部";
                                    break;
                                case "宜蘭":
                                case "花蓮":
                                case "臺東":
                                    stationLocation = "東部";
                                    break;

                            }

                            contentValues.put("name", stationNameZh_tw);
                            contentValues.put("stationLocation", stationLocation);
                            trainDB.insert("Station", null, contentValues);


                        }


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    return response;

                }

                @Override
                protected void onPostExecute(String s) {


                }
            }.execute((String[]) null);

            new AsyncTask<String, Integer, String>() {

                @Override
                protected void onPreExecute() {

                }

                @Override
                protected String doInBackground(String... params) {

                    try {

                        Map<String, Object> railParams = new LinkedHashMap<>();
                        // -- 取得火車時刻資料 -- //
                        response = sendRequest("GET", RAILURL2, railParams);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }


                    String trainData, TrainNo, Direction, StopTimes, TrainClassificationID,Note;
                    int type;

                    try {
                        JSONArray array = new JSONArray(response);

                        for (int count = 0; count < array.length(); count++) {
                            trainData = array.getString(count);

                            JSONObject DailyTrainInfoObject = new JSONObject(new JSONObject(trainData).getString("DailyTrainInfo"));

                            TrainNo = DailyTrainInfoObject.getString("TrainNo");
                            Note=DailyTrainInfoObject.getString("Note");
                            TrainClassificationID = DailyTrainInfoObject.getString("TrainClassificationID");
                            switch (TrainClassificationID) {

                                case "1130":
                                case "1141":
                                case "1150":
                                case "1131":
                                case "1132":
                                case "1140":
                                case "1135":
                                    type = 2;
                                    break;
                                default:
                                    type = 1;

                            }
                            // Direction =DailyTrainInfoObject.getString("TrainNo");

                            StopTimes = new JSONObject(trainData).getString("StopTimes");

                            trainDB.execSQL("CREATE TABLE IF NOT EXISTS Train(_id INTERGER PRIMARY KEY,TrainNo TEXT ,Direction TEXT,StopTimes TEXT ,TrainType INTEGER ,Note TEXT)");

                            trainContentValues.put("TrainNo", TrainNo);

                            // trainContentValues.put("Direction", Direction);
                            trainContentValues.put("StopTimes", StopTimes);
                            trainContentValues.put("TrainType", type);

                            if(!Note.contentEquals("每日行駛")){


                                trainContentValues.put("Note", Note);
                            }


                            trainDB.insert("Train", null, trainContentValues);
                            Log.i("test", "現在正在寫入：" + TrainNo);
                        }


                        //   Cursor c = trainDB.query("Station", new String[]{"_id", "name"}, null, null, null, null, null);
                        //   c.moveToFirst();

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    return response;

                }

                @Override
                protected void onPostExecute(String s) {


//testlistview
                    //  Cursor test = trainDB.query("Train", new String[]{"_id", "TrainNo", "StopTimes"}, null, null, null, null, null);
                    // testAdapter = new SimpleCursorAdapter(MainActivity.this, android.R.layout.simple_list_item_2, test, new String[]{"TrainNo", "StopTimes"}, new int[]{android.R.id.text1, android.R.id.text2});
                    // listView.setAdapter(testAdapter);
                    progressDialog[0].dismiss();
                }
            }.execute((String[]) null);











                   /* root = new JsonParser().parse(response.toString());
            // 若成功
            if(root.getAsJsonObject().get("Status").getAsBoolean()){
                String ticket = root.getAsJsonObject().getAsJsonObject().get("Ticket").getAsString();
                Map<String, Object> railParams = new LinkedHashMap<>();
                // -- 取得台鐵車站資料 -- //
                String data = sendRequest("GET", RAILURLl + ticket, railParams);
    */
/*

        } catch (Exception e) {
            e.printStackTrace();
        }


    }
*/

    public static String sendRequest(String method, String Url, Map<String, Object> params) throws Exception {
        URL url = new URL(Url);
        StringBuilder postData = new StringBuilder();
        for (Map.Entry<String, Object> param : params.entrySet()) {
            if (postData.length() != 0) {
                postData.append('&');
            }

            postData.append(URLEncoder.encode(param.getKey(), "UTF-8"));
            postData.append('=');
            postData.append(URLEncoder.encode(String.valueOf(param.getValue()), "UTF-8"));
        }
        byte[] postDataBytes = postData.toString().getBytes("UTF-8");
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod(method);
        conn.setRequestProperty("Content-Length", String.valueOf(postDataBytes.length));
        if ("POST".equals(method)) {
            conn.setDoOutput(true);
            conn.getOutputStream().write(postDataBytes);
        }
        BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
        String line;
        StringBuilder response = new StringBuilder();
        while ((line = br.readLine()) != null) {
            response.append(line);
        }
        return response.toString();
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.end_button:

                new ChooseStationDialog().show(getSupportFragmentManager(), "end");
                break;
            case R.id.start_button:

                new ChooseStationDialog().show(getSupportFragmentManager(), "start");
                break;


            case R.id.time:

                hour = calendar.get(Calendar.AM_PM);
                minute = calendar.get(Calendar.MINUTE);
                TimePickerDialog timepickerDialog = new TimePickerDialog(MainActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        time.setText(hourOfDay + ":" + minute);
                    }
                }, hour, minute, false);

                timepickerDialog.show();
                break;
            case R.id.nowtime:

                month = calendar.get(Calendar.MONTH);
                day = calendar.get(Calendar.DATE);
                hour = calendar.get(Calendar.HOUR_OF_DAY);
                minute = calendar.get(Calendar.MINUTE);

                time.setText(hour + ":" + minute);
                break;
            case R.id.search:


                Intent intent = new Intent(MainActivity.this, SearchResult.class);
                intent.putExtra("startStation", startStation.getText().toString());
                intent.putExtra("endStation", endStation.getText().toString());
                intent.putExtra("time", time.getText().toString());
                switch (radioGroup.getCheckedRadioButtonId()) {
                    case R.id.all:
                        intent.putExtra("type", 1);
                        break;
                    case R.id.express:
                        intent.putExtra("type", 2);
                        break;

                    case R.id.noexpress:
                        intent.putExtra("type", 3);
                        break;

                }
                startActivity(intent);

                break;
        }}



        void counttime( final int ArrivateTimeHour,final int ArrivateTimeMinute){
     // handler = new Handler();
        updateTimer = new Runnable() {
            public void run() {
                final TextView time = (TextView) findViewById(R.id.counttime);


                Calendar calendar =Calendar.getInstance();
              int minute=  Math.abs(ArrivateTimeMinute- calendar.get(Calendar.MINUTE));
              int hour=  ArrivateTimeHour- calendar.get(Calendar.HOUR_OF_DAY);
               int second=Math.abs(60-calendar.get(Calendar.SECOND));
                //計算目前已過分鐘數

                time.setText(hour+":"+minute+":"+second);


                handler.postDelayed(this, 1000);
            }};



        //設定定時要執行的方法
       handler.removeCallbacks(updateTimer);
        //設定Delay的時間
        handler.postDelayed(updateTimer, 1000);



        }


    @Override
    protected void onRestart() {

      listView.setAdapter(  initHistory());

        listView.deferNotifyDataSetChanged();

        super.onRestart();
    }
}



