package com.team4.capstone.voiceofcau;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.mobile.client.AWSMobileClient;
import com.amazonaws.mobile.config.AWSConfiguration;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBMapper;
import com.amazonaws.models.nosql.ScoreStatDO;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ScoreActivity extends AppCompatActivity {
    DynamoDBMapper dynamoDBMapper;
    AWSCredentialsProvider credentialsProvider;
    String UserID;
    private String SongName;
    private String score;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_score);

        credentialsProvider = AWSMobileClient.getInstance().getCredentialsProvider();
        AWSConfiguration configuration = AWSMobileClient.getInstance().getConfiguration();
        // Add code to instantiate a AmazonDynamoDBClient
        AmazonDynamoDBClient dynamoDBClient = new AmazonDynamoDBClient(credentialsProvider);
        dynamoDBClient.setRegion(Region.getRegion(Regions.AP_NORTHEAST_2));
        this.dynamoDBMapper = DynamoDBMapper.builder()
                .dynamoDBClient(dynamoDBClient)
                .awsConfiguration(configuration)
                .build();

        TextView textView = (TextView)findViewById(R.id.scoreText);
        Intent intent = getIntent();
        final String rawScore = intent.getStringExtra("Score");
        SongName = rawScore.split("_")[0];
        UserID = rawScore.split("_")[3];
        score = rawScore.split("_")[4];
        if(score != "-1") {
            createScoreStat();
            String scoreStr = score + "점!";
            textView.setText(scoreStr);
        }
        else {
            textView.setText("수고하셨습니다!");
        }

        Button btn1 = (Button)findViewById(R.id.score_mainbutton);
        btn1.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                finish();
            }
        });

        Button btn2 = (Button)findViewById(R.id.score_detailbutton);
        btn2.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                Intent intent = new Intent(getApplicationContext(), Detail_scoreActivity.class);
                startActivity(intent);
                finish();
            }
        });

        Button btn3 = (Button)findViewById(R.id.score_retrybutton);
        btn3.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                Intent intent = new Intent(getApplicationContext(), SongscreenActivity.class);
                String reTry = rawScore.split("_")[0] + "_" + rawScore.split("_")[1];
                intent.putExtra("Songname", reTry);
                startActivity(intent);
                finish();
            }
        });
    }

    public void createScoreStat() {
        String date = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.KOREA).format(new Date());
        final ScoreStatDO newScore = new ScoreStatDO();
        newScore.setUserId(UserID);
        newScore.setDate(date);
        newScore.setScore(score);
        newScore.setSongName(SongName);
        new Thread(new Runnable() {
            @Override
            public void run() {
                Log.d("Saving Start", "START");
                dynamoDBMapper.save(newScore);
                Log.d("Saving End", "SAVED");
            }
        }).start();
    }
    @Override
    public void onBackPressed() {
        finish();
    }
}
