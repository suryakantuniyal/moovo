package in.innobins.customer;

import android.app.Activity;
import android.content.Context;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.util.List;

/**
 * Created by Abhishek on 8/7/2015.
 */
public class FusedLocation implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener, ResultCallback<LocationSettingsResult> {
    GoogleApiClient googleApiClient;
    LocationRequest locationRequest;
    Context context;
    LocationSettingsRequest mLocationSettingsRequest;
    public Location lastLocation;
    public LatLng lastLatLng;  // initialise the value with locality of the driver
    Activity activity;
    private static final int MY_PERMISSIONS_REQUEST_READ_FINE_LOCATION = 100;


    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 1000;
    protected static final int REQUEST_CHECK_SETTINGS = 0x1;

    FusedLocation(Context context) {

        this.context = context;
        lastLatLng = new LatLng(28.6129, 77.2293);
        buildGoogleApiClient();
        createLocationRequest();
        buildLocationSettingsRequest();
    }

    FusedLocation(Context context, Activity activity) {
        this.activity = activity;
        this.context = context;
        lastLatLng = new LatLng(28.6129, 77.2293);
        buildGoogleApiClient();
        createLocationRequest();
        buildLocationSettingsRequest();
        checkLocationSettings();

    }

    protected void checkLocationSettings() {
        PendingResult<LocationSettingsResult> result =
                LocationServices.SettingsApi.checkLocationSettings(
                        googleApiClient, mLocationSettingsRequest

                );

        result.setResultCallback(this);
    }


    protected synchronized void buildGoogleApiClient() {
        googleApiClient = new GoogleApiClient.Builder(context)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        googleApiClient.connect();
    }

    protected void buildLocationSettingsRequest() {
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(locationRequest);
        builder.setAlwaysShow(true);
        mLocationSettingsRequest = builder.build();
    }

    public boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    protected void createLocationRequest() {
        locationRequest = new LocationRequest();
        locationRequest.setInterval(5000);
        locationRequest.setFastestInterval(5000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

    }

    LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
            .addLocationRequest(locationRequest);


    protected void startLocationUpdates() {

        if (ActivityCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this);
    }

    public void stopLocationUpdates() {
        LocationServices.FusedLocationApi.removeLocationUpdates(
                googleApiClient, this);
        googleApiClient.disconnect();
    }

    @Override
    public void onConnected(Bundle bundle) {

        startLocationUpdates();
        if (ActivityCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        lastLocation = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
//        lastLatLng=new LatLng(lastLocation.getLatitude(),lastLocation.getLongitude());
//        if(lastLocation!=null){
//            Toast.makeText(context, "Lat: " + lastLocation.getLatitude() + "\nLong: " + lastLocation.getLongitude(), Toast.LENGTH_LONG).show();
//        }

    }

    @Override
    public void onConnectionSuspended(int i) {


    }
//
//    @Override
//    public void onResult(LocationSettingsResult locationSettingsResult) {}

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Toast.makeText(context, "Connection Suspended", Toast.LENGTH_LONG).show();
    }


    @Override
    public void onLocationChanged(Location location) {
        this.lastLocation = location;
        this.lastLatLng = new LatLng(location.getLatitude(), location.getLongitude());
        


        //This is being changed in new app
//        if(Tracking.isLoggedIn){
//            TrackingBodyMap.map.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lastLatLng.latitude, lastLatLng.longitude), 18.0f));
//        }
    }

    String getAddressText(LatLng latLng) {
        Geocoder geocoder = new Geocoder(context);
        double latitude = latLng.latitude;
        double longitude = latLng.longitude;

        List<Address> addresses = null;
        String addressText = "";

        try {
            addresses = geocoder.getFromLocation(latitude, longitude, 1);
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (addresses != null && addresses.size() > 0) {
            Address address = addresses.get(0);

            addressText = String.format("%s, %s, %s,%s",
                    address.getMaxAddressLineIndex() > 0 ? address.getAddressLine(0) : "",
                    address.getMaxAddressLineIndex() >= 1 ? address.getAddressLine(1) : "",
                    address.getLocality(),
                    address.getCountryName());
        }

        return addressText;
    }

    @Override
    public void onResult(LocationSettingsResult locationSettingsResult) {
        final Status status = locationSettingsResult.getStatus();
        switch (status.getStatusCode()) {
            case LocationSettingsStatusCodes.SUCCESS:
                Log.d("case1", String.valueOf(status.getStatusCode()));
                startLocationUpdates();
                break;
            case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                Log.d("case2", String.valueOf(status.getStatusCode()));
                Log.d("resolution req", "Location settings are not satisfied. Show the user a dialog to" +
                        "upgrade location settings ");

                try {
                    status.startResolutionForResult(activity, REQUEST_CHECK_SETTINGS);
                } catch (IntentSender.SendIntentException e) {
                    Log.i("", "PendingIntent unable to execute request.");
                }
                break;
            case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                Log.d("case3", String.valueOf(status.getStatusCode()));
                Log.d("case3", "Location settings are inadequate, and cannot be fixed here. Dialog " +
                        "not created.");
                break;
        }
    }

}