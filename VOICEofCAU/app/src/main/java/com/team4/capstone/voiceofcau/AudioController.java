package com.team4.capstone.voiceofcau;


import android.content.Context;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaCodec;
import android.media.MediaCodecInfo;
import android.media.MediaFormat;
import android.media.MediaRecorder;

import android.os.Environment;

import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.lang.*;
import java.util.Date;
import java.util.Locale;
import java.util.StringTokenizer;


import be.tarsos.dsp.AudioDispatcher;
import be.tarsos.dsp.AudioEvent;
import be.tarsos.dsp.AudioProcessor;
import be.tarsos.dsp.io.TarsosDSPAudioFormat;
import be.tarsos.dsp.io.TarsosDSPAudioInputStream;
import be.tarsos.dsp.io.android.AndroidAudioInputStream;
import be.tarsos.dsp.pitch.PitchDetectionHandler;
import be.tarsos.dsp.pitch.PitchDetectionResult;
import be.tarsos.dsp.pitch.PitchProcessor;
import be.tarsos.dsp.pitch.PitchProcessor.PitchEstimationAlgorithm;

import static java.lang.Math.abs;

public class    AudioController{
    private final int HEADER_SIZE = 0x2c;
    private final int RECORDER_BPP = 16;
    private final int CHANNEL = AudioFormat.CHANNEL_IN_MONO;
    private final int ENCODING = android.media.AudioFormat.ENCODING_PCM_16BIT;
    private int BUFFER_SIZE;
    private int RECORDER_SAMPLERATE;
    byte[] buffer = new byte[BUFFER_SIZE];
    private BufferedInputStream mBIStream;
    private BufferedOutputStream mBOStream;
    private int mAudioLen = 0;
    boolean isScoring;
    boolean isRecord;
    String SongName;
    String filePath;
    Context context;
    private Thread scoreThread;
    AudioDispatcher dispatcher;
    AudioProcessor audioProcessor;
    AudioRecord stream;
    File waveFile;
    File tempFile;
    File finalFile;

    public AudioController(Context context, final String filePath, final String SongName, boolean isRecord, boolean isScoring){
        this.SongName = SongName;
        this.isScoring = isScoring;
        this.isRecord = isRecord;
        this.filePath = filePath;
        this.context = context;
        if(!isScoring && !isRecord){
            return;
        }
        int audioBufferSize = 2048;
        String date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.KOREA).format(new Date());
        //Setting Files
        finalFile = new File(Environment.getExternalStorageDirectory()+"/"+ filePath + "_" + date + ".aac");
        Log.d("final", finalFile.toString());
        waveFile = new File(Environment.getExternalStorageDirectory()+"/TEST.wav");
        tempFile = new File(Environment.getExternalStorageDirectory()+"/temp.bak");

        try {
            mBOStream = new BufferedOutputStream(new FileOutputStream(tempFile));
        } catch (FileNotFoundException e1) {
            Log.d("testing", "mBOS CreateFail");
            e1.printStackTrace();
        }

        StringTokenizer myTokens;

        try {
            String scoreCSV = filePath + "score26.csv";
            InputStreamReader is = new InputStreamReader(context.getAssets().open(scoreCSV));
            BufferedReader reader = new BufferedReader(is);
            String str2;
            while ((str2 = reader.readLine()) != null) {
                myTokens = new StringTokenizer(str2, ",");
                singer.singtitle.add(myTokens.nextToken());
                singer.singerStartTime.add(Double.parseDouble(myTokens.nextToken()));
                singer.singerEndTime.add(Double.parseDouble(myTokens.nextToken()));
                singer.singerInterval.add(Integer.parseInt(myTokens.nextToken()));
            }
            reader.close();
        } catch (Exception e) {

            e.printStackTrace();
        }


        BUFFER_SIZE = 4096;
        stream = createAudioRecord();

