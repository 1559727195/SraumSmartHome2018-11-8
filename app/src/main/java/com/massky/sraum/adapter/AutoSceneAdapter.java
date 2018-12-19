package com.massky.sraum.adapter;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.transition.Slide;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.AddTogenInterface.AddTogglenInterfacer;
import com.massky.sraum.R;
import com.massky.sraum.User;
import com.massky.sraum.Util.DialogUtil;
import com.massky.sraum.Util.MyOkHttp;
import com.massky.sraum.Util.Mycallback;
import com.massky.sraum.Util.SharedPreferencesUtil;
import com.massky.sraum.Util.ToastUtil;
import com.massky.sraum.Util.TokenUtil;
import com.massky.sraum.Utils.ApiHelper;
import com.massky.sraum.activity.EditLinkDeviceResultActivity;
import com.massky.sraum.activity.EditSceneSecondActivity;
import com.massky.sraum.activity.SceneSettingActivity;
import com.massky.sraum.view.ClearEditText;
import com.massky.sraum.view.PullToRefreshLayout;
import com.massky.sraum.widget.SlideSwitchButton;
import com.mcxtzhang.swipemenulib.SwipeMenuLayout;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;

/**
 * Created by masskywcy on 2017-05-16.
 */

public class AutoSceneAdapter extends BaseAdapter<User.deviceLinkList> {
    private boolean is_open_to_close;
    private DialogUtil dialogUtil;
    private View view;
    private PullToRefreshLayout refreshLayout;
    private RefreshListener refreshListener;

