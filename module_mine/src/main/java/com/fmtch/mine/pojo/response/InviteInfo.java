package com.fmtch.mine.pojo.response;

public class InviteInfo {

    private int status;

    private int pos;

    public InviteInfo(int pos) {
        this.pos = pos;
    }

    public int getPos() {
        return pos;
    }

    public void setPos(int pos) {
        this.pos = pos;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
