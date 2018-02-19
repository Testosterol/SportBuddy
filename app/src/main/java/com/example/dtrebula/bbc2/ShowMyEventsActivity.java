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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by dtrebula on 6. 4. 2017.
 */

public class ShowMyEventsActivity extends AppCompatActivity {
    ListView list;
    String idOfUser = LoginActivity.getVariableId();
    private static Integer idEventu = 0;
    Integer[] idckaEventov = new Integer[]{};
    List<Integer> idckaEventovPole = Arrays.asList(idckaEventov);
    List<Integer> docasnyList3 = new ArrayList<Integer>(idckaEventovPole);
    Integer eventId = 0;
    ArrayList<String> eventNamesArr = new ArrayList<String>();
    ArrayList<Integer> eventIdsArr = new ArrayList<Integer>();
    Integer totalId = 0;
    String[] finalEeventNames = new String[]{};
    Integer[] finalEventIds = new Integer[]{};
    Bitmap[] listObrazkov = new Bitmap[]{};
    List<Bitmap> listZoStringov = Arrays.asList(listObrazkov);
    List<Bitmap> docasnyListObrazkov = new ArrayList<Bitmap>(listZoStringov);

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_events);
        retreiveMyTotalId();
    }
    private void retreiveCurrentEventAddToList(final Integer eventId) {
        String tag_string_req = "req_login";
        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppURLs.URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jObj = new JSONObject(response);
                    String eventShowId = jObj.getString("event_id");
                    String eventName = jObj.getString("event_name");
                    String event_picture = jObj.getString("event_picture");
                    Integer eventShowId2 =Integer.parseInt(eventShowId);
                    eventNamesArr.add(eventName);
                    eventIdsArr.add(eventShowId2);
                    byte[] decodedString = Base64.decode(event_picture, Base64.DEFAULT);
                    Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                    docasnyListObrazkov.add(decodedByte);
                    Bitmap[] docasnePoleObazkovMy = new Bitmap[docasnyListObrazkov.size()];
                    listObrazkov = docasnyListObrazkov.toArray(docasnePoleObazkovMy);
                    if(listObrazkov.length == (totalId)) {
                        finalEeventNames = new String[eventNamesArr.size()];
                        finalEventIds = new Integer[eventIdsArr.size()];
                        for(int i=0; i<listObrazkov.length;i++) {
                            finalEeventNames[i] = eventNamesArr.get(i);
                            finalEventIds[i] = eventIdsArr.get(i);
                        }
                        ShowAllEventActivity2 adapter = new ShowAllEventActivity2(ShowMyEventsActivity.this, finalEeventNames, listObrazkov, finalEventIds);
                        list=(ListView)findViewById(R.id.list);
                        list.setAdapter(adapter);
                        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                Toast.makeText(ShowMyEventsActivity.this, "You Clicked at " + finalEeventNames[+ position] +
                                        finalEventIds[+ position], Toast.LENGTH_SHORT).show();
                                getEventIdd(finalEventIds[+ position]);
                                Intent intent = new Intent(ShowMyEventsActivity.this, ShowCurrentEventActivity2.class);
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
                params.put("eventId", eventId.toString());
                return params;
            }
        };
        // Adding request to  queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }
    public static Integer getEventIdd(final Integer izballah){
        idEventu = izballah;
        return idEventu;
    }
    public static Integer returnEventIdd(){
        return idEventu;
    }
    private void retreiveEventsIdOneByOne(final Integer eventNum) {
        String tag_string_req = "req_login";
        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppURLs.URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jObj = new JSONObject(response);
                    String eventShowId = jObj.getString("event_id");
                    Integer eventShowId2 =Integer.parseInt(eventShowId);
                    docasnyList3.add(eventShowId2);
                    Integer[] tempArray3 = new Integer[docasnyList3.size()];
                    idckaEventov = docasnyList3.toArray(tempArray3);
                    if(idckaEventov.length == (totalId)) {
                        for(int j = 0; j <idckaEventov.length; j++){
                            retreiveCurrentEventAddToList(idckaEventov[j]);
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
                params.put("tag", "retreiveEventsForCurrentUser");
                params.put("idOfUser", idOfUser);
                params.put("eventNum", eventNum.toString());
                return params;
            }
        };
        // Adding request to  queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }
    private void retreiveMyTotalId() {
        String tag_string_req = "req_login";
        StringRequest strReq = new StringRequest(Request.Method.POST, AppURLs.URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jObj = new JSONObject(response);
                    String totalIds = jObj.getString("event_id");
                    totalId = Integer.parseInt(totalIds);
                    for(eventId = 0; eventId<totalId; eventId++) {
                        retreiveEventsIdOneByOne(eventId);
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
                params.put("tag", "getTotalIdOfUser");
                params.put("idOfUser", idOfUser);
                return params;
            }
        };
        // Adding request to  queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }
}
