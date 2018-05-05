package com.example.android.fancyapp.network;

/**
 * Created by melisa-pc on 04.05.2018.
 */

public class ResponseEvent {
    private final String response;

    public ResponseEvent(String message) {
        this.response = message;
    }

    public String getResponse() {
        return response;
    }
}
