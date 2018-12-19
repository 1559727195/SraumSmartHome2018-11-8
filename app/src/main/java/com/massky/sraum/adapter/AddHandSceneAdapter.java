package com.massky.sraum.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.massky.sraum.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by masskywcy on 2017-05-16.
 */

public class AddHandSceneAdapter extends BaseAdapter {
    private final Context context;
    private List<Map> list = new ArrayList<>();
    // 用来控制CheckBox的选中状况
    private static HashMap<Integer, Boolean> isSelected;
    private List<Map> listmark;
    private AddHandSceneListener addHandSceneListener;

    public AddHandSceneAdapter(Context context, List<Map> list,
                               AddHandSceneListener addHandSceneListener) {
        this.context = context;
        isSelected = new HashMap<Integer, Boolean>();
        this.listmark = listmark;
        this.list = list;
        this.addHandSceneListener = addHandSceneListener;
        initDate();
    }


    // 初始化isSelected的数据
    private void initDate() {
        for (int i = 0; i < list.size(); i++) {
            getIsSelected().put(i, false);
        }
    }

    public static HashMap<Integer, Boolean> getIsSelected() {
        return isSelected;
    }

    public static void setIsSelected(HashMap<Integer, Boolean> isSelected) {
        isSelected = isSelected;
    }


    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolderContentType viewHolderContentType = null;
        if (null == convertView) {
            viewHolderContentType = new ViewHolderContentType();
            convertView = LayoutInflater.from(context).inflate(R.layout.add_hand_scene_item, null);

            viewHolderContentType.hand_device_name = (TextView) convertView.findViewById(R.id.hand_device_name);
//            viewHolderContentType.hand_gateway_content = (TextView) convertView.findViewById(R.id.hand_gateway_content);
//            viewHolderContentType. hand_scene_btn = (Button) convertView.findViewById(R.id.hand_scene_btn);

            viewHolderContentType.activity_group_radioGroup_light = (RadioGroup) convertView.findViewById(R.id.activity_group_radioGroup_light);
            viewHolderContentType.cb = (CheckBox) convertView.findViewById(R.id.checkbox);

            convertView.setTag(viewHolderContentType);
        } else {
            viewHolderContentType = (ViewHolderContentType) convertView.getTag();
        }
        String type = (String) list.get(position).get("type");
        switch (type) {
            case "A204":
            case "A203":
            case "A202":
            case "A201":
                viewHolderContentType.activity_group_radioGroup_light.setVisibility(View.VISIBLE);
                break;
            case "A501":

                break;
            case "A401":

                break;
            case "A303":

                break;
        }
        viewHolderContentType.hand_device_name.setText(list.get(position).get("name").toString());

        final ViewHolderContentType finalViewHolderContentType = viewHolderContentType;
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent = new Intent(context, ShangChuanBaoJingActivity.class);
//                intent.putExtra("id", (Serializable) list.get(position).get("id").toString());
//                context.startActivity(intent);

                if (!finalViewHolderContentType.cb.isChecked()) {
                    if (addHandSceneListener != null)
                        addHandSceneListener.addhand_scene_list(finalViewHolderContentType.cb.isChecked(), position, (String) list.get(position).get("type").toString());
                }

                finalViewHolderContentType.cb.toggle();
                //设置checkbox现在状态
                getIsSelected().put(position, finalViewHolderContentType.cb.isChecked());
            }
        });

        // 根据isSelected来设置checkbox的选中状况
        viewHolderContentType.cb.setChecked(getIsSelected().get(position));

        return convertView;
    }

    public void setLists(List<Map> list_scene) {
        this.list = list_scene;
    }

    class ViewHolderContentType {
        ImageView device_type_pic;
        TextView hand_device_name;
        TextView hand_gateway_content;
        Button hand_scene_btn;
        RadioGroup activity_group_radioGroup_light;
        CheckBox cb;
    }

    public interface AddHandSceneListener {
        void addhand_scene_list(boolean ischecked, int position, String type);
    }
}
