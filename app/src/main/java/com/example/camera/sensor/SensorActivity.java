package com.example.camera.sensor;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.camera.R;

import static androidx.vectordrawable.graphics.drawable.PathInterpolatorCompat.EPSILON;
import static java.lang.Math.cos;

public class SensorActivity extends AppCompatActivity implements SensorEventListener {
    private static final String TAG = "SensorActivity";
    //a TextView
    private TextView tv;
    //the Sensor Manager
    private SensorManager sManager;
    private Sensor accelerometer, gyrometer;
    private ImageView image;
    Animation object, object1, object2;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sensor);

        //get the TextView from the layout file
        tv = (TextView) findViewById(R.id.tv);
        image = findViewById(R.id.imageView2);
        object = AnimationUtils.loadAnimation(getApplicationContext(),// blink file is in anim folder
                R.anim.lefttoright);
        object1 = AnimationUtils.loadAnimation(getApplicationContext(),// blink file is in anim folder
                R.anim.righttoleft);
        object2 = AnimationUtils.loadAnimation(getApplicationContext(),// blink file is in anim folder
                R.anim.center);
        //get a hook to the sensor service
        sManager = (SensorManager) getSystemService(SENSOR_SERVICE);
    }

    //when this Activity starts
    @Override
    protected void onResume() {
        super.onResume();
		/*register the sensor listener to listen to the gyroscope sensor, use the
		callbacks defined in this class, and gather the sensor information as quick
		as possible*/
        gyrometer = sManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        if (gyrometer != null) {
            sManager.registerListener(this, sManager.getDefaultSensor(Sensor.TYPE_ORIENTATION), SensorManager.SENSOR_DELAY_FASTEST);
            Log.d(TAG, "onResume: Gyrometer supported");
        } else {
            tv.setText("Gyrometer not supported");
            Log.d(TAG, "onResume: Gyrometer not supported");
        }
    }

    //When this Activity isn't visible anymore
    @Override
    protected void onStop() {
        //unregister the sensor listener
        sManager.unregisterListener(this);
        super.onStop();
    }

    @Override
    public void onAccuracyChanged(Sensor arg0, int arg1) {
        //Do nothing.
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        //if sensor is unreliable, return void
        if (event.accuracy == SensorManager.SENSOR_STATUS_UNRELIABLE) {
            return;
        }

        if (event.values[1] > 0.5f) {
//            getWindow().getDecorView().setBackgroundColor(Color.BLUE);
            image.setAnimation(object);
        }
        if (event.values[1] < -0.5f) {
            //         getWindow().getDecorView().setBackgroundColor(Color.YELLOW);
            image.setAnimation(object1);
        }
        if (event.values[1] == 0.0f) {
            //         getWindow().getDecorView().setBackgroundColor(Color.YELLOW);
            image.setAnimation(object2);
        }
        //else it will output the Roll, Pitch and Yawn values
        tv.setText("Orientation Y (Roll) :" + Float.toString(event.values[2]) + "\n" +
                "Orientation X (Pitch) :" + Float.toString(event.values[1]) + "\n" +
                "Orientation Z (Yaw) :" + Float.toString(event.values[0]));
    }
}