package im.iflytek.com.imrecorddemo;

import android.content.Context;
import android.media.AudioManager;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.iflytek.cloud.im.IMClient;
import com.iflytek.cloud.im.entity.User;
import com.iflytek.cloud.im.entity.msg.CommonMsgContent;
import com.iflytek.cloud.im.entity.msg.MessageContent;
import com.iflytek.cloud.im.entity.msg.PostRltText;
import com.iflytek.cloud.im.listener.BuildMsgResultCallback;
import com.iflytek.cloud.im.listener.MsgListener;
import com.iflytek.cloud.im.listener.ResultCallback;
import com.iflytek.cloud.im.listener.SendMessageCallback;
import com.iflytek.cloud.im.record.AudioPlayer;
import com.iflytek.cloud.im.record.PcmRecorder;

import org.json.JSONException;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import static android.widget.Toast.LENGTH_SHORT;

public class MainActivity extends AppCompatActivity {

    private static final String TAG= "MainActivity";
    private final Context mContext =this;

    private Button startRecord ;
    private Button endRecord;
    private Button playRecord;
    private TextView voicelabel;
    private TextView voice;
    private TextView playLabel;
    private Button makeMsg;
    private Button sendMsg;
    private  TextView Msg;

    private String filePath;
    private CommonMsgContent msg;

    private String APPToken = "1a1f55c5-6c95-4f11-ad06-895778e1286d";

    //需要该用户存在
    private String receiver = "xiangsun4";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initMsgRecvListner();
        login();
        startRecord = (Button) findViewById(R.id.startrecord);
        endRecord=(Button) findViewById(R.id.endrecord);
        playRecord =(Button) findViewById(R.id.playrecord);
        voicelabel=(TextView) findViewById(R.id.label);
        voice =(TextView) findViewById(R.id.voice);
     //   progressBar =(SeekBar ) findViewById(R.id.progressBar);
        playLabel =(TextView)findViewById(R.id.player);
        makeMsg =(Button) findViewById(R.id.makeMsg);
        sendMsg =(Button) findViewById(R.id.sendMsg);
        Msg =(TextView)findViewById(R.id.msg);

