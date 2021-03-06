package com.massky.sraum.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.AddTogenInterface.AddTogglenInterfacer;
import com.massky.sraum.R;
import com.massky.sraum.User;
import com.massky.sraum.Util.DialogUtil;
import com.massky.sraum.Util.IntentUtil;
import com.massky.sraum.Util.LengthUtil;
import com.massky.sraum.Util.LogUtil;
import com.massky.sraum.Util.MyOkHttp;
import com.massky.sraum.Util.Mycallback;
import com.massky.sraum.Util.SharedPreferencesUtil;
import com.massky.sraum.Util.StringUtils;
import com.massky.sraum.Util.ToastUtil;
import com.massky.sraum.Util.TokenUtil;
import com.massky.sraum.Utils.ApiHelper;
import com.massky.sraum.adapter.AsccociatedpanelAdapter;
import com.massky.sraum.base.BaseActivity;
import com.yanzhenjie.statusview.StatusUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.ButterKnife;
import butterknife.InjectView;
import cn.forward.androids.utils.StatusBarUtil;

/**
 * Created by masskywcy on 2017-03-22.
 */
//关联面板界面
public class AssociatedpanelActivity extends BaseActivity implements AdapterView.OnItemClickListener {
    @InjectView(R.id.backrela_id)
    RelativeLayout backrelaId;
    @InjectView(R.id.titlecen_id)
    TextView titlecenId;
    @InjectView(R.id.panelistview)
    ListView panelistview;
    @InjectView(R.id.paonebtn)
    Button paonebtn;
    @InjectView(R.id.patwobtn)
    Button patwobtn;
    @InjectView(R.id.pathreebtn)
    Button pathreebtn;
    @InjectView(R.id.pafourbtn)
    Button pafourbtn;
    @InjectView(R.id.pafivebtn)
    ImageView pafivebtn;
    @InjectView(R.id.pasixbtn)
    ImageView pasixbtn;
    @InjectView(R.id.pasevenbtn)
    ImageView pasevenbtn;
    @InjectView(R.id.paeightbtn)
    ImageView paeightbtn;
    @InjectView(R.id.panelrela)
    RelativeLayout panelrela;
    @InjectView(R.id.paonerela)
    RelativeLayout paonerela;
    @InjectView(R.id.patworela)
    RelativeLayout patworela;
    @InjectView(R.id.pathreerela)
    RelativeLayout pathreerela;
    @InjectView(R.id.pafourrela)
    RelativeLayout pafourrela;
    @InjectView(R.id.pafiverela)
    RelativeLayout pafiverela;
    @InjectView(R.id.pasixrela)
    RelativeLayout pasixrela;
    @InjectView(R.id.pasevenrela)
    RelativeLayout pasevenrela;
    @InjectView(R.id.paeightrela)
    RelativeLayout paeightrela;
    @InjectView(R.id.backsave)
    RelativeLayout backsave;
    @InjectView(R.id.pafivetext)
    TextView pafivetext;
    @InjectView(R.id.pasixtext)
    TextView pasixtext;
    @InjectView(R.id.paseventext)
    TextView paseventext;
    @InjectView(R.id.paeighttext)
    TextView paeighttext;
    @InjectView(R.id.panelinearone)
    LinearLayout panelinearone;
    @InjectView(R.id.panelineartwo)
    LinearLayout panelineartwo;
    @InjectView(R.id.panelinearthree)
    LinearLayout panelinearthree;
    @InjectView(R.id.panelinearfour)
    LinearLayout panelinearfour;
    @InjectView(R.id.ptlitone)
    RelativeLayout ptlitone;
    @InjectView(R.id.ptlittwo)
    RelativeLayout ptlittwo;
    @InjectView(R.id.ptlitthree)
    RelativeLayout ptlitthree;
    @InjectView(R.id.ptlittwoone)
    RelativeLayout ptlittwoone;
    @InjectView(R.id.ptlittwotwo)
    RelativeLayout ptlittwotwo;
    @InjectView(R.id.ptlitoneone)
    RelativeLayout ptlitoneone;
    @InjectView(R.id.paneThreeLuTiaoGuang)
    LinearLayout paneThreeLuTiaoGuang;
    @InjectView(R.id.paonerela_sanlu)
    RelativeLayout paonerela_sanlu;
    @InjectView(R.id.patworela_sanlu)
    RelativeLayout patwobtn_sanlu;
    @InjectView(R.id.pathreerela_sanlu)
    RelativeLayout pathreebtn_sanlu;
    @InjectView(R.id.pafourrela_sanlu)
    RelativeLayout pafourbtn_sanlu;
    @InjectView(R.id.back)
    ImageView back;

