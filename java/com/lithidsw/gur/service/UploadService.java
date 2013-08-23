package com.lithidsw.gur.service;

import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.widget.Toast;

import com.lithidsw.gur.MainActivity;
import com.lithidsw.gur.R;
import com.lithidsw.gur.database.QueTable;
import com.lithidsw.gur.loader.ImageUploader;
import com.lithidsw.gur.utils.NotificationUpload;

public class UploadService extends Service {

    Context context;
    private int mQueCount = 0;
    private Intent intent;
    private final Handler handler = new Handler();

    @Override
    public void onCreate() {
        super.onCreate();
        intent = new Intent("com.lithidsw.gur.UPLOAD_COMPLETE");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        context = this;
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
        final Thread thread = new Thread() {
            @Override
            public void run() {
                mQueCount = new QueTable(context).getQueCount();
                while (mQueCount != 0) {
                    String[] que_item = new QueTable(context).getLastestQue();
                    if (que_item != null) {
                        fg("Uploading image | " + que_item[1]);
                        new ImageUploader(context).uploadImage(que_item[2], que_item[1]);
                        handler.post(sendUpdatesToUI);
                    }

                    mQueCount = new QueTable(context).getQueCount();
                }

                NotificationUpload.startNoti(context);
                stopSelf();
            }
        };
        thread.start();
        fg("Uploading...");
    }

    private Runnable sendUpdatesToUI = new Runnable() {
        public void run() {
            sendBroadcast(intent);
        }
    };

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
                .addAction(R.drawable.ic_action_remove, "Stop upload", getStopIntent());

        Intent intent = new Intent(context, MainActivity.class);
        intent.setAction(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);

        PendingIntent resultPendingIntent = PendingIntent.getActivity(context,
                0, intent, 0);
        mBuilder.setContentIntent(resultPendingIntent);
        startForeground(1, mBuilder.build());
    }
}
