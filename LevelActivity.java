package com.razani.techchooser;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.xw.repo.BubbleSeekBar;

import java.util.ArrayList;

public class LevelActivity extends BaseActivity {


     private TextView mLevelTextView;
     private  com.xw.repo.BubbleSeekBar mLevelSeekBar, mBudgetSeekBar;
    private ArrayList<String> details;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_level);
        init();

        mLevelSeekBar.setOnProgressChangedListener(new BubbleSeekBar.OnProgressChangedListener() {
            @Override
            public void onProgressChanged(BubbleSeekBar bubbleSeekBar, int progress, float progressFloat, boolean fromUser) {
                if(progress==0)
                {
                    mLevelTextView.setText("Beginner");

                }
                if(progress==25)
                {
                    mLevelTextView.setText("Experienced Beginner");

                }
                if(progress==50)
                {
                    mLevelTextView.setText("Pre Intermediate");

                }
                if(progress==75) {
                    mLevelTextView.setText("Intermediate");

                }
                if(progress==100)
                {
                    mLevelTextView.setText("Professional");
                }
            }

            @Override
            public void getProgressOnActionUp(BubbleSeekBar bubbleSeekBar, int progress, float progressFloat) {

            }

            @Override
            public void getProgressOnFinally(BubbleSeekBar bubbleSeekBar, int progress, float progressFloat, boolean fromUser) {

            }
        });
    }
    private void init()
    {
        mLevelSeekBar=findViewById(R.id.level_seekbar);
        mLevelTextView=findViewById(R.id.level_tv);
        details=getIntent().getStringArrayListExtra("details");
        mBudgetSeekBar=findViewById(R.id.budget_seekbar);
        if(details.size()>2)
        {
            String progress1=details.get(2);
            if(progress1.equals("Beginner"))
            {
                mLevelTextView.setText("Beginner");
                mLevelSeekBar.setProgress(0);
            }
            if(progress1.equals("Experienced Beginner"))
            {
                mLevelTextView.setText("Experienced Beginner");
                mLevelSeekBar.setProgress(25);

            }
            if(progress1.equals("Pre Intermediate"))
            {
                mLevelTextView.setText("Pre Intermediate");
                mLevelSeekBar.setProgress(50);

            }
            if(progress1.equals(" Intermediate")) {
                mLevelTextView.setText("Intermediate");
                mLevelSeekBar.setProgress(75);

            }
            if(progress1.contains("Professional"))
            {
                mLevelTextView.setText("Professional");
                mLevelSeekBar.setProgress(100);
            }
            mBudgetSeekBar.setProgress(Integer.parseInt(details.get(3)));
        }
    }

    public void nextLevelClick(View view) {
        if (isNetworkAvailable()) {
            Intent nextLevelIntent = new Intent(LevelActivity.this, NoteActivity.class);
            if (details.size() == 2) {
                details.add(mLevelTextView.getText().toString());
                details.add(String.valueOf(mBudgetSeekBar.getProgress()));
            } else {
                details.set(2, mLevelTextView.getText().toString());
                details.set(3, String.valueOf(mBudgetSeekBar.getProgress()));
            }
            nextLevelIntent.putExtra("details", details);
            startActivity(nextLevelIntent);
            finish();
        }
    }

    public void backLevelClick(View view) {
        Intent backLevelIntent=new Intent(LevelActivity.this,PurposeActivity.class);
        backLevelIntent.putExtra("details",details);
        startActivity(backLevelIntent);
        finish();
    }

}