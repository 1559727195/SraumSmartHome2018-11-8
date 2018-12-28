package com.massky.sraum.activity;

import android.app.Dialog;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import androidx.percentlayout.widget.PercentRelativeLayout;

import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.AddTogenInterface.AddTogglenInterfacer;
import com.bigkoo.pickerview_new.OptionsPickerView;
import com.massky.sraum.R;
import com.massky.sraum.User;
import com.massky.sraum.Util.DialogUtil;
import com.massky.sraum.Util.MyOkHttp;
import com.massky.sraum.Util.Mycallback;
import com.massky.sraum.Util.ToastUtil;
import com.massky.sraum.Util.TokenUtil;
import com.massky.sraum.Utils.ApiHelper;
import com.massky.sraum.base.BaseActivity;
import com.massky.sraum.view.ClearEditText;
import com.yanzhenjie.statusview.StatusUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import butterknife.InjectView;
import db.AreaBean;
import db.CityBean;
import db.DBManager;
import db.ProvinceBean;
import okhttp3.Call;

/**
 * Created by zhu on 2018/2/12.
 */

public class AddAreaActivity extends BaseActivity {
    @InjectView(R.id.back)
    ImageView back;
    @InjectView(R.id.add_home_rel)
    PercentRelativeLayout add_home_rel;
    @InjectView(R.id.area_name_rel)
    PercentRelativeLayout area_name_rel;
    @InjectView(R.id.txt_area_name)
    TextView txt_area_name;
    private OptionsPickerView pvOptions;//地址选择器
    private ArrayList<ProvinceBean> options1Items = new ArrayList<>();//省
    private ArrayList<ArrayList<CityBean>> options2Items = new ArrayList<>();//市
    private ArrayList<ArrayList<ArrayList<AreaBean>>> options3Items = new ArrayList<>();//区
    private ArrayList<String> Provincestr = new ArrayList<>();//省
    private ArrayList<ArrayList<String>> Citystr = new ArrayList<>();//市
    private ArrayList<ArrayList<ArrayList<String>>> Areastr = new ArrayList<>();//区
    @InjectView(R.id.location_txt)
    TextView location_txt;
    //    @InjectView(R.id.hostory_back_txt)
//    TextView hostory_back_txt;
    @InjectView(R.id.save_area)
    TextView save_area;

    String province;
    String city;
    String district;
    private String areaName = "";
    private DialogUtil dialogUtil;

    @Override
    protected int viewId() {
        return R.layout.add_area_act;
    }


    @Override
    protected void onView() {
        StatusUtils.setFullToStatusBar(this);
        dialogUtil = new DialogUtil(this);
    }

    @Override
    protected void onEvent() {
        back.setOnClickListener(this);
        add_home_rel.setOnClickListener(this);
//        hostory_back_txt.setOnClickListener(this);
        area_name_rel.setOnClickListener(this);
        save_area.setOnClickListener(this);
    }

    @Override
    protected void onData() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                initData();
            }
        }).start();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back:
                AddAreaActivity.this.finish();
                break;
            case R.id.add_home_rel:
                if (pvOptions != null)
                    pvOptions.show();
                break;//选择地址
            case R.id.hostory_back_txt:
                AddAreaActivity.this.finish();
                break;
            case R.id.area_name_rel:
                showRenameDialog();
                break;
            case R.id.save_area://保存区域
                if (areaName.equals("") || areaName == null) {
                    ToastUtil.showToast(AddAreaActivity.this, "区域名称为空");
                    return;
                }

                if (district == null) {
                    ToastUtil.showToast(AddAreaActivity.this, "区域位置为空");
                    return;
                }
                sraum_addArea();
                break;
        }
    }


    /**
     * 添加区域
     */
    private void sraum_addArea() {
        if (dialogUtil != null) {
            dialogUtil.loadDialog();
        }
        Map<String, String> mapdevice = new HashMap<>();
        mapdevice.put("token", TokenUtil.getToken(AddAreaActivity.this));
//        mapdevice.put("boxNumber", TokenUtil.getBoxnumber(SelectSensorActivity.this));
        mapdevice.put("province", province);
        mapdevice.put("city", city);
        mapdevice.put("district", district);
        mapdevice.put("name", areaName);
        MyOkHttp.postMapString(ApiHelper.sraum_addArea
                , mapdevice, new Mycallback(new AddTogglenInterfacer() {
                    @Override
                    public void addTogglenInterfacer() {//刷新togglen数据
                        sraum_addArea();
                    }
                }, AddAreaActivity.this, dialogUtil) {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        super.onError(call, e, id);
                    }

                    @Override
                    public void pullDataError() {
                        super.pullDataError();
                    }

                    @Override
                    public void emptyResult() {
                        super.emptyResult();
                    }

                    @Override
                    public void wrongToken() {
                        super.wrongToken();
                        //重新去获取togglen,这里是因为没有拉到数据所以需要重新获取togglen
                    }

                    @Override
                    public void wrongBoxnumber() {
                        super.wrongBoxnumber();
                    }

                    @Override
                    public void onSuccess(final User user) {
                        ToastUtil.showToast(AddAreaActivity.this, "添加区域成功");
                        AddAreaActivity.this.finish();
                    }
                });
    }


    //自定义dialog,自定义重命名dialog

    public void showRenameDialog() {
//        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
//        // 布局填充器
//        LayoutInflater inflater = LayoutInflater.from(getActivity());
//        View view = inflater.inflate(R.layout.user_name_dialog, null);
//        // 设置自定义的对话框界面
//        builder.setView(view);
//
//        cus_dialog = builder.create();
//        cus_dialog.show();


        View view = LayoutInflater.from(AddAreaActivity.this).inflate(R.layout.editscene_dialog, null);
        final TextView confirm; //确定按钮
        TextView cancel; //确定按钮
        TextView tv_title;
//        final TextView content; //内容
        cancel = (TextView) view.findViewById(R.id.call_cancel);
        confirm = (TextView) view.findViewById(R.id.call_confirm);
        final ClearEditText edit_password_gateway = (ClearEditText) view.findViewById(R.id.edit_password_gateway);
//        edit_password_gateway.setText(name);
        edit_password_gateway.setSelection(edit_password_gateway.getText().length());
//        tv_title = (TextView) view.findViewById(R.id.tv_title);
//        tv_title.setText("是否拨打119");
//        content.setText(message);
        //显示数据
        edit_password_gateway.setText(areaName);
        final Dialog dialog = new Dialog(AddAreaActivity.this, R.style.BottomDialog);
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
                if (edit_password_gateway.getText().toString() == null ||
                        edit_password_gateway.getText().toString().trim().equals("")) {
                    ToastUtil.showToast(AddAreaActivity.this, "区域名称为空");
                    return;
                }

                txt_area_name.setText(edit_password_gateway.getText().toString());
                areaName = edit_password_gateway.getText().toString();
                dialog.dismiss();
            }
        });

        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }


