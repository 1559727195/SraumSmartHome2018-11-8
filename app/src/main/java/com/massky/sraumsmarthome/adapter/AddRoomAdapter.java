package com.massky.sraumsmarthome.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.TextView;

import com.massky.sraumsmarthome.R;
import com.massky.sraumsmarthome.activity.SettingRoomActivity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by masskywcy on 2017-05-16.
 */

public class AddRoomAdapter extends BaseAdapter {
    private List<Map> list = new ArrayList<>();

    public AddRoomAdapter(Context context, List<Map> list) {
        super(context, list);
        this.list = list;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolderContentType viewHolderContentType = null;
        if (null == convertView) {
            viewHolderContentType = new ViewHolderContentType();
            convertView = LayoutInflater.from(context).inflate(R.layout.add_room_item, null);
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
                        ViewGroup.LayoutParams.WRAP_CONTENT);
                convertView.setLayoutParams(params);
            } else {
                params.height = itemHeight;
                params.width = itemWidth;
            }

            viewHolderContentType.txt_again_autoscene = (TextView) convertView.findViewById(R.id.txt_again_autoscene);
            viewHolderContentType.pic_room_img  = (ImageView) convertView.findViewById(R.id.pic_room_img);
            convertView.setTag(viewHolderContentType);
        } else {
            viewHolderContentType = (ViewHolderContentType) convertView.getTag();
        }

//        String type = (String) list.get(position).get("type");
//        switch (type) {
//            case "0":
//                viewHolderContentType.img_again_autoscene.setVisibility(View.GONE);//
//                break;
//            case "1":
//                viewHolderContentType.img_again_autoscene.setVisibility(View.VISIBLE);//
//                break;
//        }
//        viewHolderContentType.txt_again_autoscene.setText(list.get(position).get("name").toString());

        final ViewHolderContentType finalViewHolderContentType = viewHolderContentType;
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, SettingRoomActivity.class);
//                intent.putExtra("id", (Serializable) list.get(position).get("id").toString());
                context.startActivity(intent);
            }
        });

        int element = (Integer) list.get(position).get("image");
        viewHolderContentType.pic_room_img.setImageResource(element);
        viewHolderContentType.txt_again_autoscene.setText(list.get(position).get("name").toString());

        return convertView;
    }

    class ViewHolderContentType {
       TextView  txt_again_autoscene;
       ImageView pic_room_img;
    }
}
