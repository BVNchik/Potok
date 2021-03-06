package ru.kodep.potok.ui.fragment;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import ru.kodep.potok.DataCleaning;
import ru.kodep.potok.PotokApp;
import ru.kodep.potok.R;
import ru.kodep.potok.repository.DataRepository;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;


/**
 * Created by vlad on 22.02.18
 */

public class FragmentDisplayOfData extends Fragment implements View.OnClickListener{
    public static final int PERMISSION_REQUEST_CODE = 1;
    private static final String MEIZU = "Meizu";
    private static final String MEIZU_SHOW_APPSEC = "com.meizu.safe.security.SHOW_APPSEC";
    private static final String MEIZU_SAFE = "com.meizu.safe";
    private static final String MEIZU_APP_SEC_ACTIVITY = "com.meizu.safe.security.AppSecActivity";
    private static final String INTENT_EXTRA_PACKAGE_NAME = "packageName";
    TextView tvDescription;
    TextView tvOnAndOff;
    private Subscription mSubscription;
    PotokApp app;
    DataRepository mRepository;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        potokApp();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            getPermission();
        } else {
            getPermissionReadPhoneState();
        }

    }

    public void dialogSettings() {
        final FragmentActivity activity = getActivity();
        if (activity == null) {
            return;
        }
        PotokApp app = (PotokApp) activity.getApplication();
        final DataRepository mRepository = app.getDataRepository();
        Boolean firstStart = mRepository.getmPreferences().getFirstStart();
        if ((Build.BRAND.equals(MEIZU) && firstStart)) {
            AlertDialog.Builder builder = new AlertDialog.Builder(activity);
            builder.setTitle(R.string.important_message)
                    .setMessage(R.string.permission_background)
                    .setIcon(R.drawable.logo)
                    .setCancelable(false)
                    .setNegativeButton(R.string.go_to_settings,
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    goApplicationSettings(dialog, activity, mRepository);
                                }
                            });
            AlertDialog alert = builder.create();
            alert.show();
        }
        if (firstStart) {
            AlertDialog.Builder builder = new AlertDialog.Builder(activity);
            builder.setTitle(R.string.access_notifications)
                    .setMessage(R.string.permission_notification)
                    .setIcon(R.drawable.logo)
                    .setCancelable(false)
                    .setNegativeButton(R.string.go_to_settings,
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
                                        startActivity(new Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                                    }
                                }
                            });
            AlertDialog alert = builder.create();
            alert.show();
            mRepository.getmPreferences().setFirstStart();
        }
    }

    public void goApplicationSettings(DialogInterface dialog, FragmentActivity activity, DataRepository mRepository) {
        Intent intent = new Intent(MEIZU_SHOW_APPSEC);
        intent.setClassName(MEIZU_SAFE, MEIZU_APP_SEC_ACTIVITY);
        intent.putExtra(INTENT_EXTRA_PACKAGE_NAME, activity.getPackageName());
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        activity.startActivity(intent);
        mRepository.getmPreferences().setFirstStart();
        dialog.cancel();
    }

    public void potokApp() {
        final FragmentActivity activity = getActivity();
        if (activity == null) {
            return;
        }
        app = (PotokApp) getActivity().getApplication();
        mRepository = app.getDataRepository();
        @SuppressLint("SimpleDateFormat") String data = new SimpleDateFormat("dd MMMM yyyy HH:mm:ss").format(new Date(Calendar.getInstance().getTimeInMillis() ));
        mRepository.writeFile("Отправлен запрос на сервер ("+data + ")");
        mSubscription = mRepository.fetchAllUsers()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Integer>() {
                    @Override
                    public void call(Integer integer) {
                        //
                        mRepository.numberOfContacts(integer);
                                           }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        mRepository.writeFile("Произошла ошибка при загрузке данных: " + throwable + throwable.getMessage());
                        Log.i(getClass().getName(), String.valueOf(throwable));
                    }
                });
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mSubscription != null && !mSubscription.isUnsubscribed()) {
            mSubscription.unsubscribe();
        }
    }

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.screen_display_of_data, container, false);
        tvDescription = view.findViewById(R.id.tvDescription);
        tvOnAndOff = view.findViewById(R.id.tvOnAndOff);
        view.findViewById(R.id.btnOut).setOnClickListener(this);
        return view;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btnOut) {
            outAccount();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void outAccount() {
        new DataCleaning(getContext());
        FragmentAuthorization fragmentAuthorization = new FragmentAuthorization();
        getFragmentManager()
                .beginTransaction()
                .replace(R.id.fragmentLayout, fragmentAuthorization)
                .commit();
    }

    public void noPermission() {
        tvOnAndOff.setText(R.string.off);
        tvOnAndOff.setBackgroundResource(R.drawable.rounded_tv_off);
        tvDescription.setText(R.string.enable_permission);

    }

    public void getPermissionReadPhoneState() {
        final FragmentActivity activity = getActivity();
        if (activity == null) {
            return;
        }
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_PHONE_STATE)
                == PackageManager.PERMISSION_DENIED && ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ANSWER_PHONE_CALLS) == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{
                            Manifest.permission.READ_PHONE_STATE,
                    },
                    PERMISSION_REQUEST_CODE);
        } else {
            dialogSettings();
        }

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void getPermission() {
        final FragmentActivity activity = getActivity();
        if (activity == null) {
            return;
        }
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_PHONE_STATE)
                == PackageManager.PERMISSION_DENIED && ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ANSWER_PHONE_CALLS) == PackageManager.PERMISSION_DENIED && ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{
                            Manifest.permission.READ_PHONE_STATE,
                            Manifest.permission.ANSWER_PHONE_CALLS,
                            Manifest.permission.CALL_PHONE,
                            Manifest.permission.READ_EXTERNAL_STORAGE
                    },
                    PERMISSION_REQUEST_CODE);
        } else {
            dialogSettings();
        }
    }
}
