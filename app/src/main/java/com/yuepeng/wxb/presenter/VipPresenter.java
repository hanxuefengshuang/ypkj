package com.yuepeng.wxb.presenter;

import com.alibaba.fastjson.JSON;
import com.blankj.utilcode.util.LogUtils;
import com.wstro.thirdlibrary.base.BasePresenter;
import com.wstro.thirdlibrary.base.BaseResponse;
import com.wstro.thirdlibrary.entity.MemberEntity;
import com.wstro.thirdlibrary.entity.UserModel;
import com.wstro.thirdlibrary.entity.VipNumEntity;
import com.yuepeng.wxb.base.App;
import com.yuepeng.wxb.presenter.view.VipDetailView;

import java.util.List;

import rx.Subscriber;

/**
 * @author:create by Nico
 * company:余舒科技
 * createTime:2/4/21
 * Email:752497253@qq.com
 */
public class VipPresenter extends BasePresenter<VipDetailView> {

    public VipPresenter(VipDetailView view) {
        super(view);
    }

    public void getVipNumDetail(){
        addSubscription(mApiService.getVipNum(), new Subscriber<BaseResponse<VipNumEntity>>(){
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                mView.onError(e);
            }

            @Override
            public void onNext(BaseResponse<VipNumEntity> response) {
                LogUtils.d("VipNumEntity:"+ JSON.toJSONString(response));
                if (response.getCode() == SUCCESSCODE){
                    mView.onGetMemberNumSuccess(response.getData());
                }else {
                    mView.onfailed(response);
                }
            }
        });
    }

    public void getMemberList(){
        LogUtils.d("getMemberList");
        int[]normal = new int[]{1} ;
//        gson.toJson(normal);
        map.put("priceTypes",normal);
        addSubscription(mApiService.getMemberList(getBody(map)), new Subscriber<BaseResponse<List<MemberEntity>>>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                e.printStackTrace();
               // LogUtils.d("MemberEntity:"+ e.getMessage());
                mView.onError(e);
            }

            @Override
            public void onNext(BaseResponse<List<MemberEntity>> response) {
                LogUtils.d("MemberEntity:"+ JSON.toJSONString(response));
                if (response.getCode() == SUCCESSCODE){
                    mView.onGetMemberListSuccess(response.getData().get(0).getPriceList());
                }else {
                    mView.onfailed(response);
                }
            }
        });
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
