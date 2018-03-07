package ru.kodep.vlad.testingreadnumberphone.Database;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import ru.kodep.vlad.testingreadnumberphone.models.User;

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
        Log.i("Phone", "phone: " + phone);
        String[] selectionArgs = new String[]{phone};
        @SuppressLint("Recycle") Cursor cursor = db.query(DBHelper.NAMETABLE, null, selection, selectionArgs, null, null, null);
        if (cursor.moveToFirst()) {
            int userPhoneColIndex = cursor.getColumnIndex(DBHelper.PHONES);
            userPhone = cursor.getString(userPhoneColIndex);
        }
        Log.i("Phone", "userPhone: " + userPhone);
        if (userPhone == null) {
            cv = putCV(user);
            long rowID = db.insert(DBHelper.NAMETABLE, null, cv);
            Log.i("DataRepository", "insert: " + rowID);
        } else {
            cv = putCV(user);
            long rowID = db.update(DBHelper.NAMETABLE, cv, selection, selectionArgs);
            Log.i("DataRepository", "update: " + rowID);
        }
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
        Log.i("Phones", phoneUser);
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
            Log.i("Phones", phone);
            phones.add(phone);
            Log.i("Phones", phones.get(0));
        }
        return new User(id, userName, title, phones, avatarUrl, applicationUrl, createdAt, updatedAt);
    }

}
