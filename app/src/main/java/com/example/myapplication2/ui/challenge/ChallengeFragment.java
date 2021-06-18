package com.example.myapplication2.ui.challenge;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.myapplication2.AnswerSelectActivity;
import com.example.myapplication2.R;

import java.util.HashMap;
import java.util.Map;

public class ChallengeFragment extends Fragment {

    private Button randomPracticeBtn;
    private Button literatureBtn;
    private int checkedItem;
    private String[] chapters;
    private Map<String, Integer> chapterMap;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_challenge, container, false);
        chapters = getResources().getStringArray(R.array.chapter);
        chapterMap = new HashMap<>();
        for (int i = 0; i < chapters.length; i++) {
            String num = String.valueOf(chapters[i].charAt(0));
            chapterMap.put(chapters[i], Integer.parseInt(num));
        }
        randomPracticeBtn = root.findViewById(R.id.random_practice_Btn);
        literatureBtn = root.findViewById(R.id.literatureBtn);
        randomPracticeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startChallenge("random");
            }
        });

        literatureBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                singleChoiceDialog();
                //  startChallenge("chapter");
            }
        });
        return root;
    }

    private void startChallenge(String type) {
        Intent intent = new Intent();
        intent.setClass(this.getActivity(), AnswerSelectActivity.class);
        intent.putExtra("type", type);//源程序送参数type，表示读取内容的类型。
        startActivity(intent);
    }

    private void startChallenge(String type, int chapter) {
        Intent intent = new Intent();
        intent.setClass(this.getActivity(), AnswerSelectActivity.class);
        intent.putExtra("type", type);//源程序送参数type，表示读取内容的类型。
        intent.putExtra("chapter", chapter);
        startActivity(intent);
    }

    public void singleChoiceDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

        builder.setTitle("请选择你想要练习的章节：");


        builder.setSingleChoiceItems(chapters, checkedItem, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(getContext(), "你选择了" + chapters[which] + "章节号：" + String.valueOf(chapterMap.get(chapters[which])), Toast.LENGTH_SHORT).show();
                checkedItem = chapterMap.get(chapters[which]);
            }
        });
        builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                startChallenge("chapter", checkedItem);
            }
        });

        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        AlertDialog dialog = builder.create();  //创建AlertDialog对象
        dialog.show();                           //显示对话框
    }

}