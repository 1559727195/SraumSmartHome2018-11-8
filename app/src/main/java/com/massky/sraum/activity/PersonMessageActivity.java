package com.massky.sraum.activity;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import androidx.core.content.FileProvider;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.massky.sraum.R;
import com.massky.sraum.Util.DialogUtil;
import com.massky.sraum.base.BaseActivity;
import com.massky.sraum.permissions.RxPermissions;
import com.massky.sraum.tool.Constants;
import com.massky.sraum.widget.SlideSwitchButton;
import com.massky.sraum.widget.TakePhotoPopWin;
import com.yanzhenjie.statusview.StatusUtils;
import com.yanzhenjie.statusview.StatusView;
import org.chenglei.widget.datepicker.DatePicker;
import org.chenglei.widget.datepicker.Sound;
import java.io.File;
import java.util.concurrent.CopyOnWriteArrayList;
import butterknife.InjectView;
import cn.forward.androids.views.BitmapScrollPicker;
import cn.forward.androids.views.ScrollPickerView;
import de.hdodenhof.circleimageview.CircleImageView;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

/**
 * Created by zhu on 2018/1/17.
 */

public class PersonMessageActivity extends BaseActivity{
    @InjectView(R.id.status_view)
    StatusView statusView;
    @InjectView(R.id.back)
    ImageView back;
    @InjectView(R.id.toolbar_txt)
    TextView toolbar_txt;
    private View account_view;
    private Button cancelbtn_id, camera_id, photoalbum;
    private DialogUtil dialogUtil;
    private LinearLayout linear_popcamera;
    @InjectView(R.id.touxiang_select)
    CircleImageView touxiang_select;
    @InjectView(R.id.account_nicheng)
    RelativeLayout account_nicheng;
    @InjectView(R.id.nicheng_txt)
    TextView nicheng_txt;
//    @InjectView(R.id.xingbie_pic)
//    ImageView xingbie_pic;//性别选择图片
    @InjectView(R.id.xingbie_rel)
    RelativeLayout xingbie_rel;
    @InjectView(R.id.account_year)
    RelativeLayout account_year;
    @InjectView(R.id.birthday)
    TextView birthday;
    @InjectView(R.id.account_id_rel)
    RelativeLayout account_id_rel;
    @InjectView(R.id.accountid_txt)
    TextView accountid_txt;
    @InjectView(R.id.change_phone)
    RelativeLayout change_phone;
    @InjectView(R.id.change_phone_txt)
    TextView change_phone_txt;//手机号修改

    //请求相机
    private static final int REQUEST_CAPTURE = 100;
    //请求相册
    private static final int REQUEST_PICK = 101;
    //请求截图
    private static final int REQUEST_CROP_PHOTO = 102;
    //请求访问外部存储
    private static final int READ_EXTERNAL_STORAGE_REQUEST_CODE = 103;
    //请求写入外部存储
    private static final int WRITE_EXTERNAL_STORAGE_REQUEST_CODE = 104;
    //调用照相机返回图片临时文件
    private File tempFile;
    private String nicheng;//昵称
    private BitmapScrollPicker mPicker02;//ScrollBitmapView//竖直滑动图片
    private String account_id;
    private String change_phone_string;

    @InjectView(R.id.slide_btn)
    SlideSwitchButton slide_btn;
    @InjectView(R.id.txt_nan)
    TextView txt_nan;
    @InjectView(R.id.txt_nv)
    TextView txt_nv;


    @Override
    protected int viewId() {
        return R.layout.personmessage_act;
    }

    @Override
    protected void onView() {
//        if (!StatusUtils.setStatusBarDarkFont(this, true)) {// Dark font for StatusBar.
//            statusView.setBackgroundColor(Color.BLACK);
//        }
        StatusUtils.setFullToStatusBar(this);
        init_permissions();
        addViewid();
        createCameraTempFile(savedInstanceState);
    }

