package com.iflytek.im.demo.common.pcm2wavUtil;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.util.Log;

import com.hwangjr.rxbus.RxBus;
import com.iflytek.cloud.im.IMClient;
import com.iflytek.cloud.im.entity.msg.CommonMsgContent;
import com.iflytek.cloud.im.entity.msg.OtherSideReadedNotifyMsg;
import com.iflytek.cloud.im.listener.ResultCallback;
import com.iflytek.download.DownloadObserverInfo;
import com.iflytek.im.demo.Constants;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.X509TrustManager;


public class Pcm2Wav {

	private final String TAG	= "Pcm2Wav";


	public void text2Audio(final String wavPath, CommonMsgContent msg,final ResultCallback resultCallback)  {

		IMClient.getInstance().downloadFile( msg, false, new ResultCallback() {
            @Override
            public void onError(int errorCode) {
                Log.d(TAG,"Download text2Audio file failed");
            }

            @Override
            public void onSuccess(Object datas) {
                DownloadObserverInfo info = (DownloadObserverInfo)datas;
                Log.d(TAG,"Download text2Audio success, file path = " + info.getFilePath());
                convertWaveFile(info.getFilePath(),wavPath);
				OtherSideReadedNotifyMsg o = new OtherSideReadedNotifyMsg();
				RxBus.get().post(Constants.Event.NEW_MESSAGE_IN,o);
                resultCallback.onSuccess(datas);
            }
        });


	}






