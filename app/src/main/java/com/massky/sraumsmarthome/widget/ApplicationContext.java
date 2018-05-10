package com.massky.sraumsmarthome.widget;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.multidex.MultiDex;
import android.support.multidex.MultiDexApplication;
import android.util.Log;
import com.massky.sraumsmarthome.bean.DaoMaster;
import com.massky.sraumsmarthome.bean.DaoSession;
import com.zhy.http.okhttp.OkHttpUtils;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;
import cn.jpush.android.api.JPushInterface;
import okhttp3.OkHttpClient;

/**
 * Created by zhu on 2017/11/16.
 */

public class ApplicationContext extends MultiDexApplication implements Application.ActivityLifecycleCallbacks {
    //
    private static final String TAG = ApplicationContext.class.getSimpleName();
    private Context context;
    public String calledAcccout;

    private CopyOnWriteArrayList<Activity> activities = new CopyOnWriteArrayList<>();
    //
    private static ApplicationContext _instance;
//	public static BluetoothOpration _BluetoothOpration;
    /**
     * 当前Acitity个数
     */
    private int activityAount = 0;

    // 开放平台申请的APP key & secret key
    public static String APP_KEY = "ccd38858cc5a459bbeedcf93a25ae6be";
    public static String API_URL = "https://open.ys7.com";
    public static String WEB_URL = "https://auth.ys7.com";
    private boolean isForeground;
    private boolean isDoflag;
    private DaoSession daoSession;

    /**
     *
     */
    @Override
    public void onCreate() {
        // TODO Auto-generated method stub
        super.onCreate();
        _instance = this;
        JPushInterface.setDebugMode(true);
        JPushInterface.init(this);
        init_http();
        setGreenDaoMaster();
    }

    private void init_http() {
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                //.addInterceptor(new LoggerInterceptor("TAG"))
                .connectTimeout(5000, TimeUnit.MILLISECONDS)
                .readTimeout(5000, TimeUnit.MILLISECONDS)
                //其他配置
                .build();

        OkHttpUtils.initClient(okHttpClient);
    }

    /**
     * 设置GreenDao数据库数据
     */
    private void setGreenDaoMaster() {
        //实例化一个OpenHelper实例，类似于使用的SQLiteOpenHelper类
        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(this, "movie-db");

        //获取一个SQLiteDatabase
        SQLiteDatabase database = helper.getWritableDatabase();

        //使用数据库对象构造一个DaoMaster
        DaoMaster daoMaster = new DaoMaster(database);

        //开启DoaSession
        daoSession = daoMaster.newSession();
    }

    public DaoSession getDaoSession() {
        return daoSession;
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        Log.i(TAG, "onTerminate");
    }


    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {

    }

    @Override
    public void onActivityStarted(Activity activity) {

    }

    @Override
    public void onActivityResumed(Activity activity) {

    }

    @Override
    public void onActivityPaused(Activity activity) {
//		ToastUtils.getInstances().cancel();// activity死的时候，onActivityPaused(Activity activity)
        //ToastUtils.getInstances().cancel();
    }

    @Override
    public void onActivityStopped(Activity activity) {

    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

    }

    @Override
    public void onActivityDestroyed(Activity activity) {

    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        Log.i("info", "demo onLowMemory..");
//		_BluetoothOpration.disconnect();
//		_BluetoothOpration.onDestroy();
    }

    /**
     * @return
     */
    public static ApplicationContext getInstance() {
        return _instance;
    }

    /**
     * @param act
     */
    public void addActivity(Activity act) {
        activities.add(act);
    }

    /**
     *
     */
    public void removeActivity() {
        for (int i = activities.size() - 1; i >= 0; i--) {
            Activity activity = activities.get(i);
            activities.remove(activity);
            activity.finish();
        }
    }


    public void removeActivity_but_activity(Activity activity_new) {
        for (int i = activities.size() - 1; i >= 0; i--) {
            Activity activity = activities.get(i);
            if (activity == activity_new) {
                continue;
            }
            activities.remove(activity);
            activity.finish();
        }
    }


    /**
     * 结束指定的Activity
     */
    public void finishActivity(Activity activity) {
        if (activity != null) {
            activities.remove(activity);
            if (!activity.isFinishing()) {
                activity.finish();
            }
        }
    }

    /**
     * 结束指定类名的Activity
     */
    public void finishActivity(Class<?> cls) {
        for (Activity activity : activities) {
            if (activity.getClass().equals(cls)) {
                finishActivity(activity);
            }
        }
    }


    /**
     * 结束除了指定类名的Activity
     */
    public void finishButActivity(Class<?> cls) {
        for (Activity activity : activities) {
            if (!activity.getClass().equals(cls)) {
                finishActivity(activity);
            }
        }
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }
}