    @Override
    protected void onEvent() {
        back.setOnClickListener(this);
        touxiang_select.setOnClickListener(this);
        camera_id.setOnClickListener(this);
        photoalbum.setOnClickListener(this);
        cancelbtn_id.setOnClickListener(this);
        account_nicheng.setOnClickListener(this);
        xingbie_rel.setOnClickListener(this);
        account_year.setOnClickListener(this);
        account_id_rel.setOnClickListener(this);
        change_phone.setOnClickListener(this);

        slide_btn.setSlideListener(new MySlideSwitchButtonListener());
    }

    @Override
    protected void onData() {
        toolbar_txt.setText("个人资料");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back:
                PersonMessageActivity.this.finish();
                break;
                case R.id.touxiang_select://头像选择
                dialogUtil.loadViewBottomdialog();
                    break;
            case R.id.camera_id:
                //跳转到调用系统相机
                dialogUtil.removeviewBottomDialog();
                gotoCarema();
                break;

            case R.id.photoalbum:
                //跳转到调用系统图库
                dialogUtil.removeviewBottomDialog();
                gotoPhoto();
                break;
            case R.id.cancelbtn_id://取消打开照相
                dialogUtil.removeviewBottomDialog();
                break;
            case R.id.account_nicheng:
                Intent intent_nicheng = new Intent(PersonMessageActivity.this,
                        AccountNiChengActivity.class);
                intent_nicheng.putExtra("nicheng_txt",nicheng_txt.getText().toString());
                startActivityForResult(intent_nicheng, Constants.MyNICheng);
                break;
            case R.id.xingbie_rel://性别图片选择
//                showPopFormBottom();
                break;
            case R.id.account_year://年月日选择
                showPopFormBottom_select_datapicker();
                break;
            case R.id.account_id_rel:
                Intent intent_account_id
                        = new Intent(PersonMessageActivity.this,
                        AccountIdActivity.class);
                intent_account_id.putExtra("account_id",accountid_txt.getText().toString());
                startActivityForResult(intent_account_id, Constants.MyAccountId);
                break;
            case R.id.change_phone://修改已经绑定的手机号
                Intent intent_change
                        = new Intent(PersonMessageActivity.this,
                       ChangePhoneNumberActivity.class);
                intent_change.putExtra("account_change_phone",change_phone_txt.getText().toString());
                startActivityForResult(intent_change, Constants.MyChangePhoneNumber);
                break;
        }
    }

    private  class MySlideSwitchButtonListener  implements SlideSwitchButton.SlideListener {

        @Override
        public void openState(boolean isOpen, View view) {
            if (isOpen) {
                txt_nan.setVisibility(View.VISIBLE);
//                gender = "男";
                txt_nv.setVisibility(View.GONE);
            } else {
                txt_nan.setVisibility(View.GONE);
                txt_nv.setVisibility(View.VISIBLE);
//                gender = "女";
            }
//            upgrate_single_information();
        }
    }


    /**
     * 跳转到照相机
     */
    private void gotoCarema() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        Uri photoURI = FileProvider.getUriForFile(PersonMessageActivity.this,
                getApplicationContext().getPackageName() + ".provider",
                tempFile);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
        startActivityForResult(intent, REQUEST_CAPTURE);
    }


    /**
     * 底部弹出拍照，相册弹出框
     */
    private void addViewid() {
        account_view = LayoutInflater.from(PersonMessageActivity.this).inflate(R.layout.camera, null);
        linear_popcamera = (LinearLayout) account_view.findViewById(R.id.linear_popcamera);
        cancelbtn_id = (Button) account_view.findViewById(R.id.cancelbtn_id);
        photoalbum = (Button) account_view.findViewById(R.id.photoalbum);
        camera_id = (Button) account_view.findViewById(R.id.camera_id);
        dialogUtil = new DialogUtil(PersonMessageActivity.this, account_view, 1);
    }

    /**
     * 跳转到相册
     */
    private void gotoPhoto() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(Intent.createChooser(intent, "请选择图片"), REQUEST_PICK);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        switch (requestCode) {
            case REQUEST_CAPTURE: //调用系统相机返回
                if (resultCode == RESULT_OK) {
                    gotoClipActivity(Uri.fromFile(tempFile));
//                    final Uri uri = Uri.fromFile(tempFile);
//                    final Uri uri = intent.getData();
//                    if (uri == null) {
//                        return;
//                    }
//                    }
//                    String cropImagePath = getRealFilePathFromUri(getApplicationContext(), uri);
//                    //快速压缩
//
//                    int x =  dip2px(PhotoSelectActivity.this,80);
//                    int y =  dip2px(PhotoSelectActivity.this,80);
//                    stringBase64 = bitmapToString(cropImagePath,x,y);
////                    String stringBase64 =imageToBase64(finalPath);
////                    shangchuanAvatar(stringBase64);
////                    bitMap = bm;
//                    img_rel.setVisibility(View.VISIBLE);
//                    img_select.setVisibility(View.GONE);
////                    img_show.setImageBitmap(bitMap);
//                    RequestOptions options = new RequestOptions()
//                            .centerCrop()
//                            .placeholder(R.color.color_f6)
//                            .diskCacheStrategy(DiskCacheStrategy.ALL);
//                    Glide.with(this)
//                            .load(cropImagePath)
//                            .apply(options)
//                            .into(img_show);
//                }
                }
                break;
            case REQUEST_PICK:  //调用系统相册返回
                if (resultCode == RESULT_OK) {
                    Uri uri = intent.getData();
                    gotoClipActivity(uri);

//                    final Uri uri = intent.getData();
//                    if (uri == null) {
//                        return;
//                    }
//                    String cropImagePath = getRealFilePathFromUri(getApplicationContext(), uri);
                    //快速压缩

//                    int x =  dip2px(PhotoSelectActivity.this,80);
//                    int y =  dip2px(PhotoSelectActivity.this,80);
//                    stringBase64 = bitmapToString(cropImagePath,x,y);
////                    String stringBase64 =imageToBase64(finalPath);
////                    shangchuanAvatar(stringBase64);
////                    bitMap = bm;
//                    img_rel.setVisibility(View.VISIBLE);
//                    img_select.setVisibility(View.GONE);
////                    img_show.setImageBitmap(bitMap);
//                    RequestOptions options = new RequestOptions()
//                            .centerCrop()
//                            .placeholder(R.color.color_f6)
//                            .diskCacheStrategy(DiskCacheStrategy.ALL);
//                    Glide.with(this)
//                            .load(cropImagePath)
//                            .apply(options)
//                            .into(img_show);
                }
                break;
            case REQUEST_CROP_PHOTO:  //剪切图片返回
                if (resultCode == RESULT_OK) {
                    final Uri uri = intent.getData();
                    if (uri == null) {
                        return;
                    }
                    String cropImagePath = getRealFilePathFromUri(getApplicationContext(), uri);
//                    bitMap = BitmapFactory.decodeFile(cropImagePath);
//                    img_rel.setVisibility(View.VISIBLE);
//                    img_select.setVisibility(View.GONE);
//                    img_show.setImageBitmap(bitMap);
                    RequestOptions options = new RequestOptions()
                            .centerCrop()
                            .placeholder(R.color.color_f6)
                            .diskCacheStrategy(DiskCacheStrategy.ALL);
                    Glide.with(this)
                            .load(cropImagePath)
                            .apply(options)
                            .into(touxiang_select);
                }
                break;
            case Constants.MyNICheng:
                if (intent != null) {
                    nicheng = intent.getStringExtra("nicheng");// http://weixin.qq.com/r/bWTZwbXEsOjPrfGi9zF-
                    nicheng_txt.setText(nicheng);
                }
                break;
            case Constants.MyAccountId:
                if (intent != null) {
                    account_id = intent.getStringExtra("account_id");// http://weixin.qq.com/r/bWTZwbXEsOjPrfGi9zF-
                    accountid_txt.setText(account_id);
                }
                break;
            case Constants.MyChangePhoneNumber:
                if (intent != null) {
                    change_phone_string = intent.getStringExtra("account_change_phone");// http://weixin.qq.com/r/bWTZwbXEsOjPrfGi9zF-
                    change_phone_txt.setText(change_phone_string);
                }
                break;
        }
    }

    // 1: qq, 2: weixin
    private int type = 1;
    /**
     * 打开截图界面
     *
     * @param uri
     */
    public void gotoClipActivity(Uri uri) {
        if (uri == null) {
            return;
        }
        Intent intent = new Intent();
        intent.setClass(this, ClipImageActivity.class);
        intent.putExtra("type", type);
        intent.setData(uri);
        startActivityForResult(intent, REQUEST_CROP_PHOTO);
    }

    /**
     * 根据Uri返回文件绝对路径
     * 兼容了file:///开头的 和 content://开头的情况
     *
     * @param context
     * @param uri
     * @return the file path or null
     */
    public static String getRealFilePathFromUri(final Context context, final Uri uri) {
        if (null == uri) return null;
        final String scheme = uri.getScheme();
        String data = null;
        if (scheme == null)
            data = uri.getPath();
        else if (ContentResolver.SCHEME_FILE.equals(scheme)) {
            data = uri.getPath();
        } else if (ContentResolver.SCHEME_CONTENT.equals(scheme)) {
            Cursor cursor = context.getContentResolver().query(uri, new String[]{MediaStore.Images.ImageColumns.DATA}, null, null, null);
            if (null != cursor) {
                if (cursor.moveToFirst()) {
                    int index = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
                    if (index > -1) {
                        data = cursor.getString(index);
                    }
                }
                cursor.close();
            }
        }
        return data;
    }

    /**
     * 创建调用系统照相机待存储的临时文件
     *
     * @param savedInstanceState
     */
    private void createCameraTempFile(Bundle savedInstanceState) {
        if (savedInstanceState != null && savedInstanceState.containsKey("tempFile")) {
            tempFile = (File) savedInstanceState.getSerializable("tempFile");
        } else {
            tempFile = new File(checkDirPath(Environment.getExternalStorageDirectory().getPath() + "/image/"),
                    System.currentTimeMillis() + ".jpg");
        }
    }

    /**
     * 检查文件是否存在
     */
    private static String checkDirPath(String dirPath) {
        if (TextUtils.isEmpty(dirPath)) {
            return "";
        }
        File dir = new File(dirPath);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        return dirPath;
    }
    private String stringBase64;



    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable("tempFile", tempFile);
    }

    private void init_permissions() {

        // 清空图片缓存，包括裁剪、压缩后的图片 注意:必须要在上传完成后调用 必须要获取权限
        RxPermissions permissions = new RxPermissions(this);
        permissions.request(Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_EXTERNAL_STORAGE
                ,Manifest.permission.CAMERA
                ,Manifest.permission.WRITE_SETTINGS).subscribe(new Observer<Boolean>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(Boolean aBoolean) {
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        });
    }

    /**
     * 从底部弹出的popwindow
     * @param
     */
    public void showPopFormBottom() {
        final CopyOnWriteArrayList<Bitmap> bitmaps = new CopyOnWriteArrayList<Bitmap>();
        bitmaps.add(BitmapFactory.decodeResource(getResources(), R.drawable.slot_01));
        bitmaps.add(BitmapFactory.decodeResource(getResources(), R.drawable.slot_02));
        View view = LayoutInflater.from(this).inflate(R.layout.xingbie_select, null);
        mPicker02 = (BitmapScrollPicker) view.findViewById(R.id.picker_02);
        mPicker02.setData(bitmaps);
        mPicker02.setOnSelectedListener(new ScrollPickerView.OnSelectedListener() {
            @Override
            public void onSelected(ScrollPickerView scrollPickerView, int position) {

            }
        });
        TextView  btn_cancel = (TextView) view.findViewById(R.id.btn_cancel);//btn_onfirm
        TextView  btn_onfirm = (TextView) view.findViewById(R.id.btn_onfirm);//btn_onfirm

//        mPicker02.setSelectedPosition(0);//使ScrollPickerView<T>某一个处于选择位置,默认选择一个

        final TakePhotoPopWin takePhotoPopWin = new TakePhotoPopWin(this, new TakePhotoPopWin.PopWindowListener() {
            @Override
            public void confirm(String time) { //确定选择具体日期，在某天的具体日期

            }
        },view);
        // 取消按钮
        btn_cancel.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                // 销毁弹出框
                    takePhotoPopWin.dismiss();
            }
        });

        btn_onfirm.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                // 销毁弹出框
                takePhotoPopWin.dismiss();
            }
        });


