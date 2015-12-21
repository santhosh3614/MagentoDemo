package com.fourbuy.android;

import android.app.Application;

/**
 * Created by 3164 on 09-12-2015.
 */
public class FourBuyApp extends Application {


    private String sessionId;

    @Override
    public void onCreate() {
        super.onCreate();
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

}
