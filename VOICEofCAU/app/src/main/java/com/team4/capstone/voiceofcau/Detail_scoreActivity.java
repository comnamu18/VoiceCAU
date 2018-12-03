package com.team4.capstone.voiceofcau;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.AttributeSet;
import android.view.View;

import java.util.ArrayList;

public class Detail_scoreActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_score);
    }



    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}


class CanvasView2 extends View {

    public CanvasView2(Context context) {
        super(context);
    }
    public CanvasView2(Context context, AttributeSet att) {
        super(context, att);
    }
    public CanvasView2(Context context, AttributeSet att, int ref) {
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
            c.drawColor(Color.BLACK);
            Bitmap bm = Bitmap.createBitmap(singer.singerEndTime.get(singer.singerEndTime.size() - 1).intValue() * 31, 520, Bitmap.Config.ARGB_8888);
            Canvas cv = new Canvas(bm);
            paint.setColor(Color.GRAY);
            int tried = 0;
            int j = 0;
            class detailScore {
                ArrayList<Double> detailStartTime = new ArrayList<>();
                ArrayList<Double> detailEndTime = new ArrayList<>();
                ArrayList<Integer> detailInterval = new ArrayList<>();
            }
            detailScore detailScore = new detailScore();
            //cv.drawRect(10, 10, 40, 40, paint);
            for(int i = 0; i < CalculateScore.Interval.size(); i++) {
                if (tried == 0) {
                    detailScore.detailStartTime.add(CalculateScore.Time.get(i));
                    detailScore.detailEndTime.add(CalculateScore.Time.get(i));
                    detailScore.detailInterval.add(CalculateScore.Interval.get(i));
                    tried++;
                } else if (((detailScore.detailInterval.get(j)) - CalculateScore.Interval.get(i)) == 0 && CalculateScore.Time.get(i) - detailScore.detailEndTime.get(j) < 0.2) {
                    detailScore.detailEndTime.set(j, CalculateScore.Time.get(i));
                } else if (CalculateScore.Time.get(i) - detailScore.detailInterval.get(j) < 0.2 &&
                        CalculateScore.Interval.get(i) != detailScore.detailInterval.get(j)) {

                    detailScore.detailStartTime.add(CalculateScore.Time.get(i));
                    detailScore.detailEndTime.set(j, CalculateScore.Time.get(i));
                    detailScore.detailInterval.add(CalculateScore.Interval.get(i));
                } else {

                    detailScore.detailStartTime.add(CalculateScore.Time.get(i));
                    detailScore.detailEndTime.add(CalculateScore.Time.get(i));
                    detailScore.detailInterval.add(CalculateScore.Interval.get(i));
                }
            }

            for (int i = 0; i < detailScore.detailInterval.size(); i++) {
                if (detailScore.detailInterval.get(i) == 1) {
                    iheight = 72;
                } else if (detailScore.detailInterval.get(i) == 2) {
                    iheight = 70;
                } else if (detailScore.detailInterval.get(i) == 3) {
                    iheight = 68;
                } else if (detailScore.detailInterval.get(i) == 4) {
                    iheight = 66;
                } else if (detailScore.detailInterval.get(i) == 5) {
                    iheight = 64;
                } else if (detailScore.detailInterval.get(i) == 6) {
                    iheight = 62;
                } else if (detailScore.detailInterval.get(i) == 7) {
                    iheight = 58;
                } else if (detailScore.detailInterval.get(i) == 8) {
                    iheight = 56;
                } else if (detailScore.detailInterval.get(i) == 9) {
                    iheight = 54;
                } else if (detailScore.detailInterval.get(i) == 10) {
                    iheight = 52;
                } else if (detailScore.detailInterval.get(i) == 11) {
                    iheight = 50;
                } else if (detailScore.detailInterval.get(i) == 12) {
                    iheight = 48;
                } else if (detailScore.detailInterval.get(i) == 13) {
                    iheight = 46;
                } else if (detailScore.detailInterval.get(i) == 14) {
                    iheight = 42;
                } else if (detailScore.detailInterval.get(i) == 15) {
                    iheight = 40;
                } else if (detailScore.detailInterval.get(i) == 16) {
                    iheight = 38;
                } else if (detailScore.detailInterval.get(i) == 17) {
                    iheight = 36;
                } else if (detailScore.detailInterval.get(i) == 18) {
                    iheight = 32;
                } else if (detailScore.detailInterval.get(i) == 19) {
                    iheight = 30;
                } else if (detailScore.detailInterval.get(i) == 20) {
                    iheight = 28;
                } else if (detailScore.detailInterval.get(i) == 21) {
                    iheight = 26;
                } else if (detailScore.detailInterval.get(i) == 22) {
                    iheight = 24;
                } else if (detailScore.detailInterval.get(i) == 23) {
                    iheight = 22;
                } else if (detailScore.detailInterval.get(i) == 24) {
                    iheight = 20;
                } else if (detailScore.detailInterval.get(i) == 25) {
                    iheight = 16;
                } else if (detailScore.detailInterval.get(i) == 26) {
                    iheight = 14;
                } else if (detailScore.detailInterval.get(i) == 27) {
                    iheight = 12;
                } else if (detailScore.detailInterval.get(i) == 28) {
                    iheight = 10;
                }

                iheight *= 4;
                //singer.singerEndTime.set(i, singer.singerEndTime.get(i) * 31);
                //singer.singerStartTime.set(i, singer.singerStartTime.get(i) * 31);
                icurpos =detailScore.detailStartTime.get(i).intValue() * 31;
                iwidth = detailScore.detailEndTime.get(i).intValue() * 31 - detailScore.detailStartTime.get(i).intValue() * 31;

                cv.drawRect(icurpos, iheight + 16, icurpos + iwidth, iheight, paint);
            }
//        bm.setHeight(52);
//        bm.setWidth(2000);
            c.drawBitmap(bm, 10 + bx, 10, paint);
        }catch (Exception e) {
            e.printStackTrace();
        }
    }
}

