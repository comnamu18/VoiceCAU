package com.team4.capstone.voiceofcau;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class ScoreActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_score);

        TextView textView = (TextView)findViewById(R.id.scoreText);
        Intent intent = getIntent();
        int score = intent.getIntExtra("Score", -1);
        if(score != -1) {
            String scoreStr = String.valueOf(score) + "점!";
            textView.setText(scoreStr);
        }
        else {
            textView.setText("수고하셨습니다!");
        }

        Button btn1 = (Button)findViewById(R.id.score_button1);
        btn1.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
            }
        });

        Button btn2 = (Button)findViewById(R.id.score_button2);
        btn2.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                Intent intent = new Intent(getApplicationContext(), Detail_scoreActivity.class);
                startActivity(intent);
            }
        });

        Button btn3 = (Button)findViewById(R.id.score_button3);
        btn3.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                Intent intent = new Intent(getApplicationContext(), SongscreenActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override

    public void onBackPressed() {
        finish();
    }

}
