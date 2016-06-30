package com.example.user.simpleui;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.directions.route.AbstractRouting;
import com.directions.route.Route;
import com.directions.route.RouteException;
import com.directions.route.Routing;
import com.directions.route.RoutingListener;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

public class OrderDetailActivity extends AppCompatActivity implements GeoCodingTask.GeoCodinTaskResponse, GoogleApiClient.ConnectionCallbacks, RoutingListener, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    TextView noteTextView;
    TextView menuResultsTextView;
    TextView storeInfoTextView;
    ImageView staticMap;

    MapFragment mapFragment;
    GoogleMap googleMap;
    GoogleApiClient googleApiClient;
    LocationRequest locationRequest;

    LatLng storeLocation;
    private ArrayList<Polyline> polylines;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_detail);

        Intent intent = getIntent();
        String note = intent.getStringExtra("note");
        String menuResults = intent.getStringExtra("menuResults");
        String storeInfo = intent.getStringExtra("storeInfo");

        noteTextView = (TextView) findViewById(R.id.noteTextView);
        storeInfoTextView = (TextView) findViewById(R.id.storeInfoTextView);
        menuResultsTextView = (TextView) findViewById(R.id.menuResultsTextView);
        staticMap = (ImageView) findViewById(R.id.imageView);
        mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.fragment);

        if (note != null)
            noteTextView.setText(note);

        if (storeInfo != null)
            storeInfoTextView.setText(storeInfo);

        String text = "";
        if (menuResults != null) {
            try {
                JSONArray jsonArray = new JSONArray(menuResults);
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject object = jsonArray.getJSONObject(i);
                    String lNumber = String.valueOf(object.getInt("lNumber"));
                    String mNumber = String.valueOf(object.getInt("mNumber"));
                    text += object.getString("drinkName") + " : 大杯 " + lNumber + "杯      中杯" + mNumber + "杯 \n";
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        menuResultsTextView.setText(text);

        String[] storeInfos = storeInfo.split(",");
        if (storeInfos != null && storeInfos.length > 1) {
            String address = storeInfos[1];
            (new GeoCodingTask(this)).execute(address);
        }

        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap map) {
                googleMap = map;
            }
        });
    }

    @Override
    public void reponseWithGeoCodingResults(LatLng latLng) {
        if (googleMap != null) {
//            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 17);
//            googleMap.animateCamera(cameraUpdate);
            googleMap.addMarker(new MarkerOptions().position(latLng));
            storeLocation = latLng;

            createGoogleAPIClient();
        }
    }

    private void createGoogleAPIClient() {
        if (googleApiClient == null) {
            googleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
            googleApiClient.connect();
        }
    }

    private void createLocationRequest() {
        if(locationRequest == null) {
            locationRequest = new LocationRequest();
            locationRequest.setInterval(1000);
            locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        }
    }

    @Override
    public void onConnected(Bundle bundle) {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 0);
            }
            return;
        }
        googleMap.setMyLocationEnabled(true);
        createLocationRequest();

        Location location = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
        LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this);

        LatLng start = new LatLng(25.0186348, 121.5398379);

        if(location != null) {
            start = new LatLng(location.getLatitude(), location.getLongitude());
        }
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(start, 17));
        Routing routing = new Routing.Builder()
                .travelMode(AbstractRouting.TravelMode.WALKING)
                .waypoints(start, storeLocation)
                .withListener(this)
                .build();
        routing.execute();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    protected void onStart() {
        super.onStart();
        if(googleApiClient != null)
            googleApiClient.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();
//        if(googleApiClient != null)
//            googleApiClient.disconnect();
    }

    @Override
    public void onRoutingFailure(RouteException e) {
        Log.d("Debug", e.toString());
    }

    @Override
    public void onRoutingStart() {

    }

    @Override
    public void onRoutingSuccess(ArrayList<Route> routes, int index) {
        if (polylines != null) {
            for (Polyline poly : polylines) {
                poly.remove();
            }
        }

        polylines = new ArrayList<>();
        //add route(s) to the map.
        for (int i = 0; i < routes.size(); i++) {

            //In case of more than 5 alternative routes

            PolylineOptions polyOptions = new PolylineOptions();
            polyOptions.color(Color.GREEN);
            polyOptions.width(10 + i * 3);
            polyOptions.addAll(routes.get(i).getPoints());
            Polyline polyline = googleMap.addPolyline(polyOptions);
            polylines.add(polyline);

//            Toast.makeText(getApplicationContext(),"Route "+ (i+1) +": distance - "+ routes.get(i).getDistanceValue()+": duration - "+ routes.get(i).getDurationValue(),Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void onRoutingCancelled() {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {
        LatLng currentLocation = new LatLng(location.getLatitude(), location.getLongitude());
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(currentLocation));
    }

    //
//    private static class GeoCodingTask extends AsyncTask<String, Void, Bitmap>{
//
//        WeakReference<ImageView> imageViewWeakReference;
//
//        @Override
//        protected Bitmap doInBackground(String... params) {
//            String address = params[0];
//            double[] latlng = Utils.getLatLngFromGoogleMapAPI(address);
//            if(latlng != null) {
//                Log.d("Debug", String.valueOf(latlng[0]));
//                Log.d("Debug", String.valueOf(latlng[1]));
//            }
//            return Utils.getStaticMap(latlng);
//        }
//
//        @Override
//        protected void onPostExecute(Bitmap bitmap) {
//            super.onPostExecute(bitmap);
//            if(imageViewWeakReference.get() != null)
//            {
//                ImageView imageView = imageViewWeakReference.get();
//                imageView.setImageBitmap(bitmap);
//            }
//        }
//
//        public GeoCodingTask(ImageView imageView){
//            this.imageViewWeakReference = new WeakReference<ImageView>(imageView);
//        }
//    }
}
