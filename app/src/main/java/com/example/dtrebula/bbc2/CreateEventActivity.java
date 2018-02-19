package com.example.dtrebula.bbc2;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Calendar;
import java.util.concurrent.TimeUnit;

/**
 * Created by dtrebula on 23. 3. 2017.
 */



public class CreateEventActivity extends AppCompatActivity {
    private TextView eventChooseImage, setDefaultImage;
    private EditText eventName_text, eventDescription_text, eventNumberOfPeopleAllowed_text, eventAddress_text;
    private Button eventShowMap, eventSave, eventBack;
    private final String TAG = "NajdiLokalitu";
    private ProgressDialog pDialog;
    private Session session;
    private ImageView imageOfEvent;
    private static int IMG_RESULT = 1;
    private static String obrazok = "";
    String user = LoginActivity.getVariableId();
    Integer userId = Integer.parseInt(user);
    Button btnDatePicker, btnTimePicker;
    EditText txtDate, txtTime;
    private static long millies = 0;
    private boolean dateCheckAvailable =false;

    private int mYear, mMonth, mDay, mHour, mMinute;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_event);
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);
        session = new Session(CreateEventActivity.this);
        eventName_text = (EditText) findViewById(R.id.eventName);
        eventDescription_text = (EditText) findViewById(R.id.eventDescription);
        eventNumberOfPeopleAllowed_text = (EditText) findViewById(R.id.eventNumberOfPeople);
        eventAddress_text = (EditText) findViewById(R.id.eventAddress);
        eventChooseImage = (TextView) findViewById(R.id.eventImage_click);
        eventSave = (Button) findViewById(R.id.eventSave);
        eventBack = (Button) findViewById(R.id.eventBack);
        eventShowMap = (Button) findViewById(R.id.eventShowMap);
        imageOfEvent = (ImageView) findViewById(R.id.imageOfEvent);
        btnDatePicker = (Button) findViewById(R.id.btn_date);
        btnTimePicker = (Button) findViewById(R.id.btn_time);
        txtDate = (EditText) findViewById(R.id.in_date);
        txtTime = (EditText) findViewById(R.id.in_time);
        setDefaultImage = (TextView) findViewById(R.id.defaultImage_button);


        btnDatePicker.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get Current Date
                getThatDate();
            }

        });
        btnTimePicker.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get Current Time
                getThatTime();

            }
        });
        eventShowMap.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    String address = eventAddress_text.getText().toString();
                    address = address.replace(' ', '+');
                    Intent geoIntent = new Intent(Intent.ACTION_VIEW,
                            Uri.parse("geo:0,0?q=" + address));
                    startActivity(geoIntent);
                } catch (Exception e) {
                    Log.e(TAG, e.toString());
                }
            }
        });
        eventBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CreateEventActivity.this, EventsActivity.class);
                startActivity(intent);
                finish();
            }
        });
        setDefaultImage.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                imageOfEvent.setImageResource(R.drawable.defaultsport);
                Bitmap icon = BitmapFactory.decodeResource(getResources(), R.drawable.defaultsport);
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                icon.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                byte[] image = stream.toByteArray();
                String profilePicture = Base64.encodeToString(image,Base64.DEFAULT);
                savePicture(profilePicture);
            }
        });
        eventSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String eventName = eventName_text.getText().toString();
                String eventDescription = eventDescription_text.getText().toString();
                String eventNumberOfPeopleAllowed = eventNumberOfPeopleAllowed_text.getText().toString();
                String eventAddress = eventAddress_text.getText().toString();
                String eventPicture = getPicture();
                String eventDate = txtDate.getText().toString();
                String eventTime = txtTime.getText().toString();
                if (!eventName.isEmpty() && !eventDescription.isEmpty() && !eventNumberOfPeopleAllowed.isEmpty()
                        && !eventAddress.isEmpty() && !eventPicture.isEmpty() && !eventDate.isEmpty() && !eventTime.isEmpty()
                        && dateCheckAvailable) {
                    saveEvent(eventName, eventDescription, eventNumberOfPeopleAllowed,
                            eventAddress, eventPicture, eventDate, eventTime);
                    Toast.makeText(CreateEventActivity.this, "Event created !", Toast.LENGTH_SHORT).show();
                } else {
                    Snackbar.make(v, "Please enter the credentials or pick right date!", Snackbar.LENGTH_LONG).show();
                }
            }
        });
        eventChooseImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getEventImage();
            }
        });


    }
    private void getThatDate(){
        final Calendar c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);
        DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            Calendar j = Calendar.getInstance();
                j.set(Calendar.YEAR,year);
                j.set(Calendar.MONTH,monthOfYear);
                j.set(Calendar.DAY_OF_MONTH,dayOfMonth);
                Date d = j.getTime();
                txtDate.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);
                millies = getDateDiff(new Date(),d, TimeUnit.MINUTES);
                if(millies > -100){
                    dateCheckAvailable = true;
                }}}, mYear, mMonth, mDay);
        datePickerDialog.show();
    }
    private void getThatTime(){
        final Calendar c = Calendar.getInstance();
        mHour = c.get(Calendar.HOUR_OF_DAY);
        mMinute = c.get(Calendar.MINUTE);
        TimePickerDialog timePickerDialog = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay,
                                          int minute) {
                        txtTime.setText(hourOfDay + ":" + minute);}}, mHour, mMinute, false);
        timePickerDialog.show();
    }

    public static long getDateDiff(Date date1, Date date2, TimeUnit timeUnit){
        long diffInMillies = date2.getTime() - date1.getTime();
        millies = diffInMillies;
        return diffInMillies;
    }
    public static long getTimeVariable(){
        return millies;
    }

    private void getEventImage(){
        Intent intent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, IMG_RESULT);
    }
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Uri selectedImage = data.getData();
        try {
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(),selectedImage);
            ImageView my_img_view = (ImageView) findViewById (R.id.imageOfEvent);
            my_img_view.setImageBitmap(bitmap);
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
            byte[] image = stream.toByteArray();
            String eventPicture = Base64.encodeToString(image,Base64.DEFAULT);
            savePicture(eventPicture);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static String savePicture(final String picture){
        obrazok = picture;
        return obrazok;
    }
    public static String getPicture(){
        return obrazok;
    }
    private void saveEvent(final String eventName, final String eventDescription, final String eventNumberOfPeopleAllowed,
                                 final String eventAddress, final String eventPicture, final String eventDate, final String eventTime) {
        // Tag used to cancel the request
        final String email = LoginActivity.getVariable();
        String tag_string_req = "req_register";
        pDialog.setMessage("Saving Data ...");
        showDialog();
        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppURLs.URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jObj = new JSONObject(response);
                    countEventsId();
                    hideDialog();
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
                // Posting params to register url
                Map<String, String> params = new HashMap<String, String>();
                params.put("tag", "save_eventInformation");
                params.put("email", email);
                params.put("eventName", eventName);
                params.put("eventDescription", eventDescription);
                params.put("eventNumberOfPeopleAllowed", eventNumberOfPeopleAllowed);
                params.put("eventAddress", eventAddress);
                params.put("eventPicture", eventPicture);
                params.put("eventDate", eventDate);
                params.put("eventTime", eventTime);
                return params;
            }
        };
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }
    private void countEventsId() {
        // Tag used to cancel the request
        final String email = LoginActivity.getVariable();
        String tag_string_req = "req_register";
        pDialog.setMessage("Saving Data ...");
        showDialog();
        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppURLs.URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jObj = new JSONObject(response);
                    String event_ids = jObj.getString("event_id");
                    addEventToCurrentEventTable(event_ids);
                    hideDialog();
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
                // Posting params to register url
                Map<String, String> params = new HashMap<String, String>();
                params.put("tag", "getTotalId");
                params.put("email", email);
                return params;
            }
        };
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }
    private void addEventToCurrentEventTable(final String event_ids) {
        // Tag used to cancel the request
        final String email = LoginActivity.getVariable();
        String tag_string_req = "req_register";
        pDialog.setMessage("Saving Data ...");
        showDialog();
        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppURLs.URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(),
                        error.getMessage(), Toast.LENGTH_LONG).show();
                hideDialog();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                // Posting params to register url
                Map<String, String> params = new HashMap<String, String>();
                params.put("tag", "addCurrentUserToEvent");
                params.put("eventId", event_ids);
                params.put("userId", userId.toString());
                return params;
            }
        };
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }
    @Override
    protected void onStart(){
        super.onStart();
        Log.i(TAG, "Ativita spustena.");
    }
    @Override
    protected void onRestart(){
        super.onStart();
        Log.i(TAG, "Ativita restartovana.");
    }
    @Override
    protected void onResume(){
        super.onStart();
        Log.i(TAG, "Ativita ma fokus v stave resume.");
    }
    @Override
    protected void onPause(){
        super.onStart();
        Log.i(TAG, "Ativita Ina aktivita zistala fokus.");
    }
    @Override
    protected void onStop(){
        super.onStart();
        Log.i(TAG, "Ativita zastavena.");
    }
    @Override
    protected void onDestroy(){
        super.onStart();
        Log.i(TAG, "Ativita odstranena.");
    }

    private void showDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hideDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }

}