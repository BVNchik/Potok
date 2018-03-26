package ru.kodep.potok.database;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import ru.kodep.potok.PotokApp;
import ru.kodep.potok.models.User;
import ru.kodep.potok.repository.DataRepository;

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
        Cursor cursor = db.query(DBHelper.NAME_TABLE, null, selection, selectionArgs, null, null, null);
        if (cursor.moveToFirst()) {
            int userPhoneColIndex = cursor.getColumnIndex(DBHelper.PHONES);
            userPhone = cursor.getString(userPhoneColIndex);
        }
        if (userPhone == null) {
            cv = putCV(user);
            db.insert(DBHelper.NAME_TABLE, null, cv);
        } else {
            cv = putCV(user);
            db.update(DBHelper.NAME_TABLE, cv, selection, selectionArgs);
        }
        cursor.close();
        PotokApp app = (PotokApp) context.getApplicationContext();
        DataRepository mRepository = app.getDataRepository();
        mRepository.getmPreferences().setLastRequest(Calendar.getInstance().getTimeInMillis());

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
        String userName = null;
        String title = null;
        String avatarUrl = null;
        String applicationUrl = null;
        String createdAt = null;
        String updatedAt = null;
        Integer id = null;
        String selection = DBHelper.PHONES + "= ?";
        String phone = phoneUser.substring(1);
        String[] selectionArgs = new String[]{phone};
        Cursor cursor = db.query(DBHelper.NAME_TABLE, null, selection, selectionArgs, null, null, null);
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
        cursor.close();
        return new User(id, userName, title, phones, avatarUrl, applicationUrl, createdAt, updatedAt);
    }

}
