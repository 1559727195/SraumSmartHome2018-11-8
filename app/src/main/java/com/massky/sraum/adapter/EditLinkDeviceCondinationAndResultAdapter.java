package com.massky.sraum.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.massky.sraum.R;
import com.massky.sraum.Util.SharedPreferencesUtil;
import com.massky.sraum.widget.SlideSwitchButton;
import com.mcxtzhang.swipemenulib.SwipeMenuLayout;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by masskywcy on 2017-05-16.
 */

public class EditLinkDeviceCondinationAndResultAdapter extends android.widget.BaseAdapter {

    private List<Map> listint = new ArrayList<>();
    private List<String> listintwo = new ArrayList<>();
    private Context context;
    ExcutecuteListener excutecuteListener;
    HashMap<Integer, Boolean> isSelected = new HashMap<>();

    public EditLinkDeviceCondinationAndResultAdapter(Context context, List<Map> listint, ExcutecuteListener excutecuteListener) {
        this.listint = listint;
        this.context = context;
        this.excutecuteListener = excutecuteListener;
    }


    @Override
    public int getCount() {
        return listint.size();
    }

    @Override
    public Object getItem(int position) {
        return listint.get(position);
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
            convertView = LayoutInflater.from(context).inflate(R.layout.edit_linkage_condination_result_item, null);
            viewHolderContentType.img_guan_scene = (ImageView) convertView.findViewById(R.id.img_guan_scene);
            viewHolderContentType.panel_scene_name_txt = (TextView) convertView.findViewById(R.id.panel_scene_name_txt);
            viewHolderContentType.execute_scene_txt = (TextView) convertView.findViewById(R.id.execute_scene_txt);
            //gateway_name_txt
            viewHolderContentType.gateway_name_txt = (TextView) convertView.findViewById(R.id.gateway_name_txt);
            //tiaoguang_value
            viewHolderContentType.tiaoguang_value = (TextView) convertView.findViewById(R.id.tiaoguang_value);
            viewHolderContentType.scene_set = (ImageView) convertView.findViewById(R.id.scene_set);
            viewHolderContentType.hand_scene_btn = (SlideSwitchButton) convertView.findViewById(R.id.slide_btn);
            viewHolderContentType.swipemenu_layout = (SwipeMenuLayout) convertView.findViewById(R.id.swipemenu_layout);
            viewHolderContentType.delete_btn = (Button) convertView.findViewById(R.id.delete_btn);
            convertView.setTag(viewHolderContentType);
//            viewHolderContentType.tiaoguang_value.setTag(R.id.tiaoguang_open, position_index++);
        } else {
            viewHolderContentType = (ViewHolderContentType) convertView.getTag();
        }


//        int element = (Integer) list.get(position).get("image");
//        viewHolderContentType.img_guan_scene.setImageResource(element);
        viewHolderContentType.panel_scene_name_txt.setText(listint.get(position).get("name1").toString());
//        switch (listint.get(position).get("name1").toString()) {
//            case "手动执行":
//                viewHolderContentType.swipemenu_layout.setLeftSwipe(false);
//                break;
//            default:
//                viewHolderContentType.swipemenu_layout.setLeftSwipe(true);
//                break;
//        }
        viewHolderContentType.execute_scene_txt.setText(listint.get(position).get("action").toString());
        if (listint.get(position).get("boxName").toString().equals("")) {
            viewHolderContentType.gateway_name_txt.setVisibility(View.GONE);
        } else {
            viewHolderContentType.gateway_name_txt.setText(listint.get(position).get("boxName").toString());
        }

        final ViewHolderContentType finalViewHolderContentType = viewHolderContentType;
        viewHolderContentType.delete_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listint.get(position).get("condition") != null) {//是执行条件
                    delete_item("list_condition", position);
                } else {//执行结果
                    delete_item("list_result", position);
                }

                finalViewHolderContentType.swipemenu_layout.quickClose();
//                    delete_item();
            }
        });

        return convertView;
    }

    /**
     * 是删除条件还是结果
     *
     * @param excute_condition
     * @param position
     */
    private void delete_item(String excute_condition, int position) {
//        switch (excute_condition) {
//            case "执行条件":
//
//                break;
//            case "执行结果":
//
//                break;
//        }
        listint.remove(position);
        notifyDataSetChanged();
        excutecuteListener.excute_cordition();
        SharedPreferencesUtil.remove_current_index(context, excute_condition, position);
    }

//    public static HashMap<Integer, Boolean> getIsSelected() {
//        return isSelected;
//    }

    public static void setIsSelected(HashMap<Integer, Boolean> isSelected) {
        isSelected = isSelected;
    }

    public void setlist(List<Map> listint) {
        this.listint = listint;
        notifyDataSetChanged();
    }


    class ViewHolderContentType {
        ImageView img_guan_scene;
        TextView panel_scene_name_txt;//name
        TextView execute_scene_txt;//action
        public TextView gateway_name_txt;
        TextView tiaoguang_value;
        ImageView scene_set;
        SlideSwitchButton hand_scene_btn;
        SwipeMenuLayout swipemenu_layout;
        public Button delete_btn;
    }


    public interface ExcutecuteListener {
        void excute_cordition();

        void excute_result();
    }
}
