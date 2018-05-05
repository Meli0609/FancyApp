package com.example.android.fancyapp.network;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.widget.Toast;

import com.example.android.fancyapp.MainActivity;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by melisa-pc on 04.05.2018.
 */

public class HttpRequest extends AsyncTask<Void, Void, String> {

    URL url;
    HttpURLConnection urlConnection;
    boolean unauthorizedUser = false;

    JSONObject postData = null;
    Context context;
    public AsyncResponse delegate = null;

    public HttpRequest(AsyncResponse delegate, Context context){
        this.delegate = delegate;
        this.context = context.getApplicationContext();
    }

    public void StatusCodeHandle(int statusCode) {

        if(401 == statusCode)
        {
            unauthorizedUser = true;
        }
    }

    public HttpRequest setMethod(String method) {
        try {
            urlConnection.setRequestMethod(method);
        }catch (Exception e) {
            e.printStackTrace();
        }

        return this;
    }

    public HttpRequest setUrl(String apiUrl) {
        try {
            url = new URL(apiUrl);
            urlConnection = (HttpURLConnection) url.openConnection();
        }catch (Exception e){
            e.printStackTrace();
        }

        return this;
    }

    public HttpRequest addHeader(String key, String value) {
        urlConnection.setRequestProperty(key, value);

        return this;
    }

    // This is a constructor that allows you to pass in the JSON body
    public HttpRequest addRequestBody(JSONObject body) {
        this.postData = body;

        return this;
    }

    @Override
    protected String doInBackground(Void... params){
        String inputLine;
        String response;

        try {
            urlConnection.setReadTimeout(15000);
            urlConnection.setConnectTimeout(15000);

            if (this.postData != null) {

                OutputStreamWriter writer = new OutputStreamWriter(urlConnection.getOutputStream());
                writer.write(postData.toString());
                writer.flush();
            }

            int statusCode = urlConnection.getResponseCode();
            StatusCodeHandle(statusCode);
            if (statusCode ==  200) {
                InputStreamReader streamReader = new InputStreamReader(urlConnection.getInputStream());

                BufferedReader reader = new BufferedReader(streamReader);
                StringBuilder stringBuilder = new StringBuilder();
                //Check if the line we are reading is not null
                while((inputLine = reader.readLine()) != null){
                    stringBuilder.append(inputLine);
                }

                //Close our InputStream and Buffered reader
                reader.close();
                streamReader.close();
                //Set our result equal to our stringBuilder

                response = stringBuilder.toString();

            } else {

                response = null;
            }

        } catch (Exception e) {
            e.printStackTrace();
            response = null;
        }

        return response;
    }

    @Override
    protected void onPostExecute(String result){
        if(unauthorizedUser)
        {
            Toast.makeText(context.getApplicationContext(), "Unauthorized user", Toast.LENGTH_SHORT).show();
            Intent login = new Intent(context, MainActivity.class);
            login.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(login);

        }
        else {
            delegate.processFinish(result);
        }
    }
}
