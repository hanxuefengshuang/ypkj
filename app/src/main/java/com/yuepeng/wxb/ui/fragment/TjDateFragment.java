package com.yuepeng.wxb.ui.fragment;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;

import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeOption;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.baidu.trace.api.analysis.DrivingBehaviorResponse;
import com.baidu.trace.api.analysis.OnAnalysisListener;
import com.baidu.trace.api.analysis.StayPoint;
import com.baidu.trace.api.analysis.StayPointResponse;
import com.baidu.trace.api.track.HistoryTrackResponse;
import com.baidu.trace.api.track.OnTrackListener;
import com.baidu.trace.api.track.TrackPoint;
import com.baidu.trace.model.StatusCodes;
import com.blankj.utilcode.util.LogUtils;
import com.wstro.thirdlibrary.base.BaseResponse;
import com.wstro.thirdlibrary.entity.HisTimeResponse;
import com.wstro.thirdlibrary.entity.KithEntity;
import com.wstro.thirdlibrary.entity.TjDateResponse;
import com.yuepeng.wxb.R;
import com.yuepeng.wxb.adapter.HisSportAdapter;
import com.yuepeng.wxb.base.BaseFragment;
import com.yuepeng.wxb.databinding.FragmentTjdateBinding;
import com.yuepeng.wxb.entity.HisSportEntity;
import com.yuepeng.wxb.location.BNDemoUtils;
import com.yuepeng.wxb.presenter.HisSportPresenter;
import com.yuepeng.wxb.presenter.view.HisDetailView;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import androidx.fragment.app.Fragment;
import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by wangjun on 2021/2/8.
 */
