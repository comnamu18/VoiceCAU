package com.team4.capstone.voiceofcau;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.content.res.Resources;
import android.media.MediaCodec;
import android.media.MediaCodecInfo;
import android.media.MediaExtractor;
import android.media.MediaFormat;
import android.media.MediaMuxer;
import android.os.Environment;
import android.util.Log;

import com.coremedia.iso.boxes.Container;
import com.googlecode.mp4parser.authoring.Movie;
import com.googlecode.mp4parser.authoring.Track;
import com.googlecode.mp4parser.authoring.builder.DefaultMp4Builder;
import com.googlecode.mp4parser.authoring.container.mp4.MovieCreator;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.BufferOverflowException;
import java.nio.ByteBuffer;
import java.nio.channels.WritableByteChannel;

//https://github.com/tqnst/MP4ParserMergeAudioVideo/blob/master/Mp4ParserSample-master/src/jp/classmethod/sample/mp4parser/MainActivity.java
//https://stackoverflow.com/questions/8526552/encode-wav-to-aac-on-android
//https://github.com/sannies/mp4parser/blob/master/README.md
public class TestOverLay {
    public static String AUDIO_RECORDING_FILE_NAME; // Input PCM file
    public static final String COMPRESSED_AUDIO_FILE_NAME = "/storage/emulated/0/converted.mp4"; // Output MP4/M4A file
    public static final String COMPRESSED_AUDIO_FILE_MIME_TYPE = "audio/mp4a-latm";
    public static final int COMPRESSED_AUDIO_FILE_BIT_RATE = 64000; // 64kbps
    public static final int SAMPLING_RATE = 16000;
    public static final int BUFFER_SIZE = 48000;
    public static final int CODEC_TIMEOUT_IN_MS = 5000;
    public static boolean isConverted = false;
    String LOGTAG = "CONVERT AUDIO";
    public TestOverLay(String audioFile){
        AUDIO_RECORDING_FILE_NAME = audioFile;
        Runnable convert = new Runnable() {
            @Override
            public void run() {
                try {
                    String filePath = AUDIO_RECORDING_FILE_NAME;
                    File inputFile = new File(filePath);
                    FileInputStream fis = new FileInputStream(inputFile);

                    File outputFile = new File(COMPRESSED_AUDIO_FILE_NAME);
                    if (outputFile.exists()) outputFile.delete();

                    MediaMuxer mux = new MediaMuxer(outputFile.getAbsolutePath(), MediaMuxer.OutputFormat.MUXER_OUTPUT_MPEG_4);

                    MediaFormat outputFormat = MediaFormat.createAudioFormat(COMPRESSED_AUDIO_FILE_MIME_TYPE,SAMPLING_RATE, 1);
                    outputFormat.setInteger(MediaFormat.KEY_AAC_PROFILE, MediaCodecInfo.CodecProfileLevel.AACObjectLC);
                    outputFormat.setInteger(MediaFormat.KEY_BIT_RATE, COMPRESSED_AUDIO_FILE_BIT_RATE);
                    outputFormat.setInteger(MediaFormat.KEY_MAX_INPUT_SIZE, 16384);

                    MediaCodec codec = MediaCodec.createEncoderByType(COMPRESSED_AUDIO_FILE_MIME_TYPE);
                    codec.configure(outputFormat, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE);
                    codec.start();

                    ByteBuffer[] codecInputBuffers = codec.getInputBuffers(); // Note: Array of buffers
                    ByteBuffer[] codecOutputBuffers = codec.getOutputBuffers();

                    MediaCodec.BufferInfo outBuffInfo = new MediaCodec.BufferInfo();
                    byte[] tempBuffer = new byte[BUFFER_SIZE];
                    boolean hasMoreData = true;
                    double presentationTimeUs = 0;
                    int audioTrackIdx = 0;
                    int totalBytesRead = 0;
                    int percentComplete = 0;
                    do {
                        int inputBufIndex = 0;
                        while (inputBufIndex != -1 && hasMoreData) {
                            inputBufIndex = codec.dequeueInputBuffer(CODEC_TIMEOUT_IN_MS);

                            if (inputBufIndex >= 0) {
                                ByteBuffer dstBuf = codecInputBuffers[inputBufIndex];
                                dstBuf.clear();

                                int bytesRead = fis.read(tempBuffer, 0, dstBuf.limit());
                                Log.e("bytesRead","Readed "+bytesRead);
                                if (bytesRead == -1) { // -1 implies EOS
                                    hasMoreData = false;
                                    codec.queueInputBuffer(inputBufIndex, 0, 0, (long) presentationTimeUs, MediaCodec.BUFFER_FLAG_END_OF_STREAM);
                                } else {
                                    totalBytesRead += bytesRead;
                                    dstBuf.put(tempBuffer, 0, bytesRead);
                                    codec.queueInputBuffer(inputBufIndex, 0, bytesRead, (long) presentationTimeUs, 0);
                                    presentationTimeUs = 1000000l * (totalBytesRead / 2) / SAMPLING_RATE;
                                }
                            }
                        }
                        // Drain audio
                        int outputBufIndex = 0;
                        while (outputBufIndex != MediaCodec.INFO_TRY_AGAIN_LATER) {
                            outputBufIndex = codec.dequeueOutputBuffer(outBuffInfo, CODEC_TIMEOUT_IN_MS);
                            if (outputBufIndex >= 0) {
                                ByteBuffer encodedData = codecOutputBuffers[outputBufIndex];
                                encodedData.position(outBuffInfo.offset);
                                encodedData.limit(outBuffInfo.offset + outBuffInfo.size);
                                if ((outBuffInfo.flags & MediaCodec.BUFFER_FLAG_CODEC_CONFIG) != 0 && outBuffInfo.size != 0) {
                                    codec.releaseOutputBuffer(outputBufIndex, false);
                                }else{
                                    mux.writeSampleData(audioTrackIdx, codecOutputBuffers[outputBufIndex], outBuffInfo);
                                    codec.releaseOutputBuffer(outputBufIndex, false);
                                }
                            } else if (outputBufIndex == MediaCodec.INFO_OUTPUT_FORMAT_CHANGED) {
                                outputFormat = codec.getOutputFormat();
                                Log.v(LOGTAG, "Output format changed - " + outputFormat);
                                audioTrackIdx = mux.addTrack(outputFormat);
                                mux.start();
                            } else if (outputBufIndex == MediaCodec.INFO_OUTPUT_BUFFERS_CHANGED) {
                                Log.e(LOGTAG, "Output buffers changed during encode!");
                            } else if (outputBufIndex == MediaCodec.INFO_TRY_AGAIN_LATER) {
                                // NO OP
                            } else {
                                Log.e(LOGTAG, "Unknown return code from dequeueOutputBuffer - " + outputBufIndex);
                            }
                        }
                        percentComplete = (int) Math.round(((float) totalBytesRead / (float) inputFile.length()) * 100.0);
                        Log.v(LOGTAG, "Conversion % - " + percentComplete);
                    } while (outBuffInfo.flags != MediaCodec.BUFFER_FLAG_END_OF_STREAM);
                    fis.close();
                    mux.stop();
                    mux.release();
                    Log.v(LOGTAG, "Compression done ...");
                    isConverted = true;
                } catch (FileNotFoundException e) {
                    Log.e(LOGTAG, "File not found!", e);
                } catch (IOException e) {
                    Log.e(LOGTAG, "IO exception!", e);
                }

                //mStop = false;
                // Notify UI thread...
            }
        };
        convert.run();
    }

