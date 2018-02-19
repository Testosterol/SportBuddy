package com.example.dtrebula.bbc2;

/**
 * Created by dtrebula on 21. 4. 2017.
 */
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class NotificationServiceStarterReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        NotificationEventReceiver.setupAlarm(context);
    }
}

