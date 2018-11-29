package com.team4.capstone.voiceofcau;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.MediaController;
import android.widget.TextView;
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
    CanvasView itvView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_song_screen);
        itvView = (CanvasView)(findViewById(R.id.CanvasView));
        //258
        translateAnim(0, -1, 0, 0, 257 * 1000, itvView);
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


        TextView textView=(TextView)findViewById(R.id.song_name);
        textView.setText(SongName);
        /*
        if(type != 4) {
            isScoring = false;
        }
        if(type == 8) {
            isRecord = false;
        }*/
        if (isRecord){
            Button recordButton = (Button) findViewById(R.id.button2);
            recordButton.setBackground(ContextCompat.getDrawable(this, R.drawable.recordonbutton));
        }
        audioController = new AudioController(
                getApplicationContext(), SongPath, SongName, true, false);

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


    public static void translateAnim(float xStart, float xEnd, float yStart, float yEnd, int duration, CanvasView view) {

        TranslateAnimation translateAnimation = new TranslateAnimation(

                Animation.RELATIVE_TO_SELF,xStart,

                Animation.RELATIVE_TO_SELF, xEnd,

                Animation.RELATIVE_TO_SELF,yStart,

                Animation.RELATIVE_TO_SELF,yEnd);

        translateAnimation.setDuration(duration);
        translateAnimation.setFillAfter(true);
        view.startAnimation(translateAnimation);

    }

}


class CanvasView extends View {

    public CanvasView(Context context) {
        super(context);
    }

    public CanvasView(Context context, AttributeSet att) {
        super(context, att);
    }

    public CanvasView(Context context, AttributeSet att, int ref) {
        super(context, att, ref);
    }

    int width;
    int iheight = 0;
    int icurpos = 0;
    int iwidth = 0;
    int nextintv = 0;
    int intv = 0;
    int bx = 1;
    int total = 0;
    int canvasID = generateViewId();

