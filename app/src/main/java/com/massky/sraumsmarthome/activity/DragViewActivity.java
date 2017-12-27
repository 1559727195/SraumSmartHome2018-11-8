package com.massky.sraumsmarthome.activity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.SimpleAdapter;
import com.massky.sraumsmarthome.R;
import com.massky.sraumsmarthome.view.DragGridView;
import com.yanzhenjie.statusview.NavigationView;
import com.yanzhenjie.statusview.StatusUtils;
import com.yanzhenjie.statusview.StatusView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

/**
 * Created by zhu on 2017/12/11.
 */

public class DragViewActivity extends AppCompatActivity {
    private List<HashMap<String, Object>> dataSourceList = new ArrayList<>();
    StatusView mStatusView;
    NavigationView mNavigationView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dragview_layout);
        StatusUtils.setFullToStatusBar(this);  // StatusBar.
        StatusUtils.setFullToNavigationBar(this); // NavigationBar.
        mStatusView = (StatusView) findViewById(R.id.status_view);
        mNavigationView = (NavigationView) findViewById(R.id.navigation_view);
        setAnyBarAlpha(0);//设置状态栏的颜色为透明
        DragGridView mDragGridView = (DragGridView) findViewById(R.id.dragGridView);
        for (int i = 0; i < 30; i++) {
            HashMap<String, Object> itemHashMap = new HashMap<>();
            itemHashMap.put("item_image", R.mipmap.ic_launcher);
            itemHashMap.put("item_text", "拖拽" + Integer.toString(i));
            dataSourceList.add(itemHashMap);
        }

        final SimpleAdapter mSimpleAdapter = new SimpleAdapter(this, dataSourceList,
                R.layout.drag_view_item, new String[]{"item_image", "item_text"}, new int[]{R.id.item_image, R.id.item_text});

        mDragGridView.setAdapter(mSimpleAdapter);
        mDragGridView.setOnChangeListener(new DragGridView.OnChanageListener() {

            @Override
            public void onChange(int from, int to) {
                HashMap<String, Object> temp = dataSourceList.get(from);
                //直接交互item
                //这里的处理需要注意下
                if (from < to) {
                    for (int i = from; i < to; i++) {
                        Collections.swap(dataSourceList, i, i + 1);
                    }
                } else if (from > to) {
                    for (int i = from; i > to; i--) {
                        Collections.swap(dataSourceList, i, i - 1);
                    }
                }
                dataSourceList.set(to, temp);
                mSimpleAdapter.notifyDataSetChanged();
            }
        });

        mDragGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.e("robin debug","dataSourceList.item:" + dataSourceList.get(position).get("item_text")
                + "position:" + position);
            }
        });
    }


    /*
     *动态设置状态栏的颜色
     */
    private void setAnyBarAlpha(int alpha) {
//        mToolbar.getBackground().mutate().setAlpha(alpha);
        mStatusView.getBackground().mutate().setAlpha(alpha);
        mNavigationView.getBackground().mutate().setAlpha(alpha);//底部
    }
}
