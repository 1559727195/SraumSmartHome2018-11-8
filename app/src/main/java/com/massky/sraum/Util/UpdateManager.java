package com.massky.sraum.Util;

import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import androidx.core.app.NotificationCompat;
import androidx.core.content.FileProvider;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;
import com.massky.sraum.R;
import com.massky.sraum.Util.breakpoint.DownloadProgressListener;
import com.massky.sraum.Util.breakpoint.FileDownloadered;
import com.massky.sraum.Utils.App;
import com.massky.sraum.activity.MainGateWayActivity;
import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.List;
import java.util.Map;


/**
 * @author xufuchao
 * @date 2016-12-24
 * @blog
 */
//用于自动版本更新和安装
public class UpdateManager implements MainGateWayActivity.UpdateApkListener {
    /* 下载中 */
    private static final int DOWNLOAD = 1;
    /* 下载结束 */
    private static final int DOWNLOAD_FINISH = 2;
    private final String apkName;
    /* 下载保存路径 */
    private String mSavePath;
    /* 记录进度条数量 */
    private int progress;
    /* 是否取消更新 */
    private boolean cancelUpdate = false;
    private static final int PROCESSING = 1;   //正在下载实时数据传输Message标志
    private static final int FAILURE = -1;     //下载失败时的Message标志

    private Context mContext;
    /* 更新进度条 */
    private ProgressBar mProgress;
    private Dialog mDownloadDialog;
    private String UpApkUrl;
    private Handler mHandler = new Handler() {

        public void handleMessage(Message msg) {
            switch (msg.what) {
                // 正在下载
                case DOWNLOAD:
                    // 设置进度条位置
//                    mProgress.setProgress(progress);
                    mBuilder.setContentTitle("版本升级").setContentText(progress + "%");
                    mBuilder.setProgress(100, progress, false);
                    mNotifyManager.notify(id, mBuilder.build());
                    break;
                case DOWNLOAD_FINISH:
                    // 安装文件
                    mBuilder.setProgress(0, 0, false).setContentTitle("下载完成").setContentText("");
                    mNotifyManager.notify(id, mBuilder.build());
                    installApk();
                    break;
                default:
                    break;
            }
        }
    };

    private int id = 1;
    private NotificationManager mNotifyManager;
    private NotificationCompat.Builder mBuilder;
    private int maxsize;
    private File saveFile;
    //    private MessageReceiver mMessageReceiver;
    private boolean isnotify_close;
    private int index;

