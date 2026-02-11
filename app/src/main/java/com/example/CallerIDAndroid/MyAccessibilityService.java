package com.example.CallerIDAndroid;

import android.accessibilityservice.AccessibilityService;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;

import java.io.OutputStream;
import java.net.Socket;
import java.util.List;

// Kendi projenin R sınıfı
import com.example.CallerIDAndroid.R;


public class MyAccessibilityService extends AccessibilityService {

    private static final String DELPHI_IP = "192.168.1.100"; // Delphi PC IP
    private static final int DELPHI_PORT = 20000;

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        int eventType = event.getEventType();

        // Gelen arama ve değişen ekran durumlarını yakala
        if(eventType == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED ||
           eventType == AccessibilityEvent.TYPE_NOTIFICATION_STATE_CHANGED ||
           eventType == AccessibilityEvent.TYPE_VIEW_TEXT_CHANGED) {

            List<CharSequence> texts = event.getText();
            if(texts != null && !texts.isEmpty()) {
                String text = texts.toString(); // Listeyi stringe çevir
                Log.d("CallerID", "Detected event text: " + text);

                // Regex ile sadece rakamları ve + işaretini alıyoruz
                String number = extractPhoneNumber(text);
                if(number != null && !number.isEmpty()) {
                    sendNumberToDelphi(number);
                }
            }
        }
    }

    // Arama numarasını ayıkla
    private String extractPhoneNumber(String text) {
        return text.replaceAll("[^0-9+]", "");
    }

    // TCP ile Delphi’ye gönder
    private void sendNumberToDelphi(String number) {
        new Thread(() -> {
            Socket socket = null;
            try {
                socket = new Socket(DELPHI_IP, DELPHI_PORT);
                OutputStream out = socket.getOutputStream();
                String data = number + "\n"; // Delphi ReadLn ile uyumlu
                out.write(data.getBytes("ISO-8859-1"));
                out.flush();
                Log.d("CallerID", "Sent number to Delphi: " + number);
            } catch (Exception e) {
                e.printStackTrace();
                Log.e("CallerID", "Error sending number: " + e.getMessage());
            } finally {
                try {
                    if(socket != null) socket.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    @Override
    public void onInterrupt() {
        // Servis kesilirse buraya düşer
    }
}

