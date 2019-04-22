package com.example.admin.miplus.fragment;

import android.Manifest;
import android.support.v4.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.admin.miplus.R;
import com.example.admin.miplus.data_base.DataBaseRepository;
import com.example.admin.miplus.data_base.models.GeoData;
import com.example.admin.miplus.data_base.models.GeoSettings;
import com.example.admin.miplus.data_base.models.Profile;
import com.example.admin.miplus.fragment.FirstWindow.MapSettingsFragment;
import com.example.admin.miplus.fragment.FirstWindow.StepsInformationFragment;
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
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

public class SecondFragment extends Fragment implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {
    private static final int LAYOUT = R.layout.second_activity;

    private GoogleMap mGoogleMap;
    private GoogleApiClient mGoogleApiClient;
    private Location location;
    private TextView checkGoogleServices;
    private LocationRequest mLocationRequest;
    private GeoData geoData = new GeoData();
    private GeoSettings geoSettings = new GeoSettings();
    private GeoSettings geoSettingsM = new GeoSettings();
    private GeoSettings geoSettingsP = new GeoSettings();
    private Set<GeoData> geoDataList = new TreeSet<GeoData>(getGeoComparator());
    private DataBaseRepository dataBaseRepository = new DataBaseRepository();
    public Polyline line;

    private final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private final long UPDATE_INTERVAL = 10000, FASTEST_INTERVAL = 10000; // = 10 seconds
    private final float SMALLEST_DISPLACEMENT = 10F; //10 meters

    // lists for permissions
    private ArrayList<String> permissionsToRequest;
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
        //creates and returns the view hierarchy associated with the fragment, call to create components inside fragment
        Log.d(">>>>>>", "OnCreateView");
        View view = inflater.inflate(LAYOUT, container, false);

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        //we add permissions we need to request location of the users
        permissions.add(Manifest.permission.ACCESS_FINE_LOCATION);
        permissions.add(Manifest.permission.ACCESS_COARSE_LOCATION);

        permissionsToRequest = permissionsToRequest(permissions);

      /*  Button btn;
        btn = (Button) view.findViewById(R.id.myLocationButton);
        btn.setOnClickListener(this);

        Button mapSettings;
        mapSettings = (Button) view.findViewById(R.id.mapSettingsButton);
        mapSettings.setOnClickListener(this);*/

        getDataFirebase();

     /*   if (dataBaseRepository.getMapSettings() != null) {
            geoSettings = dataBaseRepository.getMapSettings();
            getMapType();
        } else {
            dataBaseRepository.getMapSettingsTask()
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            geoSettings = task.getResult().toObject(GeoSettings.class);
                            getMapType();
                        }
                    });
        }*/

