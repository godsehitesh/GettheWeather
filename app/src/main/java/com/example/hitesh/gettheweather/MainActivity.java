package com.example.hitesh.gettheweather;

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
import java.net.URLEncoder;
import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity {

    TextView weatherText;
    String weather;
    EditText cityNameText;

    public void getWeather(View view){

        DownloadTask task = new DownloadTask();
        String URL = "";

        InputMethodManager mgr = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        mgr.hideSoftInputFromWindow(cityNameText.getWindowToken(), 0);


        try {
            String encodedCityName = URLEncoder.encode(cityNameText.getText().toString(), "UTF-8");
            URL += "http://api.openweathermap.org/data/2.5/weather?q=";
            URL += encodedCityName;
            URL += "&APPID=19484c5921071e4a9a2859d9b32343ea";

            Log.i("URL:", URL);

            task.execute(URL);
        }
        catch(Exception e)
        {
            Toast.makeText(getApplicationContext(), "Could not find weather", Toast.LENGTH_LONG);
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

                urlConnection = (HttpURLConnection)url.openConnection();

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
            catch(Exception e) {

                showToast("Could not find weather");

                return "Failed";
            }


        }

        private void showToast(final String text) {
            MainActivity.this.runOnUiThread(new Runnable() {
                public void run() {
                    Toast.makeText(MainActivity.this, "Could not find weather", Toast.LENGTH_SHORT).show();
                }
            });
        }
        @Override
        protected void onPostExecute(String result) {

            super.onPostExecute(result);

            try {

                JSONObject jsonObject = new JSONObject(result);

                String weatherInfo = jsonObject.getString("weather");

                Log.i("Weather details:", weatherInfo);

                weather = "";

                JSONArray arr = new JSONArray(weatherInfo);
                for(int i =0; i<arr.length(); i++){

                    JSONObject jsonPart = arr.getJSONObject(i);

                    weather += jsonPart.getString("main");
                    weather += ": ";
                    weather += jsonPart.getString("description");
                    weather += "\n";
                }

                if(weather!="") {
                    weatherText.setText(weather);
                }
                else
                {
                    Toast.makeText(getApplicationContext(), "Could not find weather", Toast.LENGTH_LONG);
                }

            } catch (JSONException e) {
                Toast.makeText(getApplicationContext(), "Could not find weather", Toast.LENGTH_LONG);

            }


        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        weatherText = (TextView)findViewById(R.id.weatherText);
        cityNameText = (EditText)findViewById(R.id.cityNameText);

    }
}