    private void notification() {

        mNotifyManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
        mBuilder = new NotificationCompat.Builder(mContext);
        mBuilder.setContentTitle("版本升级").setContentText("下载中，请稍等……");
        mBuilder.setProgress(100, 0, false);
        mBuilder.setSmallIcon(R.drawable.jpush_notification_icon);// 设置通知小ICON
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {//悬挂式Notification，5.0后显示
//            mBuilder.setSmallIcon(R.drawable.jpush_notification_icon);// 设置通知小ICON（5.0必须采用白色透明图片）
//        }else{
//            mBuilder.setSmallIcon(R.drawable.jpush_notification_icon);// 设置通知小ICON
//        }
        mNotifyManager.notify(id, mBuilder.build());
    }

    public static int threadCount = 10;//进行下载的线程数量
    private List<Map> list;

    public UpdateManager(Context context, String UpApkUrl, String apkName) {
        this.mContext = context;
        this.UpApkUrl = UpApkUrl;
        this.apkName = apkName;
    }

    /**
     * 显示软件下载对话框
     */
    public void showDownloadDialog() {
        // 构造软件下载对话框
//        registerMessageReceiver();
        Builder builder = new Builder(mContext);
        builder.setTitle(R.string.soft_updating);
        // 给下载对话框增加进度条
        final LayoutInflater inflater = LayoutInflater.from(mContext);
        View v = inflater.inflate(R.layout.softupdate_progress, null);
        mProgress = (ProgressBar) v.findViewById(R.id.update_progress);
        builder.setView(v);
        // 取消更新
        builder.setNegativeButton(R.string.soft_update_cancel, new OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                // 设置取消状态
                cancelUpdate = true;
            }
        });
        mDownloadDialog = builder.create();
        mDownloadDialog.setCanceledOnTouchOutside(false);
//        mDownloadDialog.show();
        // 现在文件
//        notification();
        downloadApk();
    }


    /**
     * 下载apk文件
     */
    private void downloadApk() {
        // 启动新线程下载软件
//        new downloadApkThread().start();
//        String path = editpath.getText().toString();
        new Thread(new Runnable() {
            @Override
            public void run() {
                get_url_file_size();
            }
        }).start();

        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            File saveDir = Environment.getExternalStorageDirectory();
            if (!saveDir.exists()) saveDir.mkdir();  //如果文件不存在的话指定目录,这里可创建多层目录
            SharedPreferencesUtil.saveData(mContext, "loadapk", true);
//            download(UpApkUrl, saveDir);
            AppDownloadManager appDownloadManager = new AppDownloadManager(App.getInstance());

            appDownloadManager.downloadApk(UpApkUrl, "sraum正在下载", apkName);
        } else {
            Toast.makeText(mContext, "sd卡读取失败",

                    1).show();
        }
    }

    /**
     * 获得网络端apk文件总大小
     */
    private void get_url_file_size() {
        URL url = null;     //根据下载路径实例化URL
        try {
            url = new URL(UpApkUrl);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        HttpURLConnection conn = null;   //创建远程连接句柄,这里并未真正连接
        try {
            conn = (HttpURLConnection) url.openConnection();
        } catch (IOException e) {
            e.printStackTrace();
        }
        conn.setConnectTimeout(5000);      //设置连接超时事件为5秒
        try {
            conn.setRequestMethod("GET");      //设置请求方式为GET
        } catch (ProtocolException e) {
            e.printStackTrace();
        }
        //设置用户端可以接收的媒体类型
        conn.setRequestProperty("Accept", "image/gif, image/jpeg, image/pjpeg, " +
                "image/pjpeg, application/x-shockwave-flash, application/xaml+xml, " +
                "application/vnd.ms-xpsdocument, application/x-ms-xbap," +
                " application/x-ms-application, application/vnd.ms-excel," +
                " application/vnd.ms-powerpoint, application/msword, */*");

        conn.setRequestProperty("Accept-Language", "zh-CN");  //设置用户语言
        conn.setRequestProperty("Referer", UpApkUrl);    //设置请求的来源页面,便于服务端进行来源统计
        conn.setRequestProperty("Charset", "UTF-8");    //设置客户端编码
        //设置用户代理
        conn.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 8.0; " +
                "Windows NT 5.2; Trident/4.0; .NET CLR 1.1.4322; .NET CLR 2.0.50727;" +
                " .NET CLR 3.0.04506.30; .NET CLR 3.0.4506.2152; .NET CLR 3.5.30729)");

        conn.setRequestProperty("Connection", "Keep-Alive");  //设置connection的方式
        try {
            conn.connect();      //和远程资源建立正在的链接,但尚无返回的数据流
        } catch (IOException e) {
            e.printStackTrace();
        }

        //对返回的状态码进行判断,用于检查是否请求成功,返回200时执行下面的代码
        try {
            if (conn.getResponseCode() == 200) {
               int fileSize = conn.getContentLength();  //根据响应获得文件大小
                SharedPreferencesUtil.saveData(mContext, "apk_fileSize", fileSize);
                conn.disconnect();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * 安装APK文件
     */
    private void installApk() {
//        File apkfile = new File(mSavePath, "ackpass");
        if (!saveFile.exists()) {
            return;
        }
//        // 通过Intent安装APK文件
//        Intent i = new Intent(Intent.ACTION_VIEW);
//        //设置（版本更新）完成后出现完成和打开页面
//        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        i.setDataAndType(Uri.parse("file://" + apkfile.toString()), "application/vnd.android.package-archive");
//        mContext.startActivity(i);

        Intent i = new Intent(Intent.ACTION_VIEW);
        if (Build.VERSION.SDK_INT >= 24) { //适配安卓7.0
            i.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            Uri apkFileUri = FileProvider.getUriForFile(mContext.getApplicationContext(),
                    mContext.getPackageName() + ".installapkdemo", saveFile);
            i.setDataAndType(apkFileUri, "application/vnd.android.package-archive");
        } else {
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            i.setDataAndType(Uri.parse("file://" + saveFile.toString()),
                    "application/vnd.android.package-archive");// File.toString()会返回路径信息
        }
        mContext.startActivity(i);
    }


    /*
    由于用户的输入事件(点击button, 触摸屏幕....)是由主线程负责处理的，如果主线程处于工作状态，
    此时用户产生的输入事件如果没能在5秒内得到处理，系统就会报“应用无响应”错误。
    所以在主线程里不能执行一件比较耗时的工作，否则会因主线程阻塞而无法处理用户的输入事件，
    导致“应用无响应”错误的出现。耗时的工作应该在子线程里执行。
     */
//    private DownloadTask task;



//    /**
//     * 退出下载
//     */
//    public void exit() {
//        if (task != null) task.exit();
//    }

//    private void download(String path, File saveDir) {//运行在主线程
//        task = new DownloadTask(path, saveDir);
//        new Thread(task).start();
//    }

    @Override
    public void sendTo_UPApk() {
//        exit();
//                mNotifyManager.notify(id, mBuilder.build());
        mNotifyManager.cancel(id);
        isnotify_close = true;
    }

    public void sendTo_UPApk_Second() {
//        exit();
//                mNotifyManager.notify(id, mBuilder.build());
        mNotifyManager.cancel(id);
        isnotify_close = true;
    }

    /*
     * UI控件画面的重绘(更新)是由主线程负责处理的，如果在子线程中更新UI控件的值，更新后的值不会重绘到屏幕上
     * 一定要在主线程里更新UI控件的值，这样才能在屏幕上显示出来，不能在子线程中更新UI控件的值
     */
    private final class DownloadTask1 implements Runnable {
        private String path;
        private File saveDir;
        private FileDownloadered loader;

        public DownloadTask1(String path, File saveDir) {
            this.path = path;
            this.saveDir = saveDir;
        }

        /**
         * 退出下载
         */
        public void exit() {
            if (loader != null) {
                loader.exit();
                loader.setnotFinish(false);
            }
        }

        public void run() {
            try {
                loader = new FileDownloadered(mContext, path, saveDir, 50);
//                progressbar.setMax(loader.getFileSize());//设置进度条的最大刻度
                loader.setnotFinish(true);
                maxsize = loader.getFileSize();
                loader.download(new DownloadProgressListener() {
                    public void onDownloadSize(int size, File file) {//说明，正在下载中
                        saveFile = file;
                        Message msg = new Message();
                        msg.what = 1;
                        msg.getData().putInt("size", size);
                        handler.sendMessage(msg);
                    }

                    @Override
                    public void onDownLoadError() { //说明，下载结束
                        SharedPreferencesUtil.saveData(mContext, "loadapk", false);
//                        mContext.unregisterReceiver(mMessageReceiver);
                        if (mNotifyManager != null)
                            mNotifyManager.cancel(id);
                        exit();
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
                handler.sendMessage(handler.obtainMessage(-1));
                if (mNotifyManager != null)
                    mNotifyManager.cancel(id);
                exit();
            }
        }
    }

//    public void registerMessageReceiver() {
//        mMessageReceiver = new MessageReceiver();
//        IntentFilter filter = new IntentFilter();
//        filter.setPriority(IntentFilter.SYSTEM_HIGH_PRIORITY);
//        filter.addAction(MESSAGE_RECEIVED_ACTION_APK_LOAD);
//        mContext.registerReceiver(mMessageReceiver, filter);
//    }


//    public class MessageReceiver extends BroadcastReceiver {
//
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            if (MESSAGE_RECEIVED_ACTION_APK_LOAD.equals(intent.getAction())) {
//                exit();
////                mNotifyManager.notify(id, mBuilder.build());
//                mNotifyManager.cancel(id);
//                isnotify_close = true;
//            }
//        }
//    }


    private Handler handler = new UIHander();

    private final class UIHander extends Handler {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                //下载时
                case PROCESSING:
                    int size = msg.getData().getInt("size");     //从消息中获取已经下载的数据长度
//                    progressbar.setProgress(size);         //设置进度条的进度
                    // 计算已经下载的百分比,此处需要转换为浮点数计算
                    float num = (float) size / (float) maxsize;
                    int result = (int) (num * 100);     //把获取的浮点数计算结果转换为整数
                    // textresult.setText(result + "%");   //把下载的百分比显示到界面控件上

                    mBuilder.setContentTitle("版本升级").setContentText(result + "%");
                    mBuilder.setProgress(100, result, false);
                    if (!isnotify_close)
                        mNotifyManager.notify(id, mBuilder.build());
                    if (size == maxsize) { //下载完成时提示
//                        Toast.makeText(getApplicationContext(), "文件下载成功", 1).show();
                        // 安装文件
                        index++;
                        if (index == 1) {
                            installApk();
                        }
                        mBuilder.setProgress(0, 0, false).setContentTitle("下载完成").setContentText("");
                        mNotifyManager.notify(id, mBuilder.build());
                        SharedPreferencesUtil.saveData(mContext, "loadapk", false);
                        if (mNotifyManager != null)
                            mNotifyManager.cancel(id);
//                        mContext.unregisterReceiver(mMessageReceiver);
//                        exit();
                    } else {
//                        SharedPreferencesUtil.saveData(mContext, "loadapk", true);
                    }
                    break;

                case FAILURE:    //下载失败时提示
//                    Toast.makeText(getApplicationContext(), "文件下载失败", 1).show();
                    break;
            }
        }
    }

}



