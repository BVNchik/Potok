package ru.kodep.vlad.potok.service;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;

import ru.kodep.vlad.potok.database.UsersStorage;
import ru.kodep.vlad.potok.models.User;
import ru.kodep.vlad.potok.ui.activity.CallScreenActivity;
import rx.Single;
import rx.SingleSubscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * Created by vlad on 15.03.18
 */

@SuppressLint("Registered")
public class CallReceiverService extends Service {
    BroadcastReceiver callReceiver = new CallReceiver();
    private UsersStorage mUsersStorage;

    @Nullable
    public IBinder onBind(Intent intent) {
        return null;
    }

    @SuppressLint("InflateParams")
    private void showWindow(final Context context, final String phone, final Intent intent) {
        mUsersStorage = new UsersStorage();
        Single.create(new Single.OnSubscribe<User>() {
            @Override
            public void call(SingleSubscriber<? super User> singleSubscriber) {

                try {
                    User user = mUsersStorage.seekUser(phone, context);
                    singleSubscriber.onSuccess(user);
                } catch (Throwable e) {
                    singleSubscriber.onError(e);
                }
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<User>() {
                    @SuppressLint("WrongConstant")
                    @Override
                    public void call(User user) {
                        if (user.getName() != null) {
                            String phoneNumber = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER);
                            Intent i = new Intent(context, CallScreenActivity.class);
                            i.putExtras(intent);
                            i.putExtra("phone.number", phoneNumber);
                            i.addFlags(335544320);
                            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                            try {
                                Thread.sleep(1000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            context.startActivity(i);
                        }
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        throwable.printStackTrace();
                    }
                });


    }

    public void onCreate() {
        super.onCreate();
        registerReceiver(this.callReceiver, new IntentFilter("android.intent.action.PHONE_STATE"));
        Log.i("tags", "SERVICE CREATED");
    }

    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(this.callReceiver);
    }

    class CallReceiver extends BroadcastReceiver {
        Context pcontext;
        Intent mIntent;
        CallReceiver() {
        }

        public void onReceive(Context context, Intent intent) {
            pcontext = context;
mIntent = intent;
            try {
                Log.i(getClass().getName(), "СРАБОТАЛ РЕСИВЕР ИЗ СЕРВИСА");
                TelephonyManager tmgr = (TelephonyManager) context
                        .getSystemService(Context.TELEPHONY_SERVICE);

                MyPhoneStateListener PhoneListener = new MyPhoneStateListener();

                assert tmgr != null;
                tmgr.listen(PhoneListener, PhoneStateListener.LISTEN_CALL_STATE);

            } catch (Exception e) {
                Log.e("Phone Receive Error", " " + e);
            }
        }
        private class MyPhoneStateListener extends PhoneStateListener {
            public void onCallStateChanged(int state, String incomingNumber) {
                switch (state) {
                    case TelephonyManager.CALL_STATE_IDLE:
                        break;
                    case TelephonyManager.CALL_STATE_OFFHOOK:

                        break;
                    case TelephonyManager.CALL_STATE_RINGING:
                        Log.i(getClass().getName(), "ИДЕТ ЗВОНОК");
                        showWindow(pcontext, incomingNumber, mIntent);
                        break;
                }
            }
        }
    }
}