//        设置Popupwindow显示位置（从底部弹出）
        takePhotoPopWin.showAtLocation(findViewById(R.id.root_layout), Gravity.BOTTOM|Gravity.CENTER_HORIZONTAL, 0, 0);
        final WindowManager.LayoutParams[] params = {getWindow().getAttributes()};
        //当弹出Popupwindow时，背景变半透明
        params[0].alpha=0.7f;
        getWindow().setAttributes(params[0]);
        //设置Popupwindow关闭监听，当Popupwindow关闭，背景恢复1f
        takePhotoPopWin.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                params[0] = getWindow().getAttributes();
                params[0].alpha=1f;
                getWindow().setAttributes(params[0]);
            }
        });
    }



    /**
     * 从底部弹出的popwindow
     * @param
     */
    public void showPopFormBottom_select_datapicker() {
        final CopyOnWriteArrayList<Bitmap> bitmaps = new CopyOnWriteArrayList<Bitmap>();
        bitmaps.add(BitmapFactory.decodeResource(getResources(), R.drawable.slot_01));
        bitmaps.add(BitmapFactory.decodeResource(getResources(), R.drawable.slot_02));
        View view = LayoutInflater.from(this).inflate(R.layout.age_select, null);
//        mPicker02 = (BitmapScrollPicker) view.findViewById(R.id.picker_02);
//        mPicker02.setData(bitmaps);
//        mPicker02.setOnSelectedListener(new ScrollPickerView.OnSelectedListener() {
//            @Override
//            public void onSelected(ScrollPickerView scrollPickerView, int position) {
//
//            }
//        });

        DatePicker mDatePicker1 = (DatePicker) view.findViewById(R.id.date_picker1);
        Sound sound1 = new Sound(this);
        mDatePicker1.setSoundEffect(sound1)
                .setTextColor(Color.BLACK)
                .setFlagTextColor(Color.GRAY)
                .setTextSize(40)
                .setFlagTextSize(25)
                .setSoundEffectsEnabled(true);
        mDatePicker1.setOnDateChangedListener(new DatePicker.OnDateChangedListener() {
            @Override
            public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                String  birthday1 = year+"年"+monthOfYear+"月"+dayOfMonth+"日";
//                date = year + "-" + monthOfYear + "-" + dayOfMonth;
                birthday.setText(birthday1);
            }
        });

        TextView  btn_cancel = (TextView) view.findViewById(R.id.btn_cancel);//btn_onfirm
        TextView  btn_onfirm = (TextView) view.findViewById(R.id.btn_onfirm);//btn_onfirm

