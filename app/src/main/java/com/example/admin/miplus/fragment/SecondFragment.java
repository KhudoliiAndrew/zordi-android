package com.example.admin.miplus.fragment;

import android.Manifest;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.speech.tts.TextToSpeech;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.admin.miplus.R;
import com.example.admin.miplus.Services.MapPositionService;
import com.example.admin.miplus.data_base.DataBaseRepository;
import com.example.admin.miplus.data_base.models.CheckPoint;
import com.example.admin.miplus.data_base.models.GeoData;
import com.example.admin.miplus.data_base.models.Profile;
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
import com.google.android.gms.maps.model.Marker;
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
import java.util.Locale;
import java.util.Objects;
import java.util.SortedSet;
import java.util.TreeSet;

public class SecondFragment extends Fragment implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener, View.OnClickListener, MapPositionService.CallBack {
    private static final int LAYOUT = R.layout.second_activity;

    private GoogleMap mGoogleMap;
    private GoogleApiClient mGoogleApiClient;
    private Location location;
    private TextView checkGoogleServices;
    private LocationRequest mLocationRequest;

  //  private GeoSettings geoSettings = new GeoSettings();
  //  private GeoSettings geoSettingsM = new GeoSettings();
  //  private GeoSettings geoSettingsP = new GeoSettings();
    private SortedSet<GeoData> geoDataList = new TreeSet<GeoData>(getGeoComparator());

    private DataBaseRepository dataBaseRepository = new DataBaseRepository();
    private List<CheckPoint> checkPointsList = new ArrayList<CheckPoint>();
    CheckPoint checkPoint = new CheckPoint();

    public Polyline line;

    private MapPositionService mapPositionService;

    // lists for permissions
    private ArrayList<String> permissionsToRequest;
    private ArrayList<String> permissions = new ArrayList<>();

    //integer for permissions result request
    private static final int ALL_PERMISSIONS_RESULT = 1011;

    private TextToSpeech textSay;
    private Profile profile = new Profile();

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

        textToSpeech();
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


