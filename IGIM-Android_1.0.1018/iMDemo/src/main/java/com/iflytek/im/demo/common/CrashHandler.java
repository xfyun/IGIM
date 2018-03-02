package com.iflytek.im.demo.common;

import android.content.Context;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import com.iflytek.cloud.im.core.util.FileUtil;

/**
 * Created by Administrator on 2016/9/30.
 */

public class CrashHandler implements Thread.UncaughtExceptionHandler {
    private static CrashHandler instance; // 单例模式

    private Context context; // 程序Context对象
    private Thread.UncaughtExceptionHandler defalutHandler; // 系统默认的UncaughtException处理类

    private CrashHandler() {

    }

    /**
     * 获取CrashHandler实例
     *
     * @return CrashHandler
     */
    public static CrashHandler getInstance() {
        if (instance == null) {
            synchronized (CrashHandler.class) {
                if (instance == null) {
                    instance = new CrashHandler();
                }
            }
        }

        return instance;
    }

    /**
     * 异常处理初始化
     *
     * @param context
     */
    public void init(Context context) {
        this.context = context;
        // 获取系统默认的UncaughtException处理器
        defalutHandler = Thread.getDefaultUncaughtExceptionHandler();
        // 设置该CrashHandler为程序的默认处理器
        Thread.setDefaultUncaughtExceptionHandler(this);
    }

    /**
     * 当UncaughtException发生时会转入该函数来处理
     */
    @Override
    public void uncaughtException(Thread thread, Throwable ex) {

        // 自定义错误处理
        boolean res = handleException(ex);
        if (!res && defalutHandler != null) {
            // 如果用户没有处理则让系统默认的异常处理器来处理
            defalutHandler.uncaughtException(thread, ex);

        } else {
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                Log.e("CrashHandler", "error : ", e);
            }
//         退出程序
            android.os.Process.killProcess(android.os.Process.myPid());
            System.exit(1);
          }
    }

    /**
     * 自定义错误处理,收集错误信息 发送错误报告等操作均在此完成.
     *
     * @param ex
     * @return true:如果处理了该异常信息;否则返回false.
     */
    private boolean handleException(final Throwable ex) {
        if (ex == null ) {
            return false;
        }
        new Thread() {

            @Override
            public void run() {
                ex.printStackTrace();

                if( ex.getMessage() != null &&!ex.getMessage().contains("daemon_api21")){
                    String err = "[" + ex.getMessage() + "]";
                    Looper.prepare();
                    Toast.makeText(context,"程序出现异常，正在退出程序...." + err,Toast.LENGTH_LONG );
                    Looper.loop();
                }
            }

        }.start();
//        收集日志
        FileUtil.printExpectionLog(ex);
        return true;
    }

}


