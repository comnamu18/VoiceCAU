package com.team4.capstone.voiceofcau;

import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.MediaController;
import android.widget.VideoView;

import java.util.TimerTask;

public class SongscreenActivity extends AppCompatActivity {
    SharedPreferences prefs;
    String Song;
    String SongName;
    MediaController mediaController;
    VideoView videoView;
    AudioController audioController;
    int type;
    boolean isRecord;
    boolean isScoring;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_song_screen);

        prefs = getSharedPreferences("MODE", MODE_PRIVATE);
        isRecord= prefs.getBoolean("isRecord", true);
        isScoring = prefs.getBoolean("isScoring", true);

        Intent intent = getIntent();
        Song = intent.getStringExtra("Songname");
        String[] datas = Song.split("_");
        SongName = datas[0];
        String SongPath = datas[1];
        type = Integer.parseInt(datas[2]);
        switch (type){
            case 1:
                Song = SongName + "_" + SongPath + "_4_" + datas[3];
                break;
            case 3:
                Song = SongName + "_" + SongPath + "_8_" + datas[3];
                break;
                default:
                    Song = SongName + "_" + SongPath + "_" + datas[2] + "_" + datas[3];
                    break;
        }
        if(type != 4) {
            isScoring = false;
        }
        if(type == 8) {
            isRecord = false;
        }
        if (isRecord){
            Button recordButton = (Button) findViewById(R.id.button2);
            recordButton.setBackground(ContextCompat.getDrawable(this, R.drawable.recordonbutton));
        }
        audioController = new AudioController(
                getApplicationContext(), SongPath, SongName, isRecord, isScoring);

        videoView = (VideoView) findViewById(R.id.videoView);
        mediaController = new MediaController(this);
        mediaController.setAnchorView(videoView);
        Uri video = Uri.parse("android.resource://"+getPackageName()+"/raw/"+SongPath);
        videoView.setVideoURI(video);
        videoView.requestFocus();
        mediaController.setPadding(0, 0, 0, 80);
        videoView.start();

        videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                Intent intent = new Intent(getApplicationContext(), ScoreActivity.class);
                int score = audioController.stopAudioProcessor();
                Song = Song + "_" + String.valueOf(score);
                intent.putExtra("Score", Song);
                startActivity(intent);
                finish();
            }
        });
    }

    @Override
    public void onBackPressed() {
            videoView.pause();
            Intent intent = new Intent(this, PopupActivity.class);
            intent.putExtra("Songname", Song);
            startActivityForResult(intent,MainActivity.SUCCESS_FROM_POPUP);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == MainActivity.SUCCESS_FROM_POPUP){
            switch (resultCode){
                case MainActivity.RESULT_CONT:
                    videoView.start();
                    break;
                case MainActivity.RESULT_BEGIN:
                    audioController.stopAudioProcessor();
                    Intent intent = new Intent(getApplicationContext(), SongscreenActivity.class);
                    intent.putExtra("Songname", Song);
                    startActivity(intent);
                    finish();
                    break;
                case MainActivity.RESULT_MAIN:
                    audioController.stopAudioProcessor();
                    finish();
                    break;
            }
        }
    }

}
