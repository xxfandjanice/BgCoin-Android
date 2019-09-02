package com.fmtch.base.mvp.model;

import io.realm.RealmAsyncTask;

public abstract class BaseModel {
    /**
     * 取消任务
     * @param task
     */
    public void cancelTask(RealmAsyncTask task){
        if (task!=null&&!task.isCancelled()){
            task.cancel();
        }
    }

    public void onDestroy() {

    }
}
