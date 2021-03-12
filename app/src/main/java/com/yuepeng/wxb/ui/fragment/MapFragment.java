package com.yuepeng.wxb.ui.fragment;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.LocationManager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;

import com.baidu.location.BDLocation;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.CustomMapStyleCallBack;
import com.baidu.mapapi.map.MapCustomStyleOptions;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;
import com.baidu.trace.api.entity.OnEntityListener;
import com.baidu.trace.api.track.LatestPointRequest;
import com.baidu.trace.api.track.LatestPointResponse;
import com.baidu.trace.api.track.OnTrackListener;
import com.baidu.trace.model.LocationMode;
import com.baidu.trace.model.OnTraceListener;
import com.baidu.trace.model.ProcessOption;
import com.baidu.trace.model.PushMessage;
import com.baidu.trace.model.StatusCodes;
import com.baidu.trace.model.TransportMode;
import com.blankj.utilcode.util.LogUtils;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.gyf.immersionbar.ImmersionBar;
import com.lxj.xpopup.XPopup;
import com.tbruyelle.rxpermissions2.RxPermissions;
import com.wstro.thirdlibrary.base.BaseResponse;
import com.wstro.thirdlibrary.entity.KithEntity;
import com.wstro.thirdlibrary.event.ActivityEvent;
import com.wstro.thirdlibrary.event.EventCode;
import com.wstro.thirdlibrary.event.FragmentEvent;
import com.wstro.thirdlibrary.utils.AppCache;
import com.xuexiang.xui.widget.dialog.DialogLoader;
import com.xuexiang.xui.widget.dialog.MiniLoadingDialog;
import com.yuepeng.wxb.R;
import com.yuepeng.wxb.base.App;
import com.yuepeng.wxb.base.BaseFragment;
import com.yuepeng.wxb.databinding.FragmentMapBinding;
import com.yuepeng.wxb.location.BDLocationRegister;
import com.yuepeng.wxb.location.BNDemoUtils;
import com.yuepeng.wxb.location.BaiduLocationUtil;
import com.yuepeng.wxb.location.poi.MyPoiOverlay;
import com.yuepeng.wxb.location.poi.PoiOverlay;
import com.yuepeng.wxb.presenter.MapPresenter;
import com.yuepeng.wxb.presenter.view.MapDetailView;
import com.yuepeng.wxb.ui.activity.AddFriendActivity;
import com.yuepeng.wxb.ui.activity.ContactActivity;
import com.yuepeng.wxb.ui.activity.HisSportActivity;
import com.yuepeng.wxb.ui.activity.MapRouteSelecter2Activity;
import com.yuepeng.wxb.ui.activity.NewMsgActivity;
import com.yuepeng.wxb.ui.activity.PlayBellActivity;
import com.yuepeng.wxb.ui.pop.GPSSetPop;
import com.yuepeng.wxb.utils.BitmapUtil;
import com.yuepeng.wxb.utils.MapUtil;
import com.yuepeng.wxb.utils.PreUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

/**
 * @author:create by Nico
 * createTime:1/30/21
 * Email:752497253@qq.com
 */
