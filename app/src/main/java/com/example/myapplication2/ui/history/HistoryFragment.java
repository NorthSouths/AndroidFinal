package com.example.myapplication2.ui.history;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.myapplication2.R;
import com.example.myapplication2.UserInfo;
import com.example.myapplication2.database.DatabaseHelper;

import java.util.ArrayList;

public class HistoryFragment extends Fragment {
    private ListView recordView;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_history, container, false);
        recordView = root.findViewById(R.id.recordList);
        UserInfo userInfo = UserInfo.getInstance(getActivity());
        recordView.setAdapter(new ArrayAdapter<String>(getActivity(),android.R.layout.simple_list_item_1,userInfo.getRecord()));
        return root;
    }

    @Override
    public void onResume(){
        super.onResume();
        Log.i("qoc","history fragment show");
    }
}