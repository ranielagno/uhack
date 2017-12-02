package com.ranielagno.uhack;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

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

/**
 * Created by Makati on 12/2/2017.
 */

public class ActivityLogin extends Fragment {

    public final static String DJANGO_REST = "http://192.168.43.135:8000";
    public final static String TAG = "TAG";

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.activity_login, container, false);

        Button login = rootView.findViewById(R.id.loginButton);
        final EditText username = rootView.findViewById(R.id.username_text);
        final EditText password =  rootView.findViewById(R.id.password_text);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoginTask loginTask = new LoginTask();
                loginTask.execute(username.getText().toString(), password.getText().toString());
            }
        });

        return rootView;
    }

    private class LoginTask extends AsyncTask<String,Integer,String>{

        AlertDialog alertDialogBuilder;

        @Override
        protected String doInBackground(String... params) {
            String username = params[0];
            String password = params[1];

            try {

                JSONObject jsonObject = new JSONObject();
                jsonObject.accumulate("username", username);
                jsonObject.accumulate("password", password);
                String json = jsonObject.toString();

                URL url = new URL(DJANGO_REST + "/accounts/login");
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
                JSONObject reader = new JSONObject(result.toString());

                return reader.getString("status");

            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (ProtocolException e) {
                e.printStackTrace();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            alertDialogBuilder = new AlertDialog.Builder(getContext()).create();
            alertDialogBuilder.setTitle("Status");
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if(s == null){
                alertDialogBuilder.setMessage("Login Failed. Can't connect to the server.");
            }else if(s.equals("success")){
                alertDialogBuilder.setMessage("Login Success!");
                changeFragment();
            }else {
                alertDialogBuilder.setMessage("Login Failed! Your username or password is incorrect.");
            }
            alertDialogBuilder.show();

        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
        }

        private void changeFragment(){
            Fragment nextFrag = new ActivityHomepage();
            FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment_container, nextFrag);
            transaction.addToBackStack(null);
            transaction.commit();
        }
    }

}
