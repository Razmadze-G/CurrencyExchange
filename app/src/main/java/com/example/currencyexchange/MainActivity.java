package com.example.currencyexchange;

import android.os.Bundle;
import android.os.StrictMode;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {
    String strURL = "http://nbg.ge/rss.php";
    String strBody;

    EditText currency_to_be_converted;
    Spinner convert_to;
    Spinner convert_from;
    TextView currency_converted;
    Button button;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        currency_to_be_converted = findViewById(R.id.currency_to_be_converted);
        convert_to = findViewById(R.id.convert_to);
        convert_from = findViewById(R.id.convert_from);
        currency_converted = findViewById(R.id.currency_converted);
        button = findViewById(R.id.button);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(strURL)
                .build();
        try {
            Response response = client.newCall(request).execute();
            String strBody = response.body().string();

        } catch (IOException e) {
            e.printStackTrace();
        }
        
    }
}