package com.example.admin.miplus.fragment;

import android.Manifest;
import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.IBinder;
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
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.admin.miplus.R;
import com.example.admin.miplus.Services.MapPositionService;
import com.example.admin.miplus.data_base.DataBaseRepository;
import com.example.admin.miplus.data_base.models.GeoData;
import com.example.admin.miplus.data_base.models.GeoSettings;
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
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

public class SecondFragment extends Fragment implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener, View.OnClickListener, MapPositionService.CallBack {
    private static final int LAYOUT = R.layout.second_activity;

    private GoogleMap mGoogleMap;
    private GoogleApiClient mGoogleApiClient;
    private Location location;
    private TextView checkGoogleServices;
    private LocationRequest mLocationRequest;

    private GeoSettings geoSettings = new GeoSettings();
    private GeoSettings geoSettingsM = new GeoSettings();
    private GeoSettings geoSettingsP = new GeoSettings();
    private SortedSet<GeoData> geoDataList = new TreeSet<GeoData>(getGeoComparator());
    private DataBaseRepository dataBaseRepository = new DataBaseRepository();
    public Polyline line;

    private MapPositionService mapPositionService;

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

        bindService();

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        //we add permissions we need to request location of the users
        permissions.add(Manifest.permission.ACCESS_FINE_LOCATION);
        permissions.add(Manifest.permission.ACCESS_COARSE_LOCATION);

