package ru.kodep.vlad.potok.network;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by vlad on 22.02.18
 */

public class Preferences {
    private static final String NAME = "Launch";
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
        return sharedPreferences.getLong("LastRequest", 0);
    }

    public void setLastRequest(Long lastRequest) {
        sharedPreferences.edit().putLong("LastRequest", lastRequest).apply();
    }

    String getToken() {
        return sharedPreferences.getString("Token", "");
    }

    void setToken(String token) {
        sharedPreferences.edit().putString("Token", token).apply();
    }

    public long getValidTo() {
        return sharedPreferences.getLong("ValidTo", 0);
    }

    void setValidTo(long validTo) {
        sharedPreferences.edit().putLong("ValidTo", validTo).apply();
    }
    public void  setFirstStart() {sharedPreferences.edit().putBoolean("FirstStart", false).apply();}
    public  boolean getFirstStart() { return sharedPreferences.getBoolean("FirstStart", true); }


}