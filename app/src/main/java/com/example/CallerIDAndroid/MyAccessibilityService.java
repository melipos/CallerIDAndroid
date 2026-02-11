package com.example.CallerIDAndroid;

import android.accessibilityservice.AccessibilityService;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.util.Log;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MyAccessibilityService extends AccessibilityService {

    private static final String TAG = "MyAccessibilityService";

    // Melipos IP ve port
    private static final String MELIPOS_IP = "192.168.1.12"; // örnek IP
    private static final int MELIPOS_PORT = 8080;             // örnek port

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        if (event == null) return;

        CharSequence eventText = event.getText() != null && !event.getText().isEmpty() 
                                 ? event.getText().get(0)
                                 : null;

        if (eventText != null) {
            String text = eventText.toString();
            Log.d(TAG, "Received text: " + text);

            // Melipos’a gönder
            sendToCRM(text);
        }

        AccessibilityNodeInfo source = event.getSource();
        if (source != null) {
            CharSequence nodeText = source.getText();
            if (nodeText != null) {
                List<CharSequence> texts = new ArrayList<>();
                texts.add(nodeText.toString());
                Log.d(TAG, "Node text: " + nodeText.toString());
            }
        }
    }

    @Override
    public void onInterrupt() {
        Log.d(TAG, "Accessibility Service Interrupted");
    }

    private void sendToCRM(String phoneNumber) {
        new Thread(() -> {
            try {
                String urlStr = "http://" + MELIPOS_IP + ":" + MELIPOS_PORT + "/melipos?number=" + phoneNumber;
                URL url = new URL(urlStr);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                int code = conn.getResponseCode();
                Log.d(TAG, "CRM Response code: " + code);
                conn.disconnect();
            } catch (Exception e) {
                Log.e(TAG, "Error sending to CRM", e);
            }
        }).start();
    }
}