//        mPicker02.setSelectedPosition(0);//使ScrollPickerView<T>某一个处于选择位置,默认选择一个

        final TakePhotoPopWin takePhotoPopWin = new TakePhotoPopWin(this, new TakePhotoPopWin.PopWindowListener() {
            @Override
            public void confirm(String time) { //确定选择具体日期，在某天的具体日期

            }
        },view);
        // 取消按钮
        btn_cancel.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                // 销毁弹出框
                takePhotoPopWin.dismiss();
            }
        });

        btn_onfirm.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                // 销毁弹出框
                takePhotoPopWin.dismiss();
            }
        });


//        设置Popupwindow显示位置（从底部弹出）
        takePhotoPopWin.showAtLocation(findViewById(R.id.root_layout), Gravity.BOTTOM|Gravity.CENTER_HORIZONTAL, 0, 0);
        final WindowManager.LayoutParams[] params = {getWindow().getAttributes()};
        //当弹出Popupwindow时，背景变半透明
        params[0].alpha=0.7f;
        getWindow().setAttributes(params[0]);
        //设置Popupwindow关闭监听，当Popupwindow关闭，背景恢复1f
        takePhotoPopWin.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                params[0] = getWindow().getAttributes();
                params[0].alpha=1f;
                getWindow().setAttributes(params[0]);
            }
        });
    }
}