	public void processHttp(String pcmPath, String url){

		BufferedReader bufferedReader = null;
		FileOutputStream fos = null;
		BufferedWriter bufferedWriter = null;

		try {
			HttpGet get = new HttpGet(url);
			HttpClient httpClient = new DefaultHttpClient();
			HttpResponse httpResponse = httpClient.execute(get);
			HttpEntity httpEntity = httpResponse.getEntity();
			InputStream in = httpEntity.getContent();


			fos = new FileOutputStream(new File(pcmPath));
			int audiolen = (int) httpEntity.getContentLength();
			Log.e(TAG, "contentLength::::::::" + audiolen + "  in.availba::::::::" + in.available());

			byte[] bytes = new byte[10000];
			int bLen = 0;
			while ((bLen = in.read(bytes)) != -1) {
				fos.write(bytes, 0, bLen);
			}

		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (bufferedReader != null) {
					bufferedReader.close();
				}
				if (fos != null) {
					fos.close();
				}
				if (bufferedWriter != null) {
					bufferedWriter.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public void processHttps(String pcmPath, String url){

		InputStream is = null;
		FileOutputStream fos = null;
		SSLContext sc = null;
		try {
			sc = SSLContext.getInstance("TLS");
			sc.init(null, new TrustManager[]{new TrustManager()}, new SecureRandom());
			HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
			HttpsURLConnection.setDefaultHostnameVerifier(new HostNameVerifier());
			HttpsURLConnection conn = (HttpsURLConnection) new URL(url).openConnection();
			conn.setRequestMethod("GET");
			conn.setDoOutput(true);
			conn.setDoInput(true);
			conn.connect();
			is = conn.getInputStream();
			fos = new FileOutputStream(new File(pcmPath));

			byte[] bytes = new byte[10000];
			int bLen = 0;
			while ((bLen = is.read(bytes)) != -1) {
				fos.write(bytes, 0, bLen);
			}
		}catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (KeyManagementException e) {
			e.printStackTrace();
		} finally {
			try {
				if (fos != null) {
					fos.close();
				}
				if (is != null) {
					is.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

















	private void writeWaveFileHeader(FileOutputStream out, long totalAudioLen, long totalDataLen,
			long longSampleRate, int channels, long byteRate) throws IOException {
		byte[] header = new byte[44];
		header[0] = 'R'; // RIFF
		header[1] = 'I';
		header[2] = 'F';
		header[3] = 'F';
		header[4] = (byte) (totalDataLen & 0xff);// 数据大小
		header[5] = (byte) ((totalDataLen >> 8) & 0xff);
		header[6] = (byte) ((totalDataLen >> 16) & 0xff);
		header[7] = (byte) ((totalDataLen >> 24) & 0xff);
		header[8] = 'W';// WAVE
		header[9] = 'A';
		header[10] = 'V';
		header[11] = 'E';
		// FMT Chunk
		header[12] = 'f'; // 'fmt '
		header[13] = 'm';
		header[14] = 't';
		header[15] = ' ';// 过渡字节
		// 数据大小
		header[16] = 16; // 4 bytes: size of 'fmt ' chunk
		header[17] = 0;
		header[18] = 0;
		header[19] = 0;
		// 编码方式 10H为PCM编码格式
		header[20] = 1; // format = 1
		header[21] = 0;
		// 通道数
		header[22] = (byte) channels;
		header[23] = 0;
		// 采样率，每个通道的播放速度
		header[24] = (byte) (longSampleRate & 0xff);
		header[25] = (byte) ((longSampleRate >> 8) & 0xff);
		header[26] = (byte) ((longSampleRate >> 16) & 0xff);
		header[27] = (byte) ((longSampleRate >> 24) & 0xff);
		// 音频数据传送速率,采样率*通道数*采样深度/8
		header[28] = (byte) (byteRate & 0xff);
		header[29] = (byte) ((byteRate >> 8) & 0xff);
		header[30] = (byte) ((byteRate >> 16) & 0xff);
		header[31] = (byte) ((byteRate >> 24) & 0xff);
		// 确定系统一次要处理多少个这样字节的数据，确定缓冲区，通道数*采样位数
		header[32] = (byte) (1 * 16 / 8);
		header[33] = 0;
		// 每个样本的数据位数
		header[34] = 16;
		header[35] = 0;
		// Data chunk
		header[36] = 'd';// data
		header[37] = 'a';
		header[38] = 't';
		header[39] = 'a';
		header[40] = (byte) (totalAudioLen & 0xff);
		header[41] = (byte) ((totalAudioLen >> 8) & 0xff);
		header[42] = (byte) ((totalAudioLen >> 16) & 0xff);
		header[43] = (byte) ((totalAudioLen >> 24) & 0xff);
		out.write(header, 0, 44);
	}

	private void convertWaveFile(String inFileName,String outFileName) {
		//录音的采样频率
		int audioRate = 16000;
	    //录音的声道，单声道
		int audioChannel = AudioFormat.CHANNEL_IN_MONO;
	    //量化的深度
	    int audioFormat = AudioFormat.ENCODING_PCM_16BIT;

		int bufferSize = AudioRecord.getMinBufferSize(audioRate,audioChannel,audioFormat);
		if (bufferSize <= 0) {
            return;
		}
		
		FileInputStream in = null;
		FileOutputStream out = null;
		long totalAudioLen = 0;
		long totalDataLen = totalAudioLen + 36;
		long longSampleRate = audioRate;
		int channels = 1;
		long byteRate = 16 * audioRate * channels / 8;
		byte[] data = new byte[bufferSize];
		try {
			in = new FileInputStream(inFileName);
			out = new FileOutputStream(outFileName);
			totalAudioLen = in.getChannel().size();
			// 由于不包括RIFF和WAV
			totalDataLen = totalAudioLen + 36;
			writeWaveFileHeader(out, totalAudioLen, totalDataLen, longSampleRate, channels, byteRate);
			while (in.read(data) != -1) {
				out.write(data);
			}
			in.close();
			out.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static class HostNameVerifier implements HostnameVerifier {

		@Override
		public boolean verify(String hostname, SSLSession session) {
			// TODO Auto-generated method stub
			return true;
		}
	}


	private static class TrustManager implements X509TrustManager {
		@Override
		public void checkClientTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {

		}

		@Override
		public void checkServerTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {

		}

		@Override
		public X509Certificate[] getAcceptedIssuers() {
			return new X509Certificate[0];
		}
	}
	
	
	
}
