package com.example.admin.miplus.Services;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.os.Binder;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.example.admin.miplus.R;
import com.example.admin.miplus.activity.SplashActivity;
import com.example.admin.miplus.data_base.DataBaseRepository;
import com.example.admin.miplus.data_base.models.GeoData;
import com.example.admin.miplus.data_base.models.Profile;
import com.example.admin.miplus.data_base.models.StepsData;
import com.example.admin.miplus.fragment.SecondFragment;
import com.example.admin.miplus.pedometr.StepListener;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MapPositionService extends Service {//implements GoogleApiClient.ConnectionCallbacks, LocationListener {

    private MyBinder mLocalbinder = new MyBinder();
    private CallBack mCallBack;
    /*private DataBaseRepository dataBaseRepository = new DataBaseRepository();

    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private Location location;

    private final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private final long UPDATE_INTERVAL = 10000, FASTEST_INTERVAL = 10000; // = 10 seconds
    private final float SMALLEST_DISPLACEMENT = 10F; //10 meters

    private ArrayList<String> permissionsToRequest;
    private ArrayList<String> permissions = new ArrayList<>();
    private static final int ALL_PERMISSIONS_RESULT = 1011;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        permissions.add(Manifest.permission.ACCESS_FINE_LOCATION);
        permissions.add(Manifest.permission.ACCESS_COARSE_LOCATION);

        permissionsToRequest = permissionsToRequest(permissions);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (permissionsToRequest.size() > 0) {
                requestPermissions(permissionsToRequest.
                        toArray(new String[permissionsToRequest.size()]), ALL_PERMISSIONS_RESULT);
            }
        }
        return Service.START_STICKY;
    }

    private ArrayList<String> permissionsToRequest(ArrayList<String> wantedPermissions) {
        ArrayList<String> result = new ArrayList<>();

        for (String perm : wantedPermissions) {
            if (!hasPermission(perm)) {
                result.add(perm);
            }
        }
        return result;
    }

    private boolean hasPermission(String permission) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return this.checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED;
        }
        return true;
    }

    public void onGoogleApiClientConnected() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
        mGoogleApiClient.connect();

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.d(">>>>>>", "Connected");

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        //permission ok, we get last location
        location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        startLocationUpdates();
    }

    private void startLocationUpdates() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(UPDATE_INTERVAL);
        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);
        mLocationRequest.setSmallestDisplacement(SMALLEST_DISPLACEMENT);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
    }

    @Override
    public void onLocationChanged(final Location location) {
        LatLng ll = new LatLng(location.getLatitude(), location.getLongitude());

        GeoData geoData = new GeoData();

        geoData.setUserPosition(ll.latitude, ll.longitude);
        geoData.setDate(new Date());
        dataBaseRepository.setGeoData(geoData);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Log.d(">>>>>>", "Permissions");
        final ArrayList<String> permissionsRejected = new ArrayList<>();
        switch (requestCode) {
            case ALL_PERMISSIONS_RESULT:
                for (String perm : permissionsToRequest) {
                    if (!hasPermission(perm)) {
                        permissionsRejected.add(perm);
                    }
                }
                if (permissionsRejected.size() > 0) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (shouldShowRequestPermissionRationale(permissionsRejected.get(0))) {
                            new AlertDialog.Builder(this)
                                    .setMessage("These permissions are mandatory to get your location. You need to allow them.")
                                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                                requestPermissions(permissionsRejected.toArray(new String[permissionsRejected.size()]),
                                                        ALL_PERMISSIONS_RESULT);
                                            }
                                        }
                                    })
                                    .setNegativeButton("Cancel", null).create().show();
                            Toast.makeText(this, "Please, enable permissions to display location", Toast.LENGTH_LONG).show();
                            return;
                        }
                    }
                } else {
                    if (mGoogleApiClient != null) {
                        mGoogleApiClient.connect();
                        //permission ok, we get last location
                        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                            return;
                        }
                        location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
                        startLocationUpdates();
                    }
                }
                break;
        }
    }*/

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
    }

    @Nullable
    public IBinder onBind(Intent intent) {
        return mLocalbinder;
    }

    public class MyBinder extends Binder {
        public MapPositionService getService() {
            return MapPositionService.this;
        }
    }

    public void setCallBack(CallBack callBack) {
        mCallBack = callBack;
    }

    public interface CallBack {
        void setGeoposition(LatLng latLng);
    }
}


