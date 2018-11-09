package com.massky.sraum.adapter;

import android.content.Context;
import android.content.Intent;
import android.transition.Slide;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.massky.sraum.R;
import com.massky.sraum.Util.ToastUtil;
import com.massky.sraum.activity.EditSceneSecondActivity;
import com.massky.sraum.activity.SceneSettingActivity;
import com.massky.sraum.widget.SlideSwitchButton;
import com.mcxtzhang.swipemenulib.SwipeMenuLayout;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by masskywcy on 2017-05-16.
 */

public class AutoSceneAdapter extends BaseAdapter {
    private List<Map> list = new ArrayList<>();
    private boolean is_open_to_close;

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
            viewHolderContentType.hand_scene_btn = (SlideSwitchButton) convertView.findViewById(R.id.slide_btn);
            viewHolderContentType.swipemenu_layout = (SwipeMenuLayout) convertView.findViewById(R.id.swipemenu_layout);
            viewHolderContentType.btn_rename = (Button) convertView.findViewById(R.id.btn_rename);

            viewHolderContentType.rename_rel = (RelativeLayout) convertView.findViewById(R.id.rename_rel);
            viewHolderContentType.edit_rel = (RelativeLayout) convertView.findViewById(R.id.edit_rel);
            viewHolderContentType.delete_rel = (RelativeLayout) convertView.findViewById(R.id.delete_rel);
            viewHolderContentType.swipe_content_linear = (LinearLayout) convertView.findViewById(R.id.swipe_content_linear);
            convertView.setTag(viewHolderContentType);
        } else {
            viewHolderContentType = (ViewHolderContentType) convertView.getTag();
        }
        viewHolderContentType.hand_device_content.setText((String) list.get(position).get("name"));

        int element = (Integer) list.get(position).get("image");
        viewHolderContentType.device_type_pic.setImageResource(element);
        final ViewHolderContentType finalViewHolderContentType = viewHolderContentType;
        viewHolderContentType.hand_scene_btn.setSlideSwitchListener(new SlideSwitchButton.SlideSwitch() {
            @Override
            public void slide_switch() {//滑动时，子view滑动时，父view不能滑动
//                finalViewHolderContentType.swipemenu_layout.requestDisallowInterceptTouchEvent(true);
                if (finalViewHolderContentType.hand_scene_btn.isOpen) {
                    ToastUtil.showToast(context, "打开了");
                } else {
                    ToastUtil.showToast(context, "关闭了");
                }

            }
        });

        viewHolderContentType.rename_rel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ToastUtil.showToast(context, "重命名");
            }
        });

        viewHolderContentType.edit_rel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ToastUtil.showToast(context, "编辑");
            }
        });

        viewHolderContentType.btn_rename.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });


        viewHolderContentType.delete_rel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ToastUtil.showToast(context, "删除");
            }
        });


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
//                context.startActivity(new Intent(context, SceneSettingActivity.class));

            }
        });

        ((SwipeMenuLayout) convertView).setOnMenuClickListener(new SwipeMenuLayout.OnMenuClickListener() {

            @Override
            public void onItemClick() {
//                Intent intent = new Intent(context, EditSceneSecondActivity.class);
//                context.startActivity(intent);
                ToastUtil.showDelToast(context,"被点击");
            }

            @Override
            public void onItemClick_By_btn(boolean is_open_to_close1) {
                is_open_to_close = is_open_to_close1;
            }
        });
        return convertView;
    }

    class ViewHolderContentType {
        ImageView device_type_pic;
        TextView hand_device_content;
        //       TextView  hand_gateway_content;
        SlideSwitchButton hand_scene_btn;
        SwipeMenuLayout swipemenu_layout;
        LinearLayout swipe_content_linear;
        public RelativeLayout rename_rel;
        public RelativeLayout edit_rel;
        public RelativeLayout delete_rel;
        public Button btn_rename;

    }

}
