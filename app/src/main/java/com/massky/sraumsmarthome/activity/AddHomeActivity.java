package com.massky.sraumsmarthome.activity;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Handler;
import android.os.Message;
import android.support.percent.PercentRelativeLayout;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bigkoo.pickerview_new.OptionsPickerView;
import com.massky.sraumsmarthome.R;
import com.massky.sraumsmarthome.base.BaseActivity;
import com.yanzhenjie.statusview.StatusUtils;

import java.util.ArrayList;

import butterknife.InjectView;
import db.AreaBean;
import db.CityBean;
import db.DBManager;
import db.ProvinceBean;

/**
 * Created by zhu on 2018/2/12.
 */

public class AddHomeActivity extends BaseActivity {
    @InjectView(R.id.back)
    ImageView back;
    @InjectView(R.id.add_home_rel)
    PercentRelativeLayout add_home_rel;
    private OptionsPickerView pvOptions;//地址选择器
    private ArrayList<ProvinceBean> options1Items = new ArrayList<>();//省
    private ArrayList<ArrayList<CityBean>> options2Items = new ArrayList<>();//市
    private ArrayList<ArrayList<ArrayList<AreaBean>>> options3Items = new ArrayList<>();//区
    private ArrayList<String> Provincestr = new ArrayList<>();//省
    private ArrayList<ArrayList<String>> Citystr = new ArrayList<>();//市
    private ArrayList<ArrayList<ArrayList<String>>> Areastr = new ArrayList<>();//区
    @InjectView(R.id.location_txt)
    TextView location_txt;
    @InjectView(R.id.hostory_back_txt)
    TextView hostory_back_txt;


    @Override
    protected int viewId() {
        return R.layout.add_home_act;
    }

    @Override
    protected void onView() {
        StatusUtils.setFullToStatusBar(this);
    }

    @Override
    protected void onEvent() {
        back.setOnClickListener(this);
        add_home_rel.setOnClickListener(this);
        hostory_back_txt.setOnClickListener(this);
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
                AddHomeActivity.this.finish();
                break;
            case R.id.add_home_rel:
                pvOptions.show();
                break;//选择地址
            case R.id.hostory_back_txt:
                AddHomeActivity.this.finish();
                break;
        }
    }


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
            if(cursor != null) {
                cursor.close();
            }
        }

        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                //选项选择器
                pvOptions = new OptionsPickerView(AddHomeActivity.this);
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
                        location_txt.setText(tx);
                    }
                });
            }
        });
    }
}