    private DialogUtil dialogUtil, dialogUtilview;
    private String boxNumber, panelNumber = "", type,
            button5Type, button6Type, button7Type,
            button8Type, sceneName, sceType, buttonNumber = "", flagimagefive = "",
            flagimagesix = "", flagimageseven = "", flagimageight = "", panelid, assopanelname = "",
            gatewayid, panelType, btnflag = "";
    private AsccociatedpanelAdapter adapter;
    private List<Boolean> checkList = new ArrayList<>();
    private List<User.panellist> panelList = new ArrayList<>();
    private boolean flagfive = true, flagsix = true,
            flagseven = true, flageight = true;
    private Bundle bundle;
    private TextView dtext_id, belowtext_id;
    private Button qxbutton_id, checkbutton_id;
    private int position;


    @Override
    protected int viewId() {
        return R.layout.asspanel;
    }

    @Override
    protected void onView() {
        StatusUtils.setFullToStatusBar(this);  // StatusBar.
        bundle = IntentUtil.getIntentBundle(AssociatedpanelActivity.this);
        sceneName = bundle.getString("sceneName", "");
        sceType = bundle.getString("sceneType", "");
        gatewayid = bundle.getString("boxNumber", "");
        panelType = bundle.getString("panelType", "");
        panelNumber = bundle.getString("panelNumber", "");
        buttonNumber = bundle.getString("buttonNumber", "");

//        bundle1.putString("sceneName", linkName);
//        bundle1.putString("sceneType", "1");
//        bundle1.putString("boxNumber", boxNumber);
//        bundle1.putString("panelType", "");
//        bundle1.putString("panelNumber", "");
//        bundle1.putString("buttonNumber", "");

        LogUtil.eLength("查看数据panelNumber", panelNumber + "数据问题" + buttonNumber);
//        boxNumber = (String) SharedPreferencesUtil.getData(AssociatedpanelActivity.this, "boxnumber", "");
        dialogUtil = new DialogUtil(AssociatedpanelActivity.this);
        backrelaId.setOnClickListener(this);
        paonerela.setOnClickListener(this);
        patworela.setOnClickListener(this);
        pathreerela.setOnClickListener(this);
        pafourrela.setOnClickListener(this);
        pafiverela.setOnClickListener(this);
        pasixrela.setOnClickListener(this);
        pasevenrela.setOnClickListener(this);
        paeightrela.setOnClickListener(this);
        panelistview.setOnItemClickListener(this);
        backsave.setOnClickListener(this);
        ptlitone.setOnClickListener(this);
        ptlittwo.setOnClickListener(this);
        ptlitthree.setOnClickListener(this);
        ptlittwoone.setOnClickListener(this);
        ptlittwotwo.setOnClickListener(this);
        ptlitoneone.setOnClickListener(this);

        //三路调光
        paonerela_sanlu.setOnClickListener(this);
        patwobtn_sanlu.setOnClickListener(this);
        pathreebtn_sanlu.setOnClickListener(this);
        pafourbtn_sanlu.setOnClickListener(this);
        back.setOnClickListener(this);
        titlecenId.setText("关联面板");
        getData(1);
        replacePanel();
    }

    @Override
    protected void onEvent() {

    }

    @Override
    protected void onData() {

    }

