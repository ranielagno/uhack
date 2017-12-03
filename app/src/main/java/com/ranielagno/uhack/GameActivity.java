package com.ranielagno.uhack;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

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

    public int days_after = 0;
    private static final String DJANGO_REST = "http://172.16.2.213:8000";
    private static final String PSEI_API = "http://pseapi.com/api/Stock";
    private static final String TAG = "TAG";
    public ArrayList<String> companies;

    TextView balance, added_days, company, start_date, start_stock, end_date;
    EditText bet;
    FrameLayout  progressBarHolder;
    Button low, high;

    String s_date, e_date, symbol;
    public static int counter = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        Intent intent = getIntent();
        days_after = Integer.parseInt(intent.getExtras().getString("target_date"));
        Log.d(TAG, String.valueOf(days_after));

        balance = (TextView) findViewById(R.id.balance);
        added_days = (TextView) findViewById(R.id.added_days);
        company = (TextView) findViewById(R.id.company);
        start_date = (TextView) findViewById(R.id.start_date);
        start_stock = (TextView) findViewById(R.id.start_stock);
        end_date = (TextView) findViewById(R.id.end_date);
        bet = (EditText) findViewById(R.id.bet);

        progressBarHolder = (FrameLayout) findViewById(R.id.progressBarHolder);

        high = (Button) findViewById(R.id.high);
        low = (Button) findViewById(R.id.low);

        high.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HiLowTask hiLowTask = new HiLowTask();
                hiLowTask.execute("Up");
            }
        });

        low.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HiLowTask hiLowTask = new HiLowTask();
                hiLowTask.execute("Down");
            }
        });

        startGame();

    }

    public void startGame(){
        GameTask gameTask = new GameTask();
        gameTask.execute(days_after);
    }

    public String buttonClicked(String hiLow){

        String end_stock = bet.getText().toString();
        JSONObject jsonObject = new JSONObject();
        String response = "";

        try {

            jsonObject.accumulate("start_date", s_date);
            jsonObject.accumulate("end_date", e_date);
            jsonObject.accumulate("symbol", symbol);
            jsonObject.accumulate("bet", end_stock);
            jsonObject.accumulate("choice", hiLow);

            response = postJSONResponse(DJANGO_REST + "/prediction/save", jsonObject.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Log.d(TAG, "HiLow" + response);
        return response;

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

    public String postJSONResponse(String string_url, String json){

        try {
            URL url = new URL(string_url);
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
            while ((line = bufferedReader.readLine()) != null) {
                result += line;
            }
            bufferedReader.close();
            inputStream.close();
            httpURLConnection.disconnect();

            Log.d(TAG, "Reader - " + result);
            return result;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
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
            jsonObject.accumulate("range", days_after);
            jsonObject.accumulate("symbol", symbol);
            companies.remove(companies.lastIndexOf(symbol));
            Log.d(TAG, "Arraylist - "+ companies);
            String json = jsonObject.toString();
            String result = postJSONResponse(DJANGO_REST + "/prediction/date", json);
            JSONObject reader = new JSONObject(result);

            arrStartEndDate [0] = reader.getString("start_date");
            arrStartEndDate [1] = reader.getString("end_date");
            arrStartEndDate [2] = symbol;

            Log.d(TAG, arrStartEndDate[0] + " - " + arrStartEndDate[1] + " - " + arrStartEndDate[2]);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return arrStartEndDate;

    }

    private String randSymbol(ArrayList<String> arr){
        Random random = new Random();
        return arr.get(random.nextInt(arr.size()));
    }

    private String getStartDayReport(String... params){
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.accumulate("start_date", params[0]);
            jsonObject.accumulate("end_date", params[1]);
            jsonObject.accumulate("symbol", params[2]);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String response = postJSONResponse(DJANGO_REST + "/prediction/start_end", jsonObject.toString());
        Log.d(TAG, "GetStartDayReport " + response);
        return response;
    }

    public double[] getStartAndEndPrice(String response){
        double prices [] = new double[2];

        try{
            JSONObject decoder = new JSONObject(response);
            prices[0] = decoder.getDouble("start_price");
            prices[1] = decoder.getDouble("end_price");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return prices;
    }

    private class GameTask extends AsyncTask<Integer, Integer, String> {

        AlphaAnimation inAnimation;
        AlphaAnimation outAnimation;
        String arr [];
        double price [];

        @Override
        protected String doInBackground(Integer... params) {
            if(companies == null)
                companies = getCompanySymbols();

            arr = generateStartEndDateandSymbol();
            price = getStartAndEndPrice(getStartDayReport(arr[0], arr[1], arr[2]));

            return "";
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            inAnimation = new AlphaAnimation(0f, 1f);
            inAnimation.setDuration(200);
            progressBarHolder.setAnimation(inAnimation);
            progressBarHolder.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            start_date.setText(arr[0]);
            end_date.setText(arr[1]);
            company.setText(arr[2]);
            start_stock.setText(String.valueOf(price[0]));

            outAnimation = new AlphaAnimation(1f, 0f);
            outAnimation.setDuration(200);
            progressBarHolder.setAnimation(outAnimation);
            progressBarHolder.setVisibility(View.GONE);

            s_date = arr[0];
            e_date = arr[1];
            symbol = arr[2];
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
        }
    }

    private class HiLowTask extends AsyncTask <String, Integer, Boolean>{

        @Override
        protected Boolean doInBackground(String... params) {
            String response = buttonClicked(params[0].toString());
            try {
                JSONObject jsonObject = new JSONObject(response);
                return jsonObject.getBoolean("result");
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);

            if(result){
                Toast.makeText(GameActivity.this, "You got it right!", Toast.LENGTH_LONG).show();
            }else{
                Toast.makeText(GameActivity.this, "You failed!", Toast.LENGTH_LONG).show();
            }


            ++counter;
            if(counter == 10){
                changeFragment();
            }

            GameTask task = new GameTask();
            task.execute(days_after);

        }

        private void changeFragment(){
            Fragment nextFrag = new ActivityHomepage();
            FragmentTransaction transaction = GameActivity.this.getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment_container, nextFrag);
            transaction.addToBackStack(null);
            transaction.commit();
        }
    }
}
