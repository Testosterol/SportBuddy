package com.example.dtrebula.bbc2;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

/**
 * Created by dtrebula on 2. 5. 2017.
 */

public class FAQActivity extends AppCompatActivity {
    private TextView txtViewFAQ;
    private Button  backButton;

    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_faq_layout);
        txtViewFAQ = (TextView) findViewById(R.id.textViewFAQ);
        backButton = (Button) findViewById(R.id.backHelp_button);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FAQActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

        String FAQ = "Welcome to SportBuddy Frequently Asked Questions/Guides.";
        String FAQ1 = "1. Filling out your profile information is suggested but not a neccessity since " +
                "its usage will be available in next developement version.";
        String FAQ2 = "2. To be able to create event you must fill all the information, even choose the photo of event.";
        String FAQ3 = "3. Please take in mind that size of the photo is recommended to be small or not bigger than 1mb for profile photos and " +
                "event photos aswell, it can " +
                "cause problems with not loading the image and crashing the application on server side.";
        String FAQ4 = "4. Please take in count that you must have solid internet connection, if your device can have small pauses between" +
                "packets, the application does not have to start or save information or load events.";
        String FAQ5 = "5. You cannot join an event which is Older than actual date.";
        String FAQ6 = "7. Older events will be automatically deleted from the database in next development version on the server side.";
        String FAQ7 = "8. If you choose to allow notifications, notifications will " +
                "be displayed after loggining in to the aplication, if you have joined any events based on settings you have chosen.";
        String FAQ8 = "6. You cant join any event more than once.";
        String FAQ9 = "9. Choosing option help will allow you to send an e-mail to developers team";
        String FAQ10 = "10. Choosing show map option will open google maps or any other map device you have instaled on your mobile phone";
        String FAQ11 = "11. Once you have saved your settings with notifications, it will saved in database so you dont have to do it everytime, unless you want to change something.";

        txtViewFAQ.setMovementMethod(new ScrollingMovementMethod());
        txtViewFAQ.setText(FAQ +"\n\n"+ FAQ1 +"\n\n"+ FAQ2 +"\n\n"+ FAQ3 +"\n\n"+ FAQ4 +"\n\n"+ FAQ5 +"\n\n"+
                FAQ8 +"\n\n"+ FAQ6 +"\n\n"+ FAQ7 +"\n\n"+ FAQ9 +"\n\n"+ FAQ10 +"\n\n"+ FAQ11);

    }
}
