package ru.kodep.potok.repository;

import android.annotation.SuppressLint;
import android.content.Context;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
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

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by vlad on 26.02.18
 */

public class DataRepository {
    private static final String NAME_FAIL = "LogFile.txt";
    private static final String DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ssZZZZZ";
    private final JobDispatcher mJobDispatcher;
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

    private Single<List<User>> loadUsers(String token, String lastRequest, int page) {
        return mNetworkService.loadUsers(token, lastRequest, page);
    }

    public Single<Integer> fetchAllUsers() {
        final String lastRequest = getLastRequest();
        final String token = mPreferences.getToken();
        return Observable.range(1, 10000)

                .flatMap(new Func1<Integer, Observable<List<User>>>() {
                    @Override
                    public Observable<List<User>> call(Integer page) {
                        return loadUsers(token,lastRequest,page).toObservable();
                    }
                })
                .takeUntil(new Func1<List<User>, Boolean>() {
                    @Override
                    public Boolean call(List<User> users) {
                        return users.size() < 1000;
                    }
                })
                .flatMap(new Func1<List<User>, Observable<User>>() {
                    @Override
                    public Observable<User> call(List<User> users) {
                        return Observable.from(users);
                    }
                }).map(new Func1<User, Integer>() {
                    @Override
                    public Integer call(User user) {
                        mUsersStorage.saveUser(mContext, user);
                        return 1;
                    }
                }).reduce(0, new Func2<Integer, Integer, Integer>() {
                    @Override
                    public Integer call(Integer l, Integer r) {
                        return l + r;
                    }
                }).toSingle();
    }

    public void writeFile(String text) {
        String str = readFile() + text;
        writesFile(str);
    }

    public void writesFile(String text) {

        BufferedWriter bw = null;
        try {
            // отрываем поток для записи
            bw = new BufferedWriter(new OutputStreamWriter(
                    mContext.openFileOutput(NAME_FAIL, MODE_PRIVATE)));
            // пишем данные
            bw.write(text);
            // закрываем поток
            bw.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                bw.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void numberOfContacts(int numberOfContacts) {
        @SuppressLint("SimpleDateFormat") String data = new SimpleDateFormat("dd MMMM yyyy HH:mm:ss").format(new Date(Calendar.getInstance().getTimeInMillis()));
        writeFile("Загрузка данных прошла успешно (" + data + ")");
        writeFile("Количество добавленных контактов: " + numberOfContacts + " (" + data + ")");
    }

    public Single<Boolean> authorization(String login, String password) {
        return mNetworkService.authorization(login, password)
                .map(new Func1<AuthorizationModel, Boolean>() {
                    @Override
                    public Boolean call(AuthorizationModel customerDataResponse) {
                        String token = customerDataResponse.getToken();
                        String validTo = customerDataResponse.getValidTo();
                        Date dateValidTo;
                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(DATE_FORMAT, java.util.Locale.getDefault());
                        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("RFC"));
                        try {
                            dateValidTo = simpleDateFormat.parse(validTo);
                        } catch (ParseException e) {
                            dateValidTo = new Date();
                        }
                        getmPreferences().setToken(token);
                        getmPreferences().setValidTo(dateValidTo.getTime());
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

    public UsersStorage getmUsersStorage() {
        return mUsersStorage;
    }


    public String readFile() {
        StringBuilder str = new StringBuilder();
        BufferedReader br = null;
        try {
            // открываем поток для чтения
            br = new BufferedReader(new InputStreamReader(mContext.openFileInput(NAME_FAIL)));
            // читаем содержимое

            String string = "";
            while ((string = br.readLine()) != null) {
                str.append(string + "\n");
            }
            return str.toString();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if(br == null) {
                    writesFile("ЛОГИ:");
                }else {
                    br.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return str.toString();
    }
}
