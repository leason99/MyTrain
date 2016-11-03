package leason.mytraintime;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.Build;
import android.os.Vibrator;
import android.widget.Toast;

/**
 * Created by leason on 16/10/1.
 */
public class AlarmReceiver extends BroadcastReceiver {
    Context context;
    MediaPlayer mediaPlayer;
    @Override
    public void onReceive(Context context, Intent intent) {
        this.context = context;




        if(mediaPlayer==null){
            mediaPlayer=MediaPlayer.create(mainservice.instance.getApplication(), R.raw.music);//这是放的是所要播放音乐的资源文件
            mediaPlayer.start();
        }

        Vibrator myVibrator = (Vibrator) context.getSystemService(Service.VIBRATOR_SERVICE);
        myVibrator.vibrate(3000);
    }
}
