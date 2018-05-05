package com.example.android.fancyapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.android.fancyapp.network.AsyncResponse;
import com.example.android.fancyapp.network.HttpRequest;
import com.example.android.fancyapp.network.ResponseEvent;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

    private LoginButton txtFbLogin;
    private Button mLogin;
    private AccessToken mAccessToken;
    private CallbackManager callbackManager;
    private EditText mEmail, mPassword;
    String userToken = null;
    SharedPreferences profile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_main);

        txtFbLogin = (LoginButton) findViewById(R.id.login_button_fb);
        callbackManager = CallbackManager.Factory.create();
        mLogin = (Button) findViewById(R.id.login_button);
        mEmail = (EditText) findViewById(R.id.email);
        mPassword = (EditText) findViewById(R.id.password);
        profile = getApplicationContext().getSharedPreferences("ProfilePref", 0);

        txtFbLogin.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                mAccessToken = loginResult.getAccessToken();
                getUserProfileFb(mAccessToken);
            }
            @Override
            public void onCancel() {

            }
            @Override
            public void onError(FacebookException error) {

            }
        });

        mLogin.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v)
            {
                JSONObject loginData = new JSONObject();

                try {
                    loginData.put("email", mEmail.getText().toString());
                    loginData.put("password", mPassword.getText().toString());
                }catch (Exception e){
                    e.printStackTrace();
                }

                //User login
                new HttpRequest(new AsyncResponse(){
                    @Override
                    public void processFinish(String response){
                        if(response != null) {
                            getUserSelf(response);
                        }else {
                            displayErrorMsg("Login failed");
                        }

                    }
                }, getApplicationContext())
                        .setUrl("http://ec2-18-188-71-205.us-east-2.compute.amazonaws.com:6006" + "/api/v1/login")
                        .setMethod("POST")
                        .addHeader("Content-Type", "application/json")
                        .addRequestBody(loginData)
                        .execute();
            }
        });
    }

    public void displayErrorMsg(String errMessage)
    {
        Context context = getApplicationContext();
        int duration = Toast.LENGTH_SHORT;

        Toast toast = Toast.makeText(context, errMessage, duration);
        toast.show();
    }

    public void getUserSelf(String result)
    {
        try {
            if(result != null) {
                JSONObject response = new JSONObject(result);
                userToken = response.getString("authToken");

                //Send get user self request
                getUserSelfRequest();
            }
            else
            {
                displayErrorMsg("Get user self failed");
            }

        }catch (Exception e)
        {
            e.printStackTrace();
        }
    }


    private void getUserProfileFb(AccessToken currentAccessToken) {
        GraphRequest request = GraphRequest.newMeRequest(
                currentAccessToken,
                new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {

                        SharedPreferences.Editor editor = profile.edit();

                        try{
                            String name = object.getString("name");
                            String[] values = name.split(" ");

                            JSONObject picture = object.getJSONObject("picture");
                            JSONObject pictureData = picture.getJSONObject("data");

                            editor.putString("first_name", values[0]);
                            editor.putString("last_name", values[1]);
                            editor.putString("image_url", pictureData.getString("url"));
                        }catch (Exception e)
                        {
                            e.printStackTrace();
                        }

                        editor.commit();

                        Intent newIntent = new Intent(MainActivity.this, UserProfile.class);
                        //TODO Shared preferences
                        startActivity(newIntent);
                        finish();
                    }
                });
        Bundle parameters = new Bundle();
        parameters.putString("fields", "id,name,email,picture.width(200)");
        request.setParameters(parameters);
        request.executeAsync();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode,  data);
    }

    public void getUserSelfRequest()
    {
        new HttpRequest(new AsyncResponse(){

            @Override
            public void processFinish(String output){
                try {
                    SharedPreferences.Editor editor = profile.edit();

                    JSONObject userJson = null;
                    try{
                        userJson = new JSONObject(output);
                    }catch (Exception e)
                    {
                        e.printStackTrace();
                    }

                    editor.putString("first_name", userJson.getString("first_name"));
                    editor.putString("last_name", userJson.getString("last_name"));
                    editor.putString("image_url", userJson.getString("_imageUrl"));

                    editor.commit();

                    Intent newIntent = new Intent(MainActivity.this, UserProfile.class);
                    startActivity(newIntent);
                    finish();

                }catch (Exception e)
                {
                    e.printStackTrace();
                }

            }
        }, this).setUrl("http://ec2-18-188-71-205.us-east-2.compute.amazonaws.com:6006" + "/api/v1/self/user")
                .setMethod("GET")
                .addHeader("Content-Type", "application/json")
                .addHeader("Authorization", userToken)
                .execute();
    }


}
