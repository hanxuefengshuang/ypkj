package com.yuepeng.wxb.presenter;

import com.blankj.utilcode.util.LogUtils;
import com.wstro.thirdlibrary.base.BasePresenter;
import com.wstro.thirdlibrary.base.BaseResponse;
import com.wstro.thirdlibrary.entity.MessageEntity;
import com.wstro.thirdlibrary.entity.MessageResultDate;
import com.wstro.thirdlibrary.entity.MessageResultEntity;
import com.yuepeng.wxb.presenter.view.MapDetailView;

import rx.Subscriber;

/**
 * Created by wangjun on 2021/2/4.
 */
public class MapPresenter extends BasePresenter<MapDetailView> {
    public MapPresenter(MapDetailView view) {
        super(view);
    }

    /**
     * 上传地址
     * @param lat
     * @param lon
     * @param location
     */
    public void addLocation(double lat,double lon,String location){
        LogUtils.d("addLocation:lat"+lat);
        LogUtils.d("addLocation:lon"+lon);
        LogUtils.d("addLocation:location"+location);
        map.put("lat",String.valueOf(lat));
        map.put("lng",String.valueOf(lon));
        map.put("location",location);
        //body.put("body",map);
        addSubscription(mApiService.addLocation(getBody(map)), new Subscriber<BaseResponse>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                LogUtils.d("AddLocation:"+e.getMessage());
                mView.onError(e);
            }

            @Override
            public void onNext(BaseResponse baseResponse) {
                LogUtils.d("AddLocation:"+baseResponse.getMsg());
                if (baseResponse.getCode() == SUCCESSCODE){
                    // mView.onGetCodeSuccess(baseResponse.getData().toString());
                }else {
                    // mView.onfailed(baseResponse.getMsg());
                }
            }
        });
    }

    public void getMessageList(){
        map.put("page",1);
        map.put("pageSize",10);
        addSubscription(mApiService.getMessageList(getBody(map)), new Subscriber<BaseResponse<MessageResultEntity<MessageResultDate<MessageEntity>>>>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                mView.onError(e);
            }

            @Override
            public void onNext(BaseResponse<MessageResultEntity<MessageResultDate<MessageEntity>>> response) {
                if (response.getCode() == SUCCESSCODE){
                    mView.onGetMessageListSuccess(response.getData().getTotal());
                }else {
                    mView.onfailed(response);
                }
            }
        });
    }
}
