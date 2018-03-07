package ru.kodep.vlad.potok;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import ru.kodep.vlad.potok.Database.DBHelper;
import ru.kodep.vlad.potok.Network.Preferences;

/**
 * Created by vlad on 05.03.18
 */

public class DataCleaning {

    public DataCleaning(Context context) {
        DBHelper mDBHelper = new DBHelper(context);
        SQLiteDatabase db = mDBHelper.getWritableDatabase();
        long rowID = db.delete(DBHelper.NAMETABLE, null, null);
        new Preferences(context).cleaningData(context);
    }
}
