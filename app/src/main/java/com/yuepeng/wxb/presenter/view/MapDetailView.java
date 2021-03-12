package com.yuepeng.wxb.presenter.view;

import com.wstro.thirdlibrary.base.BaseDetailView;

/**
 * Created by wangjun on 2021/2/4.
 */
public interface MapDetailView extends BaseDetailView {

    //void addLocation(double lat,double lon,String location);
    void onGetMessageListSuccess(int totalMsg);
}
