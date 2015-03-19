package com.chamas.luis.server;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;


import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;


public class MainActivity extends ActionBarActivity implements SensorEventListener{

    private SensorManager mSensorMan;
    private Sensor mAccel;
    double mSensorX, mSensorY, mSensorZ;
    MainActivity activity = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mSensorMan = (SensorManager)getSystemService(SENSOR_SERVICE);
        mAccel = mSensorMan.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void startListener(View view) {
        Intent i = new Intent(this, serverService.class);
        i.putExtra("AccelX", mSensorX);
        i.putExtra("AccelY", mSensorY);
        startService(i);
    }

    public void stopListener(View view) {
        Intent i = new Intent(this, serverService.class);
        stopService(i);
    }

    public void quit(View view) {
        Intent i = new Intent(this, serverService.class);
        stopService(i);
        System.exit(0);
    }

    public void showToast(final String toast)
    {
        runOnUiThread(new Runnable() {
            public void run() {
                Toast.makeText(MainActivity.this, toast, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if(event.sensor.getType() != Sensor.TYPE_ACCELEROMETER){
            return;
        }else{
            double alpha = 0.8;
            double[] gravity = new double [3];

            //Isolate the force of gravity with the low pass filter
            gravity[0] = alpha * gravity[0] + (1-alpha) * event.values[0];
            gravity[1] = alpha * gravity[1] + (1-alpha) * event.values[1];
            gravity[2] = alpha * gravity[2] + (1-alpha) * event.values[2];

            //Remove gravity contribution with the high pass filter
            mSensorX = event.values[0] - gravity[0];
            mSensorY = event.values[1] - gravity[1];
            mSensorZ = event.values[2] - gravity[2];
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}