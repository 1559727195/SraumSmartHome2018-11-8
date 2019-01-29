package com.massky.sraum.activity;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
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
                showCenterDeleteDialog("是否退出登录?");
                break;//
        }
    }


    //自定义dialog,centerDialog删除对话框
    public void showCenterDeleteDialog(final String name) {
//        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
//        // 布局填充器
//        LayoutInflater inflater = LayoutInflater.from(getActivity());
//        View view = inflater.inflate(R.layout.user_name_dialog, null);
//        // 设置自定义的对话框界面
//        builder.setView(view);
//
//        cus_dialog = builder.create();
//        cus_dialog.show();

        View view = LayoutInflater.from(SettingActivity.this).inflate(R.layout.promat_dialog, null);
        TextView confirm; //确定按钮
        TextView cancel; //确定按钮
        TextView tv_title;
        TextView name_gloud;
//        final TextView content; //内容
        cancel = (TextView) view.findViewById(R.id.call_cancel);
        confirm = (TextView) view.findViewById(R.id.call_confirm);
        tv_title = (TextView) view.findViewById(R.id.tv_title);//name_gloud
        name_gloud = (TextView) view.findViewById(R.id.name_gloud);
        name_gloud.setText(name);
        tv_title.setVisibility(View.GONE);
//        tv_title.setText("是否拨打119");
//        content.setText(message);
        //显示数据
        final Dialog dialog = new Dialog(SettingActivity.this, R.style.BottomDialog);
        dialog.setContentView(view);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        DisplayMetrics dm = getResources().getDisplayMetrics();
        int displayWidth = dm.widthPixels;
        int displayHeight = dm.heightPixels;
        android.view.WindowManager.LayoutParams p = dialog.getWindow().getAttributes(); //获取对话框当前的参数值
        p.width = (int) (displayWidth * 0.8); //宽度设置为屏幕的0.5
//        p.height = (int) (displayHeight * 0.5); //宽度设置为屏幕的0.5
//        dialog.setCanceledOnTouchOutside(false);// 设置点击屏幕Dialog不消失
        dialog.getWindow().setAttributes(p);  //设置生效
        dialog.show();

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logout();
            }
        });

        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
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
