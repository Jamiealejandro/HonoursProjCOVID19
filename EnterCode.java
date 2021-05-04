package com.example.honorsproj_covid19;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.UUID;

public class EnterCode extends AppCompatActivity {

    private Button getCode;
    private Button submitCode;
    private TextView IDBox;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enter_code);
        getCode = (Button) findViewById(R.id.getID);
        submitCode = (Button) findViewById(R.id.submitID);
        IDBox = (TextView) findViewById(R.id.IDBox);
        final String[] uniqueID = {null};

        getCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                uniqueID[0] = UUID.randomUUID().toString();
                IDBox.setText(uniqueID[0]);
            }
        });

        submitCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(EnterCode.this, exposedNotification.class);
                startActivity(i);
            }
        });
    }
}