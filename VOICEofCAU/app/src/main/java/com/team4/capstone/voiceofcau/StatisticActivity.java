package com.team4.capstone.voiceofcau;


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

import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.mobile.client.AWSMobileClient;
import com.amazonaws.mobile.config.AWSConfiguration;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBMapper;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBQueryExpression;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.PaginatedList;
import com.amazonaws.models.nosql.ScoreStatDO;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.ComparisonOperator;
import com.amazonaws.services.dynamodbv2.model.Condition;
import com.google.gson.Gson;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;


public class StatisticActivity extends AppCompatActivity {
    DynamoDBMapper dynamoDBMapper;
    AWSCredentialsProvider credentialsProvider;
    String UserID;
    boolean sema = false;
    ListView listview ;
    StatisticAdapter adapter;
    ArrayList<String[]>  datas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        UserID = getIntent().getStringExtra("USERID");
        datas = new ArrayList<String[]>();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistic);
        credentialsProvider = AWSMobileClient.getInstance().getCredentialsProvider();
        AWSConfiguration configuration = AWSMobileClient.getInstance().getConfiguration();

        // Add code to instantiate a AmazonDynamoDBClient
        AmazonDynamoDBClient dynamoDBClient = new AmazonDynamoDBClient(credentialsProvider);
        dynamoDBClient.setRegion(Region.getRegion(Regions.AP_NORTHEAST_2));
        this.dynamoDBMapper = DynamoDBMapper.builder()
                .dynamoDBClient(dynamoDBClient)
                .awsConfiguration(configuration)
                .build();

        // Adapter 생성
        adapter = new StatisticAdapter() ;

        // 리스트뷰 참조 및 Adapter달기
        listview = (ListView) findViewById(R.id.listview2);
        listview.setAdapter(adapter);
        getScoreStat();

        while (!sema){{
        }}
        Log.d("Out", String.valueOf(datas.size()));
        for(int i = 0; i < datas.size(); i++) {
            String[] datas1 = datas.get(i);
            String date = new String(datas1[3]);
            String score = new String(datas1[7]);
            String songname = new String(datas1[11]);
            String userid = new String(datas1[15]);

            Log.d("date result: ", date);
            Log.d("score result: ", score);
            Log.d("songname result: ", songname );
            Log.d("userid result: ", userid );
            adapter.addItem(userid, songname, score, date) ;
        }

        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView parent, View v, int position, long id) {
                // get item
                StatisticItem item = (StatisticItem) parent.getItemAtPosition(position) ;

                String dateStr = item.getdate() ;
                String scoreStr = item.getscore() ;
                String songnameStr = item.getsongname() ;
                String useridStr = item.getuserid() ;

            }
        }) ;
    }

    public void getScoreStat() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                ScoreStatDO stats = new ScoreStatDO();
                stats.setUserId(UserID);

                DynamoDBQueryExpression<ScoreStatDO> queryExpression = new DynamoDBQueryExpression<ScoreStatDO>()
                        .withHashKeyValues(stats)
                        .withConsistentRead(false);

                PaginatedList<ScoreStatDO> result = dynamoDBMapper.query(ScoreStatDO.class, queryExpression);

                Gson gson = new Gson();
                StringBuilder stringBuilder = new StringBuilder();
                //test
                // Loop through query results
                for (int i = 0; i < result.size(); i++) {
                    String jsonFormOfItem = gson.toJson(result.get(i));
                    Log.d("TESTJSON", jsonFormOfItem);
                    String[] a = jsonFormOfItem.split("\"");
                    Log.d("TESTJSON", a[3]);
                    datas.add(jsonFormOfItem.split("\""));
                    Log.d("datasLenght", String.valueOf(datas.size()));
                }

                // Add your code here to deal with the data result
                Log.d("Query result: ", stringBuilder.toString());

                if (result.isEmpty()) {
                    // There were no items matching your query.
                }
                sema = true;
            }
        }).start();
    }

    public void onBackPressed() {
        super.onBackPressed();
    }
}
