package com.example.CallerIDAndroid;

// Kendi projenin R sınıfı
import com.example.CallerIDAndroid.R;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.OutputStream;
import java.net.Socket;

public class MainActivity extends AppCompatActivity {

    private EditText editNumber;
    private Button btnSendTest;

    // Delphi PC IP ve port
    private static final String DELPHI_IP = "192.168.1.12"; // LAN IP
    private static final int DELPHI_PORT = 20000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // EditText ve Button referansları
        editNumber = findViewById(R.id.editNumber);
        btnSendTest = findViewById(R.id.btnSendTest);

        // -----------------------------
        // ÖNEMLİ: TextWatcher veya addTextChangedListener yok!
        // -----------------------------

        // Test butonuna basınca TCP gönder
        btnSendTest.setOnClickListener(v -> {
            String number = "08505320372"; 
            if(!number.isEmpty()) {
                sendNumberToDelphi(number);
                Toast.makeText(this, "Numara gönderildi: " + number, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Numara boş!", Toast.LENGTH_SHORT).show();
            }

        
            
        });

        // Overlay izni kontrolü
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.canDrawOverlays(this)) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                        Uri.parse("package:" + getPackageName()));
                startActivity(intent);
                Toast.makeText(this,
                        "CallerIDAndroid için ekran üstü izinlerini açın.",
                        Toast.LENGTH_LONG).show();
            }
        }

        // Accessibility izinleri kontrolü
        if(!isAccessibilityServiceEnabled()) {
            Intent intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
            startActivity(intent);
            Toast.makeText(this,
                    "CallerIDAndroid için Erişilebilirlik iznini açın ve servisi etkinleştirin.",
                    Toast.LENGTH_LONG).show();
        }
    }

    // Kullanıcı Accessibility servisini açmış mı kontrol et
    private boolean isAccessibilityServiceEnabled() {
        int accessibilityEnabled = 0;
        try {
            accessibilityEnabled = Settings.Secure.getInt(
                    getContentResolver(),
                    Settings.Secure.ACCESSIBILITY_ENABLED
            );
        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
        }
        return accessibilityEnabled == 1;
    }

    // TCP ile Delphi'ye gönder
    private void sendNumberToDelphi(String number) {
        new Thread(() -> {
            Socket socket = null;
            try {
                socket = new Socket(DELPHI_IP, DELPHI_PORT);
                OutputStream out = socket.getOutputStream();
                String data = number + "\n"; // Delphi ReadLn ile uyumlu
                out.write(data.getBytes("ISO-8859-1"));
                out.flush();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    if(socket != null) socket.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}


