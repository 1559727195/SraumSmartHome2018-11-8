package com.massky.sraumsmarthome.widget;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhu on 2017/11/16.
 */

public class ApplicationContext extends Application implements Application.ActivityLifecycleCallbacks{
    //
    private static final String TAG = ApplicationContext.class.getSimpleName();
    private Context context;
    public String calledAcccout;

    private List<Activity> activities = new ArrayList<>();
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

    /**
     *
     */
    @Override
    public void onCreate() {
        // TODO Auto-generated method stub
        super.onCreate();
        _instance = this;

//		EZOpenSDK.initLib(this, APP_KEY, "");//萤石平台的sdk在android6.0上报错，监控先不用它

    }


    @Override
    public void onTerminate() {
        super.onTerminate();
        Log.i(TAG, "onTerminate");
    }
//

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
     *
     * @return
     */
    public static ApplicationContext getInstance(){

        return _instance;
    }



    /**
     *
     * @param act
     */
    public void addActivity(Activity act){
        activities.add(act);
    }

    /**
     *
     */
    public void removeActivity(){
        for(int i = activities.size() -1 ; i >= 0; i--){
            Activity activity = activities.get(i);
            activities.remove(activity);
            activity.finish();
        }
    }


    public void removeActivity_but_activity(Activity activity_new){
        for(int i = activities.size() -1 ; i >= 0; i--){
            Activity activity = activities.get(i);
            if (activity == activity_new){
                continue;
            }
            activities.remove(activity);
            activity.finish();
        }
    }

}
