package ru.kodep.vlad.potok;

import android.app.Application;

import ru.kodep.vlad.potok.Database.DBHelper;
import ru.kodep.vlad.potok.Database.UsersStorage;
import ru.kodep.vlad.potok.Network.NetworkService;
import ru.kodep.vlad.potok.Service.JobDispatcher;
import ru.kodep.vlad.potok.repository.DataRepository;

/**
 * Created by vlad on 26.02.18
 */

public class PotokApp extends Application {
    private DataRepository mDataRepository;

    @Override
    public void onCreate() {
        super.onCreate();
        mDataRepository = new DataRepository(new UsersStorage(), new NetworkService(), new JobDispatcher(this),  new DBHelper(this),this);

    }

    public DataRepository getDataRepository() {
        return mDataRepository;
    }
}
