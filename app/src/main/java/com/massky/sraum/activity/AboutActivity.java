package com.massky.sraum.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Environment;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.AddTogenInterface.AddTogglenInterfacer;
import com.massky.sraum.R;
import com.massky.sraum.User;
import com.massky.sraum.Util.DialogUtil;
import com.massky.sraum.Util.MyOkHttp;
import com.massky.sraum.Util.Mycallback;
import com.massky.sraum.Util.SharedPreferencesUtil;
import com.massky.sraum.Util.ToastUtil;
import com.massky.sraum.Util.TokenUtil;
import com.massky.sraum.Utils.ApiHelper;
import com.massky.sraum.Utils.App;
import com.massky.sraum.Utils.VersionUtil;
import com.massky.sraum.adapter.SystemMessageAdapter;
import com.massky.sraum.base.BaseActivity;
import com.massky.sraum.view.XListView;
import com.yanzhenjie.statusview.StatusUtils;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.InjectView;

import static com.massky.sraum.Util.DipUtil.dip2px;
import static com.massky.sraum.activity.MainGateWayActivity.MESSAGE_RECEIVED_FROM_ABOUT_FRAGMENT;

/**
 * Created by zhu on 2018/2/12.
 */

public class AboutActivity extends BaseActivity {
    @InjectView(R.id.back)
    ImageView back;
    @InjectView(R.id.btn_common_problems)
    Button btn_common_problems;
    @InjectView(R.id.btn_privacy_policy)
    Button btn_privacy_policy;

    private Button checkbutton_id, qxbutton_id;
    private TextView dtext_id, belowtext_id;
    private DialogUtil viewDialog;
    private int versionCode;
    private String Version;
    private WeakReference<Context> weakReference;
    @InjectView(R.id.check_btn_version)
    Button check_btn_version;


    @Override
    protected int viewId() {
        return R.layout.about_act;
    }

    @Override
    protected void onView() {
        StatusUtils.setFullToStatusBar(this);
        versionCode = Integer.parseInt(VersionUtil.getVersionCode(AboutActivity.this));
        getDialog();
    }

    @Override
    protected void onEvent() {
        back.setOnClickListener(this);
        btn_common_problems.setOnClickListener(this);
        btn_privacy_policy.setOnClickListener(this);
        check_btn_version.setOnClickListener(this);
    }

    @Override
    protected void onData() {

    }

    //用于设置dialog展示
    private void getDialog() {
        View view = getLayoutInflater().inflate(R.layout.check, null);
        checkbutton_id = (Button) view.findViewById(R.id.checkbutton_id);
        qxbutton_id = (Button) view.findViewById(R.id.qxbutton_id);
        dtext_id = (TextView) view.findViewById(R.id.dtext_id);
        belowtext_id = (TextView) view.findViewById(R.id.belowtext_id);
        dtext_id.setText("发现新版本");
        checkbutton_id.setText("立即更新");
        qxbutton_id.setText("以后再说");
        viewDialog = new DialogUtil(AboutActivity.this, view);
        checkbutton_id.setOnClickListener(this);
        qxbutton_id.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back:
                AboutActivity.this.finish();
                break;
            case R.id.btn_common_problems:
                startActivity(new Intent(AboutActivity.this, ProductActivity.class));
                break;
            case R.id.btn_privacy_policy:
                startActivity(new Intent(AboutActivity.this, YinSiActivity.class));
                break;
            case R.id.checkbutton_id:
                viewDialog.removeviewDialog();
//                String UpApkUrl = ApiHelper.UpdateApkUrl + "sraum" + Version + ".apk";
//                UpdateManager manager = new UpdateManager(getActivity(), UpApkUrl);
//                manager.showDownloadDialog();
                Intent broadcast = new Intent(MESSAGE_RECEIVED_FROM_ABOUT_FRAGMENT);
                sendBroadcast(broadcast);
                break;
            case R.id.qxbutton_id:
                viewDialog.removeviewDialog();
                break;
            case R.id.check_btn_version:
                if (viewDialog != null) {
                    viewDialog.loadDialog();
                    about_togglen();
                }
                break;
        }
    }

    private void about_togglen() {
        Map<String, Object> map = new HashMap<>();
        map.put("token", TokenUtil.getToken(AboutActivity.this));
        MyOkHttp.postMapObject(ApiHelper.sraum_getVersion, map, new Mycallback(new AddTogglenInterfacer() {
            @Override
            public void addTogglenInterfacer() {
                about_togglen();
            }
        }, AboutActivity.this, viewDialog) {
            @Override
            public void onSuccess(User user) {
                super.onSuccess(user);
                Version = user.version;
                int sracode = Integer.parseInt(user.versionCode);
                if (versionCode >= sracode) {
                    ToastUtil.showDelToast(AboutActivity.this, "您的应用为最新版本");
                } else {
//                    belowtext_id.setText("版本更新至" + Version);
//                    viewDialog.loadViewdialog();
                    //在这里判断有没有正在更新的apk,文件大小小于总长度即可
                    weakReference = new WeakReference<Context>(App.getInstance());
                    File apkFile = new File(weakReference.get().getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), "app_name.apk");
                    if (apkFile != null && apkFile.exists()) {
                        long apksize = 0;
                        try {
                            apksize = getFileSize(apkFile);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        int totalapksize = (int) SharedPreferencesUtil.getData(AboutActivity.this, "apk_fileSize", 0);
                        if (totalapksize == 0) {//则说明，还没有下载过
                            belowtext_id.setText("版本更新至" + Version);
                            viewDialog.loadViewdialog();
                            return;
                        }

                        if (apksize - totalapksize == 0) { //说明正在下载或者下载完毕，安装失败时，//->或者是下载完毕后没有去安装；
//                                    down_load_thread();
                            ToastUtil.showToast(AboutActivity.this, "检测到有新版本，正在下载中");
                        }
                    } else {//不存在，apk文件
                        belowtext_id.setText("版本更新至" + Version);
                        viewDialog.loadViewdialog();
                    }
                }
            }

            @Override
            public void wrongToken() {
                super.wrongToken();
            }
        });
    }


    /**
     * 获取文件长度
     */
    public long getFileSize(File file) {
        if (file.exists() && file.isFile()) {
            String fileName = file.getName();
            System.out.println("文件" + fileName + "的大小是：" + file.length());
            return file.length();
        }
        return 0;
    }
}
