package com.massky.sraum.adapter;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Vibrator;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.AddTogenInterface.AddTogglenInterfacer;
import com.massky.sraum.R;
import com.massky.sraum.User;
import com.massky.sraum.Util.DialogUtil;
import com.massky.sraum.Util.MusicUtil;
import com.massky.sraum.Util.MyOkHttp;
import com.massky.sraum.Util.Mycallback;
import com.massky.sraum.Util.SharedPreferencesUtil;
import com.massky.sraum.Util.ToastUtil;
import com.massky.sraum.Util.TokenUtil;
import com.massky.sraum.Utils.ApiHelper;
import com.massky.sraum.activity.EditLinkDeviceResultActivity;
import com.massky.sraum.activity.EditSceneActivity;
import com.massky.sraum.activity.EditSceneSecondActivity;
import com.massky.sraum.view.ClearEditText;
import com.mcxtzhang.swipemenulib.SwipeMenuLayout;
import com.wheel.widget.TosAdapterView;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;

/**
 * Created by masskywcy on 2017-05-16.
 */

public class HandSceneAdapter extends BaseAdapter {
    private List<Map> list = new ArrayList<>();
    private boolean is_open_to_close;
    private List<Integer> listint = new ArrayList<>();
    private List<Integer> listintwo = new ArrayList<>();
    DialogUtil dialogUtil;
    private  RefreshListener refreshListener;

    public HandSceneAdapter(Context context, List<Map> list,
                            List<Integer> listint,
                            List<Integer> listintwo, DialogUtil dialogUtil,RefreshListener refreshListener) {
        super(context, list);
        this.list = list;
        this.listint = listint;
        this.listintwo = listintwo;
        this.dialogUtil = dialogUtil;
        this.refreshListener = refreshListener;
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
            viewHolderContentType.hand_gateway_content = (TextView) convertView.findViewById(R.id.hand_gateway_content);
            viewHolderContentType.hand_scene_btn = (ImageView) convertView.findViewById(R.id.hand_scene_btn);
            viewHolderContentType.swipe_layout = (SwipeMenuLayout) convertView.findViewById(R.id.swipe_layout);
            viewHolderContentType.delete_btn = (Button) convertView.findViewById(R.id.delete_btn);
            viewHolderContentType.edit_btn = (Button) convertView.findViewById(R.id.edit_btn);
            viewHolderContentType.rename_btn = (Button) convertView.findViewById(R.id.rename_btn);

            convertView.setTag(viewHolderContentType);
        } else {
            viewHolderContentType = (ViewHolderContentType) convertView.getTag();
        }

//        int element = (Integer) list.get(position).get("image");
        viewHolderContentType.device_type_pic.setImageResource(listint.get(position));
        viewHolderContentType.hand_device_content.setText(list.get(position).get("name").toString());

        final ViewHolderContentType finalViewHolderContentType1 = viewHolderContentType;
        String type = list.get(position).get("name").toString();
        if (type != null)
            switch (type) {
                case "100":
                    viewHolderContentType.hand_gateway_content.setText("云场景");
                    break;
                default:
                    viewHolderContentType.hand_gateway_content.setText("网关场景");
                    break;
            }

        ((SwipeMenuLayout) convertView).setOnMenuClickListener(new SwipeMenuLayout.OnMenuClickListener() {


            @Override
            public void onItemClick() {
                goto_editlink(position);
            }

            @Override
            public void onItemClick_By_btn(boolean is_open_to_close1) {//SwipeLayout是否在打开到关闭的过程
                is_open_to_close = is_open_to_close1;
            }
        });


