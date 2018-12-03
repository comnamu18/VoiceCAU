package com.team4.capstone.voiceofcau;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;

import java.util.List;


public class SearchActivity extends AppCompatActivity {
    ListView listview = null ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        final ListViewAdapter adapter = new ListViewAdapter();
        // 리스트뷰 참조 및 Adapter달기
        listview = (ListView) findViewById(R.id.listview1);
        listview.setAdapter(adapter);

        adapter.addItem(ContextCompat.getDrawable(this, R.drawable.play_button),"My Way","이수", "myway");
        adapter.addItem(ContextCompat.getDrawable(this, R.drawable.play_button),"가을안부","먼데이키즈", "kauel");
        adapter.addItem(ContextCompat.getDrawable(this, R.drawable.play_button),"그날처럼","장덕철", "thatday");
        adapter.addItem(ContextCompat.getDrawable(this, R.drawable.play_button),"그때 헤어지면 돼","로이킴", "thattime");
        adapter.addItem(ContextCompat.getDrawable(this, R.drawable.play_button),"넋두리","닐로", "neokduri");
        adapter.addItem(ContextCompat.getDrawable(this, R.drawable.play_button),"모든 날, 모든 순간","폴킴", "everytime");
        adapter.addItem(ContextCompat.getDrawable(this, R.drawable.play_button),"열애중","벤", "dating");
        adapter.addItem(ContextCompat.getDrawable(this, R.drawable.play_button),"좋니","윤종신", "good");
        adapter.addItem(ContextCompat.getDrawable(this, R.drawable.play_button),"지나오다","닐로", "passby");
        adapter.addItem(ContextCompat.getDrawable(this, R.drawable.play_button),"첫눈처럼 너에게 가겠다","에일리", "snow");
        adapter.addItem(ContextCompat.getDrawable(this, R.drawable.play_button),"징글벨","데모곡", "demo");

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
        });
        //리스트 뷰를 클릭하면 해당 위치값을 받아와서 그 위치값의 Data를 읽어와서 curData에 저장한 후 Toast로 보여줌
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ListViewItem curItem = (ListViewItem) adapter.getItem(position);
                String[] curData=curItem.getData();
                Intent intent = new Intent();
                String songData = curData[0] + "_" + curData[2] + "_1_";
                intent.putExtra("Songname",songData);
                setResult(MainActivity.RESULT_NORMAL, intent);
                finish();
            }
        });
    }
    @Override
    public void onBackPressed() {
        setResult(MainActivity.RESULT_CANCEL);
        finish();
        return;
    }
}













