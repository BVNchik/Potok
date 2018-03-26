package ru.kodep.potok.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


/**
 * Created by vlad on 26.02.18
 */

public class DBHelper extends SQLiteOpenHelper {
   private static final String TEXT = " text,";
   static final String NAME = "name";
   static final String ID = "ids";
   static final String TITLE = "title";
   static final String PHONES = "phones";
   static final String AVATAR = "avatar";
   static final String APPLICATION_URL = "application_url";
   static final String CREATED_AT = "created_at";
   static final String UPDATED_AT = "updated_at";
 public   static final String NAME_TABLE = "PersonTable";


    public DBHelper(Context context) {
        super(context, NAME_TABLE, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table PersonTable (" +
                "id integer primary key autoincrement," +
                NAME + TEXT +
                ID + TEXT +
                TITLE + TEXT +
                PHONES + TEXT +
                AVATAR + TEXT +
                APPLICATION_URL + TEXT +
                CREATED_AT + TEXT +
                UPDATED_AT + " text"+ ");");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
       //
    }
}
