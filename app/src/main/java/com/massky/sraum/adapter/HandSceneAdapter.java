package com.massky.sraum.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.massky.sraum.R;
import com.massky.sraum.activity.EditSceneActivity;
import com.massky.sraum.activity.EditSceneSecondActivity;
import com.mcxtzhang.swipemenulib.SwipeMenuLayout;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by masskywcy on 2017-05-16.
 */

public class HandSceneAdapter extends BaseAdapter {
    private List<Map> list = new ArrayList<>();
    private boolean is_open_to_close;

    public HandSceneAdapter(Context context, List<Map> list) {
        super(context, list);
        this.list = list;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolderContentType viewHolderContentType = null;
        if (null == convertView) {
            viewHolderContentType = new ViewHolderContentType();
            convertView = LayoutInflater.from(context).inflate(R.layout.hand_scene_item, null);
            viewHolderContentType.device_type_pic = (ImageView) convertView.findViewById(R.id.device_type_pic);
            viewHolderContentType.hand_device_content = (TextView) convertView.findViewById(R.id.hand_device_content);
            viewHolderContentType.swipe_context = (LinearLayout) convertView.findViewById(R.id.swipe_context);
//            viewHolderContentType.hand_gateway_content = (TextView) convertView.findViewById(R.id.hand_gateway_content);
            viewHolderContentType.hand_scene_btn = (ImageView) convertView.findViewById(R.id.hand_scene_btn);
            viewHolderContentType.swipe_layout = (SwipeMenuLayout) convertView.findViewById(R.id.swipe_layout);


            convertView.setTag(viewHolderContentType);
        } else {
            viewHolderContentType = (ViewHolderContentType) convertView.getTag();
        }

        int element = (Integer) list.get(position).get("image");
        viewHolderContentType.device_type_pic.setImageResource(element);
        viewHolderContentType.hand_device_content.setText(list.get(position).get("name").toString());

        final ViewHolderContentType finalViewHolderContentType1 = viewHolderContentType;
//        viewHolderContentType.swipe_context.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if ( finalViewHolderContentType1.swipe_layout.isIsopen()) {
//
//                } else {
//                    Intent intent = new Intent(context, EditSceneSecondActivity.class);
//                    context.startActivity(intent);
//                }
//            }
//        });

        ((SwipeMenuLayout) convertView).setOnMenuClickListener(new SwipeMenuLayout.OnMenuClickListener() {


            @Override
            public void onItemClick() {
                Intent intent = new Intent(context, EditSceneSecondActivity.class);
                context.startActivity(intent);
            }

            @Override
            public void onItemClick_By_btn(boolean is_open_to_close1) {//SwipeLayout是否在打开到关闭的过程
                is_open_to_close = is_open_to_close1;
            }
        });

        final ViewHolderContentType finalViewHolderContentType = viewHolderContentType;
        viewHolderContentType.hand_scene_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(is_open_to_close) {//SwipeLayout是否在打开到关闭的过程
                    is_open_to_close = false;
                } else
                finalViewHolderContentType.hand_scene_btn.setImageResource(R.drawable.icon_root);
            }
        });
        return convertView;
    }

    class ViewHolderContentType {
        ImageView device_type_pic;
        TextView hand_device_content;
        TextView hand_gateway_content;
        ImageView hand_scene_btn;
        LinearLayout swipe_context;
        SwipeMenuLayout swipe_layout;

    }
}
