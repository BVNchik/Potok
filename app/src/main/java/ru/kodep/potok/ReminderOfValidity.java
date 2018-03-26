package ru.kodep.potok;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import ru.kodep.potok.repository.DataRepository;
import ru.kodep.potok.service.ReminderOfValidityReceiver;

/**
 * Created by vlad on 07.03.18
 */

public class ReminderOfValidity {
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