public class TjDateFragment extends BaseFragment<FragmentTjdateBinding, HisSportPresenter>
    implements HisDetailView, OnGetGeoCoderResultListener {
    private HisSportAdapter adapter;
    private HisTimeResponse.RecordsBean record;
    private List<HisSportEntity> list;
    private OnAnalysisListener analysisListener =  new OnAnalysisListener() {
        @Override
        public void onStayPointCallback(StayPointResponse stayPointResponse) {
            long startTime;
            long endTime;
            HisSportEntity  hisSportEntity = new HisSportEntity();
            if (StatusCodes.SUCCESS != stayPointResponse.getStatus() || 0 == stayPointResponse.getStayPoints().size()) {
                startTime = mPresenter.dateToStamp(tjDateResponse.getKithTjBeginTime())/1000;
                endTime = mPresenter.dateToStamp(tjDateResponse.getKithTjEndTime())/1000;
                hisSportEntity.setStartTime(startTime);
                hisSportEntity.setEndTime(endTime);
                getHisTj(hisSportEntity);
                return;
            }


            for (int i = 0; i < stayPointResponse.getStayPoints().size(); i++) {
                hisSportEntity = new HisSportEntity();
                StayPoint stayPoint = stayPointResponse.getStayPoints().get(i);
                if (i == 0){
                    startTime = mPresenter.dateToStamp(tjDateResponse.getKithTjBeginTime())/1000;
                    endTime = stayPoint.getStartTime()+(60*60);
                    hisSportEntity.setEndLatLng(stayPoint.getLocation());
                    hisSportEntity.setStartTime(startTime);
                    hisSportEntity.setEndTime(endTime);
                    getHisTj(hisSportEntity);

                }else if (i == stayPointResponse.getStayPoints().size()-1){
                    startTime = stayPoint.getEndTime();
                    endTime = mPresenter.dateToStamp(tjDateResponse.getKithTjEndTime())/1000;
                    hisSportEntity.setStartLatLng(stayPoint.getLocation());
                    hisSportEntity.setStartTime(startTime-10);
                    hisSportEntity.setEndTime(endTime);
                    getHisTj(hisSportEntity);
                }else {
                    startTime = stayPoint.getEndTime();
                    StayPoint stayPoint2 = stayPointResponse.getStayPoints().get(i+1);
                    endTime = stayPoint2.getStartTime()+(60*60)-10;
                    hisSportEntity.setStartLatLng(stayPoint.getLocation());
                    hisSportEntity.setEndLatLng(stayPoint2.getLocation());
                    hisSportEntity.setStartTime(startTime);
                    hisSportEntity.setEndTime(endTime);
                    list.add(hisSportEntity);
                    mCoder.reverseGeoCode(new ReverseGeoCodeOption()
                            .location(new LatLng(hisSportEntity.getStartLatLng().getLatitude(), hisSportEntity.getStartLatLng().getLongitude()))
                            // 设置是否返回新数据 默认值0不返回，1返回
                            .newVersion(1)
                            // POI召回半径，允许设置区间为0-1000米，超过1000米按1000米召回。默认值为1000
                            .radius(100));
                    mCoder.reverseGeoCode(new ReverseGeoCodeOption()
                            .location(new LatLng(hisSportEntity.getEndLatLng().getLatitude(), hisSportEntity.getEndLatLng().getLongitude()))
                            // 设置是否返回新数据 默认值0不返回，1返回
                            .newVersion(1)
                            // POI召回半径，允许设置区间为0-1000米，超过1000米按1000米召回。默认值为1000
                            .radius(100));
                }

                if (stayPointResponse.getStayPoints().size()==1){
                    HisSportEntity hisSportEntity2 = new HisSportEntity();
                    startTime = stayPoint.getEndTime();
                    endTime = mPresenter.dateToStamp(tjDateResponse.getKithTjEndTime())/1000;
                    hisSportEntity2.setStartTime(startTime);
                    hisSportEntity2.setEndTime(endTime);
                    hisSportEntity2.setStartLatLng(stayPoint.getLocation());
                    getHisTj(hisSportEntity2);

                }
                LogUtils.d("HIS start:"+hisSportEntity.getStartTime() + " end:"+hisSportEntity.getEndTime());
            }
            adapter.notifyDataSetChanged();
        }

        private void getHisTj(HisSportEntity hisSportEntity) {
            list.add(hisSportEntity);
            mPresenter.getHisSport(hisSportEntity.getStartTime(), hisSportEntity.getEndTime(), kithEntity.getKithCustCode(), new OnTrackListener() {
                @Override
                public void onHistoryTrackCallback(HistoryTrackResponse historyTrackResponse) {
                    super.onHistoryTrackCallback(historyTrackResponse);
                    if (StatusCodes.SUCCESS != historyTrackResponse.getStatus()||historyTrackResponse.getTrackPoints().size()<2) {
                        if (list == null || list.size() == 1){  //只有一条直接剔除
                            assert list != null;
                            list.clear();
                            adapter.notifyDataSetChanged();
                        }else {
                            if (historyTrackResponse.getTrackPoints().size() == 1){ //单个轨迹点去除
                                TrackPoint trackPoint = historyTrackResponse.getTrackPoints().get(0);
                                for (int i = 0; i < list.size(); i++) {
                                    HisSportEntity sportEntity = list.get(i);
                                    if ( mPresenter.dateToStamp(trackPoint.getCreateTime())/1000>=sportEntity.getStartTime()&& mPresenter.dateToStamp(trackPoint.getCreateTime())/1000<sportEntity.getEndTime()) {
                                        list.remove(i);
                                    }
                                }
//                                TrackPoint trackPoint = historyTrackResponse.getTrackPoints().get(0);
//                                list.get(list.size()-1).setEndLatLng(trackPoint.getLocation());
//                                list.get(list.size()-1).setStartTime(mPresenter.dateToStamp(trackPoint.getCreateTime())/1000);
//                                list.get(list.size()-1).setEndTime(mPresenter.dateToStamp(trackPoint.getCreateTime())/1000);
//                                mCoder.reverseGeoCode(new ReverseGeoCodeOption()
//                                        .location(new LatLng(trackPoint.getLocation().getLatitude(),trackPoint.getLocation().getLongitude()))
//                                        // 设置是否返回新数据 默认值0不返回，1返回
//                                        .newVersion(1)
//                                        // POI召回半径，允许设置区间为0-1000米，超过1000米按1000米召回。默认值为1000
//                                        .radius(500));
                            }
                        }
                        return;
                    }
                    TrackPoint trackPoint = historyTrackResponse.getTrackPoints().get(0);
                    TrackPoint trackPoint2 = historyTrackResponse.getTrackPoints().get(historyTrackResponse.getTrackPoints().size()-1);
                    if ( mPresenter.dateToStamp(trackPoint.getCreateTime())/1000>=list.get(0).getStartTime()&& mPresenter.dateToStamp(trackPoint.getCreateTime())/1000<list.get(0).getEndTime()) {
                        list.get(0).setStartTime(mPresenter.dateToStamp(trackPoint.getCreateTime())/1000);
                        list.get(0).setEndTime(mPresenter.dateToStamp(trackPoint2.getCreateTime())/1000);
                        list.get(0).setStartLatLng(trackPoint.getLocation());
                        if (list.get(0).getEndLatLng() == null ){
                            list.get(0).setEndLatLng(trackPoint2.getLocation());
                        }
                    }else {
                        if (list.get(0).getStartLatLng() == null ){
                            list.get(0).setStartLatLng(trackPoint.getLocation());
                        }
                        list.get(list.size()-1).setEndLatLng(trackPoint2.getLocation());
                        list.get(list.size()-1).setStartTime(mPresenter.dateToStamp(trackPoint.getCreateTime())/1000);
                        list.get(list.size()-1).setEndTime(mPresenter.dateToStamp(trackPoint2.getCreateTime())/1000);
                    }

                    mCoder.reverseGeoCode(new ReverseGeoCodeOption()
                            .location(new LatLng(trackPoint.getLocation().getLatitude(),trackPoint.getLocation().getLongitude()))
                            // 设置是否返回新数据 默认值0不返回，1返回
                            .newVersion(1)
                            // POI召回半径，允许设置区间为0-1000米，超过1000米按1000米召回。默认值为1000
                            .radius(500));

                    mCoder.reverseGeoCode(new ReverseGeoCodeOption()
                            .location(new LatLng(trackPoint2.getLocation().getLatitude(),trackPoint2.getLocation().getLongitude()))
                            // 设置是否返回新数据 默认值0不返回，1返回
                            .newVersion(1)
                            // POI召回半径，允许设置区间为0-1000米，超过1000米按1000米召回。默认值为1000
                            .radius(500));

                    Iterator<HisSportEntity> iterator = list.iterator();
                    while (iterator.hasNext()){
                        count ++;
                        HisSportEntity next = iterator.next();
                        if (next.getStartTime() == next.getEndTime()){
                            iterator.remove();
                            adapter.notifyDataSetChanged();
                        }
                    }
                }
            });
        }

        @Override
        public void onDrivingBehaviorCallback(DrivingBehaviorResponse drivingBehaviorResponse) {

        }
    };


    private GeoCoder mCoder;
    private KithEntity kithEntity;
    private TjDateResponse tjDateResponse;


    public static Fragment getIntance(HisTimeResponse.RecordsBean recordsBean) {
        Bundle bundle = new Bundle();
        TjDateFragment tjDateFragment = new TjDateFragment();
        bundle.putSerializable("record",recordsBean);
        tjDateFragment.setArguments(bundle);
        return tjDateFragment;
    }

    @Override
    protected int onBindLayout() {
        return R.layout.fragment_tjdate;
    }

    @Override
    protected HisSportPresenter createPresenter() {
        return new HisSportPresenter(this);
    }

    @Override
    protected void initView() {
        record = (HisTimeResponse.RecordsBean) getArguments().getSerializable("record");
        mPresenter.getTjdateList(record.getKtihTjDateId());
        list = new ArrayList<>();
        adapter = new HisSportAdapter(list,getActivity());
        View view = LayoutInflater.from(getContext()).inflate(R.layout.layout_empty_redlist, null, false);
        adapter.setEmptyView(view);
        mBinding.rv.setAdapter(adapter);
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
        mCoder = GeoCoder.newInstance();
        mCoder.setOnGetGeoCodeResultListener(this);
        kithEntity = (KithEntity) getActivity().getIntent().getSerializableExtra("KithEntity");
    }

    @Override
    public void onTjDateSuccess(HisTimeResponse response) {

    }

    @Override
    public void onTjDateListSuccess(List<TjDateResponse> data) {
        //for (int i = 0; i < data.size(); i++) {
        if (data!=null && data.size()>0) {
            tjDateResponse = data.get(0);
            mPresenter.getHisStay(data.get(0), kithEntity.getKithCustCode(), analysisListener);
        }
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
    protected boolean isStatusBarEnabled() {
        return false;
    }

    @Override
    protected boolean enableSimplebar() {
        return false;
    }

    @Override
    protected boolean enableLazy() {
        return false;
    }
    int count = 0;
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mCoder.destroy();
    }

    @Override
    public void onGetGeoCodeResult(GeoCodeResult geoCodeResult) {
    }

    @Override
    public void onGetReverseGeoCodeResult(ReverseGeoCodeResult reverseGeoCodeResult) {
        getResultAddress(reverseGeoCodeResult);
    }

    private void getResultAddress(ReverseGeoCodeResult reverseGeoCodeResult) {
        count++;
        LogUtils.d("count:"+count);
        if (reverseGeoCodeResult == null || reverseGeoCodeResult.error != SearchResult.ERRORNO.NO_ERROR) {
            LogUtils.d("count:"+count + " error:"+reverseGeoCodeResult.error);
            //没有找到检索结果
            return;
        }

        Flowable.just(list)
                .map(hisSportEntities -> {
                    int ts = -1;
                    double resultDistance = 999999D;
                    double distance = 9999999D;
                    for (int i=0;i< hisSportEntities.size();i++) {
                        HisSportEntity hisSportEntity = hisSportEntities.get(i);
                        if (hisSportEntity.getStartLatLng()!=null){
                            distance = BNDemoUtils.getDistance2(hisSportEntity.getStartLatLng().longitude, hisSportEntity.getStartLatLng().latitude, reverseGeoCodeResult.getLocation().longitude, reverseGeoCodeResult.getLocation().latitude);
                            if (distance<500){
                                if (TextUtils.isEmpty(hisSportEntity.getStartAddress())){
                                    hisSportEntity.setStartdistance(distance);
                                    hisSportEntity.setStartAddress(reverseGeoCodeResult.getAddress());
                                }else {
                                    if (distance<hisSportEntity.getStartdistance()){
                                        hisSportEntity.setStartdistance(distance);
                                        hisSportEntity.setStartAddress(reverseGeoCodeResult.getAddress());
                                    }
                                }
                            }
                        }
                        if (hisSportEntity.getEndLatLng()!=null){
                            distance = BNDemoUtils.getDistance2(hisSportEntity.getEndLatLng().longitude, hisSportEntity.getEndLatLng().latitude, reverseGeoCodeResult.getLocation().longitude, reverseGeoCodeResult.getLocation().latitude);
                            if (distance<500){
                                if (TextUtils.isEmpty(hisSportEntity.getEndAddress())){
                                    hisSportEntity.setEnddistance(distance);
                                    hisSportEntity.setEndAddress(reverseGeoCodeResult.getAddress());
                                }else {
                                    if (distance<hisSportEntity.getEnddistance()){
                                        hisSportEntity.setEnddistance(distance);
                                        hisSportEntity.setEndAddress(reverseGeoCodeResult.getAddress());
                                    }
                                }
                            }
                        }
                        LogUtils.d("distance:"+distance + "  adr:"+reverseGeoCodeResult.getAddress());
                        if (resultDistance > distance && distance<500){
                            resultDistance = distance;
                            ts = i;
                        }
                    }
                    return ts;
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(ts -> {
                    if (ts!=-1){
                        adapter.notifyDataSetChanged();
                    }
                });
    }
}