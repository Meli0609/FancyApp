package com.example.android.fancyapp;

import android.app.Application;
import android.content.SharedPreferences;
import android.os.LocaleList;

/**
 * Created by melisa-pc on 04.05.2018.
 */

public class App extends Application {

    SharedPreferences profile;

    @Override
    public void onCreate() {
        super.onCreate();

        profile = getApplicationContext().getSharedPreferences("ProfilePref", 0);
        SharedPreferences.Editor editor = profile.edit();

        editor.putString("0", "English");
        editor.putString("1", "Deutsch");
        editor.putString("2", "Bosanski");
    }
}
