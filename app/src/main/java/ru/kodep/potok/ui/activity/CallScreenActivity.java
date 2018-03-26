package ru.kodep.potok.ui.activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.telecom.TelecomManager;
import android.telephony.TelephonyManager;
import android.util.Log;
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
import java.util.Arrays;

import ru.kodep.potok.R;
import ru.kodep.potok.database.UsersStorage;
import ru.kodep.potok.models.User;
import ru.kodep.potok.utils.Logger;
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

public class CallScreenActivity extends Activity implements View.OnClickListener {
    Button btnAnswerCall;
    Button btnRejectCall;
    BroadcastReceiver receiver = new BReceiver();
    private UsersStorage mUsersStorage;

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
        mUsersStorage = new UsersStorage();
        Single.create(new Single.OnSubscribe<User>() {
            @Override
            public void call(SingleSubscriber<? super User> singleSubscriber) {
                try {
                    User user = mUsersStorage.seekUser(phone, context);
                    singleSubscriber.onSuccess(user);
                } catch (RuntimeException e) {
                    Logger.print(e);
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
                            tvUserName.setText(user.getName());
                            tvTitle.setText(user.getTitle());
                            Glide.with(context).load(user.getAvatar()).apply(RequestOptions.placeholderOf(R.drawable.face_icon)).apply(RequestOptions.circleCropTransform()).into(ivAvatar);
                        }
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        Logger.print(throwable);
                    }
                });
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btnAccept) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                TelecomManager telecomManager = (TelecomManager) getSystemService(Context.TELECOM_SERVICE);
                assert telecomManager != null;
                telecomManager.acceptRingingCall();
                stopScreen();
            } else {
                answerCall();
            }
        }
        if (v.getId() == R.id.btnRejectCall) {
            rejectCall();
        }
    }

    private void rejectCall() {
        try {
            TelephonyManager telephonyManager = (TelephonyManager) getApplicationContext().getSystemService(Context.TELEPHONY_SERVICE);
            assert telephonyManager != null;
            Method methodGetITelephony = Class.forName(telephonyManager.getClass().getName()).getDeclaredMethod("getITelephony");
            methodGetITelephony.setAccessible(true);
            Object telephonyInterface = methodGetITelephony.invoke(telephonyManager);
            Class.forName(telephonyInterface.getClass().getName()).getDeclaredMethod("endCall").invoke(telephonyInterface);
        } catch (Exception ignored) {
            Log.i(getClass().getName(), Arrays.toString(ignored.getStackTrace()));
        }
        stopScreen();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            unregisterReceiver(this.receiver);
        } catch (Exception e) {
            Logger.print(e);
        }
    }

    public void stopScreen() {
        new Thread(new Runnable() {
            public void run() {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    Logger.print(e);
                    Thread.currentThread().interrupt();
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
                    Logger.print(e2);
                    Thread.currentThread().interrupt();
                }
                CallScreenActivity.this.finish();
            }
        }).start();
    }

    class BReceiver extends BroadcastReceiver {
        BReceiver() {
        }
        public void onReceive(Context context, Intent intent) {
            Object service = context.getSystemService(Context.TELEPHONY_SERVICE);

            if (service instanceof TelephonyManager && ((TelephonyManager) service).getCallState() != 1) {
                finish();
            }
        }
    }
}
