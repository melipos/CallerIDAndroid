package com.example.CallerIDAndroid;

import android.accessibilityservice.AccessibilityService;
import android.view.accessibility.AccessibilityEvent;
import android.util.Log;
import java.io.PrintWriter;
import java.net.Socket;

public class MyAccessibilityService extends AccessibilityService {

    // Delphi PC IP ve port (LAN IP)
    private static final String DELPHI_IP = "192.168.1.100"; // kendi PC LAN IP
    private static final int DELPHI_PORT = 20000;

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        if (event == null) return;

        // Genellikle gelen arama TYPE_WINDOW_STATE_CHANGED veya TYPE_NOTIFICATION_STATE_CHANGED
        CharSequence eventText = getEventText(event);
        if (eventText != null && eventText.length() > 0) {

            String incomingNumber = extractPhoneNumber(eventText.toString());
            if (incomingNumber != null && !incomingNumber.isEmpty()) {
                Log.d("CallerIDService", "Gelen numara: " + incomingNumber);

                // TCP ile Delphi server'a gönder
                sendNumberToDelphi(incomingNumber);
            }
        }
    }

    @Override
    public void onInterrupt() {
        // Servis kesilirse çalışacak
    }

    // AccessibilityEvent içindeki metni al
    private CharSequence getEventText(AccessibilityEvent event) {
        if (event.getText() != null && !event.getText().isEmpty()) {
            return event.getText().toString();
        }
        return null;
    }

    // Burada sadece demo: eventText’i direkt numara olarak alıyoruz
    private String extractPhoneNumber(String text) {
        // Gerçek kullanımda regex veya paket bazlı parse gerekebilir
        return text; // demo amaçlı
    }

    // TCP client ile Delphi server’a gönder
    private void sendNumberToDelphi(final String number) {
        new Thread(() -> {
            try {
                Socket socket = new Socket(DELPHI_IP, DELPHI_PORT);
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                out.println(number);
                socket.close();
                Log.d("CallerIDService", "Numara gönderildi: " + number);
            } catch (Exception e) {
                e.printStackTrace();
                Log.e("CallerIDService", "Hata: " + e.getMessage());
            }
        }).start();
    }
}




