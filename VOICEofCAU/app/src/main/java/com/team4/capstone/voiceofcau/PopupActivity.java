package com.team4.capstone.voiceofcau;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

public class PopupActivity extends Activity {

    TextView txtText;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //타이틀바 없애기
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_popup);

        //UI 객체생성
        txtText = (TextView)findViewById(R.id.txtText);

        //데이터 가져오기
        Intent intent = getIntent();
        String data = intent.getStringExtra("data");
        txtText.setText(data);

    }


    public void Return (View v){
        Intent intent = new Intent();

        setResult(SongscreenActivity.RESULT_BEGIN, intent);

        //액티비티(팝업) 닫기
        finish();

    }

    //확인 버튼 클릭
    public void Continue (View v){
        Intent intent = new Intent();

        setResult(SongscreenActivity.RESULT_CONT, intent);

        //액티비티(팝업) 닫기
        finish();

    }



    public void Main (View v){
        //데이터 전달하기
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);

        //액티비티(팝업) 닫기
        finish();
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //바깥레이어 클릭시 안닫히게
        if(event.getAction()==MotionEvent.ACTION_OUTSIDE){
            return false;
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        //안드로이드 백버튼 막기
        return;
    }
}

