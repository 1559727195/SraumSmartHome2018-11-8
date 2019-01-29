package webapp.download;

import android.app.DownloadManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Toast;

import java.io.File;

import webapp.config.SystemParams;

import static com.massky.sraum.Util.AppDownloadManager.install;

/**
 * Apk下载
 * Created by Song on 2016/11/2.
 */
public class DownloadApk {

    private static ApkInstallReceiver apkInstallReceiver;

    /**
     * 下载APK文件
     *
     * @param context
     * @param url
     * @param title
     * @param appName
     */
    public static void downloadApk(Context context, String url, String title, final String appName) {

        //获取存储的下载ID
        long downloadId = SystemParams.getInstance().getLong(DownloadManager.EXTRA_DOWNLOAD_ID, -1L);
        if (downloadId != -1) {
            //存在downloadId
            DownLoadUtils downLoadUtils = DownLoadUtils.getInstance(context);
            //获取当前状态
            int status = downLoadUtils.getDownloadStatus(downloadId);
            if (DownloadManager.STATUS_SUCCESSFUL == status) {
                //状态为下载成功
                //获取下载路径URI
                Uri downloadUri = downLoadUtils.getDownloadUri(downloadId);
                if (null != downloadUri) {
                    //存在下载的APK，如果两个APK相同，启动更新界面。否之则删除，重新下载。
                    if (compare(getApkInfo(context,getRealFilePathFromUri(context,downloadUri)), context)) {//downloadUri.getPath()
                        startInstall(context, downloadUri,appName);
                        return;
                    } else {
                        //删除下载任务以及文件
                        downLoadUtils.getDownloadManager().remove(downloadId);
                    }
                }
                start(context, url, title, appName);
            } else if (DownloadManager.STATUS_FAILED == status) {
                //下载失败,重新下载
                start(context, url, title, appName);
            } else if (status == -1) {
                start(context, url, title, appName);
            } else {
                Log.d(context.getPackageName(), "apk is already downloading");
            }
        } else {
            //不存在downloadId，没有下载过APK
            start(context, url, title, appName);
        }
    }

    /**
     * 开始下载
     *
     * @param context
     * @param url
     * @param title
     * @param appName
     */
    private static void start(Context context, String url, String title, String appName) {

        if (hasSDKCard()) {
            long id = DownLoadUtils.getInstance(context).download(url,
                    title, "下载完成后点击打开", appName);
            SystemParams.getInstance().setLong(DownloadManager.EXTRA_DOWNLOAD_ID, id);
        } else {
            Toast.makeText(context, "手机未安装SD卡，下载失败", Toast.LENGTH_LONG).show();
        }
    }

    public static void registerBroadcast(Context context) {
        apkInstallReceiver = new ApkInstallReceiver();
        context.registerReceiver(apkInstallReceiver, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
    }

    public static void unregisterBroadcast(Context context) {
        if (null != apkInstallReceiver) {
            context.unregisterReceiver(apkInstallReceiver);
        }
    }

    /**
     * 跳转到安装界面
     *  @param context
     * @param uri
     * @param appName
     */
    private static void startInstall(Context context, Uri uri, String appName) {

//        Intent install = new Intent(Intent.ACTION_VIEW);
//        install.setDataAndType(uri, "application/vnd.android.package-archive");
//        install.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        context.startActivity(install);
        install(context,new Intent(),appName);
    }




    /**
     * 获取APK程序信息
     *
     * @param context
     * @param path
     * @return
     */
  public static PackageInfo getApkInfo(Context context, String path) {
        PackageManager pm = context.getPackageManager();
        PackageInfo pi = pm.getPackageArchiveInfo(path, PackageManager.GET_ACTIVITIES);
        if (null != pi) {
            return pi;
        }
        return null;
    }


    /**
     * 根据Uri返回文件绝对路径
     * 兼容了file:///开头的 和 content://开头的情况
     *
     * @param context
     * @param uri
     * @return the file path or null
     */
    public static String getRealFilePathFromUri(final Context context, final Uri uri) {
        if (null == uri) return null;
        final String scheme = uri.getScheme();
        String data = null;
        if (scheme == null)
            data = uri.getPath();
        else if (ContentResolver.SCHEME_FILE.equals(scheme)) {
            data = uri.getPath();
        } else if (ContentResolver.SCHEME_CONTENT.equals(scheme)) {
            Cursor cursor = context.getContentResolver().query(uri, new String[]{MediaStore.Images.ImageColumns.DATA}, null, null, null);
            if (null != cursor) {
                if (cursor.moveToFirst()) {
                    int index = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
                    if (index > -1) {
                        data = cursor.getString(index);
                    }
                }
                cursor.close();
            }
        }
        return data;
    }


    /**
     * 获取apk包的信息：版本号，名称，图标等
     *
     * @param absPath  apk包的绝对路径
     * @param context 
     */
    public void apkInfo(String absPath, Context context) {

        PackageManager pm = context.getPackageManager();
        PackageInfo pkgInfo = pm.getPackageArchiveInfo(absPath, PackageManager.GET_ACTIVITIES);
        if (pkgInfo != null) {
            ApplicationInfo appInfo = pkgInfo.applicationInfo;
            /* 必须加这两句，不然下面icon获取是default icon而不是应用包的icon */
            appInfo.sourceDir = absPath;
            appInfo.publicSourceDir = absPath;
            String appName = pm.getApplicationLabel(appInfo).toString();// 得到应用名 
            String packageName = appInfo.packageName; // 得到包名 
            String version = pkgInfo.versionName; // 得到版本信息 
            /* icon1和icon2其实是一样的 */
            Drawable icon1 = pm.getApplicationIcon(appInfo);// 得到图标信息 
            Drawable icon2 = appInfo.loadIcon(pm);
            String pkgInfoStr = String.format("PackageName:%s, Vesion: %s, AppName: %s", packageName, version, appName);
            Log.i("robin debug", String.format("PkgInfo: %s", pkgInfoStr));
        }
    }


    /**
     * 比较两个APK的信息
     *
     * @param apkInfo
     * @param context
     * @return
     */
    private static boolean compare(PackageInfo apkInfo, Context context) {

        if (null == apkInfo) {
            return false;
        }
        String localPackageName = context.getPackageName();
        if (localPackageName.equals(apkInfo.packageName)) {
            try {
                PackageInfo packageInfo = context.getPackageManager().getPackageInfo(localPackageName, 0);
                //比较当前APK和下载的APK版本号
                if (apkInfo.versionCode > packageInfo.versionCode) {
                    //如果下载的APK版本号大于当前安装的APK版本号，返回true
                    return true;
                }
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    /**
     * 是否存在SD卡
     */
    private static boolean hasSDKCard() {
        return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
    }

    /**
     * 删除已下载的文件
     */
    public static void removeFile(Context context) {
        String filePath = SystemParams.getInstance().getString("downloadApk", null);
        if (null != filePath) {
            File downloadFile = new File(filePath);
            if (null != downloadFile && downloadFile.exists()) {
                //删除之前先判断用户是否已经安装了，安装了才删除。
                if (!compare(getApkInfo(context, filePath), context)) {
                    downloadFile.delete();
                    Log.e("----", "已删除");
                }
            }
        }
    }
}
