package ru.kodep.vlad.potok.Network;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Build;
import android.telephony.TelephonyManager;
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

import java.util.Objects;

import ru.kodep.vlad.potok.Database.UsersStorage;
import ru.kodep.vlad.potok.R;
import ru.kodep.vlad.potok.models.User;
import ru.kodep.vlad.potok.ui.activity.HTCActivity;
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
                        if( user.getName() != null) {
                            windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
                            LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                            @SuppressLint("InlinedApi") WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                                    WindowManager.LayoutParams.MATCH_PARENT,
                                    WindowManager.LayoutParams.WRAP_CONTENT,
                                    WindowManager.LayoutParams.TYPE_SYSTEM_OVERLAY,
                                    WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL,
                                    PixelFormat.TRANSLUCENT);
                            params.gravity = Gravity.TOP;
                            assert layoutInflater != null;
                            windowLayout = (ViewGroup) layoutInflater.inflate(R.layout.info, null);
                            TextView tvNumber = windowLayout.findViewById(R.id.tvNumberPhone);
                            TextView tvUserName = windowLayout.findViewById(R.id.tvUserName);
                            TextView tvTitle = windowLayout.findViewById(R.id.tvTitles);
                            ImageView ivAvatar = windowLayout.findViewById(R.id.ivAvatar);
                            tvNumber.setText(phone);
                            numberInTheDatabase[0] = user.getName();
                            tvUserName.setText(user.getName());
                            tvTitle.setText(user.getTitle());
                            Glide.with(context).load(user.getAvatar()).apply(RequestOptions.placeholderOf(R.drawable.face_icon)).into(ivAvatar);
                            Button buttonClose = windowLayout.findViewById(R.id.buttonClose);
                            buttonClose.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    closeWindow();
                                }
                            });

                            windowManager.addView(windowLayout, params);
                        }
                        }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        throwable.printStackTrace();
                    }
                });


    }

    private void closeWindow() {
        if (windowLayout != null) {
            windowManager.removeView(windowLayout);
            windowLayout = null;
        }
    }

}
