package ru.kodep.vlad.potok;

import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import java.util.Calendar;

import ru.kodep.vlad.potok.repository.DataRepository;
import ru.kodep.vlad.potok.ui.fragment.FragmentAuthorization;
import ru.kodep.vlad.potok.ui.fragment.FragmentDisplayOfData;

public class MainActivity extends AppCompatActivity {
    private int request_counter = 0;
    FragmentAuthorization fragmentAuthorization;
    FragmentDisplayOfData fragmentDisplayOfData;

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        PotokApp app = (PotokApp) getApplication();
        DataRepository mRepository = app.getDataRepository();
        long time = mRepository.getmPreferences().getValidTo();
        if (time > Calendar.getInstance().getTimeInMillis()) {
            fragmentDisplayOfData = new FragmentDisplayOfData();
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragmentLayout, fragmentDisplayOfData)
                    .commit();
        } else {
            fragmentAuthorization = new FragmentAuthorization();
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragmentLayout, fragmentAuthorization)
                    .commit();
        }
    }
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Log.i(getClass().getName(), "Сработал");
        if (requestCode == FragmentDisplayOfData.PERMISSION_REQUEST_CODE && grantResults.length == 1) {
          if (request_counter < 1){
            if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
                request_counter++;
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Важное сообщение!")
                        .setMessage("Для работы приложения следует разрешить!")
                        .setIcon(R.drawable.logo)
                        .setCancelable(false)
                        .setNegativeButton("ОК",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        FragmentDisplayOfData fragment = (FragmentDisplayOfData) getSupportFragmentManager().findFragmentById(R.id.fragmentLayout);
                                        fragment.getExternalStorageFiles();
                                        dialog.cancel();
                                    }
                                });
                AlertDialog alert = builder.create();
                alert.show();
            } }
            else{
                FragmentDisplayOfData fragment = (FragmentDisplayOfData) getSupportFragmentManager().findFragmentById(R.id.fragmentLayout);
                fragment.noPermission();
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}
