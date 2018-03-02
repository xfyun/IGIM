package com.iflytek.im.demo.common;

import android.content.Context;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.util.Log;

/**
 * Created by admin on 2017/5/15.
 */

public class PermissonUtil {
    public  static final int STATE_RECORDING=-1;
    public  static final int STATE_NO_PERMISSION=-2;
    public  static final int STATE_SUCCESS=1;
    /**
     * 用于检测是否具有录音权限
     *
     * @return
     */
    public static int getRecordState() {
        int minBuffer = AudioRecord.getMinBufferSize(44100, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT);
        AudioRecord audioRecord = new AudioRecord(MediaRecorder.AudioSource.DEFAULT, 44100, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT, (minBuffer * 100));
        short[] point = new short[minBuffer];
        int readSize = 0;
        try {

            audioRecord.startRecording();//检测是否可以进入初始化状态
        } catch (Exception e) {
            if (audioRecord != null) {
                audioRecord.release();
                audioRecord = null;
                Log.d("CheckAudioPermission","无法进入录音初始状态");
            }
            return STATE_NO_PERMISSION;
        }
        if (audioRecord.getRecordingState() != AudioRecord.RECORDSTATE_RECORDING) {
            //6.0以下机型都会返回此状态，故使用时需要判断bulid版本
            //检测是否在录音中
            if (audioRecord != null) {
                audioRecord.stop();
                audioRecord.release();
                audioRecord = null;
                Log.d("CheckAudioPermission","录音机被占用");
            }
            return STATE_RECORDING;
        } else {
            //检测是否可以获取录音结果

            readSize = audioRecord.read(point, 0, point.length);


            if (readSize <=0) {
                if (audioRecord != null) {
                    audioRecord.stop();
                    audioRecord.release();
                    audioRecord = null;

                }
                Log.d("CheckAudioPermission","录音的结果为空");
                return STATE_NO_PERMISSION;

            } else {
                if (audioRecord != null) {
                    audioRecord.stop();
                    audioRecord.release();
                    audioRecord = null;

                }

                return STATE_SUCCESS;
            }
        }
    }


























/*

    // 音频获取源
    public static int audioSource = MediaRecorder.AudioSource.MIC;
    // 设置音频采样率，44100是目前的标准，但是某些设备仍然支持22050，16000，11025
    public static int sampleRateInHz = 44100;
    // 设置音频的录制的声道CHANNEL_IN_STEREO为双声道，CHANNEL_CONFIGURATION_MONO为单声道
    public static int channelConfig = AudioFormat.CHANNEL_IN_STEREO;
    // 音频数据格式:PCM 16位每个样本。保证设备支持。PCM 8位每个样本。不一定能得到设备支持。
    public static int audioFormat = AudioFormat.ENCODING_PCM_16BIT;
    // 缓冲区字节大小
    public static int bufferSizeInBytes = 0;
    */
/**
     * 判断是是否有录音权限
     *//*

    public static boolean isHasAudioPermission(final Context context){
        bufferSizeInBytes = 0;
        bufferSizeInBytes = AudioRecord.getMinBufferSize(sampleRateInHz,
                channelConfig, audioFormat);
        AudioRecord audioRecord =  new AudioRecord(audioSource, sampleRateInHz,
                channelConfig, audioFormat, bufferSizeInBytes);
        //开始录制音频
        try{
            // 防止某些手机崩溃，例如联想
            audioRecord.startRecording();
        }catch (IllegalStateException e){
            e.printStackTrace();
        }
        */
/**
         * 根据开始录音判断是否有录音权限
         *//*

        if (audioRecord.getRecordingState() != AudioRecord.RECORDSTATE_RECORDING) {
            return false;
        }
        audioRecord.stop();
        audioRecord.release();
        audioRecord = null;

        return true;
    }
*/

}
