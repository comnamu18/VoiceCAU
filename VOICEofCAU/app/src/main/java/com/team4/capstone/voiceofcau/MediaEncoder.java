package com.team4.capstone.voiceofcau;

import android.annotation.TargetApi;
import android.media.MediaCodec;
import android.media.MediaCodecInfo;
import android.media.MediaFormat;
import android.os.Build;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

public class MediaEncoder {


    private static final String TAG = MediaEncoder.class.getSimpleName();

    private static final long QUEUE_TIMEOUT = 4096;

    private static final int CHANNEL_COUNT = 2;
    private static final int AAC_PROFILE = MediaCodecInfo.CodecProfileLevel.AACObjectLC;
    private static final int ADTS_SIZE = 7;
    private static final int WAV_HEADER_SIZE = 44;

    private int mAduioLen;
    public MediaEncoder(int mAudioLen) {
        this.mAduioLen = mAudioLen;
    }
    public void encode(File inputFile, File outputFile) {

        MediaCodec codec = null;
        MediaFormat format;
        FileOutputStream outputStream;

        try {
            // create input stream
            FileInputStream inputStream = new FileInputStream(inputFile);
            inputStream.skip(WAV_HEADER_SIZE);

            // create output stream
            outputStream = new FileOutputStream(outputFile);

            // set ouput mime type
            final String outputMimeType = "audio/mp4a-latm";

            // set output format
            format = new MediaFormat();
            format.setString(MediaFormat.KEY_MIME, outputMimeType);
            format.setInteger(MediaFormat.KEY_AAC_PROFILE, AAC_PROFILE);
            format.setInteger(MediaFormat.KEY_SAMPLE_RATE, 48000);
            format.setInteger(MediaFormat.KEY_CHANNEL_COUNT, CHANNEL_COUNT);
            format.setInteger(MediaFormat.KEY_BIT_RATE, 128 * 1024); // desired output(!) rate for encoder
            Log.d(TAG, "format created");

            // get and configure encoding codec
            codec = MediaCodec.createEncoderByType(outputMimeType);
            codec.configure(format, null /* surface */, null /* crypto */, MediaCodec.CONFIGURE_FLAG_ENCODE);

            // encode wav file
            encodeSong(inputStream, outputStream, codec);

            // close input and output streams
            outputStream.close();
            inputStream.close();

        } catch (Exception e) {
            Log.e(TAG, "error during encoding: " + e);

        } finally {
            if (codec != null) {
                codec.flush();
                codec.stop();
                codec.release();
            }
        }
    }

    private void encodeSong(InputStream inputStream, FileOutputStream outputStream, MediaCodec codec) {
        MediaCodec.BufferInfo info = new MediaCodec.BufferInfo();

        boolean sawInputEOS = false;
        boolean sawOutputEOS = false;
        int noOutputCounter = 0;

        codec.start();

        try {
            while (!sawOutputEOS && noOutputCounter < mAduioLen) {

                noOutputCounter++;

                // fill codec input buffers with wav data
                if (!sawInputEOS) {
                    // get index of free input buffer from codec
                    int inputBufferIndex = codec.dequeueInputBuffer(QUEUE_TIMEOUT);
                    if (inputBufferIndex >= 0) {
                        // get free input buffer from codec
                        ByteBuffer inputBuffer = codec.getInputBuffer(inputBufferIndex);
                        if (inputBuffer != null) {
                            // read wav data into byte array
                            final int bufferSize = inputBuffer.capacity();
                            byte[] buffer = new byte[bufferSize];
                            int bytesRead = inputStream.read(buffer, 0, bufferSize);
                            long presentationTimeUs = System.nanoTime();
                            if (bytesRead < 0) {
                                Log.d(TAG, "saw input EOS.");
                                sawInputEOS = true;
                                bytesRead = 0;
                            } else {
                                // put wav data into inputBuffer for encoding
                                ByteArrayOutputStream outStream = new ByteArrayOutputStream(bufferSize);
                                outStream.write(buffer, 0, bytesRead);
                                inputBuffer.put(outStream.toByteArray());
                            }
                            // queue new input buffer to encode it
                            codec.queueInputBuffer(
                                    inputBufferIndex,
                                    0,
                                    bytesRead,
                                    presentationTimeUs,
                                    sawInputEOS ? MediaCodec.BUFFER_FLAG_END_OF_STREAM : 0
                            );
                        }
                    }
                }

                // see if codec has encoded data in a new output buffer
                int outputBufferIndex = codec.dequeueOutputBuffer(info, QUEUE_TIMEOUT);
                if (outputBufferIndex >= 0) {
                    if (info.size > 0) {
                        noOutputCounter = 0;
                    }
                    // prepare output buffer including ADTS header
                    int outBitsSize = info.size;
                    int outPacketSize = outBitsSize + ADTS_SIZE;
                    ByteBuffer outputBuffer = codec.getOutputBuffer(outputBufferIndex);
                    if (outputBuffer != null) {
                        // add encoded data to file
                        drainOutputBuffer(outputStream, info, outBitsSize, outPacketSize, outputBuffer);
                    }
                    codec.releaseOutputBuffer(outputBufferIndex, false /* render */);
                    if (isEndOfStream(info)) {
                        Log.d(TAG, "saw output EOS.");
                        sawOutputEOS = true;
                    }
                }
            }
        } catch (FileNotFoundException e) {
            Log.e(TAG, "FilePath not found: " + e);
        } catch (IOException e) {
            Log.e(TAG, "Could not close FileInputStream: " + e);
        }
    }

