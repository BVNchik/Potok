package ru.kodep.vlad.potok;


import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.util.Objects;

import ru.kodep.vlad.potok.Network.Preferences;
import ru.kodep.vlad.potok.ui.fragment.FragmentAuthorization;
import ru.kodep.vlad.potok.ui.fragment.FragmentDisplayOfData;

public class MainActivity extends AppCompatActivity {


    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FragmentAuthorization fragmentAuthorization;
        FragmentDisplayOfData fragmentDisplayOfData;
        String launched = new Preferences(this).getLaunch();
        if (Objects.equals(launched, "true")) {
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
            new Preferences(this).setLaunch();
            new Preferences(this).setLastRequest("1970-03-07T13:31:50+03:00");
        }
    }
}