    private void replacePanel() {
        View view = getLayoutInflater().inflate(R.layout.check, null);
        dtext_id = ButterKnife.findById(view, R.id.dtext_id);
        belowtext_id = ButterKnife.findById(view, R.id.belowtext_id);
        qxbutton_id = ButterKnife.findById(view, R.id.qxbutton_id);
        checkbutton_id = ButterKnife.findById(view, R.id.checkbutton_id);
        dtext_id.setText("替换面板");
        qxbutton_id.setOnClickListener(this);
        checkbutton_id.setOnClickListener(this);
        dialogUtilview = new DialogUtil(AssociatedpanelActivity.this, view);
    }

    //设置选中的位置，将其他位置设置为未选
    public void checkPosition(int position) {
        for (int i = 0; i < checkList.size(); i++) {
            if (position == i) {// 设置已选位置
                checkList.set(i, true);
            } else {
                checkList.set(i, false);
            }
        }
        adapter.notifyDataSetChanged();
    }

    private void getData(final int index) {
        dialogUtil.loadDialog();
        Map<String, Object> map = new HashMap<>();
        String areaNumber = (String) SharedPreferencesUtil.getData(AssociatedpanelActivity.this,
                "areaNumber", "");
        map.put("areaNumber", areaNumber);
        map.put("boxNumber", gatewayid);
        map.put("token", TokenUtil.getToken(AssociatedpanelActivity.this));
        MyOkHttp.postMapObject(ApiHelper.sraum_getAllPanel,
                map, new Mycallback(new AddTogglenInterfacer() {
                    @Override
                    public void addTogglenInterfacer() {
                        getData(index);
                    }
                }, AssociatedpanelActivity.this, dialogUtil) {
                    @Override
                    public void onSuccess(User user) {
                        super.onSuccess(user);
                        panelList = user.panelList;
                        checkList.clear();
                        List<User.panellist> panelListnew = new ArrayList<User.panellist>();
                        for (int i = 0; i < panelList.size(); i++) {
                            User.panellist upanel = panelList.get(i);
                            if (upanel.type.trim().equals("A401") || upanel.type.trim().equals("A501") ||
                                    upanel.type.trim().equals("A601") || upanel.type.trim().equals("A701")
                                    || upanel.type.trim().equals("A611")
                                    || upanel.type.trim().equals("A711")
                                    || upanel.type.trim().equals("A511")) {
                                panelListnew.add(upanel);
                            }
                        }
                        panelList.removeAll(panelListnew);
                        boolean flag = false;
                        if(panelList.size() != 0) {
                            User.panellist upone = panelList.get(0);
                            for (int i = 0; i < panelList.size(); i++) {
                                User.panellist up = panelList.get(i);
                                checkList.add(false);
                                if (panelNumber.equals(up.id)) {//说明该面板已经关联了该场景，置顶该面板
                                    flag = true;
                                    panelid = up.id;
                                    panelList.set(0, up);
                                    panelList.set(i, upone);//替换位置
                                    LogUtil.eLength("改变图片", "数据问题");
//                                panelrela.setVisibility(View.VISIBLE);//主布局显示
                                    setPicture(up.type, up.button5Type, pafivebtn, 5);//pafivebtn为下面第五个relativelayout里面的图片
                                    setPicture(up.type, up.button6Type, pasixbtn, 6);
                                    setPicture(up.type, up.button7Type, pasevenbtn, 7);
                                    setPicture(up.type, up.button8Type, paeightbtn, 8);
                                    setLinear(up.type);
                                    pafivetext.setText(LengthUtil.doit_spit_str(up.button5Name == null ? "" :
                                            up.button5Name));
                                    pasixtext.setText(LengthUtil.doit_spit_str(up.button6Name == null ? "" :
                                            up.button6Name));
                                    paseventext.setText(LengthUtil.doit_spit_str(up.button7Name == null ? "" :
                                            up.button7Name));
                                    paeighttext.setText(LengthUtil.doit_spit_str(up.button8Name == null ? "" :
                                            up.button8Name));
                                    setFlag(up.button5Type, up.button6Type, up.button7Type, up.button8Type);
                                    switch (index) {
                                        case 1:
                                            checkList.set(0, true);
                                            break;
                                    }
                                } else {
//                                checkList.add(false);
                                    switch (index) {
                                        case 1:
                                            checkList.set(i, false);
                                            break;
                                    }
                                }
                            }
                        }

                        if (flag) {//该场景关联了面板，实现如果该场景未关联该面板的按钮，则面板框不显示，面板不被选中（待实现）
                            panelrela.setVisibility(View.VISIBLE);//主布局显示
                        } else {
                            panelrela.setVisibility(View.GONE);//主布局显示
                        }

                        adapter = new AsccociatedpanelAdapter(AssociatedpanelActivity.this, panelList, checkList);
                        panelistview.setAdapter(adapter);

//
//                        switch (index) {
//                            case 1:
//                                onitemclick(0);
//                                break;
//                        }
                        switch (index) {
                            case 1:
                                for (int i = 0; i < checkList.size(); i++) {
                                    if (checkList.get(i)) {
                                        onitemclick(0);
                                        break;
                                    }
                                }
                                break;
                        }
                    }

                    @Override
                    public void wrongToken() {
                        super.wrongToken();
                    }
                });
    }

