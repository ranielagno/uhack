package com.ranielagno.uhack;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

/**
 * Created by Makati on 12/3/2017.
 */

public class ActivityEvent extends Fragment {
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.activity_event, container, false);

        Button btn_car_reg = (Button) rootView.findViewById(R.id.loginButton);

        btn_car_reg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
