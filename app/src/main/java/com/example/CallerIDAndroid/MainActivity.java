package com.example.CallerIDAndroid;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import java.io.OutputStream;
import java.net.Socket;

// Kendi projenin R sınıfı
import com.example.CallerIDAndroid.R;

public class MainActivity extends AppCompatActivity {

    private EditText editNumber;
    private Button btnSendTest;

    private static final String DELPHI_IP = "192.168.1.12"; // Delphi PC LAN IP
    private static final int DELPHI_PORT = 20000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editNumber = findViewById(R.id.editNumber);
        btnSendTest = findViewById(R.id.btnSendTest);

        btnSendTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String number = editNumber.getText().toString().trim();
                if (!number.isEmpty()) {
                    sendNumberToDelphi(number);
                } else {
                    Toast.makeText(MainActivity.this, "Numara giriniz", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void sendNumberToDelphi(String number) {
        new Thread(() -> {
            Socket socket = null;
            try {
                socket = new Socket(DELPHI_IP, DELPHI_PORT);

                // Delphi6 Indy9 ile uyumlu byte gönderimi
                OutputStream out = socket.getOutputStream();
                String data = number + "\n"; // ReadLn ile uyumlu
                out.write(data.getBytes("ISO-8859-1"));
                out.flush();

                runOnUiThread(() ->
                        Toast.makeText(MainActivity.this, "Numara gönderildi: " + number, Toast.LENGTH_SHORT).show()
                );

            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() ->
                        Toast.makeText(MainActivity.this, "Hata: " + e.getMessage(), Toast.LENGTH_LONG).show()
                );
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


