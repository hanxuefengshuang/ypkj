package com.yuepeng.wxb.ui.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.CustomMapStyleCallBack;
import com.baidu.mapapi.map.MapCustomStyleOptions;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.Overlay;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.PolylineOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.model.LatLngBounds;
import com.baidu.trace.api.track.HistoryTrackRequest;
import com.baidu.trace.api.track.HistoryTrackResponse;
import com.baidu.trace.api.track.OnTrackListener;
import com.baidu.trace.api.track.TrackPoint;
import com.baidu.trace.model.CoordType;
import com.baidu.trace.model.ProcessOption;
import com.baidu.trace.model.SortType;
import com.baidu.trace.model.StatusCodes;
import com.baidu.trace.model.TransportMode;
import com.blankj.utilcode.util.LogUtils;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.wstro.thirdlibrary.base.BasePresenter;
import com.wstro.thirdlibrary.entity.KithEntity;
import com.yuepeng.wxb.R;
import com.yuepeng.wxb.base.App;
import com.yuepeng.wxb.base.BaseActivity;
import com.yuepeng.wxb.databinding.ActivityMapSportBinding;
import com.yuepeng.wxb.location.BNDemoUtils;
import com.yuepeng.wxb.utils.BitmapUtil;
import com.yuepeng.wxb.utils.CommonUtil;
import com.yuepeng.wxb.utils.MapUtil;
import com.yuepeng.wxb.utils.PreUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wangjun on 2021/2/7.
 */
public class MapSportActivity extends BaseActivity<ActivityMapSportBinding, BasePresenter> {

    private KithEntity kithEntity;
    private long startTime;
    private long endTime;
    private BaiduMap mBaiduMap;
    private MapStatusUpdate mapStatusUpdate;
    private LatLng cneter;
    public LatLng lastPoint = null;
    private Marker mMoveMarker = null;
    private Overlay polylineOverlay;
    private MapStatus mapStatus;

    @Override
    protected View injectTarget() {
        return null;
    }

    @Override
    protected BasePresenter createPresenter() {
        return null;
    }

    @Override
    protected void Retry() {

    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_map_sport;
    }

    @Override
    protected void initView() {
        //mapUtil = MapUtil.getInstance();
        //mapUtil.init(mBinding.mapview);
        mSwipeBackHelper.setIsOnlyTrackingLeftEdge(true);
        kithEntity = (KithEntity) getIntent().getSerializableExtra("KithEntity");
        LatLng center = PreUtils.getLocation();
        //mapUtil.setCenter(new LatLng(Double.parseDouble(kithEntity.getLat()), Double.parseDouble(kithEntity.getLng())));
        mBinding.userName.setText(kithEntity.getKithNote());
        mBinding.ivBack.setOnClickListener(view -> finish());
        Glide.with(this).load(kithEntity.getHeadImg()).apply(new RequestOptions().circleCrop()).into(mBinding.avatar);
        String distance = BNDemoUtils.getDistance(center.longitude, center.latitude, Double.parseDouble(kithEntity.getLng()), Double.parseDouble(kithEntity.getLat()));
        int v = (int) (Double.parseDouble(distance) * 1000);
        if (v < 500) {
            mBinding.tvDistance.setText("距我" + v + "m | " + kithEntity.getLocation());
        } else {
            mBinding.tvDistance.setText("距我" + distance + "km | " + kithEntity.getLocation());
        }
        mBinding.ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        cneter = new LatLng(Double.parseDouble(kithEntity.getLat()), Double.parseDouble(kithEntity.getLng()));
        mBaiduMap = mBinding.mapview.getMap();
        mapStatusUpdate = MapStatusUpdateFactory.newLatLngZoom(cneter, 18.0f);
        mBaiduMap.animateMapStatus(mapStatusUpdate);
        MapStatus.Builder builder = new MapStatus.Builder();
        builder.target(cneter);
        MapStatusUpdate newMapStatus = MapStatusUpdateFactory.newMapStatus(builder.build());
        mBaiduMap.setMapStatus(newMapStatus);
        addMarker(cneter);

        //个性化地图
        MapCustomStyleOptions mapCustomStyleOptions = new MapCustomStyleOptions();
        mapCustomStyleOptions.customStyleId("b96233dc70ee6e369b3152e7e44c99fe"); //在线样式文件对应的id。
        mBinding.mapview.setMapCustomStyle(mapCustomStyleOptions, new CustomMapStyleCallBack() {
            @Override
            public boolean onPreLoadLastCustomMapStyle(String customStylePath) {
                LogUtils.d("onPreLoadLastCustomMapStyle:"+customStylePath);
                return false; //默认返回false，由SDK内部处理加载逻辑；返回true则SDK内部不会做任何处理，由开发者自行完成样式加载。
            }

            @Override
            public boolean onCustomMapStyleLoadSuccess(boolean hasUpdate, String customStylePath) {
                LogUtils.d("onCustomMapStyleLoadSuccess:"+customStylePath);

                return false; //默认返回false，由SDK内部处理加载逻辑；返回true则SDK内部不会做任何处理，由开发者自行完成样式加载。
            }

            @Override
            public boolean onCustomMapStyleLoadFailed(int status, String Message, String customStylePath) {
                LogUtils.d("onCustomMapStyleLoadFailed:"+customStylePath + " status:"+status +" msg:"+Message);
                return false; //默认返回false，由SDK内部处理加载逻辑；返回true则SDK内部不会做任何处理，由开发者自行完成样式加载。
            }
        });

    }
    public Marker addOverlay(LatLng currentPoint, BitmapDescriptor icon, Bundle bundle) {
        OverlayOptions overlayOptions = new MarkerOptions().position(currentPoint)
                .icon(icon).zIndex(9).draggable(true);
        Marker marker = (Marker) mBaiduMap.addOverlay(overlayOptions);
        if (null != bundle) {
            marker.setExtraInfo(bundle);
        }
        return marker;
    }

