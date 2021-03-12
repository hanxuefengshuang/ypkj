package com.yuepeng.wxb.presenter;

import com.wstro.thirdlibrary.base.BasePresenter;
import com.wstro.thirdlibrary.base.BaseResponse;
import com.yuepeng.wxb.presenter.view.AddFriendDetailView;

import rx.Subscriber;

/**
 * @author:create by Nico
 * company:余舒科技
 * createTime:2/7/21
 * Email:752497253@qq.com
 */
public class AddFriendPresenter extends BasePresenter<AddFriendDetailView> {

    public AddFriendPresenter(AddFriendDetailView view) {
        super(view);
    }

    public void checkIfIsRegister(String mobile){
        map.put("mobile",mobile);
        addSubscription(mApiService.checkIfIsRegister(getBody(map)), new Subscriber<BaseResponse>() {
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
                    mView.onCheckResultWithNoBind(mobile);
                }
                else if (baseResponse.getCode() == 1502){
                    mView.onCheckResultWithNotRegister(mobile);
                }else {
                    mView.onCheckFailed(mobile,baseResponse.getMsg());
                }
            }
        });
    }

    public void addFriend(String mobile){
        map.put("invitedCustMobile",mobile);
        addSubscription(mApiService.addFriend(getBody(map)), new Subscriber<BaseResponse>() {
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
                   mView.onInviteSuccess();
               }else {
                   mView.onfailed(baseResponse);
               }
            }
        });
    }
}
