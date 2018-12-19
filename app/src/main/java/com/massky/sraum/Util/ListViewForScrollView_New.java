package com.massky.sraum.Util;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ListView;

/**
 * Created by zhu on 2018/6/8.
 */

public class ListViewForScrollView_New extends ListView {

    public ListViewForScrollView_New(Context context) {
        super(context);
    }

    public ListViewForScrollView_New(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ListViewForScrollView_New(Context context, AttributeSet attrs,
                                     int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    /**
     * 重写该方法，达到使ListView适应ScrollView的效果
     */
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2,
                MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, expandSpec);
    }
}
