package com.example.vishal.weatherapp;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity {

    String j1 = null;
    String j2 = null;
    EditText cityName;
    String key;
    TextView weatherReport;

    public void checkWeather (View view){

        DownloadTask task = new DownloadTask();

        //hide keyboard on button press
        InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        mgr.hideSoftInputFromWindow(cityName.getWindowToken(), 0);

        try {
            j1 = task.execute("http://apidev.accuweather.com/locations/v1/search?q="+ cityName.getText().toString() + "&apikey=hoArfRosT1215").get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

    }

    public class DownloadTask extends AsyncTask<String, Void, String> {


        @Override
        protected String doInBackground(String... urls) {

            String result = "";
            URL url;
            HttpURLConnection urlConnection = null;

            try {

                url = new URL(urls[0]);
                urlConnection = (HttpURLConnection) url.openConnection();
                InputStream in = urlConnection.getInputStream();
                InputStreamReader reader = new InputStreamReader(in);
                int data = reader.read();
                while (data != -1) {

                    char current = (char) data;
                    result += current;
                    data = reader.read();

                }

                return result;

            }
            catch (Exception e) {
                e.printStackTrace();
                return "FAILED";

            }

        }

        @Override
        protected void onPostExecute(String j1) {
            super.onPostExecute(j1);

            // print key value

            try {
                JSONArray arr = new JSONArray(j1);
                for (int i = 0; i<arr.length(); i++){

                    JSONObject jsonPart = arr.getJSONObject(i);
                    key = jsonPart.getString("Key");

                }


                try {
                    DownloadTask weather = new DownloadTask();
                    j2 = weather.execute("http://apidev.accuweather.com/currentconditions/v1/"+ key + ".json?language=en&apikey=hoArfRosT1215").get();

                    JSONArray weatherArray = new JSONArray(j2);
                    for (int i = 0; i<arr.length(); i++){

                        JSONObject weatherPart = weatherArray.getJSONObject(i);
                        weatherReport.setText(weatherPart.getString("WeatherText")+ " " + weatherPart.getString("Temperature"));

                    }

                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }



        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        cityName = (EditText) findViewById(R.id.cityName);
        weatherReport = (TextView) findViewById(R.id.weatherReport);

    }
}
