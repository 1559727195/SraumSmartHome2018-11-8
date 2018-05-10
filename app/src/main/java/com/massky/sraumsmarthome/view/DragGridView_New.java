package com.massky.sraumsmarthome.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.os.Handler;
import android.os.Vibrator;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import com.massky.sraumsmarthome.R;
import com.massky.sraumsmarthome.listener.OnRefreshListener;

import java.lang.reflect.Field;

/**
 * Created by zhu on 2017/12/11.
 */

public class DragGridView_New extends HeaderGridView implements AbsListView.OnScrollListener{
    /**
     * DragGridView的item长按响应的时间， 默认是1000毫秒，也可以自行设置
     */
    private long dragResponseMS = 500;

    /**
     * 是否可以拖拽，默认不可以
     */
    private boolean isDrag = false;

    private int mDownX;
    private int mDownY;
    private int moveX;
    private int moveY;
    /**
     * 正在拖拽的position
     */
    private int mDragPosition;

    /**
     * 刚开始拖拽的item对应的View
     */
    private View mStartDragItemView = null;

    /**
     * 用于拖拽的镜像，这里直接用一个ImageView
     */
    private ImageView mDragImageView;

    /**
     * 震动器
     */
    private Vibrator mVibrator;

    private WindowManager mWindowManager;
    /**
     * item镜像的布局参数
     */
    private WindowManager.LayoutParams mWindowLayoutParams;

    /**
     * 我们拖拽的item对应的Bitmap
     */
    private Bitmap mDragBitmap;

    /**
     * 按下的点到所在item的上边缘的距离
     */
    private int mPoint2ItemTop ;

    /**
     * 按下的点到所在item的左边缘的距离
     */
    private int mPoint2ItemLeft;

    /**
     * DragGridView距离屏幕顶部的偏移量
     */
    private int mOffset2Top;

    /**
     * DragGridView距离屏幕左边的偏移量
     */
    private int mOffset2Left;

    /**
     * 状态栏的高度
     */
    private int mStatusHeight;

    /**
     * DragGridView自动向下滚动的边界值
     */
    private int mDownScrollBorder;

    /**
     * DragGridView自动向上滚动的边界值
     */
    private int mUpScrollBorder;

    /**
     * DragGridView自动滚动的速度
     */
    private static final int speed = 80;

    /**
     * item发生变化回调的接口
     */
    private OnChanageListener onChanageListener;

    /**
     *
     * @param listview的下拉刷新
     */

    //头布局视图
    private View header;
    //头布局高度
    private int headerViewHeight;
    //按下时y的偏移量
    private int downY;
    //移动时y的偏移量
    private int moveY_listview;
    //距离顶部的距离
    private int paddingTop;
    //listview第一个可见的item项
    private int firstVisibleItemPosition;
    //下拉刷新
    private final int PULL_DOWN_REFRESH = 0;
    //释放刷新
    private  final int RELEASE_REFRESH = 1;
    //正在刷新
    private final int REFRESHING = 2;
    //当前状态，默认为下拉刷新
    private int currentState = PULL_DOWN_REFRESH;
    //刷新列表构造函数
    private Context context;
    //刷新监听
    private OnRefreshListener mOnRefreshListener;
    private ImageView refresh;
    private RotateAnimation animation;
    //图片向上旋转
    private Animation upAnimation;
    //图片向下旋转
    private Animation downAnimation;
    //箭头
    private ImageView row;
    //提示文本
    private TextView header_tv;

    /**
     *
     * @param listview的下拉刷新
     */



    /**
     *
     * @param context
     */



    public DragGridView_New(Context context) {
        this(context, null);
    }

    public DragGridView_New(Context context, AttributeSet attrs) {

        this(context, attrs, 0);
        //初始化头布局
        initHeaderView();
        //滑动监听
        this.setOnScrollListener(this);
        this.context = context;
    }


