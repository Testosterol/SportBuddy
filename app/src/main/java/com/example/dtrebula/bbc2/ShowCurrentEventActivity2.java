package com.example.dtrebula.bbc2;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import java.text.ParseException;
import java.util.*;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.HashMap;
import java.util.Map;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.concurrent.TimeUnit;


/**
 * Created by dtrebula on 3. 4. 2017.
 */

public class ShowCurrentEventActivity2 extends AppCompatActivity {
    private Button joinEventButton, backToEventsButton, currentEventShowMap, logOffButton;
    private ProgressDialog progressDialog;
    private Session session;
    private TextView event_Name, event_Description, event_NumberOfPeople, event_PeopleJoined, event_Address,event_date, event_time;
    private ImageView event_Picture;
    private final String TAG = "NajdiLokalitu";
    private Integer currentPeopleJoined = 0, numberOfPeopleAllowed = 0, peopleJoined = 0, eventId = 0;
    private boolean checkJoinAvailable = false, dateCheckAvailable = false;
    private String dateConversion = "", eventNumberOfPeople = "", eventPeopleJoined = "", idOfUser = "";
    private static long millies = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_current_event);
        event_Picture = (ImageView) findViewById(R.id.event_Image);
        session = new Session(ShowCurrentEventActivity2.this);
        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        backToEventsButton = (Button) findViewById(R.id.backToEvents_button);
        joinEventButton = (Button) findViewById(R.id.joinEvent_button);
        currentEventShowMap = (Button) findViewById(R.id.eventShowMap);
        eventId = ShowMyEventsActivity.returnEventIdd();
        event_Name = (TextView) findViewById(R.id.event_name);
        event_Description = (TextView) findViewById(R.id.event_description);
        event_NumberOfPeople = (TextView) findViewById(R.id.event_numberOfPeople);
        event_PeopleJoined = (TextView) findViewById(R.id.event_peopleJoined);
        event_Address = (TextView) findViewById(R.id.event_address);
        idOfUser = LoginActivity.getVariableId();
        event_date = (TextView) findViewById(R.id.event_date);
        event_time = (TextView) findViewById(R.id.event_time);
        logOffButton = (Button) findViewById(R.id.logOff_button);
        retreiveCurrentEventInformation();
        countPeopleJoined();
        //ADD BUTTON FOR REMOVING USER FROM EVENT - IF HE CLICKS ON REMOVE FROM THE EVENT DELETE HIM FROM TABLE
        // and send him to main ? or to events.. trivialita..
        backToEventsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ShowCurrentEventActivity2.this, EventsActivity.class);
                startActivity(intent);
                finish();
            }
        });
        logOffButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              logOff();
                Toast.makeText(ShowCurrentEventActivity2.this, "You have been removed from the event!", Toast.LENGTH_SHORT).show();
            }
        });
        currentEventShowMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    String address = event_Address.getText().toString();
                    address = address.replace(' ', '+');
                    Intent geoIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("geo:0,0?q=" + address));
                    startActivity(geoIntent);
                } catch (Exception e){
                    Log.e(TAG, e.toString());
                }
            }
        });
        joinEventButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkJoinAvailable && dateCheckAvailable){
                    //check dates, if the event is older than actual date, dont allow user to join
                    addUserToEvent();
                    countPeopleJoined();
                    addPeopleJoined();
                    retreiveCurrentEventInformation();
                    Toast.makeText(ShowCurrentEventActivity2.this, "You Joined Event", Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(ShowCurrentEventActivity2.this, "Event uz je plny, je nam luto/ alebo " +
                            "ste nan uz prihlaseny/event sa uz konal", Toast.LENGTH_LONG).show();
                }
            }
        });
    }
    public void logOff(){
        String tag_string_req = "req_login";
        StringRequest strReq = new StringRequest(Request.Method.POST, AppURLs.URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jObj = new JSONObject(response);
                    eventPeopleJoined = jObj.getString("countPeopleJoined");
                    event_PeopleJoined.setText(eventPeopleJoined);
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
                params.put("tag", "removeUserFromEvent");
                params.put("eventId", eventId.toString());
                params.put("userId", idOfUser);
                return params;
            }
        };
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }
    public void retreiveCurrentEventInformation(){
        String tag_string_req = "req_login";
        StringRequest strReq = new StringRequest(Request.Method.POST, AppURLs.URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jObj = new JSONObject(response);
                    String eventName = jObj.getString("event_name");
                    String eventDescription = jObj.getString("event_description");
                    eventNumberOfPeople = jObj.getString("event_numberOfPeople");
                    String eventNumberOfPeopleCurrent = eventNumberOfPeople;
                    String eventAddress = jObj.getString("event_address");
                    String eventPicture = jObj.getString("event_picture");
                    eventPeopleJoined = jObj.getString("event_peopleJoined");
                    String eventPeopleJoinedCurrent = eventPeopleJoined;
                    eventNumberOfPeopleCurrent = "Pocet povolenych ludi: " + eventNumberOfPeopleCurrent;
                    eventPeopleJoinedCurrent = "Prihlasenych ludi: " + eventPeopleJoinedCurrent;
                    String eventdate = jObj.getString("event_date");
                    dateConversion = eventdate;
                    String eventTime = jObj.getString("event_time");
                    eventName = "Nazov eventu: " + eventName;
                    eventDescription = "Popis eventu: " + eventDescription;
                    eventAddress = "Adresa: " + eventAddress;
                    eventdate = "Datum eventu: " + eventdate;
                    eventTime = "Cas eventu: " + eventTime;
                    session.setLogin(true);
                    event_Name.setText(eventName);
                    event_Description.setText(eventDescription);
                    event_NumberOfPeople.setText(eventNumberOfPeopleCurrent);
                    event_Address.setText(eventAddress);
                    event_PeopleJoined.setText(eventPeopleJoinedCurrent);
                    event_date.setText(eventdate);
                    event_time.setText(eventTime);
                    if(!eventPicture.equals("")) {
                        byte[] decodedString = Base64.decode(eventPicture, Base64.DEFAULT);
                        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                        ImageView event_Picture = (ImageView) findViewById (R.id.event_Image);
                        event_Picture.setImageBitmap(decodedByte);

                    }
                    calculateDate();
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
                params.put("tag", "getCurrentEventInformation");
                params.put("eventId", eventId.toString());
                return params;
            }
        };
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }
    public void calculateDate(){
        String dateFromDatabase = dateConversion;
        Date dateActual = new Date();
        DateFormat format = new SimpleDateFormat("d-M-yyyy", Locale.ENGLISH);
        Date parsed = new Date();
        try {
            parsed = format.parse(dateFromDatabase);
            System.out.println(parsed);
        }
        catch(ParseException pe) {
            System.out.println("ERROR: Cannot parse \"" + dateFromDatabase + "\"");
        }
        millies = getDateDiff(dateActual,parsed,TimeUnit.MINUTES);
        if(millies > 0){
            dateCheckAvailable = true;
        }
    }
    public static long getDateDiff(Date date1, Date date2, TimeUnit timeUnit){
        long diffInMillies = date2.getTime() - date1.getTime();
        millies = diffInMillies;
        return diffInMillies;
    }
    public void countPeopleJoined(){
        String tag_string_req = "req_login";
        StringRequest strReq = new StringRequest(Request.Method.POST, AppURLs.URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jObj = new JSONObject(response);
                    String count = jObj.getString("countPeopleJoined");
                    if (count.equals("null") || count.equals("")) {
                        checkJoinAvailable = true;
                    }else{
                        numberOfPeopleAllowed = Integer.parseInt(eventNumberOfPeople);
                        peopleJoined = Integer.parseInt(eventPeopleJoined);
                        currentPeopleJoined = Integer.parseInt(count);
                        if (currentPeopleJoined < numberOfPeopleAllowed) {
                            checkJoinAvailable = true;
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
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                // Post params to login url
                Map<String, String> params = new HashMap<String, String>();
                params.put("tag", "countPeopleJoined");
                params.put("eventId", eventId.toString());
                return params;
            }
        };
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }
    public void addUserToEvent(){
        String tag_string_req = "req_login";
        StringRequest strReq = new StringRequest(Request.Method.POST, AppURLs.URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(),
                        error.getMessage(), Toast.LENGTH_LONG).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                // Post params to login url
                Map<String, String> params = new HashMap<String, String>();
                params.put("tag", "addCurrentUserToEvent");
                params.put("eventId", eventId.toString());
                params.put("userId", idOfUser);
                return params;
            }
        };
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }
    public void addPeopleJoined(){
        String tag_string_req = "req_login";
        StringRequest strReq = new StringRequest(Request.Method.POST, AppURLs.URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
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
                params.put("tag", "addPeopleJoined");
                params.put("eventId", eventId.toString());
                params.put("peopleJoined", currentPeopleJoined.toString());
                return params;
            }
        };
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }
}