    /**
     * 显示那种布局
     *
     * @param linearType
     */
    private void setLinear(String linearType) {
        clear();
        switch (linearType) {
            case "A201":
                panelinearone.setVisibility(View.VISIBLE);
                LogUtil.eLength("这是进入A201", "看看操作");
                break;
            case "A202":
            case "A411":
            case "A311":
                panelineartwo.setVisibility(View.VISIBLE);
                LogUtil.eLength("这是进入A202", "进入了");
                break;
            case "A203":
            case "A412":
            case "A312":
            case "A321":
                panelinearthree.setVisibility(View.VISIBLE);
                LogUtil.eLength("这是进入A203", "看看操作");
//                paonerela.setVisibility(View.GONE);
                break;
            case "A204":
            case "A313":
            case "A322":
            case "A331":
            case "A413":
            case "A414":
                panelinearfour.setVisibility(View.VISIBLE);
                break;
            case "A303"://三路调光
                flagfive = false;
                flagsix = false;
                flagseven = false;
                flageight = true;
                paneThreeLuTiaoGuang.setVisibility(View.VISIBLE);
                break;
            default:
                panelinearfour.setVisibility(View.VISIBLE);
                break;
        }
    }

    private void setFlag(String fivetype, String sixtype,
                         String sevemtype, String eighttype) {
        if (fivetype == null) {
            flagimagefive = "1";
        } else {
            flagimagefive = "3";
        }
        if (sixtype == null) {
            flagimagesix = "1";
        } else {
            flagimagesix = "3";
        }
        if (sevemtype == null) {
            flagimageseven = "1";
        } else {
            flagimageseven = "3";
        }
        if (eighttype == null) {
            flagimageight = "1";
        } else {
            flagimageight = "3";
        }
        String scenename = LengthUtil.doit_spit_str(sceneName == null ? "" :
                sceneName);
        switch (buttonNumber) {
            case "5":
                pafivetext.setText(scenename);
                flagimagefive = "2";
                break;
            case "6":
                pasixtext.setText(scenename);
                flagimagesix = "2";
                break;
            case "7":////说明该面板7按钮已经关联了该场景，置顶该面板-flagimageseven = "2";
                paseventext.setText(scenename);
                flagimageseven = "2";
                break;
            case "8":
                paeighttext.setText(scenename);
                flagimageight = "2";
                break;
        }
        //
    }

