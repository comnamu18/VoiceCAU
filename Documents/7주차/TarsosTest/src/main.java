import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
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
        URL soundURL = main.class.getResource("DetectPicthFromWav/myway.wav");
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
    
    String cmessage = new String();
    for(int i = 0; i < CalculateScore.startTime.size(); i++) {
    	if(CalculateScore.subInterval1.get(i) != -1) {
    		cmessage = String.format("시간 : %.2f ~ %.2f 음정1 : %d 음정2 : %d ", CalculateScore.startTime.get(i),
			CalculateScore.endTime.get(i), CalculateScore.subInterval1.get(i), CalculateScore.subInterval2.get(i));
    		System.out.println(cmessage);
    	}
    }
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
			pitch *= 2; // Because of bit of file
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
			if(pitch < 8000 && probability > 0.85) {
				String message = String.format("Pitch detected at %d 분 %d초, %.2f: %.2fHz ( %.2f probability, RMS: %.5f ) %s", 
						pMinute, pSecond, timeStamp, pitch,probability,rms,tInterval);
				// System.out.println(message);
				if(tried == 0) {
					CalculateScore.startTime.add(timeStamp);
					CalculateScore.endTime.add(timeStamp);
					CalculateScore.subInterval1.add(intvNum);
					CalculateScore.subInterval2.add(intvNum);
					CalculateScore.realInterval.add(null);
					tried++;
				}
				else if(((CalculateScore.subInterval1.get(idx)) - intvNum) == 0 || Math.abs(((CalculateScore.subInterval1.get(idx)) - intvNum)) == 1) {
					CalculateScore.endTime.set(idx, timeStamp);
					CalculateScore.subInterval2.set(idx, intvNum);
					
				}
				else {
					CalculateScore.endTime.set(idx, timeStamp);
					CalculateScore.startTime.add(timeStamp);
					CalculateScore.endTime.add(timeStamp);
					CalculateScore.subInterval1.add(intvNum);
					CalculateScore.subInterval2.add(intvNum);
					CalculateScore.realInterval.add(null);
					idx++;
				}

			}
		}
	}
}


class CalculateScore {
	public static ArrayList<Double> startTime = new ArrayList<>();
	public static ArrayList<Double> endTime = new ArrayList<>();
	public static ArrayList<Integer> subInterval1 = new ArrayList<>();
	public static ArrayList<Integer> subInterval2 = new ArrayList<>();
	public static ArrayList<Integer> realInterval = new ArrayList<>();
}



    
   