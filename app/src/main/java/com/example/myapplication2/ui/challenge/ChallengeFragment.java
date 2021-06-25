package com.example.myapplication2.ui.challenge;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.myapplication2.AnswerSelectActivity;
import com.example.myapplication2.InputQuestionActivity;
import com.example.myapplication2.R;

import java.util.HashMap;
import java.util.Map;

public class ChallengeFragment extends Fragment {

    private Button randomPracticeBtn;
    private Button chapterPracticeBtn;
    private Button addQuestionBtn;
    private int checkedItem;
    private String[] chapters;
    private Map<String, Integer> chapterMap;
    private int inputQuesType = 0;
    private String userName;

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
        chapterPracticeBtn = root.findViewById(R.id.chapter_practice_Btn);
        addQuestionBtn = root.findViewById(R.id.addQuestion);
        userName = getActivity().getIntent().getStringExtra("name");

        randomPracticeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startChallenge("random");
            }
        });

        chapterPracticeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                singleChoiceDialog();
                //  startChallenge("chapter");
            }
        });

        addQuestionBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addQuestionDialog();
            }
        });

        return root;
    }

    private void startChallenge(String type) {
        Intent intent = new Intent();
        intent.setClass(this.getActivity(), AnswerSelectActivity.class);
        intent.putExtra("type", type);//源程序送参数type，表示读取内容的类型。
        intent.putExtra("name",userName);
        startActivity(intent);
    }

    private void startChallenge(String type, int chapter) {
        Intent intent = new Intent();
        intent.setClass(this.getActivity(), AnswerSelectActivity.class);
        intent.putExtra("type", type);//源程序送参数type，表示读取内容的类型。
        intent.putExtra("chapter", chapter);
        intent.putExtra("name",userName);
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
                startChallenge("chapter", checkedItem + 1); // 注意这里，它是从0开始的
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

    public void addQuestionDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("请选择你导入题目的方式：");

        builder.setSingleChoiceItems(getResources().getStringArray(R.array.inuputQuestionMethod), checkedItem, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(getContext(), "你选择了" + which, Toast.LENGTH_SHORT).show();
                inputQuesType = which;
            }
        });
        builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                if (inputQuesType == 0) {
                    //手动输入，跳转界面
                    Intent intent = new Intent();
                    intent.setClass(getContext(), InputQuestionActivity.class);
                    startActivity(intent);
                } else {
                    //从文件输入
                    Toast.makeText(getContext(), "要从文件中读取，想办法实现", Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                    //intent.setDataAndType(Uri.fromFile(dir.getParentFile()), "file/*.txt");
                    intent.setType("file/*.txt"); //华为手机mate7不支持
                    intent.setType("text/plain");
                    intent.addCategory(Intent.CATEGORY_OPENABLE);
                    startActivityForResult(intent, 1001);

                }
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertDialog dialog = builder.create();  //创建AlertDialog对象
        dialog.show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK && requestCode == 1001) {//判断是否选择和Code判断
            Uri uri = data.getData();//拿到路径
        }
    }
}