    public AutoSceneAdapter(Context context, List<User.deviceLinkList> list, DialogUtil dialogUtil, RefreshListener refreshListener) {
        super(context, list);
//        this.list = list;
        this.dialogUtil = dialogUtil;
        this.refreshListener = refreshListener;
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
        viewHolderContentType.hand_device_content.setText((String) list.get(position).name);


//        int element = (Integer) list.get(position).get("image");
//        viewHolderContentType.device_type_pic.setImageResource(element);
        final ViewHolderContentType finalViewHolderContentType = viewHolderContentType;
//
//        //成员，业主accountType->addrelative_id
//        String accountType = (String) SharedPreferencesUtil.getData(context, "accountType", "");
//        switch (accountType) {
//            case "1":
//                viewHolderContentType.swipe_content_linear.setEnabled(true);
//                finalViewHolderContentType.swipemenu_layout.setLeftSwipe(true);
//                break;//业主
//            case "2":
//                viewHolderContentType.swipe_content_linear.setEnabled(false);
//                finalViewHolderContentType.swipemenu_layout.setLeftSwipe(false);
//                break;//家庭成员
//        }

        String isUse = list.get(position).isUse;
        if (isUse != null) {
            switch (isUse) {
                case "1":
                    viewHolderContentType.hand_scene_btn.changeOpenState(true);
                    break;
                case "0":
                    viewHolderContentType.hand_scene_btn.changeOpenState(false);
                    break;
            }
        }

        viewHolderContentType.hand_scene_btn.setSlideSwitchListener(new SlideSwitchButton.SlideSwitch() {
            @Override
            public void slide_switch() {//滑动时，子view滑动时，父view不能滑动
                if (finalViewHolderContentType.hand_scene_btn.isOpen) {//启用和禁止启用，
//                    ToastUtil.showToast(context, "打开了");
                    linkage_setting(list.get(position).id, "1");
                } else {
//                    ToastUtil.showToast(context, "关闭了");
                    linkage_setting(list.get(position).id, "0");
                }
            }
        });


        viewHolderContentType.edit_rel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                ToastUtil.showToast(context, "编辑");
                goto_editlink(position);
                finalViewHolderContentType.swipemenu_layout.quickClose();
            }
        });

        viewHolderContentType.btn_rename.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showRenameDialog(list.get(position).id, list.get(position).name, position);
                finalViewHolderContentType.swipemenu_layout.quickClose();
            }
        });


        viewHolderContentType.delete_rel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                ToastUtil.showToast(context, "删除");
                showCenterDeleteDialog(list.get(position).id, list.get(position).name);
                finalViewHolderContentType.swipemenu_layout.quickClose();
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
                goto_editlink(position);
                finalViewHolderContentType.swipemenu_layout.quickClose();
            }
        });


        ((SwipeMenuLayout) convertView).setOnMenuClickListener(new SwipeMenuLayout.OnMenuClickListener() {

            @Override
            public void onItemClick() {
//                Intent intent = new Intent(context, EditSceneSecondActivity.class);
//                context.startActivity(intent);
//                ToastUtil.showDelToast(context, "被点击");
                goto_editlink(position);
            }

            @Override
            public void onItemClick_By_btn(boolean is_open_to_close1) {
                is_open_to_close = is_open_to_close1;
            }
        });
        return convertView;
    }


    /**
     * 去编辑联动
     *
     * @param position
     */
    private void goto_editlink(int position) {
        Intent intent = new Intent(context, EditLinkDeviceResultActivity.class);
        Map map = new HashMap();
        map.put("link_edit", true);
        map.put("linkId", list.get(position).id);
        map.put("linkName", list.get(position).name);
        map.put("type", "100");//自动场景
//                intent.putExtra("link_edit", true);
//                intent.putExtra("linkId", list.get(position).id);
        intent.putExtra("link_information", (Serializable) map);
        context.startActivity(intent);
        SharedPreferencesUtil.saveData(context, "link_first", true);
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

                    if (var.equals(list.get(position).name)) {//list.get(position).get("number").toString(), list.get(position).get("name").toString()
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
        String areaNumber = (String) SharedPreferencesUtil.getData(context, "areaNumber", "");
        mapdevice.put("token", TokenUtil.getToken(context));
        mapdevice.put("areaNumber", areaNumber);
        mapdevice.put("linkId", linkId);
//        mapdevice.put("boxNumber", TokenUtil.getBoxnumber(LinkageListActivity.this));
        MyOkHttp.postMapString(ApiHelper.sraum_deleteDeviceLink
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
                        ToastUtil.showToast(context, "areaNumber\n" +
                                "不存在");
                    }

                    @Override
                    public void threeCode() {
                        super.threeCode();
                        ToastUtil.showToast(context, "number 不存在");
                    }

                    @Override
                    public void fourCode() {
                        ToastUtil.showToast(context, "删除失败");
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
     * 联动设置启用与否
     *
     * @param isUse
     */
    private void linkage_setting(final String linkId, final String isUse) {
        Map<String, String> mapdevice = new HashMap<>();
        String areaNumber = (String) SharedPreferencesUtil.getData(context, "areaNumber", "");
        mapdevice.put("token", TokenUtil.getToken(context));
        mapdevice.put("areaNumber", areaNumber);
        mapdevice.put("linkId", linkId);
        mapdevice.put("isUse", isUse);
        dialogUtil.loadDialog();
//        mapdevice.put("boxNumber", TokenUtil.getBoxnumber(LinkageListActivity.this));
        MyOkHttp.postMapString(ApiHelper.sraum_setDeviceLinkIsUse
                , mapdevice, new Mycallback(new AddTogglenInterfacer() {
                    @Override
                    public void addTogglenInterfacer() {//刷新togglen数据
                        linkage_setting(linkId, isUse);
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
                    }

                    @Override
                    public void onSuccess(final User user) {
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
        String areaNumber = (String) SharedPreferencesUtil.getData(context, "areaNumber", "");
        mapdevice.put("token", TokenUtil.getToken(context));
        mapdevice.put("linkId", linkId);
        mapdevice.put("newName", newName);
        mapdevice.put("areaNumber", areaNumber);
//        mapdevice.put("boxNumber", TokenUtil.getBoxnumber(LinkageListActivity.this));
        MyOkHttp.postMapString(ApiHelper.sraum_updateDeviceLinkName
                , mapdevice, new Mycallback(new AddTogglenInterfacer() {
                    @Override
                    public void addTogglenInterfacer() {//刷新togglen数据
                        linkage_rename(linkId, newName, dialog);
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

    public interface RefreshListener {
        void refresh();
    }

}
