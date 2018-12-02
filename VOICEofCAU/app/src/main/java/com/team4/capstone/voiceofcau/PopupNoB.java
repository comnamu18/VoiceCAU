package com.team4.capstone.voiceofcau;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.amazonaws.mobile.client.AWSMobileClient;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.amazonaws.services.s3.AmazonS3Client;

import java.io.File;

public class PopupNoB extends Activity {
    TextView txtText;
    String SongData;
    String SongName;
    int SongType;
    String ExtraPath;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //타이틀바 없애기
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_popupnobuttons);
        SongData = getIntent().getStringExtra("SongName");
        txtText = (TextView)findViewById(R.id.popupnobText);
        if (SongData != null){
            txtText.setText("Downloading...");
            SongName = SongData.split("_")[1];
            SongType = Integer.valueOf(SongData.split("_")[2]);
            switch (SongType){
                default:
                    ExtraPath = "";
                    break;
                case 2:
                    ExtraPath = "pr/";
                    break;
                case 6:
                case 7:
                case 9:
                    ExtraPath = "duet/";
                    break;
            }
            if (SongType != 9) downloadWithTransferUtility();
            else downloadWav();
        }
        else{
            finish();
        }
    }
    private void downloadWithTransferUtility() {
        TransferUtility transferUtility =
                TransferUtility.builder()
                        .context(getApplicationContext())
                        .awsConfiguration(AWSMobileClient.getInstance().getConfiguration())
                        .s3Client(new AmazonS3Client(AWSMobileClient.getInstance().getCredentialsProvider()))
                        .build();
        TransferObserver downloadObserver =
                transferUtility.download(
                        "public/" + ExtraPath + SongName + ".mp4",
                        new File("/storage/emulated/0/" + SongName +".mp4"));
        // Attach a listener to the observer to get state update and progress notifications
        downloadObserver.setTransferListener(new TransferListener() {
            @Override
            public void onStateChanged(int id, TransferState state) {
                if (TransferState.COMPLETED == state) {
                    Toast.makeText(getApplicationContext(), "DOWNLOAD COMPLETE", Toast.LENGTH_LONG).show();
                    if (SongType == 6 || SongType == 7) {
                        File wavTmp = new File("/storage/emulated/0/" + SongName + ".wav");
                        if (!wavTmp.exists()){
                            Intent intent = new Intent(getApplicationContext(), PopupNoB.class);
                            String wavDownload = SongData.split("_")[0] + "_" + SongData.split("_")[1]
                                    + "_9_" + SongData.split("_")[3];
                            intent.putExtra("SongName", wavDownload);
                            startActivityForResult(intent, MainActivity.SUCCESS_FROM_PROGRESSBAR);
                        }
                    }
                    Intent intent = new Intent();
                    intent.putExtra("SongData", SongData);
                    setResult(MainActivity.RESULT_NORMAL, intent);
                    finish();
                }
            }
            @Override
            public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {
                float percentDonef = ((float)bytesCurrent/(float)bytesTotal) * 100;
                int percentDone = (int)percentDonef;
                Log.d("DOWNLOAD", "   ID:" + id + "   bytesCurrent: " + bytesCurrent + "   bytesTotal: " + bytesTotal + " " + percentDone + "%");

            }
            @Override
            public void onError(int id, Exception ex) {
                ex.printStackTrace();
                setResult(MainActivity.RESULT_CANCEL);
                finish();
            }
        });
    }

    private void downloadWav() {
        TransferUtility transferUtility =
                TransferUtility.builder()
                        .context(getApplicationContext())
                        .awsConfiguration(AWSMobileClient.getInstance().getConfiguration())
                        .s3Client(new AmazonS3Client(AWSMobileClient.getInstance().getCredentialsProvider()))
                        .build();
        TransferObserver downloadObserver =
                transferUtility.download(
                        "public/" + ExtraPath + SongName + ".wav",
                        new File("/storage/emulated/0/" + SongName +".wav"));
        // Attach a listener to the observer to get state update and progress notifications
        downloadObserver.setTransferListener(new TransferListener() {
            @Override
            public void onStateChanged(int id, TransferState state) {
                if (TransferState.COMPLETED == state) {
                    Toast.makeText(getApplicationContext(), "DOWNLOAD COMPLETE", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent();
                    intent.putExtra("SongData", SongData);
                    setResult(MainActivity.RESULT_NORMAL, intent);
                    finish();
                }
            }
            @Override
            public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {
                float percentDonef = ((float)bytesCurrent/(float)bytesTotal) * 100;
                int percentDone = (int)percentDonef;
                Log.d("DOWNLOAD", "   ID:" + id + "   bytesCurrent: " + bytesCurrent + "   bytesTotal: " + bytesTotal + " " + percentDone + "%");

            }
            @Override
            public void onError(int id, Exception ex) {
                ex.printStackTrace();
                setResult(MainActivity.RESULT_CANCEL);
                finish();
            }
        });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case MainActivity.SUCCESS_FROM_PROGRESSBAR:
                if (resultCode != MainActivity.RESULT_CANCEL){
                    setResult(MainActivity.RESULT_CANCEL);
                    finish();
                }
                else{
                    Intent intent = new Intent();
                    intent.putExtra("SongData", SongData);
                    setResult(MainActivity.RESULT_NORMAL, intent);
                    finish();
                }
                break;
        }
    }
}
