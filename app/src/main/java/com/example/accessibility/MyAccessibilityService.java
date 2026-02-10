package com.example.accessibility;

import android.accessibilityservice.AccessibilityService;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.util.Log;

import java.util.List;
import java.io.PrintWriter;
import java.net.Socket;

public class MyAccessibilityService extends AccessibilityService {

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        int eventType = event.getEventType();

        if (eventType == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED ||
            eventType == AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED) {

            AccessibilityNodeInfo source = event.getSource();
            if (source != null) {
                List<CharSequence> texts = source.getText();
                if (texts != null) {
                    for (CharSequence t : texts) {
                        String s = t.toString();
                        if (s.matches("\\+?\\d{10,15}")) {
                            Log.d("MyAccessibilityService", "Arayan numara: " + s);
                            sendNumberToWindowsTCP(s);
                        }
                    }
                }
            }
        }
    }

    private void sendNumberToWindowsTCP(String number) {
        new Thread(() -> {
            try {
                Socket socket = new Socket("192.168.1.12", 5000); // Windows PC IP ve port
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                out.println(number);
                socket.close();
                Log.d("MyAccessibilityService", "Numara TCP ile gönderildi");
            } catch (Exception e) {
                Log.e("MyAccessibilityService", "TCP Gönderilemedi", e);
            }
        }).start();
    }

    @Override
    public void onInterrupt() {
        Log.d("MyAccessibilityService", "Service Interrupted");
    }
}

