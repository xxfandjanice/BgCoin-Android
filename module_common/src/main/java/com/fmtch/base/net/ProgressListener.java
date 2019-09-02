package com.fmtch.base.net;
public interface ProgressListener {

	//已完成的 总的文件长度 是否完成
	void onProgress(long progress, long total, boolean done);
}
