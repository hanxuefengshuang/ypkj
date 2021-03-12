package com.yuepeng.wxb.presenter;

import com.wstro.thirdlibrary.base.BasePresenter;
import com.wstro.thirdlibrary.base.BaseResponse;
import com.wstro.thirdlibrary.entity.EmergencyMsgEntity;
import com.yuepeng.wxb.presenter.view.EmergencyMsgDetailView;

import rx.Subscriber;

/**
 * @author:create by Nico
 * company:余舒科技
 * createTime:2/19/21
 * Email:752497253@qq.com
 */
public class EmergencyMsgPresenter extends BasePresenter<EmergencyMsgDetailView> {

    public EmergencyMsgPresenter(EmergencyMsgDetailView view) {
        super(view);
    }

    public void getEmergencyMsg(){
        addSubscription(mApiService.getEmergencyMsgTemp(), new Subscriber<BaseResponse<EmergencyMsgEntity>>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                mView.onError(e);
            }

            @Override
            public void onNext(BaseResponse<EmergencyMsgEntity> emergencyMsgEntityBaseResponse) {
                if (emergencyMsgEntityBaseResponse.getCode() == SUCCESSCODE){
                    mView.onGetEmergencyMsgSuccess(emergencyMsgEntityBaseResponse.getData());
                }else {
                    mView.onfailed(emergencyMsgEntityBaseResponse);
                }
            }
        });
    }
}
