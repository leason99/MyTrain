package leason.mytraintime;

import android.app.Activity;
import android.app.ProgressDialog;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.Rect;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;
import com.pnikosis.materialishprogress.ProgressWheel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Created by leason on 16/9/18.
 */
public class SearchResult extends FragmentActivity implements AdapterView.OnItemLongClickListener {
    String startStation, endStation, time;
    Cursor c;
    SQLiteDatabase trainDB;
    private List<Map<String, String>> search_results;
    String stationName;
    // ListView listview;

     RecyclerView listview;

    int type;  //   全部＝1  對號車＝2 非對號車 ＝3
    Handler handler ;
    static SearchResult instance;
    SimpleAdapter simpleAdapter;
    RecyclerAdapter recyclerAdapter;
    Bundle bundle;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */

   ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_result);

        progressDialog = new ProgressDialog(this);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setMessage("請稍候");
        progressDialog.show();

        init();
        Log.i("timetest","callsearch");
        mainservice.instance.searchforResult(bundle);
   /*     new AsyncTask<String, Integer, SimpleAdapter>() {

            @Override
            protected SimpleAdapter doInBackground(String... params) {

                SimpleAdapter simpleAdapter = search();

                return search();
            }

            @Override
            protected void onPostExecute(SimpleAdapter simpleAdapter) {
                listview.setAdapter(simpleAdapter);
                        simpleAdapter.notifyDataSetChanged();
            }

        }.execute();

*/
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
    //    client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    private void init() {
        instance=this;
         bundle = getIntent().getExtras();
       startStation = bundle.getString("startStation");
        endStation = bundle.getString("endStation");
       // time = bundle.getString("time");
       // type = bundle.getInt("type");
        trainDB = MainActivity.instance.trainDB;
       /* listview = (ListView) findViewById(R.id.resultListView);
        listview.setOnItemLongClickListener(this);
        tmp for recycleview
        */
        listview=(RecyclerView)findViewById(R.id.resultListView);
        listview.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        listview.addItemDecoration(new SpaceItemDecoration(10));


        handler =new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            recyclerAdapter=(RecyclerAdapter) msg.obj;

        //    listview.setAdapter(simpleAdapter);    //change for recycleview
            listview.setAdapter(recyclerAdapter);
            recyclerAdapter.notifyDataSetChanged();
            mainservice.instance.addHistory(startStation,endStation);
            progressDialog.dismiss();

        }
    };


    }



    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        setRemind setremind=new  setRemind();

       TextView  textView=(TextView) view.findViewById(R.id.Arrivaltime);

 setremind.setTime(textView.getText().toString());


        setremind.show(getSupportFragmentManager(),"remindTime");



        return false;
    }

    public class SpaceItemDecoration extends RecyclerView.ItemDecoration{

        private int space;

        public SpaceItemDecoration(int space) {
            this.space = space;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {

            if(parent.getChildPosition(view) != 0)
                outRect.top = space;
        }
    }

}