    @Override
    public void onClick(View v) {
        String scenename =
                LengthUtil.doit_spit_str(sceneName == null?"":
        sceneName);
        switch (v.getId()) {
            case R.id.ptlitone:
                ToastUtil.showToast(AssociatedpanelActivity.this, "不可以设置场景");
                break;
            case R.id.ptlittwo:
                ToastUtil.showToast(AssociatedpanelActivity.this, "不可以设置场景");
                break;
            case R.id.ptlitthree:
                ToastUtil.showToast(AssociatedpanelActivity.this, "不可以设置场景");
                break;
            case R.id.ptlittwoone:
                ToastUtil.showToast(AssociatedpanelActivity.this, "不可以设置场景");
                break;
            case R.id.ptlittwotwo:
                ToastUtil.showToast(AssociatedpanelActivity.this, "不可以设置场景");
                break;
            case R.id.ptlitoneone:
                ToastUtil.showToast(AssociatedpanelActivity.this, "不可以设置场景");
                break;
            //三路调光
            case R.id.paonerela_sanlu:
                ToastUtil.showToast(AssociatedpanelActivity.this, "不可以设置场景");
                break;
            case R.id.patworela_sanlu:
                ToastUtil.showToast(AssociatedpanelActivity.this, "不可以设置场景");
                break;
            case R.id.pathreerela_sanlu:
                ToastUtil.showToast(AssociatedpanelActivity.this, "不可以设置场景");
                break;
            case R.id.pafourrela_sanlu:
                ToastUtil.showToast(AssociatedpanelActivity.this, "不可以设置场景");
                break;


            case R.id.qxbutton_id:
                dialogUtilview.removeviewDialog();
                break;
            case R.id.checkbutton_id://当两个场景不一样时，弹出切换场景关联
                dialogUtilview.removeviewDialog();
                switch (btnflag) {
                    case "5":
                        buttonNumber = "5";
                        panelRelation(panelNumber);
                        break;
                    case "6":
                        buttonNumber = "6";
                        panelRelation(panelNumber);
                        break;
                    case "7":
                        buttonNumber = "7";
                        panelRelation(panelNumber);
                        break;
                    case "8":
                        buttonNumber = "8";
                        panelRelation(panelNumber);
                        break;
                }
                break;
            case R.id.backrela_id:
            case R.id.back:
                AssociatedpanelActivity.this.finish();
                break;
            case R.id.paonerela:
                ToastUtil.showToast(AssociatedpanelActivity.this, "不可以设置场景");
                break;
            case R.id.patworela:
                ToastUtil.showToast(AssociatedpanelActivity.this, "不可以设置场景");
                break;
            case R.id.pathreerela:
                ToastUtil.showToast(AssociatedpanelActivity.this, "不可以设置场景");
                break;
            case R.id.pafourrela:
                ToastUtil.showToast(AssociatedpanelActivity.this, "不可以设置场景");
                break;
            case R.id.pafiverela:
                if (flagfive) {//第5个按钮可以设置场景
                    //1代表为空值2非空值代表场景一致3非空值不相等
                    switch (flagimagefive) {
                        case "1":
                            flagimagefive = "2";
                            buttonNumber = "5";
                            panelNumber = panelid;
                            pafivetext.setText(scenename);
                            panelRelation(panelNumber);
                            break;
                        case "2":
                            flagimagefive = "1";
                            buttonNumber = "0";
                            panelRelation("0");
                            break;
                        case "3":
                            if ( StringUtils.replaceBlank(pafivetext.getText().toString()).equals(sceneName)) {
                                flagimagefive = "1";
                                buttonNumber = "0";
                                panelRelation("0");
                            } else {
                                belowtext_id.setText("确定从 " +
                                        StringUtils.replaceBlank(pafivetext.getText().toString()) + " 替换成 " + sceneName + " 吗？");
                                btnflag = "5";
                                dialogUtilview.loadViewdialog();
                            }
                            break;
                        default:
                            break;
                    }

                } else {
                    ToastUtil.showToast(AssociatedpanelActivity.this, "不可以设置场景");
                }
                break;
            case R.id.pasixrela:
                if (flagsix) { //1代表为空值或者非空值不相等2非空值代表场景一致
                    LogUtil.eLength("数据查看", panelNumber);
                    switch (flagimagesix) {
                        case "1":
                            LogUtil.eLength("进入行为", "行为操作");
                            flagimagesix = "2";
                            buttonNumber = "6";
                            panelNumber = panelid;
                            pasixtext.setText(scenename);
                            panelRelation(panelNumber);
                            break;
                        case "2":
                            LogUtil.eLength("取消行为", "取消数据");
                            flagimagesix = "1";
                            buttonNumber = "0";
                            panelRelation("0");
                            break;
                        case "3":
                            if (StringUtils.replaceBlank(pasixtext.getText().toString()).equals(sceneName)) {
                                LogUtil.eLength("取消行为", "取消数据");
                                flagimagesix = "1";
                                buttonNumber = "0";
                                panelRelation("0");
                            } else {
                                belowtext_id.setText("确定从 " +
                                        StringUtils.replaceBlank(pasixtext.getText().toString()) + " 替换成 " + sceneName + " 吗？");
                                btnflag = "6";
                                dialogUtilview.loadViewdialog();
                            }
                            break;
                        default:
                            break;
                    }
                } else {
                    ToastUtil.showToast(AssociatedpanelActivity.this, "不可以设置场景");
                }
                break;
            case R.id.pasevenrela:
                LogUtil.eLength("查看数据", sceType + flagseven + "数据" + flagimageseven);
                if (flagseven) {
                    switch (flagimageseven) {
                        case "1"://该面板上的7按钮没有关联场景
                            LogUtil.eLength("直接选中空白", "第七数据判断");
                            flagimageseven = "2";
                            buttonNumber = "7";
                            panelNumber = panelid;
                            paseventext.setText(scenename);
                            panelRelation(panelNumber);
                            break;
                        case "2"://取消该面板关联的场景
                            LogUtil.eLength("直接取消状态", "取消行为");
                            flagimageseven = "1";
                            buttonNumber = "0";
                            panelRelation("0");
                            break;
                        case "3"://将该面板按钮7关联的场景切换为最近场景
                            if ( StringUtils.replaceBlank(paseventext.getText().toString()).equals(sceneName)) {
                                LogUtil.eLength("直接取消状态", "取消行为");
                                flagimageseven = "1";
                                buttonNumber = "0";
                                panelRelation("0");
                            } else {
                                belowtext_id.setText("确定从 " +
                                        StringUtils.replaceBlank(paseventext.getText().toString()) + " 替换成 " + sceneName + " 吗？");
                                btnflag = "7";
                                dialogUtilview.loadViewdialog();
                            }
                            break;
                        default:
                            break;
                    }
                } else {
                    ToastUtil.showToast(AssociatedpanelActivity.this, "不可以设置场景");
                }
                break;
            case R.id.paeightrela:
                LogUtil.eLength("查看数据", sceType + flageight + "第二次数据" + panelNumber);
                if (flageight) {
                    switch (flagimageight) {
                        case "1":
                            LogUtil.eLength("确定关联数据", "传入");
                            flagimageight = "2";
                            buttonNumber = "8";
                            panelNumber = panelid;
                            paeighttext.setText(scenename);
                            panelRelation(panelNumber);
                            break;
                        case "2":
                            LogUtil.eLength("相等传输数据", "传入");
                            flagimageight = "1";
                            buttonNumber = "0";
                            panelRelation("0");
                            break;
                        case "3":
                            if ( StringUtils.replaceBlank(paeighttext.getText().toString()).equals(sceneName)) {
                                LogUtil.eLength("相等传输数据", "传入");
                                flagimageight = "1";
                                buttonNumber = "0";
                                panelRelation("0");
                            } else {
                                belowtext_id.setText("确定从 " +
                                        StringUtils.replaceBlank(paeighttext.getText().toString())+ " 替换成 " + sceneName + " 吗？");
                                btnflag = "8";
                                dialogUtilview.loadViewdialog();
                            }
                            break;
                        default:
                            break;
                    }
                } else {
                    ToastUtil.showToast(AssociatedpanelActivity.this, "不可以设置场景");
                }
                break;
        }
    }


