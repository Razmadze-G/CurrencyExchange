package com.example.currencyexchange;

import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import java.io.IOException;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {
    String strURL = "http://nbg.ge/rss.php";
    Map<String, Double> currencies = new HashMap<>();

    EditText currencyToBeConverted;
    Spinner convertTo;
    Spinner convertFrom;
    TextView currencyConverted;
    Button button;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        currencyToBeConverted = findViewById(R.id.currency_to_be_converted);
        convertTo = findViewById(R.id.convert_to);
        convertFrom = findViewById(R.id.convert_from);

        currencyConverted = findViewById(R.id.currency_converted);
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
            Document doc = XMLHelper.loadXMLFromString(strBody);
            NodeList description = doc.getElementsByTagName("description").item(1).getChildNodes();
            String descriptionValue = description.item(0).getNodeValue();

            org.jsoup.nodes.Document doc1 = Jsoup.parse(descriptionValue);

            Elements rows = doc1.getElementsByTag("tr");
            for (Element tr : rows) {
                Elements tds = tr.getElementsByTag("td");
                String currency = tds.get(0).text();
                int multiplier = Integer.parseInt(tds.get(1).text().replaceAll("\\D+",""));
                double rate = Double.parseDouble(tds.get(2).text()) / multiplier;
                currencies.put(currency, rate);
            }
            currencies.put("GEL", 1.0);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        String[] currenciesArray = currencies.keySet().toArray(new String[0]);
        Arrays.sort(currenciesArray);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.support_simple_spinner_dropdown_item, currenciesArray);
        convertFrom.setAdapter(adapter);
        convertTo.setAdapter(adapter);


    }
    public void onConvert(View v){
        if(currencyToBeConverted.getText().toString().isEmpty()) {
            Toast.makeText(this, "Please provide amount",Toast.LENGTH_SHORT).show();
            return;
        }
        double inputValue = Double.parseDouble(currencyToBeConverted.getText().toString());
        double selectedTo = currencies.get(convertTo.getSelectedItem().toString());
        double selectedFrom = currencies.get(convertFrom.getSelectedItem().toString());

        Double resultValue = (inputValue * selectedFrom) / selectedTo;
        DecimalFormat df = new DecimalFormat("#.###");
        df.setRoundingMode(RoundingMode.CEILING);
        String result ="Result: " + df.format(resultValue);

        currencyConverted.setText(result);
    }
}