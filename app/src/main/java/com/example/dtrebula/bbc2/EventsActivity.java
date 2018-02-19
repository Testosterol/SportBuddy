package com.example.dtrebula.bbc2;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

/**
 * Created by dtrebula on 10. 11. 2016.
 */

public class EventsActivity extends AppCompatActivity {

    private Button createEvent, showEvents, backButton, myEvents;

    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_events);
        backButton = (Button) findViewById(R.id.backEvents_button);
        createEvent = (Button) findViewById(R.id.createEvent_button);
        showEvents = (Button) findViewById(R.id.showEvents_button);
        myEvents = (Button) findViewById(R.id.btnShowMyOwn);

        createEvent.setOnClickListener(new View.OnClickListener() {
        @Override
            public void onClick(View v) {
                Intent intent = new Intent(EventsActivity.this, CreateEventActivity.class);
                startActivity(intent);
                finish();
            }
        });
        myEvents.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(EventsActivity.this, ShowMyEventsActivity.class);
                startActivity(intent);
                finish();
            }
        });
        showEvents.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(EventsActivity.this, ShowEventActivity.class);
                startActivity(intent);
                finish();
                //showEvents();
            }
        });
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(EventsActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
}
