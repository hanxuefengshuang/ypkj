package com.yuepeng.wxb.utils;

/**
 * @author:create by Nico
 * company:余舒科技
 * createTime:2/25/21
 * Email:752497253@qq.com
 */
public class LocationTools {

    private static final double PI = 3.1415926535897932384626433832795;

    /**
     * 百度坐标系 BD-09 to 火星坐标系 GCJ-02
     *
     * @param latitude  纬度
     * @param longitude 经度
     * @return [纬度, 经度]
     */
    public static double[] calBD09toGCJ02(double latitude, double longitude) {
        double x = longitude - 0.0065, y = latitude - 0.006;
        double z = Math.sqrt(x * x + y * y) - 0.00002 * Math.sin(y * PI);
        double theta = Math.atan2(y, x) - 0.000003 * Math.cos(x * PI);
        double retLat = z * Math.sin(theta);
        double retLon = z * Math.cos(theta);
        return new double[]{retLat, retLon};
    }
}
