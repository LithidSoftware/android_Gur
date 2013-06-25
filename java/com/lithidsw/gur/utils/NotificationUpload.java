package com.lithidsw.gur.utils;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

import com.lithidsw.gur.MainActivity;
import com.lithidsw.gur.R;

public class NotificationUpload {

    private static NotificationManager nm;
    private static NotificationCompat.Builder mBuilder;

    public static void startNoti(Context context) {
        nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        mBuilder = new NotificationCompat.Builder(context);
        mBuilder.setContentTitle("Gur")
                .setContentText("Upload complete")
                .setSmallIcon(R.drawable.ic_stat_noti_gur);

        Intent intent = new Intent(context, MainActivity.class);
        intent.setAction(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.putExtra("opentab", 1);
        intent.putExtra("clear", true);

        PendingIntent resultPendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        mBuilder.setContentIntent(resultPendingIntent);

        nm.notify(0, mBuilder.build());
    }
}
