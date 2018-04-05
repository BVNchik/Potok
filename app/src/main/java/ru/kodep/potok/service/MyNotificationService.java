package ru.kodep.potok.service;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.support.annotation.RequiresApi;
import android.support.v4.content.LocalBroadcastManager;

@RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
public class MyNotificationService extends NotificationListenerService {
    private static final String INTENT_EXTRA_PACKAGE ="package" ;
    private static final String INTENT_ACTION = "Msg";
    StatusBarNotification mysbn;
    Context context;

    public StatusBarNotification[] getActiveNotifications() {
        return super.getActiveNotifications();
    }

    public void onCreate() {
        super.onCreate();
        this.context = getApplicationContext();
    }

    public void onNotificationPosted(StatusBarNotification statusBarNotification) {
        mysbn = statusBarNotification;
      String packageName = statusBarNotification.getPackageName();
        Intent intent = new Intent(INTENT_ACTION);
        intent.putExtra(INTENT_EXTRA_PACKAGE, packageName);
        LocalBroadcastManager.getInstance(this.context).sendBroadcast(intent);
    }

    public void onNotificationRemoved(StatusBarNotification statusBarNotification) {
        //
    }
}