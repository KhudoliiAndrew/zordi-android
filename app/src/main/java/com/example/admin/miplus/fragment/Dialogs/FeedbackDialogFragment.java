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
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.admin.miplus.R;

import static com.facebook.FacebookSdk.getApplicationContext;

public class FeedbackDialogFragment extends DialogFragment implements View.OnClickListener {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        View v = inflater.inflate(R.layout.feedback_dialog, null);
        v.findViewById(R.id.go_to_email_btn).setOnClickListener(this);
        v.findViewById(R.id.copy_button_feedback_dialog).setOnClickListener(this);

        return v;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.go_to_email_btn:
                Intent toEmail = new Intent(Intent.ACTION_SEND);
                toEmail.setData(Uri.parse("email"));
                toEmail.setType("message/rfc822");
                Intent launcherMail = Intent.createChooser(toEmail, "Launch Email");
                startActivity(launcherMail);
                break;
            case R.id.copy_button_feedback_dialog:
                ClipboardManager clipboard = (ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("", "penjuinj@gmail.com");
                clipboard.setPrimaryClip(clip);
                Toast toast = Toast.makeText(getApplicationContext(),
                        "Copy", Toast.LENGTH_SHORT);
                toast.show();
                break;
        }
    }
}
