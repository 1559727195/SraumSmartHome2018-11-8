package com.king.photo.util;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import android.app.Activity;

/**
 * 存放所有的list在最后退出时一起关闭
 *
 * @author king
 * @QQ:595163260
 * @version 2014年10月18日  下午11:50:49
 */
public class PublicWay {
	public static CopyOnWriteArrayList<Activity> activityList = new CopyOnWriteArrayList<Activity>();
	
	public static int num = 9;


	/**
	 * 结束指定的Activity
	 */
	private static void finishActivity(Activity activity) {
		if (activity != null) {
			activityList.remove(activity);
			if(!activity.isFinishing()) {
				activity.finish();
			}
		}
	}

	/**
	 * 结束指定类名的Activity
	 */
	public static void finishActivity(Class<?> cls) {
		for (Activity activity : activityList) {
			if (activity.getClass().equals(cls)) {
				finishActivity(activity);
			}
		}
	}


	/**
	 * 结束除了指定类名的Activity
	 */
	public static void finishButActivity(Class<?> cls) {
		for (Activity activity : activityList) {
			if (!activity.getClass().equals(cls)) {
				finishActivity(activity);
			}
		}
	}


}
