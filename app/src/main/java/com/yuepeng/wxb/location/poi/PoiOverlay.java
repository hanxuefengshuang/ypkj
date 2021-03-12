package com.yuepeng.wxb.location.poi;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.Polyline;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.poi.PoiResult;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.wstro.thirdlibrary.entity.KithEntity;
import com.wstro.thirdlibrary.widget.RoundImageView;
import com.yuepeng.wxb.R;
import com.yuepeng.wxb.location.overlay.OverlayManager;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * 用于显示poi的overly
 */
public class PoiOverlay extends OverlayManager {

    private static final int MAX_POI_SIZE = 10;
    private Handler handler;
    private List<KithEntity> mPoiResult = null;
    private OnPoiClickListener onPoiClickListener;
    private LayoutInflater mInflater;
    private int newPoiPos = -1;  //新点击覆盖物
    private int oldPoiPos = -2;  //新点击覆盖物
    private List<OverlayOptions> markerList;


    public void setOnPoiClickListener(OnPoiClickListener onPoiClickListener) {
        this.onPoiClickListener = onPoiClickListener;
    }

    /**
     * 构造函数
     *
     * @param baiduMap   该 PoiOverlay 引用的 BaiduMap 对象
     */
    public PoiOverlay(BaiduMap baiduMap, LayoutInflater mInflater) {
        super(baiduMap);
        this.mInflater = mInflater;
        handler = new Handler();
    }

    /**
     * 设置POI数据
     * 
     * @param poiResult    设置POI数据
     */
    public void setData(List<KithEntity>  poiResult) {
        this.mPoiResult = poiResult;
    }

    @Override
    public final List<OverlayOptions> getOverlayOptions(BaiduMap mBaiduMap) {
        if (mPoiResult == null || mPoiResult.size()<1) {
            return null;
        }
        markerList = new ArrayList<>();
        //int markerSize = 0;

        for (int i = 0; i < mPoiResult.size() ; i++) {
            if (TextUtils.isEmpty(mPoiResult.get(i).getLat()) || TextUtils.isEmpty(mPoiResult.get(i).getLng())) {
                continue;
            }

            //markerSize++;
            Bundle bundle = new Bundle();
            bundle.putInt("index", i);
            KithEntity poiInfo = mPoiResult.get(i);
            //String station = poiInfo.name + "站";
            LatLng latLng = new LatLng(Double.parseDouble(poiInfo.getLat()),Double.parseDouble(poiInfo.getLng()));
            View view;
            view = mInflater.inflate(R.layout.map_marker, null, false);
            final RoundImageView iv = view.findViewById(R.id.iv_image);
            int finalI = i;
            Glide.with(mInflater.getContext()).load(poiInfo.getHeadImg()).apply(new RequestOptions()
            .error(R.mipmap.ic_launcher_round)
            .placeholder(R.mipmap.ic_launcher_round).circleCrop()
            ).into(new SimpleTarget<Drawable>() {
                @Override
                public void onLoadFailed(@Nullable Drawable errorDrawable) {

                }

                @Override
                public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {

                        if (handler == null)
                            handler = new Handler();
                        handler.postDelayed(()->{
                            if (mOverlayList!=null && mOverlayList.size()>finalI) {
                                iv.setImageDrawable(resource);
                                Marker marker = (Marker) mOverlayList.get(finalI);
                                marker.getIcon().recycle();
                                marker.setIcon(BitmapDescriptorFactory.fromView(view));
                                mOverlayList.set(finalI, marker);
                            }
                        },2000);

                }


            });
            BitmapDescriptor bitmapDescriptor = BitmapDescriptorFactory.fromView(view);
            MarkerOptions markerOptions = new MarkerOptions()
                    .animateType(MarkerOptions.MarkerAnimateType.none)
                    .icon(bitmapDescriptor)
                    .extraInfo(bundle)
                    .position(latLng);
            markerList.add(markerOptions);
        }
        return markerList;
    }


    /**
     * 覆写此方法以改变默认点击行为
     * 
     * @param i    被点击的poi在
     *             {@link PoiResult#getAllPoi()} 中的索引
     * @return     true--事件已经处理，false--事件未处理
     */
    public boolean onPoiClick(int i) {
        if (mPoiResult!= null && mPoiResult.size()>i
                && mPoiResult.get(i) != null) {
//            newPoiPos = i;
//            if (oldPoiPos == newPoiPos)
//                return false;
//            else {
                KithEntity poiInfo = mPoiResult.get(i);
                if (onPoiClickListener!=null)
                    onPoiClickListener.onPoiClick(poiInfo);
//                oldPoiPos = newPoiPos;
//            }
        }
        return false;
    }

    @Override
    public final boolean onMarkerClick(Marker marker) {
        if (mOverlayList == null || !mOverlayList.contains(marker)) {
            return false;
        }

        if (marker.getExtraInfo() != null) {
            return onPoiClick(marker.getExtraInfo().getInt("index"));
        }

        return false;
    }

    @Override
    public boolean onPolylineClick(Polyline polyline) {
        return false;
    }

    public void clear() {
        if (mOverlayList !=null){
            for (int i = 0; i < mOverlayList.size(); i++) {
                mOverlayList.get(i).remove();
            }
            mOverlayList.clear();
        }
    }

    /**
     * Poi marker地址被点击监听回调
     */
    public interface OnPoiClickListener{
        void onPoiClick(KithEntity info);
    }
}