        return view;
    }

  /*  @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.myLocationButton:
                if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
                LatLng ll = new LatLng(location.getLatitude(), location.getLongitude());
                CameraUpdate update = CameraUpdateFactory.newLatLngZoom(ll, 15);
                mGoogleMap.animateCamera(update);
                break;

            case R.id.mapSettingsButton:
                MapSettingsFragment fragment = new MapSettingsFragment();
                FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.map_fragment, fragment);
                fragmentTransaction.commit();
                break;
        }

    }*/

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
            return getActivity().checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED;
        }
        return true;
    }

    public void onGoogleApiClientConnected() {
        mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
        mGoogleApiClient.connect();

    }

    @Override
    public void onStart() {
        // makes the fragment visible to the user
        super.onStart();

        if (mGoogleApiClient != null) {
            mGoogleApiClient.connect();
        }
    }

    @Override
    public void onResume() {
        //makes the fragment begin interacting with the user, calls after onStart
        super.onResume();

        if (!checkPlayServices()) {
            checkGoogleServices.setText("You need to install Google Play Services to use the App properly");
        }
    }

    private boolean checkPlayServices() {
        Log.d(">>>>>>", "CheckPlayServices");
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(getActivity());

        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                apiAvailability.getErrorDialog(getActivity(), resultCode, PLAY_SERVICES_RESOLUTION_REQUEST);
            }
            return false;
        }
        return true;
    }

    @Override
    public void onPause() {
        //fragment is no longer interacting with the user either because its activity is being paused
        super.onPause();

        //stop location updates
        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
            mGoogleApiClient.disconnect();
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        //Called immediately after onCreateView has returned, but before any saved state has been restored in to the view
        super.onViewCreated(view, savedInstanceState);

    }

    @Nullable
    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.d(">>>>>>", "MapReady");
        mGoogleMap = googleMap;
        mGoogleMap.getUiSettings().setCompassEnabled(false);
        onGoogleApiClientConnected();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.d(">>>>>>", "Connected");

        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(getActivity(), "You need to enable permissions to display location!", Toast.LENGTH_SHORT).show();
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

        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
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
    public void onLocationChanged(final Location location) {
        Log.d(">>>>>>", "Location Zordi");
        if (location == null) {
            Toast.makeText(getActivity(), "Can't get current location", Toast.LENGTH_LONG).show();

        } else {

            LatLng ll = new LatLng(location.getLatitude(), location.getLongitude());
            CameraUpdate update = CameraUpdateFactory.newLatLngZoom(ll, 15);
            mGoogleMap.animateCamera(update);

            MarkerOptions marker = new MarkerOptions().position(ll);
            mGoogleMap.addMarker(marker);

            geoData.setUserPosition(location.getLatitude(), location.getLongitude());
            geoData.setDate(new Date());
            dataBaseRepository.setGeoData(geoData);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (permissionsToRequest.size() > 0) {
                    requestPermissions(permissionsToRequest.
                            toArray(new String[permissionsToRequest.size()]), ALL_PERMISSIONS_RESULT);
                }
            }

            geoDataList.add(geoData);

            /*if (dataBaseRepository.getMarkerColorFS() != null) {
                geoSettingsM = dataBaseRepository.getMarkerColorFS();
                //getMarkerColorHere();
            } else {
                dataBaseRepository.getMarkerColorFSTask()
                        .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                geoSettingsM = task.getResult().toObject(GeoSettings.class);
                                //getMarkerColorHere();
                            }
                        });
            }*/

            redrawLine();

        }
    }

    private void getDataFirebase() {
        dataBaseRepository.getGeoDataTask()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.getResult() != null && !task.getResult().isEmpty()) {
                            geoDataList.addAll(task.getResult().toObjects(GeoData.class));
                            redrawLine();

                        } else {
                            LatLng ll = new LatLng(location.getLatitude(), location.getLongitude());
                            CameraUpdate update = CameraUpdateFactory.newLatLngZoom(ll, 15);
                            mGoogleMap.animateCamera(update);

                            geoData.setUserPosition(location.getLatitude(), location.getLongitude());
                            geoData.setDate(new Date());
                            dataBaseRepository.setGeoData(geoData);
                        }
                    }
                });
    }

    private Comparator<GeoData> getGeoComparator() {
        return new Comparator<GeoData>() {
            @Override
            public int compare(GeoData o1, GeoData o2) {
                return o1.getDate().compareTo(o2.getDate());
            }

        };
    }

  /*  private void getMapType() {
        if(geoSettings != null) {
            if (geoSettings.getMapType().equals(getString(R.string.map_type_normal))) {
                mGoogleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
            } else if (geoSettings.getMapType().equals(getString(R.string.map_type_satellite))) {
                mGoogleMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
            } else if (geoSettings.getMapType().equals(getString(R.string.map_type_hybrid))) {
                mGoogleMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
            } else if (geoSettings.getMapType().equals(getString(R.string.map_type_terrain))) {
                mGoogleMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
            }
        }
    }*/

    /*private void getMarkerColorHere(){
        LatLng ll = new LatLng(location.getLatitude(), location.getLongitude());
        if(geoSettingsM.getMarkerColor().equals(getString(R.string.marker_color_blue))) {
            MarkerOptions marker = new MarkerOptions().icon(bitmapDescriptorFromVector(getActivity(), R.drawable.ic_location_point_blue)).position(ll);
            mGoogleMap.addMarker(marker);
        }
        else if(geoSettingsM.getMarkerColor().equals(getString(R.string.marker_color_red))) {
            MarkerOptions marker = new MarkerOptions().icon(bitmapDescriptorFromVector(getActivity(), R.drawable.ic_location_point_red)).position(ll);
            mGoogleMap.addMarker(marker);
        }
        else if(geoSettingsM.getMarkerColor().equals(getString(R.string.marker_color_green))) {
            MarkerOptions marker = new MarkerOptions().icon(bitmapDescriptorFromVector(getActivity(), R.drawable.ic_location_point_green)).position(ll);
            mGoogleMap.addMarker(marker);
        }
        else if(geoSettingsM.getMarkerColor().equals(getString(R.string.marker_color_black))) {
            MarkerOptions marker = new MarkerOptions().icon(bitmapDescriptorFromVector(getActivity(), R.drawable.ic_location_point_black)).position(ll);
            mGoogleMap.addMarker(marker);
        }
    }*/

/*    private BitmapDescriptor bitmapDescriptorFromVector(Context context, int vectorResId) {
        Drawable vectorDrawable = ContextCompat.getDrawable(context, vectorResId);
        vectorDrawable.setBounds(0, 0, vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight());
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }*/

    private void redrawLine() {
        Log.d(">>>>>>", "Drawing Line");
        mGoogleMap.clear();  //clears all Markers and Polylines

        PolylineOptions options = new PolylineOptions().width(10).color(Color.parseColor("#3f51b5")).geodesic(true);
        Iterator<GeoData> itr = geoDataList.iterator();

        while (itr.hasNext()) {
            GeoData a = itr.next();
            Date date = new Date();
            if(a.getDate().getDate() == date.getDate()){
                LatLng latLng = new LatLng(a.getLatitude(), a.getLongitude());
                options.add(latLng);
            }

        }

        line = mGoogleMap.addPolyline(options); //add Polyline
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
                            new AlertDialog.Builder(getActivity())
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
                            Toast.makeText(getActivity(), "Please, enable permissions to display location", Toast.LENGTH_LONG).show();
                            return;
                        }
                    }
                } else {
                    if (mGoogleApiClient != null) {
                        mGoogleApiClient.connect();
                        //permission ok, we get last location
                        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                            // TODO: Consider calling
                            //    ActivityCompat#requestPermissions
                            // here to request the missing permissions, and then overriding
                            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                            //                                          int[] grantResults)
                            // to handle the case where the user grants the permission. See the documentation
                            // for ActivityCompat#requestPermissions for more details.
                            return;
                        }
                        location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
                        startLocationUpdates();
                    }
                }
                break;
        }
    }
}