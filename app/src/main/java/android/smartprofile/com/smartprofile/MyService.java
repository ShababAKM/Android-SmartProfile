package android.smartprofile.com.smartprofile;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.widget.Toast;

public class MyService extends Service implements SensorEventListener{
    private SensorManager mSensorManager;
    private Sensor mLight,mAcc,mProx;
    private float lightV,proxV,xAccV,yAccV,zAccV;
    private boolean flag1 = false,flag2=false,flag3=false,flag4=false;
    private int current,max,medium;
    private AudioManager myAudioManager;
    @Override
    public void onCreate() {
        super.onCreate();
        mSensorManager = (SensorManager)getSystemService(Context.SENSOR_SERVICE);
        mAcc = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mLight = mSensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
        mProx = mSensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
        myAudioManager = (AudioManager)getSystemService(Context.AUDIO_SERVICE);
        max = myAudioManager.getStreamMaxVolume(AudioManager.STREAM_RING);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        if(sensorEvent.sensor.getType()==Sensor.TYPE_LIGHT)
        {
            lightV = sensorEvent.values[0];
        }
        else if(sensorEvent.sensor.getType()==Sensor.TYPE_PROXIMITY)
        {
            proxV = sensorEvent.values[0];
        }
        else if(sensorEvent.sensor.getType()==Sensor.TYPE_ACCELEROMETER)
        {
            xAccV = sensorEvent.values[0];
            yAccV = sensorEvent.values[1];
            zAccV = sensorEvent.values[2];
        }
        if(xAccV<0 && proxV>0 && flag1!=true){
            flag1=true;
            flag2=false;
            flag3=false;
            flag4=false;
            normal();
        }
        else if(zAccV<0 && proxV==0 && lightV<1 && flag2!=true) {
            flag1=false;
            flag2=true;
            flag3=false;
            flag4=false;
            meeting();
        }
        else if(yAccV>9 && proxV==0 && lightV<1 && flag3!=true){
            flag1=false;
            flag2=false;
            flag3=true;
            flag4=false;
            loud();}
        else if(yAccV<-9&& proxV==0 && lightV<1 && flag4!=true){
            flag1=false;
            flag2=false;
            flag3=false;
            flag4=true;
            loud();}

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        mSensorManager.unregisterListener(this);
        Toast.makeText(this, "Destroyed", Toast.LENGTH_SHORT).show();
    }

    private void loud()
    {
        for (int i=0;i<max;i++) {
            myAudioManager.adjustVolume(AudioManager.ADJUST_RAISE, AudioManager.FLAG_VIBRATE);
        }
        Toast.makeText(this, "Loud Mode Activated", Toast.LENGTH_SHORT).show();
    }
    private void meeting()
    {
        current = myAudioManager.getStreamVolume(AudioManager.STREAM_RING);
        for(int i=current;i>0;i--)
        {
            myAudioManager.adjustVolume(AudioManager.ADJUST_LOWER,AudioManager.FLAG_VIBRATE);
        }
        Toast.makeText(this, "Meeting Mode Activated", Toast.LENGTH_SHORT).show();
    }
    private void normal()
    {
        current = myAudioManager.getStreamVolume(AudioManager.STREAM_RING);
        medium = (max/2);
        if (current<medium) {
            for (int i=current;i<medium;i++) {
                myAudioManager.adjustVolume(AudioManager.ADJUST_RAISE, AudioManager.FLAG_VIBRATE);
            }
        }
        if (current>medium) {
            for (int i=current; i>medium; i--) {
                myAudioManager.adjustVolume(AudioManager.ADJUST_LOWER, AudioManager.FLAG_VIBRATE);
            }
        }
        Toast.makeText(this, "Normal Mode Activated", Toast.LENGTH_SHORT).show();
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        mSensorManager.registerListener(this,mAcc,SensorManager.SENSOR_DELAY_NORMAL);
        mSensorManager.registerListener(this,mLight,SensorManager.SENSOR_DELAY_NORMAL);
        mSensorManager.registerListener(this,mProx,SensorManager.SENSOR_DELAY_NORMAL);
        Toast.makeText(this,"Smart Profile Manager Started", Toast.LENGTH_LONG).show();
        return START_STICKY;
    }
}
