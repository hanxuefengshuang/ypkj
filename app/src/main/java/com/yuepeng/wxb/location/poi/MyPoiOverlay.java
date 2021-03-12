package com.yuepeng.wxb.location.poi;

import android.view.LayoutInflater;

import com.baidu.mapapi.map.BaiduMap;

/**
 * Created by ${User} on 2018/9/26
 */
public class MyPoiOverlay extends PoiOverlay {

    public MyPoiOverlay(BaiduMap baiduMap, LayoutInflater from) {
        super(baiduMap,from);


    }

    @Override
    public boolean onPoiClick( int index) {
        super.onPoiClick(index);

        return true;
    }
}
