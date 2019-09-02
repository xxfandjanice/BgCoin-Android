package com.fmtch.base.manager;

import android.app.Activity;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import java.util.Stack;

public class ActivityManager {
    private static Stack<Activity> mActivityStack;

    /**
     * 构造函数必须是私有的 这样在外部便无法使用 new 来创建该类的实例
     */
    private ActivityManager() {
        mActivityStack = new Stack<>();
    }

    /**
     * 单一实例
     * @return
     */
    public static ActivityManager getInstance() {
        return Holder.instance;
    }

    /**
     * 添加一个Activity到堆栈中
     * @param activity
     */
    public void addActivity(Activity activity) {
        mActivityStack.add(activity);
    }

    /**
     * 从堆栈中移除指定的Activity
     * @param activity
     */
    public void removeActivity(Activity activity) {
        if(activity != null) {
            mActivityStack.remove(activity);
        }
    }

    /**
     * 获取顶部的Activity
     * @return
     */
    public Activity getTopActivity() {
        if(mActivityStack.isEmpty()) {
            return null;
        } else {
            return mActivityStack.get(mActivityStack.size() - 1);
        }
    }

    /**
     * 结束所有的Activity, 推出应用
     */
    public void removeAllActivity() {
        if(mActivityStack != null && mActivityStack.size() > 0) {
            for(Activity activity : mActivityStack) {
                activity.finish();
            }
        }
    }

    /**
     * 将一个Fragment 添加到Activity中
     * @param fragmentManager
     * @param fragment
     * @param container
     */
    public void addFragmentToActivity(FragmentManager fragmentManager, Fragment fragment, int container) {
        if(null != fragmentManager && null != fragment) {
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            transaction.add(container, fragment);
            transaction.commit();
        }
    }

    /**
     * 将一个Fragment添加到Activity中，并添加tag标识
     * @param fragmentManager  fragment管理器
     * @param fragment         需要添加的fragment
     * @param container        布局FrameLayout的Id
     * @param tag              fragment的唯一tag标识
     * @param isAddToBackStack   是否添加到栈中，可通过返回键进行切换fragment
     */
    public void addFragmentToActivity(FragmentManager fragmentManager, Fragment fragment,
                                      int container, String tag, boolean isAddToBackStack) {
        if(null != fragmentManager && null != fragment) {
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            transaction.add(container,fragment,tag);
            if(isAddToBackStack) {
                transaction.addToBackStack(tag);
            }
            transaction.commit();
        }
    }

    /**
     * 对Fragment进行显示隐藏的切换，减少fragment的重复创建
     * @param fragmentManager    fragment管理器
     * @param hideFragment       需要隐藏的Fragment
     * @param showFragment       需要显示的Fragment
     * @param container          布局FrameLayout的Id
     * @param tag                fragment的唯一tag标识
     */
    public void switchFragment(FragmentManager fragmentManager, Fragment hideFragment,
                               Fragment showFragment, int container, String tag) {
        if(fragmentManager != null) {
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            if(!showFragment.isAdded()) {
                transaction.hide(hideFragment)
                        .add(container, showFragment,tag);
            } else {
                transaction.hide(hideFragment)
                        .show(showFragment)
                        .commit();
            }
        }
    }

    /**
     * 替换Activity中的Fragment
     * @param fragmentManager fragment管理器
     * @param fragment        需要替换到Activity的Fragment
     * @param container       布局FrameLayout的container
     */
    public void replaceFragmentFromActivity(FragmentManager fragmentManager, Fragment fragment, int container) {
        if(null != fragmentManager && null != fragment) {
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            transaction.replace(container, fragment);
            transaction.commit();
        }
    }

    private static class Holder {
        private static ActivityManager instance = new ActivityManager();
    }
}
