package ru.kodep.vlad.potok.repository;

import android.content.Context;

import java.util.List;

import ru.kodep.vlad.potok.Database.DBHelper;
import ru.kodep.vlad.potok.Database.UsersStorage;
import ru.kodep.vlad.potok.Network.NetworkService;
import ru.kodep.vlad.potok.Network.Preferences;
import ru.kodep.vlad.potok.Service.JobDispatcher;
import ru.kodep.vlad.potok.models.User;
import rx.Observable;
import rx.Single;

import rx.functions.Func1;
import rx.functions.Func2;

/**
 * Created by vlad on 26.02.18
 */

public class DataRepository {

    private final UsersStorage mUsersStorage;
    private final NetworkService mNetworkService;
    private final Context mContext;
    private final JobDispatcher mJobDispatcher;

    public DataRepository(UsersStorage usersStorage, NetworkService networkService, JobDispatcher jobDispatcher, DBHelper dbHelper, Context context) {
        mUsersStorage = usersStorage;
        mNetworkService = networkService;
        mContext = context;
        mJobDispatcher = jobDispatcher;

    }

    public Single<Boolean> loadUsers() {
        return mNetworkService.loadUsers(mContext)
                .toObservable()
                .flatMap(new Func1<List<User>, Observable<User>>() {
                    @Override
                    public Observable<User> call(List<User> users) {
                        return Observable.from(users);
                    }
                }).map(new Func1<User, Boolean>() {
                    @Override
                    public Boolean call(User user) {
                        mUsersStorage.saveUser(mContext, user);
                        return true;
                    }
                }).reduce(true, new Func2<Boolean, Boolean, Boolean>() {
                    @Override
                    public Boolean call(Boolean l, Boolean r) {
                        return l && r;
                    }
                }).toSingle();
    }

    public String getLastRequest(){
      return new Preferences(mContext).getLastRequest();
    }
}
