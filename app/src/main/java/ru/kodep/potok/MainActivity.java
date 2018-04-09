package ru.kodep.potok;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;

import java.util.Calendar;

import ru.kodep.potok.repository.DataRepository;
import ru.kodep.potok.ui.fragment.FragmentAuthorization;
import ru.kodep.potok.ui.fragment.FragmentDisplayOfData;

public class MainActivity extends AppCompatActivity {
    FragmentAuthorization fragmentAuthorization;
    FragmentDisplayOfData fragmentDisplayOfData;
    private int requestCounter = 0;

    @SuppressLint("WrongConstant")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        PotokApp app = (PotokApp) getApplication();
        DataRepository mRepository = app.getDataRepository();
        long time = mRepository.getmPreferences().getValidTo();
        if (time > Calendar.getInstance().getTimeInMillis()) {
            newFragmentDisplayOfData();
        } else {
            newFragmentAuthorization();
        }
    }

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
