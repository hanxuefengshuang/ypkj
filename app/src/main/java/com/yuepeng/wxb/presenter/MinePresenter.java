package com.yuepeng.wxb.presenter;

import com.wstro.thirdlibrary.base.BaseDetailView;
import com.wstro.thirdlibrary.base.BasePresenter;
import com.wstro.thirdlibrary.base.BaseResponse;
import com.wstro.thirdlibrary.entity.UserModel;
import com.yuepeng.wxb.base.App;

import rx.Subscriber;

/**
 * @author:create by Nico
 * company:余舒科技
 * createTime:2/1/21
 * Email:752497253@qq.com
 */
public class MinePresenter extends BasePresenter<BaseDetailView> {

    public MinePresenter(BaseDetailView view) {
        super(view);
    }

    public void getUserInfo(){
        addSubscription(mApiService.getUserInfo(), new Subscriber<BaseResponse<UserModel>>() {
            @Override
            public void onCompleted() {
            }

            @Override
            public void onError(Throwable e) {
                mView.onError(e);
            }

            @Override
            public void onNext(BaseResponse<UserModel> baseResponse) {
                if (baseResponse.getCode() == SUCCESSCODE){
                    App.getInstance().saveUserModel(baseResponse.getData());
                    mView.onSuccess();
                }else {
                    mView.onfailed(baseResponse);
                }
            }
        });
    }
}
