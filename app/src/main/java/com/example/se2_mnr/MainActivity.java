package com.example.se2_mnr;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;

public class MainActivity extends AppCompatActivity {

    private EditText userInput;
    private TextView serverResponse;
    private Button connectBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        userInput = findViewById(R.id.userInput);
        serverResponse = findViewById(R.id.serverResponse);
        connectBtn = findViewById(R.id.connectBtn);
    }

    /**
     * called when "Abschicken" Button is clicked
     * connects to "se2-submission.aau.at" and sends value in the input field (matrikelnummer)
     * response is shown in serverResponse TextView
     */
    public void connect(){
        new Thread(() -> {
            try {
                Socket TcpConnection = new Socket("se2-submission.aau.at", 20080);

                BufferedWriter sendOut = new BufferedWriter(new OutputStreamWriter(TcpConnection.getOutputStream()));
                String matrikelnummer = userInput.getText().toString();
                sendOut.write(matrikelnummer);

                BufferedReader readerIn = new BufferedReader(new InputStreamReader(TcpConnection.getInputStream()));
                String received = readerIn.readLine();

                runOnUiThread(() -> {serverResponse.setText(received);});

                sendOut.flush();
                sendOut.close();
                readerIn.close();
                TcpConnection.close();


            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }
}