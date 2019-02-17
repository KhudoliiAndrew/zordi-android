package com.example.admin.miplus.fragment.Dialogs;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.admin.miplus.R;

import static com.facebook.FacebookSdk.getApplicationContext;

public class DonateDialogFragment extends DialogFragment implements View.OnClickListener {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        View v = inflater.inflate(R.layout.donate_dialog, null);
        v.findViewById(R.id.coppy_button_donate_dialog).setOnClickListener(this);

        return v;
    }

    @Override
    public void onClick(View v) {
        ClipboardManager clipboard = (ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("", "5168 7559 0373 9171");
        clipboard.setPrimaryClip(clip);
        Toast toast = Toast.makeText(getApplicationContext(), "Copy", Toast.LENGTH_SHORT);
        toast.show();
    }
}
