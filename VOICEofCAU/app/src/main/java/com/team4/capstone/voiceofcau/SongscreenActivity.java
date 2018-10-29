package com.team4.capstone.voiceofcau;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Button;
import android.widget.MediaController;
import android.widget.VideoView;
public class SongscreenActivity extends AppCompatActivity {
    MediaRecorder mRecorder;
    SharedPreferences prefs;
    String Song;
    String SongName;
    MediaController mediaController;
    VideoView videoView;
    AudioController audioController;
    boolean isRecord;
    boolean isScoring;
    public static final int RESULT_TRUE = 1;
    public static final int RESULT_MAIN = 45;
    public static final int RESULT_CONT = 44;
    public static final int RESULT_BEGIN = 43;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_song_screen);

        prefs = getSharedPreferences("MODE", MODE_PRIVATE);
        isRecord= prefs.getBoolean("isRecord", true);
        isScoring = prefs.getBoolean("isScoring", true);

        Intent intent = getIntent();
        Song = intent.getStringExtra("Songname");
        SongName = Song.split("_")[0];
        String SongPath = Song.split("_")[1];

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
            //데이터 담아서 팝업(액티비티) 호출
            Intent intent = new Intent(this, PopupActivity.class);
            startActivityForResult(intent,RESULT_TRUE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == RESULT_TRUE){
            switch (resultCode){
                case RESULT_CONT:
                    videoView.start();
                    break;
                case RESULT_BEGIN:
                    audioController.stopAudioProcessor();
                    Intent intent = new Intent(getApplicationContext(), SongscreenActivity.class);
                    intent.putExtra("Songname", Song);
                    startActivity(intent);
                    finish();
                    break;
                case RESULT_MAIN:
                    audioController.stopAudioProcessor();
                    finish();
                    break;
            }
        }
    }

}
