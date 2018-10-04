package com.team4.capstone.voiceofcau;

import android.media.MediaPlayer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class song_screen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_song_screen);

        MediaPlayer music = MediaPlayer.create(this,R.raw.my_way);
        music.start();
        music.setLooping(false);


        music.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp)
            {
                mp.stop();
                mp.release();
            }

        });
}
}
