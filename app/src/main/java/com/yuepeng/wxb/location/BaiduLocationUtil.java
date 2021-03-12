package com.yuepeng.wxb.location;

import android.app.Notification;
import android.content.Context;

import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;

/**
 * Created by wangjun on 2021/1/24.
 */
public class BaiduLocationUtil {
    private static LocationClient mLocationClient = null;
    private static BDLocationRegister myListener = new BDLocationRegister();
    private static LocationClientOption locationClientOption;
    /**
     * 初始化百度定位
     */
    public static void initBaiduLocation(Context context) {
        if(mLocationClient == null) {
            mLocationClient = new LocationClient(context);
            //声明LocationClient类
            mLocationClient.registerLocationListener(myListener);
            if (locationClientOption == null)
                initLocation(context);
            mLocationClient.start();
        }else
            startBaiduLocation(context);
    }

    /**
     * 启动百度定位
     */
    public static void startBaiduLocation(Context context){
        if (locationClientOption == null)
            initLocation(context);
        mLocationClient.restart();
    }
    /**
     * 停止百度定位
     */
    public static void stopBaiduLocation(){
        if (locationClientOption !=null) {
            mLocationClient.stop();
        }
    }



    /**
     * 启动百度定位
     * @param context
     */
    private static void initLocation(Context context) {
        locationClientOption = new LocationClientOption();
        locationClientOption.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);
        //可选，默认高精度，设置定位模式，高精度，低功耗，仅设备
        locationClientOption.setCoorType("bd09ll");
        //可选，默认gcj02，设置返回的定位结果坐标系
        locationClientOption.setScanSpan(5000);
        //可选，默认0，即仅定位一次，设置发起定位请求的间隔需要大于等于1000ms才是有效的
        locationClientOption.setIsNeedAddress(true);
        locationClientOption.setWifiCacheTimeOut(5*60*1000);
        //可选，V7.2版本新增能力
        //如果设置了该接口，首次启动定位时，会先判断当前Wi-Fi是否超出有效期，若超出有效期，会先重新扫描Wi-Fi，然后定位
        //可选，设置是否需要地址信息，默认不需要
        locationClientOption.setOpenGps(true);
        //可选，默认false,设置是否使用gps
        locationClientOption.setLocationNotify(true);
        //可选，默认false，设置是否当GPS有效时按照1S/1次频率输出GPS结果
        locationClientOption.setIsNeedLocationDescribe(true);
        //可选，默认false，设置是否需要位置语义化结果，可以在BDLocation.getLocationDescribe里得到，结果类似于“在北京天安门附近”
        locationClientOption.setIsNeedLocationPoiList(true);
        //可选，默认false，设置是否需要POI结果，可以在BDLocation.getPoiList里得到
        locationClientOption.setIgnoreKillProcess(false);
        //可选，默认true，定位SDK内部是一个SERVICE，并放到了独立进程，设置是否在stop的时候杀死这个进程，默认不杀死
        locationClientOption.SetIgnoreCacheException(false);
        //可选，默认false，设置是否收集CRASH信息，默认收集
        locationClientOption.setEnableSimulateGps(true);
        //可选，默认false，设置是否需要过滤GPS仿真结果，默认需要

        //设置打开自动回调位置模式，该开关打开后，期间只要定位SDK检测到位置变化就会主动回调给开发者，该模式下开发者无需再关心定位间隔是多少，定位SDK本身发现位置变化就会及时回调给开发者
        locationClientOption.setOpenAutoNotifyMode();
        //设置打开自动回调位置模式，该开关打开后，期间只要定位SDK检测到位置变化就会主动回调给开发者
        locationClientOption.setOpenAutoNotifyMode(3000,1, LocationClientOption.LOC_SENSITIVITY_HIGHT);

        if (mLocationClient == null) {
            mLocationClient = new LocationClient(context);
            //声明LocationClient类
            mLocationClient.registerLocationListener(myListener);
        }
        mLocationClient.setLocOption(locationClientOption);
    }

    /**
     * 获取百度定位回调
     * @return
     */
    public static BDLocationRegister getBDLocationListener() {
        return myListener;
    }

    public static void enableLocInForeground(Context context ,int i, Notification notification) {
        if (locationClientOption == null)
            initLocation(context);
        mLocationClient.enableLocInForeground(i,notification);
        mLocationClient.start();
    }
}
