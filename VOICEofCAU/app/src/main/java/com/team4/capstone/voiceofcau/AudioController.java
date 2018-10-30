package com.team4.capstone.voiceofcau;

import android.app.Activity;
import android.content.Context;
import android.content.res.AssetManager;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.net.Uri;
import android.net.rtp.AudioStream;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.opencsv.CSVReader;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PipedInputStream;
import java.io.RandomAccessFile;
import java.net.URL;
import java.util.ArrayList;
import java.lang.*;
import java.util.StringTokenizer;


import be.tarsos.dsp.AudioDispatcher;
import be.tarsos.dsp.AudioEvent;
import be.tarsos.dsp.AudioProcessor;
import be.tarsos.dsp.io.PipedAudioStream;
import be.tarsos.dsp.io.TarsosDSPAudioFormat;
import be.tarsos.dsp.io.TarsosDSPAudioInputStream;
import be.tarsos.dsp.io.android.AndroidAudioInputStream;
import be.tarsos.dsp.io.android.AudioDispatcherFactory;
import be.tarsos.dsp.pitch.PitchDetectionHandler;
import be.tarsos.dsp.pitch.PitchDetectionResult;
import be.tarsos.dsp.pitch.PitchProcessor;
import be.tarsos.dsp.pitch.PitchProcessor.PitchEstimationAlgorithm;

import static java.lang.Math.abs;

public class AudioController{
    boolean isScoring = false;
    boolean isRecord = false;
    String SongName;
    String filePath;
    Context context;
    Thread scoreThread;
    AudioDispatcher dispatcher;
    AudioProcessor audioProcessor;
    FileOutputStream outputStream;

    public AudioController(Context context, String filePath, String SongName, boolean isRecord, boolean isScoring){
        this.SongName = SongName;
        this.isScoring = isScoring;
        this.isRecord = isRecord;
        this.filePath = filePath;
        this.context = context;
        if(!isScoring && !isRecord){
            return;
        }
        int sampleRate = 22050;
        int audioBufferSize = 2048;

        AudioRecord stream = new AudioRecord(MediaRecorder.AudioSource.MIC,
                sampleRate, AudioFormat.CHANNEL_IN_MONO, android.media.AudioFormat.ENCODING_PCM_16BIT, 4096);
        //Convert into TarsosDSP API
        TarsosDSPAudioFormat format = new TarsosDSPAudioFormat(sampleRate, 16, 1, true, false);
        TarsosDSPAudioInputStream audioStream = new AndroidAudioInputStream(stream, format);
        dispatcher = new AudioDispatcher(audioStream, 2048, 0);
        MyPitchDetector myPitchDetector = new MyPitchDetector();
        audioProcessor = new PitchProcessor(PitchEstimationAlgorithm.FFT_YIN, 22050, audioBufferSize, myPitchDetector);
        dispatcher.addAudioProcessor(audioProcessor);
        stream.startRecording();
        scoreThread = new Thread(dispatcher, "Dispatcher");
        scoreThread.start();

    }
    public int stopAudioProcessor(){
        if(isScoring || isRecord){
            dispatcher.stop();
            dispatcher.removeAudioProcessor(audioProcessor);
            scoreThread.interrupt();
            return getScore();
        }
        return -1;
    }