    /**
     * extracts the packet from the outputBuffer, adds an ADTS header and appends the encoded data to
     * the outputStream (i.e. the encoded aac file).
     */
    private void drainOutputBuffer(FileOutputStream outputStream, MediaCodec.BufferInfo info, int outBitsSize, int outPacketSize, ByteBuffer outputBuffer) {
        // set position and limit of outputBuffer
        outputBuffer.position(info.offset);
        outputBuffer.limit(info.offset + outBitsSize);

        try {
            // prepare byte array containing encoded data
            byte[] data = new byte[outPacketSize];
            // add ADTS header to data packet
            addADTStoPacket(data, outPacketSize);
            // place encoded audio + ADTS header into data array
            outputBuffer.get(data, ADTS_SIZE, outBitsSize);
            // update outputBuffer position
            outputBuffer.position(info.offset);
            // only write real audio data (exclude codec info and EOS info)
            if (!isCodecInfo(info) && !isEndOfStream(info)) {
                outputStream.write(data, 0, outPacketSize);
            }
        } catch (IOException e) {
            Log.e(TAG, "failed writing bit stream data to file");
            e.printStackTrace();

        }
        outputBuffer.clear();
    }

    private boolean isEndOfStream(MediaCodec.BufferInfo info) {
        return (info.flags & MediaCodec.BUFFER_FLAG_END_OF_STREAM) != 0;
    }

    private boolean isCodecInfo(MediaCodec.BufferInfo info) {
        return (info.flags & MediaCodec.BUFFER_FLAG_CODEC_CONFIG) != 0;
    }

    /**
     * Add ADTS header at the beginning of each and every AAC packet.
     * This is needed as MediaCodec encoder generates a packet of raw
     * AAC data.<br/>
     * Note the packetLength must count in the ADTS header itself.
     **/
    private void addADTStoPacket(byte[] packet, int packetLength) {
        int profile = AAC_PROFILE;
        int chanCfg = CHANNEL_COUNT;

        // 0: 96000 Hz
        // 1: 88200 Hz
        // 2: 64000 Hz
        // 3: 48000 Hz
        // 4: 44100 Hz
        // 5: 32000 Hz
        int freqIdx = 3;

        // fill in ADTS data
        packet[0] = (byte) 0xFF;
        packet[1] = (byte) 0xF9;
        packet[2] = (byte) (((profile - 1) << 6) + (freqIdx << 2) + (chanCfg >> 2));
        packet[3] = (byte) (((chanCfg & 3) << 6) + (packetLength >> 11));
        packet[4] = (byte) ((packetLength & 0x7FF) >> 3);
        packet[5] = (byte) (((packetLength & 7) << 5) + 0x1F);
        packet[6] = (byte) 0xFC;
    }
}