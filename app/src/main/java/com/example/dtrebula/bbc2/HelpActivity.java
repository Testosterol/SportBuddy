package com.example.dtrebula.bbc2;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

/**
 * Created by dtrebula on 10. 11. 2016.
 */

public class HelpActivity extends AppCompatActivity {


    private EditText sendHelp_text;
    private Button sendEmail, backButton;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);
        sendHelp_text = (EditText) findViewById(R.id.sendHelp_text);
        sendEmail = (Button) findViewById(R.id.sendEmail_button);
        backButton = (Button) findViewById(R.id.backHelp_button);

        sendEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String helpText = sendHelp_text.getText().toString();
                if(!helpText.isEmpty()){
                    sendEmail(helpText);
                }
            }
        });
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HelpActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private void sendEmail(final String helpText){
        Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
        emailIntent.setData(Uri.parse("mailto:" + "denistreb@gmail.com"));
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, ""); //subject
        emailIntent.putExtra(Intent.EXTRA_TEXT, helpText); //body
        try {
            startActivity(Intent.createChooser(emailIntent, "Send email using..."));
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(HelpActivity.this, "No email clients installed.", Toast.LENGTH_SHORT).show();
        }

    }
}