    public int getScore() {
        double score = 0;
        double totalTime = 0;

        BufferedReader bufrd = null;
        String[] str;
        ArrayList<String> singtitle = new ArrayList<>();
        ArrayList<Double> singerStartTime = new ArrayList<>();
        ArrayList<Double> singerEndTime = new ArrayList<>();
        ArrayList<Integer> singerSubInterval1 = new ArrayList<>();
        ArrayList<Integer> singerSubInterval2 = new ArrayList<>();
        StringTokenizer myTokens;

        try {
            InputStreamReader is = new InputStreamReader(context.getAssets().open("mywayscore.csv"));
            BufferedReader reader = new BufferedReader(is);
            String str2;
            while ((str2 = reader.readLine()) != null) {
                myTokens = new StringTokenizer(str2, ",");
                singtitle.add(myTokens.nextToken());
                singerStartTime.add(Double.parseDouble(myTokens.nextToken()));
                singerEndTime.add(Double.parseDouble(myTokens.nextToken()));
                singerSubInterval1.add(Integer.parseInt(myTokens.nextToken()));
                singerSubInterval2.add(Integer.parseInt(myTokens.nextToken()));
            }
            reader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        //calculate Total time on the scorecard
        for(int i = 0; i < singerStartTime.size(); i++)
        {
            totalTime = totalTime +  (singerEndTime.get(i) - singerStartTime.get(i));
            String message = String.format("Total Time = %f", totalTime);
            Log.d("Total time", message);
        }

        //Scoring
        int i = 0;
        int j = 0;
        while(j < singerStartTime.size() && i < CalculateScore.Time.size()) {
            if (CalculateScore.Time.get(i) > (singerStartTime.get(j)) && CalculateScore.Time.get(i) < (singerEndTime.get(j))) {
                //시간은 맞을 때
                if(CalculateScore.Interval.get(i) == singerSubInterval1.get(j) || CalculateScore.Interval.get(i) == singerSubInterval2.get(j)) {
                    score += ((singerEndTime.get(j) - singerStartTime.get(j)) / totalTime)*100;
                    String message = String.format("case 1 Score = %f, CalculateScoreTime : %f, CalculateScoreInterval : %d , i: %d, j: %d",
                            score, CalculateScore.Time.get(i), CalculateScore.Interval.get(i), i, j);
                    Log.d("test1", message);
                    i++;
                    j++;
                }
                else if((singerSubInterval1.get(j) == singerSubInterval2.get(j)) && (abs(CalculateScore.Interval.get(i) - singerSubInterval1.get(j)) == 1)){
                    score += ((singerEndTime.get(j) - singerStartTime.get(j)) / totalTime) * 50;
                    i++;
                    j++;
                }
                //시간은 맞는데 음정이 다를 때
                else {
                    String message = String.format("case 2 Score = %f, %d, %d : Time O Interval X", score, i, j);
                    Log.d("test2", message);
                    i++;
                }
            }
            //입력시간이 채점표시간을 뛰어넘었을 때

            else if(CalculateScore.Time.get(i) > singerEndTime.get(j)) {
                String message = String.format("case 3 Score = %f : Time X (over), %d, %d", score, i, j);
                Log.d("test3", message);
                j++;
            }
            //입력시간이 채점표 시간보다 작을때
            else if(CalculateScore.Time.get(i) < singerStartTime.get(j)){
                String message = String.format("case 4 Score = %f : Time X (low), %d, %d", score, i, j);
                Log.d("test4", message);
                i++;
            }
        }
        CalculateScore.Time.clear();
        CalculateScore.Interval.clear();

        return (int)score;
    }
}

class  MyPitchDetector implements PitchDetectionHandler{
    static int tried = 0;
    static int idx = 0;
    //Here the result of pitch is always less than half.
    @Override
    public void handlePitch(PitchDetectionResult pitchDetectionResult, AudioEvent audioEvent) {
        if(pitchDetectionResult.getPitch() != -1){
            double timeStamp = audioEvent.getTimeStamp();
            float pitch = pitchDetectionResult.getPitch();
            float probability = pitchDetectionResult.getProbability();
            double rms = audioEvent.getRMS() * 100;
            int pMinute = (int) (timeStamp/60);
            int pSecond = (int) (timeStamp%60);
            int intvNum = -1;
            String tInterval = new String();

            if(pitch >= 127.142 && pitch < 134.702) {
                tInterval = "C3";
                intvNum = 1;
            }
            else if(pitch >= 134.702 && pitch < 142.712) {
                tInterval = "C3#";
                intvNum = 2;
            }
            else if(pitch >= 142.712 && pitch < 151.198) {
                tInterval = "D3";
                intvNum=3;
            }
            else if(pitch >= 151.198 && pitch < 160.189) {
                tInterval = "D3#";
                intvNum=4;
            }
            else if(pitch >= 160.189 && pitch < 169.714) {
                tInterval = "E3";
                intvNum=5;
            }
            else if(pitch >= 169.714 && pitch < 179.806) {
                tInterval = "F3";
                intvNum=6;
            }
            else if(pitch >= 179.806 && pitch < 190.497) {
                tInterval = "F3#";
                intvNum=7;
            }
            else if(pitch >= 190.497 && pitch < 201.825) {
                tInterval = "G3";
                intvNum=8;
            }
            else if(pitch >= 201.825 && pitch < 213.826) {
                tInterval = "G3#";
                intvNum=9;
            }
            else if(pitch >= 213.826 && pitch < 226.541) {
                tInterval = "A3";
                intvNum=10;
            }
            else if(pitch >= 226.541 && pitch < 240.012) {
                tInterval = "A3#";
                intvNum=11;
            }
            else if(pitch >= 240.012 && pitch < 254.284) {
                tInterval = "B3";
                intvNum=12;
            }
            else if(pitch >= 254.284 && pitch < 269.404) {
                tInterval = "C4";
                intvNum=13;
            }
            else if(pitch >= 269.404 && pitch < 287.924) {
                tInterval = "C4#";
                intvNum=14;
            }
            else if(pitch >= 287.294 && pitch < 302.396) {
                tInterval = "D4";
                intvNum=15;
            }
            else if(pitch >= 302.396 && pitch < 320.377) {
                tInterval = "D4#";
                intvNum=16;
            }
            else if(pitch >= 320.377 && pitch < 339.428) {
                tInterval = "E4";
                intvNum=17;
            }
            else if(pitch >= 339.428 && pitch < 359.611) {
                tInterval = "F4";
                intvNum=18;
            }
            else if(pitch >= 359.611 && pitch < 380.995) {
                tInterval = "F4#";
                intvNum=19;
            }
            else if(pitch >= 380.995 && pitch < 403.65) {
                tInterval = "G4";
                intvNum=20;
            }
            else if(pitch >= 403.65 && pitch < 427.652) {
                tInterval = "G4#";
                intvNum=21;
            }
            else if(pitch >= 427.652 && pitch < 453.082) {
                tInterval = "A4";
                intvNum=22;
            }
            else if(pitch >= 453.082 && pitch < 480.234) {
                tInterval = "A4#";
                intvNum=23;
            }
            else if(pitch >= 480.234 && pitch < 508.567) {
                tInterval = "B4";
                intvNum=24;
            }
            else if(pitch >= 508.567 && pitch < 538.808) {
                tInterval = "C5";
                intvNum=25;
            }
            else if(pitch >= 538.808 && pitch < 570.847) {
                tInterval = "C5#";
                intvNum=26;
            }
            else if(pitch >= 570.847 && pitch < 604.792) {
                tInterval = "D5";
                intvNum=27;
            }
            else if(pitch >= 604.792 && pitch < 640.755) {
                tInterval = "D5#";
                intvNum=28;
            }
            else {
                tInterval = "null";
                intvNum=-1;
            }
            //Converting Ended
            if(pitch < 8000) {
                String message = String.format("Pitch detected at %d 분 %d초, %.2f: %.2fHz ( %.2f probability, RMS: %.5f ) %s",
                        pMinute, pSecond, timeStamp, pitch,probability,rms,tInterval);
                Log.d("test", message);
                if(intvNum!=-1) {
                    CalculateScore.Time.add(timeStamp);
                    CalculateScore.Interval.add(intvNum);
                }
            }
        }
    }
}


class CalculateScore {
    public static ArrayList<Double> Time = new ArrayList<>();
    public static ArrayList<Integer> Interval = new ArrayList<>();
}


