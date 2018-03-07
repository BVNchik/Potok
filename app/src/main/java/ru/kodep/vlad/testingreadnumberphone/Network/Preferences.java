package ru.kodep.vlad.testingreadnumberphone.Network;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by vlad on 22.02.18
 */

public class Preferences {
    //Вспомогательный класс для хранения выбранного города
    private SharedPreferences sharedPreferences;
private static final String NAME = "Launch";
    @SuppressLint("CommitPrefEdits")
    public Preferences(Context activity) {
        sharedPreferences = activity.getSharedPreferences(NAME, Activity.MODE_PRIVATE);
    }

    @SuppressLint("CommitPrefEdits")
    public void cleaningData(Context context) {
        SharedPreferences.Editor editor = context.getSharedPreferences(NAME, Context.MODE_PRIVATE).edit(); editor.clear(); editor.apply();
    }

    // Возвращаем город по умолчанию, если SharedPreferences пустые
    String getToken() {
        return sharedPreferences.getString("Token", "");
    }

    void setToken(String token) {
        sharedPreferences.edit().putString("Token", token).apply();
    }

    public String getValidTo() {
        return sharedPreferences.getString("ValidTo", "");
    }

    void setValidTo(String validTo) {
        sharedPreferences.edit().putString("ValidTo", validTo).apply();
    }

    public String getLaunch() {
        return sharedPreferences.getString("Launched", "");
    }

    public void setLaunch() {
        sharedPreferences.edit().putString("Launched", "true").apply();
    }

}