        TarsosDSPAudioFormat format = new TarsosDSPAudioFormat(RECORDER_SAMPLERATE,
                RECORDER_BPP, 1, true, false);
        TarsosDSPAudioInputStream audioStream = new AndroidAudioInputStream(stream, format);
        dispatcher = new AudioDispatcher(audioStream, audioBufferSize, 0);
        MyPitchDetector myPitchDetector = new MyPitchDetector(mBOStream, BUFFER_SIZE, isRecord);
        audioProcessor = new PitchProcessor(PitchEstimationAlgorithm.FFT_YIN,
                RECORDER_SAMPLERATE, audioBufferSize, myPitchDetector);
        dispatcher.addAudioProcessor(audioProcessor);
        stream.startRecording();
        if(this.isScoring || this.isRecord){
            scoreThread = new Thread(dispatcher, "Dispatcher");
            scoreThread.start();
        }
    }
    private AudioRecord createAudioRecord() {
        int[] SAMPLE_RATE_CANDIDATES = {16000, 11025, 22050, 44100};
        for (int sampleRate : SAMPLE_RATE_CANDIDATES) { // 후보군 for-loop
            final int sizeInBytes = AudioRecord.getMinBufferSize(sampleRate, CHANNEL, ENCODING);
            if (sizeInBytes == AudioRecord.ERROR_BAD_VALUE) { // 값이 비정상임
                continue; // 통과
            }
            final AudioRecord audioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC,
                    sampleRate, CHANNEL, ENCODING, sizeInBytes); // AudioRecord init 시도
            if (audioRecord.getState() == AudioRecord.STATE_INITIALIZED) { // 성공?
                buffer = new byte[sizeInBytes]; // byte[] 버퍼 생성
                RECORDER_SAMPLERATE = sampleRate;
                Log.d("SampleRate", String.valueOf(sampleRate));
                return audioRecord;
            } else {
                audioRecord.release(); // 실패했으니 릴리즈.
            }
        }
        return null;
    }
    private byte[] getFileHeader() {
        byte[] header = new byte[HEADER_SIZE];
        int totalDataLen = mAudioLen + 40;
        long byteRate = RECORDER_BPP * RECORDER_SAMPLERATE * 1/8;
        header[0] = 'R';  // RIFF/WAVE header
        header[1] = 'I';
        header[2] = 'F';
        header[3] = 'F';
        header[4] = (byte) (totalDataLen & 0xff);
        header[5] = (byte) ((totalDataLen >> 8) & 0xff);
        header[6] = (byte) ((totalDataLen >> 16) & 0xff);
        header[7] = (byte) ((totalDataLen >> 24) & 0xff);
        header[8] = 'W';
        header[9] = 'A';
        header[10] = 'V';
        header[11] = 'E';
        header[12] = 'f';  // 'fmt ' chunk
        header[13] = 'm';
        header[14] = 't';
        header[15] = ' ';
        header[16] = 16;  // 4 bytes: size of 'fmt ' chunk
        header[17] = 0;
        header[18] = 0;
        header[19] = 0;
        header[20] = (byte)1;  // format = 1 (PCM방식)
        header[21] = 0;
        header[22] = 1;
        header[23] = 0;
        header[24] = (byte) (RECORDER_SAMPLERATE & 0xff);
        header[25] = (byte) ((RECORDER_SAMPLERATE >> 8) & 0xff);
        header[26] = (byte) ((RECORDER_SAMPLERATE >> 16) & 0xff);
        header[27] = (byte) ((RECORDER_SAMPLERATE >> 24) & 0xff);
        header[28] = (byte) (byteRate & 0xff);
        header[29] = (byte) ((byteRate >> 8) & 0xff);
        header[30] = (byte) ((byteRate >> 16) & 0xff);
        header[31] = (byte) ((byteRate >> 24) & 0xff);
        header[32] = (byte) RECORDER_BPP * 1/8;  // block align
        header[33] = 0;
        header[34] = RECORDER_BPP;  // bits per sample
        header[35] = 0;
        header[36] = 'd';
        header[37] = 'a';
        header[38] = 't';
        header[39] = 'a';
        header[40] = (byte)(mAudioLen & 0xff);
        header[41] = (byte)((mAudioLen >> 8) & 0xff);
        header[42] = (byte)((mAudioLen >> 16) & 0xff);
        header[43] = (byte)((mAudioLen >> 24) & 0xff);
        return header;
    }
    public int stopAudioProcessor(){
        if (isRecord) {
            // Saving as File
            try{
                int read;
                mBOStream.flush();
                mAudioLen = (int)tempFile.length();
                mBIStream = new BufferedInputStream(new FileInputStream(tempFile));
                mBOStream.close();
                mBOStream = new BufferedOutputStream(new FileOutputStream(waveFile));
                mBOStream.write(getFileHeader());
                while ((read = mBIStream.read(buffer)) != -1) {
                    mBOStream.write(buffer);
                }
                mBOStream.flush();
                mBIStream.close();
                mBOStream.close();
                /*
                MediaEncoder mediaEncoder = new MediaEncoder(mAudioLen);
                mediaEncoder.encode(waveFile, finalFile);*/
            }catch (Exception e) {
                Log.d("testing", "mBOS CreateFail");
            }
        }
        if(isScoring || isRecord){
            dispatcher.stop();
            dispatcher.removeAudioProcessor(audioProcessor);
            scoreThread.interrupt();
            scoreThread = null;
            return getScore();
        }
        return -1;
    }
    public int getScore() {
        double score = 0;
        double totalTime = 0;

        //calculate Total time on the scorecard
        for(int i = 0; i < singer.singerStartTime.size(); i++)
        {
            totalTime = totalTime +  (singer.singerEndTime.get(i) - singer.singerStartTime.get(i));
            String message = String.format("Total Time = %f", totalTime);
            Log.d("Total time", message);
        }

        //Scoring
        int i = 0;
        int j = 0;
        while(j < singer.singerStartTime.size() && i < CalculateScore.Time.size()) {
            if (CalculateScore.Time.get(i) > (singer.singerStartTime.get(j)) && CalculateScore.Time.get(i) < (singer.singerEndTime.get(j))) {
                //시간은 맞을 때
                if(CalculateScore.Interval.get(i) == singer.singerInterval.get(j)) {
                    score += ((singer.singerEndTime.get(j) - singer.singerStartTime.get(j)) / totalTime)*100;
                    String message = String.format("case 1 Score = %f, CalculateScoreTime : %f, CalculateScoreInterval : %d , i: %d, j: %d",
                            score, CalculateScore.Time.get(i), CalculateScore.Interval.get(i), i, j);
                    Log.d("test1", message);
                    i++;
                    j++;
                }
                else if((abs(CalculateScore.Interval.get(i) - singer.singerInterval.get(j)) == 1)){
                    score += ((singer.singerEndTime.get(j) - singer.singerStartTime.get(j)) / totalTime) * 50;
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

            else if(CalculateScore.Time.get(i) > singer.singerEndTime.get(j)) {
                String message = String.format("case 3 Score = %f : Time X (over), %d, %d", score, i, j);
                Log.d("test3", message);
                j++;
            }
            //입력시간이 채점표 시간보다 작을때
            else if(CalculateScore.Time.get(i) < singer.singerStartTime.get(j)){
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
    private BufferedOutputStream mBOStream;
    private int BUFFER_SIZE;
    private boolean isRecord;
    byte[] data;
    public MyPitchDetector(BufferedOutputStream mBOStream, int BUFFER_SIZE, boolean isRecord){
        this.mBOStream = mBOStream;
        this.BUFFER_SIZE = BUFFER_SIZE;
        data = new byte[BUFFER_SIZE];
        this.isRecord = isRecord;
    }
    //Here the result of pitch is always less than half.
    @Override
    public void handlePitch(PitchDetectionResult pitchDetectionResult, AudioEvent audioEvent) {
        float pitch = pitchDetectionResult.getPitch();

        if(isRecord){
            data = audioEvent.getByteBuffer();
            try{
                mBOStream.write(data, 0, BUFFER_SIZE);
            } catch (Exception e){
                Log.d("testing", "mBOS Write Fail");
            }

        }

        if(pitch != -1 && pitch < 8000){
            double timeStamp = audioEvent.getTimeStamp();
            int intvNum = getintvNum(pitch);
            CalculateScore.Time.add(timeStamp);
            CalculateScore.Interval.add(intvNum);
        }

    }

    private int getintvNum(float pitch) {
        int intvNum = 0;
        if(pitch >= 127.142 && pitch < 134.702) {
            intvNum = 1;
        }
        else if(pitch >= 134.702 && pitch < 142.712) {
            intvNum = 2;
        }
        else if(pitch >= 142.712 && pitch < 151.198) {
            intvNum=3;
        }
        else if(pitch >= 151.198 && pitch < 160.189) {
            intvNum=4;
        }
        else if(pitch >= 160.189 && pitch < 169.714) {
            intvNum=5;
        }
        else if(pitch >= 169.714 && pitch < 179.806) {
            intvNum=6;
        }
        else if(pitch >= 179.806 && pitch < 190.497) {
            intvNum=7;
            //F3#
        }
        else if(pitch >= 190.497 && pitch < 201.825) {
            intvNum=8;
        }
        else if(pitch >= 201.825 && pitch < 213.826) {
            intvNum=9;
        }
        else if(pitch >= 213.826 && pitch < 226.541) {
            intvNum=10;
        }
        else if(pitch >= 226.541 && pitch < 240.012) {
            intvNum=11;
        }
        else if(pitch >= 240.012 && pitch < 254.284) {
            intvNum=12;
        }
        else if(pitch >= 254.284 && pitch < 269.404) {
            intvNum=13;
        }
        else if(pitch >= 269.404 && pitch < 287.924) {
            intvNum=14;
        }
        else if(pitch >= 287.294 && pitch < 302.396) {
            intvNum=15;
        }
        else if(pitch >= 302.396 && pitch < 320.377) {
            intvNum=16;
        }
        else if(pitch >= 320.377 && pitch < 339.428) {
            intvNum=17;
        }
        else if(pitch >= 339.428 && pitch < 359.611) {
            intvNum=18;
        }
        else if(pitch >= 359.611 && pitch < 380.995) {
            intvNum=19;
        }
        else if(pitch >= 380.995 && pitch < 403.65) {
            intvNum=20;
        }
        else if(pitch >= 403.65 && pitch < 427.652) {
            intvNum=21;
        }
        else if(pitch >= 427.652 && pitch < 453.082) {
            intvNum=22;
        }
        else if(pitch >= 453.082 && pitch < 480.234) {
            intvNum=23;
        }
        else if(pitch >= 480.234 && pitch < 508.567) {
            intvNum=24;
        }
        else if(pitch >= 508.567 && pitch < 538.808) {
            intvNum=25;
            //C5
        }
        else if(pitch >= 538.808 && pitch < 570.847) {
            intvNum=26;
        }
        else if(pitch >= 570.847 && pitch < 604.792) {
            intvNum=27;
        }
        else if(pitch >= 604.792 && pitch < 640.755) {
            intvNum=28;
        }
        else {
            intvNum=-1;
        }
        return intvNum;
    }
}

class CalculateScore {
    public static ArrayList<Double> Time = new ArrayList<>();
    public static ArrayList<Integer> Interval = new ArrayList<>();
}

class singer {
    public static ArrayList<String> singtitle = new ArrayList<>();
    public static ArrayList<Double> singerStartTime = new ArrayList<>();
    public static ArrayList<Double> singerEndTime = new ArrayList<>();
    public static ArrayList<Integer> singerInterval = new ArrayList<>();
}
