package ru.kodep.vlad.potok;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import ru.kodep.vlad.potok.database.DBHelper;
import ru.kodep.vlad.potok.repository.DataRepository;

/**
 * Created by vlad on 05.03.18
 */

public class DataCleaning {

    public DataCleaning(Context context) {

        PotokApp app = (PotokApp) context.getApplicationContext();
        DataRepository mRepository = app.getDataRepository();
        SQLiteDatabase db = mRepository.getmDBHelper().getWritableDatabase();
        long rowID = db.delete(DBHelper.NAMETABLE, null, null);
        mRepository.getmPreferences().cleaningData(context);
    }
}
