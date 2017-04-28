package com.example.checked.receivers;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;

public class CheckedReceiver extends BroadcastReceiver {

    Uri uri;

    @Override
    public void onReceive(Context context, Intent intent) {

        uri = NotificationReceiver.mCurrentUri;
        context.getContentResolver().delete(uri, null, null);

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(1);
    }
}
