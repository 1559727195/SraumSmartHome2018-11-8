package com.massky.sraum.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;

import com.massky.sraum.R;

/**
 * Created by zhu on 2018/1/2.
 */

public class SycleSearchView extends View implements Runnable{
    private int width,height;
    private int srcId;
    private Bitmap src=null;
    private Context context;
    private int left=0,top=0;
    private int step=4; //5
    private boolean positive = true;
    private Thread thread;
    private boolean canStart=true;
    private int iw,ih;//图片的宽高

    public SycleSearchView(Context context, AttributeSet attrs) {
        super(context, attrs);
        // TODO Auto-generated constructor stub
        this.context=context;
        initView();
    }

    private void initView() {
        src= BitmapFactory.decodeResource(context.getResources(), R.drawable.img_fangdajing);
        iw=src.getWidth();
        ih=src.getHeight();
        width = getScreenWidth() / 3;
        height = getScreenWidth() / 3;
        left = getScreenWidth() / 3 / 2;
    }


    public SycleSearchView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        // TODO Auto-generated constructor stub
        this.context=context;
    }


//    private void init(){
//        left=0;
//        top=height/2;
//    }

    /**
     *
     * 方法: initXY
     * 描述: TODO
     * 参数: @param x
     * 参数: @param positive true 表示向右运动
     * 返回: void
     * 异常
     */
    private void initXY(int x){
        left=x;
        top=height/2-(int) Math.sqrt(height*height/4-(width/2-x)*(width/2-x));
        if(!positive)
        {
            top=height-top;
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        // TODO Auto-generated method stub
        super.onDraw(canvas);
        if(src==null){
            return;
        }
        canvas.drawBitmap(src, left, top, null);//Bitmap bitmap, float left, float top
    }


    public void startsycle(){
        canStart=true;
        Thread localThread = new Thread(this);
        this.thread = localThread;
        this.thread.start();
    }

    public void stopsycle(){
        canStart=false;
        if(this.thread!=null){
            this.thread.interrupt();
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {//画布宽高
        // 获取父 View 的测量模式
        int mode = MeasureSpec.getMode(widthMeasureSpec);
        int width = 0;
        int heigh = 0;
        switch (mode) {
            case MeasureSpec.AT_MOST:
                Log.e("robin debug", "MeasureSpec.AT_MOST");
                width = getScreenWidth() / 2;
                heigh = getScreenWidth() / 2;
                break;//wrap_content
            case MeasureSpec.EXACTLY:

                break;//MatchParent
            case MeasureSpec.UNSPECIFIED:

                break;
        }
        setMeasuredDimension(width, heigh);
    }

    /**
     * 得到屏幕宽度
     * @return
     */
    private int getScreenWidth() {
        WindowManager windowManager = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        windowManager.getDefaultDisplay().getMetrics(displayMetrics);
        return displayMetrics.widthPixels;
    }

    @Override
    public void run() {
        // TODO Auto-generated method stub
        while(canStart){
            if(positive){
                if(left+step<width){
                    initXY(left+step);
                }else{
                    positive=!positive;
                    initXY(left-step);
                }
            } else {
                if(left-step>0){
                    initXY(left-step);
                } else {
                    positive=!positive;
                    initXY(left+step);
                }
            }

            postInvalidate();

            try {
                Thread.sleep(20);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
