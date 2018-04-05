package ru.kodep.potok.network;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by vlad on 22.02.18
 */

public class Preferences {
    private static final String NAME = "Launch";
    private static final String KEY_TOKEN = "Token";
    private static final String KEY_VALID_TO = "ValidTo";
    private static final String KEY_FIRST_START = "FirstStart";
    private static final String KEY_LAST_REQUEST = "LastRequest";
    private SharedPreferences sharedPreferences;


    public Preferences(Context activity) {
        sharedPreferences = activity.getSharedPreferences(NAME, Activity.MODE_PRIVATE);
    }


    public void cleaningData(Context context) {
        SharedPreferences.Editor editor = context.getSharedPreferences(NAME, Context.MODE_PRIVATE).edit();
        editor.clear();
        editor.apply();
    }

    public long getLastRequest() {
        return sharedPreferences.getLong(KEY_LAST_REQUEST, 0);
    }

    public void setLastRequest(Long lastRequest) {
        sharedPreferences.edit().putLong(KEY_LAST_REQUEST, lastRequest).apply();
    }

    String getToken() {
        return sharedPreferences.getString(KEY_TOKEN, "");
    }

    public void setToken(String token) {
        sharedPreferences.edit().putString(KEY_TOKEN, token).apply();
    }

    public long getValidTo() {
        return sharedPreferences.getLong(KEY_VALID_TO, 0);
    }

    public void setValidTo(long validTo) {
        sharedPreferences.edit().putLong(KEY_VALID_TO, validTo).apply();
    }

    public void setFirstStart() {
        sharedPreferences.edit().putBoolean(KEY_FIRST_START, false).apply();
    }

    public boolean getFirstStart() {
        return sharedPreferences.getBoolean(KEY_FIRST_START, true);
    }


}