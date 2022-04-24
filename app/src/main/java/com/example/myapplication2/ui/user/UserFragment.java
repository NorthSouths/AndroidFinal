package com.example.myapplication2.ui.user;
/*
    author : 2191110613 任明宇
 */
import android.app.AlertDialog;
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

import com.example.myapplication2.AboutUsActivity;
import com.example.myapplication2.LogInActivity;
import com.example.myapplication2.ModifyPwdActivity;
import com.example.myapplication2.R;

import java.io.FileOutputStream;
import java.io.IOException;

import static android.content.Context.MODE_PRIVATE;


public class UserFragment extends Fragment {
    private Button modifyPwd;
    private Button logout;
    private Button about;
    private Button setRandomQuesNumBtn;
    private TextView userNameTextView;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_user, container, false);
        modifyPwd = root.findViewById(R.id.modify_passwordBtn);
        logout = root.findViewById(R.id.logoutBtn);
        about = root.findViewById(R.id.instrumentBtn);
        userNameTextView = root.findViewById(R.id.user_showinfo);
        final String name = getActivity().getIntent().getStringExtra("name");
        userNameTextView.setText(name);
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
                            int num = Integer.parseInt(number);
                            if (num > 50) {
                                throw new NumberFormatException();
                            }
                        } catch (NumberFormatException e) {
                            Toast.makeText(getContext(), "请输入不大于50的整数", Toast.LENGTH_SHORT).show();
                            return;
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
                getActivity().finish();
//                Intent intent = new Intent();
//                intent.setClass(getActivity(), LogInActivity.class);
//                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                startActivity(intent);
            }


        });

        modifyPwd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(getActivity(), ModifyPwdActivity.class);
                intent.putExtra("name", name);
                startActivity(intent);
            }
        });

        about.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(getActivity(), AboutUsActivity.class);
                startActivity(intent);
            }
        });
        return root;
    }
}