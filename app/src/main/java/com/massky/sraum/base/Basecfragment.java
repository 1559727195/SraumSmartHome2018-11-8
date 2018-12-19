package com.massky.sraum.base;

import android.content.Context;
import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.massky.sraum.Util.SharedPreferencesUtil;
import com.massky.sraum.fragment.SceneFragment;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import butterknife.ButterKnife;
/*用于fragment的基类*/

/**
 * Created by masskywcy on 2016-07-08.
 */
public abstract class Basecfragment extends Fragment implements View.OnClickListener {
    public Context mContext;
    protected boolean isVisible;
    private boolean isPrepared;
    public static boolean isForegrounds = false;
    public static boolean isDestroy = false;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(viewId(), null);
        ButterKnife.inject(this, rootView);
        onView();
        return rootView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset(this);
    }

    @Override
    public void onStart() {
        super.onStart();
    }


    protected abstract int viewId();

    protected abstract void onView();

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (getUserVisibleHint()) {
            isVisible = true;
            lazyLoad();
        } else {
            isVisible = false;
            onInvisible();
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
//        Log.d("TAG", "fragment->onActivityCreated");
        isPrepared = true;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getActivity();
        isDestroy = false;
        setHasOptionsMenu(true);
        lazyLoad();
//        Log.d("TAG", "fragment->onCreate");
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
    }

    /**
     * 懒加载
     */
    protected void lazyLoad() {
        if (!isPrepared || !isVisible) {
            return;
        }

        initData();
    }

    public abstract void initData();

    //do something
    protected void onInvisible() {

    }

    @Override
    public void onResume() {
        isForegrounds = true;
        super.onResume();
    }

    @Override
    public void onPause() {
        isForegrounds = false;
        super.onPause();
    }

    @Override
    public void onDestroy() {
        isDestroy = true;
        super.onDestroy();
    }
}
