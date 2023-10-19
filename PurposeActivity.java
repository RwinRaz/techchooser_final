package com.razani.techchooser;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;


import android.content.Intent;
import android.graphics.Color;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;


public class PurposeActivity extends BaseActivity {
    private ArrayList<String> details;
    private CardView programmerCard, videogameCard, personalCard, designCard;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_purpose);
        init();

    }

    @Override
    protected void onStart() {
        super.onStart();

    }

    private void init() {
        personalCard = findViewById(R.id.personal_card);
        programmerCard = findViewById(R.id.programmer_card);
        designCard = findViewById(R.id.design_card);
        videogameCard = findViewById(R.id.videogame_card);

        //Color.rgb(43, 165, 255)
        personalCard.setCardBackgroundColor(Color.rgb(4, 211, 218));
        programmerCard.setCardBackgroundColor(Color.rgb(4, 211, 218));
        videogameCard.setCardBackgroundColor(Color.rgb(4, 211, 218));
        designCard.setCardBackgroundColor(Color.rgb(4, 211, 218));
        details = getIntent().getStringArrayListExtra("details");
        if (details.size() == 1) {
            details.add("");
        } else {
            if (details.get(1).equals("Graphic Design")) {
                designCard.setCardBackgroundColor(Color.rgb(255, 90, 40));
            }
            if (details.get(1).equals("Gaming")) {
                videogameCard.setCardBackgroundColor(Color.rgb(255, 90, 40));
            }
            if (details.get(1).equals("Programming")) {
                programmerCard.setCardBackgroundColor(Color.rgb(255, 90, 40));
            }
            if (details.get(1).equals("Personal Uses")) {
                personalCard.setCardBackgroundColor(Color.rgb(255, 90, 40));
            }
        }
    }

    public void purposeClick(View view) {
        switch (view.getId()) {
            case R.id.design_card: {
                if (details.get(1).isEmpty()) {
                    designCard.setCardBackgroundColor(Color.rgb(255, 90, 40));
                    //orderPurposeArray[3]="Graphic Design";
                    details.set(1, "Graphic Design");
                } else if (details.get(1).contains("Graphic Design")) {
                    designCard.setCardBackgroundColor(Color.rgb(4, 211, 218));
                    //orderPurposeArray[3]="";
                    details.set(1, "");
                } else {

                    personalCard.setCardBackgroundColor(Color.rgb(4, 211, 218));
                    programmerCard.setCardBackgroundColor(Color.rgb(4, 211, 218));
                    videogameCard.setCardBackgroundColor(Color.rgb(4, 211, 218));
                    designCard.setCardBackgroundColor(Color.rgb(255, 90, 40));
                    details.set(1, "Graphic Design");
                }
                break;
            }

            case R.id.videogame_card: {
                if (details.get(1).isEmpty()) {
                    videogameCard.setCardBackgroundColor(Color.rgb(255, 90, 40));
                    //  orderPurposeArray[2]="Gaming";
                    details.set(1, "Gaming");
                } else if (details.get(1).contains("Gaming")) {
                    videogameCard.setCardBackgroundColor(Color.rgb(4, 211, 218));
                    ;
                    //  orderPurposeArray[2]="";
                    details.set(1, "");
                } else {
                    personalCard.setCardBackgroundColor(Color.rgb(4, 211, 218));
                    programmerCard.setCardBackgroundColor(Color.rgb(4, 211, 218));
                    videogameCard.setCardBackgroundColor(Color.rgb(255, 90, 40));
                    designCard.setCardBackgroundColor(Color.rgb(4, 211, 218));
                    details.set(1, "Gaming");
                }
                break;
            }
            case R.id.programmer_card: {
                if (details.get(1).equals("")) {
                    programmerCard.setCardBackgroundColor(Color.rgb(255, 90, 40));
                    details.set(1, "Programming");
                } else if (details.get(1).contains("Programming")) {
                    programmerCard.setCardBackgroundColor(Color.rgb(4, 211, 218));
                    details.set(1, "");
                } else {
                    personalCard.setCardBackgroundColor(Color.rgb(4, 211, 218));
                    programmerCard.setCardBackgroundColor(Color.rgb(255, 90, 40));
                    videogameCard.setCardBackgroundColor(Color.rgb(4, 211, 218));
                    designCard.setCardBackgroundColor(Color.rgb(4, 211, 218));
                    details.set(1, "Programming");
                }
                break;
            }
            case R.id.personal_card: {
                if (details.get(1).isEmpty()) {
                    personalCard.setCardBackgroundColor(Color.rgb(255, 90, 40));
                    //orderPurposeArray[0]="Personal Uses";
                    details.set(1, "Personal Uses");
                } else if (details.get(1).contains("Personal Uses")) {
                    personalCard.setCardBackgroundColor(Color.rgb(4, 211, 218));
                    ;
                    details.set(1, "");
                } else {
                    personalCard.setCardBackgroundColor(Color.rgb(255, 90, 40));
                    programmerCard.setCardBackgroundColor(Color.rgb(4, 211, 218));
                    videogameCard.setCardBackgroundColor(Color.rgb(4, 211, 218));
                    designCard.setCardBackgroundColor(Color.rgb(4, 211, 218));
                    details.set(1, "Personal Uses");
                }
                break;
            }
        }
    }

    public void nextClick(View view) {
        if (isNetworkAvailable()) {
            //info.add(Order+"\n");
            if (details.get(1).isEmpty()) {
                Toast.makeText(PurposeActivity.this, "You must select an option", Toast.LENGTH_LONG).show();
            } else {
                //info.add(mOrder);
                Intent PurposeIntent = new Intent(PurposeActivity.this, LevelActivity.class).putExtra("details", details);
                //  PurposeIntent.putExtra("order",  mOrder);
                startActivity(PurposeIntent);
                finish();
            }
        }
    }

    public void backPurposeClick(View view) {
        startActivity(new Intent(PurposeActivity.this, MainActivity.class));
        finish();
    }
}