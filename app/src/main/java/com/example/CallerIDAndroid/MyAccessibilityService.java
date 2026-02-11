package com.example.CallerIDAndroid;

import android.accessibilityservice.AccessibilityService;
import android.view.accessibility.AccessibilityEvent;
import android.util.Log;

import java.io.OutputStream;
import java.net.Socket;

// Kendi projenin R sınıfı
import com.example.CallerIDAndroid.R;

public class MyAccessibilityService extends AccessibilityService {

    private static final String DELPHI_IP = "192.168.1.12"; // Delphi PC LAN IP
    private static final int DELPHI_PORT = 20000;

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        if(event.getEventType() == AccessibilityEvent.TYPE_NOTIFICATION_STATE_CHANGED
           || event.getEventType() == AccessibilityEvent.TYPE_VIEW_TEXT_CHANGED) {

            CharSequence text = event.getText().toString();

            if(text != null && text.length() > 0) {
                String number = text.toString();
                Log.d("CallerIDAndroid", "Gelen numara: " + number);

                sendNumberToDelphi(number);
            }
        }
    }

    @Override
    public void onInterrupt() {
        Log.d("CallerIDAndroid", "AccessibilityService kesildi.");
    }

    private void sendNumberToDelphi(String number) {
        new Thread(() -> {
            Socket socket = null;
            try {
                socket = new Socket(DELPHI_IP, DELPHI_PORT);

                OutputStream out = socket.getOutputStream();
                String data = number + "\n"; // Delphi ReadLn ile uyumlu
                out.write(data.getBytes("ISO-8859-1"));
                out.flush();

                Log.d("CallerIDAndroid", "Numara gönderildi: " + number);

            } catch (Exception e) {
                e.printStackTrace();
                Log.e("CallerIDAndroid", "Hata: " + e.getMessage());
            } finally {
                try {
                    if (socket != null)
                        socket.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
