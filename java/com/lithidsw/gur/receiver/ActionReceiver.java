package com.lithidsw.gur.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.lithidsw.gur.service.UploadService;
import com.lithidsw.gur.utils.NotificationUpload;

public class ActionReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if ((intent.getAction() != null)) {
            if (intent.getAction().equals("com.lithidsw.gur.STOP_CURRENT")) {
                context.stopService(new Intent(context, UploadService.class));
            }
        }
    }
}
