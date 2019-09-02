package com.fmtch.base.net.response;


import java.io.Serializable;

public class ProgressModel implements Serializable {

    public long progress;

    public long total;

    public boolean isDone;

    public ProgressModel(long progress, long total, boolean isDone) {

        this.progress = progress;
        this.total = total;
        this.isDone = isDone;
    }

    public long getProgress() {
        return progress;
    }

    public void setProgress(long progress) {
        this.progress = progress;
    }

    public long getTotal() {
        return total;
    }

    public void setTotal(long total) {
        this.total = total;
    }

    public boolean isDone() {
        return isDone;
    }

    public void setDone(boolean done) {
        isDone = done;
    }

}
