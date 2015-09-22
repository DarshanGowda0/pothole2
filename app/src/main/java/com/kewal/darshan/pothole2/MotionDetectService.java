package com.kewal.darshan.pothole2;

import android.app.IntentService;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.location.ActivityRecognitionResult;
import com.google.android.gms.location.DetectedActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by darshan on 22/09/15.
 */
public class MotionDetectService extends IntentService {


    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */
    public MotionDetectService(String name) {
        super(name);
    }

    public MotionDetectService() {
        super("");

    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("ROHAN", "service created");

    }

    @Override
    protected void onHandleIntent(Intent intent) {

        Log.d("ROHAN","service started");

        ActivityRecognitionResult result = ActivityRecognitionResult.extractResult(intent);

        ArrayList<DetectedActivity> detectedActivities = (ArrayList) result.getProbableActivities();

        for (DetectedActivity da : detectedActivities) {

            if(da.getType()==DetectedActivity.IN_VEHICLE){
                Log.d("ROHAN","In vehicle "+da.getConfidence());
            }

            if(da.getType()==DetectedActivity.ON_FOOT){
                Log.d("ROHAN","on foot "+da.getConfidence());
            }
            if(da.getType()==DetectedActivity.ON_BICYCLE){
                Log.d("ROHAN","on bicycle "+da.getConfidence());
            }
            if(da.getType()==DetectedActivity.RUNNING){
                Log.d("ROHAN","running "+da.getConfidence());
            }
            if(da.getType()==DetectedActivity.STILL){
                Log.d("ROHAN","STILL"+da.getConfidence());
            }

            if(da.getType()==DetectedActivity.WALKING){
                Log.d("ROHAN","Walking "+da.getConfidence());
            }

            if(da.getType()==DetectedActivity.TILTING){
                Log.d("ROHAN","tilting "+da.getConfidence());
            }

            if(da.getType()==DetectedActivity.UNKNOWN){
                Log.d("ROHAN","Unknown "+da.getConfidence());
            }



        }



    }
}
