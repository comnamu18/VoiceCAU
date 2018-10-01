package com.team4.capstone.voiceofcau;

import android.graphics.drawable.Drawable;


public class IconTextItem {
    //private란 변경이 되면 안 되는 변수에 대해 접근 제한 자 설정
    private Drawable mIcon;
    private String[] mData;

    private boolean mSelectable = true;


    //파라메타값이 배열로된것을 사용함
    public IconTextItem(Drawable icon, String[] obj){
        mIcon = icon;
        mData = obj;

    }

    //위와 다르게 파라메타 값에 배열이 아닌 직접 본인이 넣을 때
    //위와 이거와 동일한 기능을 하지만 위의 방법을 쓰는 게 좀 더 간단하고 좋아 보임
    public IconTextItem(Drawable icon, String obj01, String obj02, String obj03) {
        mIcon = icon;

        mData = new String[3];
        mData[0] = obj01;
        mData[1] = obj02;
        mData[2] = obj03;
    }

    public boolean isSelectable(){
        return mSelectable;
    }
    public void setSelectable(boolean selectable) {
        mSelectable = selectable;
    }

    //메소드 getData()는 mData 값을 반환(이유는 다른 자바 클래스에서 쓸 수 있게)
    public String[] getData() {
        return mData;
    }

    //위의 메소드 getData()는 배열로 되어있기 때문에 한 개씩 값을 가져가기 위해서는 쪼개주는 작업이 필요함
    //현재 이 getData() 메소드는 파라메타 값에 가져오고 싶은 배열의 순번을 넣어주면 알아서 쪼개주는 역할
    public String getData(int index) {
        //이 구문은 getData(index) 값을 줬을 때 값을 불러올 mData에 값이 있는지 혹은 index 값이 mData가 가지고 있는 값을 초과했는지를 판별해주는 조건문
        if (mData == null || index >= mData.length) {
            return null;
        }
        return mData[index];
    }

    public void setData(String[] obj){
        mData = obj;
    }
    public void setIcon(Drawable icon){
        mIcon = icon;
    }

    //메소드 getIcon()는 mIcon 값을 반환(이유는 다른 자바 클래스에서 쓸 수 있게)
    public Drawable getIcon() {
        return mIcon;
    }

}
