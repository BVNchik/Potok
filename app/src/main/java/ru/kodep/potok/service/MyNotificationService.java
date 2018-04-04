package ru.kodep.potok.service;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.support.v4.content.LocalBroadcastManager;

@SuppressLint("NewApi")
public class MyNotificationService extends NotificationListenerService {
    static StatusBarNotification mysbn;
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
        Intent intent = new Intent("Msg");
        intent.putExtra("package", packageName);
        LocalBroadcastManager.getInstance(this.context).sendBroadcast(intent);
    }

    public void onNotificationRemoved(StatusBarNotification statusBarNotification) {
    }
}