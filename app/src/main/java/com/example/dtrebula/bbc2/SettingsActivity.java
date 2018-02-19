package com.example.dtrebula.bbc2;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Toast;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by dtrebula on 1. 5. 2017.
 */

public class SettingsActivity extends AppCompatActivity {

    private Button saveButton, backButton;
    private CheckBox sendNotificationsCheckBox,allEventsCheckBox, currentDayCheckBox;
    private String sendNotifications = "false";
    private String sendNotificationsOfCurrentDay = "false";
    private String sendNotificationsOfAllJoined = "false";
    String user = LoginActivity.getVariableId();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        saveButton = (Button) findViewById(R.id.save_button);
        backButton = (Button) findViewById(R.id.back_button);
        sendNotificationsCheckBox = (CheckBox) findViewById(R.id.sendNotifications);
        allEventsCheckBox = (CheckBox) findViewById(R.id.sendAllEventsNotifications);
        currentDayCheckBox = (CheckBox) findViewById(R.id.sendCurrentDayEventsNotifications);


        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(sendNotificationsCheckBox.isChecked()){
                    sendNotifications = "true";
                    if(allEventsCheckBox.isChecked() && !currentDayCheckBox.isChecked()){
                        sendNotificationsOfAllJoined = "true";
                        sendNotificationsOfCurrentDay = "false";
                        Toast.makeText(SettingsActivity.this, "Changes saved!", Toast.LENGTH_SHORT).show();
                    }
                    if(!allEventsCheckBox.isChecked() && currentDayCheckBox.isChecked()){
                        sendNotificationsOfCurrentDay = "true";
                        sendNotificationsOfAllJoined = "false";
                        Toast.makeText(SettingsActivity.this, "Changes saved!", Toast.LENGTH_SHORT).show();
                    }
                    if(!allEventsCheckBox.isChecked() && !currentDayCheckBox.isChecked()){
                        sendNotificationsOfCurrentDay = "false";
                        sendNotificationsOfAllJoined = "false";
                        Snackbar.make(v, "Please Check atleast one option!", Snackbar.LENGTH_LONG).show();
                    }
                    if(allEventsCheckBox.isChecked() && currentDayCheckBox.isChecked()){
                        sendNotificationsOfCurrentDay = "false";
                        sendNotificationsOfAllJoined = "false";
                        Snackbar.make(v, "Please Check only one option!", Snackbar.LENGTH_LONG).show();
                    }
                }else{
                    sendNotifications = "false";
                    Toast.makeText(SettingsActivity.this, "Changes saved!", Toast.LENGTH_SHORT).show();
                }
                sendNotificationsSettingsSave();
            }
        });
    }
    private void sendNotificationsSettingsSave() {
        String tag_string_req = "req_login";
        StringRequest strReq = new StringRequest(Request.Method.POST, AppURLs.URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jObj = new JSONObject(response);
                    String userId = jObj.getString("user_id");
                    if (!userId.equals("null")) {
                    } else {
                        String errorMsg = jObj.getString("error_msg");
                        Toast.makeText(getApplicationContext(),
                                errorMsg, Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                // Post params to login url
                Map<String, String> params = new HashMap<String, String>();
                params.put("tag", "NotficationsSettings");
                params.put("user", user);
                params.put("sendNotifications", sendNotifications);
                params.put("sendNotificationsOfCurrentDay", sendNotificationsOfCurrentDay);
                params.put("sendNotificationsOfAllJoined", sendNotificationsOfAllJoined);
                return params;
            }
        };

        // Adding request to  queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }
}
