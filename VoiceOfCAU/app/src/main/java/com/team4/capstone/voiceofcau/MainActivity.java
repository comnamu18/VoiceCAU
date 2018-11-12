package com.team4.capstone.voiceofcau;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.design.widget.BottomNavigationView;
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

import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.mobile.auth.core.IdentityManager;
import com.amazonaws.mobile.client.AWSMobileClient;
import com.amazonaws.mobile.config.AWSConfiguration;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    ListView listView;
    IconTextListAdapter adapter;
    SharedPreferences prefs;
    DynamoDBMapper dynamoDBMapper;
    String UserID;

    public static final int MY_RECORD_PERMISSION = 78;
    public static final int MY_SAVING_PERMISSION = 79;
    public static final int RESULT_NORMAL = 55;
    public static final int RESULT_PRACTICE = 54;
    public static final int RESULT_DUET = 53;
    public static final int RESULT_CANCEL = -1;
    public static final int SUCCESS_FROM_POPUP = 1;
    public static final int SUCCESS_FROM_SEARCH = 2;
    public static final int RESULT_MAIN = 45;
    public static final int RESULT_CONT = 44;
    public static final int RESULT_BEGIN = 43;

    boolean isReocrdPermission = false;
    boolean isSavingPermission = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Intent intent = getIntent();
        UserID = intent.getStringExtra("UserID");
        Log.d("USER ID", UserID);

        // AWSMobileClient enables AWS user credentials to access your table
        AWSMobileClient.getInstance().initialize(this).execute();
        AWSCredentialsProvider credentialsProvider = AWSMobileClient.getInstance().getCredentialsProvider();
        AWSConfiguration configuration = AWSMobileClient.getInstance().getConfiguration();
        // Add code to instantiate a AmazonDynamoDBClient
        AmazonDynamoDBClient dynamoDBClient = new AmazonDynamoDBClient(credentialsProvider);
        this.dynamoDBMapper = DynamoDBMapper.builder()
                .dynamoDBClient(dynamoDBClient)
                .awsConfiguration(configuration)
                .build();

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
                String[] curData=curItem.getData();
                Intent intent = new Intent(getApplicationContext(), PopupActivity.class);
                // songData[2] => 1 == From main / 2 == From songscreen
                String songData = curData[0] + "_" + curData[2] + "_1_" + UserID;
                Log.d("songData Test", songData);
                intent.putExtra("Songname",songData);
                startActivityForResult(intent, SUCCESS_FROM_POPUP);
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottomnavigation);
        Menu menu = bottomNavigationView.getMenu();
        bottomNavigationView.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem item) {
                        int id = item.getItemId();
                        Intent intent;
                        prefs = getSharedPreferences("MODE", MODE_PRIVATE);
                        SharedPreferences.Editor ed = prefs.edit();
                        switch (id){
                            case R.id.action_record:
                                if (!prefs.contains("isRecord")) {
                                    ed.putBoolean("isRecord", false);
                                } else {
                                    if(!getRecordPermission()){
                                        Toast.makeText(getApplicationContext(), "Record need MIC Permissions", Toast.LENGTH_LONG).show();
                                        break;
                                    }
                                    boolean isRecord = !prefs.getBoolean("isRecord", true);
                                    if (isRecord) {
                                        item.setTitle("record On");
                                        Toast.makeText(getApplicationContext(), "Record On", Toast.LENGTH_LONG).show();
                                    } else {
                                        item.setTitle("record Off");
                                        Toast.makeText(getApplicationContext(), "Record Off", Toast.LENGTH_LONG).show();
                                    }
                                    ed.putBoolean("isRecord", isRecord);
                                    ed.commit();
                                }
                                break;
                            case R.id.action_sync:
                                if (!prefs.contains("isScoring")) {
                                    ed.putBoolean("isScoring", false);
                                } else {
                                    if(!getSavingPermission()){
                                        Toast.makeText(getApplicationContext(), "Scoring need MIC and SAVE Permissions", Toast.LENGTH_LONG).show();
                                        break;
                                    }
                                    boolean isScoring = !prefs.getBoolean("isScoring", true);
                                    if (isScoring) {
                                        item.setTitle("Scoring On");
                                        Toast.makeText(getApplicationContext(), "Scoring On", Toast.LENGTH_LONG).show();
                                    } else {
                                        item.setTitle("Scoring Off");
                                        Toast.makeText(getApplicationContext(), "Scoring Off", Toast.LENGTH_LONG).show();
                                    }
                                    ed.putBoolean("isScoring", isScoring);
                                    ed.commit();
                                }
                                break;
                            case R.id.action_search:
                                intent = new Intent(getApplicationContext(), SearchActivity.class);
                                startActivityForResult(intent, SUCCESS_FROM_SEARCH);
                                break;
                            case R.id.action_stat:
                                intent = new Intent(getApplicationContext(), StatisticActivity.class);
                                startActivity(intent);
                                break;
                        }
                        return false;
                    }
                });

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case SUCCESS_FROM_POPUP:
                switch (resultCode) {
                    case RESULT_CANCEL:
                        break;
                    default :
                        String SongData = data.getStringExtra("SongData");
                        Intent intent = new Intent(getApplicationContext(), SongscreenActivity.class);
                        intent.putExtra("Songname", SongData);
                        startActivity(intent);
                        break;
                }
                break;
            case SUCCESS_FROM_SEARCH:
                //찾은 노래 실행
                break;
        }
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
