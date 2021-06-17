package com.example.myapplication2.ui.challenge;

import android.content.Intent;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.example.myapplication2.AnswerInputActivity;
import com.example.myapplication2.AnswerSelectActivity;
import com.example.myapplication2.R;

public class ChallengeFragment extends Fragment {
    private Button numeracyBtn;
    private Button thinkingBtn;
    private Button literatureBtn;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_challenge, container, false);
        numeracyBtn = root.findViewById(R.id.numeracyBtn);
        thinkingBtn = root.findViewById(R.id.thinkingBtn);
        literatureBtn = root.findViewById(R.id.literatureBtn);
        numeracyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startChallenge("Numeracy");
            }
        });
        Drawable numeracyIcon = getResources().getDrawable(R.mipmap.numeracy);
        numeracyIcon.setBounds(0, 0, 50, 50);
        numeracyBtn.setCompoundDrawables(numeracyIcon, null, null, null);
        numeracyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startChallenge("Numeracy");
            }
        });
        thinkingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startChallenge("Thinking");
            }
        });
        Drawable thinkingIcon = getResources().getDrawable(R.mipmap.thinking);
        thinkingIcon.setBounds(new Rect(0, 0, 50, 50));
        thinkingBtn.setCompoundDrawables(thinkingIcon, null, null, null);

        literatureBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startChallenge("Literature");
            }
        });
        Drawable literatureIcon = getResources().getDrawable(R.mipmap.literature);
        literatureIcon.setBounds(0, 0, 50, 50);
        literatureBtn.setCompoundDrawables(literatureIcon, null, null, null);
        return root;
    }

    private void startChallenge(String type) {
        Intent intent = new Intent();
        if (type.equals("Numeracy"))
            intent.setClass(this.getActivity(), AnswerInputActivity.class);
        else
            intent.setClass(this.getActivity(), AnswerSelectActivity.class);
        intent.putExtra("type", type);
        startActivity(intent);
    }
}