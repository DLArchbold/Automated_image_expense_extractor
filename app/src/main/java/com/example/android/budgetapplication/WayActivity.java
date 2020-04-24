package com.example.android.budgetapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class WayActivity extends AppCompatActivity {





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_way);


        TextView manual_entry = (TextView) findViewById(R.id.manual_entry);

        manual_entry.setOnClickListener(new View.OnClickListener()
        {

            @Override
            public void onClick(View view)
            {

                Intent manualEntryIntent = new Intent(WayActivity.this, ManualEntryActivity.class);
                startActivity(manualEntryIntent);
                finish();

            }
        });

        TextView image_recognition_entry = (TextView) findViewById(R.id.image_recognition_entry);

        image_recognition_entry.setOnClickListener(new View.OnClickListener()
        {

            @Override
            public void onClick(View view)
            {

                Intent ImageRecognitionEntryIntent = new Intent(WayActivity.this, ImageRecognitionEntryActivity.class);
                startActivity(ImageRecognitionEntryIntent);
                //finish();

            }
        });



    }




}
