package ru.kodep.vlad.potok;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.RequiresApi;

import ru.kodep.vlad.potok.service.ReminderOfValidityReceiver;
import ru.kodep.vlad.potok.repository.DataRepository;

/**
 * Created by vlad on 07.03.18
 */

public class ReminderOfValidity {

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void onCreate(Context context) {
        PotokApp app = (PotokApp) context.getApplicationContext();
        DataRepository mRepository = app.getDataRepository();
        AlarmManager manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent myIntent;
        myIntent = new Intent(context, ReminderOfValidityReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, myIntent, 0);
        assert manager != null;
        manager.set(AlarmManager.RTC_WAKEUP, mRepository.getmPreferences().getValidTo(), pendingIntent);
    }

}
