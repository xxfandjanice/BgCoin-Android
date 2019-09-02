package com.fmtch.base.event;

import com.fmtch.base.ui.BaseActivity;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by MakerYan on 16/9/20 14:08.
 * Personal e-mail : light.yan@qq.com
 */
public class EventBean<T>
		implements Serializable {

	@SerializedName("tag")
	public int tag;

	@SerializedName("data")
	public T data;

	@SerializedName("targetActivity")
	public BaseActivity targetActivity;

	@SerializedName("targetActivityByName")
	public String targetActivityByName;

	public EventBean() {

	}

	public EventBean(int tag, BaseActivity targetActivity) {

		this.tag = tag;
		this.targetActivity = targetActivity;
	}

	public EventBean(int tag, String targetActivityByName) {

		this.tag = tag;
		this.targetActivityByName = targetActivityByName;
	}

	public EventBean(int tag) {

		this.tag = tag;
	}

	public EventBean(int tag, T data) {

		this.tag = tag;
		this.data = data;
	}

	public EventBean(int tag, String targetActivityByName, T data) {

		this.tag = tag;
		this.targetActivityByName = targetActivityByName;
		this.data = data;
	}


	public int getTag() {

		return tag;
	}

	public void setTag(int tag) {

		this.tag = tag;
	}

	public T getData() {

		return data;
	}

	public void setData(T data) {

		this.data = data;
	}

	public BaseActivity gettargetActivity() {

		return targetActivity;
	}

	public void settargetActivity(BaseActivity targetActivity) {

		this.targetActivity = targetActivity;
	}

	@Override
	public String toString() {

		return "EventBusBean{" + "tag=" + tag + ", data=" + data + "} " + super.toString();
	}
}
