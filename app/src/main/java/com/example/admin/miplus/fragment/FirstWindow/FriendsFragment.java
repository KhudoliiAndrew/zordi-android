package com.example.admin.miplus.fragment.FirstWindow;

import android.app.FragmentManager;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.admin.miplus.R;
import com.example.admin.miplus.adapter.ItemClickListener;
import com.example.admin.miplus.adapter.MyRecyclerViewAdapter;
import com.example.admin.miplus.adapter.RowType;
import com.example.admin.miplus.data_base.DataBaseRepository;
import com.example.admin.miplus.data_base.models.Profile;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import static com.facebook.FacebookSdk.getApplicationContext;

public class FriendsFragment extends Fragment implements ItemClickListener {

    final DataBaseRepository dataBaseRepository = new DataBaseRepository();
    private Profile profile = new Profile();

    /*RecyclerView recyclerView;
    MyRecyclerViewAdapter adapter;
    List<RowType> items = new ArrayList<>();*/

    private TextToSpeech textSay;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.friends_fragment, container, false);

        textToSpeech("You have two friends");
        if (dataBaseRepository.getProfile() != null) {
            profile = dataBaseRepository.getProfile();
            setHeaderContent(view);
            setCardContent(view);
        } else {
            dataBaseRepository.getProfileTask()
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            profile = Objects.requireNonNull(task.getResult()).toObject(Profile.class);
                            setHeaderContent(view);
                            setCardContent(view);
                        }
                    });
        }

      /*  recyclerView = (RecyclerView) view.findViewById(R.id.friends_container);
        items.add(new FriendRowType());
        items.add(new AddFriendRowType());
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        adapter = new MyRecyclerViewAdapter(items);
        adapter.setClickListener(this);
        recyclerView.setAdapter(adapter);*/

        initToolbar();
        return view;
    }

    private void initToolbar() {
        Toolbar toolbar = Objects.requireNonNull(getActivity()).findViewById(R.id.toolbar);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        ActionBar actionbar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        Objects.requireNonNull(actionbar).setDisplayHomeAsUpEnabled(true);
        actionbar.setHomeAsUpIndicator(R.drawable.ic_arrow_back_white_24dp);
        actionbar.setDisplayShowHomeEnabled(true);
    }

    private void setHeaderContent(View view) {
        final FirebaseAuth mAuth = FirebaseAuth.getInstance();
        final TextView name = view.findViewById(R.id.user_name_google);
        final TextView email = view.findViewById(R.id.user_email_google);
        final ImageView logo = view.findViewById(R.id.user_logo_google);
        if (getActivity() != null) {
            name.setText(Objects.requireNonNull(mAuth.getCurrentUser()).getDisplayName());
            email.setText(mAuth.getCurrentUser().getEmail());
            Glide.with(getActivity()).load(mAuth.getCurrentUser().getPhotoUrl()).apply(RequestOptions.circleCropTransform()).into(logo);
        }
    }

    private void setCardContent(View view) {
        if (getActivity() != null) {
            ImageView logo = view.findViewById(R.id.friend_logo);
            Glide.with(getActivity()).load(R.drawable.photo_oleg).apply(RequestOptions.circleCropTransform()).into(logo);

            ImageView logo2 = view.findViewById(R.id.friend_Andrew_logo);
            Glide.with(getActivity()).load(R.drawable.andrew_logo).apply(RequestOptions.circleCropTransform()).into(logo2);

            RelativeLayout relativeLayout = view.findViewById(R.id.add_friend_container);
            relativeLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(getActivity(), "You can add more friends in the future", Toast.LENGTH_LONG).show();
                    textToSpeech("You can add more friends in the future");
                }
            });
            SwitchCompat switchCompat = view.findViewById(R.id.geoposition_switch);
            switchCompat.setChecked(profile.getShowGeoposition());
            switchCompat.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    profile.setShowGeoposition(isChecked);
                    dataBaseRepository.setProfile(profile);
                }
            });
        }
    }

    private void textToSpeech(final String text) {
        if (profile.getSpeak()) {
            textSay = new TextToSpeech(getActivity(), new TextToSpeech.OnInitListener() {
                @Override
                public void onInit(int status) {
                    if (status == TextToSpeech.SUCCESS && Locale.UK != null) {
                        textSay.setLanguage(Locale.UK);
                        textSay.speak(text, TextToSpeech.QUEUE_FLUSH, null);
                    } else {
                        Toast.makeText(getApplicationContext(), "Feature not supported in your device", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        Toolbar toolbar = Objects.requireNonNull(getActivity()).findViewById(R.id.toolbar);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        ActionBar actionbar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        Objects.requireNonNull(actionbar).setDisplayHomeAsUpEnabled(true);
        actionbar.setHomeAsUpIndicator(R.drawable.ic_menu_white_24dp);
        getActivity().getSupportFragmentManager().beginTransaction().remove(this).commit();
    }

    @Override
    public void onClick(View view, int position) {
        AddFriendFragment addFriendFragment = new AddFriendFragment();
        FragmentManager fragmentManager = Objects.requireNonNull(getActivity()).getFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.fragments_container, addFriendFragment).addToBackStack(null).commit();
    }


}
