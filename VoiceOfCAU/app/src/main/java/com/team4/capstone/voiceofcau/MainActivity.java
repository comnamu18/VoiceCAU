package com.team4.capstone.voiceofcau;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
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


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    ListView listView;
    IconTextListAdapter adapter;
    SharedPreferences prefs;
    static final int MY_RECORD_PERMISSION = 78;
    static final int MY_SAVING_PERMISSION = 79;
    boolean isReocrdPermission = false;
    boolean isSavingPermission = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

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
    public boolean getRecordPermission(){
        int recordPermission = ContextCompat.checkSelfPermission(this,
                Manifest.permission.RECORD_AUDIO);
        if (recordPermission != PackageManager.PERMISSION_GRANTED) {
            Log.d("SAVING", "NOT GRANTED");
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_CONTACTS},
                    MY_RECORD_PERMISSION);
        }
        else {
            return true;
        }
        return false;
    }
    public boolean getSavingPermission(){
        int savingPermission = ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (savingPermission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_CONTACTS},
                    MY_SAVING_PERMISSION);
        }
        else {
            return true;
        }
        return false;
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        //ask for permission
        getRecordPermission();
        getSavingPermission();

        //basic settings based on shared preferences
        prefs = getSharedPreferences("MODE", MODE_PRIVATE);
        SharedPreferences.Editor ed = prefs.edit();
        if(!prefs.contains("isRecord")){
            ed.putBoolean("isRecord", false);
        }
        else{
            MenuItem item = menu.findItem(R.id.action_record);
            boolean isRecord = prefs.getBoolean("isRecord", true);
            if (isRecord && isReocrdPermission && isSavingPermission) {
                item.setIcon(R.drawable.mic_on);
            }
            //If permission denied then change settings as false
            else if (isRecord) {
                ed.putBoolean("isRecord", false);
            }
        }
        if(!prefs.contains("isScoring")){
            ed.putBoolean("isScoring", false);
        }
        else{
            MenuItem item = menu.findItem(R.id.action_sync);
            boolean isScoring = prefs.getBoolean("isScoring", true);
            if (isScoring && isReocrdPermission) {
                item.setIcon(R.drawable.pen_on);
            }
            //If permission denied then change settings as false
            else if (isScoring) {
                ed.putBoolean("isScoring", false);
            }
        }
        ed.commit();
        return true;
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_RECORD_PERMISSION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    isReocrdPermission = true;
                } else {
                    isReocrdPermission = false;
                }
                return;
            }
            case MY_SAVING_PERMISSION: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    isSavingPermission = true;
                } else {
                    isSavingPermission = false;
                }
                return;
            }
        }
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_record) {
            // check currently permission is true
            boolean checkPermission = getRecordPermission() && getSavingPermission();
            // if already has permissions or changed permissions
            if (checkPermission || (isSavingPermission && isReocrdPermission)){
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
            else{
                Toast.makeText(getApplicationContext(),
                        "record and write Permission needed for recording", Toast.LENGTH_LONG).show();
            }
        }
        else if (id == R.id.action_sync) {
            // check currently permission is true
            boolean checkPermission = getRecordPermission();
            // if already has permissions or changed permissions
            if (checkPermission || isReocrdPermission){
                boolean isRecord = !prefs.getBoolean("isScoring", true);
                SharedPreferences.Editor ed = prefs.edit();
                ed.putBoolean("isScoring", isRecord);
                ed.commit();
                if (isRecord) {
                    item.setIcon(R.drawable.pen_on);
                    Toast.makeText(getApplicationContext(), "Scoring On", Toast.LENGTH_LONG).show();
                }
                else {
                    item.setIcon(R.drawable.pen_off);
                    Toast.makeText(getApplicationContext(), "Scoring Off", Toast.LENGTH_LONG).show();
                }
            }
            else{
                Toast.makeText(getApplicationContext(),
                        "record Permission needed for scoring", Toast.LENGTH_LONG).show();
            }
        }

        else if (id == R.id.action_search) {
            Intent intent = new Intent(this, SearchActivity.class);
            startActivity(intent);
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
