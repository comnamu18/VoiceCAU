package com.team4.capstone.voiceofcau;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.MediaController;
import android.widget.VideoView;

public class SongscreenActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_song_screen);


        Intent intent = getIntent();
        String Song = intent.getStringExtra("Songname");
        String SongName = Song.split("_")[0];
        String SongPath = Song.split("_")[1];

//        getSupportActionBar().setTitle("타이틀");
//        MediaPlayer music = MediaPlayer.create(this,R.raw.my_way);
//        music.start();
//        music.setLooping(false);



        final VideoView videoView =
                (VideoView) findViewById(R.id.videoView);
        MediaController mediaController = new MediaController(this);
        mediaController.setAnchorView(videoView);
       Uri video = Uri.parse("android.resource://"+getPackageName()+"/raw/"+SongPath);
       videoView.setVideoURI(video);
        videoView.requestFocus();
        mediaController.setPadding(0, 0, 0, 80); //상위 레이어의 바닥에서 얼마 만큼? 패딩을 줌
        //videoView.setMediaController(mediaController);

        videoView.start();



        videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

            @Override
            public void onCompletion(MediaPlayer mp) {
                Intent intent = new Intent(getApplicationContext(), ScoreActivity.class);
                startActivity(intent);
            }
        });


//        music.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
//            @Override
//            public void onCompletion(MediaPlayer mp)
//            {
//                mp.stop();
//                mp.release();
//            }
//
//        });
    }


    @Override

    public void onBackPressed() {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);


        super.onBackPressed();

    }

}
