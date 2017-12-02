package com.ranielagno.uhack;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

/**
 * Created by Makati on 12/2/2017.
 */

public class ActivityLogin extends Fragment {
    //Button login;


    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.activity_login, container, false);

        Button btn_car_reg = (Button) rootView.findViewById(R.id.loginButton);

        btn_car_reg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                /*API CODE
                RequestQueue requestQueue;
                String URL = "https://acarjsystem.herokuapp.com/carlist/";
                StringRequest request;
                requestQueue = Volley.newRequestQueue(getContext());
                request = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            if(jsonObject.getString("Error").equals("False")) {
                                Toast.makeText(getActivity(), "Car added.", Toast.LENGTH_SHORT).show();
                                Fragment nextFrag = new locate_before();
                                FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                                transaction.replace(R.id.fragment_container, nextFrag);
                                transaction.addToBackStack(null);
                                transaction.commit();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                }){
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        HashMap<String,String> hashMap = new HashMap<String, String>();
                        hashMap.put("user",uname);
                        hashMap.put("plate_no",reg_plate.getText().toString());
                        hashMap.put("car_model",reg_carmodel.getText().toString());

                        return hashMap;
                    }
                };

                requestQueue.add(request);*/

                Fragment nextFrag = new ActivityHomepage();
                FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.fragment_container, nextFrag);
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });

























        // Inflate the layout for this fragment
        return rootView;
    }

}
