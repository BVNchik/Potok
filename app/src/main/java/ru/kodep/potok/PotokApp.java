package ru.kodep.potok;

import android.app.Application;


import ru.kodep.potok.database.DBHelper;
import ru.kodep.potok.database.UsersStorage;
import ru.kodep.potok.network.NetworkService;
import ru.kodep.potok.network.Preferences;
import ru.kodep.potok.service.JobDispatcher;
import ru.kodep.potok.repository.DataRepository;

/**
 * Created by vlad on 26.02.18
 */

public class PotokApp extends Application {
    private DataRepository mDataRepository;

    @Override
    public void onCreate() {
        super.onCreate();
        mDataRepository = new DataRepository(new UsersStorage(), new NetworkService(), new JobDispatcher(this),  new DBHelper(this), new ReminderOfValidity(), new Preferences(this), this);
    }
    public DataRepository getDataRepository() {
        return mDataRepository;
    }
}
