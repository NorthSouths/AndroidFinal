package com.example.myapplication2.ui.history;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.myapplication2.AnswerSelectActivity;
import com.example.myapplication2.R;
import com.example.myapplication2.ShowAnswerActivity;
import com.example.myapplication2.dao.RecordDao;
import com.example.myapplication2.data.Record;

import java.util.ArrayList;
import java.util.List;

public class HistoryFragment extends Fragment {
    private ListView recordView;
    private RecordDao recordDao;
    private Button delete;

    public View onCreateView(@NonNull final LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_history, container, false);
        recordView = root.findViewById(R.id.recordList);
        recordDao = new RecordDao(getContext());
        delete = root.findViewById(R.id.deleteBtn);
        //调整此处代码，使用DAO层控制record表，获取到答题记录列表 List<Record>
        // 随后建立一个List<String>并赋值给ArrayAdapter<String>
        // 建立点击监听事件，重点是获取对应的GroupID.
        // 注意要按用户进行查询，要想办法获取用户信息。

        final String name = getActivity().getIntent().getStringExtra("name");
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle("警告");
                builder.setMessage("确定要清空该用户全部记录嘛？");

                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                        recordDao.clearUserRecord(name);
                        List<String> temp = new ArrayList<>();
                        recordView.setAdapter(new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, temp));
                        return;
                    }
                });
                builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                        return;
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });
        //   Toast.makeText(getContext(), name2, Toast.LENGTH_SHORT).show();
        final List<Record> userAnswerHistoryLists = recordDao.getRecordsByName(name);
        if (userAnswerHistoryLists != null) {
            List<String> userAnswerInfoLists = new ArrayList<>();
            for (Record record : userAnswerHistoryLists) {
                String str = record.getUser() + "于" + record.getTime() + "获得" + record.getScore() + "分";
                userAnswerInfoLists.add(str);
            }
            recordView.setAdapter(new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, userAnswerInfoLists));
            //       UserInfo userInfo = UserInfo.getInstance(getActivity());
            //       recordView.setAdapter(new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, userInfo.getRecord()));
        }

        recordView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                int groupID = userAnswerHistoryLists.get(i).getGruopID();
                Toast.makeText(getContext(), String.valueOf(groupID), Toast.LENGTH_SHORT).show();
                Intent intent = new Intent();
                intent.setClass(getContext(), ShowAnswerActivity.class);
                intent.putExtra("groupID", groupID);
                startActivity(intent);
            }
        });
        return root;
    }


}