    /**
     * 添加地图覆盖物
     */
    public void addMarker(LatLng currentPoint) {
        if (null == mMoveMarker) {
            mMoveMarker = addOverlay(currentPoint, BitmapDescriptorFactory.fromResource(R.mipmap.icon_point), null);
            return;
        }

        lastPoint = currentPoint;
        mMoveMarker.setPosition(currentPoint);
        moveLooper(currentPoint);
    }
    /**
     * 移动逻辑
     */
    public void moveLooper(LatLng endPoint) {

        mMoveMarker.setPosition(lastPoint);
        mMoveMarker.setRotate((float) CommonUtil.getAngle(lastPoint, endPoint));

        double slope = CommonUtil.getSlope(lastPoint, endPoint);
        // 是不是正向的标示（向上设为正向）
        boolean isReverse = (lastPoint.latitude > endPoint.latitude);
        double intercept = CommonUtil.getInterception(slope, lastPoint);
        double xMoveDistance = isReverse ? CommonUtil.getXMoveDistance(slope) : -1 * CommonUtil.getXMoveDistance(slope);

        for (double latitude = lastPoint.latitude; latitude > endPoint.latitude == isReverse; latitude =
                latitude - xMoveDistance) {
            LatLng latLng;
            if (slope != Double.MAX_VALUE) {
                latLng = new LatLng(latitude, (latitude - intercept) / slope);
            } else {
                latLng = new LatLng(latitude, lastPoint.longitude);
            }
            mMoveMarker.setPosition(latLng);
        }
    }
    @Override
    protected void initData() {
        List<LatLng> trackPoints = new ArrayList<>();
        startTime = getIntent().getLongExtra("start",0);
        endTime = getIntent().getLongExtra("end",0);
        HistoryTrackRequest historyTrackRequest = new HistoryTrackRequest();
        App.getInstance().initRequest(historyTrackRequest);
        historyTrackRequest.setEntityName(kithEntity.getKithCustCode());
        historyTrackRequest.setStartTime(startTime);
        historyTrackRequest.setEndTime(endTime);
        historyTrackRequest.setPageIndex(1);
        historyTrackRequest.setPageSize(10000);
        historyTrackRequest.setProcessed(true);
        //historyTrackRequest.setLowSpeedThreshold(5);
        ProcessOption processOption = new ProcessOption();
      //  processOption.setRadiusThreshold(50);
        processOption.setNeedDenoise(true);//去噪处理
        processOption.setNeedMapMatch(true);//绑路处理
        processOption.setTransportMode(TransportMode.auto);
        historyTrackRequest.setProcessOption(processOption);
        historyTrackRequest.setCoordTypeOutput(CoordType.bd09ll);
        App.getInstance().mClient.queryHistoryTrack(historyTrackRequest, new OnTrackListener() {
            @Override
            public void onHistoryTrackCallback(HistoryTrackResponse response) {
                if (StatusCodes.SUCCESS != response.getStatus()) {
                    return;
                } else if (0 == response.getTotal()) {

                } else {
                    List<TrackPoint> points = response.getTrackPoints();
                    if (null != points) {
                        for (TrackPoint trackPoint : points) {
                            if (!CommonUtil.isZeroPoint(trackPoint.getLocation().getLatitude(),
                                    trackPoint.getLocation().getLongitude())) {
                                trackPoints.add(MapUtil.convertTrace2Map(trackPoint.getLocation()));
                            }
                        }
                    }
                }
                drawHistoryTrack(trackPoints, SortType.asc);
            }
        });
        mBinding.userNvi.setOnClickListener(view -> {
            Intent intent = new Intent(mContext, MapRouteSelecter2Activity.class);
            intent.putExtra("KithEntity",kithEntity);
            startActivity(intent);
        });
    }