    public boolean muxer(String videoFile, String outputFile) {
        outputFile = "/storage/emulated/0/sample.m4a";
        while(!isConverted){
        }
        Movie video;
        try {
            video = new MovieCreator().build(videoFile);
        } catch (RuntimeException e) {
            e.printStackTrace();
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        Movie audio;
        try {
            audio = new MovieCreator().build(outputFile);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        } catch (NullPointerException e) {
            e.printStackTrace();
            return false;
        }

        Track audioTrack = audio.getTracks().get(0);
        video.addTrack(audioTrack);

        Container out = new DefaultMp4Builder().build(video);

        FileOutputStream fos;
        try {
            fos = new FileOutputStream(outputFile);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return false;
        }
        BufferedWritableFileByteChannel byteBufferByteChannel = new BufferedWritableFileByteChannel(fos);
        try {
            out.writeContainer(byteBufferByteChannel);
            byteBufferByteChannel.close();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    private static class BufferedWritableFileByteChannel implements WritableByteChannel {
        private static final int BUFFER_CAPACITY = 1000000;

        private boolean isOpen = true;
        private final OutputStream outputStream;
        private final ByteBuffer byteBuffer;
        private final byte[] rawBuffer = new byte[BUFFER_CAPACITY];

        private BufferedWritableFileByteChannel(OutputStream outputStream) {
            this.outputStream = outputStream;
            this.byteBuffer = ByteBuffer.wrap(rawBuffer);
        }

        @Override
        public int write(ByteBuffer inputBuffer) throws IOException {
            int inputBytes = inputBuffer.remaining();

            if (inputBytes > byteBuffer.remaining()) {
                dumpToFile();
                byteBuffer.clear();

                if (inputBytes > byteBuffer.remaining()) {
                    throw new BufferOverflowException();
                }
            }

            byteBuffer.put(inputBuffer);

            return inputBytes;
        }

        @Override
        public boolean isOpen() {
            return isOpen;
        }

        @Override
        public void close() throws IOException {
            dumpToFile();
            isOpen = false;
        }
        private void dumpToFile() {
            try {
                outputStream.write(rawBuffer, 0, byteBuffer.position());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }


}