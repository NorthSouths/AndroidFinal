package com.example.myapplication2;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class InstrumentActivity extends AppCompatActivity {
    private TextView instrument1;
    private TextView instrument2;
    private TextView instrument3;
    @Override
    public void onCreate(Bundle savedInstance){
        super.onCreate(savedInstance);
        setContentView(R.layout.activity_instrument);

        instrument1 = findViewById(R.id.instrumentText1);
        instrument2 = findViewById(R.id.instrumentText2);
        instrument3 = findViewById(R.id.instrumentText3);

        instrument1.setText("1. 你可以从三个方面选择挑战: \"数理逻辑\", \"批判思维\", 和 \"文化常识\"\n");
        ObjectAnimator animator1 = ObjectAnimator.ofFloat(instrument1,"alpha",0,1).setDuration(2000);
        animator1.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                instrument2.setText("2. 每题回答正确可得5个经验豆，但回答错误要丢失2个经验豆哦\n\n");
                ObjectAnimator animator2 = ObjectAnimator.ofFloat(instrument2,"alpha",0,1).setDuration(2000);
                animator2.addListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        instrument3.append("3. 你可以在历史这一栏里查看自己的挑战记录\n\n");
                        ObjectAnimator animator3 = ObjectAnimator.ofFloat(instrument3,"alpha",0,1).setDuration(2000);
                        animator3.start();
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {

                    }

                    @Override
                    public void onAnimationRepeat(Animator animation) {

                    }
                });
                animator2.start();
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        animator1.start();

    }

}
