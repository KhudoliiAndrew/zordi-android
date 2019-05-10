package com.example.admin.miplus.fragment.FirstWindow;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import com.example.admin.miplus.R;
import com.example.admin.miplus.data_base.DataBaseRepository;
import com.example.admin.miplus.data_base.models.GeoSettings;
import com.example.admin.miplus.data_base.models.Profile;
import com.example.admin.miplus.fragment.SecondFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

public class MapSettingsFragment extends Fragment {

    final DataBaseRepository dataBaseRepository = new DataBaseRepository();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private GeoSettings geoSettings = new GeoSettings();
    private GeoSettings geoSettingsM = new GeoSettings();
    private GeoSettings geoSettingsP = new GeoSettings();
    String[] listItems;
    String[] listItemsM;
    String[] listItemsP;

    private static final int LAYOUT = R.layout.map_settings_activity;

    public static MapSettingsFragment getInstance() {
        Bundle args = new Bundle();
        MapSettingsFragment fragment = new MapSettingsFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(LAYOUT, container, false);

/*
        Button mapSettingsPhone = (Button) view.findViewById(R.id.mapSettingsPhone);
        mapSettingsPhone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS), 0);
            }
        });
*/

        Button backToMapButton = (Button) view.findViewById(R.id.backToMapButton);
        backToMapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SecondFragment fragment = new SecondFragment();
                FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.map_settings, fragment);
                fragmentTransaction.commit();
            }
        });

        Button mapType = (Button) view.findViewById(R.id.map_type_button);
        final TextView mapTypeText = (TextView) view.findViewById(R.id.map_type_text);
       /* mapType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listItems = new String[] {"Normal", "Satellite", "Hybrid", "Terrain"};
                AlertDialog.Builder mapTypeBuilder = new AlertDialog.Builder(getActivity());
                mapTypeBuilder.setTitle("Choose a type");
          //      mapTypeBuilder.setIcon(R.drawable.ic_list);
                mapTypeBuilder.setSingleChoiceItems(listItems, -1, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mapTypeText.setText(listItems[which]);
                        dialog.dismiss();

                        switch(which){
                            case 0 :
                                geoSettings.setMapType(getString(R.string.map_type_normal));
                                dataBaseRepository.setMapSettings(geoSettings);
                                break;
                            case 1 :
                                geoSettings.setMapType(getString(R.string.map_type_satellite));
                                dataBaseRepository.setMapSettings(geoSettings);
                                break;
                            case 2 :
                                geoSettings.setMapType(getString(R.string.map_type_hybrid));
                                dataBaseRepository.setMapSettings(geoSettings);
                                break;
                            case 3 :
                                geoSettings.setMapType(getString(R.string.map_type_terrain));
                                dataBaseRepository.setMapSettings(geoSettings);
                                break;
                        }
                    }
                });

                AlertDialog mapTypeDialog = mapTypeBuilder.create();
                mapTypeDialog.show();
            }
        });*/

        Button markerColor = (Button) view.findViewById(R.id.marker_color_button);
        final TextView markerColorText = (TextView) view.findViewById(R.id.marker_color_text);
        markerColor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listItemsM = new String[] {"Blue", "Red", "Green", "Black"};
                AlertDialog.Builder markerColorBuilder = new AlertDialog.Builder(getActivity());
                markerColorBuilder.setTitle("Choose a type");
                //markerColorBuilder.setIcon(R.drawable.ic_list);
                markerColorBuilder.setSingleChoiceItems(listItemsM, -1, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        markerColorText.setText(listItemsM[which]);
                        dialog.dismiss();

                        switch(which){
                            case 0 :
                                geoSettingsM.setMarkerColor(getString(R.string.marker_color_blue));
                                dataBaseRepository.setMarkerColorFS(geoSettingsM);
                                break;
                            case 1 :
                                geoSettingsM.setMarkerColor(getString(R.string.marker_color_red));
                                dataBaseRepository.setMarkerColorFS(geoSettingsM);
                                break;
                            case 2 :
                                geoSettingsM.setMarkerColor(getString(R.string.marker_color_green));
                                dataBaseRepository.setMarkerColorFS(geoSettingsM);
                                break;
                            case 3 :
                                geoSettingsM.setMarkerColor(getString(R.string.marker_color_black));
                                dataBaseRepository.setMarkerColorFS(geoSettingsM);
                                break;
                        }
                    }
                });

                AlertDialog markerColorDialog = markerColorBuilder.create();
                markerColorDialog.show();
            }
        });
        return view;
    }
}
