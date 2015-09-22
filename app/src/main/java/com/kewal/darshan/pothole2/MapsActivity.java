package com.kewal.darshan.pothole2;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.ClusterItem;
import com.google.maps.android.clustering.ClusterManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class MapsActivity extends FragmentActivity {

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    private ClusterManager<MyItem> mClusterManager;

    ArrayList<String> latitude = new ArrayList<>();
    ArrayList<String> longitude = new ArrayList<>();

    ArrayList<Double> latVal = new ArrayList<>();
    ArrayList<Double> lonVal = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        setUpMapIfNeeded();
        new getData().execute();

    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
    }

    /**
     * Sets up the map if it is possible to do so (i.e., the Google Play services APK is correctly
     * installed) and the map has not already been instantiated.. This will ensure that we only ever
     * call {@link #setUpMap()} once when {@link #mMap} is not null.
     * <p/>
     * If it isn't installed {@link SupportMapFragment} (and
     * {@link com.google.android.gms.maps.MapView MapView}) will show a prompt for the user to
     * install/update the Google Play services APK on their device.
     * <p/>
     * A user can return to this FragmentActivity after following the prompt and correctly
     * installing/updating/enabling the Google Play services. Since the FragmentActivity may not
     * have been completely destroyed during this process (it is likely that it would only be
     * stopped or paused), {@link #onCreate(Bundle)} may not be called again so we should call this
     * method in {@link #onResume()} to guarantee that it will be called.
     */
    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();
            // Check if we were successful in obtaining the map.
//            if (mMap != null) {
//                setUpMap();
//            }
        }
    }

    /**
     * This is where we can add markers or lines, add listeners or move the camera. In this case, we
     * just add a marker near Africa.
     * <p/>
     * This should only be called once and when we are sure that {@link #mMap} is not null.
     */
    private void setUpMap() {

        mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);

        mClusterManager = new ClusterManager<MyItem>(this, mMap);
        mMap.setOnCameraChangeListener(mClusterManager);

        for (int i = 0; i < latVal.size(); i++) {

//            mMap.addMarker(new MarkerOptions().position(new LatLng(latVal.get(i), lonVal.get(i))).title("Pothole"));

            MyItem offsetItem = new MyItem(latVal.get(i), lonVal.get(i));
            mClusterManager.addItem(offsetItem);


        }
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(12.978301, 77.571945), 14.0f));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(10), 2000, null);

//        mMap.addMarker(new MarkerOptions().position(new LatLng(0, 0)).title("Marker"));
    }


    public class getData extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            for (int j = 0; j < latitude.size(); j++) {
                latVal.add(Double.parseDouble(latitude.get(j)));
                lonVal.add(Double.parseDouble(longitude.get(j)));
            }
            setUpMap();
        }

        @Override
        protected Void doInBackground(Void... params) {

            try {
                StringBuffer output = new StringBuffer("");
                URL url = new URL("http://84.200.84.218/pothole/getloc.php");
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();
                InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                BufferedReader buffer = new BufferedReader(
                        new InputStreamReader(in));
                String s = "";
                while ((s = buffer.readLine()) != null)
                    output.append(s);
                String result = output.toString();
                Log.d("Darshanrohantesting", result);
                JSONObject object = new JSONObject(result);
                for (int i = 0; i < object.length(); i++) {
                    JSONObject locObject = object.getJSONObject("" + i);
                    String lon = locObject.getString("long");
                    String lat = locObject.getString("lat");
                    latitude.add(lat);
                    longitude.add(lon);

                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    public class MyItem implements ClusterItem {
        private final LatLng mPosition;

        public MyItem(double lat, double lng) {
            mPosition = new LatLng(lat, lng);
        }

        @Override
        public LatLng getPosition() {
            return mPosition;
        }
    }


}
