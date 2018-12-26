package com.massky.sraum.activity;

import android.Manifest;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.AddTogenInterface.AddTogglenInterfacer;
import com.massky.sraum.R;
import com.massky.sraum.User;
import com.massky.sraum.Util.DialogUtil;
import com.massky.sraum.Util.IntentUtil;
import com.massky.sraum.Util.MyOkHttp;
import com.massky.sraum.Util.Mycallback;
import com.massky.sraum.Util.SharedPreferencesUtil;
import com.massky.sraum.Util.TokenUtil;
import com.massky.sraum.Utils.ApiHelper;
import com.massky.sraum.Utils.AppManager;
import com.massky.sraum.base.BaseActivity;
import com.massky.sraum.permissions.RxPermissions;
import com.massky.sraum.widget.ApplicationContext;
import com.yanzhenjie.statusview.StatusUtils;
import com.yanzhenjie.statusview.StatusView;

import java.util.HashMap;
import java.util.Map;

import butterknife.InjectView;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

/**
 * Created by zhu on 2018/1/17.
 */

public class SettingActivity extends BaseActivity {
    @InjectView(R.id.status_view)
    StatusView statusView;
    @InjectView(R.id.back)
    ImageView back;
    @InjectView(R.id.toolbar_txt)
    TextView toolbar_txt;
    private View account_view;
    private Button cancelbtn_id, camera_id, photoalbum;
    private DialogUtil dialogUtil;
    private LinearLayout linear_popcamera;
    //    @InjectView(R.id.account_nicheng)
//    RelativeLayout account_nicheng;
    //    @InjectView(R.id.xingbie_pic)
//    ImageView xingbie_pic;//性别选择图片
    @InjectView(R.id.xingbie_rel)
    RelativeLayout xingbie_rel;
    @InjectView(R.id.account_year)
    RelativeLayout account_year;

    @InjectView(R.id.account_id_rel)
    RelativeLayout account_id_rel;

    @InjectView(R.id.change_phone)
    RelativeLayout change_phone;

    @InjectView(R.id.home_room_manger_rel)
    RelativeLayout home_room_manger_rel;

    @InjectView(R.id.infor_setting_rel)
    RelativeLayout infor_setting_rel;
    @InjectView(R.id.btn_quit_gateway)
    Button btn_quit_gateway;

    @Override
    protected int viewId() {
        return R.layout.setting_act;
    }

    @Override
    protected void onView() {
//        if (!StatusUtils.setStatusBarDarkFont(this, true)) {// Dark font for StatusBar.
//            statusView.setBackgroundColor(Color.BLACK);
//        }
        StatusUtils.setFullToStatusBar(this);
        init_permissions();
    }

    @Override
    protected void onEvent() {
        back.setOnClickListener(this);
        home_room_manger_rel.setOnClickListener(this);
        infor_setting_rel.setOnClickListener(this);
        btn_quit_gateway.setOnClickListener(this);
    }

    @Override
    protected void onData() {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back:
                SettingActivity.this.finish();
                break;
            case R.id.home_room_manger_rel:
                startActivity(new Intent(SettingActivity.this, HomeSettingActivity.class));
                break;//家庭和房间管理
            case R.id.infor_setting_rel:
                startActivity(new Intent(SettingActivity.this, InformationSettingActivity.class));
                break;//消息设置
            case R.id.btn_quit_gateway:
//                ApplicationContext.getInstance().removeActivity();
//                startActivity(new Intent(SettingActivity.this,LoginCloudActivity.class));
//                SharedPreferencesUtil.saveData(SettingActivity.this, "editFlag", false);
//                SharedPreferencesUtil.saveData(SettingActivity.this, "loginflag", false);
//                IntentUtil.startActivityAndFinishFirst(SettingActivity.this, LoginCloudActivity.class);
//                AppManager.getAppManager().finishAllActivity();
                //退出登录
                logout();

                break;//
        }
    }


    private void logout() {
        //在这里先调
        //设置网关模式-sraum-setBox
        Map map = new HashMap();
//        String phoned = getDeviceId(getActivity());
        map.put("token", TokenUtil.getToken(SettingActivity.this));
        if (dialogUtil != null)
            dialogUtil.loadDialog();
        MyOkHttp.postMapObject(ApiHelper.sraum_logout, map, new Mycallback(new AddTogglenInterfacer() {
                    @Override
                    public void addTogglenInterfacer() {
                        logout();
                    }
                }, SettingActivity.this, dialogUtil) {
                    @Override
                    public void onSuccess(User user) {
                        SharedPreferencesUtil.saveData(SettingActivity.this, "editFlag", false);
                        SharedPreferencesUtil.saveData(SettingActivity.this, "loginflag", false);
                        IntentUtil.startActivityAndFinishFirst(SettingActivity.this, LoginCloudActivity.class);
                        AppManager.getAppManager().finishAllActivity();
                    }

                    @Override
                    public void wrongToken() {
                        super.wrongToken();
                    }

                    @Override
                    public void wrongBoxnumber() {

                    }
                }
        );
    }


    private void init_permissions() {

        // 清空图片缓存，包括裁剪、压缩后的图片 注意:必须要在上传完成后调用 必须要获取权限
        RxPermissions permissions = new RxPermissions(this);
        permissions.request(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE
                , Manifest.permission.CAMERA
                , Manifest.permission.WRITE_SETTINGS).subscribe(new Observer<Boolean>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(Boolean aBoolean) {

            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        });
    }
}
