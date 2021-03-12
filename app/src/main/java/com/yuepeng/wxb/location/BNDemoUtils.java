/*
 * Copyright (C) 2018 Baidu, Inc. All Rights Reserved.
 */
package com.yuepeng.wxb.location;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Point;
import android.os.Build;
import android.view.Display;
import android.view.KeyCharacterMap;
import android.view.KeyEvent;
import android.view.ViewConfiguration;

import java.text.DecimalFormat;


public class BNDemoUtils {

    public static final String APP_FOLDER_NAME = "com.yuepeng.wxb";
    public static final int NORMAL = 1 << 1;
    public static final int ANALOG = 1 << 2;
    public static final int EXTGPS = 1 << 3;

    public static final String KEY_gb_iconset = "gb_iconset"; // 用于sp储存 定制icon 开关状态
    public static final String KEY_gb_iconshow = "gb_iconshow"; // icon显示 开关状态
    public static final String KEY_gb_carnum = "gb_carnum"; // 设置车牌 开关状态
    public static final String KEY_gb_carnumtxt = "gb_carnumtxt"; // 设置的车牌
    public static final String KEY_gb_cariconoffset = "gb_cariconoffset"; // 车标偏移 开关状态
    public static final String KEY_gb_cariconoffset_x = "gb_cariconoffset_x"; // 车标偏移x坐标
    public static final String KEY_gb_cariconoffset_y = "gb_cariconoffset_y"; // 车标偏移y坐标
    public static final String KEY_gb_margin = "gb_margin"; // 屏幕边距 开关状态
    public static final String KEY_gb_routeSort = "gb_routeSort"; // 路线偏好 开关状态
    public static final String KEY_gb_routeSearch = "gb_routeSearch"; // 沿途检索 开关状态
    public static final String KEY_gb_moreSettings = "gb_moreSettings"; // 更多设置 开关状态
    public static final String KEY_gb_seeall = "gb_seeall"; // 用于sp储存 定制icon 开关状态



    public static boolean checkDeviceHasNavigationBar(Activity context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            Display display = context.getWindowManager().getDefaultDisplay();
            Point size = new Point();
            Point realSize = new Point();
            display.getSize(size);
            display.getRealSize(realSize);
            return realSize.y != size.y;
        } else {
            boolean hasMenuKey = ViewConfiguration.get(context)
                    .hasPermanentMenuKey();
            boolean hasBackKey = KeyCharacterMap
                    .deviceHasKey(KeyEvent.KEYCODE_BACK);
            return hasMenuKey || hasBackKey;
        }
    }

    public static int getNavigationBarHeight(Activity context) {
        int result = 0;
        if (checkDeviceHasNavigationBar(context)) {
            Resources res = context.getResources();
            int resourceId = res.getIdentifier("navigation_bar_height",
                    "dimen", "android");
            if (resourceId > 0) {
                result = res.getDimensionPixelSize(resourceId);
            }
        }
        return result;
    }
    public static void setString(Context context, String key, String value) {
        SharedPreferences sp =
                context.getSharedPreferences(APP_FOLDER_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(key, value);
        editor.apply();
    }

    public static String getString(Context context, String key) {
        SharedPreferences sp =
                context.getSharedPreferences(APP_FOLDER_NAME, Context.MODE_PRIVATE);
        return sp.getString(key, "");
    }

    public static void setBoolean(Context context, String key, boolean value) {
        SharedPreferences sp =
                context.getSharedPreferences(APP_FOLDER_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean(key, value);
        editor.apply();
    }

    public static Boolean getBoolean(Context context, String key) {
        SharedPreferences sp =
                context.getSharedPreferences(APP_FOLDER_NAME, Context.MODE_PRIVATE);
        return sp.getBoolean(key, false);
    }

    public static Boolean getBoolean(Context context, String key, boolean defaultreturn) {
        SharedPreferences sp =
                context.getSharedPreferences(APP_FOLDER_NAME, Context.MODE_PRIVATE);
        return sp.getBoolean(key, defaultreturn);
    }

    /**
     * 计算两点之间真实距离
     *
     * @return 千米(km)
     */
    public static String getDistance(double longitude1, double latitude1, double longitude2, double latitude2) {
        // 纬度
        double lat1 = (Math.PI / 180) * latitude1;
        double lat2 = (Math.PI / 180) * latitude2;
        // 经度
        double lon1 = (Math.PI / 180) * longitude1;
        double lon2 = (Math.PI / 180) * longitude2;
        // 地球半径
        double R = 6371;
        // 两点间距离 km,如果想要米的话,结果*1000
        DecimalFormat df = new DecimalFormat("#.00");
        return df.format(Math.acos(Math.sin(lat1) * Math.sin(lat2) + Math.cos(lat1) * Math.cos(lat2) * Math.cos(lon2 - lon1)) * R);

    }


    private static double EARTH_RADIUS = 6378.137;

    private static double rad(double d) {
        return d * Math.PI / 180.0;
    }

    /**
     * 通过经纬度获取距离(单位：米)
     * @param lng1 经度1
     * @param lat1 纬度1
     * @param lng2 经度2
     * @param lat2 纬度2
     * @return
     */
    public static double getDistance2(double lng1, double lat1, double lng2, double lat2) {
        double radLat1 = rad(lat1);
        double radLat2 = rad(lat2);
        double a = radLat1 - radLat2;
        double b = rad(lng1) - rad(lng2);
        double s = 2 * Math.asin(Math.sqrt(Math.pow(Math.sin(a / 2), 2)
                + Math.cos(radLat1) * Math.cos(radLat2)
                * Math.pow(Math.sin(b / 2), 2)));
        s = s * EARTH_RADIUS;
        s = Math.round(s * 10000d) / 10000d;
        s = s*1000;
        return s;
    }
}
