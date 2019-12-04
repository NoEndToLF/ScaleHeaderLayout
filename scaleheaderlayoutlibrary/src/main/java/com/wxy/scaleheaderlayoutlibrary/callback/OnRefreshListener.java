package com.wxy.scaleheaderlayoutlibrary.callback;

public interface OnRefreshListener {
    void onRefreshPrepare();
    void onRefreshPulldown(float dy,float headViewHeight);
    void onRefreshReady();
}
