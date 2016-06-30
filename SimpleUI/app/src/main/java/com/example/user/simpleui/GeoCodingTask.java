package com.example.user.simpleui;

import android.os.AsyncTask;

import com.google.android.gms.maps.model.LatLng;

import java.lang.ref.WeakReference;

/**
 * Created by user on 2016/6/30.
 */
public class GeoCodingTask extends AsyncTask<String, Void, double[]> {
    WeakReference<GeoCodinTaskResponse> geoCodinTaskResponseWeakReference;

    @Override
    protected double[] doInBackground(String... params) {
        String address = params[0];
        double[] latlng = Utils.getLatLngFromGoogleMapAPI(address);
        return latlng;
    }

    @Override
    protected void onPostExecute(double[] doubles) {
        super.onPostExecute(doubles);
        if (geoCodinTaskResponseWeakReference.get() != null)
        {
            GeoCodinTaskResponse response = geoCodinTaskResponseWeakReference.get();
            response.reponseWithGeoCodingResults(new LatLng(doubles[0], doubles[1]));
        }
    }

    public GeoCodingTask(GeoCodinTaskResponse geoCodinTaskResponse){
        geoCodinTaskResponseWeakReference = new WeakReference<GeoCodinTaskResponse>(geoCodinTaskResponse);
    }

    interface GeoCodinTaskResponse{
        void reponseWithGeoCodingResults(LatLng latLng);
    }
}