    private void panelRelation(String panelrenumb) {
        dialogUtil.loadDialog();
        sraum_panelRelation_(panelrenumb);
    }

    private void sraum_panelRelation_(final String panelrenumb) {
        Map<String, Object> map = new HashMap<>();
        map.put("token", TokenUtil.getToken(AssociatedpanelActivity.this));
        String areaNumber = (String) SharedPreferencesUtil.getData(AssociatedpanelActivity.this,
                "areaNumber", "");
        map.put("areaNumber", areaNumber);
        map.put("boxNumber", gatewayid);
        map.put("panelNumber", panelrenumb);
        map.put("buttonNumber", buttonNumber);//关联哪个面板上的哪个按钮
        map.put("sceneName", sceneName);
        MyOkHttp.postMapObject(ApiHelper.sraum_panelRelation, map,
                new Mycallback(new AddTogglenInterfacer() {
                    @Override
                    public void addTogglenInterfacer() {
                        sraum_panelRelation_(panelrenumb);
                    }
                }, AssociatedpanelActivity.this, dialogUtil) {
                    @Override
                    public void onSuccess(User user) {
                        super.onSuccess(user);
                        getData(1);
                    }

                    @Override
                    public void wrongToken() {
                        super.wrongToken();
                    }
                });
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        this.position = position;
        onitemclick(position);
    }

