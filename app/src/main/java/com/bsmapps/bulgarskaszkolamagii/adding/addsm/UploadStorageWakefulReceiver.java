package com.bsmapps.bulgarskaszkolamagii.adding.addsm;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.WakefulBroadcastReceiver;

/**
 * Created by Mlody Danon on 8/13/2017.
 */

public class UploadStorageWakefulReceiver extends WakefulBroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        Intent upload = new Intent(context, ReportUploader.class);
        Bundle bundle = intent.getExtras();
        upload.putExtras(bundle);

        startWakefulService(context, upload);
    }
}
