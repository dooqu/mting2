package cn.xylink.mting;

import android.app.Activity;

import java.util.Stack;

public class ActivityManager {

	private Stack<Activity> activityStack;
	private static ActivityManager instance;
	private ActivityManager(){}
	public static ActivityManager getScreenManager(){
		if(null == instance){
			instance = new ActivityManager();
		}
		return instance;
	}
	public void popActivity(Activity activity){
		if(null != activity){
			activity.finish();
			activityStack.remove(activity);
			activity = null;
		}
	}
	public void pushActivity(Activity activity){
		if(null == activityStack){
			activityStack = new Stack<Activity>();
		}
		activityStack.add(activity);
	}
	public Activity currentActivity(){
		Activity activity = null;
		if(activityStack != null && !activityStack.isEmpty()){
			activity = activityStack.lastElement();
		}
		return activity;
	}
	@SuppressWarnings("rawtypes")
	public void popAllActivityExceptOne(Class cls){
		while(true){
			Activity activity = currentActivity();
			if(null == activity){
				break;
			}
			if(activity.getClass().equals(cls)){
				break;
			}
			popActivity(activity);
		}
		
	}
	public void popAllActivitys(){
		while(true){
			Activity activity = currentActivity();
			if(null == activity){
				break;
			}

			popActivity(activity);
		}

	}
}
