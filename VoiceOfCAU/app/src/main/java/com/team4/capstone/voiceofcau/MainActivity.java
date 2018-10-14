package com.team4.capstone.voiceofcau;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.amazonaws.mobile.auth.core.IdentityManager;
import com.amazonaws.mobile.client.AWSMobileClient;
import com.opencsv.CSVReader;


import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.StringTokenizer;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    ListView listView;
    IconTextListAdapter adapter;
    SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        prefs = getSharedPreferences("MODE", MODE_PRIVATE);
        SharedPreferences.Editor ed = prefs.edit();
        ed.putBoolean("isRecord", false);
        ed.putBoolean("isScoring", false);
        ed.commit();
        //ToDO : Keep changes and setting icon
        /*
        if(!prefs.contains("isRecord")){
        }
        if(!prefs.contains("isScoring")){
        }*/

        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        listView = (ListView) findViewById(R.id.songList);
        adapter = new IconTextListAdapter(this);

        String[] song1 = new String[3];
        song1[0] = "My Way";
        song1[1] = "이수";
        song1[2] = "myway";

        String[] song2 = new String[3];
        song2[0] = "가을 안부";
        song2[1] = "먼데이키즈";
        song2[2] = "kauel";

        String[] song3 = new String[3];
        song3[0] = "그날처럼";
        song3[1] = "장덕철";
        song3[2] = "thatday";

        String[] song4 = new String[3];
        song4[0] = "그떄 헤어지면 돼";
        song4[1] = "로이킴";
        song4[2] = "thattime";

        String[] song5 = new String[3];
        song5[0] = "넋두리";
        song5[1] = "닐로";
        song5[2] = "neokduri";

        String[] song6 = new String[3];
        song6[0] = "모든 날, 모든 순간";
        song6[1] = "폴킴";
        song6[2] = "everytime";

        String[] song7 = new String[3];
        song7[0] = "열애중";
        song7[1] = "벤";
        song7[2] = "dating";

        String[] song8 = new String[3];
        song8[0] = "좋니";
        song8[1] = "윤종신";
        song8[2] = "good";

        String[] song9 = new String[3];
        song9[0] = "지나오다";
        song9[1] = "닐로";
        song9[2] = "passby";

        String[] song10 = new String[3];
        song10[0] = "첫눈처럼 너에게 가겠다";
        song10[1] = "에일리";
        song10[2] = "snow";

        Resources res = getResources();

        adapter.addItem(new IconTextItem(res.getDrawable(R.drawable.play_button),song1));
        adapter.addItem(new IconTextItem(res.getDrawable(R.drawable.play_button),song2));
        adapter.addItem(new IconTextItem(res.getDrawable(R.drawable.play_button),song3));
        adapter.addItem(new IconTextItem(res.getDrawable(R.drawable.play_button),song4));
        adapter.addItem(new IconTextItem(res.getDrawable(R.drawable.play_button),song5));
        adapter.addItem(new IconTextItem(res.getDrawable(R.drawable.play_button),song6));
        adapter.addItem(new IconTextItem(res.getDrawable(R.drawable.play_button),song7));
        adapter.addItem(new IconTextItem(res.getDrawable(R.drawable.play_button),song8));
        adapter.addItem(new IconTextItem(res.getDrawable(R.drawable.play_button),song9));
        adapter.addItem(new IconTextItem(res.getDrawable(R.drawable.play_button),song10));

        //리스트 뷰에 어댑터를 셋팅 함
        listView.setAdapter(adapter);
        //리스트 뷰를 클릭하면 해당 위치값을 받아와서 그 위치값의 Data를 읽어와서 curData에 저장한 후 Toast로 보여줌
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                IconTextItem curItem = (IconTextItem) adapter.getItem(position);
                //총 3개의 값을 가져오기 때문에 첫 번째 제목을 보여주기 위해선 배열 0번째의 값을 나타내주면 됨
                String[] curData=curItem.getData();
                Intent intent = new Intent(getApplicationContext(), SongscreenActivity.class);
                String songData = curData[0] + "_" + curData[2];
                intent.putExtra("Songname",songData);
                startActivity(intent);
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            IdentityManager.getDefaultIdentityManager().signOut();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_record) {
            boolean isRecord = !prefs.getBoolean("isRecord", true);
            SharedPreferences.Editor ed = prefs.edit();
            ed.putBoolean("isRecord", isRecord);
            ed.commit();
            if (isRecord) {
                item.setIcon(R.drawable.mic_on);
                Toast.makeText(getApplicationContext(), "Record On", Toast.LENGTH_LONG).show();
            }
            else {
                item.setIcon(R.drawable.mic_off);
                Toast.makeText(getApplicationContext(), "Record Off", Toast.LENGTH_LONG).show();
            }
        }

        else if (id == R.id.action_sync) {
            boolean isRecord = prefs.getBoolean("isRecord", true);
            if (isRecord){
                boolean isScoring = !prefs.getBoolean("isScoring", true);
                SharedPreferences.Editor ed = prefs.edit();
                ed.putBoolean("isScoring", isRecord);
                ed.commit();
                if(isScoring){
                    item.setIcon(R.drawable.pen_on);
                    Toast.makeText(getApplicationContext(),"ScoreMode On", Toast.LENGTH_LONG).show();
                }
                else{
                    item.setIcon(R.drawable.pen_off);
                    Toast.makeText(getApplicationContext(),"ScoreMode Off", Toast.LENGTH_LONG).show();
                }
            }
            else{
                Toast.makeText(getApplicationContext(),"ScoreMode need Record On", Toast.LENGTH_LONG).show();
            }
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
