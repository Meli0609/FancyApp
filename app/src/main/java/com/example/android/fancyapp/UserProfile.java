package com.example.android.fancyapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import org.json.JSONObject;

import de.hdodenhof.circleimageview.CircleImageView;
import com.squareup.picasso.Picasso;

/**
 * Created by melisa-pc on 04.05.2018.
 */

public class UserProfile extends AppCompatActivity{

    private JSONObject userJson = null;
    CircleImageView mProfileImage;
    SharedPreferences profile;
    TextView mFirstName, mLastName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_profile);

        mProfileImage = (CircleImageView) findViewById(R.id.profile_picture);
        profile = getApplicationContext().getSharedPreferences("ProfilePref", 0);
        mFirstName = (TextView) findViewById(R.id.first_name);
        mLastName = (TextView) findViewById(R.id.last_name);

        Toolbar toolbar=(Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        setUserProfile();
    }

    private void setUserProfile()
    {
        Picasso.get().load(profile.getString("image_url", null)).into(mProfileImage);
        mFirstName.setText(profile.getString("first_name", null));
        mLastName.setText(profile.getString("last_name", null));
    }

    @Override
    public void onBackPressed() {
        Intent setIntent = new Intent(this, MainActivity.class);
        startActivity(setIntent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.toolbar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_english) {
            LocaleManager.setLocale(UserProfile.this, "en");

            //It is required to recreate the activity to reflect the change in UI.
            recreate();
        }

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_deutsch) {
            LocaleManager.setLocale(UserProfile.this, "de");

            //It is required to recreate the activity to reflect the change in UI.
            recreate();
        }

        return super.onOptionsItemSelected(item);
    }
}
