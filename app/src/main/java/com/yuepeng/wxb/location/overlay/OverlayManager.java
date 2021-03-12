package com.yuepeng.wxb.location.overlay;

import android.util.Log;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BaiduMap.OnPolylineClickListener;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.Overlay;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.wstro.thirdlibrary.utils.GeoHasher;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.baidu.mapapi.map.BaiduMap.OnMarkerClickListener;

/**
 * 该类提供一个能够显示和管理多个Overlay的基类
 * <p>
 * 复写{@link #} 设置欲显示和管理的Overlay列表
 * </p>
 * <p>
 * 通过
 * {@link BaiduMap#setOnMarkerClickListener(OnMarkerClickListener)}
 * 将覆盖物点击事件传递给OverlayManager后，OverlayManager才能响应点击事件。
 * <p>
 * 复写{@link #onMarkerClick(Marker)} 处理Marker点击事件
 * </p>
 */
public abstract class OverlayManager implements OnMarkerClickListener, OnPolylineClickListener {

   protected BaiduMap mBaiduMap = null;

    private List<OverlayOptions> mOverlayOptionList = null;

   protected List<Overlay> mOverlayList = null;
    private double maxLatitude;
    private double minLatitude;
    private double maxLongitude;
    private double minLongitude;
    private double distance;
    private int level;

    /**
     * 通过一个BaiduMap 对象构造
     *
     * @param baiduMap
     */
    public OverlayManager(BaiduMap baiduMap) {
        mBaiduMap = baiduMap;
        // mBaiduMap.setOnMarkerClickListener(this);
        if (mOverlayOptionList == null) {
            mOverlayOptionList = new ArrayList<>();
        }
        if (mOverlayList == null) {
            mOverlayList = new ArrayList<>();
        }
    }

    /**
     * 覆写此方法设置要管理的Overlay列表
     * 
     * @return 管理的Overlay列表
     */
    public abstract List<OverlayOptions> getOverlayOptions(BaiduMap mBaiduMap);

    /**
     * 将所有Overlay 添加到地图上
     */
    public final void addToMap() {
        if (mBaiduMap == null) {
            return;
        }

        mOverlayOptionList.clear();
        List<OverlayOptions> overlayOptions = getOverlayOptions(mBaiduMap);
        if (overlayOptions != null) {
            mOverlayOptionList.addAll(getOverlayOptions(mBaiduMap));
        }

        for (int i=0;i<mOverlayOptionList.size();i++) {
            OverlayOptions option = mOverlayOptionList.get(i);
            mOverlayList.add(mBaiduMap.addOverlay(option));
        }
    }

    /**
     * 将所有Overlay 从 地图上消除
     */
    public final void removeFromMap() {
        if (mBaiduMap == null) {
            return;
        }
        for (Overlay marker : mOverlayList) {
            marker.remove();
        }
        mOverlayOptionList.clear();
        mOverlayList.clear();

    }

    /**
     * 缩放地图，使所有Overlay都在合适的视野内
     * <p>
     * 注： 该方法只对Marker类型的overlay有效
     * </p>
     *
     * @param center
     */
    public void zoomToSpan(LatLng center) {
        if (mBaiduMap == null) {
            return;
        }
//        if (mOverlayList.size() > 0) {
//            LatLngBounds.Builder builder = new LatLngBounds.Builder();
//            for (Overlay overlay : mOverlayList) {
//                // polyline 中的点可能太多，只按marker 缩放
//                if (overlay instanceof Marker) {
//                    builder.include(((Marker) overlay).getPosition());
//                }
//            }
//            builder.include(center);
//            mBaiduMap.setMapStatus(MapStatusUpdateFactory
//                    .newLatLngBounds(builder.build()));
//        }
        //比较选出集合中最大经纬度
        getMax(center);
        //计算两个Marker之间的距离
        calculateDistance();
        //根据距离判断地图级别
        getLevel();
        //计算中心点经纬度，将其设为启动时地图中心
    }

    /**
     * 比较选出集合中最大经纬度
     */
    private void getMax(LatLng center) {
        List<Double> latitudeList = new ArrayList<>();
        List<Double> longitudeList = new ArrayList<>();
        for (Overlay overlay : mOverlayList) {
            // polyline 中的点可能太多，只按marker 缩放
            if (overlay instanceof Marker) {
                LatLng latLng = ((Marker) overlay).getPosition();
                latitudeList.add(latLng.latitude);
                longitudeList.add(latLng.longitude);
            }
        }
        latitudeList.add(center.latitude);
        longitudeList.add(center.longitude);

        maxLatitude = Collections.max(latitudeList);
        minLatitude = Collections.min(latitudeList);
        maxLongitude = Collections.max(longitudeList);
        minLongitude = Collections.min(longitudeList);
    }

    /**
     * 计算两个Marker之间的距离
     */
    private void calculateDistance() {
        distance = GeoHasher.GetDistance(maxLatitude, maxLongitude, minLatitude, minLongitude);
    }

    /**
     *根据距离判断地图级别
     */
    private void getLevel() {
        int zoom[] = {10, 20, 50, 100, 200, 500, 1000, 2000, 5000, 1000, 2000, 25000, 50000, 100000, 200000, 500000, 1000000, 2000000};
        Log.i("info", "maxLatitude==" + maxLatitude + ";minLatitude==" + minLatitude + ";maxLongitude==" + maxLongitude + ";minLongitude==" + minLongitude);
        Log.i("info", "distance==" + distance);
        for (int i = 0; i < zoom.length; i++) {
            int zoomNow = zoom[i];
            if (zoomNow - distance * 1000 > 0) {
                level = 18 - i + 6;
                //设置地图显示级别为计算所得level
                mBaiduMap.setMapStatus(MapStatusUpdateFactory.newMapStatus(new MapStatus.Builder().zoom(level).build()));
                break;
            }
        }
    }

}
