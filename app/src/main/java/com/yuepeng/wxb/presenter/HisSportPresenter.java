package com.yuepeng.wxb.presenter;

import com.alibaba.fastjson.JSON;
import com.baidu.trace.api.analysis.OnAnalysisListener;
import com.baidu.trace.api.analysis.StayPointRequest;
import com.baidu.trace.api.track.HistoryTrackRequest;
import com.baidu.trace.api.track.OnTrackListener;
import com.baidu.trace.model.CoordType;
import com.baidu.trace.model.ProcessOption;
import com.baidu.trace.model.TransportMode;
import com.blankj.utilcode.util.LogUtils;
import com.wstro.thirdlibrary.base.BasePresenter;
import com.wstro.thirdlibrary.base.BaseResponse;
import com.wstro.thirdlibrary.entity.HisTimeResponse;
import com.wstro.thirdlibrary.entity.TjDateResponse;
import com.yuepeng.wxb.base.App;
import com.yuepeng.wxb.presenter.view.HisDetailView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import rx.Subscriber;

/**
 * Created by wangjun on 2021/2/7.
 */
public class HisSportPresenter  extends BasePresenter<HisDetailView> {
    public HisSportPresenter(HisDetailView view) {
        super(view);
    }

    public void getTjdate(String custKithId){
        LogUtils.d("custKithId："+custKithId);
        addSubscription(mApiService.getTjdate(custKithId,1,1000), new Subscriber<BaseResponse<HisTimeResponse>>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                LogUtils.d("AddLocation:"+e.getMessage());
                mView.onError(e);
            }

            @Override
            public void onNext(BaseResponse<HisTimeResponse> baseResponse) {
                LogUtils.d("Tjdate:"+ JSON.toJSONString(baseResponse));
                if (baseResponse.getCode() == SUCCESSCODE){
                    HisTimeResponse data = baseResponse.getData();
                    if (data!=null && data.getRecords().size()>0) {
                        mView.onTjDateSuccess(data);
//                        for (int i = 0; i < data.getRecords().size(); i++) {
//                            getTjdateList(data.getRecords().get(i).getKtihTjDateId());
//                        }
                    }
                    // mView.onGetCodeSuccess(baseResponse.getData().toString());
                }else {
                    // mView.onfailed(baseResponse.getMsg());
                }
            }
        });

    }

    public void getTjdateList(long kithTjDateId){
        addSubscription(mApiService.getTjdateList(kithTjDateId), new Subscriber<BaseResponse<List<TjDateResponse>>>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                LogUtils.d("AddLocation:"+e.getMessage());
                mView.onError(e);
            }

            @Override
            public void onNext(BaseResponse<List<TjDateResponse>> baseResponse) {
                LogUtils.d("TjdateDate:"+ JSON.toJSONString(baseResponse));
                if (baseResponse.getCode() == SUCCESSCODE){
                    mView.onTjDateListSuccess(baseResponse.getData());
                }else {
                    // mView.onfailed(baseResponse.getMsg());
                }
            }
        });

    }

    /**
     * 获取历史轨迹
     * @param tjDateResponse
     * @param custKithId
     * @param trackListener
     */
    public void getHisSport(long startTime,long endTime, String custKithId, OnTrackListener trackListener) {
        HistoryTrackRequest historyTrackRequest = new HistoryTrackRequest();
        App.getInstance().initRequest(historyTrackRequest);
        historyTrackRequest.setEntityName(custKithId+"");
        historyTrackRequest.setStartTime(startTime);
        historyTrackRequest.setEndTime(endTime);
        historyTrackRequest.setPageIndex(1);
        historyTrackRequest.setPageSize(1000);
        historyTrackRequest.setProcessed(true);
        historyTrackRequest.setLowSpeedThreshold(5);
        ProcessOption processOption = new ProcessOption();
        //processOption.setTransportMode(TransportMode.driving);
        historyTrackRequest.setProcessOption(processOption);
        historyTrackRequest.setCoordTypeOutput(CoordType.bd09ll);
        App.getInstance().mClient.queryHistoryTrack(historyTrackRequest, trackListener);
    }

    public void getHisStay(TjDateResponse tjDateResponse,String custKithId,OnAnalysisListener analysisListener){
        StayPointRequest stayPointRequest = new StayPointRequest();
        App.getInstance().initRequest(stayPointRequest);
        stayPointRequest.setStayTime(60*60);
        stayPointRequest.setStayRadius(50);
        stayPointRequest.setCoordTypeOutput(CoordType.bd09ll);
        stayPointRequest.setEntityName(custKithId+"");
        stayPointRequest.setStartTime(dateToStamp(tjDateResponse.getKithTjBeginTime())/1000);
        stayPointRequest.setEndTime(dateToStamp(tjDateResponse.getKithTjEndTime())/1000);
        ProcessOption processOption = new ProcessOption();
        stayPointRequest.setProcessOption(processOption);
        App.getInstance().mClient.queryStayPoint(stayPointRequest,analysisListener);
    }

    /*
     * 将时间转换为时间戳
     */
    public long dateToStamp(String s) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = null;
        try {
            date = simpleDateFormat.parse(s);
            return date.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return 0;

    }

   // public void get
}