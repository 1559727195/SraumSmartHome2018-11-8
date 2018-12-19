package com.massky.sraum.myzxingbar.qrcodescanlib;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.hardware.Camera;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.Result;
import com.massky.sraum.R;
import com.massky.sraum.activity.EditGateWayActivity;
import com.massky.sraum.myzxingbar.zxing.camera.CameraManager;
import com.massky.sraum.myzxingbar.zxing.decoding.CaptureActivityHandler;
import com.massky.sraum.myzxingbar.zxing.decoding.InactivityTimer;
import com.massky.sraum.myzxingbar.zxing.view.ViewfinderView;
import com.massky.sraum.permissions.RxPermissions;
import com.yanzhenjie.statusview.StatusUtils;
import com.yanzhenjie.statusview.StatusView;

import java.io.IOException;
import java.util.Vector;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

public class CaptureActivity extends AppCompatActivity implements Callback, OnClickListener {

    private CaptureActivityHandler handler;
    private ViewfinderView viewfinderView;
    private boolean hasSurface;
    private Vector<BarcodeFormat> decodeFormats;
    private String characterSet;
    private InactivityTimer inactivityTimer;
    private MediaPlayer mediaPlayer;
    private boolean playBeep;
    private static final float BEEP_VOLUME = 0.10f;
    private boolean vibrate;
    private ImageView _ivBack;
    private static final String DECODED_CONTENT_KEY = "codedContent";//扫码的内容
    private Toolbar toolbars;//toolbar的返回键
    private boolean IsHaveFlash;
    private ImageView open_light;
    private StatusView statusView;
    private ImageView back;
    private ImageView edit_gateway;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_capture_new);
        initPermission();
        CameraManager.init(getApplication());
        initView();
        initEvent();
    }

    private void init_splash_light() {
        camera = Camera.open();
        parameters = camera.getParameters();
    }

    private void initPermission() {
        // 清空图片缓存，包括裁剪、压缩后的图片 注意:必须要在上传完成后调用 必须要获取权限
        RxPermissions permissions = new RxPermissions(this);
        permissions.request(Manifest.permission.CAMERA).subscribe(new Observer<Boolean>() {
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

    private void initView() {
//		toolbars = (Toolbar) findViewById(R.id.toolbar_scanel);
//		setSupportActionBar(toolbars);
//		getSupportActionBar().setDisplayShowHomeEnabled(true);//toolbar返回键设置
        viewfinderView = (ViewfinderView) findViewById(R.id.viewfinder_view);
        hasSurface = false;
        inactivityTimer = new InactivityTimer(this);

//		@InjectView(R.id.status_view)
//		StatusView statusView;
        statusView = (StatusView) findViewById(R.id.status_view);//
        StatusUtils.setFullToStatusBar(this);  // StatusBar.
        setAnyBarAlpha(0);//设置状态栏的颜色为透明
        back = (ImageView) findViewById(R.id.back);
        edit_gateway = (ImageView) findViewById(R.id.edit_gateway);//手动编辑网关
    }


    //注册二维码返回
    private void initEvent() {
//		toolbars.setNavigationOnClickListener(new OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				onBackPressed();
//			}
//		});
        back.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        edit_gateway.setOnClickListener(this);
    }

    @SuppressWarnings("deprecation")
    @Override
    protected void onResume() {
        super.onResume();
        SurfaceView surfaceView = (SurfaceView) findViewById(R.id.preview_view);
        SurfaceHolder surfaceHolder = surfaceView.getHolder();
        if (hasSurface) {
            initCamera(surfaceHolder);
        } else {
            surfaceHolder.addCallback(this);
            surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        }
        decodeFormats = null;
        characterSet = null;

        playBeep = true;
        AudioManager audioService = (AudioManager) getSystemService(AUDIO_SERVICE);
        if (audioService.getRingerMode() != AudioManager.RINGER_MODE_NORMAL) {
            playBeep = false;
        }
        initBeepSound();
        vibrate = true;
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (handler != null) {
            handler.quitSynchronously();
            handler = null;
        }
        CameraManager.get().closeDriver();
    }

    @Override
    protected void onDestroy() {
        inactivityTimer.shutdown();
        super.onDestroy();
    }

    private void initCamera(SurfaceHolder surfaceHolder) {
        try {
            CameraManager.get().openDriver(surfaceHolder);
        } catch (IOException ioe) {
            return;
        } catch (RuntimeException e) {
            return;
        }

        if (handler == null) {
            handler = new CaptureActivityHandler(this, decodeFormats,
                    characterSet);
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width,
                               int height) {
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        if (!hasSurface) {
            hasSurface = true;
            initCamera(holder);
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        hasSurface = false;
    }

    public ViewfinderView getViewfinderView() {
        return viewfinderView;
    }

    public Handler getHandler() {
        return handler;
    }

    public void drawViewfinder() {
        viewfinderView.drawViewfinder();

    }

    public void handleDecode(Result obj, Bitmap barcode) {
        inactivityTimer.onActivity();
        String code = obj.getText();
        if (code == "") {
            Toast.makeText(CaptureActivity.this, "Scan failed!", Toast.LENGTH_SHORT).show();
        } else {
            Log.d("SCAN", code);
            Intent intent = new Intent();
            Bundle bundle = new Bundle();
            bundle.putString("result", code);
            intent.putExtras(bundle);
            this.setResult(RESULT_OK, intent);
        }
        CaptureActivity.this.finish();
    }

    private void initBeepSound() {
        if (playBeep && mediaPlayer == null) {
            // The volume on STREAM_SYSTEM is not adjustable, and users found it
            // too loud,
            // so we now play on the music stream.
            setVolumeControlStream(AudioManager.STREAM_MUSIC);
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaPlayer.setOnCompletionListener(beepListener);

            AssetFileDescriptor file = getResources().openRawResourceFd(
                    R.raw.beep);
            try {
                mediaPlayer.setDataSource(file.getFileDescriptor(),
                        file.getStartOffset(), file.getLength());
                file.close();
                mediaPlayer.setVolume(BEEP_VOLUME, BEEP_VOLUME);
                mediaPlayer.prepare();
            } catch (IOException e) {
                mediaPlayer = null;
            }
        }
    }

    private static final long VIBRATE_DURATION = 200L;

    private void playBeepSoundAndVibrate() {
        if (playBeep && mediaPlayer != null) {
            mediaPlayer.start();
        }
        if (vibrate) {
            Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
            vibrator.vibrate(VIBRATE_DURATION);
        }
    }

    /**
     * When the beep has finished playing, rewind to queue up another one.
     */
    private final OnCompletionListener beepListener = new OnCompletionListener() {
        public void onCompletion(MediaPlayer mediaPlayer) {
            mediaPlayer.seekTo(0);
        }
    };


    public Camera camera = null;
    public Camera.Parameters parameters = null;

    public boolean IsHaveFlash()//判断设备是否有闪光灯
    {
        return !getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH);
    }

    //	public ControlFlash() {//初始化
//		// TODO 自动生成的构造函数存根
//		camera= Camera.open();
//		parameters=camera.getParameters();
//	}
    public void open() {//打开闪光灯
        if (IsHaveFlash) {
            //设备不支持闪光灯
            return;
        }
        parameters.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);//设置闪光灯为手电筒模式
        camera.setParameters(parameters);
        camera.startPreview();
    }

    public void close()//关闭闪光灯
    {
        parameters.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
        camera.setParameters(parameters);
    }

    /*
*动态设置状态栏的颜色
*/
    private void setAnyBarAlpha(int alpha) {
//        mToolbar.getBackground().mutate().setAlpha(alpha);
        statusView.getBackground().mutate().setAlpha(alpha);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.edit_gateway://手动编辑网关信息
                startActivity(new Intent(CaptureActivity.this, EditGateWayActivity.class));
                CaptureActivity.this.finish();
                break;
        }
    }
}