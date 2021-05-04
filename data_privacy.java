package com.example.honorsproj_covid19;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class data_privacy extends AppCompatActivity {

    private Button Agree;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_privacy);

        Agree = findViewById(R.id.buttonAgree);
        this.Agree = Agree;


        Agree.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent i2 = new Intent(data_privacy.this, status.class);
                startActivity(i2);
            }
        });

    }
}