package com.matrixfrats.syntex;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.widget.Toast;

/**
 * Created by DELL on 30-03-2018.
 */

public class Rebootreceiver extends BroadcastReceiver {


    @Override
    public void onReceive(Context context, Intent intent) {

        if(intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)){
            context.startService(new Intent(context,Uploadlatlongservice.class));
        }
    }
}