        if (dataBaseRepository.getCheckPointTask() != null) {
            dataBaseRepository.getCheckPointTask()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            checkPointsList = Objects.requireNonNull(task.getResult()).toObjects(CheckPoint.class);
                            Date date = new Date();
                            if(checkPointsList.size() == 0){
                                for (int p = 0; p < 10; p++) {
                                    if (p >= checkPointsList.size()) {
                                        checkPoint.setNum(p + 1);
                                        checkPoint.setDefaultInstance();
                                        checkPointsList.add(checkPoint);
                                        dataBaseRepository.setCheckPoint(checkPoint);
                                        redrawLine();
                                    }
                                }
                            } else {
                                checkPoint = checkPointsList.get(checkPointsList.size() - 1);
                                if (checkPoint.getDate().getDate() != date.getDate()) {
                                    for (int p = 0; p < 10; p++) {
                                        checkPoint.setNum(p + 1);
                                        checkPoint.setDefaultInstance();
                                        checkPointsList.add(checkPoint);
                                        dataBaseRepository.setCheckPoint(checkPoint);
                                        redrawLine();
                                    }
                                } else{
                                    for (int p = 0; p < checkPointsList.size(); p++) {
                                        checkPoint = checkPointsList.get(p);
                                        redrawLine();
                                    }
                                }
                            }
                            Log.d(">>>>>>", "Boy");
                        }
                    });
        }

        if (dataBaseRepository.getProfile() != null) {
            profile = dataBaseRepository.getProfile();
            Date date = new Date();
            TextView textView = Objects.requireNonNull(getView()).findViewById(R.id.checkpoints_counter_text);
            textView.setText(String.valueOf(profile.getCheckpoints()));
            if (profile.getCheckPointsDate().getDate() != date.getDate()) {
                profile.setCheckpoints(0);
                profile.setCheckPointsDate(date);
                dataBaseRepository.setProfile(profile);
                textView.setText(String.valueOf(profile.getCheckpoints()));
            }
        } else {
            dataBaseRepository.getProfileTask()
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            profile = Objects.requireNonNull(task.getResult()).toObject(Profile.class);
                            TextView textView = Objects.requireNonNull(getView()).findViewById(R.id.checkpoints_counter_text);
                            textView.setText(String.valueOf(profile.getCheckpoints()));
                            Date date = new Date();
                            if (profile.getCheckPointsDate().getDate() != date.getDate()) {
                                profile.setCheckpoints(0);
                                profile.setCheckPointsDate(date);
                                dataBaseRepository.setProfile(profile);
                                textView.setText(String.valueOf(profile.getCheckpoints()));

                            }
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
            return Objects.requireNonNull(getActivity()).checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED;
        }
        return true;
    }

    public void onGoogleApiClientConnected() {
        mGoogleApiClient = new GoogleApiClient.Builder(Objects.requireNonNull(getActivity()))
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
        getDataFirebase();
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
                int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
                apiAvailability.getErrorDialog(getActivity(), resultCode, PLAY_SERVICES_RESOLUTION_REQUEST);
            }
            return false;
        }
        return true;
    }

    @Override
    public void onPause() {
        if (textSay != null) {
            textSay.stop();
            textSay.shutdown();
        }
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

    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.d(">>>>>>", "MapReady");
        mGoogleMap = googleMap;
        mGoogleMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
            @Override
            public View getInfoWindow(Marker arg0) {
                return null;
            }

            @Override
            public View getInfoContents(Marker marker) {

                LinearLayout info = new LinearLayout(getContext());
                info.setOrientation(LinearLayout.VERTICAL);

                TextView title = new TextView(getContext());
                title.setTextColor(Color.BLACK);
                title.setGravity(Gravity.CENTER);
                title.setTypeface(null, Typeface.BOLD);
                title.setText(marker.getTitle());

                TextView snippet = new TextView(getContext());
                snippet.setTextColor(Color.GRAY);
                snippet.setText(marker.getSnippet());

                info.addView(title);
                info.addView(snippet);

                return info;
            }
        });

        mGoogleMap.getUiSettings().setCompassEnabled(false);
        onGoogleApiClientConnected();

    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.d(">>>>>>", "Connected");

        if (ActivityCompat.checkSelfPermission(Objects.requireNonNull(getActivity()), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
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
        long UPDATE_INTERVAL = 10000;
        mLocationRequest.setInterval(UPDATE_INTERVAL);
        // = 10 seconds
        long FASTEST_INTERVAL = 10000;
        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);
        //10 meters
        float SMALLEST_DISPLACEMENT = 10F;
        mLocationRequest.setSmallestDisplacement(SMALLEST_DISPLACEMENT);

        if (ActivityCompat.checkSelfPermission(Objects.requireNonNull(getActivity()), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        if(mGoogleApiClient.isConnected()){
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
    public void onLocationChanged(final Location location) {
        if (location == null) {
            Toast.makeText(getActivity(), "Can't get current location", Toast.LENGTH_LONG).show();
        } else {

            LatLng ll = new LatLng(location.getLatitude(), location.getLongitude());

            addNewMarker(ll);
        }
    }

    private void getDataFirebase() {
        if(dataBaseRepository.getGeoDataTask() != null){
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

    /*private void getMapType() {
        *//*if (geoSettings != null && geoSettings.getMapType() != null){
            if (geoSettings.getMapType().equals(getString(R.string.map_type_normal))) {
                mGoogleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
            } else if (geoSettings.getMapType().equals(getString(R.string.map_type_satellite))) {
                mGoogleMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
            } else if (geoSettings.getMapType().equals(getString(R.string.map_type_hybrid))) {
                mGoogleMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
            } else if (geoSettings.getMapType().equals(getString(R.string.map_type_terrain))) {
                mGoogleMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
            }
        }else{*//*
            mGoogleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        //}
    }*/

    private String getCheckPointsColor(CheckPoint checkPoint) {
        if (checkPoint.getGone()) {
            return "#4D5656";
        } else {
            return "#3f51b5";
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
        vectorDrawable.setBounds(0, 0, Objects.requireNonNull(vectorDrawable).getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight());
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

    private void redrawLine() {
        Log.d(">>>>>>", "Drawing Line");
        mGoogleMap.clear();  //clears all Markers and Polylines

        PolylineOptions options = new PolylineOptions().width(10).color(Color.parseColor("#3f51b5")).geodesic(true);

        for (GeoData a : geoDataList) {
            if (a != null) {
            } else {
                continue;
            }
            LatLng latLng = new LatLng(a.getLatitude(), a.getLongitude());
            options.add(latLng);
        }

        if(geoDataList.size() != 0  ){
            GeoData last = geoDataList.last();
            LatLng latLng = new LatLng(last.getLatitude(), last.getLongitude());
            MarkerOptions marker = new MarkerOptions().icon(bitmapDescriptorFromVector(getActivity(), R.drawable.ic_location_point_blue)).position(latLng).title("My position").snippet("    I'm here");
            mGoogleMap.addMarker(marker);
        }

        for (int c = 0; c < checkPointsList.size(); c++) {
            checkPoint = checkPointsList.get(c);
            switch (checkPoint.getNum()) {
                case 1:
                    mGoogleMap.addMarker(new MarkerOptions()
                            .position(new LatLng(50.459807, 30.516141))
                            .title("Andriyivskyy Descent")
                            .icon(getMarkerIcon(getCheckPointsColor(checkPoint)))
                            .snippet(" Historic descent connecting \n Kiev's Upper Town neighborhood \n and the historically commercial \n Podil neighborhood"));
                    break;
                case 2:
                    mGoogleMap.addMarker(new MarkerOptions()
                            .position(new LatLng(50.426596, 30.563049))
                            .title("The Motherland Monument")
                            .icon(getMarkerIcon(getCheckPointsColor(checkPoint)))
                            .snippet(" Monumental statue in Kiev. \n The sculpture is a part of the Museum of \n The History of Ukraine in World War II"));
                    break;
                case 3:
                    mGoogleMap.addMarker(new MarkerOptions()
                            .position(new LatLng(50.449556, 30.525385))
                            .title("Independence Square")
                            .icon(getMarkerIcon(getCheckPointsColor(checkPoint)))
                            .snippet(" Central square of Kiev. One of the city's main squares, \n it is located on Khreshchatyk Street in the Shevchenko District. \n The square has been known under many different names, \n but often it is called simply Maidan"));
                    break;
                case 4:
                    mGoogleMap.addMarker(new MarkerOptions()
                            .position(new LatLng(50.454469, 30.529963))
                            .title("People's Friendship Arch")
                            .icon(getMarkerIcon(getCheckPointsColor(checkPoint)))
                            .snippet(" Monument in Kiev. It was opened together with\n Ukrainian House to commemorate the 60th \n Anniversary of the USSR and the celebration\n of the 1,500th Anniversary of the Kiev city"));
                    break;
                case 5:
                    mGoogleMap.addMarker(new MarkerOptions()
                            .position(new LatLng(50.448456, 30.537592))
                            .title("Mariyinsky Palace")
                            .icon(getMarkerIcon(getCheckPointsColor(checkPoint)))
                            .snippet(" Official ceremonial residence of \n the President of Ukraine in Kiev and \n adjoins the neo-classical building \n of the Verkhovna Rada of Ukraine"));
                    break;
                case 6:
                    mGoogleMap.addMarker(new MarkerOptions()
                            .position(new LatLng(50.448840, 30.513328))
                            .title("Golden Gate")
                            .icon(getMarkerIcon(getCheckPointsColor(checkPoint)))
                            .snippet(" Main gate in the 11th century fortifications of Kiev, \n the capital of Kievan Rus'. Modern history accepts this\n gateway as one of three constructed by Yaroslav the Wise"));
                    break;
                case 7:
                    mGoogleMap.addMarker(new MarkerOptions()
                            .position(new LatLng(50.452926, 30.514296))
                            .title("Saint Sophia's Cathedral")
                            .icon(getMarkerIcon(getCheckPointsColor(checkPoint)))
                            .snippet(" Cathedral is one of the city's best known landmarks and \n the first heritage site in Ukraine to be inscribed on the\n World Heritage List along with the Kiev Cave Monastery\n complex. Cathedral is also known as Sobor Sviatoyi Sofiyi"));
                    break;
                case 8:
                    mGoogleMap.addMarker(new MarkerOptions()
                            .position(new LatLng(50.434655, 30.557235))
                            .title("Kiev Pechersk Lavra")
                            .icon(getMarkerIcon(getCheckPointsColor(checkPoint)))
                            .snippet(" Also known as the Kiev Monastery of the Caves, \n is a historic Orthodox Christian monastery which gave its  \n name to one of the city districts where it is located in Kiev"));
                    break;
                case 9:
                    mGoogleMap.addMarker(new MarkerOptions()
                            .position(new LatLng(50.414932, 30.562758))
                            .title("Hryshko National Botanical Garden")
                            .icon(getMarkerIcon(getCheckPointsColor(checkPoint)))
                            .snippet("  Botanical garden of the National Academy of Sciences of\n  Ukraine. Founded in 1936, the garden covers 1.3 km²\n  (120 hectares) and contains 13,000 types of trees, shrubs, \n  flowers and other plants from all over the world"));
                    break;
                case 10:
                    mGoogleMap.addMarker(new MarkerOptions()
                            .position(new LatLng(50.452094, 30.462310))
                            .title("The Zoo")
                            .icon(getMarkerIcon(getCheckPointsColor(checkPoint)))
                            .snippet(" One of the largest zoos in the former Soviet Union \n and the only large zoo in Kiev. Situated on about 40\n hectares (99 acres), the zoo is cared for by 378 staff\n members and receives about 280,000 visitors annually"));
                    break;
            }
        }
        line = mGoogleMap.addPolyline(options); //add Polyline
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Log.d(">>>>>>", "Permissions");
        final ArrayList<String> permissionsRejected = new ArrayList<>();
        if (requestCode == ALL_PERMISSIONS_RESULT) {
            for (String perm : permissionsToRequest) {
                if (!hasPermission(perm)) {
                    permissionsRejected.add(perm);
                }
            }
            if (permissionsRejected.size() > 0) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (shouldShowRequestPermissionRationale(permissionsRejected.get(0))) {
                        new AlertDialog.Builder(Objects.requireNonNull(getActivity()))
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
                    }
                }
            } else {
                if (mGoogleApiClient != null) {
                    mGoogleApiClient.connect();
                    //permission ok, we get last location
                    if (ActivityCompat.checkSelfPermission(Objects.requireNonNull(getActivity()), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        return;
                    }
                    location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
                    startLocationUpdates();
                }
            }
        }
    }

    private void addNewMarker(LatLng ll) {
        CameraUpdate update = CameraUpdateFactory.newLatLngZoom(ll, 15);
        mGoogleMap.animateCamera(update);

        if (checkPointsList.size() == 10) {
            TextView textView = Objects.requireNonNull(getActivity()).findViewById(R.id.checkpoints_counter_text);
            if (ll.latitude < 50.4601 && ll.latitude > 50.4595 && ll.longitude < 30.5164 && ll.longitude > 30.5158 && !checkPointsList.get(0).getGone()) {
                Log.d(">>>>>>", "+1");
                profile.setCheckpoints(profile.getCheckpoints() + 1);
                dataBaseRepository.setProfile(profile);
                textView.setText(String.valueOf(profile.getCheckpoints()));
                checkPoint.setNum(1);
                checkPoint.setGone(true);
                checkPoint.setDate(new Date());
                dataBaseRepository.setCheckPoint(checkPoint);
                checkPointsList.get(0).setGone(true);
            } else if (ll.latitude < 50.4268 && ll.latitude > 50.4262 && ll.longitude < 30.5633 && ll.longitude > 30.5627) {
                Log.d(">>>>>>", "+1");
                profile.setCheckpoints(profile.getCheckpoints() + 1);
                dataBaseRepository.setProfile(profile);
                textView.setText(String.valueOf(profile.getCheckpoints()));
                checkPoint.setNum(2);
                checkPoint.setGone(true);
                checkPoint.setDate(new Date());
                dataBaseRepository.setCheckPoint(checkPoint);
                checkPointsList.get(1).setGone(true);
            } else if (ll.latitude < 50.4498 && ll.latitude > 50.4492 && ll.longitude < 30.5256 && ll.longitude > 30.5250) {
                Log.d(">>>>>>", "+1");
                profile.setCheckpoints(profile.getCheckpoints() + 1);
                dataBaseRepository.setProfile(profile);
                textView.setText(String.valueOf(profile.getCheckpoints()));
                checkPoint.setNum(3);
                checkPoint.setGone(true);
                checkPoint.setDate(new Date());
                dataBaseRepository.setCheckPoint(checkPoint);
                checkPointsList.get(2).setGone(true);
            } else if (ll.latitude < 50.4547 && ll.latitude > 50.4541 && ll.longitude < 30.5302 && ll.longitude > 30.5296) {
                Log.d(">>>>>>", "+1");
                profile.setCheckpoints(profile.getCheckpoints() + 1);
                dataBaseRepository.setProfile(profile);
                textView.setText(String.valueOf(profile.getCheckpoints()));
                checkPoint.setNum(4);
                checkPoint.setGone(true);
                checkPoint.setDate(new Date());
                dataBaseRepository.setCheckPoint(checkPoint);
                checkPointsList.get(3).setGone(true);
            } else if (ll.latitude < 50.4487 && ll.latitude > 50.4481 && ll.longitude < 30.5378 && ll.longitude > 30.5372) {
                Log.d(">>>>>>", "+1");
                profile.setCheckpoints(profile.getCheckpoints() + 1);
                dataBaseRepository.setProfile(profile);
                textView.setText(String.valueOf(profile.getCheckpoints()));
                checkPoint.setNum(5);
                checkPoint.setGone(true);
                checkPoint.setDate(new Date());
                dataBaseRepository.setCheckPoint(checkPoint);
                checkPointsList.get(4).setGone(true);
            } else if (ll.latitude < 50.4491 && ll.latitude > 50.4485 && ll.longitude < 30.5136 && ll.longitude > 30.5130) {
                Log.d(">>>>>>", "+1");
                profile.setCheckpoints(profile.getCheckpoints() + 1);
                dataBaseRepository.setProfile(profile);
                textView.setText(String.valueOf(profile.getCheckpoints()));
                checkPoint.setNum(6);
                checkPoint.setGone(true);
                checkPoint.setDate(new Date());
                dataBaseRepository.setCheckPoint(checkPoint);
                checkPointsList.get(5).setGone(true);
            } else if (ll.latitude < 50.4532 && ll.latitude > 50.4526 && ll.longitude < 30.5145 && ll.longitude > 30.5139) {
                Log.d(">>>>>>", "+1");
                profile.setCheckpoints(profile.getCheckpoints() + 1);
                dataBaseRepository.setProfile(profile);
                textView.setText(String.valueOf(profile.getCheckpoints()));
                checkPoint.setNum(7);
                checkPoint.setGone(true);
                checkPoint.setDate(new Date());
                dataBaseRepository.setCheckPoint(checkPoint);
                checkPointsList.get(6).setGone(true);
            } else if (ll.latitude < 50.4349 && ll.latitude > 50.4343 && ll.longitude < 30.5575 && ll.longitude > 30.5569) {
                Log.d(">>>>>>", "+1");
                profile.setCheckpoints(profile.getCheckpoints() + 1);
                dataBaseRepository.setProfile(profile);
                textView.setText(String.valueOf(profile.getCheckpoints()));
                checkPoint.setNum(8);
                checkPoint.setGone(true);
                checkPoint.setDate(new Date());
                dataBaseRepository.setCheckPoint(checkPoint);
                checkPointsList.get(7).setGone(true);
            } else if (ll.latitude < 50.4152 && ll.latitude > 50.4146 && ll.longitude < 30.5630 && ll.longitude > 30.5624) {
                Log.d(">>>>>>", "+1");
                profile.setCheckpoints(profile.getCheckpoints() + 1);
                dataBaseRepository.setProfile(profile);
                textView.setText(String.valueOf(profile.getCheckpoints()));
                checkPoint.setNum(9);
                checkPoint.setGone(true);
                checkPoint.setDate(new Date());
                dataBaseRepository.setCheckPoint(checkPoint);
                checkPointsList.get(8).setGone(true);
            } else if (ll.latitude < 50.4523 && ll.latitude > 50.4517 && ll.longitude < 30.4626 && ll.longitude > 30.4620) {
                Log.d(">>>>>>", "+1");
                profile.setCheckpoints(profile.getCheckpoints() + 1);
                dataBaseRepository.setProfile(profile);
                textView.setText(String.valueOf(profile.getCheckpoints()));
                checkPoint.setNum(10);
                checkPoint.setGone(true);
                checkPoint.setDate(new Date());
                dataBaseRepository.setCheckPoint(checkPoint);
                checkPointsList.get(9).setGone(true);
            }
        }
        GeoData geoData = new GeoData();

        geoData.setUserPosition(ll.latitude, ll.longitude);
        geoData.setDate(new Date());
        dataBaseRepository.setGeoData(geoData);

        geoDataList.add(geoData);
        redrawLine();
    }

    private void textToSpeech() {
        if (dataBaseRepository.getProfile() != null) {
            profile = dataBaseRepository.getProfile();
            if (profile.getSpeak()) {
                textSay = new TextToSpeech(getActivity(), new TextToSpeech.OnInitListener() {
                    @Override
                    public void onInit(int status) {
                        if (status == TextToSpeech.SUCCESS && Locale.UK != null) {
                            textSay.setLanguage(Locale.UK);
                            textSay.speak(profile.getCheckpoints() + "checkpoints out of 10 completed", TextToSpeech.QUEUE_FLUSH, null);
                        } else {
                            Toast.makeText(getActivity(), "Feature not supported in your device", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        } else {
            dataBaseRepository.getProfileTask()
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            profile = Objects.requireNonNull(task.getResult()).toObject(Profile.class);
                            if (profile != null && profile.getSpeak()) {
                                textSay = new TextToSpeech(getActivity(), new TextToSpeech.OnInitListener() {
                                    @Override
                                    public void onInit(int status) {
                                        if (status == TextToSpeech.SUCCESS && Locale.UK != null) {
                                            textSay.setLanguage(Locale.UK);
                                            textSay.speak(profile.getCheckpoints() + "checkpoints out of 10 completed", TextToSpeech.QUEUE_FLUSH, null);
                                        } else {
                                            Toast.makeText(getActivity(), "Feature not supported in your device", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                            }
                        }
                    });
        }
    }

    public BitmapDescriptor getMarkerIcon(String color) {
        float[] hsv = new float[3];
        Color.colorToHSV(Color.parseColor(color), hsv);
        return BitmapDescriptorFactory.defaultMarker(hsv[0]);
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
        if(getActivity() != null){
            getActivity().bindService(intent, connection, Context.BIND_AUTO_CREATE);
        }
    }

    @Override
    public void setGeoposition(LatLng latLng) {
        // принимаешь значения из сервиса сюда
        // и ставишь точку(redrawLine)
        addNewMarker(latLng);
    }
}