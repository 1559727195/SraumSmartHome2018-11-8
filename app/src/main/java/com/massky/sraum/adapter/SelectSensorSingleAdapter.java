package com.massky.sraum.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.massky.sraum.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by masskywcy on 2017-05-16.
 */

public class SelectSensorSingleAdapter extends android.widget.BaseAdapter {
    private List<Map> list = new ArrayList<>();
    private List<Integer> listint = new ArrayList<>();
    private List<Integer> listintwo = new ArrayList<>();
    private int temp = -1;
    private Activity activity;//上下文
    // 用来控制CheckBox的选中状况
    private static HashMap<Integer, Boolean> isSelected = new HashMap<>();

    public SelectSensorSingleAdapter(Activity context, List<Map> list, List<Integer> listint, List<Integer> listintwo, SelectSensorListener selectSensorListener) {
        this.list = list;
        this.listint = listint;
        this.listintwo = listintwo;
        this.activity = context;
        this.selectSensorListener = selectSensorListener;
        this.activity = context;
//        isSelected = new HashMap<Integer, Boolean>();
//        initDate();
    }
//
//    // 初始化isSelected的数据
//    private void initDate() {
//        for (int i = 0; i < list_bool.size(); i++) {
//            getIsSelected().put(i, false);
//        }
//    }


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
            convertView = LayoutInflater.from(activity).inflate(R.layout.select_sensor_item, null);
            viewHolderContentType.img_guan_scene = (ImageView) convertView.findViewById(R.id.img_guan_scene);
            viewHolderContentType.panel_scene_name_txt = (TextView) convertView.findViewById(R.id.panel_scene_name_txt);
            viewHolderContentType.execute_scene_txt = (TextView) convertView.findViewById(R.id.execute_scene_txt);
            viewHolderContentType.checkbox = (CheckBox) convertView.findViewById(R.id.checkbox);
            //gateway_name_txt
            viewHolderContentType.gateway_name_txt = (TextView) convertView.findViewById(R.id.gateway_name_txt);

            convertView.setTag(viewHolderContentType);
        } else {
            viewHolderContentType = (ViewHolderContentType) convertView.getTag();
        }


        viewHolderContentType.panel_scene_name_txt.setText((String) list.get(position).get("name"));
        viewHolderContentType.gateway_name_txt.setText((String) list.get(position).get("boxName"));
//        int element = (Integer) list.get(position).get("image");
//        viewHolderContentType.img_guan_scene.setImageResource(element);
//        viewHolderContentType.panel_scene_name_txt.setText(list.get(position).get("name").toString());


//        convertView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
////                Intent intent = new Intent(context, DeviceExcuteOpenActivity.class);
////                context.startActivity(intent);
//
//
//            }
//        });
//        viewHolderContentType.checkbox.setChecked(getIsSelected().get(position));
//        if (getIsSelected().get(position)) {
//            viewHolderContentType.img_guan_scene.setImageResource(listintwo.get(position));
//        } else {
//            viewHolderContentType.img_guan_scene.setImageResource(listint.get(position));
//        }

        viewHolderContentType.checkbox.setId(position);//对checkbox的id进行重新设置为当前的position
        viewHolderContentType.checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            //把上次被选中的checkbox设为false
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {//实现checkbox的单选功能,同样适用于radiobutton
                    if (temp != -1) {
                        //找到上次点击的checkbox,并把它设置为false,对重新选择时可以将以前的关掉
//                        CheckBox tempCheckBox = (CheckBox) activity.findViewById(temp);
//                        if (tempCheckBox != null)
//                            tempCheckBox.setChecked(false);
                    }
                    temp = buttonView.getId();//保存当前选中的checkbox的id值
                } else {
//                    CheckBox tempCheckBox = (CheckBox) activity.findViewById(temp);
//                    if (tempCheckBox != null)
//                        tempCheckBox.setChecked(false);
                }
                notifyDataSetChanged();
            }
        });
        //System.out.println("temp:"+temp);
        //System.out.println("position:"+position);
        if (position == temp) {//比对position和当前的temp是否一致
//            viewHolderContentType.checkbox.setChecked(true);
//            viewHolderContentType.checkbox.toggle();
            if (viewHolderContentType.checkbox.isChecked()) {
                viewHolderContentType.img_guan_scene.setImageResource(listintwo.get(position));
                viewHolderContentType.panel_scene_name_txt.setTextColor(activity.getResources().getColor(R.color.gold_color));
                if (selectSensorListener != null)
                    selectSensorListener.selectsensor(position);
            } else {
                viewHolderContentType.img_guan_scene.setImageResource(listint.get(position));
                viewHolderContentType.panel_scene_name_txt.setTextColor(activity.getResources().getColor(R.color.black_color));
            }
        } else {
            viewHolderContentType.checkbox.setChecked(false);
            viewHolderContentType.img_guan_scene.setImageResource(listint.get(position));
            viewHolderContentType.panel_scene_name_txt.setTextColor(activity.getResources().getColor(R.color.black_color));
        }
        return convertView;
    }

    public void setLists(List<Map> list_hand_scene, List<Integer> listint, List<Integer> listintwo) {
        this.list = list_hand_scene;
        this.listint = listint;
        this.listintwo = listintwo;
    }

//    public static HashMap<Integer, Boolean> getIsSelected() {
//        return isSelected;
//    }
//
//    public static void setIsSelected(HashMap<Integer, Boolean> isSelected) {
//        SelectSensorSingleAdapter.isSelected = isSelected;
//    }


    public static class ViewHolderContentType {
        public ImageView img_guan_scene;
        public TextView panel_scene_name_txt;
        public TextView execute_scene_txt;
        public CheckBox checkbox;
        public TextView gateway_name_txt;
    }

    public SelectSensorListener selectSensorListener;

    public interface SelectSensorListener {
        void selectsensor(int position);
    }
}
