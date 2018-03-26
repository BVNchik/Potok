package ru.kodep.potok;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import ru.kodep.potok.database.DBHelper;
import ru.kodep.potok.repository.DataRepository;

/**
 * Created by vlad on 05.03.18
 */

public class DataCleaning {
    public DataCleaning(Context context) {
        PotokApp app = (PotokApp) context.getApplicationContext();
        DataRepository mRepository = app.getDataRepository();
        SQLiteDatabase db = mRepository.getmDBHelper().getWritableDatabase();
        db.delete(DBHelper.NAME_TABLE, null, null);
        mRepository.getmPreferences().cleaningData(context);
    }
}
