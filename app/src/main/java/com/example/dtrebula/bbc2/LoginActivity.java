package com.example.dtrebula.bbc2;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.design.widget.Snackbar;

import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {

    private Button registrationButton, loginButton;
    private EditText email_to_login, password_to_login;
    private ProgressDialog progressDialog;
    private Session session;
    private static String id = "", eventsNum = "", karol = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        session = new Session(LoginActivity.this);
        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        registrationButton = (Button) findViewById(R.id.registration_button);
        loginButton = (Button) findViewById(R.id.signin_button);
        email_to_login = (EditText) findViewById(R.id.email_to_login);
        password_to_login = (EditText) findViewById(R.id.password_to_login);



        registrationButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),
                        RegistrationActivity.class);
                startActivity(intent);
                finish();
            }
        });

        loginButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = email_to_login.getText().toString();
                String password = password_to_login.getText().toString();
                if (email.trim().length() > 0 && password.trim().length() > 0) {
                    checkLogin(email, password);
                    saveEmail(email);
                } else {
                    Snackbar.make(v, "Please enter the credentials!", Snackbar.LENGTH_LONG).show();
                }
            }
        });
    }
    public static String saveEmail(final String email){
        karol = email;
        return karol;
    }
    public static String getVariable(){
        return karol;
    }
    private void retreiveNotificationSettings() {
        String tag_string_req = "req_login";
        StringRequest strReq = new StringRequest(Request.Method.POST, AppURLs.URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                hideDialog();
                try {
                    JSONObject jObj = new JSONObject(response);
                    String sendNot = jObj.getString("user_sendNot");
                    String sendNotOfAllJoined = jObj.getString("user_setNotOfAllJoined");
                    String sendNotOfCurrentDay = jObj.getString("user_sendNotOfCurrentDay");
                    if(!sendNot.isEmpty() && !sendNotOfCurrentDay.isEmpty() && !sendNotOfAllJoined.isEmpty()) {
                        if (sendNot.equals("true") && sendNotOfAllJoined.equals("true") && sendNotOfCurrentDay.equals("false")) {
                            CheckNumberOfEventsForNotification();
                        }
                        if (sendNot.equals("true") && sendNotOfCurrentDay.equals("true") && sendNotOfAllJoined.equals("false")) {
                            Date dateActual = new Date();
                            Format formattter = new SimpleDateFormat("d-M-yyyy", Locale.ENGLISH);
                            String actualDate = formattter.format(dateActual);
                            CheckNumberOfEventsForNotificationToday(actualDate);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show();
                hideDialog();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                // Post params to login url
                Map<String, String> params = new HashMap<String, String>();
                params.put("tag", "retreiveNotificationSettings");
                params.put("userId", id);
                return params;
            }
        };

        // Adding request to  queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }
    private void CheckNumberOfEventsForNotificationToday(final String actualDate) {
        String tag_string_req = "req_login";

        StringRequest strReq = new StringRequest(Request.Method.POST, AppURLs.URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jObj = new JSONObject(response);
                    String event_idNum = jObj.getString("event_idd");
                    System.out.println(event_idNum);
                    saveEventsNum(event_idNum);
                    int eventNumber = Integer.parseInt(event_idNum);
                    if(eventNumber > 0){
                        NotificationEventReceiver.setupAlarm(getApplicationContext());
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
                params.put("tag", "CheckNumberOfEventsForUserToday");
                params.put("userId", id);
                params.put("actualDate", actualDate);
                return params;
            }
        };

        // Adding request to  queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }
    private void checkLogin(final String email, final String password) {
        String tag_string_req = "req_login";
        progressDialog.setMessage("Logging in ...");
        StringRequest strReq = new StringRequest(Request.Method.POST, AppURLs.URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jObj = new JSONObject(response);
                    String userId = jObj.getString("user_id");
                    if (!userId.equals("null")) {
                        saveId(userId);
                        session.setLogin(true);
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                        retreiveNotificationSettings();
                    } else {
                        Toast.makeText(getApplicationContext(), "error", Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }}}, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show();}
        }) {
            @Override
            protected Map<String, String> getParams() {
                // Post params to login url
                Map<String, String> params = new HashMap<String, String>();
                params.put("tag", "login");
                params.put("email", email);
                params.put("password", password);
                return params;
            }
        };
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }



    // NOTIFICATION FOR ALL EVENTS
    private void CheckNumberOfEventsForNotification() {
        String tag_string_req = "req_login";

        StringRequest strReq = new StringRequest(Request.Method.POST, AppURLs.URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jObj = new JSONObject(response);
                    String event_idNum = jObj.getString("event_id");
                    saveEventsNum(event_idNum);
                    int eventNumber = Integer.parseInt(event_idNum);
                    if(eventNumber > 0){
                        NotificationEventReceiver.setupAlarm(getApplicationContext());
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
                params.put("tag", "CheckNumberOfEventsForUser");
                params.put("userId", id);
                return params;
            }
        };

        // Adding request to  queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }
    public static String saveEventsNum(final String email){
        eventsNum = email;
        return eventsNum;
    }
    public static String getEventsNum(){
        return eventsNum;
    }
    public static String saveId(final String user_id){
        id = user_id;
        return id;
    }
    public static String getVariableId(){
        return id;
    }
    private void showDialog() {
        if (!progressDialog.isShowing())
            progressDialog.show();
    }

    private void hideDialog() {
        if (progressDialog.isShowing())
            progressDialog.dismiss();
    }

}
