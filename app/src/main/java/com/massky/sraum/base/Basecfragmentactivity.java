package com.massky.sraum.base;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import android.util.Log;
import android.view.View;

import com.AddTogenInterface.AddTogglenInterfacer;
import com.chenhongxin.autolayout.AutoLayoutFragmentActivity;
import com.massky.sraum.User;
import com.massky.sraum.Util.HomeWatcher;
import com.massky.sraum.Util.IntentUtil;
import com.massky.sraum.Util.MyOkHttp;
import com.massky.sraum.Util.Mycallback;
import com.massky.sraum.Util.ScreenListener;
import com.massky.sraum.Util.SharedPreferencesUtil;
import com.massky.sraum.Util.TokenUtil;
import com.massky.sraum.Utils.ApiHelper;
import com.massky.sraum.Utils.AppManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.annotation.Nullable;
import butterknife.ButterKnife;
/*用于非侧滑侧面fragmentactivity的基类*/

/**
 * Created by masskywcy on 2016-07-08.
 */
public abstract class Basecfragmentactivity extends AutoLayoutFragmentActivity implements View.OnClickListener {
    private HomeWatcher mHomeWatcher;
    private boolean mainEditFlag, mainGesFlag;
    private ScreenListener screen;
    private String loginPhone = "";
    private SharedPreferences preferences;
//    private List<Allbox> allboxList = new ArrayList<Allbox>();
    public static boolean isForegrounds = false;
    public static boolean isDestroy = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(viewId());
        ButterKnife.inject(this);
        // 添加Activity到堆栈
        AppManager.getAppManager().addActivity(this);
        loginPhone = (String) SharedPreferencesUtil.getData(this, "loginPhone", "");
        preferences = getSharedPreferences("sraum" + loginPhone, Context.MODE_PRIVATE);
        screenListener();
        onView();
        Log.e("feis","onCreate:" + Basecfragmentactivity.this);
        isDestroy = false;
    }

    //用于监听是否锁屏状态
    private void screenListener() {
        screen = new ScreenListener(this);
        screen.begin(new ScreenListener.ScreenStateListener() {
            @Override
            public void onScreenOn() {
                SharedPreferencesUtil.saveData(Basecfragmentactivity.this, "screenFlag", true);
            }

            @Override
            public void onScreenOff() {
                SharedPreferencesUtil.saveData(Basecfragmentactivity.this, "screenFlag", false);
            }

            @Override
            public void onUserPresent() {
            }
        });

    }



    protected abstract int viewId();

    protected abstract void onView();

    @Override
    protected void onRestart() {
        super.onRestart();
        //进行判断手势密码验证
        boolean gesflag = (boolean) SharedPreferencesUtil.getData(Basecfragmentactivity.this, "gesflag", false);
        boolean editFlag = preferences.getBoolean("editFlag", false);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 结束Activity&从堆栈中移除
        screen.unregisterListener();
        mHomeWatcher.stopWatch();
        AppManager.getAppManager().finishActivity(this);
        isDestroy = true;
    }

    @Override
    protected void onPause() {
        isForegrounds = false;
        super.onPause();
    }

    @Override
    protected void onResume() {
        isForegrounds = true;
        super.onResume();
    }


}
