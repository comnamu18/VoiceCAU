package com.team4.capstone.voiceofcau;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

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
    private String SongPath;
    private int score;
    private int type;
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
        SongPath = rawScore.split("_")[1];
        type = Integer.valueOf(rawScore.split("_")[2]);
        UserID = rawScore.split("_")[3];
        score = Integer.valueOf(rawScore.split("_")[4]);
        if(score != -1) {
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
                if (type == 5) {
                    Toast.makeText(getApplicationContext(), "연습모드는 다시 듣기를 지원하지 않습니다.",
                            Toast.LENGTH_LONG).show();
                }
                else{
                    TestOverLay muxing = new TestOverLay();
                    if (type == 6 || type == 7 || type == 9) {
                        try{
                            muxing.mixSound("test.wav", SongPath + ".wav", "muxed.wav");
                            muxing.runM4AConverter("/storage/emulated/0/muxed.wav", "/storage/emulated/0/muxed.m4a");
                            muxing.mux(SongPath + ".mp4", "muxed.m4a", "duetMuxed.mp4");
                        }
                        catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    else {
                        try{
                            String date = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.KOREA).format(new Date());
                            String finalFile = SongPath + "_" + date + ".m4a";
                            muxing.mux(SongPath+".mp4", finalFile, "recordMuxed.mp4");
                        }
                        catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    Intent intent = new Intent(getApplicationContext(), Detail_scoreActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        });

        Button btn3 = (Button)findViewById(R.id.score_retrybutton);
        btn3.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                Intent intent = new Intent(getApplicationContext(), SongscreenActivity.class);
                String reTry = rawScore.split("_")[0] + "_" + rawScore.split("_")[1]
                        + rawScore.split("_")[2] + "_" + rawScore.split("_")[3];
                intent.putExtra("Songname", reTry);
                startActivity(intent);
                finish();
            }
        });
    }

    //UPLOAD SCORE INFO
    public void createScoreStat() {
        String date = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.KOREA).format(new Date());
        final ScoreStatDO newScore = new ScoreStatDO();
        newScore.setUserId(UserID);
        newScore.setDate(date);
        newScore.setScore(String.valueOf(score));
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
