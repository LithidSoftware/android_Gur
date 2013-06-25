package com.lithidsw.gur.service;

import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;

import com.lithidsw.gur.MainActivity;
import com.lithidsw.gur.R;
import com.lithidsw.gur.utils.NotificationUpload;

public class UploadService extends Service {

    Context context;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        context = this;
        System.out.println("Gur, starting the upload service");
        start();
        return (START_NOT_STICKY);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        stopForeground(true);
    }

    private void start() {
        System.out.println("Gur, starting the start function");
        final Thread thread = new Thread() {
            @Override
            public void run() {
                for (int i=1; i<=5; i++) {
                    fg("Uploading... "+i+"/5");
                    try {
                        sleep(5000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    NotificationUpload.startNoti(context);
                }
                stopSelf();
            }
        };
        thread.start();
        fg("Uploading...");
    }

    private PendingIntent getStopIntent() {
        Intent intent = new Intent("com.lithidsw.gur.STOP_CURRENT");
        return PendingIntent.getBroadcast(context, 0, intent, 0);
    }

    private void fg(String message) {
        Bitmap bit = BitmapFactory.decodeResource(getResources(),
                R.drawable.ic_stat_noti_upload);
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
                context)
                .setSmallIcon(R.drawable.ic_stat_noti_upload)
                .setLargeIcon(bit)
                .setProgress(0, 0, true)
                .setContentTitle("Gur Upload")
                .setContentText(message)
                .addAction(R.drawable.ic_action_remove, "Stop upload",
                        getStopIntent());

        Intent intent = new Intent(context, MainActivity.class);
        intent.setAction(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);

        PendingIntent resultPendingIntent = PendingIntent.getActivity(context,
                0, intent, 0);
        mBuilder.setContentIntent(resultPendingIntent);
        startForeground(1, mBuilder.build());
    }
}
