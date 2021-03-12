package com.yuepeng.wxb.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import com.alibaba.fastjson.JSON;
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
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.PoiInfo;
import com.blankj.utilcode.util.LogUtils;
import com.gyf.immersionbar.ImmersionBar;
import com.wstro.thirdlibrary.base.BaseResponse;
import com.wstro.thirdlibrary.entity.SafetyEntity;
import com.wstro.thirdlibrary.event.ActivityEvent;
import com.wstro.thirdlibrary.event.EventCode;
import com.xuexiang.xui.widget.dialog.materialdialog.DialogAction;
import com.xuexiang.xui.widget.dialog.materialdialog.MaterialDialog;
import com.yuepeng.wxb.R;
import com.yuepeng.wxb.adapter.RadiusAdapter;
import com.yuepeng.wxb.base.BaseActivity;
import com.yuepeng.wxb.databinding.ActivityAddSafetyPlaceBinding;
import com.yuepeng.wxb.presenter.SafetyPlacePresent;
import com.yuepeng.wxb.presenter.view.SafetyDetailView;
import com.yuepeng.wxb.utils.CommonUtil;
import com.yuepeng.wxb.utils.PreUtils;

import org.greenrobot.eventbus.EventBus;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;

import static com.yuepeng.wxb.ui.activity.MapSearchActivity.RESPONSE_MAP_SEARCH_CODE;

public class AddSafetyPlaceActivity extends BaseActivity<ActivityAddSafetyPlaceBinding, SafetyPlacePresent> implements SafetyDetailView {

    private List<Integer> list;
    private RadiusAdapter adapter;
    private BaiduMap mBaiduMap;
    private LatLng cneter;
    public LatLng lastPoint = null;
    private Marker mMoveMarker = null;
    // private LocationClient mLocClient;
    public final int REQUEST_MAP_SEARCH_CODE = 11112;
    private PoiInfo poiItem;
    private int raduis = 100;

    private MapStatusUpdate mapStatusUpdate;
    private SafetyEntity safetyEntity;

    @Override
    protected View injectTarget() {
        return null;
    }

    @Override
    protected SafetyPlacePresent createPresenter() {
        return new SafetyPlacePresent(this);
    }

    @Override
    protected void Retry() {

    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_add_safety_place;
    }

    @Override
    public void onRightClick(View v) {
        String name = mBinding.etAdrName.getText().toString().trim();
        if (TextUtils.isEmpty(name)){
            toast("请输入地点名称");
            return;
        }
        if (safetyEntity == null){
            toast("请选择地址");
            return;
        }
        raduis = list.get(adapter.getOldIndex());
        safetyEntity.setRemindRange(raduis);
        safetyEntity.setAreaAlias(name);
        mPresenter.addSafeArea(safetyEntity);
    }

