package ru.kodep.vlad.potok.Database;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import ru.kodep.vlad.potok.Network.Preferences;
import ru.kodep.vlad.potok.models.User;

/**
 * Created by vlad on 26.02.18
 */

public class UsersStorage {
    private DBHelper mDBHelper;
    private ContentValues cv;
    private SQLiteDatabase db;

    public void saveUser(Context context, User user) {
        mDBHelper = new DBHelper(context);
        cv = new ContentValues();
        db = mDBHelper.getWritableDatabase();
        String userPhone = null;
        String selection = DBHelper.PHONES + "= ?";
        List<String> userPhones = user.getPhones();
        String phone = userPhones.get(0);
        String[] selectionArgs = new String[]{phone};
        @SuppressLint("Recycle") Cursor cursor = db.query(DBHelper.NAMETABLE, null, selection, selectionArgs, null, null, null);
        if (cursor.moveToFirst()) {
            int userPhoneColIndex = cursor.getColumnIndex(DBHelper.PHONES);
            userPhone = cursor.getString(userPhoneColIndex);
        }
        if (userPhone == null) {
            cv = putCV(user);
          db.insert(DBHelper.NAMETABLE, null, cv);
        } else {
            cv = putCV(user);
          db.update(DBHelper.NAMETABLE, cv, selection, selectionArgs);
        }
        @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZZZZZ");
        String lastRequest = dateFormat.format(new Date());
        new Preferences(context).setLastRequest(lastRequest);
    }

    private ContentValues putCV(User user) {
        cv.put(DBHelper.NAME, user.getName());
        cv.put(DBHelper.ID, user.getId());
        cv.put(DBHelper.TITLE, user.getTitle());
        cv.put(DBHelper.PHONES, user.getPhones().get(0));
        cv.put(DBHelper.AVATAR, user.getAvatar());
        cv.put(DBHelper.APPLICATION_URL, user.getApplicantUrl());
        cv.put(DBHelper.CREATED_AT, user.getCreatedAt());
        cv.put(DBHelper.UPDATED_AT, user.getUpdatedAt());
        return cv;
    }

    public User seekUser(String phoneUser, Context context) {
        List<String> phones = new ArrayList<>();
        mDBHelper = new DBHelper(context);
        db = mDBHelper.getWritableDatabase();
        String userName = null, title = null, avatarUrl = null, applicationUrl = null, createdAt = null, updatedAt = null;
        Integer id = null;
        String selection = DBHelper.PHONES + "= ?";
        String phone = phoneUser.substring(1);
        String[] selectionArgs = new String[]{phone};
        @SuppressLint("Recycle") Cursor cursor = db.query(DBHelper.NAMETABLE, null, selection, selectionArgs, null, null, null);
        if (cursor.moveToFirst()) {
            int idColIndex = cursor.getColumnIndex(DBHelper.ID);
            int userNameColIndex = cursor.getColumnIndex(DBHelper.NAME);
            int titleColIndex = cursor.getColumnIndex(DBHelper.TITLE);
            int avatarColIndex = cursor.getColumnIndex(DBHelper.AVATAR);
            int applicationUrlColIndex = cursor.getColumnIndex(DBHelper.APPLICATION_URL);
            int createdAtColIndex = cursor.getColumnIndex(DBHelper.CREATED_AT);
            int updatedAtColIndex = cursor.getColumnIndex(DBHelper.UPDATED_AT);
            id = cursor.getInt(idColIndex);
            userName = cursor.getString(userNameColIndex);
            title = cursor.getString(titleColIndex);
            avatarUrl = cursor.getString(avatarColIndex);
            applicationUrl = cursor.getString(applicationUrlColIndex);
            createdAt = cursor.getString(createdAtColIndex);
            updatedAt = cursor.getString(updatedAtColIndex);
            phones.add(phone);
        }
        return new User(id, userName, title, phones, avatarUrl, applicationUrl, createdAt, updatedAt);
    }

}
