package com.team4.capstone.voiceofcau;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class PopupActivity extends Activity {
    TextView txtText;
    private String[] datas;
    private int type;
    private String songName;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent data = getIntent();
        datas = data.getStringExtra("Songname").split("_");
        songName = datas[0];
        type = Integer.parseInt(datas[2]);
        //타이틀바 없애기
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_popup);
        TestOverLay test = new TestOverLay();

        try{
            test.mixSound("!","2","3");
        }catch (Exception e){
            e.printStackTrace();
        }

        //UI 객체생성
        txtText = (TextView)findViewById(R.id.popupText);
        if (type < 3){
            txtText.setText(songName);
            Button button = (Button)findViewById(R.id.popup_button1);
            button.setText(R.string.popup_button11);
            button = (Button)findViewById(R.id.popup_button2);
            button.setText(R.string.popup_button21);
            button = (Button)findViewById(R.id.popup_button3);
            button.setText(R.string.popup_button31);
        }
        else if(type == 3){
            txtText.setText(songName);
            Button button = (Button)findViewById(R.id.popup_button1);
            button.setText("1번 파트");
            button = (Button)findViewById(R.id.popup_button2);
            button.setText("2번 파트");
            button = (Button)findViewById(R.id.popup_button3);
            button.setVisibility(View.GONE);
        }
        else {
            txtText.setText(R.string.popup_notice);
        }

    }
    //돌아가기 or 일반모드
    public void Return (View v){
        if(type < 3){
            Intent intent = new Intent();
            String SongData = datas[0] + "_" + datas[1] + "_1_" + datas[3];
            intent.putExtra("SongData", SongData);
            setResult(MainActivity.RESULT_NORMAL, intent);
            finish();
        }
        else if(type == 3){
            Intent intent = new Intent();
            String SongData = datas[0] + "_" + datas[1] + "_6_" + datas[3];
            intent.putExtra("SongData", SongData);
            setResult(MainActivity.RESULT_PART_A, intent);

            finish();
        }
        else {
            setResult(MainActivity.RESULT_BEGIN);
            finish();
        }
    }

    //계속하기 or 연습모드
    public void Continue (View v){
        if(type < 1){
            Intent intent = new Intent();
            String SongData = datas[0] + "_" + datas[1] + "_2_" + datas[3];
            intent.putExtra("SongData", SongData);
            setResult(MainActivity.RESULT_PRACTICE, intent);
            finish();
        }
        else if(type == 3){
            Intent intent = new Intent();
            String SongData = datas[0] + "_" + datas[1] + "_7_" + datas[3];
            intent.putExtra("SongData", SongData);
            setResult(MainActivity.RESULT_PART_B, intent);
            finish();
        }
        else {
            setResult(MainActivity.RESULT_CONT);
            finish();
        }
    }

    //메인 메뉴로 돌아가기 or 듀엣모드
    public void Main (View v){
        if(type < 4){
            Intent intent = new Intent();
            String SongData = datas[0] + "_" + datas[1] + "_3_" + datas[3];
            intent.putExtra("SongData", SongData);
            setResult(MainActivity.RESULT_DUET, intent);
            finish();
        }
        else {
            setResult(MainActivity.RESULT_MAIN);
            finish();
        }
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if(type < 4){
            setResult(MainActivity.RESULT_CANCEL);
            finish();
        }
        //바깥레이어 클릭시 안닫히게
        if(event.getAction()==MotionEvent.ACTION_OUTSIDE){
            return false;
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        if(type < 4){
            setResult(MainActivity.RESULT_CANCEL);
            finish();
        }
        //안드로이드 백버튼 막기
        return;
    }
}

