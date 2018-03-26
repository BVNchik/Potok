package ru.kodep.potok.network;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.TelephonyManager;
import android.util.Log;

import ru.kodep.potok.database.UsersStorage;
import ru.kodep.potok.models.User;
import ru.kodep.potok.ui.activity.CallScreenActivity;
import ru.kodep.potok.utils.Logger;
import rx.Single;
import rx.SingleSubscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;


/**
 * Created by vlad on 21.02.18
 */

public class PhoneStateChangedReceiver extends BroadcastReceiver {
    private UsersStorage mUsersStorage;

    public void onReceive(Context context, Intent intent) {
        Log.i(getClass().getName(), "SRABOTAL");
        if (intent.getAction().equals("android.intent.action.PHONE_STATE")) {
            String phoneState = intent.getStringExtra(TelephonyManager.EXTRA_STATE);
            if (phoneState.equals(TelephonyManager.EXTRA_STATE_RINGING)) {
                String phoneNumber = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER);
                showWindow(context, phoneNumber, intent);
            }
        }
    }

    private void showWindow(final Context context, final String phone, final Intent intent) {
        mUsersStorage = new UsersStorage();
        Single.create(new Single.OnSubscribe<User>() {
            @Override
            public void call(SingleSubscriber<? super User> singleSubscriber) {
                try {
                    User user = mUsersStorage.seekUser(phone, context);
                    singleSubscriber.onSuccess(user);
                } catch (RuntimeException e) {
                    Logger.print(e);
                    singleSubscriber.onError(e);
                }
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<User>() {
                    @SuppressLint("WrongConstant")
                    public void call(User user) {
                        if (user.getName() != null) {
                            String phoneNumber = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER);
                            Intent i = new Intent(context, CallScreenActivity.class);
                            i.putExtras(intent);
                            i.putExtra("phone.number", phoneNumber);
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