public class MapFragment extends BaseFragment<FragmentMapBinding, MapPresenter> implements
        BDLocationRegister.OnLocationListener, MapDetailView,
        SensorEventListener, PoiOverlay.OnPoiClickListener {

    private BaiduMap mBaiduMap;
    private MyLocationData myLocationData;
    private LatLng center;
    private boolean isFirstLoc = true;
    private double lastX;
    private float mCurrentAccracy = 50;
    private double mCurrentLat;
    private double mCurrentLon;

    private boolean isClickLoc = false;//是否点击定位
    private LayoutInflater mInflater;
    private com.wstro.thirdlibrary.entity.KithEntity meKithEntity = null;
    private KithEntity KithEntity = null;
    private MapUtil mapUtil;

    /**
     * 轨迹服务监听器
     */
    private OnTraceListener traceListener = new OnTraceListener(){
        /**
         * 绑定服务回调接口
         * @param errorNo
         * @param message
         */
        @Override
        public void onBindServiceCallback(int errorNo, String message) {
            LogUtils.d(String.format("onBindServiceCallback, errorNo:%d, message:%s ", errorNo, message));
        }
        //开启服务回调接口
        @Override
        public void onStartTraceCallback(int errorNo, String message) {
            LogUtils.d(String.format("onStartTraceCallback, errorNo:%d, message:%s ", errorNo, message));
            if (errorNo == StatusCodes.SUCCESS){
                App.getInstance().mClient.startGather(traceListener);

            }
        }

        @Override
        public void onStopTraceCallback(int i, String s) {
            LogUtils.d(String.format("onStopTraceCallback, errorNo:%d, message:%s ", i, s));
        }

        @Override
        public void onStartGatherCallback(int i, String s) {
            LogUtils.d(String.format("onStartGatherCallback, errorNo:%d, message:%s ", i, s));
        }

        @Override
        public void onStopGatherCallback(int i, String s) {
            LogUtils.d(String.format("onStopGatherCallback, errorNo:%d, message:%s ", i, s));
        }

        @Override
        public void onPushCallback(byte b, PushMessage pushMessage) {

        }

        @Override
        public void onInitBOSCallback(int i, String s) {
            LogUtils.d(String.format("onInitBOSCallback, errorNo:%d, message:%s ", i, s));
        }
    };

    /**
     * Entity监听器(用于接收实时定位回调)
     */
    private MyPoiOverlay overlay;
    public  static final int REQUEST_CODE_OPEN_GPS = 11119;
    private GPSSetPop gpsSetPop;


    @Override
    protected int onBindLayout() {
        return R.layout.fragment_map;
    }

    @Override
    protected MapPresenter createPresenter() {
        return new MapPresenter(this);
    }
    private MiniLoadingDialog loadingDialog;
    protected int mCurrentDirection = 0;    //地图方向
    public static boolean isPermissionRequested = false;

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
    public void onDestroyView() {
        super.onDestroyView();
        mapUtil.clear();
        if (overlay!= null)
            overlay.clear();
        mBinding.mapview.onDestroy();
        mapUtil = null;
        EventBus.getDefault().unregister(this);

    }

    @Override
    protected void initView() {
        ImmersionBar.with(this)
                .statusBarDarkFont(true)
                .navigationBarDarkIcon(true)
                .init();
        BitmapUtil.init();
        mInflater = LayoutInflater.from(getActivity());
        if (!EventBus.getDefault().isRegistered(this))
        EventBus.getDefault().register(this);
        mBaiduMap = mBinding.mapview.getMap();
        // 开启定位图层，一定不要少了这句，否则对在地图的设置、绘制定位点将无效
        mBaiduMap.setMyLocationEnabled(true);
        mapUtil = MapUtil.getInstance();
        mapUtil.init(mBinding.mapview);
        mBinding.userSport.setOnClickListener(view1 -> {        //我的轨迹
            if (meKithEntity == null){
                toast("请稍后");
                return;
            }
            Intent intent = new Intent(getActivity(), HisSportActivity.class);
            intent.putExtra("KithEntity",meKithEntity);
            startActivity(intent);
        });
        mBinding.userHisLocation.setOnClickListener(view1 -> {      //好友轨迹
            if (KithEntity == null){
                toast("请稍后");
                return;
            }

            Intent intent = new Intent(getActivity(), HisSportActivity.class);
            intent.putExtra("KithEntity",KithEntity);
            startActivity(intent);
        });
        mBinding.userAlarm.setOnClickListener(view1 -> {
           openActivity(PlayBellActivity.class);
        });

        mBinding.userCall.setOnClickListener(view1 -> {
            toast("敬请期待");
        });

        mBinding.userNvi.setOnClickListener(view->{
            if (KithEntity == null){
                toast("请稍后");
                return;
            }
            Intent intent = new Intent(getActivity(), MapRouteSelecter2Activity.class);
            intent.putExtra("KithEntity",KithEntity);
            startActivity(intent);
        });
        mBinding.cMsg.setOnClickListener(view1 -> {
            openActivityForResult(NewMsgActivity.class,1);
        });

        mBinding.ivLocation.setOnClickListener(view1 ->{
            isClickLoc = true;
            mBinding.itemFriend.setVisibility(View.GONE);
            mBinding.itemFirst.setVisibility(View.VISIBLE);
            if (center == null){
               LatLng latLng = PreUtils.getLocation();
               if (latLng !=null && latLng.latitude!=0 && latLng.longitude!=0){
                   center = latLng;
               }
            }
            changeMapLocation(center);
            startLocation();
        } );

        mBinding.userFriendLocation.setOnClickListener(view1 -> {
            if (AppCache.HaveFriend){
                EventBus.getDefault().post(new ActivityEvent(EventCode.Main.GOTOFRIENDLIST));
            }else {
                openActivity(AddFriendActivity.class);
            }
        });

        mBinding.userSos.setOnClickListener(view1 -> {
            openActivity(ContactActivity.class);
        });
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

        startLocation();
    }

    public void startRealTimeLoc() {
        //设置定位模式
        App.getInstance().mClient.setLocationMode(LocationMode.High_Accuracy);
        // 设置定位和打包周期
        App.getInstance().mClient.setInterval(5, 10);
        App.getInstance().mClient.startTrace(App.getInstance().mTrace, traceListener);
    }

    public void stopRealTimeLoc() {
        App.getInstance().mClient.stopRealTimeLoc();
    }

    private void startLocation() {
        new RxPermissions(this).requestEach(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
        ).subscribe(permission -> {
            if (permission.granted) {
                // 用户已经同意该权限
                if (checkGPSIsOpen()) {
                    BaiduLocationUtil.startBaiduLocation(getActivity());
                    BaiduLocationUtil.getBDLocationListener().setOnlocationListener(MapFragment.this);
                }else {
                    if (gpsSetPop == null || !gpsSetPop.isShow())
                    gpsSetPop = (GPSSetPop) new XPopup.Builder(getActivity())
                            .autoOpenSoftInput(true)
                            .asCustom(new GPSSetPop(getActivity())
                            .show());
                }
            } else if (permission.shouldShowRequestPermissionRationale) {
                // 用户拒绝了该权限，没有选中『不再询问』（Never ask again）,那么下次再次启动时。还会提示请求权限的对话框
                //Log.d(TAG, permission.name + " is denied. More info should be provided.");
            } else {
                // 用户拒绝了该权限，而且选中『不再询问』
                //Log.d(TAG, permission.name + " is denied.");
                String msg;
                if (permission.name.equals(Manifest.permission.ACCESS_COARSE_LOCATION)
                        || permission.name.equals(Manifest.permission.ACCESS_FINE_LOCATION)) {
                    msg = "在设置-应用-位寻宝-权限中开启位置信息权限，以正常使用位置定位功能！";
                    DialogLoader.getInstance().showConfirmDialog(getContext(), "提示", msg, "跳转", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            //TODO跳转权限页面
                        }
                    }, "取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    });

                }
            }
        });
    }

    @Override
    protected void Retry() {

    }

    @Override
    protected View injectTarget() {
        return null;
    }

    @Override
    public void initData() {
        if (PreUtils.getLocation()!=null && PreUtils.getLocation().longitude!=0 && PreUtils.getLocation().latitude!=0) {
            center = PreUtils.getLocation();
            changeMapLocation(center);
        }
        mPresenter.getMessageList();
    }

    @Override
    protected boolean enableSimplebar() {
        return false;
    }

    @Override
    protected boolean isStatusBarEnabled() {
        return false;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        double x = event.values[SensorManager.DATA_X];
        if (Math.abs(x - lastX) > 1.0) {
            mCurrentDirection = (int) x;
            myLocationData = new MyLocationData.Builder()
                    .accuracy(mCurrentAccracy)
                    // 此处设置开发者获取到的方向信息，顺时针0-360
                    .direction(mCurrentDirection).latitude(mCurrentLat)
                    .longitude(mCurrentLon).build();
            mBaiduMap.setMyLocationData(myLocationData);
        }
        lastX = x;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int event) {
    }

    /**
     * 定位成功
     * @param bdLocation
     */
    @Override
    public void sucessLocation(BDLocation bdLocation) {
        if (getContext() == null || mBinding == null)
            return;
        mCurrentAccracy = bdLocation.getRadius();
        mCurrentLat = bdLocation.getLatitude();
        mCurrentLon = bdLocation.getLongitude();

        if (mBaiduMap == null)
            return;
        center = new LatLng(bdLocation.getLatitude(), bdLocation.getLongitude());
        mBinding.userLocation.setText(bdLocation.getAddress().address);
        PreUtils.putAddress(bdLocation);
        //mPresenter.addLocation(bdLocation.getLatitude(),bdLocation.getLongitude(),PreUtils.getAddress());
    }

    private void changeMapLocation(LatLng latLng) {
        if (latLng!=null && latLng.longitude!= 0  && latLng.longitude!= 0) {
            mapUtil.setCurrentAccracy(mCurrentAccracy);
            mapUtil.setCenter(latLng);
        }
    }

    @Override
    public void failedLocation(String msg) {
        isClickLoc = false;
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
    public void dismissProgressDialog() {

    }

    @Override
    public void onPoiClick(KithEntity info) {
        mBinding.itemFirst.setVisibility(View.GONE);
        mBinding.itemFriend.setVisibility(View.VISIBLE);
        mBinding.userName.setText(info.getKithNote());
        Glide.with(getActivity()).load(info.getHeadImg()).apply(new RequestOptions().circleCrop()).into(mBinding.avatar);
        String distance = BNDemoUtils.getDistance(center.longitude, center.latitude, Double.parseDouble(info.getLng()), Double.parseDouble(info.getLat()));
        int v = (int) (Double.parseDouble(distance) * 1000);
        if (v<500){
            mBinding.tvDistance.setText("距我" + v + "m | " + info.getLocation());
        }else {
            mBinding.tvDistance.setText("距我" + distance + "km | " + info.getLocation());
        }
        KithEntity = info;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void getKithEntity(List<KithEntity> poiResult){
        if (poiResult == null  || poiResult.size()<1) {
            return;
        }
        meKithEntity = poiResult.get(0);
        mBaiduMap.clear();
        overlay = new MyPoiOverlay( mBaiduMap, mInflater);
        mBaiduMap.setOnMarkerClickListener(overlay);
        overlay.setOnPoiClickListener(this);
        List<KithEntity> list = new ArrayList<>();
        for (int i = 0; i < poiResult.size() ; i++) {
            if (i >0){
                if (!TextUtils.isEmpty(poiResult.get(i).getLat()) && !TextUtils.isEmpty(poiResult.get(i).getLng()))
                list.add(poiResult.get(i));
            }
        }
        overlay.setData(list);
        overlay.addToMap();


        mapUtil.setCenter(center);
        overlay.zoomToSpan(center);

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void getKithEntity(FragmentEvent event){
        if (event.getCode() == EventCode.Main.ALREADYSAVEUSERINFO){
            App.getInstance().initBaiduTrace();
            startRealTimeLoc();
        }
    }


    @Override
    public void onGetMessageListSuccess(int totalMsg) {
        if (PreUtils.getInt(PreUtils.KEY_READ_MSG_COUNT)<totalMsg){
            mBinding.haveNewMsg.setVisibility(View.VISIBLE);
        }else {
            mBinding.haveNewMsg.setVisibility(View.GONE);
        }
    }

    private boolean checkGPSIsOpen() {//检测gps是否打开
        if (getActivity() == null) {
            return false;
        }
        LocationManager locationManager = (LocationManager) getActivity().getSystemService(Context
                .LOCATION_SERVICE);
        return locationManager != null && locationManager.isProviderEnabled(LocationManager
                .GPS_PROVIDER);
    }

    @Override
    public void onEvent(FragmentEvent event) {
        super.onEvent(event);
        switch (event.getCode()){
            case EventCode.Main.CANCELREAD:
                mBinding.haveNewMsg.setVisibility(View.GONE);
                break;
        }
    }
}
