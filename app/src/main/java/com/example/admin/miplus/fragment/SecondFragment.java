package com.example.admin.miplus.fragment;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.admin.miplus.R;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;

public class SecondFragment extends Fragment implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {
    private static final int LAYOUT = R.layout.second_activity;
    private static final String CHANNEL_ID = "exampleServiceChannel";

    private GoogleMap mGoogleMap;
    private GoogleApiClient mGoogleApiClient;
    private Location location;
    private TextView checkGoogleServices;
    private LocationRequest mLocationRequest;
    private ArrayList<LatLng> points;
    public Polyline line;

    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private static final long UPDATE_INTERVAL = 30000, FASTEST_INTERVAL = 30000; // = 30 seconds
    private static final float SMALLEST_DISPLACEMENT = 10F; //10 meters

    // lists for permissions
    private ArrayList<String> permissionsToRequest;
    private ArrayList<String> permissionsRejected = new ArrayList<>();
    private ArrayList<String> permissions = new ArrayList<>();

    //integer for permissions result request
    private static final int ALL_PERMISSIONS_RESULT = 1011;


    public static SecondFragment getInstance() {
        Bundle args = new Bundle();
        SecondFragment fragment = new SecondFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d(">>>>>>", "OnCreateView");
        View view = inflater.inflate(LAYOUT, container, false);
        //we add permissions we need to request location of the users
        permissions.add(Manifest.permission.ACCESS_FINE_LOCATION);
        permissions.add(Manifest.permission.ACCESS_COARSE_LOCATION);

        permissionsToRequest = permissionsToRequest(permissions);

        points = new ArrayList<LatLng>();

        Button myButton = (Button) view.findViewById(R.id.location_settings);
        myButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS), 0);
            }
        });

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (permissionsToRequest.size() > 0) {
                requestPermissions(permissionsToRequest.
                        toArray(new String[permissionsToRequest.size()]), ALL_PERMISSIONS_RESULT);
                }
        }
        return view;
    }

    /*private void setPoint(LatLng latLng){
        mGoogleMap.addMarker(new MarkerOptions()
                .position(latLng)
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.google_icon)));
    }*/

    private ArrayList<String> permissionsToRequest(ArrayList<String> wantedPermissions){
        ArrayList<String> result = new ArrayList<>();

        for (String perm: wantedPermissions) {
            if(!hasPermission(perm)) {
                result.add(perm);
            }
        }
        return result;
    }

    private boolean hasPermission(String permission) {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            return getActivity().checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED;
        }
        return true;
    }

    @Override
    public void onStart() {
        super.onStart();

        if(mGoogleApiClient != null) {
            mGoogleApiClient.connect();
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        if(!checkPlayServices()){
            checkGoogleServices.setText("You need to install Google Play Services to use the App properly");
        }
    }

    private boolean checkPlayServices(){
        Log.d(">>>>>>", "CheckPlayServices");
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(getActivity());

        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                apiAvailability.getErrorDialog(getActivity(), resultCode, PLAY_SERVICES_RESOLUTION_REQUEST);
            } else {
                finish();
            }return false;
        } return true;
    }

    @Override
    public void onPause() {
        super.onPause();

        //stop location updates
        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()){
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
            mGoogleApiClient.disconnect();
        }
    }
    private void finish() {
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Nullable
    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.d(">>>>>>", "MapReady");
        mGoogleMap = googleMap;
        mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.d(">>>>>>", "Connected");
        mLocationRequest = LocationRequest.create();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(1000);

        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        //permission ok, we get last location
        location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

        if (location != null) {
        }
        startLocationUpdates();
    }

    private void startLocationUpdates() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(UPDATE_INTERVAL);
        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);
        mLocationRequest.setSmallestDisplacement(SMALLEST_DISPLACEMENT);

        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            Toast.makeText(getActivity(), "You need to enable permissions to display location!", Toast.LENGTH_SHORT).show();
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {
        Log.d(">>>>>>", "Location Zordi");
        if (location == null){
    Toast.makeText(getActivity(), "Can't get current location", Toast.LENGTH_LONG).show();

    }else{

    LatLng ll = new LatLng(location.getLatitude(), location.getLongitude());
    CameraUpdate update = CameraUpdateFactory.newLatLngZoom(ll, 15);
    mGoogleMap.animateCamera(update);

            double latitude = location.getLatitude();
            double longitude = location.getLongitude();
            LatLng latLng = new LatLng(latitude, longitude);

            points.add(latLng);

            redrawLine(latLng);
        }
    }

    private void redrawLine(LatLng latLng) {
        Log.d(">>>>>>", "Drawing Line");
        mGoogleMap.clear();  //clears all Markers and Polylines

        PolylineOptions options = new PolylineOptions().width(10).color(Color.parseColor("#3f51b5")).geodesic(true);
        for (int i = 0; i < points.size(); i++) {
            LatLng point = points.get(i);
            options.add(point);
        }

        mGoogleMap.addMarker(new MarkerOptions()
                .position(latLng)
                .icon(BitmapDescriptorFactory
                .defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
        line = mGoogleMap.addPolyline(options); //add Polyline
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Log.d(">>>>>>", "Permissions");
        switch(requestCode){
            case ALL_PERMISSIONS_RESULT:
            for (String perm : permissionsToRequest){
                if(!hasPermission(perm)){
                    permissionsRejected.add(perm);
                }
            }

            if (permissionsRejected.size()> 0){
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                    if (shouldShowRequestPermissionRationale(permissionsRejected.get(0))){
                        new AlertDialog.Builder(getActivity())
                                .setMessage("These permissions are mandatory to get your location. You need to allow them.")
                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                                            requestPermissions(permissionsRejected.toArray(new String[permissionsRejected.size()]),
                                            ALL_PERMISSIONS_RESULT);
                                        }
                                    }
                                })
                                .setNegativeButton("Cancel", null).create().show();
                        Toast.makeText(getActivity(), "Please, enable permissions to display location", Toast.LENGTH_LONG).show();
                        return;
                    }
                }
            } else {
                if (mGoogleApiClient != null){
                    mGoogleApiClient.connect();
                }
            }
            break;
        }
    }



}