//    name：区域名称
//    province：省
//    city：市
//    district：区

    private void initData() {
//        //选项选择器
//        pvOptions = new OptionsPickerView(this);
        // 获取数据库
        SQLiteDatabase db = DBManager.getdb(getApplication());
        //省
        Cursor cursor = db.query("province", null, null, null, null, null,
                null);
        try {
            while (cursor.moveToNext()) {
                int pro_id = cursor.getInt(0);
                String pro_code = cursor.getString(1);
                String pro_name = cursor.getString(2);
                String pro_name2 = cursor.getString(3);
                ProvinceBean provinceBean = new ProvinceBean(pro_id, pro_code, pro_name, pro_name2);
                options1Items.add(provinceBean);//添加一级目录
                Provincestr.add(cursor.getString(2));
                //查询二级目录，市区
                Cursor cursor1 = db.query("city", null, "province_id=?", new String[]{pro_id + ""}, null, null,
                        null);
                ArrayList<CityBean> cityBeanList = new ArrayList<>();
                ArrayList<String> cityStr = new ArrayList<>();
                //地区集合的集合（注意这里要的是当前省份下面，当前所有城市的地区集合我去）
                ArrayList<ArrayList<AreaBean>> options3Items_03 = new ArrayList<>();
                ArrayList<ArrayList<String>> options3Items_str = new ArrayList<>();
                try {
                    while (cursor1.moveToNext()) {
                        int cityid = cursor1.getInt(0);
                        int province_id = cursor1.getInt(1);
                        String code = cursor1.getString(2);
                        String name = cursor1.getString(3);
                        String provincecode = cursor1.getString(4);
                        CityBean cityBean = new CityBean(cityid, province_id, code, name, provincecode);
                        //添加二级目录
                        cityBeanList.add(cityBean);
                        cityStr.add(cursor1.getString(3));
                        //查询三级目录
                        Cursor cursor2 = db.query("area", null, "city_id=?", new String[]{cityid + ""}, null, null,
                                null);
                        ArrayList<AreaBean> areaBeanList = new ArrayList<>();
                        ArrayList<String> areaBeanstr = new ArrayList<>();
                        try {
                            while (cursor2.moveToNext()) {
                                int areaid = cursor2.getInt(0);
                                int city_id = cursor2.getInt(1);
//                    String code0=cursor2.getString(2);
                                String areaname = cursor2.getString(3);
                                String citycode = cursor2.getString(4);
                                AreaBean areaBean = new AreaBean(areaid, city_id, areaname, citycode);
                                areaBeanList.add(areaBean);
                                areaBeanstr.add(cursor2.getString(3));
                            }
                        } finally {
                            if (cursor2 != null) {
                                cursor2.close();
                            }
                        }
                        options3Items_str.add(areaBeanstr);//本次查询的存储内容
                        options3Items_03.add(areaBeanList);
                    }

                } finally {
                    if (cursor1 != null) {
                        cursor1.close();
                    }
                }
                options2Items.add(cityBeanList);//增加二级目录数据
                Citystr.add(cityStr);
                options3Items.add(options3Items_03);//添加三级目录
                Areastr.add(options3Items_str);
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                //选项选择器
                pvOptions = new OptionsPickerView(AddAreaActivity.this);
                //设置三级联动效果
                pvOptions.setPicker(Provincestr, Citystr, Areastr, true);
                //设置选择的三级单位
//        pvOptions.setLabels("省", "市", "区");
                pvOptions.setTitle("选择地区");
                //设置是否循环滚动
                pvOptions.setCyclic(false, false, false);
                //设置默认选中的三级项目
                //监听确定选择按钮
                pvOptions.setSelectOptions(0, 0, 0);
                pvOptions.setOnoptionsSelectListener(new OptionsPickerView.OnOptionsSelectListener() {
                    @Override
                    public void onOptionsSelect(int options1, int option2, int options3) {
                        //返回的分别是三个级别的选中位置
                        String tx = options1Items.get(options1).getPro_name()
                                + options2Items.get(options1).get(option2).getName()
                                + options3Items.get(options1).get(option2).get(options3).getName();
//                tvTitle.setText(tx);

                        province = options1Items.get(options1).getPro_name();
                        city = options2Items.get(options1).get(option2).getName();
                        district = options3Items.get(options1).get(option2).get(options3).getName();
                        ;


                        location_txt.setText(tx);
                    }
                });
            }
        });
    }
}
