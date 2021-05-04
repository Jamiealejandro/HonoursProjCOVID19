package com.example.honorsproj_covid19;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.widget.ConstraintLayout;


public class MainActivity extends Activity {
    Button Continue;
    ConstraintLayout root;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Continue = findViewById(R.id.confirmOnboarding);
        this.Continue = Continue;
        root = findViewById(R.id.root_element);
        this.root = root;

        Continue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDataPrivacyWindow();
            }
        });

    }


    public void showDataPrivacyWindow(){

        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("Are you 16 years or over?");
        dialog.setMessage("You must confirm that you are 16 years or over to be able to use this application");

        LayoutInflater inflater = LayoutInflater.from(this);
        View agerequire = inflater.inflate(R.layout.activity_agerequire, null);
        dialog.setView(agerequire);


        startActivity(new Intent(MainActivity.this, data_privacy.class));
        finish();

    }

}