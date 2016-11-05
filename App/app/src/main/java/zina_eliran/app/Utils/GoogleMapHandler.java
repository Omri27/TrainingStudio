package zina_eliran.app.Utils;


import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import zina_eliran.app.BusinessEntities.BETrainingLocation;


public class GoogleMapHandler implements
        OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationListener {


    final String TAG = "GoogleMapHandler";

    GoogleApiClient mGoogleApiClient;
    GoogleMap mMap;
    LocationRequest mLocationRequest;
    LatLng mLastlatLng;
    BETrainingLocation trainingLocation;
    ArrayList<BETrainingLocation> trainingLocations;
    Marker mCurrLocationMarker;
    MapFragment mfMap;
    Context context;

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

        setEngLocale();
        mfMap.getMapAsync(this);
    }

    private void setEngLocale(){
        //"force" LTR alignment + english language
        String languageToLoad = "en";
        Locale locale = new Locale(languageToLoad);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        this.context.getResources().updateConfiguration(config,
                this.context.getResources().getDisplayMetrics());
    }


    @Override
    public void onConnected(@Nullable Bundle bundle) {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        //check permissions
        if (ContextCompat.checkSelfPermission(context,
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);

        }
    }

    @Override
    public void onConnectionSuspended(int i) {
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
    }

    @Override
    public void onLocationChanged(Location location) {
        if (mCurrLocationMarker != null) {
            mCurrLocationMarker.remove();
        }

        if(this.trainingLocation == null){
            //Place current location marker
            mLastlatLng = new LatLng(location.getLatitude(), location.getLongitude());
        }
        else {
            //remove old markers
            mMap.clear();
            //Place current location marker
            mLastlatLng = new LatLng(trainingLocation.getLatitude(), trainingLocation.getLongitude());
        }

        mCurrLocationMarker = addMarker(mLastlatLng);

        //move map camera
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(mLastlatLng, 16);
        mMap.animateCamera(cameraUpdate);

        //stop location updates
        if (mGoogleApiClient != null) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
        }

    }

    public Marker addMarker(LatLng latlng) {
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latlng);
        markerOptions.title(getAddressString(latlng));
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ROSE));
        return mMap.addMarker(markerOptions);
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


    private String getAddressString(LatLng latLng) {
        Geocoder geocoder = new Geocoder(context, Locale.getDefault());
        List<Address> addresses = null;
        try {
            addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
        } catch (Exception ex) {
        }
        return addresses.get(0).getAddressLine(0);
    }


}