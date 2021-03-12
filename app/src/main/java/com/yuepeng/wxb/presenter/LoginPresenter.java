package com.yuepeng.wxb.presenter;

import com.wstro.thirdlibrary.base.BasePresenter;
import com.wstro.thirdlibrary.base.BaseResponse;
import com.wstro.thirdlibrary.entity.UserModel;
import com.wstro.thirdlibrary.utils.AppCache;
import com.yuepeng.wxb.base.App;
import com.yuepeng.wxb.presenter.view.LoginDetailView;
import com.yuepeng.wxb.utils.PreUtils;

import rx.Subscriber;

/**
 * @author:create by Nico
 * company:余舒科技
 * createTime:1/30/21
 * Email:752497253@qq.com
 */
public class LoginPresenter extends BasePresenter<LoginDetailView> {

    public LoginPresenter(LoginDetailView view) {
        super(view);
    }

    public void getCode(String mobile){
        map.put("mobile",mobile);
        body.put("body",map);
        addSubscription(mApiService.getCode(getBody(map)), new Subscriber<BaseResponse>() {
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
                    mView.onGetCodeSuccess(baseResponse.getData().toString());
                }else {
                    mView.onfailed(baseResponse);
                }
            }
        });
    }

    public void getLoginInfo(String code,String mobile){
        map.put("mobile",mobile);
        map.put("captcha",code);
        addSubscription(mApiService.getLoginInfo(getBody(map)), new Subscriber<BaseResponse<UserModel>>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                mView.onError(e);
            }

            @Override
            public void onNext(BaseResponse<UserModel> userModelBaseResponse) {
                if (userModelBaseResponse.getCode() == SUCCESSCODE){
                    App.getInstance().saveUserModel(userModelBaseResponse.getData());
                    PreUtils.putTokenInfo(userModelBaseResponse.getData().getToken());
                    AppCache.setAccessToken(userModelBaseResponse.getData().getToken());
                    AppCache.IsLogin = true;
                    mView.onGetLoginInfoSuccess();
                }else {
                    mView.onfailed(userModelBaseResponse);
                }
            }
        });
    }
}
