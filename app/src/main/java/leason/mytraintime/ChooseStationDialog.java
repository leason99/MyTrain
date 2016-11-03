package leason.mytraintime;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.widget.SimpleCursorAdapter;
import android.text.StaticLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by leason on 16/9/15.
 */
public class ChooseStationDialog extends DialogFragment {
    ListView listView;
    SimpleCursorAdapter simpleCursorAdapter;
    ArrayAdapter stationLocationAdapter;
    Cursor c = null;
    Context context;
    Boolean chooselocation = false;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // return super.onCreateView(inflater, container, savedInstanceState);

        context = this.getContext();
        stationLocationAdapter = new ArrayAdapter(context, android.R.layout.simple_list_item_1);
        stationLocationAdapter.add("北部");
        stationLocationAdapter.add("中部");
        stationLocationAdapter.add("南部");
        stationLocationAdapter.add("東部");


        View rootview = inflater.inflate(R.layout.choose_station, container, true);

        listView = (ListView) rootview.findViewById(R.id.listView);


        listView.setAdapter(stationLocationAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {


                if (chooselocation) {
                    if (getTag() == "start") {
                        MainActivity.instance.startStation.setText(c.getString(c.getColumnIndex("name")));
                    } else {
                        MainActivity.instance.endStation.setText(c.getString(c.getColumnIndex("name")));

                    }
                    ChooseStationDialog.this.dismiss();
                    chooselocation = false;
                } else {
                    //WHERE stationLocation='" + stationLocationAdapter.getItem(position).toString()+"'

                  c=  MainActivity.instance.trainDB.rawQuery("SELECT _id,name FROM Station WHERE stationLocation='" + stationLocationAdapter.getItem(position).toString()+ "'",null);
                  //  c=  MainActivity.instance.trainDB.rawQuery("SELECT _id,name FROM Station ",null);

                    //  c = MainActivity.instance.trainDB.query("Station",new String[]{"name"},"stationLocation='" + stationLocationAdapter.getItem(position).toString()+"'",null,null,null,null);
                    simpleCursorAdapter = new SimpleCursorAdapter(context, android.R.layout.simple_list_item_1, c, new String[]{"name"}, new int[]{android.R.id.text1});
                    listView.setAdapter(simpleCursorAdapter);
                    chooselocation = true;
                }
            }

        });
        return rootview;


    }

}
