package com.ranielagno.uhack;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;

public class GameActivity extends AppCompatActivity {

    private static final int DAYS_AFTER = 2;
    private static final String DJANGO_REST = "http://192.168.43.135:8000";
    private static final String PSEI_API = "http://pseapi.com/api/Stock";
    private static final String TAG = "TAG";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        GameTask gameTask = new GameTask();
        gameTask.execute("");
    }

    public String getJSONResponse(String string_url){

        String json = "";

        try {
            URL url = new URL(string_url);
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestMethod("GET");
            httpURLConnection.setRequestProperty("User-Agent", "Mozilla/5.0");
            int responseCode = httpURLConnection.getResponseCode();
            Log.d(TAG, "GET Response Code :: " + responseCode);

            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader in = new BufferedReader(new InputStreamReader(
                        httpURLConnection.getInputStream()));
                String inputLine;
                StringBuffer response = new StringBuffer();

                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();
                json = response.toString();

            } else {
                //TODO: No data for the company
                Log.d(TAG, "GET request not worked");
            }

            httpURLConnection.disconnect();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return json;
    }

    public ArrayList<String> getCompanySymbols(){
        ArrayList<String> company = new ArrayList<>();
        String response = getJSONResponse(DJANGO_REST + "/prediction/companies");
        Log.d(TAG, response);
        try {
            JSONObject jsonObject = new JSONObject(response);
            JSONArray jsonArray = jsonObject.getJSONArray("symbols");
            for (int i = 0; i < jsonArray.length(); i++){
                company.add(String.valueOf(jsonArray.get(i)));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.d(TAG, company.toString());
        return company;
    }

    //TODO: Parameter DAYS_AFTER
    private static String[] generateStartEndDate(){

        JSONObject jsonObject = new JSONObject();
        String arrStartEndDate [] = new String[2];
        URL url;

        try {

            jsonObject.accumulate("range", DAYS_AFTER);
            String json = jsonObject.toString();
            url = new URL(DJANGO_REST + "/prediction/date");
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.setDoOutput(true);
            httpURLConnection.setRequestProperty("Content-Type", "application/json; charset=utf-8");
            OutputStream outputStream = httpURLConnection.getOutputStream();
            outputStream.write(json.getBytes());
            outputStream.flush();
            outputStream.close();
            InputStream inputStream = httpURLConnection.getInputStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "iso-8859-1"));
            String result = "";
            String line = "";
            while((line = bufferedReader.readLine()) != null){
                result += line;
            }
            bufferedReader.close();
            inputStream.close();
            httpURLConnection.disconnect();

            Log.d(TAG, result);

            JSONObject reader = new JSONObject(result.toString());

            arrStartEndDate [0] = reader.getString("start_date");
            arrStartEndDate [1] = reader.getString("end_date");

            Log.d(TAG, arrStartEndDate[0] + " - " + arrStartEndDate[1]);

        } catch (JSONException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return arrStartEndDate;

    }

    private void getStartDayReport(String symbol, String start_date){
        String response = getJSONResponse(PSEI_API + "/" + symbol + "/" + start_date);
        Log.d(TAG, response);
    }

    private class GameTask extends AsyncTask<String, Integer, String> {

        @Override
        protected String doInBackground(String... params) {
            getCompanySymbols();

            return "";
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
        }
    }
}