    /**滚动状态改变时调用，一般用于列表视图和网格视图
     *
     * @param view
     * @param scrollState 有三种值，分别是SCROLL_STATE_IDLE,SCROLL_STATE_TOUCH_SCROLL,SCROLL_STATE_FLING
     *                    SCROLL_STATE_IDLE:当屏幕停止滚动时
     *                    SCROLL_STATE_TOUCH_SCROLL:当屏幕以触屏方式滚动并且手指还在屏幕上时
     *                    SCROLL_STATE_FLING:当用户之前滑动屏幕并抬起手指，屏幕以惯性滚动
     */
    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        if (scrollState==SCROLL_STATE_IDLE||scrollState == SCROLL_STATE_FLING){

        }
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        firstVisibleItemPosition = firstVisibleItem;
    }
//    public void setOnRefreshListener(OnRefreshListener listener){
//
//    }

    public void setOnRefreshListener(OnRefreshListener listener){
        mOnRefreshListener = listener;
    }

    private void initHeaderView(){
        //头布局文件
        header = LayoutInflater.from(getContext()).inflate(R.layout.header,null);
        //测量头布局，绘制一个视图一般经过measure,layout,draw
        header.measure(0,0);
        //头布局高度
        headerViewHeight = header.getMeasuredHeight();
        //设置间隔
        header.setPadding(0,-headerViewHeight,0,0);
        //加载头布局
        this.addHeaderView(header);
        refresh = (ImageView) header.findViewById(R.id.refresh);
        row = (ImageView) header.findViewById(R.id.row);
        header_tv = (TextView) header.findViewById(R.id.header_tv);
        initAnimation();

    }
    private void initAnimation(){
        animation = new RotateAnimation(0f,360f, Animation.RELATIVE_TO_SELF,0.48f,Animation.RELATIVE_TO_SELF,0.47f);
        animation.setDuration(500);
        animation.setRepeatCount(5);

        upAnimation = new RotateAnimation(0f,180f,Animation.RELATIVE_TO_SELF,0.5f,Animation.RELATIVE_TO_SELF,0.5f);
        upAnimation.setDuration(300);
        upAnimation.setFillAfter(true);

        downAnimation = new RotateAnimation(180f,360f,Animation.RELATIVE_TO_SELF,0.5f,Animation.RELATIVE_TO_SELF,0.5f);
        downAnimation.setDuration(300);
        downAnimation.setFillAfter(true);
//        refresh.setAnimation(animation);

    }

    public DragGridView_New(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mVibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        mWindowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        mStatusHeight = getStatusBarHeight(context); //获取状态栏的高度
    }

    private Handler mHandler = new Handler();

    //用来处理是否为长按的Runnable
    private Runnable mLongClickRunnable = new Runnable() {

        @Override
        public void run() {
            isDrag = true; //设置可以拖拽
            mVibrator.vibrate(50); //震动一下
            mStartDragItemView.setVisibility(View.INVISIBLE);//隐藏该item

            //根据我们按下的点显示item镜像
            createDragImage(mDragBitmap, mDownX, mDownY);
        }
    };

    /**
     * 设置回调接口
     * @param onChanageListener
     */
    public void setOnChangeListener(OnChanageListener onChanageListener){
        this.onChanageListener = onChanageListener;
    }

    /**
     * 设置响应拖拽的毫秒数，默认是1000毫秒
     * @param dragResponseMS
     */
    public void setDragResponseMS(long dragResponseMS) {
        this.dragResponseMS = dragResponseMS;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        switch(ev.getAction()){
            case MotionEvent.ACTION_DOWN:
                //使用Handler延迟dragResponseMS执行mLongClickRunnable
                mHandler.postDelayed(mLongClickRunnable, dragResponseMS);

                mDownX = (int) ev.getX();
                mDownY = (int) ev.getY();

                //根据按下的X,Y坐标获取所点击item的position
                mDragPosition = pointToPosition(mDownX, mDownY);

                if(mDragPosition == AdapterView.INVALID_POSITION){
                    return super.dispatchTouchEvent(ev);
                }

                //根据position获取该item所对应的View
                mStartDragItemView = getChildAt(mDragPosition - getFirstVisiblePosition());

                //下面这几个距离大家可以参考我的博客上面的图来理解下
                mPoint2ItemTop = mDownY - mStartDragItemView.getTop();
                mPoint2ItemLeft = mDownX - mStartDragItemView.getLeft();

                mOffset2Top = (int) (ev.getRawY() - mDownY);
                mOffset2Left = (int) (ev.getRawX() - mDownX);

                //获取DragGridView自动向上滚动的偏移量，小于这个值，DragGridView向下滚动
                mDownScrollBorder = getHeight() /4;
                //获取DragGridView自动向下滚动的偏移量，大于这个值，DragGridView向上滚动
                mUpScrollBorder = getHeight() * 3/4;



                //开启mDragItemView绘图缓存
                mStartDragItemView.setDrawingCacheEnabled(true);
                //获取mDragItemView在缓存中的Bitmap对象
                mDragBitmap = Bitmap.createBitmap(mStartDragItemView.getDrawingCache());
                //这一步很关键，释放绘图缓存，避免出现重复的镜像
                mStartDragItemView.destroyDrawingCache();


                break;
            case MotionEvent.ACTION_MOVE:
                int moveX = (int)ev.getX();
                int moveY = (int) ev.getY();

                //如果我们在按下的item上面移动，只要不超过item的边界我们就不移除mRunnable
                if(!isTouchInItem(mStartDragItemView, moveX, moveY)){
                    mHandler.removeCallbacks(mLongClickRunnable);
                }
                break;
            case MotionEvent.ACTION_UP:
                mHandler.removeCallbacks(mLongClickRunnable);
                mHandler.removeCallbacks(mScrollRunnable);
                break;
        }
        return super.dispatchTouchEvent(ev);
    }


    /**
     * 是否点击在GridView的item上面
     * @param dragView
     * @param x
     * @param y
     * @return
     */
    private boolean isTouchInItem(View dragView, int x, int y){
        if(dragView == null) return false;
        int leftOffset = dragView.getLeft();
        int topOffset = dragView.getTop();
        if(x < leftOffset || x > leftOffset + dragView.getWidth()){
            return false;
        }

        if(y < topOffset || y > topOffset + dragView.getHeight()){
            return false;
        }

        return true;
    }



    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if(isDrag && mDragImageView != null){
            switch(ev.getAction()){
                case MotionEvent.ACTION_MOVE:
                    moveX = (int) ev.getX();
                    moveY = (int) ev.getY();
                    //拖动item
                    onDragItem(moveX, moveY);
                    break;
                case MotionEvent.ACTION_UP:
                    onStopDrag();
                    isDrag = false;
                    break;
            }
            return true;
        } else {
            switch(ev.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    /**
                     * listview
                     */
                    //记录按下时y的偏移量
                    downY = (int) ev.getY();//
                    break;
                case MotionEvent.ACTION_MOVE:
                /**
                 * listview
                 */
                //记录移动时的y值偏移
                moveY_listview = (int) ev.getY();
                //可以看成头布局距离屏幕顶部的距离
                paddingTop = ( moveY_listview - downY)/2-headerViewHeight;
                if(firstVisibleItemPosition==0&&-headerViewHeight<paddingTop){//必须的条件
                    //如果paddingTop>0就说明完全显示了，但还要判断当前状态是否是下拉刷新状态，因为正在刷新状态也是完全显示
                    if(paddingTop>0&&currentState == PULL_DOWN_REFRESH){//完全显示
                        header_tv.setText("松开刷新");
                        //将当前状态置为释放刷新
                        currentState = RELEASE_REFRESH;
                        changeHeaderByViewState();
                    }else if(paddingTop<0&&currentState == RELEASE_REFRESH){//没有完全显示,currentState=RELEASE_REFRESH原因是可以先滑到完全显示后再往上滑到不完全显示
                        currentState = PULL_DOWN_REFRESH;
                        header_tv.setText("下拉刷新");
                        changeHeaderByViewState();
                    }
                    header.setPadding(0,paddingTop,0,0);
                }
                break;
                case MotionEvent.ACTION_UP:
                    /**
                     * listview
                     */
                    if(currentState == RELEASE_REFRESH){//完全显示
                        header.setPadding(0,0,0,0);
                        currentState = REFRESHING;
                        changeHeaderByViewState();
                        if (mOnRefreshListener!=null){
                            mOnRefreshListener.downPullRefresh();
                        }
                    }else if(currentState == PULL_DOWN_REFRESH){//未完全显示
                        //当手指松开时，若头部未完全显示则隐藏头部
                        header.setPadding(0,-headerViewHeight,0,0);
                    }
                    break;
            }
            return true;
        }
    }

    private void changeHeaderByViewState(){
        switch (currentState){
            case PULL_DOWN_REFRESH:
                row.startAnimation(downAnimation);
                break;
            case RELEASE_REFRESH:
                row.startAnimation(upAnimation);
                break;
            case REFRESHING:
                row.clearAnimation();
                row.setVisibility(View.GONE);
                header_tv.setText("正在刷新");
                refresh.clearAnimation();
                refresh.setVisibility(View.VISIBLE);
                refresh.startAnimation(animation);
                break;
        }
    }
    public void hideHeaderView(){
        header.setPadding(0,-headerViewHeight,0,0);
        refresh.setVisibility(View.GONE);
        currentState = PULL_DOWN_REFRESH;
        header_tv.setText("下拉刷新");
        row.setVisibility(View.VISIBLE);

    }



    /**
     * 创建拖动的镜像
     * @param bitmap
     * @param downX
     *          按下的点相对父控件的X坐标
     * @param downY
     *          按下的点相对父控件的X坐标
     */
    private void createDragImage(Bitmap bitmap, int downX , int downY){
        mWindowLayoutParams = new WindowManager.LayoutParams();
        mWindowLayoutParams.format = PixelFormat.TRANSLUCENT; //图片之外的其他地方透明
        mWindowLayoutParams.gravity = Gravity.TOP | Gravity.LEFT;
        mWindowLayoutParams.x = downX - mPoint2ItemLeft + mOffset2Left;
        mWindowLayoutParams.y = downY - mPoint2ItemTop + mOffset2Top - mStatusHeight;
        mWindowLayoutParams.alpha = 0.55f; //透明度
        mWindowLayoutParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
        mWindowLayoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        mWindowLayoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE ;

        mDragImageView = new ImageView(getContext());
        mDragImageView.setImageBitmap(bitmap);
        mWindowManager.addView(mDragImageView, mWindowLayoutParams);
    }

    /**
     * 从界面上面移动拖动镜像
     */
    private void removeDragImage(){
        if(mDragImageView != null){
            mWindowManager.removeView(mDragImageView);
            mDragImageView = null;
        }
    }

    /**
     * 拖动item，在里面实现了item镜像的位置更新，item的相互交换以及GridView的自行滚动
     * @param moveX
     * @param moveY
     */
    private void onDragItem(int moveX, int moveY){
        mWindowLayoutParams.x = moveX - mPoint2ItemLeft + mOffset2Left;
        mWindowLayoutParams.y = moveY - mPoint2ItemTop + mOffset2Top - mStatusHeight;
        mWindowManager.updateViewLayout(mDragImageView, mWindowLayoutParams); //更新镜像的位置
        onSwapItem(moveX, moveY);

        //GridView自动滚动
        mHandler.post(mScrollRunnable);
    }


    /**
     * 当moveY的值大于向上滚动的边界值，触发GridView自动向上滚动
     * 当moveY的值小于向下滚动的边界值，触犯GridView自动向下滚动
     * 否则不进行滚动
     */
    private Runnable mScrollRunnable = new Runnable() {

        @Override
        public void run() {
            int scrollY;
            if(moveY > mUpScrollBorder){
                scrollY = -speed;
                mHandler.postDelayed(mScrollRunnable, 25);
            }else if(moveY < mDownScrollBorder){
                scrollY = speed;
                mHandler.postDelayed(mScrollRunnable, 25);
            }else{
                scrollY = 0;
                mHandler.removeCallbacks(mScrollRunnable);
            }

            //当我们的手指到达GridView向上或者向下滚动的偏移量的时候，可能我们手指没有移动，但是DragGridView在自动的滚动
            //所以我们在这里调用下onSwapItem()方法来交换item
            onSwapItem(moveX, moveY);

            View view = getChildAt(mDragPosition - getFirstVisiblePosition());
            //实现GridView的自动滚动
            if(view == null)return;
            smoothScrollToPositionFromTop(mDragPosition, view.getTop() + scrollY);
        }
    };


    /**
     * 交换item,并且控制item之间的显示与隐藏效果
     * @param moveX
     * @param moveY
     */
    private void onSwapItem(int moveX, int moveY){
        //获取我们手指移动到的那个item的position
        int tempPosition = pointToPosition(moveX, moveY);

        //假如tempPosition 改变了并且tempPosition不等于-1,则进行交换
        if(tempPosition != mDragPosition && tempPosition != AdapterView.INVALID_POSITION){
            getChildAt(tempPosition - getFirstVisiblePosition()).setVisibility(View.INVISIBLE);//拖动到了新的item,新的item隐藏掉
            getChildAt(mDragPosition - getFirstVisiblePosition()).setVisibility(View.VISIBLE);//之前的item显示出来

            if(onChanageListener != null){
                onChanageListener.onChange(mDragPosition, tempPosition);
            }

            mDragPosition = tempPosition;
        }
    }


    /**
     * 停止拖拽我们将之前隐藏的item显示出来，并将镜像移除
     */
    private void onStopDrag(){
        getChildAt(mDragPosition - getFirstVisiblePosition()).setVisibility(View.VISIBLE);
        removeDragImage();
    }

