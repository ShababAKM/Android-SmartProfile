package android.smartprofile.com.smartprofile;

import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

public class MainActivity extends AppCompatActivity implements SensorEventListener{

    private SensorManager mSensorManager;
    private Sensor mLight,mAcc,mProx;
    private TextView light,prox,xAcc,yAcc,zAcc;
    private float lightV,proxV,xAccV,yAccV,zAccV;
    Button ring,vibrate,silent;
    private AudioManager myAudioManager;
    private int current,max,medium;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mSensorManager = (SensorManager)getSystemService(Context.SENSOR_SERVICE);
        mAcc = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mLight = mSensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
        mProx = mSensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
        light = (TextView)findViewById(R.id.Light);
        prox = (TextView)findViewById(R.id.Prox);
        xAcc = (TextView)findViewById(R.id.ax);
        yAcc = (TextView)findViewById(R.id.ay);
        zAcc = (TextView)findViewById(R.id.az);

        Button  meeting=(Button)findViewById(R.id.meating);
        Button   loud=(Button)findViewById(R.id.loud);
        Button  normal=(Button)findViewById(R.id.normal);
        myAudioManager = (AudioManager)getSystemService(Context.AUDIO_SERVICE);
        max = myAudioManager.getStreamMaxVolume(AudioManager.STREAM_RING);
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mSensorManager.registerListener(this,mAcc,SensorManager.SENSOR_DELAY_NORMAL);
        mSensorManager.registerListener(this,mLight,SensorManager.SENSOR_DELAY_NORMAL);
        mSensorManager.registerListener(this,mProx,SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent)
    {
        if(sensorEvent.sensor.getType()==Sensor.TYPE_LIGHT)
        {
            lightV = sensorEvent.values[0];
            light.setText(String.valueOf(lightV+" lx"));
        }
        else if(sensorEvent.sensor.getType()==Sensor.TYPE_PROXIMITY)
        {
            proxV = sensorEvent.values[0];
            prox.setText(String.valueOf(proxV));
        }
        else if(sensorEvent.sensor.getType()==Sensor.TYPE_ACCELEROMETER)
        {
            xAccV = sensorEvent.values[0];
            yAccV = sensorEvent.values[1];
            zAccV = sensorEvent.values[2];
            xAcc.setText(String.valueOf(xAccV));
            yAcc.setText(String.valueOf(yAccV));
            zAcc.setText(String.valueOf(zAccV));
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {
    }
    public void onClickMeeting(View v) {
        current = myAudioManager.getStreamVolume(AudioManager.STREAM_RING);
        for(int i=current;i>0;i--)
        {
            myAudioManager.adjustVolume(AudioManager.ADJUST_LOWER,AudioManager.FLAG_VIBRATE);
        }
        //myAudioManager.setRingerMode(AudioManager.RINGER_MODE_SILENT);
        Toast.makeText(this,"Now in Silent Mode "+current, Toast.LENGTH_LONG).show();
    }

    public void onClickNormal(View v) {
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
        Toast.makeText(this, "Now in Normal Mode"+medium, Toast.LENGTH_LONG).show();
    }
    public void onClickLoud (View v){
        for (int i=0;i<max;i++) {
            myAudioManager.adjustVolume(AudioManager.ADJUST_RAISE, AudioManager.FLAG_VIBRATE);
            }
            Toast.makeText(this, "Now in Loud Mode "+max, Toast.LENGTH_LONG).show();
        }
    public void startService(View view) {
        startService(new Intent(this, MyService.class));

    }

    public void stopService(View view) {
        stopService(new Intent(this, MyService.class));

    }
}
