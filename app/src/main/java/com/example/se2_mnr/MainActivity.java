package com.example.se2_mnr;

import static java.util.stream.Collectors.toList;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
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
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Arrays;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class MainActivity extends AppCompatActivity {

    private EditText userInput;
    private TextView serverResponse;
    private Button connectBtn, solveBtn;

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
        solveBtn = findViewById(R.id.solveBtn);

        connectBtn.setOnClickListener(connect);
        solveBtn.setOnClickListener(solve);
    }

    /**
     * called when "Abschicken" Button is clicked
     * connects to "se2-submission.aau.at" and sends value in the input field (matrikelnummer)
     * response is shown in serverResponse TextView
     */
    public View.OnClickListener connect = new View.OnClickListener(){
        @Override
        public void onClick(View v){
            Log.i("connectBtn", "clicked");
            new Thread(() -> {
                try {
                    Socket TcpConnection = new Socket("se2-submission.aau.at", 20080);
                    Log.i("TcpClient", "created socket");

                    PrintWriter sendOut = new PrintWriter( new OutputStreamWriter(TcpConnection.getOutputStream()), true);
                    BufferedReader readerIn = new BufferedReader(new InputStreamReader(TcpConnection.getInputStream()));

                    String matrikelnummer = userInput.getText().toString();
                    sendOut.println(matrikelnummer);
                    Log.i("TcpClient", "message sent: " + matrikelnummer);

                    String received = readerIn.readLine();
                    Log.i("TcpClient", received);

                    runOnUiThread(() -> {serverResponse.setText(received);});

                    sendOut.flush();
                    sendOut.close();
                    readerIn.close();
                    TcpConnection.close();


                } catch (IOException e) {
                    Log.e("Failure", e.toString());
                }
            }).start();
        }
    };

    /**
     * called when "Rechnen" Button is clicked
     * converts every second digit of the input field to ASCII characters
     */
    public View.OnClickListener solve = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Log.i("solveBtn", "clicked");

            char[] matrikelnummer = userInput.getText().toString().toCharArray();
            char[] result = new char[matrikelnummer.length];

            for(int i=0; i<matrikelnummer.length;i++){
                result[i] = (i%2==0) ? matrikelnummer[i] : (char)(matrikelnummer[i]+48);
            }

            serverResponse.setText(new String(result));
            Log.i("solveBtn", new String(result));


        }
    };
}