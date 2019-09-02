package com.fmtch.base.event;

/**
 * Created by wtc on 2019/6/27
 */
public class BottomDialogItem {

    private String text;
    private boolean isSelected;

    public BottomDialogItem(String text, boolean isSelected) {
        this.text = text;
        this.isSelected = isSelected;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }
}
