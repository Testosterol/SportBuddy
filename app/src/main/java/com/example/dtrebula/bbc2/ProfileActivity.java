package com.example.dtrebula.bbc2;

import android.graphics.Bitmap;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.app.ProgressDialog;
import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import org.json.JSONException;
import org.json.JSONObject;
import android.graphics.BitmapFactory;
import android.provider.MediaStore;
import android.widget.ImageView;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by dtrebula on 8. 11. 2016.
 */

public class ProfileActivity extends AppCompatActivity {

    private EditText fullname, age_toSave, weight_toSave, height_toSave, sports_toSave;
    private Button saveButton, backButton ;
    private TextView imageButton, setDefaultImage;
    private Session session;
    private ProgressDialog pDialog;
    ImageView imageViewLoad;
    private static int IMG_RESULT = 1;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);
        session = new Session(ProfileActivity.this);
        fullname = (EditText) findViewById(R.id.fullname_profile);
        age_toSave = (EditText) findViewById(R.id.age_profile);
        weight_toSave = (EditText) findViewById(R.id.weight_profile);
        height_toSave = (EditText) findViewById(R.id.height_profile);
        sports_toSave = (EditText) findViewById(R.id.sports_profile);
        saveButton = (Button) findViewById(R.id.save_button);
        backButton = (Button) findViewById(R.id.back_button);
        imageButton = (TextView) findViewById(R.id.image_button);
        setDefaultImage = (TextView) findViewById(R.id.defaultImage_button);
        imageViewLoad = (ImageView) findViewById(R.id.imageView1);

        retreiveUserInformation();

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = fullname.getText().toString();
                String age = age_toSave.getText().toString();
                String weight = weight_toSave.getText().toString();
                String height = height_toSave.getText().toString();
                String sports = sports_toSave.getText().toString();
                if (!name.isEmpty() && !age.isEmpty() && !weight.isEmpty() && !height.isEmpty() &&
                        !sports.isEmpty()) {
                    saveUserProfile(name, age, weight, height, sports);
                    Toast.makeText(ProfileActivity.this, "Profile saved!", Toast.LENGTH_SHORT).show();
                } else {
                    Snackbar.make(v, "Please enter the credentials!", Snackbar.LENGTH_LONG).show();
                }
            }
        });
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProfileActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
        setDefaultImage.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                imageViewLoad.setImageResource(R.drawable.defaultprofile);
                Bitmap icon = BitmapFactory.decodeResource(getResources(), R.drawable.defaultprofile);
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                icon.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                byte[] image = stream.toByteArray();
                String profilePicture = Base64.encodeToString(image,Base64.DEFAULT);
                saveProfilePicture(profilePicture);
            }
        });
        imageButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                getUserImage();
            }
        });
    }
    private void getUserImage(){
        Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, IMG_RESULT);
    }
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
     super.onActivityResult(requestCode, resultCode, data);
         Uri selectedImage = data.getData();
         try {
             Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(),selectedImage);
             ImageView my_img_view = (ImageView) findViewById (R.id.imageView1);
             my_img_view.setImageBitmap(bitmap);
             ByteArrayOutputStream stream = new ByteArrayOutputStream();
             bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
             byte[] image = stream.toByteArray();
             String profilePicture = Base64.encodeToString(image,Base64.DEFAULT);
             saveProfilePicture(profilePicture);
         } catch (IOException e) {
             e.printStackTrace();
         }
     }
    private void saveProfilePicture(final String profilePicture){
        // Tag used to cancel the request
        final String email = LoginActivity.getVariable();
        String tag_string_req = "req_register";
        pDialog.setMessage("Saving Data ...");
        showDialog();
        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppURLs.URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                hideDialog();
                try {
                    JSONObject jObj = new JSONObject(response);
                    String userId = jObj.getString("user_id");
                    String userProfilePicture = jObj.getString("user_profilePicture");
                    System.out.println(userProfilePicture);
                    if (!userId.equals("null")) {
                        session.setLogin(true);
                        if(!userProfilePicture.equals("")) {
                            byte[] decodedString = Base64.decode(userProfilePicture, Base64.DEFAULT);
                            Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                            ImageView my_img_view = (ImageView) findViewById (R.id.imageView1);
                            my_img_view.setImageBitmap(decodedByte);
                        }
                    } else {
                        String errorMsg = jObj.getString("error_msg");
                        Toast.makeText(getApplicationContext(), errorMsg, Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
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
                params.put("tag", "save_ProfilePicture");
                params.put("email", email);
                params.put("profilePicture", profilePicture);
                return params;
            }
        };
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
        retreiveUserInformationPicture();
    }

    private void retreiveUserInformation() {
        final String userEmail = LoginActivity.getVariable();
        String tag_string_req = "req_login";
        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppURLs.URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                hideDialog();
                try {
                    JSONObject jObj = new JSONObject(response);
                    String userId = jObj.getString("user_id");
                    String userName = jObj.getString("user_name");
                    String userAge = jObj.getString("user_age");
                    String userWeight = jObj.getString("user_weight");
                    String userHeight = jObj.getString("user_height");
                    String userSports = jObj.getString("user_sports");
                    userName = "Name: " + userName;
                    userAge = "Age: " + userAge;
                    userHeight = "Height: " + userHeight;
                    userWeight = "Weight: " + userWeight;
                    String userProfilePicture = jObj.getString("user_profilePicture");
                    if (!userId.equals("null")) {
                        session.setLogin(true);
                        fullname.setText(userName);
                        age_toSave.setText(userAge);
                        weight_toSave.setText(userWeight);
                        height_toSave.setText(userHeight);
                        sports_toSave.setText(userSports);
                        if(!userProfilePicture.equals("")) {
                            byte[] decodedString = Base64.decode(userProfilePicture, Base64.DEFAULT);
                            Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                            ImageView my_img_view = (ImageView) findViewById (R.id.imageView1);
                            my_img_view.setImageBitmap(decodedByte);
                        }
                    } else {
                        String errorMsg = jObj.getString("error_msg");
                        Toast.makeText(getApplicationContext(), errorMsg, Toast.LENGTH_LONG).show();
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
                params.put("tag", "getUserInformation");
                  params.put("email", userEmail);
                return params;
            }
        };
        // Adding request to  queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

    private void saveUserProfile(final String name, final String age, final String weight,
                                 final String height, final String sports) {
        // Tag used to cancel the request
        final String email = LoginActivity.getVariable();
        String tag_string_req = "req_register";
        pDialog.setMessage("Saving Data ...");
        showDialog();
        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppURLs.URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                hideDialog();
                try {
                    JSONObject jObj = new JSONObject(response);
                    String userId = jObj.getString("user_id");
                    String userName = jObj.getString("user_name");
                    String userAge = jObj.getString("user_age");
                    String userWeight = jObj.getString("user_weight");
                    String userHeight = jObj.getString("user_height");
                    String userSports = jObj.getString("user_sports");
                    if (!userId.equals("null")) {
                        session.setLogin(true);
                        fullname.setText(userName);
                        age_toSave.setText(userAge);
                        weight_toSave.setText(userWeight);
                        height_toSave.setText(userHeight);
                        sports_toSave.setText(userSports);
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
                Toast.makeText(getApplicationContext(),
                        error.getMessage(), Toast.LENGTH_LONG).show();
                hideDialog();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                // Posting params to register url
                Map<String, String> params = new HashMap<String, String>();
                params.put("tag", "save_Profile");
                params.put("email", email);
                params.put("name", name);
                params.put("age", age);
                params.put("weight", weight);
                params.put("height", height);
                params.put("sports", sports);
                return params;
            }
        };
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }
    private void retreiveUserInformationPicture() {
        final String userEmail = LoginActivity.getVariable();
        String tag_string_req = "req_login";
        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppURLs.URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                hideDialog();
                try {
                    JSONObject jObj = new JSONObject(response);
                    String userId = jObj.getString("user_id");
                    String userProfilePicture = jObj.getString("user_profilePicture");
                    if (!userId.equals("null")) {
                        if(!userProfilePicture.equals("")) {
                            byte[] decodedString = Base64.decode(userProfilePicture, Base64.DEFAULT);
                            Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                            ImageView my_img_view = (ImageView) findViewById (R.id.imageView1);
                            my_img_view.setImageBitmap(decodedByte);
                        }
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
                Toast.makeText(getApplicationContext(),
                        error.getMessage(), Toast.LENGTH_LONG).show();
                hideDialog();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                // Post params to login url
                Map<String, String> params = new HashMap<String, String>();
                params.put("tag", "getUserInformation");
                params.put("email", userEmail);
                return params;
            }
        };
        // Adding request to  queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
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
