package com.team4.capstone.voiceofcau;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.amazonaws.mobile.client.AWSMobileClient;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.amazonaws.services.s3.AmazonS3Client;

import java.io.File;

public class ProgressDialogActivity extends Activity {
    int percentDone = 0;
    String SongName;
    String SongData;
    String ExtraPath;
    int SongType;
    CheckTypesTask task;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SongData = getIntent().getStringExtra("SongName");
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
                ExtraPath = "duet/";
                break;
            case 7:
                ExtraPath = "duet/";
                break;
        }
        task = new CheckTypesTask();
        task.execute();
    }

    private class CheckTypesTask extends AsyncTask<Void, Void, Void> {
        ProgressDialog asyncDialog = new ProgressDialog(
                ProgressDialogActivity.this);
        @Override
        protected void onPreExecute() {
            asyncDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            asyncDialog.setMessage("다운로드 중..");
            // show dialog
            downloadWithTransferUtility();
            asyncDialog.show();
            super.onPreExecute();
        }
        @Override
        protected Void doInBackground(Void... arg0) {
            asyncDialog.setProgress(percentDone);
            return null;
        }
        @Override
        protected void onPostExecute(Void result) {
            asyncDialog.dismiss();
            super.onPostExecute(result);
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
                    Intent intent = new Intent();
                    intent.putExtra("SongData", SongData);
                    setResult(MainActivity.RESULT_NORMAL, intent);
                    finish();

                }
            }
            @Override
            public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {
                float percentDonef = ((float)bytesCurrent/(float)bytesTotal) * 100;
                percentDone = (int)percentDonef;
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

}
