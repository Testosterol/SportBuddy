package com.example.dtrebula.bbc2;


import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by dtrebula on 30. 3. 2017.
 */

public class ShowEventActivity extends AppCompatActivity {
    /** Called when the activity is first created. */
    ListView list;

    private static Integer idEventu = 0;
    Integer[] idsOfEvent = new Integer[]{};
    List<Integer> idsOfEventArray = Arrays.asList(idsOfEvent);
    List<Integer> tempList3 = new ArrayList<Integer>(idsOfEventArray);
    Integer eventId = 0;
    String[] eventNames = new String[]{};
    List<String> listFromArray = Arrays.asList(eventNames);
    List<String> tempList = new ArrayList<String>(listFromArray);
    String eventName = "";
    Integer totalId = 0;
    Bitmap[] imageList = new Bitmap[]{};
    List<Bitmap> listFromString = Arrays.asList(imageList);
    List<Bitmap> tempList2 = new ArrayList<Bitmap>(listFromString);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_events);
        retreiveTotalId();
}
    private void retreiveEventAndAddToList(final Integer eventIdd) {
        String tag_string_req = "req_login";
        StringRequest strReq = new StringRequest(Request.Method.POST, AppURLs.URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jObj = new JSONObject(response);
                    String eventShowId = jObj.getString("event_id");
                    String eventName = jObj.getString("event_name");
                    String event_picture = jObj.getString("event_picture");
                    Integer eventShowId2 =Integer.parseInt(eventShowId);
                    tempList3.add(eventShowId2);
                    Integer[] tempArray3 = new Integer[tempList3.size()];
                    idsOfEvent = tempList3.toArray(tempArray3);
                    tempList.add(eventName);
                    String[] tempArray = new String[tempList.size()];
                    eventNames = tempList.toArray(tempArray);
                    byte[] decodedString = Base64.decode(event_picture, Base64.DEFAULT);
                    Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                    tempList2.add(decodedByte);
                    Bitmap[] tempArray2 = new Bitmap[tempList2.size()];
                    imageList = tempList2.toArray(tempArray2);
                    if(imageList.length == (totalId)) {
                        ShowAllEventActivity adapter = new ShowAllEventActivity(ShowEventActivity.this, eventNames, imageList, idsOfEvent);
                        list=(ListView)findViewById(R.id.list);
                        list.setAdapter(adapter);
                        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                Toast.makeText(ShowEventActivity.this, "You Clicked at " +eventNames[+ position] +
                                        idsOfEvent[+ position], Toast.LENGTH_SHORT).show();
                                getEventId(idsOfEvent[+ position]);
                                Intent intent = new Intent(ShowEventActivity.this, ShowCurrentEventActivity.class);
                                startActivity(intent);
                                finish();
                            }
                        });
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
                params.put("tag", "retreiveEventNameAndPicture");
                params.put("eventId", eventIdd.toString());
                return params;
            }
        };
        // Adding request to  queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }
    public static Integer getEventId(final Integer idOfEvent){
        idEventu = idOfEvent;
        return idEventu;
    }
    public static Integer returnEventId(){
        return idEventu;
    }
    private void retreiveTotalId() {
        final String userEmail = LoginActivity.getVariable();
        String tag_string_req = "req_login";
        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppURLs.URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jObj = new JSONObject(response);
                    String totalIds = jObj.getString("event_id");
                    totalId = Integer.parseInt(totalIds);
                    for(eventId = 1; eventId <= totalId; eventId++){
                        retreiveEventAndAddToList(eventId);
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
                params.put("tag", "getTotalId");
                params.put("email", userEmail);
                return params;
            }
        };
        // Adding request to  queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }
}

