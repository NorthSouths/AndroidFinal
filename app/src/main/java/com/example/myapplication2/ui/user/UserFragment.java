package com.example.myapplication2.ui.user;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.example.myapplication2.InstrumentActivity;
import com.example.myapplication2.LogInActivity;
import com.example.myapplication2.MainActivity;
import com.example.myapplication2.ModifyPwdActivity;
import com.example.myapplication2.R;
import com.example.myapplication2.UserInfo;

import java.util.ArrayList;

public class UserFragment extends Fragment {
    private Button modifyPwd;
    private Button logout;
    private Button instrument;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_user, container, false);
        modifyPwd = root.findViewById(R.id.modify_passwordBtn);
        logout = root.findViewById(R.id.logoutBtn);
        instrument = root.findViewById(R.id.instrumentBtn);

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UserInfo info = UserInfo.getInstance(getContext());
                String msg = info.getName() + ", you have overall " + info.getScore() + " points";
                new AlertDialog.Builder(getContext()).setTitle("Tips").setMessage(msg)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intent = new Intent();
                                intent.setClass(getActivity(), LogInActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                            }
                        }).show();
            }
        });

        modifyPwd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(getActivity(), ModifyPwdActivity.class);
                startActivity(intent);
            }
        });

        instrument.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(getActivity(), InstrumentActivity.class);
                startActivity(intent);
            }
        });
        return root;
    }
}