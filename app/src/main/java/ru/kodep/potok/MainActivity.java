package ru.kodep.potok;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import ru.kodep.potok.repository.DataRepository;
import ru.kodep.potok.ui.fragment.FragmentAuthorization;
import ru.kodep.potok.ui.fragment.FragmentDisplayOfData;
import ru.kodep.potok.ui.fragment.FragmentLog;

import static android.view.KeyEvent.KEYCODE_HELP;

public class MainActivity extends AppCompatActivity {
    FragmentAuthorization fragmentAuthorization;
    FragmentDisplayOfData fragmentDisplayOfData;
    FragmentLog fragmentLog;
    Date mOneClickTime;
    Date mNextClickTime;
    int click = 0;
    long nextTime = 0;
    long oneTime = 0;
    long time = 0;
    private int requestCounter = 0;
    PotokApp app;
    DataRepository mRepository;

    @SuppressLint("WrongConstant")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        app = (PotokApp) getApplication();
        mRepository = app.getDataRepository();
        long time = mRepository.getmPreferences().getValidTo();
        if (time > Calendar.getInstance().getTimeInMillis()) {
            newFragmentDisplayOfData();
        } else {
            newFragmentAuthorization();
        }
//        if(mRepository.getmPreferences().getFirstStart()){
//            mRepository.writesFile("ЛОГИ:");
//        }
    }

    @SuppressLint("WrongConstant")
    private void newFragmentDisplayOfData() {
       fragmentDisplayOfData = new FragmentDisplayOfData();
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragmentLayout, fragmentDisplayOfData)
                .commit();

    }

    private void newFragmentAuthorization() {
        fragmentAuthorization = new FragmentAuthorization();
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragmentLayout, fragmentAuthorization)
                .commit();
    }

    private void newFragmentLog() {
        fragmentLog = new FragmentLog();
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragmentLayout, fragmentLog)
                .addToBackStack(fragmentLog.getClass().getName())
                .commit();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == 24) {
            if (click == 0) {
                mOneClickTime = Calendar.getInstance().getTime();
            }
            click++;
            if (click == 2) {
                mNextClickTime = Calendar.getInstance().getTime();
                oneTime = mOneClickTime.getTime();
                nextTime = mNextClickTime.getTime();
                time = nextTime - oneTime;

                if (time != 0 && time < 300) {
                    oneTime = nextTime;
                    nextTime = 0;
                    time = 0;
                } else {
                    oneTime = 0;
                    nextTime = 0;
                    time = 0;
                    click = 0;
                }
            }
            if (click == 3) {
                mNextClickTime = Calendar.getInstance().getTime();
                nextTime = mNextClickTime.getTime();
                time = nextTime - oneTime;
                if (time != 0 && time < 300) {
                    newFragmentLog();
                    click = 0;
                }
            }

        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        FragmentDisplayOfData fragment = (FragmentDisplayOfData) getSupportFragmentManager().findFragmentById(R.id.fragmentLayout);
        if (requestCode == FragmentDisplayOfData.PERMISSION_REQUEST_CODE && grantResults.length == 3) {
            if (requestCounter < 1) {
                if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
                    requestCounter++;
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle(R.string.important_message)
                            .setMessage(R.string.work_permit)
                            .setIcon(R.drawable.logo)
                            .setCancelable(false)
                            .setNegativeButton(R.string.ok,
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            FragmentDisplayOfData fragment = (FragmentDisplayOfData) getSupportFragmentManager().findFragmentById(R.id.fragmentLayout);
                                            fragment.getPermissionReadPhoneState();
                                            dialog.cancel();
                                        }
                                    });
                    AlertDialog alert = builder.create();
                    alert.show();
                }
            } else {
                fragment.noPermission();
            }
        }
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            fragment.dialogSettings();
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

}
