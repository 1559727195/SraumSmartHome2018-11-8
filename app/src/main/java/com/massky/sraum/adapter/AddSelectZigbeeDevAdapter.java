package com.massky.sraum.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.TextView;

import com.massky.sraum.R;
import com.massky.sraum.activity.AddZigbeeDeviceActivity;
import com.massky.sraum.activity.SelectZigbeeDeviceActivity;
import com.massky.sraum.activity.SettingRoomActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by masskywcy on 2017-05-16.
 */

public class AddSelectZigbeeDevAdapter extends BaseAdapter {
    private List<Map> list = new ArrayList<>();

    public AddSelectZigbeeDevAdapter(Context context, List<Map> list) {
        super(context, list);
        this.list = list;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolderContentType viewHolderContentType = null;
        if (null == convertView) {
            viewHolderContentType = new ViewHolderContentType();
            convertView = LayoutInflater.from(context).inflate(R.layout.add_select_zigbeedev_item, null);
            // Calculate the item width by the column number to let total width fill the screen width
            // 根据列数计算项目宽度，以使总宽度尽量填充屏幕
            int itemWidth = (int) (context.getResources().getDisplayMetrics().widthPixels - 4 * 10) / 4;
            // Calculate the height by your scale rate, I just use itemWidth here
            // 下面根据比例计算您的item的高度，此处只是使用itemWidth
            int itemHeight = itemWidth;
            AbsListView.LayoutParams params = (AbsListView.LayoutParams) convertView.getLayoutParams();
            if (params == null) {
                params = new AbsListView.LayoutParams(
                        itemWidth / 10 * 9,
                        itemHeight / 10 * 9);
                convertView.setLayoutParams(params);
            } else {
                params.height = itemHeight;
                params.width = itemWidth;
            }

            viewHolderContentType.txt_again_autoscene = (TextView) convertView.findViewById(R.id.title);
            viewHolderContentType.img_again_autoscene = (ImageView) convertView.findViewById(R.id.image);
            convertView.setTag(viewHolderContentType);
        } else {
            viewHolderContentType = (ViewHolderContentType) convertView.getTag();
        }



        viewHolderContentType.img_again_autoscene.setImageResource((Integer) list.get(position).get("item_image"));

        viewHolderContentType.txt_again_autoscene.setText(list.get(position).get("device_name").toString());

        final ViewHolderContentType finalViewHolderContentType = viewHolderContentType;
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String device_name = list.get(position).get("device_name").toString();
                Intent intent = new Intent(context,
                        AddZigbeeDeviceActivity.class);
                intent.putExtra("device_name",device_name);
                 context.startActivity(intent);
            }
        });


        return convertView;
    }

    class ViewHolderContentType {
       ImageView img_again_autoscene;
       TextView  txt_again_autoscene;
    }
}