//    private static int getStatusHeight(Context context){
//        int statusHeight = 0;
//        Rect localRect = new Rect();
//        ((Activity) context).getWindow().getDecorView().getWindowVisibleDisplayFrame(localRect);
//        statusHeight = localRect.top;
//        if (0 == statusHeight){
//            Class<?> localClass;
//            try {
//                localClass = Class.forName(com.android.internal.R.dimen);
//                Object localObject = localClass.newInstance();
//                int i5 = Integer.parseInt(localClass.getField(status_bar_height).get(localObject).toString());
//                statusHeight = context.getResources().getDimensionPixelSize(i5);
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
//        return statusHeight;
//    }


    /**
     * 获取状态栏的高度
     * @param context
     * @return
     */
    public static int getStatusBarHeight(Context context){

        Class<?> c = null;

        Object obj = null;

        Field field = null;

        int x = 0, statusBarHeight = 38;//通常这个值会是38

        try {

            c = Class.forName("com.android.internal.R$dimen");

            obj = c.newInstance();

            field = c.getField("status_bar_height");

            x = Integer.parseInt(field.get(obj).toString());

            statusBarHeight = context.getResources().getDimensionPixelSize(x);

        } catch (Exception e1) {

            e1.printStackTrace();

        }

        return statusBarHeight;

    }


    /**
     *
     * @author xiaanming
     *
     */
    public interface OnChanageListener{

        /**
         * 当item交换位置的时候回调的方法，我们只需要在该方法中实现数据的交换即可
         * @param form
         *          开始的position
         * @param to
         *          拖拽到的position
         */
        public void onChange(int form, int to);
    }
}