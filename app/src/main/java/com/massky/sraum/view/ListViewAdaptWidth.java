package com.massky.sraum.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ListView;

/**
 * Created by zhu on 2018/10/17.
 */

public class ListViewAdaptWidth extends ListView {
    public ListViewAdaptWidth(Context context) {
        super(context);
    }

    public ListViewAdaptWidth(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ListViewAdaptWidth(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = getMaxWidthOfChildren() + getPaddingLeft() + getPaddingRight();//计算listview的宽度
        super.onMeasure(MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY), heightMeasureSpec);//设置listview的宽高

    }

    /**
     * 计算item的最大宽度
     *
     * @return
     */
    private int getMaxWidthOfChildren() {
        int maxWidth = 0;
        View view = null;
        int count = getAdapter().getCount();
        for (int i = 0; i < count; i++) {
            view = getAdapter().getView(i, view, this);
            view.measure(MeasureSpec.UNSPECIFIED, MeasureSpec.UNSPECIFIED);
            if (view.getMeasuredWidth() > maxWidth)
                maxWidth = view.getMeasuredWidth();
        }
        return maxWidth;
    }

}