        viewHolderContentType.rename_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                finalViewHolderContentType.popwindowUtil.loadPopupwindow();
                showRenameDialog(list.get(position).get("number").toString(), list.get(position).get("name").toString(), position);
                finalViewHolderContentType1.swipe_layout.quickClose();
            }
        });

        final ViewHolderContentType finalViewHolderContentType2 = viewHolderContentType;
        viewHolderContentType.edit_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goto_editlink(position);
                finalViewHolderContentType2.swipe_layout.quickClose();
            }
        });

        viewHolderContentType.delete_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCenterDeleteDialog(list.get(position).get("number").toString(), list.get(position).get("name").toString());
                finalViewHolderContentType1.swipe_layout.quickClose();
            }
        });

        final ViewHolderContentType finalViewHolderContentType = viewHolderContentType;
        viewHolderContentType.hand_scene_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!is_open_to_close) {//SwipeLayout是否在打开到关闭的过程
                    sraum_manualSceneControl(list.get(position).get("number").toString());
//                    finalViewHolderContentType.hand_scene_btn.setImageResource(R.drawable.icon_root);
                    ToastUtil.showToast(context,"click");
                }
            }
        });
        return convertView;
    }

    //自定义dialog,自定义重命名dialog

    public void showRenameDialog(final String id, final String name, final int position) {
//        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
//        // 布局填充器
//        LayoutInflater inflater = LayoutInflater.from(getActivity());
//        View view = inflater.inflate(R.layout.user_name_dialog, null);
//        // 设置自定义的对话框界面
//        builder.setView(view);
//
//        cus_dialog = builder.create();
//        cus_dialog.show();


        View view = LayoutInflater.from(context).inflate(R.layout.editscene_dialog, null);
        final TextView confirm; //确定按钮
        TextView cancel; //确定按钮
        TextView tv_title;
//        final TextView content; //内容
        cancel = (TextView) view.findViewById(R.id.call_cancel);
        confirm = (TextView) view.findViewById(R.id.call_confirm);
        final ClearEditText edit_password_gateway = (ClearEditText) view.findViewById(R.id.edit_password_gateway);
        edit_password_gateway.setText(name);
        edit_password_gateway.setSelection(edit_password_gateway.getText().length());
//        tv_title = (TextView) view.findViewById(R.id.tv_title);
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

        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (edit_password_gateway.getText().toString() == null ||
                        edit_password_gateway.getText().toString().trim().equals("")) {
                    ToastUtil.showToast(context, "云场景名称为空");
                    return;
                }

                boolean isexist = false;
                String var = edit_password_gateway.getText().toString();
                for (int i = 0; i < list.size(); i++) {
                    if (i == position) {
                        continue;
                    }

                    if (var.equals(list.get(position).get("name").toString())) {//list.get(position).get("number").toString(), list.get(position).get("name").toString()
                        isexist = true;
                    }
                }

                if (isexist) {
                    ToastUtil.showToast(context, "云场景名称已存在");
                    return;
                }

                if (name.equals(var)) {
                    dialog.dismiss();
                } else {
                    linkage_rename(id, edit_password_gateway.getText().toString() == null ? "" :
                                    edit_password_gateway.getText().toString()
                            , dialog);
                }
            }
        });
    }

    //自定义dialog,centerDialog删除对话框
    public void showCenterDeleteDialog(final String linkId, final String name) {
//        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
//        // 布局填充器
//        LayoutInflater inflater = LayoutInflater.from(getActivity());
//        View view = inflater.inflate(R.layout.user_name_dialog, null);
//        // 设置自定义的对话框界面
//        builder.setView(view);
//
//        cus_dialog = builder.create();
//        cus_dialog.show();

        View view = LayoutInflater.from(context).inflate(R.layout.promat_dialog, null);
        TextView confirm; //确定按钮
        TextView cancel; //确定按钮
        TextView tv_title;
        TextView name_gloud;
//        final TextView content; //内容
        cancel = (TextView) view.findViewById(R.id.call_cancel);
        confirm = (TextView) view.findViewById(R.id.call_confirm);
        tv_title = (TextView) view.findViewById(R.id.tv_title);//name_gloud
        name_gloud = (TextView) view.findViewById(R.id.name_gloud);
        name_gloud.setVisibility(View.VISIBLE);
        name_gloud.setText(name);
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

        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                linkage_delete(linkId, dialog);
            }
        });
    }


    /**
     * 删除联动设备
     *
     * @param
     * @param dialog
     */
    private void linkage_delete(final String linkId, final Dialog dialog) {
        Map<String, String> mapdevice = new HashMap<>();
        String areaNumber = (String) SharedPreferencesUtil.getData(context,"areaNumber","");
        mapdevice.put("token", TokenUtil.getToken(context));
        mapdevice.put("areaNumber", areaNumber);
        mapdevice.put("number", linkId);
//        mapdevice.put("boxNumber", TokenUtil.getBoxnumber(LinkageListActivity.this));
        MyOkHttp.postMapString(ApiHelper.sraum_deleteManuallyScene
                , mapdevice, new Mycallback(new AddTogglenInterfacer() {
            @Override
            public void addTogglenInterfacer() {//刷新togglen数据
                linkage_delete(linkId, dialog);
            }
        }, context, dialogUtil) {
            @Override
            public void onError(Call call, Exception e, int id) {
                super.onError(call, e, id);
            }

            @Override
            public void pullDataError() {
                super.pullDataError();
            }

            @Override
            public void emptyResult() {
                super.emptyResult();
            }

            @Override
            public void wrongToken() {
                super.wrongToken();
                //重新去获取togglen,这里是因为没有拉到数据所以需要重新获取togglen

            }

            @Override
            public void wrongBoxnumber() {
                super.wrongBoxnumber();
                ToastUtil.showToast(context,"areaNumber\n" +
                        "不存在");
            }

                    @Override
                    public void threeCode() {
                        super.threeCode();
                        ToastUtil.showToast(context, "number 不存在");
                    }

                    @Override
                    public void fourCode() {
                        ToastUtil.showToast(context,"删除失败");
                    }

                    @Override
            public void onSuccess(final User user) {
                dialog.dismiss();
//                refreshLayout.autoRefresh();
                if (refreshListener != null)
                    refreshListener.refresh();
            }
        });
    }

    /**
     * 联动重命名
     *
     * @param
     * @param dialog
     */
    private void linkage_rename(final String linkId, final String newName, final Dialog dialog) {
        Map<String, String> mapdevice = new HashMap<>();
        String areaNumber = (String) SharedPreferencesUtil.getData(context,"areaNumber","");
        mapdevice.put("token", TokenUtil.getToken(context));
        mapdevice.put("number", linkId);
        mapdevice.put("newName", newName);
        mapdevice.put("areaNumber", areaNumber);
//        mapdevice.put("boxNumber", TokenUtil.getBoxnumber(LinkageListActivity.this));
        MyOkHttp.postMapString(ApiHelper.sraum_reNameManuallyScene, mapdevice, new Mycallback(new AddTogglenInterfacer() {
            @Override
            public void addTogglenInterfacer() {//刷新togglen数据
             linkage_rename(linkId,newName,dialog);
            }
        }, context, dialogUtil) {
            @Override
            public void onError(Call call, Exception e, int id) {
                super.onError(call, e, id);
            }

            @Override
            public void pullDataError() {
                super.pullDataError();
            }

            @Override
            public void emptyResult() {
                super.emptyResult();
            }

            @Override
            public void wrongToken() {
                super.wrongToken();
                //重新去获取togglen,这里是因为没有拉到数据所以需要重新获取togglen
            }

            @Override
            public void fourCode() {
                ToastUtil.showToast(context, "名字已存在");
            }

            @Override
            public void wrongBoxnumber() {
                super.wrongBoxnumber();
                ToastUtil.showToast(context, "areaNumber\n" +
                        "不存在");
            }

            @Override
            public void threeCode() {
                super.threeCode();
                ToastUtil.showToast(context, "number 不存在");
            }

            @Override
            public void onSuccess(final User user) {
                dialog.dismiss();
//                refreshLayout.autoRefresh();
                if (refreshListener != null)
                    refreshListener.refresh();
            }
        });
    }



    /**
     * 去编辑联动
     *
     * @param position
     */
    private void goto_editlink(int position) {

//        map.put("name", us.name);
//        map.put("number", us.number);
        Intent intent = new Intent(context, EditLinkDeviceResultActivity.class);
        Map map = new HashMap();
        map.put("link_edit", true);
        map.put("linkId", list.get(position).get("number").toString());
        map.put("linkName", list.get(position).get("name").toString());
        map.put("type", "101");
        intent.putExtra("link_information", (Serializable) map);
        context.startActivity(intent);
        SharedPreferencesUtil.saveData(context, "link_first", true);
    }


    /**
     * 控制手动场景
     */
    private void sraum_manualSceneControl(final String sceneId) {
        Map map = new HashMap();
        map.put("token", TokenUtil.getToken(context));
        String areaNumber = (String) SharedPreferencesUtil.getData(context, "areaNumber", "");
        map.put("areaNumber", areaNumber);
        map.put("sceneId", sceneId);
        MyOkHttp.postMapObject(ApiHelper.sraum_manualSceneControl, map, new Mycallback(new AddTogglenInterfacer() {
            @Override
            public void addTogglenInterfacer() {
                sraum_manualSceneControl(sceneId);
            }
        }, context, dialogUtil) {
            @Override
            public void fourCode() {
                super.fourCode();
                ToastUtil.showToast(context, "控制失败");
            }

            @Override
            public void threeCode() {
                super.threeCode();
                ToastUtil.showToast(context, "sceneId 错误");
            }


            @Override
            public void onSuccess(User user) {
                super.onSuccess(user);
                ToastUtil.showToast(context, "操作成功");
            }

            @Override
            public void wrongToken() {
                super.wrongToken();
            }
        });
    }

    public void setList_s(List<Map> list, List<Integer> listint, List<Integer> listintwo, boolean b) {
        this.list = new ArrayList<>();
        this.listint = new ArrayList<>();
        this.listintwo = new ArrayList<>();
        this.list = list;
        this.listint = listint;
        this.listintwo = listintwo;
    }

    class ViewHolderContentType {
        public Button delete_btn;
        public Button edit_btn;
        public Button rename_btn;
        ImageView device_type_pic;
        TextView hand_device_content;
        TextView hand_gateway_content;
        ImageView hand_scene_btn;
        LinearLayout swipe_context;
        SwipeMenuLayout swipe_layout;

    }

    public interface RefreshListener {
        void refresh();
    }
}