    @Override
    public void onResume() {
        super.onResume();
        if (mBinding!=null && mBinding.mapview!=null)
            mBinding.mapview.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mBinding!=null && mBinding.mapview!=null)
            mBinding.mapview.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mBaiduMap.clear();
        mBinding.mapview.onDestroy();
        mapStatusUpdate = null;
    }

    /**
     * 绘制历史轨迹
     */
    public void drawHistoryTrack(List<LatLng> points, SortType sortType) {
        // 绘制新覆盖物前，清空之前的覆盖物
        mBaiduMap.clear();
        if (points == null || points.size() == 0) {
            if (null != polylineOverlay) {
                polylineOverlay.remove();
                polylineOverlay = null;
            }
            return;
        }

        if (points.size() == 1) {
            OverlayOptions startOptions = new MarkerOptions().position(points.get(0)).icon(BitmapDescriptorFactory.fromResource(R.mipmap.start_point))
                    .zIndex(9).draggable(true);
            mBaiduMap.addOverlay(startOptions);
            animateMapStatus(points.get(0), 18.0f);
            return;
        }

        LatLng startPoint;
        LatLng endPoint;
        if (sortType == SortType.asc) {
            startPoint = points.get(0);
            endPoint = points.get(points.size() - 1);
        } else {
            startPoint = points.get(points.size() - 1);
            endPoint = points.get(0);
        }

        // 添加起点图标
        OverlayOptions startOptions = new MarkerOptions()
                .position(startPoint).icon(BitmapDescriptorFactory.fromResource(R.mipmap.start_point))
                .zIndex(9).draggable(true);
        // 添加终点图标
        OverlayOptions endOptions = new MarkerOptions().position(endPoint)
                .icon(BitmapDescriptorFactory.fromResource(R.mipmap.end_point)).zIndex(9).draggable(true);

        // 添加路线（轨迹）
        OverlayOptions polylineOptions = new PolylineOptions().width(10)
                .color(Color.BLUE).points(points);

        mBaiduMap.addOverlay(startOptions);
        mBaiduMap.addOverlay(endOptions);
        polylineOverlay = mBaiduMap.addOverlay(polylineOptions);

        OverlayOptions markerOptions =
                new MarkerOptions().flat(true).anchor(0.5f, 0.5f).icon(BitmapUtil.bmArrowPoint)
                        .position(points.get(points.size() - 1))
                        .rotate((float) CommonUtil.getAngle(points.get(0), points.get(1)));
        mMoveMarker = (Marker) mBaiduMap.addOverlay(markerOptions);

        animateMapStatus(points);
    }
    public void animateMapStatus(List<LatLng> points) {
        if (null == points || points.isEmpty()) {
            return;
        }
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        for (LatLng point : points) {
            builder.include(point);
        }
        MapStatusUpdate msUpdate = MapStatusUpdateFactory.newLatLngBounds(builder.build());
        mBaiduMap.animateMapStatus(msUpdate);
    }

    public void animateMapStatus(LatLng point, float zoom) {
        MapStatus.Builder builder = new MapStatus.Builder();
        mapStatus = builder.target(point).zoom(zoom).build();
        mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(mapStatus));
    }
}