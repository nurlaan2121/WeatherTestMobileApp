package com.example.weathertest;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.TaskStackBuilder;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class MainActivity2 extends AppCompatActivity {

    private static EditText user_field;
    private static Button main_button;
    private static TextView result_info;
    private static final String API_KEY = "d91c2589af32ec2147c770dbafe8681a"; // Замените на ваш ключ
    private static final String BASE_URL = "https://api.openweathermap.org/data/2.5/weather";

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        user_field = findViewById(R.id.user_field);
        main_button = findViewById(R.id.main_button);
        result_info = findViewById(R.id.result_info);


        main_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (user_field.getText().toString().trim().isEmpty()) {
                    Toast.makeText(MainActivity2.this, R.string.nouser_input, Toast.LENGTH_LONG).show();
                } else {
                    String userCity = user_field.getText().toString().trim();
                    String url = BASE_URL + "?q=" + userCity + "&appid=" + API_KEY + "&units=metric&lang=ru";
                    new GetWeatherTask().execute(url);
                }
            }
        });
    }

    private static class GetWeatherTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... strings) {
            HttpURLConnection connection = null;
            BufferedReader bufferedReader = null;

            try {
                URL url = new URL(strings[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();

                InputStream stream = connection.getInputStream();
                bufferedReader = new BufferedReader(new InputStreamReader(stream));

                StringBuilder buffer = new StringBuilder();
                String line;

                while ((line = bufferedReader.readLine()) != null) {
                    buffer.append(line).append("\n");
                }

                return buffer.toString();
            } catch (MalformedURLException e) {
                throw new RuntimeException(e);
            } catch (IOException e) {
                return "error";
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
                if (bufferedReader != null) {
                    try {
                        bufferedReader.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        @Override
        protected void onPostExecute(String resultset) {
            super.onPostExecute(resultset);
            try {
                JSONObject object = new JSONObject(resultset);
                double temperature = object.getJSONObject("main").getDouble("temp");
                double feelsLike = object.getJSONObject("main").getDouble("feels_like");
                String description = object.getJSONArray("weather").getJSONObject(0).getString("description");
                String country = object.getJSONObject("sys").getString("country");

                String resultText = "Температура: " + temperature + "\nШамал: " + feelsLike + "\nОписание: " + description + "\nОлко: " + country;
                result_info.setText(resultText);
            } catch (JSONException e) {
                e.printStackTrace();
                result_info.setText("Ошибка при обработке данных. Убедитесь, что введенный город корректен.");
            }
        }
    }
}


