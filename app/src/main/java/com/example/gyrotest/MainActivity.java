package com.example.gyrotest;

import android.annotation.SuppressLint;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.UnknownHostException;
import java.text.DecimalFormat;


import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import okio.ByteString;


public class MainActivity extends AppCompatActivity {

    private SensorManager sm;
    private Sensor s;
    private TextView Xvalue;
    private TextView Yvalue;
    private TextView Zvalue;

    SensorEventListener sl;
    private static final DecimalFormat df = new DecimalFormat("0.00");

    private DatagramSocket socket;
    private InetAddress address;
    private byte[] data;
    
    private float[] savedValues;
    float[] orientation;


    private Button connectButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Xvalue = findViewById(R.id.Xvalue);
        Yvalue = findViewById(R.id.Yvalue);
        Zvalue = findViewById(R.id.Zvalue);


        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        savedValues = new float[]{0, 0, 0};
        connectButton = findViewById(R.id.connect_btn);
        connectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                for(int i = 0; i < orientation.length; i++) {{
                    savedValues[i] = orientation[i];
                }}
            }
        });
        try {
            address = InetAddress.getByName("192.168.43.167");
            socket = new DatagramSocket(8080);

        } catch (IOException e) {
            e.printStackTrace();
        }


        sm = (SensorManager) getSystemService(SENSOR_SERVICE);
        if (sm != null) s = sm.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
         sl  = new SensorEventListener() {

            @SuppressLint("SetTextI18n")
            @Override
            public void onSensorChanged(SensorEvent event) {

                float[] rotationMatrix = new float[16];
                SensorManager.getRotationMatrixFromVector(rotationMatrix, event.values);
                orientation = new float[3];
                SensorManager.getOrientation(rotationMatrix, orientation);

                orientation[0] = (float) Math.toDegrees(orientation[0]); // Azimuth
                orientation[1] = (float) Math.toDegrees(orientation[1]); // Pitch
                orientation[2] = (float) Math.toDegrees(orientation[2]); // Roll



                Xvalue.setText("Yaw: " + df.format(orientation[0]-savedValues[0]));
                Yvalue.setText("Pitch: " + df.format(orientation[1]-savedValues[1]));
                Zvalue.setText("Row: " + df.format(orientation[2]-savedValues[2]));

                data = ((orientation[0]-savedValues[0]) + "," + (orientation[1]-savedValues[1]) + "," + (orientation[2]-savedValues[2]) + "\n").getBytes();
                DatagramPacket packet = new DatagramPacket(data, data.length, address, 8080);
                try {
                    socket.send(packet);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {


            }




        };



        Thread sendData = new Thread(new Runnable() {


            @Override
            public void run() {
                try {
                    DatagramPacket packet = new DatagramPacket(data, data.length, address, 1000);
                    socket.send(packet);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        });



    }

    protected void onResume() {
        super.onResume();
        if (s != null) sm.registerListener(sl, s, SensorManager.SENSOR_DELAY_NORMAL);
    }

    protected void onPause() {
        super.onPause();
        if (s != null) sm.unregisterListener(sl);
    }

}



