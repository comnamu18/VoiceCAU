import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.lang.*;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;

import be.tarsos.dsp.AudioDispatcher;
import be.tarsos.dsp.AudioEvent;
import be.tarsos.dsp.io.jvm.JVMAudioInputStream;
import be.tarsos.dsp.pitch.PitchDetectionHandler;
import be.tarsos.dsp.pitch.PitchDetectionResult;
import be.tarsos.dsp.pitch.PitchProcessor;
import be.tarsos.dsp.pitch.PitchProcessor.PitchEstimationAlgorithm;

public class main {

public static void main(String[] args){
    try{
        float sampleRate = 44100;
        int audioBufferSize = 2048;
        int bufferOverlap = 0;

        //Create an AudioInputStream from my .wav file
        URL soundURL = main.class.getResource("DetectPicthFromWav/myway.WAV");
        System.out.println(soundURL);
        AudioInputStream stream = AudioSystem.getAudioInputStream(soundURL);

        //Convert into TarsosDSP API
        JVMAudioInputStream audioStream = new JVMAudioInputStream(stream);
        AudioDispatcher dispatcher = new AudioDispatcher(audioStream, audioBufferSize, bufferOverlap);
        MyPitchDetector myPitchDetector = new MyPitchDetector();

        dispatcher.addAudioProcessor(new PitchProcessor(PitchEstimationAlgorithm.YIN, sampleRate, audioBufferSize, myPitchDetector));
        dispatcher.run();


    }
    catch(FileNotFoundException fne){fne.printStackTrace();}
    catch(UnsupportedAudioFileException uafe){uafe.printStackTrace();}
    catch(IOException ie){ie.printStackTrace();}
}
}

    class  MyPitchDetector implements PitchDetectionHandler{

//Here the result of pitch is always less than half.
@Override
public void handlePitch(PitchDetectionResult pitchDetectionResult,
        AudioEvent audioEvent) {
    if(pitchDetectionResult.getPitch() != -1){
        double timeStamp = audioEvent.getTimeStamp();
        float pitch = pitchDetectionResult.getPitch();
        float probability = pitchDetectionResult.getProbability();
        double rms = audioEvent.getRMS() * 100;
        int pMinute = (int) (timeStamp/60);
        int pSecond = (int) (timeStamp%60);
        if(pitch < 5000 && probability > 0.90) {
        	String message = String.format("Pitch detected at %d ∫– %d√ : %.2fHz ( %.2f probability, RMS: %.5f )", pMinute, pSecond, pitch,probability,rms);
        	System.out.println(message);
        }
    }
}
}