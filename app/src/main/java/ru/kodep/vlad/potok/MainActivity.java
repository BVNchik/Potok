package ru.kodep.vlad.potok;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import java.util.Calendar;

import ru.kodep.vlad.potok.service.CallReceiverService;
import ru.kodep.vlad.potok.repository.DataRepository;
import ru.kodep.vlad.potok.ui.fragment.FragmentAuthorization;
import ru.kodep.vlad.potok.ui.fragment.FragmentDisplayOfData;

public class MainActivity extends AppCompatActivity {


    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        startService(new Intent(this,CallReceiverService.class));
        FragmentAuthorization fragmentAuthorization;
        FragmentDisplayOfData fragmentDisplayOfData;
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

    @Override
    protected void onPause() {
        super.onPause();
        Log.i(getClass().getName(), "ONPAUSE");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.i(getClass().getName(), "ONSTOP");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.i(getClass().getName(), "ONRESTART");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i(getClass().getName(), "ONDESTROY");
    }
}
