package leason.mytraintime;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.InterpolatorRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.Calendar;

/**
 * Created by leason on 16/10/1.
 */
public class setRemind extends DialogFragment {


View rootview,listItemView;
    TextView setTime;
    String time;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {


        if(getShowsDialog()){
            return super.onCreateView(inflater, container, savedInstanceState);
        }


        rootview=  inflater.inflate(R.layout.set_remind_dialogment,container,false);
        setTime=(TextView) rootview.findViewById(R.id.setTime);
        return  rootview;
    }


    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

       Dialog dialog= new AlertDialog.Builder(getContext())
                .setView(R.layout.set_remind_dialogment)
                .setPositiveButton("Cancle", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        getDialog().dismiss();
                    }
                })
                .setNegativeButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        getDialog().dismiss();


                       // String time = getArguments().getString("settime");
                       int ArrivaltimeHour  = Integer.parseInt( time.substring(0,time.indexOf(":")));
                       int ArrivateTimeMinute=Integer.parseInt( time.substring(time.indexOf(":")+1,time.length()));
                        MainActivity.instance.counttime(ArrivaltimeHour,ArrivateTimeMinute);
                        Calendar cal =Calendar.getInstance();
                        cal.set(cal.get(Calendar.YEAR),cal.get(Calendar.MONTH),cal.get(Calendar.DATE),ArrivaltimeHour,ArrivateTimeMinute);


                        Intent intent =new Intent();
                        intent.setClass(getContext(),AlarmReceiver.class);
                        PendingIntent pi=PendingIntent.getBroadcast(getContext(),1,intent,PendingIntent.FLAG_CANCEL_CURRENT);
                        AlarmManager am =(AlarmManager)getContext().getSystemService(Context.ALARM_SERVICE);
                        am.set(AlarmManager.RTC_WAKEUP,cal.getTimeInMillis(),pi);
                        getActivity().finish();

                    }
                })
                .setCancelable(false)
                .create();
dialog.setCanceledOnTouchOutside(true);
        return dialog;
    }
    void setTime(String time){


        this.time=time;

    };


}
