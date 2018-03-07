package ru.kodep.vlad.testingreadnumberphone.Network;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Build;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;


import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.GooglePlayDriver;
import com.firebase.jobdispatcher.Job;
import com.firebase.jobdispatcher.Lifetime;
import com.firebase.jobdispatcher.RetryStrategy;
import com.firebase.jobdispatcher.Trigger;

import java.util.Objects;

import ru.kodep.vlad.testingreadnumberphone.Database.UsersStorage;
import ru.kodep.vlad.testingreadnumberphone.R;
import ru.kodep.vlad.testingreadnumberphone.models.User;
import ru.kodep.vlad.testingreadnumberphone.ui.activity.HTCActivity;
import rx.Single;
import rx.SingleSubscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;


/**
 * Created by vlad on 21.02.18
 */

public class PhoneStateChangedReceiver extends BroadcastReceiver {
    private static final String HTC = "HTC";
    @SuppressLint("UnsafeProtectedBroadcastReceiver")
    private static boolean incomingCall = false;
    private static WindowManager windowManager;
    @SuppressLint("StaticFieldLeak")
    private static ViewGroup windowLayout;
    private UsersStorage mUsersStorage;

    @SuppressLint({"NewApi", "WrongConstant"})
    @Override
    public void onReceive(Context context, Intent intent) {

        if (Objects.equals(intent.getAction(), "android.intent.action.PHONE_STATE")) {
            String phoneState = intent.getStringExtra(TelephonyManager.EXTRA_STATE);
            if (phoneState.equals(TelephonyManager.EXTRA_STATE_RINGING)) {
                //Трубка не поднята, телефон звонит
                String phoneNumber = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER);
                incomingCall = true;
                if (Objects.equals(Build.BRAND, HTC)) {
                    Intent intents = new Intent(context, HTCActivity.class);
                    intents.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intents);
                } else
                    Log.i("Receiver", "showWindow");
                showWindow(context, phoneNumber);

            } else if (phoneState.equals(TelephonyManager.EXTRA_STATE_OFFHOOK)) {
                //Телефон находится в режиме звонка (набор номера при исходящем звонке / разговор)
                if (incomingCall) {
                    closeWindow();
                    incomingCall = false;
                }
            } else if (phoneState.equals(TelephonyManager.EXTRA_STATE_IDLE)) {
                //Телефон находится в ждущем режиме - это событие наступает по окончанию разговора
                //или в ситуации "отказался поднимать трубку и сбросил звонок".
                if (incomingCall) {
                    closeWindow();
                    incomingCall = false;
                }
            }
        }
    }

    @SuppressLint("InflateParams")
    private void showWindow(final Context context, final String phone) {

        mUsersStorage = new UsersStorage();
        windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        @SuppressLint("InlinedApi") WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_SYSTEM_OVERLAY,
//                WindowManager.LayoutParams.TYPE_APPLICATION_ATTACHED_DIALOG,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL,
                PixelFormat.TRANSLUCENT);
        params.gravity = Gravity.TOP;
        assert layoutInflater != null;
        windowLayout = (ViewGroup) layoutInflater.inflate(R.layout.info, null);

        Single.create(new Single.OnSubscribe<User>() {
            @Override
            public void call(SingleSubscriber<? super User> singleSubscriber) {

                try {
                    User user = mUsersStorage.seekUser(phone, context);
                    Log.i("Vivelo", " " + user.getName());
                    singleSubscriber.onSuccess(user);
                } catch (Throwable e) {
                    singleSubscriber.onError(e);
                    Log.i("Cursor2", "ошибка: " + e);
                }
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<User>() {
                    @Override
                    public void call(User user) {
                        Log.i("Pokaz", Thread.currentThread().getName());
                        TextView tvNumber = windowLayout.findViewById(R.id.tvNumberPhone);
                        TextView tvUserName = windowLayout.findViewById(R.id.tvUserName);
                        TextView tvTitle = windowLayout.findViewById(R.id.tvTitles);
                        ImageView ivAvatar = windowLayout.findViewById(R.id.ivAvatar);
                        tvNumber.setText(phone);
                        tvUserName.setText(user.getName());
                        tvTitle.setText(user.getTitle());
                        Glide.with(context).load(user.getAvatar()).apply(RequestOptions.placeholderOf(R.drawable.face_icon)).into(ivAvatar);
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        throwable.printStackTrace();
                        Log.i("ERROR", String.valueOf(throwable));
                    }
                });

        Button buttonClose = windowLayout.findViewById(R.id.buttonClose);
        buttonClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeWindow();
            }
        });
        windowManager.addView(windowLayout, params);
    }

    private void closeWindow() {
        if (windowLayout != null) {
            windowManager.removeView(windowLayout);
            windowLayout = null;
        }
    }

}
