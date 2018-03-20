package ru.kodep.vlad.potok.ui.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.telephony.TelephonyManager;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.io.IOException;
import java.lang.reflect.Method;

import ru.kodep.vlad.potok.database.UsersStorage;
import ru.kodep.vlad.potok.R;
import ru.kodep.vlad.potok.models.User;
import rx.Single;
import rx.SingleSubscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

import static android.view.WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD;
import static android.view.WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED;

/**
 * Created by vlad on 13.03.18
 */

@SuppressLint("Registered")
public class CallScreenActivity extends Activity implements View.OnClickListener {
    Button btnAnswerCall, btnRejectCall;
    private UsersStorage mUsersStorage;
    BroadcastReceiver receiver = new BReceiver();
    class BReceiver extends BroadcastReceiver {
        BReceiver() {
        }

        @SuppressLint("WrongConstant")
        public void onReceive(Context context, Intent intent) {
            if (((TelephonyManager) context.getSystemService("phone")).getCallState() != 1) {
                CallScreenActivity.this.finish();
            }
        }
    }
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        registerReceiver(this.receiver, new IntentFilter("android.intent.action.PHONE_STATE"));
        setContentView(R.layout.call_screen_activity);
        Window window = getWindow();
        window.addFlags(FLAG_DISMISS_KEYGUARD);
        window.addFlags(FLAG_SHOW_WHEN_LOCKED);
        window.addFlags(2097152);
        btnAnswerCall = findViewById(R.id.btnAccept);
        btnAnswerCall.setOnClickListener(this);
        btnRejectCall = findViewById(R.id.btnRejectCall);
        btnRejectCall.setOnClickListener(this);
        showWindow(this, getIntent().getStringExtra("phone.number"));
    }


    private void showWindow(final Context context, final String phone) {
        final String[] numberInTheDatabase = new String[1];
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
                    @Override
                    public void call(User user) {
                        if (user.getName() != null) {
                            TextView tvNumber = findViewById(R.id.tvNumberPhone);
                            TextView tvUserName = findViewById(R.id.tvUserName);
                            TextView tvTitle = findViewById(R.id.tvTitles);
                            ImageView ivAvatar = findViewById(R.id.ivAvatar);
                            tvNumber.setText(phone);
                            numberInTheDatabase[0] = user.getName();
                            tvUserName.setText(user.getName());
                            tvTitle.setText(user.getTitle());
                            Glide.with(context).load(user.getAvatar()).apply(RequestOptions.placeholderOf(R.drawable.face_icon)).apply(RequestOptions.circleCropTransform()).into(ivAvatar);
                        }
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        throwable.printStackTrace();
                    }
                });
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnAccept:
                answerCall();
                break;
            case R.id.btnRejectCall:
                rejectCall();
                break;
        }
    }

    private void rejectCall() {
        try {
            @SuppressLint("WrongConstant") TelephonyManager telephonyManager = (TelephonyManager) getApplicationContext().getSystemService("phone");
            Method methodGetITelephony = Class.forName(telephonyManager.getClass().getName()).getDeclaredMethod("getITelephony", new Class[0]);
            methodGetITelephony.setAccessible(true);
            Object telephonyInterface = methodGetITelephony.invoke(telephonyManager, new Object[0]);
            Class.forName(telephonyInterface.getClass().getName()).getDeclaredMethod("endCall", new Class[0]).invoke(telephonyInterface, new Object[0]);
        } catch (Exception e) {

        }
        stopScreen();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            unregisterReceiver(this.receiver);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }



    public void stopScreen() {
        new Thread(new Runnable() {
            public void run() {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                }
                CallScreenActivity.this.finish();
            }
        }).start();
    }

    private void answerCall() {
        new Thread(new Runnable() {
            public void run() {
                try {
                    Runtime.getRuntime().exec("input keyevent " + Integer.toString(79));
                } catch (IOException e) {
                    String enforcedPerm = "android.permission.CALL_PRIVILEGED";
                    Intent btnDown = new Intent("android.intent.action.MEDIA_BUTTON").putExtra("android.intent.extra.KEY_EVENT", new KeyEvent(0, 79));
                    Intent btnUp = new Intent("android.intent.action.MEDIA_BUTTON").putExtra("android.intent.extra.KEY_EVENT", new KeyEvent(1, 79));
                    CallScreenActivity.this.getApplicationContext().sendOrderedBroadcast(btnDown, enforcedPerm);
                    CallScreenActivity.this.getApplicationContext().sendOrderedBroadcast(btnUp, enforcedPerm);
                }
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e2) {
                    e2.printStackTrace();
                }
                CallScreenActivity.this.finish();
            }
        }).start();
    }

}
