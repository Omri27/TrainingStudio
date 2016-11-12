package zina_eliran.app.Utils;


import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.widget.Toast;

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
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import zina_eliran.app.BusinessEntities.BETrainingLocation;


public class GoogleMapHandler implements
        OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationListener {

    GoogleApiClient mGoogleApiClient;
    GoogleMap mMap;
    LocationRequest mLocationRequest;
    LatLng mLastlatLng;
    BETrainingLocation trainingLocation;
    ArrayList<BETrainingLocation> trainingLocations;
    Marker mCurrLocationMarker;
    MapFragment mfMap;
    Context context;

    int interval = 1000;
    int fastInterval = 1000;
    boolean isDrawOnMap = false;
    boolean isDrawFirstTime = false;
    int i = 0;

    AppLocationChangedHandler activity;

    public GoogleMapHandler(Context context, MapFragment mfMap) {
        this.context = context;
        this.mfMap = mfMap;

        setEngLocale();
        mfMap.getMapAsync(this);
    }

    public GoogleMapHandler(Context context, MapFragment mfMap, BETrainingLocation trainingLocation) {
        this.context = context;
        this.mfMap = mfMap;
        this.trainingLocation = trainingLocation;

        setEngLocale();
        mfMap.getMapAsync(this);
    }

    public GoogleMapHandler(Context context, MapFragment mfMap, ArrayList<BETrainingLocation> trainingLocations) {
        this.context = context;
        this.mfMap = mfMap;
        this.trainingLocations = trainingLocations;
        this.isDrawOnMap = true;

        setEngLocale();
        mfMap.getMapAsync(this);
    }

    public GoogleMapHandler(Context context, AppLocationChangedHandler activity, MapFragment mfMap, BETrainingLocation startLocation, int interval, int fastInterval) {
        this(context, mfMap, startLocation);
        this.interval = interval;
        this.fastInterval = fastInterval;
        this.activity = activity;
    }


    @Override
    public void onConnected(@Nullable Bundle bundle) {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(interval);
        mLocationRequest.setFastestInterval(fastInterval);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        //check permissions
        if (ContextCompat.checkSelfPermission(context,
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }
    }

    @Override
    public void onLocationChanged(Location location) {

        if (activity != null) {
            if (!isDrawFirstTime) {

                if (mCurrLocationMarker != null) {
                    mCurrLocationMarker.remove();
                }
                //Place current location marker
                mLastlatLng = new LatLng(trainingLocation.getLatitude(), trainingLocation.getLongitude());
                mCurrLocationMarker = addMarker(mLastlatLng);
                isDrawFirstTime = true;
            }
            else {
                activity.onLocationChangedCallback(location);
                Toast.makeText(this.context, "i: " + (i++) + " | " +location.toString(), Toast.LENGTH_LONG).show();
                return;
            }

        } else {

            if (mCurrLocationMarker != null) {
                mCurrLocationMarker.remove();
            }

            if (this.trainingLocation != null) {
                //remove old markers
                mMap.clear();
                //Place current location marker
                mLastlatLng = new LatLng(trainingLocation.getLatitude(), trainingLocation.getLongitude());
                mCurrLocationMarker = addMarker(mLastlatLng);
            } else if (isDrawOnMap && trainingLocations.size() > 0) {
                List<LatLng> list = BETrainingLocation.getLatLngList(trainingLocations);
                drawOnMap(list);
                LatLng middleLocation = list.get((int) (list.size() / 2));
                mLastlatLng = new LatLng(middleLocation.latitude, middleLocation.longitude);
            } else {
                //Place current location marker
                mLastlatLng = new LatLng(location.getLatitude(), location.getLongitude());
                mCurrLocationMarker = addMarker(mLastlatLng);
            }
        }

        //move map camera
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(mLastlatLng, 16);
        mMap.animateCamera(cameraUpdate);

        //stop location updates
        if (mGoogleApiClient != null) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
        }

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        buildGoogleApiClient();
        if (ContextCompat.checkSelfPermission(context,
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED)
            mMap.setMyLocationEnabled(true);

    }

    @Override
    public void onConnectionSuspended(int i) {
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
    }

    private void setEngLocale() {
        //"force" LTR alignment + english language
        String languageToLoad = "en";
        Locale locale = new Locale(languageToLoad);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        this.context.getResources().updateConfiguration(config,
                this.context.getResources().getDisplayMetrics());
    }

    protected synchronized void buildGoogleApiClient() {
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(context)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }

        mGoogleApiClient.connect();
    }

    public Marker addMarker(LatLng latlng) {
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latlng);
        markerOptions.title(getAddressString(latlng));
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ROSE));
        return mMap.addMarker(markerOptions);
    }

    private String getAddressString(LatLng latLng) {
        Geocoder geocoder = new Geocoder(context, Locale.getDefault());
        List<Address> addresses = null;
        try {
            addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
        } catch (Exception ex) {
        }
        return addresses.get(0).getAddressLine(0);
    }

    public void stopListener() {
        mGoogleApiClient.disconnect();
    }

    public void drawOnMap(List<LatLng> list) {
        try {
            mMap.clear();
            Polyline line = mMap.addPolyline(new PolylineOptions()
                    .addAll(list)
                    .width(12)
                    .color(Color.parseColor("#05b1fb"))//Google maps blue color
                    .geodesic(true)
            );
        } catch (Exception ex) {
        }
    }

    public void drawNextLocationOnMap(LatLng l1, LatLng l2) {
        try {
            mMap.clear();
            Polyline line = mMap.addPolyline(new PolylineOptions()
                    .add(l1)
                    .add(l2)
                    .width(14)
                    .color(Color.parseColor("#ddFFA330"))//Google maps blue color
                    .geodesic(true)
            );
        } catch (Exception ex) {
        }
    }

}