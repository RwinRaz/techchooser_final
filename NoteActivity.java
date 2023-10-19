package com.razani.techchooser;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import android.view.View;
import android.widget.EditText;

import java.util.ArrayList;

public class NoteActivity extends BaseActivity {

    private ArrayList<String> details;
    private EditText userApp,userExplain;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);
        details=getIntent().getStringArrayListExtra("details");
        userApp=findViewById(R.id.uses_et);
        userExplain=findViewById(R.id.need_et);
        if(details.size()==6)
        {
            userApp.setText(details.get(4));
            userExplain.setText(details.get(5));
        }

    }

    public void nextNoteClick(View view) {
        if (isNetworkAvailable()) {
            Intent nextLevelIntent = new Intent(NoteActivity.this, FinalActivity.class);
            if (userApp.getText().toString().isEmpty() || userApp.getText().toString() == null) {
                userApp.setText("");
            }
            if (userExplain.getText().toString().isEmpty() || userExplain.getText().toString() == null) {
                userExplain.setText("");
            }
            if (details.size() == 6) {
                details.set(4, userApp.getText().toString());
                details.set(5, userExplain.getText().toString());
            } else {

                details.add(userApp.getText().toString());
                details.add(userExplain.getText().toString());
            }
            nextLevelIntent.putExtra("details", details);
            startActivity(nextLevelIntent);
            finish();
        }
    }
    public void noteBackClick(View view) {
        Intent backLevelIntent=new Intent(NoteActivity.this,LevelActivity.class);
        backLevelIntent.putExtra("details", details);
        startActivity(backLevelIntent);
        finish();

    }
}