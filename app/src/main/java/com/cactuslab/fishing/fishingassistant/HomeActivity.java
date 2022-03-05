package com.cactuslab.fishing.fishingassistant;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.util.List;

public class HomeActivity extends AppCompatActivity {
    Button btnConnect;
    Button btnExit;
    ProgressDialog connectingPrgrsBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

//        connectingPrgrsBar = (ProgressBar) findViewById(R.id.connectingProgressBar);
//        connectingPrgrsBar.setProgress(0);

        btnConnect = (Button) findViewById(R.id.buttonConnect);
        btnConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final String networkSSID = "ESP_3B1D1B";
                final String networkPass = "";

//                String networkSSID = "Cactus";
//                String networkPass = "8765433210";
//                connectingPrgrsBar = (ProgressBar) findViewById(R.id.connectingProgressBar);
//                connectingPrgrsBar.setProgress(0);

                connectToNetwork(networkSSID, networkPass);

                connectingPrgrsBar = new ProgressDialog(HomeActivity.this);
                connectingPrgrsBar.setTitle("Connecting");
                connectingPrgrsBar.setMessage("Please wait");
                connectingPrgrsBar.setIndeterminate(true);
                connectingPrgrsBar.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                connectingPrgrsBar.setProgress(0);
//                connectingPrgrsBar.setMax(100);
                connectingPrgrsBar.show();

                final int totalProgressTime = 100;
                final Thread t = new Thread() {
                    @Override
                    public void run() {
                        int jumpTime = 0;
//                        long duration = 0;
//                        long max = 10;
//                        long millis = System.currentTimeMillis() % 1000;
                        boolean connected = false;
                        while(jumpTime < totalProgressTime) {
                            try {
//                                long curMillis = System.currentTimeMillis() % 1000;
//                                long diff = curMillis - millis;
//                                duration += diff;
//                                System.out.println("Duration ---> " + duration);
//                                if(duration < max * 1000){
                                    sleep(200);
                                    jumpTime += 1;
                                    System.out.println("Jump Time ---> " + jumpTime);
                                    connectingPrgrsBar.setProgress(jumpTime);
                                    System.out.println("Trying to connect to " + networkSSID);
                                    if(isConnected()){
                                        connected = true;
                                        connectingPrgrsBar.dismiss();
                                        Intent intent = new Intent(HomeActivity.this,FishingActivity.class);
                                        startActivity(intent);
                                        break;
                                    }
//                                } else {

//                                    break;
//                                }
                            } catch (InterruptedException e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            }
                        }
                        if(!connected){
                            connectingPrgrsBar.dismiss();
                            System.out.println("Time out");
//                            Toast.makeText(getApplicationContext(), "Please Try Again",
//                                    Toast.LENGTH_LONG).show();
                        }

                    }
                };
                t.start();

//                while (!isConnected()){
//
//                }
            }
        });

        btnExit = (Button) findViewById(R.id.buttonExit);
        btnExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                finish();
                android.os.Process.killProcess(android.os.Process.myPid());
                System.exit(1);
            }
        });
    }

    private void connectToNetwork(String _ssid, String _pass){


        System.out.println("ID Pass Set");

        WifiConfiguration conf = new WifiConfiguration();
        System.out.println("conf instantiated");
        conf.SSID = "\"" + _ssid + "\"";

        conf.wepKeys[0] = "\"" + _pass + "\"";
        conf.wepTxKeyIndex = 0;
        System.out.println("variables1");
        conf.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
        System.out.println("allowedKeyManagement set");
        conf.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);
        System.out.println("allowedGroupCiphers set");
        conf.preSharedKey = "\""+ _pass +"\"";
        System.out.println("variables2");
        conf.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
        System.out.println("allowedKeyManagement set");
        WifiManager wifiManager = (WifiManager)getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        System.out.println("wifiManager instantiated");
//        wifiManager.addNetwork(conf);
//        System.out.println("wifiManager addNetwork");

        List<WifiConfiguration> list = wifiManager.getConfiguredNetworks();
        System.out.println("WifiConfiguration list found");
        for(int i = 0; i < list.size(); i++){//( WifiConfiguration i : list ) {
            WifiConfiguration wifiConf = list.get(i);
            System.out.println("Config " + i + ": SSID ---> " + wifiConf.SSID);
            if (wifiConf.SSID != null && wifiConf.SSID.equals("\"" + _ssid + "\"")) {
                wifiManager.disconnect();
                System.out.println("Enabling Network NetworkID ---> " + wifiConf.networkId);
                wifiManager.enableNetwork(wifiConf.networkId, true);
                wifiManager.reconnect();

                break;

            }

        }
    }

    private boolean isConnected(){
        boolean connected = false;

        ConnectivityManager connManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

        if (mWifi.isConnected()) {
            // Do whatever
            connected = true;
            System.out.println("Connected");


        }

        return  connected;
    }
}
