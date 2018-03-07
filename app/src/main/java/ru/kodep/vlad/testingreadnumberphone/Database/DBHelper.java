package ru.kodep.vlad.testingreadnumberphone.Database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;


/**
 * Created by vlad on 26.02.18
 */

public class DBHelper extends SQLiteOpenHelper {
   static final String NAME = "name";
   static final String ID = "ids";
   static final String TITLE = "title";
   static final String PHONES = "phones";
   static final String AVATAR = "avatar";
   static final String APPLICATION_URL = "application_url";
   static final String CREATED_AT = "created_at";
   static final String UPDATED_AT = "updated_at";
 public   static final String NAMETABLE = "PersonTable";


    public DBHelper(Context context) {
        super(context, NAMETABLE, null, 1);
        Log.i("DBHelper", String.valueOf(context));
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table PersonTable (" +
                "id integer primary key autoincrement," +
                NAME + " text," +
                ID + " text," +
                TITLE + " text," +
                PHONES + " text," +
                AVATAR + " text," +
                APPLICATION_URL + " text,"+
                CREATED_AT + " text,"+
                UPDATED_AT + " text"+ ");");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }
}
