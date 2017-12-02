package com.ranielagno.uhack;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

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
import java.util.Random;

public class GameActivity extends AppCompatActivity {

    private static final int DAYS_AFTER = 2;
    private static final String DJANGO_REST = "http://172.16.2.213:8000";
    private static final String PSEI_API = "http://pseapi.com/api/Stock";
    private static final String TAG = "TAG";
    public ArrayList<String> companies;

    TextView balance, added_days, company, start_date, start_stock, end_date;
    EditText bet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        balance = (TextView) findViewById(R.id.balance);
        added_days = (TextView) findViewById(R.id.added_days);
        company = (TextView) findViewById(R.id.company);
        start_date = (TextView) findViewById(R.id.start_date);
        start_stock = (TextView) findViewById(R.id.start_stock);
        end_date = (TextView) findViewById(R.id.end_date);
        bet = (EditText) findViewById(R.id.bet);

        GameTask gameTask = new GameTask();
        gameTask.execute(DAYS_AFTER);
    }

    public void lowButtonClicked(View v){

    }

    public void highButtonClicked(View v){

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

    private String[] generateStartEndDateandSymbol(){

        JSONObject jsonObject = new JSONObject();
        String arrStartEndDate [] = new String[3];
        URL url;

        try {
            String symbol = randSymbol(companies);
            Log.d(TAG, "Find this " + symbol);
            jsonObject.accumulate("range", DAYS_AFTER);
            jsonObject.accumulate("symbol", symbol);
            companies.remove(companies.lastIndexOf(symbol));
            Log.d(TAG, "Arraylist - "+ companies);
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

            Log.d(TAG, "Reader - " + result);

            JSONObject reader = new JSONObject(result.toString());

            arrStartEndDate [0] = reader.getString("start_date");
            arrStartEndDate [1] = reader.getString("end_date");
            arrStartEndDate [2] = symbol;

            Log.d(TAG, arrStartEndDate[0] + " - " + arrStartEndDate[1] + " - " + arrStartEndDate[2]);

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

    private String randSymbol(ArrayList<String> arr){
        Random random = new Random();
        return arr.get(random.nextInt(arr.size() - 1));
    }

    private String getStartDayReport(String symbol, String start_date){
        String response = getJSONResponse(PSEI_API + "/" + symbol + "/" + start_date);
        Log.d(TAG, "GetStartDayReport " + response);
        return response;
    }

    private class GameTask extends AsyncTask<Integer, Integer, String> {

        String arr [];
        @Override
        protected String doInBackground(Integer... params) {
            if(companies == null)
                companies = getCompanySymbols();

            arr = generateStartEndDateandSymbol();
            String response = getStartDayReport(arr[2], arr[0]);
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
            start_date.setText(arr[0]);
            end_date.setText(arr[1]);
            company.setText(arr[2]);
        }
    }
}