        IMClient.getInstance().setDebugAble(true);
        initListener();

    }

    private void login() {
        // uid      用户ID，每一个用户都不同"uid:" + new Date().getTime()
        // name     用户名称，可重复
        // props    用户自定义属性，数据格式可为JSON，也可以是其他数据格式
        // icon     用户头像URL
        User user = new User(receiver, "name", "props", "icon");
        //具体参数设置请参照详细接口文档
        IMClient.getInstance().login(user, true, APPToken, new ResultCallback<String>() {
            @Override
            public void onError(int i) {
                Log.e(TAG, "onError: 登陆失败：" + i);
            }

            @Override
            public void onSuccess(String s) {
                Log.d(TAG, "onSuccess: 登陆成功");
            }
        });
    }

    private void initListener(){
        startRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //此处可以不设置，默认会存储在/IFlyIM_包名/ImAudioDir/
//                IMClient.getInstance().setAudioPath();

                //文件名此处设置是防止每次都重复。
                    IMClient.getInstance().startRecording("filename" + new Date().getTime(), new PcmRecorder.PcmRecordListener() {
                        @Override
                        public void onRecordBuffer(int i, double v) {
                            //
                            Log.d(TAG, "onRecordBuffer: 音量：" + v + "(单位是分贝)");
                         //   myThread mythread =new myThread(v);
                         //   mythread.run();
                              updateMicstatus((float) v);

                        }

                        @Override
                        public void onError(int i) {
                            Log.e(TAG, "onError: 录音错误，错误码是：" + i);
                        }

                        @Override
                        public void onRecordStarted(boolean b) {
                            Log.d(TAG, "onRecordStarted: 开始录音");
                        }

                        @Override
                        public void onRecordFinished(String s) {
                            Log.d(TAG, "onRecordFinished: 录音结束，录音文件路径是：" + s);
                            filePath = s;
                            msg = null;
                        }
                    });
            }
        });

        endRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IMClient.getInstance().stopRecord();
            }
        });

        playRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(filePath)) {
                    Toast.makeText(mContext, "请先录制音频", LENGTH_SHORT).show();
                    return;
                }

                IMClient.getInstance().initPlayer(filePath, new AudioPlayer.PlayerListener() {

                @Override
                    public void onPause() {
                        Log.d(TAG, "onPause: 暂停");
                    }

                    @Override
                    public void onStart() {
                        Log.d(TAG, "onStart: 开始");
//                        Toast.makeText(mContext, "播放开始", Toast.LENGTH_SHORT).show();
//                        加上此处，出现如下错误：play media failed:java.lang.RuntimeException: Can't create handler inside thread that has not called Looper.prepare()
//

                }

                    @Override
                    public void onStop() {
                        Log.d(TAG, "onStop: 停止");

//                        同上
//                        Toast.makeText(mContext, "播放停止", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onCompleted() {
                        Log.d(TAG, "onCompleted: 完成");
//                        同上
//                        Toast.makeText(mContext, "播放完成", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onError(int i) {
                        Log.d(TAG, "onError: 错误：" + i);
                    }
                });

                IMClient.getInstance().startPlay();
            }
        });


        makeMsg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(filePath)) {
                    Toast.makeText(mContext, "请先录制音频", LENGTH_SHORT).show();
                    return;
                }
                buildAudioMsg(receiver, filePath);
            }
        });

        sendMsg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (msg == null) {
                    Toast.makeText(mContext, "请先构建消息", LENGTH_SHORT).show();
                }else {
                    sendMsg(msg);
                }
            }
        });

    }

    private void buildAudioMsg(String receiver, String filePath) {
        //接口参数参照详细接口设计
        IMClient.getInstance().buildAudioMsg(receiver, filePath, false, 2, "", new BuildMsgResultCallback<CommonMsgContent>() {
            @Override
            public void onError(Object o, int i) {
                Log.d(TAG, "onError: 构建消息失败，错误码：" + i);
            }

            @Override
            public void onSuccess(CommonMsgContent commonMsgContent) {
                //发送消息
                msg = commonMsgContent;
                Toast.makeText(mContext, "onSuccess: 已构建成功消息，可以发送", LENGTH_SHORT).show();
                Log.d(TAG, "onSuccess: 已构建成功消息，可以发送");
            }
        });
    }

    private void sendMsg(CommonMsgContent msg) {

        //4000ms
        IMClient.getInstance().sendMessage(msg, 4000, new SendMessageCallback<String>() {
            @Override
            public void onFaile(String s, int i) {
                Log.e(TAG, "onFaile: 发送消息失败，错误码：" + i);
            }

            @Override
            public void onSuccess(String s, long l) {
                Log.d(TAG, "onSuccess: 发送消息成功");
            }
        });
    }

    private void initMsgRecvListner() {
        IMClient.getInstance().regMsgListener(new MsgListener() {
            @Override
            public void onMsg(MessageContent msgContent) {
                //语音转文字的结果需要在这里获取。
                // 具体的结果和消息的对应，是这个消息msgContent.getCMsgID()与发送的消息相同。

                if (msgContent instanceof CommonMsgContent) {
                    CommonMsgContent msg = (CommonMsgContent) msgContent;
                    try {
                        PostRltText text = IMClient.getInstance().parse2PostTextRlt(msg);
                     //   Toast.makeText(mContext, "转写结果：" + text.getText(), LENGTH_SHORT).show();
                        Msg.setText(text.getText());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    private void updateMicstatus(float v){

        Bundle bundle=new Bundle();
        bundle.putFloat("voice",(float)v);
        Message message = new Message();
        message.what = 0;
        message.setData(bundle);
        mUpdateMicStatusTimer.setV(v);
        myHandler.postDelayed(mUpdateMicStatusTimer,200);
        myHandler.sendMessage(message);

    }

    protected  class NewRunnable implements Runnable{
        float v;
        public void setV(float v){
            this.v= v;
        }
        @Override
        public void run() {
            updateMicstatus(v);
        }
    }

    private NewRunnable mUpdateMicStatusTimer = new NewRunnable();

    Handler myHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                   voice.setText(Float.toString(msg.getData().getFloat("voice")));
                    break;
            }
            super.handleMessage(msg);
        }
    };









}
