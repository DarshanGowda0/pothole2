package com.kewal.darshan.pothole2;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by darshan on 22/09/15.
 */
public class SendDataReciever extends BroadcastReceiver
{

    BufferedReader mBufferedInputStream;
    String Response = "",KEY="loc";

    @Override
    public void onReceive(Context context, Intent intent) {

        final ConnectivityManager connMgr = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);

        final android.net.NetworkInfo wifi = connMgr
                .getNetworkInfo(ConnectivityManager.TYPE_WIFI);

        final android.net.NetworkInfo mobile = connMgr
                .getNetworkInfo(ConnectivityManager.TYPE_MOBILE);


        if (wifi.isAvailable()) {
            // Do something

            Log.d("ROHAN", "Connected");
            String read = readFile(context);
            read = "["+read+"]";
            if(read!=null){
             sendData(read,context);
            }
        } else {

            Log.d("ROHAN", "Not Connected");
        }

    }

    private void sendData(final String read, final Context context) {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {

                Log.d("ROHAN","sending data : \n"+read);

                // add URL

                String urlString = "http://84.200.84.218/pothole/putlocj.php";
                URL url = null;
                try {
                    url = new URL(urlString);
                    HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();

                    httpURLConnection.setConnectTimeout(15000);
                    httpURLConnection.setReadTimeout(10000);
                    httpURLConnection.setDoInput(true);
                    httpURLConnection.setDoOutput(true);

                    Uri.Builder builder = new Uri.Builder()
                            .appendQueryParameter(KEY, read);

                    String query = builder.build().getEncodedQuery();

                    OutputStream os = httpURLConnection.getOutputStream();

                    BufferedWriter mBufferedWriter = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
                    mBufferedWriter.write(query);
                    mBufferedWriter.flush();
                    mBufferedWriter.close();
                    os.close();

                    httpURLConnection.connect();


                    if (httpURLConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {

                        mBufferedInputStream = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));
                        String inline;
                        while ((inline = mBufferedInputStream.readLine()) != null) {
                            Response += inline;
                        }
                        mBufferedInputStream.close();

                        Log.d("ROHAN", "" + Response);

                        deleteFile(context);


                    } else {
                        Log.d("darshan", "something wrong");

                    }


                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }


            }
        });

        thread.start();
    }

    private String readFile(Context context) {
        String ret = null;
        try {
            InputStream inputStream = context.
                    openFileInput(MotionDetectService.FILENAME);

            if (inputStream != null) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString = "";
                StringBuilder stringBuilder = new StringBuilder();

                while ((receiveString = bufferedReader.readLine()) != null) {
                    stringBuilder.append(receiveString);
                }

                inputStream.close();
                ret = stringBuilder.toString();
                return ret;
            }
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return ret;
    }

    private boolean deleteFile(Context context){
        boolean deleted=false;

        try {
            File dir = context.getFilesDir();
            File file = new File(dir, MotionDetectService.FILENAME);
            deleted = file.delete();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return deleted;

    }




}
