package com.yuepeng.wxb.entity;

import com.baidu.mapapi.model.LatLng;
import com.wstro.thirdlibrary.base.BaseDetailView;
import com.wstro.thirdlibrary.base.BasePresenter;
import com.wstro.thirdlibrary.base.BaseResponse;
import com.yuepeng.wxb.utils.PreUtils;

import rx.Subscriber;

/**
 * @author:create by Nico
 * createTime:2/11/21
 * Email:752497253@qq.com
 */
public class AddBellPresenter extends BasePresenter<BaseDetailView> {

    public AddBellPresenter(BaseDetailView view) {
        super(view);
    }

    public void addBellLog(){
        LatLng latLng = PreUtils.getLocation();
        String address = PreUtils.getAddress();
        map.put("warningLat",latLng.latitude);
        map.put("warningLng",latLng.longitude);
        map.put("warningAddress",address);
        addSubscription(mApiService.addBellLog(getBody(map)), new Subscriber<BaseResponse>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                mView.onError(e);
            }

            @Override
            public void onNext(BaseResponse baseResponse) {
                if (baseResponse.getCode() == SUCCESSCODE){
                    mView.onSuccess();
                }else {
                    mView.onfailed(baseResponse);
                }
            }
        });
    }
}
