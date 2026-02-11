package com.example.CallerIDAndroid;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    private static final String MELIPOS_IP = "192.168.1.12"; // kendi IP
    private static final int MELIPOS_PORT = 20000;             // kendi port

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button btnTest = findViewById(R.id.btnTestSend);
        btnTest.setOnClickListener(v -> sendTestNumber("5551234567")); // test numarası
    }

    private void sendTestNumber(String phoneNumber) {
        new Thread(() -> {
            try {
                String urlStr = "http://" + MELIPOS_IP + ":" + MELIPOS_PORT + "/melipos?number=" + phoneNumber;
                URL url = new URL(urlStr);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                int code = conn.getResponseCode();
                Log.d("TEST_SEND", "Response code: " + code);
                conn.disconnect();
            } catch (Exception e) {
                Log.e("TEST_SEND", "Error sending test number", e);
            }
        }).start();
    }
}


