package com.kewal.darshan.pothole2;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Created by darshan on 23/09/15.
 */
public class On_off_BC extends BroadcastReceiver{

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("ROHAN","boot completed and starting service");
        Intent in = new Intent(context,BroadcastReceiver.class);
        context.startService(in);
    }
}
