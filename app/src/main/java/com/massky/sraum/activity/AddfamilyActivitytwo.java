package com.massky.sraum.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import com.AddTogenInterface.AddTogglenInterfacer;
import com.massky.sraum.R;
import com.massky.sraum.User;
import com.massky.sraum.Util.DialogUtil;
import com.massky.sraum.Util.IntentUtil;
import com.massky.sraum.Util.MyOkHttp;
import com.massky.sraum.Util.Mycallback;
import com.massky.sraum.Util.SharedPreferencesUtil;
import com.massky.sraum.Util.ToastUtil;
import com.massky.sraum.Util.TokenUtil;
import com.massky.sraum.Utils.ApiHelper;
import com.massky.sraum.Utils.AppManager;
import com.massky.sraum.base.BaseActivity;
import com.yanzhenjie.statusview.StatusUtils;
import java.util.HashMap;
import java.util.Map;
import butterknife.InjectView;
import okhttp3.Call;

/**
 * Created by masskywcy on 2017-02-21.
 */

public class AddfamilyActivitytwo extends BaseActivity {
    @InjectView(R.id.familytwoedit)
    TextView familytwoedit;
    @InjectView(R.id.nametwoedit)
    TextView nametwoedit;
    @InjectView(R.id.famtwobtn_id)
    Button famtwobtn_id;
    @InjectView(R.id.back)
    ImageView back;
    private String mobilePhone, familyName;
    private DialogUtil dialogUtil;
    private String areaNumber;

    @Override
    protected int viewId() {
        return R.layout.addfamilytwo;
    }

    @Override
    protected void onView() {
        StatusUtils.setFullToStatusBar(this);
        dialogUtil = new DialogUtil(AddfamilyActivitytwo.this);
        Bundle bundle = IntentUtil.getIntentBundle(AddfamilyActivitytwo.this);
        mobilePhone = bundle.getString("mobilePhone");
        familyName = bundle.getString("familyName");
        areaNumber = bundle.getString("areaNumber");
        familytwoedit.setText(mobilePhone);
        nametwoedit.setText(familyName);
        back.setOnClickListener(this);
        famtwobtn_id.setOnClickListener(this);
    }

    @Override
    protected void onEvent() {

    }

    @Override
    protected void onData() {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back:
                AddfamilyActivitytwo.this.finish();
                break;
            case R.id.famtwobtn_id:
                add_family_act();
                break;
        }
    }

    private void add_family_act() {
        Map<String, Object> map = new HashMap<>();
        map.put("token", TokenUtil.getToken(AddfamilyActivitytwo.this));
//        String areaNumber = (String) SharedPreferencesUtil.getData(AddfamilyActivitytwo.this, "areaNumber", "");
        map.put("mobilePhone", mobilePhone);
        map.put("familyName", familyName);
        map.put("areaNumber", areaNumber);
        dialogUtil.loadDialog();
        MyOkHttp.postMapObject(ApiHelper.sraum_addFamily, map, new Mycallback(new AddTogglenInterfacer() {
            @Override
            public void addTogglenInterfacer() {
                add_family_act();
            }
        }, AddfamilyActivitytwo.this, dialogUtil) {
            @Override
            public void onError(Call call, Exception e, int id) {
                super.onError(call, e, id);
                dialogUtil.removeDialog();
                ToastUtil.showDelToast(AddfamilyActivitytwo.this, "网络连接超时");
            }

            @Override
            public void onSuccess(User user) {
                super.onSuccess(user);
                if (user.result.equals("100")) {
//                    IntentUtil.startActivity(AddfamilyActivitytwo.this, MyfamilyActivity.class);
                    AppManager.getAppManager().finishActivity_current(AddfamilyActivity.class);
                    AddfamilyActivitytwo.this.finish();
                } else {
                    ToastUtil.showDelToast(AddfamilyActivitytwo.this, "名字重复");
                }
            }

            @Override
            public void wrongBoxnumber() {
                ToastUtil.showDelToast(AddfamilyActivitytwo.this, "areaNumber\n" +
                        "不存在");
            }

            @Override
            public void wrongToken() {
                super.wrongToken();
            }

            @Override
            public void threeCode() {
                super.threeCode();
                ToastUtil.showDelToast(AddfamilyActivitytwo.this, "mobilePhone 错误");
            }

            @Override
            public void fourCode() {
                super.fourCode();
                ToastUtil.showDelToast(AddfamilyActivitytwo.this, "familyName 错误");
            }
        });
    }
}
