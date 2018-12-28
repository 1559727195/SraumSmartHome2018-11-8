package com.massky.sraum.adapter;

import android.app.Dialog;
import android.content.Context;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.AddTogenInterface.AddTogglenInterfacer;
import com.massky.sraum.R;
import com.massky.sraum.User;
import com.massky.sraum.Util.MyOkHttp;
import com.massky.sraum.Util.Mycallback;
import com.massky.sraum.Util.ToastUtil;
import com.massky.sraum.Utils.ApiHelper;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by masskywcy on 2017-05-16.
 */

public class ManagerRoomAdapter extends BaseAdapter {
//    private List<Map> list = new ArrayList<>();

    public ManagerRoomAdapter(Context context, List<Map> list) {
        super(context, list);
//        this.list = list;

    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolderContentType viewHolderContentType = null;
        if (null == convertView) {
            viewHolderContentType = new ViewHolderContentType();
            convertView = LayoutInflater.from(context).inflate(R.layout.room_manager_item, null);
            viewHolderContentType.room_name_txt = (TextView) convertView.findViewById(R.id.room_name_txt);
            viewHolderContentType.img_again_autoscene = (ImageView) convertView.findViewById(R.id.img_again_autoscene);
            //pic_room_img
            viewHolderContentType.pic_room_img = (ImageView) convertView.findViewById(R.id.pic_room_img);
            viewHolderContentType.txt_device_num = (TextView) convertView.findViewById(R.id.txt_device_num);
            viewHolderContentType.rename_btn = (Button) convertView.findViewById(R.id.rename_btn);
            viewHolderContentType.remove_btn = (Button) convertView.findViewById(R.id.remove_btn);
            convertView.setTag(viewHolderContentType);
        } else {
            viewHolderContentType = (ViewHolderContentType) convertView.getTag();
        }

        viewHolderContentType.room_name_txt.setText(((Map) getList().get(position)).get("name").toString());
//        viewHolderContentType.txt_device_num.setText(((Map) getList().get(position)).get("count").toString() + "个设备");

        final ViewHolderContentType finalViewHolderContentType = viewHolderContentType;
        viewHolderContentType.rename_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ToastUtil.showToast(context, "重命名");
                Map map = new HashMap();
                map.put("number", (Map) ((Map) getList().get(position)).get("number"));
                showRenameDialog(map);
            }
        });

        viewHolderContentType.remove_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ToastUtil.showToast(context, "删除");
                sraum_deleteRoom(position);
            }
        });
        return convertView;//
    }

    /**
     * 删除房间（）APP->网关
     *
     * @param position
     */
    private void sraum_deleteRoom(final int position) {
        Map map = new HashMap();
        map.put("command", "sraum_deleteRoom");
        map.put("number", ((Map) getList().get(position)).get("number"));
        MyOkHttp.postMapObject(ApiHelper.sraum_deleteRoom, null,
                new Mycallback(new AddTogglenInterfacer() {
                    @Override
                    public void addTogglenInterfacer() {

                    }
                }, context, null) {
                    @Override
                    public void onSuccess(User user) {
                        ToastUtil.showToast(context, "删除房间成功");
                        for (int i = 0; i < getList().size(); i++) {
                            if (((Map) getList().get(position)).get("number").equals(
                                    ((Map) getList().get(i)).get("number"))) {
                                //
                                getList().remove(i);
                                notifyDataSetChanged();
                            }
                        }
                    }
                });
    }

    /**
     * 修改房间名称
     *
     * @param map
     */
    private void sraum_updateRoomName(final Map map) {
        //获取网关名称（APP->网关）

        MyOkHttp.postMapObject(ApiHelper.sraum_updateRoomName, map,
                new Mycallback(new AddTogglenInterfacer() {
                    @Override
                    public void addTogglenInterfacer() {
                        sraum_updateRoomName(map);
                    }
                }, context, null) {
                    @Override
                    public void onSuccess(User user) {
                        ToastUtil.showToast(context, "修改名字成功");
                    }
                });
    }

    //自定义dialog,自定义重命名dialog

    public void showRenameDialog(final Map map) {
        View view = LayoutInflater.from(context).inflate(R.layout.editscene_dialog, null);
        TextView confirm; //确定按钮
        TextView cancel; //确定按钮
        TextView tv_title;
//        final TextView content; //内容
        cancel = (TextView) view.findViewById(R.id.call_cancel);
        confirm = (TextView) view.findViewById(R.id.call_confirm);
        tv_title = (TextView) view.findViewById(R.id.tv_title);
//        tv_title.setText("是否拨打119");
//        content.setText(message);
        //显示数据
        final Dialog dialog = new Dialog(context, R.style.BottomDialog);
        dialog.setContentView(view);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        int displayWidth = dm.widthPixels;
        int displayHeight = dm.heightPixels;
        android.view.WindowManager.LayoutParams p = dialog.getWindow().getAttributes(); //获取对话框当前的参数值
        p.width = (int) (displayWidth * 0.8); //宽度设置为屏幕的0.5
//        p.height = (int) (displayHeight * 0.5); //宽度设置为屏幕的0.5
//        dialog.setCanceledOnTouchOutside(false);// 设置点击屏幕Dialog不消失
        dialog.getWindow().setAttributes(p);  //设置生效
        dialog.show();

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                map.put("newName", "newName");
                sraum_updateRoomName(map);//修改房间名称
                dialog.dismiss();
            }
        });
    }

    class ViewHolderContentType {
        ImageView img_again_autoscene;
        TextView room_name_txt;
        ImageView pic_room_img;
        TextView txt_device_num;
        Button rename_btn;
        Button remove_btn;
    }
}
