package com.ranielagno.uhack;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

/**
 * Created by Makati on 12/3/2017.
 */

public class ActivitySetDay extends Fragment{
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.activity_setdays, container, false);

        Button next = rootView.findViewById(R.id.next);
        final EditText targetDate = rootView.findViewById(R.id.targetDate);

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), GameActivity.class);
                intent.putExtra("target_date", targetDate.getText().toString());
                startActivity(intent);
            }
        });
        return rootView;
    }
}
