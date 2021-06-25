package com.example.myapplication2;

import android.content.DialogInterface;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication2.dao.UserDao;

public class ForgetActivity extends AppCompatActivity {

    private String verNum = "";
    private Button returnBtn, confirmBtn, getCodeBtn;
    private EditText phoneNumberText, verText, pwdText, pwdRepeatText;
    private String phoneNum;
    private UserDao userDao;
    private String correctPhoneNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.forget);
        userDao = new UserDao(ForgetActivity.this);
        returnBtn = findViewById(R.id.forget_returnBtn);
        confirmBtn = findViewById(R.id.forget_confirmBtn);
        getCodeBtn = findViewById(R.id.getCodeBtn);
        phoneNumberText = findViewById(R.id.forget_inputUserPhone);
        pwdText = findViewById(R.id.forget_inputPwd);
        pwdRepeatText = findViewById(R.id.forget_inputPwdRepeat);
        verText = findViewById(R.id.forget_verNum);
        phoneNumberText.setText("15555215554");

        for (int i = 0; i < 6; i++) {
            verNum += (char) ('0' + (int) (Math.random() * 10));
        }

        getCodeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //15555215554
                correctPhoneNumber = phoneNum = phoneNumberText.getText().toString();
                if (!userDao.isExistSth(phoneNum, "phonenum")) {
                    Toast.makeText(ForgetActivity.this, "手机号不存在", Toast.LENGTH_SHORT).show();
                    return;
                }
                SmsManager massage = SmsManager.getDefault();
                massage.sendTextMessage(phoneNum, null, verNum, null, null);
                Toast.makeText(ForgetActivity.this, "验证码已发送成功", Toast.LENGTH_SHORT).show();
//                Uri smsToUri = Uri.parse("smsto:");
//                Intent intent = new Intent(Intent.ACTION_SENDTO, smsToUri);
//              //  intent.setAction(Intent.ACTION_SENDTO);
//                intent.setType("vnd.android-dir/mms-sms");
//               // intent.setData(Uri.parse("smsto:" + phoneNum));
//                intent.putExtra("sms_body", verNum);
//                intent.putExtra("address", phoneNum);
//                startActivity(intent);
            }
        });

        returnBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        confirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                forgetPwd();
            }
        });
    }

    void forgetPwd() {
        String inputVerNum = verText.getText().toString();
        String phoneNum = phoneNumberText.getText().toString();
        String pwd = pwdText.getText().toString();
        String pwdRepeat = pwdRepeatText.getText().toString();
        if (phoneNum.isEmpty() || pwd.isEmpty() || inputVerNum.isEmpty() || pwdRepeat.isEmpty()) {
            Toast.makeText(ForgetActivity.this, "请输入完整信息", Toast.LENGTH_SHORT).show();
            return;
        }
        if (pwd.length() < 6) {
            Toast.makeText(ForgetActivity.this, "密码至少需要6个字符", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!pwd.equals(pwdRepeat)) {
            Toast.makeText(ForgetActivity.this, "两次密码不一致", Toast.LENGTH_SHORT).show();
            return;
        }

        if (verNum.compareTo(inputVerNum) != 0 || !userDao.isExistSth(phoneNum, "phonenum")) {
            Toast.makeText(ForgetActivity.this, "验证码错误", Toast.LENGTH_SHORT).show();
            return;
        }

        //更新密码

        userDao.updateUserPwd(phoneNum, "phonenum", pwd);
        new AlertDialog.Builder(this).setTitle("提示").setMessage("你已经成功修改密码啦")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                }).show();

    }
}