    @Override
    public void onDraw(Canvas c) {

        Paint paint = new Paint();
        c.drawColor(Color.BLACK);
        Bitmap bm = Bitmap.createBitmap(singer.singerEndTime.get(singer.singerEndTime.size()-1).intValue() * 31, 520, Bitmap.Config.ARGB_8888);

        Canvas cv = new Canvas(bm);

        paint.setColor(Color.GRAY);


        //cv.drawRect(10, 10, 40, 40, paint);
        for (int i = 0; i < singer.singerEndTime.size(); i++) {
            if (i != singer.singerInterval.size() - 1) {
                nextintv = singer.singerInterval.get(i + 1);
                intv = singer.singerInterval.get(i);
            }

            if (singer.singerInterval.get(i) == 1) {
                iheight = 72;
            } else if (singer.singerInterval.get(i) == 2) {
                iheight = 70;
            } else if (singer.singerInterval.get(i) == 3) {
                iheight = 68;
            } else if (singer.singerInterval.get(i) == 4) {
                iheight = 66;
            } else if (singer.singerInterval.get(i) == 5) {
                iheight = 64;
            } else if (singer.singerInterval.get(i) == 6) {
                iheight = 62;
            } else if (singer.singerInterval.get(i) == 7) {
                iheight = 58;
            } else if (singer.singerInterval.get(i) == 8) {
                iheight = 56;
            } else if (singer.singerInterval.get(i) == 9) {
                iheight = 54;
            } else if (singer.singerInterval.get(i) == 10) {
                iheight = 52;
            } else if (singer.singerInterval.get(i) == 11) {
                iheight = 50;
            } else if (singer.singerInterval.get(i) == 12) {
                iheight = 48;
            } else if (singer.singerInterval.get(i) == 13) {
                iheight = 46;
            } else if (singer.singerInterval.get(i) == 14) {
                iheight = 42;
            } else if (singer.singerInterval.get(i) == 15) {
                iheight = 40;
            } else if (singer.singerInterval.get(i) == 16) {
                iheight = 38;
            } else if (singer.singerInterval.get(i) == 17) {
                iheight = 36;
            } else if (singer.singerInterval.get(i) == 18) {
                iheight = 32;
            } else if (singer.singerInterval.get(i) == 19) {
                iheight = 30;
            } else if (singer.singerInterval.get(i) == 20) {
                iheight = 28;
            } else if (singer.singerInterval.get(i) == 21) {
                iheight = 26;
            } else if (singer.singerInterval.get(i) == 22) {
                iheight = 24;
            } else if (singer.singerInterval.get(i) == 23) {
                iheight = 22;
            } else if (singer.singerInterval.get(i) == 24) {
                iheight = 20;
            } else if (singer.singerInterval.get(i) == 25) {
                iheight = 16;
            } else if (singer.singerInterval.get(i) == 26) {
                iheight = 14;
            } else if (singer.singerInterval.get(i) == 27) {
                iheight = 12;
            } else if (singer.singerInterval.get(i) == 28) {
                iheight = 10;
            }

//            if (singer.singerInterval.get(i) == 1) {
//                iheight = 460;
//            } else if (singer.singerInterval.get(i) == 2) {
//                iheight = 445;
//            } else if (singer.singerInterval.get(i) == 3) {
//                iheight = 430;
//            } else if (singer.singerInterval.get(i) == 4) {
//                iheight = 415;
//            } else if (singer.singerInterval.get(i) == 5) {
//                iheight = 400;
//            } else if (singer.singerInterval.get(i) == 6) {
//                iheight = 370;
//            } else if (singer.singerInterval.get(i) == 7) {
//                iheight = 355;
//            } else if (singer.singerInterval.get(i) == 8) {
//                iheight = 340;
//            } else if (singer.singerInterval.get(i) == 9) {
//                iheight = 325;
//            } else if (singer.singerInterval.get(i) == 10) {
//                iheight = 310;
//            } else if (singer.singerInterval.get(i) == 11) {
//                iheight = 295;
//            } else if (singer.singerInterval.get(i) == 12) {
//                iheight = 280;
//            } else if (singer.singerInterval.get(i) == 13) {
//                iheight = 250;
//            } else if (singer.singerInterval.get(i) == 14) {
//                iheight = 235;
//            } else if (singer.singerInterval.get(i) == 15) {
//                iheight = 220;
//            } else if (singer.singerInterval.get(i) == 16) {
//                iheight = 205;
//            } else if (singer.singerInterval.get(i) == 17) {
//                iheight = 190;
//            } else if (singer.singerInterval.get(i) == 18) {
//                iheight = 160;
//            } else if (singer.singerInterval.get(i) == 19) {
//                iheight = 145;
//            } else if (singer.singerInterval.get(i) == 20) {
//                iheight = 130;
//            } else if (singer.singerInterval.get(i) == 21) {
//                iheight = 115;
//            } else if (singer.singerInterval.get(i) == 22) {
//                iheight = 100;
//            } else if (singer.singerInterval.get(i) == 23) {
//                iheight = 85;
//            } else if (singer.singerInterval.get(i) == 24) {
//                iheight = 70;
//            } else if (singer.singerInterval.get(i) == 25) {
//                iheight = 55;
//            } else if (singer.singerInterval.get(i) == 26) {
//                iheight = 40;
//            } else if (singer.singerInterval.get(i) == 27) {
//                iheight = 25;
//            } else if (singer.singerInterval.get(i) == 28) {
//                iheight = 10;
//            }
            iheight*=4;
            singer.singerEndTime.set(i, singer.singerEndTime.get(i) * 31);

            singer.singerStartTime.set(i, singer.singerStartTime.get(i) * 31);
            icurpos = singer.singerStartTime.get(i).intValue();
            iwidth = singer.singerEndTime.get(i).intValue() - singer.singerStartTime.get(i).intValue();

            cv.drawRect(icurpos, iheight + 16 , icurpos + iwidth, iheight, paint);
        }
        c.drawBitmap(bm, 10 + bx, 10, paint);
    }
}


