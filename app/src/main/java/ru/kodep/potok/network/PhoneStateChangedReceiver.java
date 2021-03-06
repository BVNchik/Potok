package ru.kodep.potok.network;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.TelephonyManager;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import ru.kodep.potok.PotokApp;
import ru.kodep.potok.database.UsersStorage;
import ru.kodep.potok.exception.UserNotFoundException;
import ru.kodep.potok.models.User;
import ru.kodep.potok.repository.DataRepository;
import ru.kodep.potok.ui.activity.CallScreenActivity;
import ru.kodep.potok.utils.Logger;
import rx.Single;
import rx.SingleSubscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;


/**
 * Created by vlad on 21.02.18
 */

public class PhoneStateChangedReceiver extends BroadcastReceiver {
    public static final String INTENT_EXTRA_NUMBER_PHONE = "phone.number";
    private static final String INTENT_ACTION_PHONE_STATE = "android.intent.action.PHONE_STATE";
    int repeat = 0;
    private UsersStorage mUsersStorage;
    private DataRepository mRepository;


    public void onReceive(Context context, Intent intent) {
        PotokApp app = (PotokApp) context.getApplicationContext();
        mRepository = app.getDataRepository();
        if (intent.getAction().equals(INTENT_ACTION_PHONE_STATE)) {
            String phoneState = intent.getStringExtra(TelephonyManager.EXTRA_STATE);
            if (phoneState.equals(TelephonyManager.EXTRA_STATE_RINGING)) {
                String phoneNumber = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER);
                showWindow(context, phoneNumber, intent);
                @SuppressLint("SimpleDateFormat") String data = new SimpleDateFormat("dd MMMM yyyy HH:mm:ss").format(new Date(Calendar.getInstance().getTimeInMillis()));
                mRepository.writeFile("Звонит номер: " + phoneNumber + " (" + data + ")");
            }
        }
    }

    Single<User> findUser(final Context context, final String phone) {
        return Single.create(new Single.OnSubscribe<User>() {
            @Override
            public void call(SingleSubscriber<? super User> singleSubscriber) {
                try {
                    User user = mUsersStorage.seekUser(phone, context);
                    repeat++;
                    if (user.getName() != null) {
                        singleSubscriber.onSuccess(user);
                        mRepository.writeFile("Номер: " + phone + " определился как: " + user.getName());
                    } else {
                        if (repeat == 1) {
                            mRepository.writeFile("Номер: " + phone + " номера в базе нет: ");
                        } else {
                            mRepository.writeFile("Номер: " + phone + " контакта с таким номером на сервере нет: ");
                            repeat = 0;
                        }
                        singleSubscriber.onError(new UserNotFoundException());
                    }
                } catch (Exception e) {
                    Logger.print(e);
                    singleSubscriber.onError(e);
                }
            }
        });
    }

    void showWindow(final Context context, final String phone, final Intent intent) {
        PotokApp app = (PotokApp) context.getApplicationContext();
        mRepository = app.getDataRepository();
        mUsersStorage = mRepository.getmUsersStorage();
        findUser(context, phone)
                .onErrorResumeNext(new Func1<Throwable, Single<? extends User>>() {
                    @Override
                    public Single<? extends User> call(final Throwable throwable) {
                        if (throwable instanceof UserNotFoundException) {
                            return mRepository.fetchAllUsers()
                                    .flatMap(new Func1<Integer, Single<User>>() {
                                        @Override
                                        public Single<User> call(Integer updated) {
                                            mRepository.writeFile("Запрос на сервер для поиска номера");
                                            return (updated>0)
                                                    ? findUser(context, phone)
                                                    : Single.<User>error(throwable);
                                        }
                                    });
                        } else {
                            return Single.error(throwable);
                        }
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<User>() {
                    public void call(User user) {
                        if (user.getName() != null) {

                            try {
                                Thread.sleep(1000);
                            } catch (InterruptedException e) {
                                Logger.print(e);
                            }

                            String phoneNumber = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER);
                            Intent i = new Intent(context, CallScreenActivity.class);
                            i.putExtras(intent);
                            i.putExtra(INTENT_EXTRA_NUMBER_PHONE, phoneNumber);
                            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            context.startActivity(i);
                        }
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        Logger.print(throwable);
                    }
                });
    }
}
