package com.example.myapplication2.ui.user;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.myapplication2.InstrumentActivity;
import com.example.myapplication2.LogInActivity;
import com.example.myapplication2.ModifyPwdActivity;
import com.example.myapplication2.R;
import com.example.myapplication2.UserInfo;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;

import static android.content.Context.MODE_PRIVATE;


public class UserFragment extends Fragment {
    private Button modifyPwd;
    private Button logout;
    private Button instrument;
    private Button setRandomQuesNumBtn;
    private TextView userNameTextView;
    private String userInfo;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_user, container, false);
        modifyPwd = root.findViewById(R.id.modify_passwordBtn);
        logout = root.findViewById(R.id.logoutBtn);
        instrument = root.findViewById(R.id.instrumentBtn);
        userNameTextView = root.findViewById(R.id.user_showinfo);
        userInfo = UserInfo.getInstance(getContext()).getName();
        userNameTextView.setText(userInfo);
        setRandomQuesNumBtn = root.findViewById(R.id.set_random_quesnum_btn);

        setRandomQuesNumBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                final AlertDialog dialog = builder.create();
                View dialogView = View.inflate(getContext(), R.layout.input_something_dialog_layout, null);
                dialog.setView(dialogView);
                dialog.show();
                final EditText inputNum;
                final Button dialogConfirmBtn, dialogCancelBtn;
                inputNum = dialog.findViewById(R.id.input_dialog_num);
                dialogConfirmBtn = dialog.findViewById(R.id.input_dialog_confirmBtn);
                dialogCancelBtn = dialog.findViewById(R.id.input_dialog_cancelBtn);

                dialogCancelBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });

                dialogConfirmBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String number = inputNum.getText().toString();
                        try {
                            Integer.parseInt(number);

                        } catch (NumberFormatException e) {
                            Toast.makeText(getContext(), "请输入整数", Toast.LENGTH_SHORT).show();
                        }
                        FileOutputStream fileOutputStream = null;
                        try {
                            fileOutputStream = getActivity().openFileOutput("questionNum", MODE_PRIVATE);
                            String content = "randomQuestionNum " + number;
                            fileOutputStream.write(content.getBytes());
                            fileOutputStream.flush();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        Toast.makeText(getContext(), "设置成功", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    }
                });
            }
        });

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