    private void onitemclick(int position) {
        panelNumber = panelList.get(position).id;
        panelid = panelList.get(position).id;
        type = panelList.get(position).type;
        button5Type = panelList.get(position).button5Type;
        button6Type = panelList.get(position).button6Type;
        button7Type = panelList.get(position).button7Type;
        button8Type = panelList.get(position).button8Type;
        String fivename = panelList.get(position).button5Name;
        String sixname = panelList.get(position).button6Name;
        String sevenname = panelList.get(position).button7Name;
        String eightname = panelList.get(position).button8Name;
        LogUtil.eLength("查看name", fivename + "那么" + sixname + "数据" +
                sevenname + "查看" + eightname + "你看呢");
        compareName(fivename, sixname, sevenname, eightname);
        pafivetext.setText((LengthUtil.doit_spit_str(fivename == null? "" :
                fivename)));
        pasixtext.setText((LengthUtil.doit_spit_str(sixname == null? "" :
                sixname)));
        paseventext.setText((LengthUtil.doit_spit_str(sevenname == null? "" :
                sevenname)));
        paeighttext.setText((LengthUtil.doit_spit_str(eightname == null? "" :
                eightname)));
        LogUtil.eLength("点击数据", type + "查看数据" + button5Type + "12" + button6Type + "34" +
                button7Type + "45" + button8Type + "67");
        checkPosition(position);
        panelrela.setVisibility(View.GONE);
        panelrela.setVisibility(View.VISIBLE);
        clear();
        setPicture(type, button5Type, pafivebtn, 5);
        setPicture(type, button6Type, pasixbtn, 6);
        setPicture(type, button7Type, pasevenbtn, 7);
        setPicture(type, button8Type, paeightbtn, 8);
        LogUtil.eLength("查看item", type + "数据" + position);
        switch (type) {
            case "A201":
                panelinearone.setVisibility(View.VISIBLE);
                LogUtil.eLength("这是进入A201", "看看操作");
                break;
            case "A202":
            case "A311":
            case "A411"://1窗帘相当于两个按钮
                panelineartwo.setVisibility(View.VISIBLE);
                LogUtil.eLength("这是进入A202", "进入了");
                break;
            case "A203":
            case "A312":
            case "A321":
            case "A412"://1窗帘相当于两个按钮
                panelinearthree.setVisibility(View.VISIBLE);
                LogUtil.eLength("这是进入A203", "看看操作");
//                paonerela.setVisibility(View.GONE);
                break;
            case "A204":
            case "A313":
            case "A322":
            case "A331":
            case "A413"://1窗帘相当于两个按钮
            case "A414":
                panelinearfour.setVisibility(View.VISIBLE);
                break;
            case "A301":
                panelinearfour.setVisibility(View.VISIBLE);
                flagfive = false;
                flagsix = true;
                flagseven = true;
                flageight = true;
                break;
            case "A302":
                panelinearfour.setVisibility(View.VISIBLE);
                flagfive = false;
                flagsix = false;
                flagseven = true;
                flageight = true;
                break;
            case "A303":
                flagfive = false;
                flagsix = false;
                flagseven = false;
                flageight = true;
                paneThreeLuTiaoGuang.setVisibility(View.VISIBLE);
                break;
            default:
                panelinearfour.setVisibility(View.VISIBLE);
                break;
        }
    }

