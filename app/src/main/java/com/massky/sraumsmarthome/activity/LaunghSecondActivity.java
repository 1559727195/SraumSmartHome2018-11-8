package com.massky.sraumsmarthome.activity;

import android.content.Intent;
import android.view.View;
import android.widget.Button;
import com.massky.sraumsmarthome.R;
import com.massky.sraumsmarthome.base.BaseActivity;
import com.yanzhenjie.statusview.StatusUtils;
import com.yanzhenjie.statusview.StatusView;
import butterknife.InjectView;

/**
 * Created by zhu on 2017/12/27.
 */

 public class LaunghSecondActivity extends BaseActivity {
   @InjectView(R.id.status_view)
   StatusView statusView;
   @InjectView(R.id.login)
    Button login;
   @InjectView(R.id.regist)
   Button regist;
    @Override
    protected int viewId() {
        return R.layout.laung_hsecond;
    }

    @Override
    protected void onView() {
       StatusUtils.setFullToStatusBar(this);  // StatusBar.
       setAnyBarAlpha(0);//设置状态栏的颜色为透明
    }

    @Override
    protected void onEvent() {
        login.setOnClickListener(this);
        regist.setOnClickListener(this);
    }

    @Override
    protected void onData() {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.login:
                startActivity(new Intent(LaunghSecondActivity.this,LoginCloudActivity.class));
                break;
            case R.id.regist:
                startActivity(new Intent(LaunghSecondActivity.this,RegistActivity.class));
                break;
        }
    }

   /*
*动态设置状态栏的颜色
*/
   private void setAnyBarAlpha(int alpha) {
//        mToolbar.getBackground().mutate().setAlpha(alpha);
      statusView.getBackground().mutate().setAlpha(alpha);
   }

}
