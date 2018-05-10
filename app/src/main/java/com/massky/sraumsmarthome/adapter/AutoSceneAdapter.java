package com.massky.sraumsmarthome.adapter;

import android.content.Context;
import android.content.Intent;
import android.transition.Slide;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.massky.sraumsmarthome.R;
import com.massky.sraumsmarthome.activity.SceneSettingActivity;
import com.massky.sraumsmarthome.widget.SlideSwitchButton;
import com.mcxtzhang.swipemenulib.SwipeMenuLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by masskywcy on 2017-05-16.
 */

public class AutoSceneAdapter extends BaseAdapter {
    private List<Map> list = new ArrayList<>();

    public AutoSceneAdapter(Context context, List<Map> list) {
        super(context, list);
        this.list = list;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolderContentType viewHolderContentType = null;
        if (null == convertView) {
            viewHolderContentType = new ViewHolderContentType();
            convertView = LayoutInflater.from(context).inflate(R.layout.auto_scene_item, null);
            viewHolderContentType.device_type_pic = (ImageView) convertView.findViewById(R.id.device_type_pic);
            viewHolderContentType.hand_device_content = (TextView) convertView.findViewById(R.id.hand_device_content);
//            viewHolderContentType.hand_gateway_content = (TextView) convertView.findViewById(R.id.hand_gateway_content);
            viewHolderContentType. hand_scene_btn = (SlideSwitchButton) convertView.findViewById(R.id.slide_btn);
            viewHolderContentType.swipemenu_layout = (SwipeMenuLayout) convertView.findViewById(R.id.swipemenu_layout);
            viewHolderContentType.swipe_content_linear = (LinearLayout) convertView.findViewById(R.id.swipe_content_linear);
            convertView.setTag(viewHolderContentType);
        } else {
            viewHolderContentType = (ViewHolderContentType) convertView.getTag();
        }
        viewHolderContentType.hand_device_content.setText((String) list.get(position).get("name"));

        int element = (Integer) list.get(position).get("image");
        viewHolderContentType.device_type_pic.setImageResource(element);
        final ViewHolderContentType finalViewHolderContentType = viewHolderContentType;
//        viewHolderContentType.hand_scene_btn.setSlideSwitchListener(new SlideSwitchButton.SlideSwitch() {
//            @Override
//            public void slide_switch() {//滑动时，子view滑动时，父view不能滑动
//                finalViewHolderContentType.swipemenu_layout.requestDisallowInterceptTouchEvent(true);
//            }
//        });

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent = new Intent(context, ShangChuanBaoJingActivity.class);
//                intent.putExtra("id", (Serializable) list.get(position).get("id").toString());
//                context.startActivity(intent);
            }
        });
        viewHolderContentType.swipe_content_linear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                context.startActivity(new Intent(context, SceneSettingActivity.class));
            }
        });
        return convertView;
    }

    class ViewHolderContentType {
       ImageView device_type_pic;
       TextView  hand_device_content;
//       TextView  hand_gateway_content;
       SlideSwitchButton hand_scene_btn;
       SwipeMenuLayout swipemenu_layout;
       LinearLayout swipe_content_linear;
    }

}
