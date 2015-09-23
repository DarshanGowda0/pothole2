package com.kewal.darshan.pothole2;

import android.app.IntentService;
import android.app.Service;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Criteria;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;
import android.location.Location;


import com.google.android.gms.location.ActivityRecognitionResult;
import com.google.android.gms.location.DetectedActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by darshan on 22/09/15.
 */
public class MotionDetectService extends Service{

    private SensorManager sensorManager;
    double ax, ay, az;
    private double mAccel;
    private double mAccelCurrent;
    private double mAccelLast;
    public static final String TAG = "DARSHANROHAN";
    Location location;
    double latitude;
    double longitude;
    protected LocationManager locationManager;
    boolean isGPSEnabled = false;
    boolean canGetLocation = false;
    Criteria criteria;
    boolean isNetworkEnabled;
    String bestProvider;
    Location loc;
    public static String FILENAME = "abhi.rohan.darshan";

    LocListener locListener;
    mySensorListener mySL;


    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "service destroyed");
        unregisterSensor();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("ROHAN", "service created");
        locListener = new LocListener();
        mySL = new mySensorListener();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public void registerSensor(){
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        sensorManager.registerListener(mySL, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_NORMAL);

    }

    public void unregisterSensor(){
        if(sensorManager!=null){
            try {
                sensorManager.unregisterListener(mySL);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        callOnHandle(intent);
        return Service.START_REDELIVER_INTENT;
    }

    void callOnHandle(Intent intent){

        //remove this later before releasing
        registerSensor();

//        callMovementDetection(intent);

    }

    private void callMovementDetection(Intent intent) {

        Log.d("ROHAN", "service started");

        ActivityRecognitionResult result = ActivityRecognitionResult.extractResult(intent);

        ArrayList<DetectedActivity> detectedActivities = (ArrayList) result.getProbableActivities();



        for (DetectedActivity da : detectedActivities) {

            if(da.getType()==DetectedActivity.IN_VEHICLE){
                Log.d("ROHAN","In vehicle "+da.getConfidence());
                if(da.getConfidence()>=70){
                    //start recording
//                    registerSensor();
                }

            }
            if(da.getType()==DetectedActivity.ON_FOOT){
                Log.d("ROHAN","on foot "+da.getConfidence());
            }
            if(da.getType()==DetectedActivity.ON_BICYCLE){
                Log.d("ROHAN", "on bicycle " + da.getConfidence());
                if(da.getConfidence()>=70){
                    //start recording
//                    registerSensor();
                }
            }
            if(da.getType()==DetectedActivity.RUNNING){
                Log.d("ROHAN","running "+da.getConfidence());
            }
            if(da.getType()==DetectedActivity.STILL){
                Log.d("ROHAN","STILL"+da.getConfidence());
                if(da.getConfidence()>=70){
                    //stop recording
//                    unregisterSensor();
                }
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

    private void getSensorChangedDetails(SensorEvent event) {


        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            ax = event.values[0];
            ay = event.values[1];
            az = event.values[2];
            mAccelLast = mAccelCurrent;
            mAccelCurrent = Math.sqrt(ax * ax + ay * ay + az * az);
            double delta = mAccelCurrent - mAccelLast;
            mAccel = mAccel * 0.9f + delta;


//            Log.d("Test"," "+ax+" "+ay+" "+az);
            int temp = compare((int) ax, (int) ay, (int) az);

            if (temp == 0) {
                //orientation x
//                Log.d("test","X orientation");
//                Log.d("test",""+(mAccelLast-mAccelCurrent));
                if ((mAccelLast - mAccelCurrent) > 7) {
//                    Toast.makeText(this, "pothole x", Toast.LENGTH_SHORT).show();
                    Log.d("DARSHANROHAN", "pothole x");
                    if (loc == null) {
                        loc = getLocation();
                    }
                    double latitude = loc.getLatitude();
                    double longitude = loc.getLongitude();
                    Log.d(TAG, "location : " + latitude + " " + longitude);
                    storeData(latitude, longitude);
                }
            } else if (temp == 1) {
                //orientation y
//                Log.d("test","y orientation");
//                Log.d("test",""+(mAccelLast-mAccelCurrent));
                if ((mAccelLast - mAccelCurrent) > 7) {
//                    Toast.makeText(this, "pothole y", Toast.LENGTH_SHORT).show();
                    Log.d("DARSHANROHAN", "pothole y");
                    if (loc == null) {
                        loc = getLocation();
                    }
                    double latitude = loc.getLatitude();
                    double longitude = loc.getLongitude();
                    Log.d(TAG, "location : " + latitude + " " + longitude);
                    storeData(latitude, longitude);

                }
            } else if (temp == 2) {
                //orientation z
//                Log.d("test","cur:"+mAccelCurrent+"      last:"+mAccelLast);
                if ((mAccelLast - mAccelCurrent) > 7) {
//                    Toast.makeText(this, "pothole z", Toast.LENGTH_SHORT).show();
//                    Log.d("test",""+(mAccelLast-mAccelCurrent));
                    Log.d("DARSHANROHAN", "pothole z");
                    if (loc == null) {
                        loc = getLocation();
                    }
                    double latitude = loc.getLatitude();
                    double longitude = loc.getLongitude();

                    Log.d(TAG, "location : " + latitude + " " + longitude);
                    storeData(latitude, longitude);

                }
            }

        }

    }

    public Location getLocation() {
        try {
            locationManager = (LocationManager) this
                    .getSystemService(LOCATION_SERVICE);

            // getting GPS status
            isGPSEnabled = locationManager
                    .isProviderEnabled(LocationManager.GPS_PROVIDER);

            // getting network status
            isNetworkEnabled = locationManager
                    .isProviderEnabled(LocationManager.NETWORK_PROVIDER);

            if (!isGPSEnabled && !isNetworkEnabled) {
                // no network provider is enabled
                Log.d(TAG, " no network provider is enabled");
            } else {
                this.canGetLocation = true;
                // First get location from Network Provider
                if (isNetworkEnabled) {
                    locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 1, locListener);
                    Log.d(TAG, "Network");
                    if (locationManager != null) {
                        location = locationManager
                                .getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                        if (location != null) {
                            latitude = location.getLatitude();
                            longitude = location.getLongitude();
                        }
                    }
                }
                // if GPS Enabled get lat/long using GPS Services
                if (isGPSEnabled) {
                    if (location == null) {
                        locationManager.requestLocationUpdates(
                                LocationManager.GPS_PROVIDER,
                                1000,
                                1,locListener);
                        Log.d(TAG, "GPS Enabled");
                        if (locationManager != null) {
                            location = locationManager
                                    .getLastKnownLocation(LocationManager.GPS_PROVIDER);
                            if (location != null) {
                                latitude = location.getLatitude();
                                longitude = location.getLongitude();
                            }
                        }
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return location;
    }

    private void storeData(double latitude, double longitude) {

        class darsh implements Runnable {
            double lat, lon;
            JSONObject jsonObject = new JSONObject();
            String jsonString;

            darsh(double lat, double lon) {
                this.lat = lat;
                this.lon = lon;

            }

            @Override
            public void run() {

                try {
                    jsonObject.put("latitude",lat);
                    jsonObject.put("longitude",lon);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                jsonString=jsonObject.toString();
                jsonString=jsonString+",";
                writeTo(jsonString);

            }
        }

        Thread thread = new Thread(new darsh(latitude, longitude));

        thread.start();

    }

    void writeTo(String str){
        try {
            FileOutputStream fos = openFileOutput(FILENAME, MODE_APPEND);
            fos.write(str.getBytes());
            fos.close();
            Log.d(TAG,"written to "+FILENAME+" successfully");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private int compare(int ax, int ay, int az) {
        ax = Math.abs(ax);
        ay = Math.abs(ay);
        az = Math.abs(az);
        if (ax > ay) {
            if (ax > az) return 0;
        } else if (ay > az) return 1;
        else return 2;

        return -1;
    }

    public class LocListener implements LocationListener {


        @Override
        public void onLocationChanged(Location location) {
            loc = location;
            Log.d(TAG, "" + location);
            latitude = location.getLatitude();
            longitude = location.getLongitude();

        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }
    }

    public class mySensorListener implements SensorEventListener{

        @Override
        public void onSensorChanged(SensorEvent event) {
            getSensorChangedDetails(event);
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }
    }

}
