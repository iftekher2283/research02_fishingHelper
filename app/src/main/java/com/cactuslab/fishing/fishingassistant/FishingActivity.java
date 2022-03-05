package com.cactuslab.fishing.fishingassistant;

import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.w3c.dom.Text;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.Random;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

public class FishingActivity extends AppCompatActivity {
    Button btnReset;
    Button btnBack;

    TextView lblStatus;
    TextView lblForce;

    int force;

    MediaPlayer mPlayer;

    ConnectToURL myURLConnection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fishing);

        System.out.println("Loading Music");
        initAlarm();
        System.out.println("Music Loaded");
        force = -10;

        connectToMyURL();
//        takeReading();

        lblStatus = (TextView) findViewById(R.id.labelStatus);
        lblForce = (TextView) findViewById(R.id.labelForce);
//        lblForce.setText("Force: Reading...");

        btnReset = (Button) findViewById(R.id.buttonReset);
        btnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resetHelper();
            }
        });

        btnBack = (Button) findViewById(R.id.buttonBack);
        btnBack.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                if(force == -10){
                    myURLConnection.cancel(true);
                } else {
                    if(mPlayer.isPlaying()){
                        stopAlarm();
                    }
                }

                if(isDisconnected()){
                    lblStatus.setText("Disconnecting");

//                    Intent intent = new Intent(FishingActivity.this,HomeActivity.class);
//                    startActivity(intent);
                    finish();
                }
            }
        });
    }

    private void connectToMyURL(){
        String myUrl = "http://192.168.4.1/";
        myURLConnection = new ConnectToURL(myUrl);

        myURLConnection.execute();
    }

    private void setReadData(int _force){
//        boolean alarmToTrigure = false;
//        while (!alarmToTrigure){
//            force = getForce();
        System.out.println("Set Read Force ---> " + _force);
//            lblForce = (TextView) findViewById(R.id.labelForce);
        System.out.println("Set Read Force 2---> " + _force);
        force = _force;
        String text = "Force: " + _force;
            lblForce.setText((CharSequence) text);
//            lblForce.setText();
        System.out.println("Set Read Force 3---> " + _force);

//            if(force > 100){
//                playAlarm();
////                break;
//            } else {
//                takeReading();
//            }
//        }
    }

    private int getForce() {
        String readForceData = "20";//myURLConnection.getReadData();
        int readForce = Integer.parseInt(readForceData);
        return readForce;
    }

    private void initAlarm(){
        mPlayer = MediaPlayer.create(FishingActivity.this, R.raw.alarm);
    }

    private void playAlarm(int readForce) {
        //PLay an mp3
        force = readForce;
        System.out.println("Starting Music");
//        mPlayer = new MediaPlayer();
        initAlarm();
        mPlayer.start();
        System.out.println("Music Started");

        Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
// Vibrate for 500 milliseconds
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            v.vibrate(VibrationEffect.createOneShot(2000, VibrationEffect.DEFAULT_AMPLITUDE));
        } else {
            //deprecated in API 26
            v.vibrate(2000);
        }

//        String readData = myURLConnection.getReadData();
//        int readForce = Integer.parseInt(readData);
        myURLConnection.cancel(true);
        System.out.println("Read Force ---> " + readForce);
//        setReadData(readForce);
//        connectToMyURL();
//        myURLConnection.cancel(true);
    }

    private void stopAlarm(){
        System.out.println("Stopping Music");
        mPlayer.stop();
        mPlayer.release();
        System.out.println("Music Stopped");
    }

    private void resetHelper(){
        force = -10;
//        myURLConnection.stopAlarm();
//        takeReading();
//        lblForce.setText("Force: Reading...");
//        lblForce.setCont
        if(!myURLConnection.isCancelled()){
            myURLConnection.cancel(true);
        }
        stopAlarm();
        connectToMyURL();
    }

    private boolean isDisconnected(){
        boolean disconnected = false;

        WifiManager wifi = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        wifi.disconnect();

        disconnected = true;

//        ConnectivityManager connManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
//        NetworkInfo mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
//
//
//
//        while(mWifi.isConnected()){
//            if (!mWifi.isConnected()) {
//                // Do whatever
////            mWifi.
//                disconnected = true;
//                System.out.println("Disconnected");
//            }
//        }

        return  disconnected;
    }

    public class ConnectToURL extends AsyncTask {

        private String url;
        private HttpURLConnection connection;
        private String readData;

        public ConnectToURL(String _url){
            this.url = _url;
        }

        @Override
        protected Object doInBackground(Object... arg0) {
            while(true){
                connectToURL();
                this.readData = readDataFromURL();
                System.out.println("Read Data ---> " + this.readData);
                int readForce = Integer.parseInt(this.readData);
//            setReadForce(readForce);
//                setReadData(readForce);
                if(readForce > 100){
//                    playAlarm();
                    onPostExecute(readForce);
                    break;
                }
            }

            return null;
        }

        protected void onPostExecute(int result) {
            //do stuff
//            myMethod(myValue);
//            int readForce = Integer.parseInt(this.readData);
//            setReadData(readForce);
            playAlarm(result);
        }

        private void connectToURL(){
            try
            {
                // create the HttpURLConnection
//            String myUrl = "http://192.168.4.1/";
//            System.out.println("Log ---> myUrl " + this.url);
                URL url = new URL(this.url);
//            System.out.println("Log ---> URL created");
                this.connection = (HttpURLConnection) url.openConnection();
//            System.out.println("Log ---> connection created");

                // just want to do an HTTP GET here
                this.connection.setRequestMethod("GET");
//            System.out.println("Log ---> connection setRequestMethod");

                // uncomment this if you want to write output to this url
                //connection.setDoOutput(true);

                // give it 15 seconds to respond
                this.connection.setReadTimeout(15*1000);
//            System.out.println("Log ---> connection setReadTimeout");
                this.connection.connect();
//            System.out.println("Log ---> connection connect");
//            return stringBuilder.toString();
            }
            catch (Exception e)
            {
                System.out.println("Exception Found");
                e.printStackTrace();
                System.err.println(e);
            }
        }

        private String readDataFromURL(){
            // read the output from the server
            String readData = "";
            try {
                BufferedReader reader = new BufferedReader(new InputStreamReader(this.connection.getInputStream()));
//            System.out.println("Log ---> connection reader");
                readData = reader.readLine();
//            System.out.println
            } catch (IOException e){
                System.out.println("Exception in Reading Data");
                e.printStackTrace();;
                System.err.println(e);
            }

            return  readData;
        }

        private String getReadData(){
            return this.readData;
        }
    }
}