        permissionsToRequest = permissionsToRequest(permissions);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (permissionsToRequest.size() > 0) {
                requestPermissions(permissionsToRequest.
                        toArray(new String[permissionsToRequest.size()]), ALL_PERMISSIONS_RESULT);
            }
        }

        /*Button btn;
        btn = (Button) view.findViewById(R.id.myLocationButton);
        btn.setOnClickListener(this);

        Button mapSettings;
        mapSettings = (Button) view.findViewById(R.id.mapSettingsButton);
        mapSettings.setOnClickListener(this);*/

        getDataFirebase();

        if (dataBaseRepository.getMapSettings() != null) {
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
        }

        return view;
    }

    @Override
    public void onClick(View v) {
        /*switch (v.getId()) {
            case R.id.myLocationButton:
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
        }*/

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
            addNewMarker(ll);
        }
    }

    private void addNewMarker(LatLng ll){
        CameraUpdate update = CameraUpdateFactory.newLatLngZoom(ll, 15);
        mGoogleMap.animateCamera(update);

        if (ll.latitude < 50.459827 && location.getLatitude() > 50.459787 && location.getLongitude() < 30.516161 && location.getLongitude() > 30.516121) {
            Log.d(">>>>>>", "+1");
        }
        else if(location.getLatitude() < 50.426616 && location.getLatitude() > 50.426576 && location.getLongitude() < 30.563069 && location.getLongitude() > 30.563029){
            Log.d(">>>>>>", "+1");
        }
        else if(location.getLatitude() < 50.449576 && location.getLatitude() > 50.449536 && location.getLongitude() < 30.525405 && location.getLongitude() > 30.525365){
            Log.d(">>>>>>", "+1");
        }
        else if(location.getLatitude() < 50.454489 && location.getLatitude() > 50.454449 && location.getLongitude() < 30.529983 && location.getLongitude() > 30.529943){
            Log.d(">>>>>>", "+1");
        }
        else if(location.getLatitude() < 50.448476 && location.getLatitude() > 50.448436 && location.getLongitude() < 30.537612 && location.getLongitude() > 30.537572){
            Log.d(">>>>>>", "+1");
        }
        else if(location.getLatitude() < 50.448860 && location.getLatitude() > 50.448820 && location.getLongitude() < 30.513348 && location.getLongitude() > 30.513308){
            Log.d(">>>>>>", "+1");
        }
        else if(location.getLatitude() < 50.452946 && location.getLatitude() > 50.452906 && location.getLongitude() < 30.514316 && location.getLongitude() > 30.514276){
            Log.d(">>>>>>", "+1");
        }
        else if(location.getLatitude() < 50.434675 && location.getLatitude() > 50.434635 && location.getLongitude() < 30.557255 && location.getLongitude() > 30.557215){
            Log.d(">>>>>>", "+1");
        }
        else if(location.getLatitude() < 50.414952 && location.getLatitude() > 50.414912 && location.getLongitude() < 30.562778 && location.getLongitude() > 30.562738){
            Log.d(">>>>>>", "+1");
        }
        else if(location.getLatitude() < 50.452114 && location.getLatitude() > 50.452074 && location.getLongitude() < 30.462330 && location.getLongitude() > 30.462290){
            Log.d(">>>>>>", "+1");
        }
        GeoData geoData = new GeoData();

        geoData.setUserPosition(location.getLatitude(), location.getLongitude());
        geoData.setDate(new Date());
        dataBaseRepository.setGeoData(geoData);

        geoDataList.add(geoData);

        if (dataBaseRepository.getMarkerColorFS() != null) {
            geoSettingsM = dataBaseRepository.getMarkerColorFS();
            getMarkerColorHere();
        } else {
            dataBaseRepository.getMarkerColorFSTask()
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            geoSettingsM = task.getResult().toObject(GeoSettings.class);
                            getMarkerColorHere();
                        }
                    });
        }
        redrawLine();
    }

    public BitmapDescriptor getMarkerIcon(String color) {
    float[] hsv = new float[3];
    Color.colorToHSV(Color.parseColor(color), hsv);
    return BitmapDescriptorFactory.defaultMarker(hsv[0]);}


    private void getDataFirebase() {

        dataBaseRepository.getGeoDataTask()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        GeoData geoData = new GeoData();
                        if (task.getResult() != null) {
                            Calendar cal = Calendar.getInstance();
                            cal.set(Calendar.HOUR_OF_DAY, 0);
                            cal.set(Calendar.MINUTE, 0);
                            cal.set(Calendar.SECOND, 0);
                            long cal2 = cal.getTime().getTime();
                            List<GeoData> a = task.getResult().toObjects(GeoData.class);
                            for (int u = 0; u < a.size(); u++) {
                                GeoData item = a.get(u);
                                if(item == null) continue;
                                if (item.getDate().getTime() > cal2) {
                                    geoDataList.add(item);
                                }
                            }
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
                if(o1 != null && o2 != null){
                    if(o1.getDate() != null && o2.getDate() != null){
                        return o1.getDate().compareTo(o2.getDate());
                    } else {
                        return 0;
                    }
                } else {
                    return 0;
                }
            }

        };
    }

    private void getMapType() {
        if (geoSettings != null && geoSettings.getMapType() != null){
        if (geoSettings.getMapType().equals(getString(R.string.map_type_normal))) {
            mGoogleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        } else if (geoSettings.getMapType().equals(getString(R.string.map_type_satellite))) {
            mGoogleMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
        } else if (geoSettings.getMapType().equals(getString(R.string.map_type_hybrid))) {
            mGoogleMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
        } else if (geoSettings.getMapType().equals(getString(R.string.map_type_terrain))) {
            mGoogleMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
        }
        }else{
            mGoogleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        }
    }

    private void getMarkerColorHere() {
        /*LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        if (geoSettingsM.getMarkerColor().equals(getString(R.string.marker_color_blue))) {
            MarkerOptions marker = new MarkerOptions().icon(bitmapDescriptorFromVector(getActivity(), R.drawable.ic_location_point_blue)).position(latLng);
            mGoogleMap.addMarker(marker);
        } else if (geoSettingsM.getMarkerColor().equals(getString(R.string.marker_color_red))) {
            MarkerOptions marker = new MarkerOptions().icon(bitmapDescriptorFromVector(getActivity(), R.drawable.ic_location_point_red)).position(latLng);
            mGoogleMap.addMarker(marker);
        } else if (geoSettingsM.getMarkerColor().equals(getString(R.string.marker_color_green))) {
            MarkerOptions marker = new MarkerOptions().icon(bitmapDescriptorFromVector(getActivity(), R.drawable.ic_location_point_green)).position(latLng);
            mGoogleMap.addMarker(marker);
        } else if (geoSettingsM.getMarkerColor().equals(getString(R.string.marker_color_black))) {
            MarkerOptions marker = new MarkerOptions().icon(bitmapDescriptorFromVector(getActivity(), R.drawable.ic_location_point_black)).position(latLng);
            mGoogleMap.addMarker(marker);
        }*/
    }

    private BitmapDescriptor bitmapDescriptorFromVector(Context context, int vectorResId) {
        Drawable vectorDrawable = ContextCompat.getDrawable(context, vectorResId);
        vectorDrawable.setBounds(0, 0, vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight());
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

    private void redrawLine() {
        Log.d(">>>>>>", "Drawing Line");
        mGoogleMap.clear();  //clears all Markers and Polylines

        PolylineOptions options = new PolylineOptions().width(10).color(Color.parseColor("#3f51b5")).geodesic(true);
        Iterator<GeoData> itr = geoDataList.iterator();

        while (itr.hasNext()) {
            GeoData a = itr.next();
            if (a != null)
            {
            }
            else {
                continue;
            }
            LatLng latLng = new LatLng(a.getLatitude(), a.getLongitude());
            options.add(latLng);
        }

        if(geoDataList.size() != 0  ){
            GeoData last = geoDataList.last();
            LatLng latLng = new LatLng(last.getLatitude(), last.getLongitude());
            MarkerOptions marker = new MarkerOptions().icon(bitmapDescriptorFromVector(getActivity(), R.drawable.ic_location_point_blue)).position(latLng);
            mGoogleMap.addMarker(marker);
        }

        mGoogleMap.addMarker(new MarkerOptions()
                .position(new LatLng(50.459807, 30.516141))
                .title("Andriyivskyy Descent")
                .icon(getMarkerIcon("#3f51b5")));

        mGoogleMap.addMarker(new MarkerOptions()
                .position(new LatLng(50.426596, 30.563049))
                .title("The Motherland Monument")
                .icon(getMarkerIcon("#3f51b5")));

        mGoogleMap.addMarker(new MarkerOptions()
                .position(new LatLng(50.449556, 30.525385))
                .title("Independence Square")
                .icon(getMarkerIcon("#3f51b5")));

        mGoogleMap.addMarker(new MarkerOptions()
                .position(new LatLng(50.454469, 30.529963))
                .title("People's Friendship Arch")
                .icon(getMarkerIcon("#3f51b5")));

        mGoogleMap.addMarker(new MarkerOptions()
                .position(new LatLng(50.448456, 30.537592))
                .title("Mariyinsky Palace")
                .icon(getMarkerIcon("#3f51b5")));

        mGoogleMap.addMarker(new MarkerOptions()
                .position(new LatLng(50.448840, 30.513328))
                .title("Golden Gate")
                .icon(getMarkerIcon("#3f51b5")));

        mGoogleMap.addMarker(new MarkerOptions()
                .position(new LatLng(50.452926, 30.514296))
                .title("Saint Sophia's Cathedral")
                .icon(getMarkerIcon("#3f51b5")));

        mGoogleMap.addMarker(new MarkerOptions()
                .position(new LatLng(50.434655, 30.557235))
                .title("Kiev Pechersk Lavra")
                .icon(getMarkerIcon("#3f51b5")));

        mGoogleMap.addMarker(new MarkerOptions()
                .position(new LatLng(50.414932, 30.562758))
                .title("Hryshko National Botanical Garden")
                .icon(getMarkerIcon("#3f51b5")));

        mGoogleMap.addMarker(new MarkerOptions()
                .position(new LatLng(50.452094, 30.462310))
                .title("The Zoo")
                .icon(getMarkerIcon("#3f51b5")));

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
                            return;
                        }
                        location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
                        startLocationUpdates();
                    }
                }
                break;
        }
    }

    private ServiceConnection connection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            MapPositionService.MyBinder binder = (MapPositionService.MyBinder) service;
            mapPositionService = binder.getService();
            mapPositionService.setCallBack(SecondFragment.this);
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            if (mapPositionService != null) mapPositionService.setCallBack(null);
        }
    };

    private void bindService() {
        Intent intent = new Intent(getActivity(), MapPositionService.class);
        getActivity().bindService(intent, connection, Context.BIND_AUTO_CREATE);
    }

    @Override
    public void setGeoposition(LatLng latLng) {
       addNewMarker(latLng);
    }
}