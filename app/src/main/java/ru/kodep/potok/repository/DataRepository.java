package ru.kodep.potok.repository;

import android.content.Context;
import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import ru.kodep.potok.ReminderOfValidity;
import ru.kodep.potok.database.DBHelper;
import ru.kodep.potok.database.UsersStorage;
import ru.kodep.potok.models.AuthorizationModel;
import ru.kodep.potok.models.User;
import ru.kodep.potok.network.NetworkService;
import ru.kodep.potok.network.Preferences;
import ru.kodep.potok.service.JobDispatcher;
import rx.Observable;
import rx.Single;
import rx.functions.Func1;
import rx.functions.Func2;

/**
 * Created by vlad on 26.02.18
 */

public class DataRepository {
    private static final String DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ssZZZZZ";
    final JobDispatcher mJobDispatcher;
    private final UsersStorage mUsersStorage;
    private final NetworkService mNetworkService;
    private final Context mContext;
    private ReminderOfValidity mReminderOfValidity;
    private Preferences mPreferences;
    private DBHelper mDBHelper;

    public DataRepository(UsersStorage usersStorage, NetworkService networkService, JobDispatcher jobDispatcher, DBHelper dbHelper, ReminderOfValidity reminderOfValidity, Preferences preferences, Context context) {
        mUsersStorage = usersStorage;
        mNetworkService = networkService;
        mContext = context;
        mJobDispatcher = jobDispatcher;
        mReminderOfValidity = reminderOfValidity;
        mPreferences = preferences;
        mDBHelper = dbHelper;
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

    public Single<Boolean> authorization(String login, String password) {
        return mNetworkService.authorization(login, password)
                .map(new Func1<AuthorizationModel, Boolean>() {
                    @Override
                    public Boolean call(AuthorizationModel customerDataResponse) {
                        String token = customerDataResponse.getToken();
                        String validTo = customerDataResponse.getValidTo();
                        Date dateValidTo;
                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(DATE_FORMAT,java.util.Locale.getDefault() );
                        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("RFC"));
                        try {
                            dateValidTo = simpleDateFormat.parse(validTo);
                        } catch (ParseException e) {
                            dateValidTo = new Date();
                        }
                        getmPreferences().setToken(token);
                        getmPreferences().setValidTo(dateValidTo.getTime());
                        Log.i(getClass().getName(), String.valueOf(getmPreferences().getValidTo()));
                        return true;
                    }
                });
    }

    public String getLastRequest() {
        long time = mPreferences.getLastRequest();
     SimpleDateFormat simpleDateFormat = new SimpleDateFormat(DATE_FORMAT, java.util.Locale.getDefault());
        return simpleDateFormat.format(time);
    }

    public Preferences getmPreferences() {
        return mPreferences;
    }

    public DBHelper getmDBHelper() {
        return mDBHelper;
    }

    public ReminderOfValidity getmReminderOfValidity() {
        return mReminderOfValidity;
    }
<<<<<<< HEAD

    public UsersStorage getmUsersStorage() {
        return mUsersStorage;
    }
=======
    public UsersStorage getmUsersStorage() {return  mUsersStorage; }
>>>>>>> master
}
