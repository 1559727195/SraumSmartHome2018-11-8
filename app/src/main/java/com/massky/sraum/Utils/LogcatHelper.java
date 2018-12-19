package com.massky.sraum.Utils;

import android.content.Context;
import android.os.Environment;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.ref.WeakReference;

public class LogcatHelper {
    private static LogcatHelper INSTANCE = null;
    private static String PATH_LOGCAT;
    private LogDumper mLogDumper = null;
    private String content;
    private WeakReference<Context> weakReference;

    /**
     * 初始化目录
     */
    public void init(Context context) {
//        if (Environment.getExternalStorageState().equals(
//                Environment.MEDIA_MOUNTED)) {// 优先保存到SD卡中
//            PATH_LOGCAT = Environment.getExternalStorageDirectory()
//                    .getAbsolutePath() + File.separator + "miniGPS";
//        } else {// 如果SD卡不存在，就保存到本应用的目录下
//            PATH_LOGCAT = context.getFilesDir().getAbsolutePath()
//                    + File.separator + "miniGPS";
//        }
        weakReference = new WeakReference<Context>(context);
        PATH_LOGCAT = weakReference.get().getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath()
                + File.separator + "crash";
        File file = new File(PATH_LOGCAT);
        if (!file.exists()) {
            file.mkdirs();
        }
    }

    public static LogcatHelper getInstance(Context context, String content) {
        if (INSTANCE == null) {
            INSTANCE = new LogcatHelper(context, content);
        }
        return INSTANCE;
    }

    private LogcatHelper(Context context, String content) {
        init(context);
        this.content = content;
//        mPId = android.os.Process.myPid();
    }

    public void start() {
        if (mLogDumper == null)
            mLogDumper = new LogDumper(content, PATH_LOGCAT);
        mLogDumper.start();
    }

    public void stop() {
        if (mLogDumper != null) {
            mLogDumper.stopLogs();
            mLogDumper = null;
        }
    }

    private class LogDumper extends Thread {

        private Process logcatProc;
        private BufferedReader mReader = null;
        private boolean mRunning = true;
        String cmds = null;
        private String content;
        private FileOutputStream out = null;

        public LogDumper(String content, String dir) {
            this.content = content;
            try {
                out = new FileOutputStream(new File(dir, "crash-"
                        + MyDate.getDateEN() + ".txt"));
            } catch (FileNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            /**
             *
             * 日志等级：*:v , *:d , *:w , *:e , *:f , *:s
             *
             * 显示当前mPID程序的 E和W等级的日志.
             *
             * */

            // cmds = "logcat *:e *:w | grep \"(" + mPID + ")\"";
            // cmds = "logcat  | grep \"(" + mPID + ")\"";//打印所有日志信息
            // cmds = "logcat -s way";//打印标签过滤信息
//            cmds = "logcat *:e *:i | grep \"(" + mPID + ")\"";

        }

        public void stopLogs() {
            mRunning = false;
        }

        @Override
        public void run() {
            try {
                if (out != null)
                    out.write((MyDate.getDateEN() + "  " + content + "\n")
                            .getBytes());

            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (logcatProc != null) {
                    logcatProc.destroy();
                    logcatProc = null;
                }
                if (mReader != null) {
                    try {
                        mReader.close();
                        mReader = null;
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if (out != null) {
                    try {
                        out.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    out = null;
                }
            }
        }
    }
}
