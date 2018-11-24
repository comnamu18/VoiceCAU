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


public class StatisticActivity extends AppCompatActivity {
    DynamoDBMapper dynamoDBMapper;
    AWSCredentialsProvider credentialsProvider;
    String UserID;
    ListView listview ;
    StatisticAdapter adapter;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        UserID = getIntent().getStringExtra("USERID");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistic);



        // Adapter 생성
        adapter = new StatisticAdapter() ;

        // 리스트뷰 참조 및 Adapter달기
        listview = (ListView) findViewById(R.id.listview2);
        listview.setAdapter(adapter);

        adapter.addItem("date", "score", "songname", "userid") ;
        adapter.addItem("asdsssf", "qwssser", "zxcvzxcvss", "qwssser") ;
        getScoreStat();


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






        credentialsProvider = AWSMobileClient.getInstance().getCredentialsProvider();
        AWSConfiguration configuration = AWSMobileClient.getInstance().getConfiguration();
        // Add code to instantiate a AmazonDynamoDBClient
        AmazonDynamoDBClient dynamoDBClient = new AmazonDynamoDBClient(credentialsProvider);
        dynamoDBClient.setRegion(Region.getRegion(Regions.AP_NORTHEAST_2));
        this.dynamoDBMapper = DynamoDBMapper.builder()
                .dynamoDBClient(dynamoDBClient)
                .awsConfiguration(configuration)
                .build();

        Log.d("STAT", "STARTING");
        getScoreStat();

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

                // Loop through query results
                for (int i = 0; i < result.size(); i++) {
                    String jsonFormOfItem = gson.toJson(result.get(i));

                    String[] datas1 = jsonFormOfItem.split("\"");
                    String date = datas1[3];
                    String score = datas1[7];
                    String songname = datas1[11];
                    String userid = datas1[15];
                    adapter.addItem(date, score, songname, userid) ;


                    stringBuilder.append(jsonFormOfItem + "\n\n");
                    Log.d("date result: ", date.toString());
                    Log.d("score result: ", score.toString());
                    Log.d("songname result: ", songname.toString());
                    Log.d("userid result: ", userid.toString());

                }

                // Add your code here to deal with the data result
                Log.d("Query result: ", stringBuilder.toString());

                if (result.isEmpty()) {
                    // There were no items matching your query.
                }
            }
        }).start();
    }

    public void onBackPressed() {
        super.onBackPressed();
    }
}
