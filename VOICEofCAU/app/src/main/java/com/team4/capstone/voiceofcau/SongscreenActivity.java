package com.team4.capstone.voiceofcau;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
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

import java.util.ArrayList;

import static com.team4.capstone.voiceofcau.SongscreenActivity.checkduet;


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
    boolean isPractice = false;
    static int checkduet = 0;
    CanvasView itvView;
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

        TextView textView=(TextView)findViewById(R.id.song_name);
        textView.setText(SongName);
        boolean isDuet = false;
        switch(type) {
            case 1:
                type = 4;
                Song = datas[0] + "_" + datas[1] + "_" + String.valueOf(type) + "_" + datas[3];
                break;
            case 2:
                type = 5;
                isPractice = true;
                isRecord = false;
                isScoring = false;
                Song = datas[0] + "_" + datas[1] + "_" + String.valueOf(type) + "_" +datas[3];
                break;
            case 6:
            case 7:
            case 9:
                isScoring = false;
                isDuet = true;
                break;
            case 8:
                isScoring = false;
                isRecord = false;
                break;
        }
        if (!isPractice) {
            itvView = (CanvasView)(findViewById(R.id.CanvasView));
            translateAnim(0, -1, 0, 0, 50 * 1000, itvView);
        }

        if (isRecord){
            Button recordButton = (Button) findViewById(R.id.button2);
            recordButton.setBackground(ContextCompat.getDrawable(this, R.drawable.recordonbutton));
        }

        if (type != 8 ) {
            audioController = new AudioController(
                    getApplicationContext(), SongPath, SongName, isRecord, isScoring, isDuet);
        }
        else {
            audioController = new AudioController(
                    getApplicationContext(), SongPath, SongName, false, false, isDuet);
        }

        videoView = (VideoView) findViewById(R.id.videoView);
        Uri video = Uri.parse(Environment.getExternalStorageDirectory().toString()
                + "/" + SongPath + ".mp4");
        videoView.setVideoURI(video);
        videoView.requestFocus();
        mediaController = new MediaController(this);
        mediaController.setAnchorView(videoView);
        mediaController.setPadding(0, 0, 0, 80);
        videoView.start();

        videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                if (type == 8) {
                   setContentView(R.layout.activity_song_screen);
                   audioController.stopAudioProcessor(false);
                   checkduet = 1;
//                    singer.singerEndTime.clear();
//                    singer.singerInterval.clear();
//                    singer.singerStartTime.clear();
//                    singer.singtitle.clear();
                    finish();
                }
                else{
                    Intent intent = new Intent(getApplicationContext(), ScoreActivity.class);
                    int score = audioController.stopAudioProcessor(true);
//                    CalculateScore.Time.clear();
//                    CalculateScore.Interval.clear();
//                    singer.singerEndTime.clear();
//                    singer.singerInterval.clear();
//                    singer.singerStartTime.clear();
//                    singer.singtitle.clear();
                    audioController = null;
                    Song = Song + "_" + String.valueOf(score);
                    intent.putExtra("Score", Song);
                    startActivity(intent);
                    finish();
                }
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
                    audioController.stopAudioProcessor(false);
                    Intent intent = new Intent(getApplicationContext(), SongscreenActivity.class);
                    intent.putExtra("Songname", Song);
                    startActivity(intent);
                    finish();
                    break;
                case MainActivity.RESULT_MAIN:
                    audioController.stopAudioProcessor(false);
                    finish();
                    break;
            }
        }
    }


    public static void translateAnim(float xStart, float xEnd, float yStart, float yEnd, int duration, CanvasView view) {
        TranslateAnimation translateAnimation = new TranslateAnimation(
                Animation.RELATIVE_TO_SELF,xStart,
                Animation.RELATIVE_TO_SELF,xEnd,
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
        try{
            Paint paint = new Paint();
            Paint detailpaint = new Paint();
            c.drawColor(Color.BLACK);
            detailpaint.setColor(Color.YELLOW);
            Log.d("checkbitmap",singer.singerEndTime.get(singer.singerEndTime.size() - 1).toString() );
            Bitmap bm = Bitmap.createBitmap((int)(singer.singerEndTime.get(singer.singerEndTime.size() - 1)* 100), 520, Bitmap.Config.ARGB_8888);
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

                    iheight *= 4;
                    icurpos = (int) (singer.singerStartTime.get(i) * 100);
                    iwidth = (int) (singer.singerEndTime.get(i) * 100 - singer.singerStartTime.get(i) * 100);
                    Log.d("testintervalbar", singer.singerInterval.get(i).toString());
                    Log.d("testTimebar", singer.singerStartTime.get(i).toString());
                    cv.drawRect(icurpos, iheight + 16, icurpos + iwidth, iheight, paint);
                }
            if(checkduet == 1)
            {
                Log.d("detail mode start", "start");
                ArrayList<Double> startTime = new ArrayList<>();
                ArrayList<Double> endTime = new ArrayList<>();
                ArrayList<Integer> realInterval = new ArrayList<>();
                int tried = 0;
                int j = 0;

                Log.d("CalculateScore Interval 1st", CalculateScore.Interval.get(0).toString());
                for(int i = 0; i < CalculateScore.Interval.size(); i++) {
                    if (tried == 0) {
                        Log.d("CalculateScore Interval 2nd", CalculateScore.Interval.get(0).toString());
                        startTime.add(CalculateScore.Time.get(i));
                        endTime.add(CalculateScore.Time.get(i));
                        realInterval.add(CalculateScore.Interval.get(i));
                        tried++;
                    } else if ((realInterval.get(j) - CalculateScore.Interval.get(i)) == 0 && CalculateScore.Time.get(i) - endTime.get(j) < 0.2) {
                        endTime.set(j, CalculateScore.Time.get(i));
                    } else if (CalculateScore.Time.get(i) - endTime.get(j) < 0.2 &&
                            realInterval.get(j) != CalculateScore.Interval.get(i)) {
                        endTime.set(j, CalculateScore.Time.get(i));
                        startTime.add(CalculateScore.Time.get(i));
                        endTime.add(CalculateScore.Time.get(i));
                        realInterval.add(CalculateScore.Interval.get(i));
                        j++;
                    } else {
                        endTime.add( CalculateScore.Time.get(i));
                        startTime.add(CalculateScore.Time.get(i));
                        endTime.add(CalculateScore.Time.get(i));
                        realInterval.add(CalculateScore.Interval.get(i));
                        j++;
                    }
                }

                for (int i = 0; i < endTime.size(); i++) {
                    if (realInterval.get(i) == 1) {
                        iheight = 72;
                    } else if (realInterval.get(i) == 2) {
                        iheight = 70;
                    } else if (realInterval.get(i) == 3) {
                        iheight = 68;
                    } else if (realInterval.get(i) == 4) {
                        iheight = 66;
                    } else if (realInterval.get(i) == 5) {
                        iheight = 64;
                    } else if (realInterval.get(i) == 6) {
                        iheight = 62;
                    } else if (realInterval.get(i) == 7) {
                        iheight = 58;
                    } else if (realInterval.get(i) == 8) {
                        iheight = 56;
                    } else if (realInterval.get(i) == 9) {
                        iheight = 54;
                    } else if (realInterval.get(i) == 10) {
                        iheight = 52;
                    } else if (realInterval.get(i) == 11) {
                        iheight = 50;
                    } else if (realInterval.get(i) == 12) {
                        iheight = 48;
                    } else if (realInterval.get(i) == 13) {
                        iheight = 46;
                    } else if (realInterval.get(i) == 14) {
                        iheight = 42;
                    } else if (realInterval.get(i) == 15) {
                        iheight = 40;
                    } else if (realInterval.get(i) == 16) {
                        iheight = 38;
                    } else if (realInterval.get(i) == 17) {
                        iheight = 36;
                    } else if (realInterval.get(i) == 18) {
                        iheight = 32;
                    } else if (realInterval.get(i) == 19) {
                        iheight = 30;
                    } else if (realInterval.get(i) == 20) {
                        iheight = 28;
                    } else if (realInterval.get(i) == 21) {
                        iheight = 26;
                    } else if (realInterval.get(i) == 22) {
                        iheight = 24;
                    } else if (realInterval.get(i) == 23) {
                        iheight = 22;
                    } else if (realInterval.get(i) == 24) {
                        iheight = 20;
                    } else if (realInterval.get(i) == 25) {
                        iheight = 16;
                    } else if (realInterval.get(i) == 26) {
                        iheight = 14;
                    } else if (realInterval.get(i) == 27) {
                        iheight = 12;
                    } else if (realInterval.get(i) == 28) {
                        iheight = 10;
                    }

                    iheight *= 4;
                    icurpos = (int) (startTime.get(i) * 100);
                    iwidth = (int) (endTime.get(i) * 100 - startTime.get(i) * 100);
                    Log.d("testDetailintervalbar", realInterval.get(i).toString());
                    Log.d("testDetailTimebar", startTime.get(i).toString());
                    cv.drawRect(icurpos, iheight + 16, icurpos + iwidth, iheight, detailpaint);
                }
//        bm.setHeight(52);
//        bm.setWidth(2000);

            }

//        bm.setHeight(52);
//        bm.setWidth(2000);
                c.drawBitmap(bm, 10, 10, paint);

        }catch (Exception e) {
            e.printStackTrace();
        }
    }
}


