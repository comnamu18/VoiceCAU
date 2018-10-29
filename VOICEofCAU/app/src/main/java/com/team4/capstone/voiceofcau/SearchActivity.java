package com.team4.capstone.voiceofcau;

import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.ListView;

public class SearchActivity extends AppCompatActivity {



    ListView listview = null ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);





            ListViewAdapter adapter;

            // Adapter 생성
            adapter = new ListViewAdapter() ;

            // 리스트뷰 참조 및 Adapter달기
            listview = (ListView) findViewById(R.id.listview1);
            listview.setAdapter(adapter);





        adapter.addItem(ContextCompat.getDrawable(this, R.drawable.play_button),"My Way","이수");
        adapter.addItem(ContextCompat.getDrawable(this, R.drawable.play_button),"가을안부","먼데이키즈");
        adapter.addItem(ContextCompat.getDrawable(this, R.drawable.play_button),"그날처럼","장덕철");
        adapter.addItem(ContextCompat.getDrawable(this, R.drawable.play_button),"그때 헤어지면 돼","로이킴");
        adapter.addItem(ContextCompat.getDrawable(this, R.drawable.play_button),"열애중","벤");
        adapter.addItem(ContextCompat.getDrawable(this, R.drawable.play_button),"gkfnri","벤");
        adapter.addItem(ContextCompat.getDrawable(this, R.drawable.play_button),"zxcvzxcv","벤");
        adapter.addItem(ContextCompat.getDrawable(this, R.drawable.play_button),"asdfzxcv","벤");
        adapter.addItem(ContextCompat.getDrawable(this, R.drawable.play_button),"asdfasdf","벤");
        adapter.addItem(ContextCompat.getDrawable(this, R.drawable.play_button),"asdfqwer","벤");

        EditText editTextFilter = (EditText)findViewById(R.id.editTextFilter) ;
        editTextFilter.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable edit) {
                String filterText = edit.toString() ;
                if (filterText.length() > 0) {
                    listview.setFilterText(filterText) ;
                } else {
                    listview.clearTextFilter() ;
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }




        }) ;
        }

}













