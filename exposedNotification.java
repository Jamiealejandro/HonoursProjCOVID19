package com.example.honorsproj_covid19;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class exposedNotification extends AppCompatActivity {

    private Button understand;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exposed_notifcation);

        understand = (Button) findViewById(R.id.understandButton);
        understand.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i5 = new Intent(exposedNotification.this, status.class);
                startActivity(i5);
            }
        });
    }

}