    private void clear() {
        panelinearone.setVisibility(View.GONE);
        panelineartwo.setVisibility(View.GONE);
        panelinearthree.setVisibility(View.GONE);
        panelinearfour.setVisibility(View.GONE);
        paneThreeLuTiaoGuang.setVisibility(View.GONE);

        pafivebtn.setImageBitmap(null);
        pasixbtn.setImageBitmap(null);
        pasevenbtn.setImageBitmap(null);
        paeightbtn.setImageBitmap(null);
    }

    private void compareName(String fivename, String sixname, String sevenname, String eightname) {
        if (fivename == null) {
            fivename = "";
        }
        if (sixname == null) {
            sixname = "";
        }
        if (sevenname == null) {
            sevenname = "";
        }
        if (eightname == null) {
            eightname = "";
        }

        //1代表为空值或者2非空值代表场景一直3非空值不相等
        if (fivename.equals("")) {
            flagimagefive = "1";
        } else {
            if (fivename.equals(sceneName)) {
                flagimagefive = "2";
            } else {
                flagimagefive = "3";
            }
        }
        if (sixname.equals("")) {
            flagimagesix = "1";
        } else {
            if (sixname.equals(sceneName)) {
                flagimagesix = "2";
            } else {
                flagimagesix = "3";
            }
        }
        if (sevenname.equals("")) {
            flagimageseven = "1";
        } else {
            if (sevenname.equals(sceneName)) {
                flagimageseven = "2";
            } else {
                flagimageseven = "3";
            }
        }
        if (eightname.equals("")) {
            flagimageight = "1";
        } else {
            if (fivename.equals(sceneName)) {
                flagimageight = "2";
            } else {
                flagimageight = "3";
            }
        }
    }

    private void setPicture(String panel_type, String type, ImageView button, int index) {//buttonType是按钮关联的场景类型
        LogUtil.eLength("这是类型数据", type + "查看类型");
        button.setVisibility(View.VISIBLE);
//        if (type != null) {
//            switch (type) {
//                case "1":
//                    button.setImageResource(R.drawable.add_scene_homein);
//                    break;
//                case "2":
//                    button.setImageResource(R.drawable.add_scene_homeout);
//                    break;
//                case "3":
//                    button.setImageResource(R.drawable.add_scene_sleep);
//                    break;
//                case "4":
//                    button.setImageResource(R.drawable.add_scene_nightlamp);
//                    break;
//                case "5":
//                    button.setImageResource(R.drawable.add_scene_getup);
//                    break;
//                case "6":
//                    button.setImageResource(R.drawable.add_scene_cup);
//                    break;
//                case "7":
//                    button.setImageResource(R.drawable.add_scene_book);
//                    break;
//                case "8":
//                    button.setImageResource(R.drawable.add_scene_moive);
//                    break;
//                case "9":
//                    button.setImageResource(R.drawable.add_scene_meeting);
//                    break;
//                case "10":
//                    button.setImageResource(R.drawable.add_scene_cycle);
//                    break;
//                case "11":
//                    button.setImageResource(R.drawable.add_scene_noddle);
//                    break;
//                case "12":
//                    button.setImageResource(R.drawable.add_scene_lampon);
//                    break;
//                case "13":
//                    button.setImageResource(R.drawable.add_scene_lampoff);
//                    break;
//                case "14":
//                    button.setImageResource(R.drawable.defaultpic);
//                    break;
//                default://没有设置场景
//                    switch (panel_type) {
//                        case "A303":
//                            if (index != 8) {
//                                button.setImageResource(R.drawable.light_turn_off);
//                            } else {
//                                button.setVisibility(View.GONE);
//                            }
//                            break;
//                        default:
//                            button.setVisibility(View.GONE);
//                            break;
//                    }
//                    break;
//            }
    }
}