    @Override
    protected void initView() {
        ImmersionBar.with(this).keyboardEnable(true).titleBar(mBinding.title.titlebar);
        mBinding.title.titlebar.setTitle("添加位置");
        mBinding.title.titlebar.setRightTitle("下一步");
        mSwipeBackHelper.setIsOnlyTrackingLeftEdge(true);
        mBinding.tvAdrSel.setOnClickListener(view -> {
            Intent intent = new Intent(AddSafetyPlaceActivity.this, MapSearchActivity.class);
            int oldIndex = adapter.getOldIndex();
            raduis = list.get(oldIndex);
            intent.putExtra("radius", raduis);
            intent.putExtra("latlng", cneter);
            startActivityForResult(intent,REQUEST_MAP_SEARCH_CODE);

        });
        Bundle bundle = getIntent().getBundleExtra("bundle");
        Serializable serializable = bundle.getSerializable("SAFETIENTITY");
        if (serializable !=null){
            safetyEntity = (SafetyEntity) serializable;
            if (TextUtils.isEmpty(safetyEntity.getAddressLat())){
                cneter = PreUtils.getLocation();
            }else {
                cneter = new LatLng(Double.parseDouble(safetyEntity.getAddressLat()),Double.parseDouble(safetyEntity.getAddressLng()));
            }
            if (!TextUtils.isEmpty(safetyEntity.getAreaAlias()))
                mBinding.etAdrName.setText(safetyEntity.getAreaAlias());
            if (!TextUtils.isEmpty(safetyEntity.getAreaName()))
                mBinding.tvAdrSel.setText(safetyEntity.getAreaName());
            raduis = safetyEntity.getRemindRange();
            if (safetyEntity.getSafeAreaId()==null ||safetyEntity.getSafeAreaId() == 0){
                mBinding.llAddItem.setVisibility(View.VISIBLE);
                mBinding.rlItem.setVisibility(View.GONE);
            }
            if (!TextUtils.isEmpty(safetyEntity.getAreaAddress()))
                mBinding.tvAddresss.setText(safetyEntity.getAreaAddress());
        }else {
            cneter = PreUtils.getLocation();
            mBinding.llAddItem.setVisibility(View.VISIBLE);
            mBinding.rlItem.setVisibility(View.GONE);
        }
        mBaiduMap = mBinding.mapview.getMap();
        mapStatusUpdate = MapStatusUpdateFactory.newLatLngZoom(cneter, 18.0f);
        mBaiduMap.animateMapStatus(mapStatusUpdate);
        MapStatus.Builder builder = new MapStatus.Builder();
        builder.target(cneter);
        MapStatusUpdate newMapStatus = MapStatusUpdateFactory.newMapStatus(builder.build());
        mBaiduMap.setMapStatus(newMapStatus);
       addMarker(cneter);



        LinearLayoutManager lm = new LinearLayoutManager(this);
        lm.setOrientation(LinearLayoutManager.HORIZONTAL);
        mBinding.rv.setLayoutManager(lm);
        list = new ArrayList<>();
        list.add(100);
        list.add(500);
        list.add(1000);
        list.add(2000);
        list.add(5000);
        adapter = new RadiusAdapter(list,raduis);
        mBinding.rv.setAdapter(adapter);
        hideSoftKeyboard();

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
        mBinding.btnDelete.setOnClickListener(view -> {
            new MaterialDialog.Builder(this)
                    .title("温馨提示")
                    .content("确定删除该安全区域吗？")
                    .positiveText("确定")
                    .positiveColor(getResources().getColor(R.color.appThemeColor))
                    .negativeColor(getResources().getColor(R.color.adadad))
                    .negativeText("取消")
                    .onPositive(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                            if (safetyEntity == null || safetyEntity.getSafeAreaId()==null ||safetyEntity.getSafeAreaId() == 0){
                                toast("数据错误");
                                return;
                            }
                            mPresenter.deleteSafety(safetyEntity.getSafeAreaId());
                            dialog.dismiss();
                        }
                    })
                    .show();
        });
        mBinding.btnUpdate.setOnClickListener(view -> {
            mBinding.llAddItem.setVisibility(View.VISIBLE);
            mBinding.rlItem.setVisibility(View.GONE);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_MAP_SEARCH_CODE && resultCode == RESPONSE_MAP_SEARCH_CODE && data!=null ){
            poiItem = data.getParcelableExtra("PoiItem");
            mBinding.tvAdrSel.setText(poiItem.getName());
            LogUtils.d("PoiItem:"+ JSON.toJSONString(poiItem));
            addMarker(poiItem.getLocation());
            mapStatusUpdate = MapStatusUpdateFactory.newLatLngZoom(poiItem.getLocation(), 18.0f);
            mBaiduMap.animateMapStatus(mapStatusUpdate);
            if (safetyEntity == null) {
                safetyEntity = new SafetyEntity();
            }
            safetyEntity.setAddressLat(poiItem.getLocation().latitude+"");
            safetyEntity.setAddressLng(poiItem.getLocation().longitude+"");
            safetyEntity.setAreaAddress(poiItem.getAddress());
            safetyEntity.setAreaName(poiItem.getName());

        }
    }

    @Override
    public void onGetSafetyPlaceSuccess(List<SafetyEntity> safetyList) {

    }

    @Override
    public void onAddSafetyPlaceSuccess() {
        showSuccessDialog("添加成功");
    }

    @Override
    public void onEditSafetyPlaceSuccess() {
        showSuccessDialog("修改成功");
    }

    @Override
    public void onDelSafetyPlaceSuccess() {
        showSuccessDialog("已删除");
    }

    @Override
    public void onSuccess() {

    }

    @Override
    public void onError(Throwable e) {

    }

    @Override
    public void onfailed(BaseResponse baseResponse) {
        showErrorDialog(baseResponse);
    }


    @Override
    public void doOnDismiss() {
        super.doOnDismiss();
        EventBus.getDefault().post(new ActivityEvent(EventCode.SafePlace.UPDATE,true));
        finish();
    }


}