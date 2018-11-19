package com.team4.capstone.voiceofcau;
import android.graphics.drawable.Drawable;

public class ListViewItem {
    private Drawable iconDrawable ;
    private String titleStr ;
    private String descStr ;
    private String pathStr;
    public void setIcon(Drawable icon) {
        iconDrawable = icon ;
    }
    public void setTitle(String title) {
        titleStr = title ;
    }
    public void setDesc(String desc) {
        descStr = desc ;
    }
    public void setPath(String path) {
        pathStr = path ;
    }
    public Drawable getIcon() {
        return this.iconDrawable ;
    }
    public String getTitle() {
        return this.titleStr ;
    }
    public String getDesc() {
        return this.descStr ;
    }
    public String getPath() {
        return this.pathStr ;
    }
    public String[] getData() {
        String[] ret = new String[3];
        ret[0] = titleStr;
        ret[1] = descStr;
        ret[2] = pathStr;
        return ret;
    }
}

