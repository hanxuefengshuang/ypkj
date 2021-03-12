package com.yuepeng.wxb.presenter;

import com.wstro.thirdlibrary.base.BasePresenter;
import com.wstro.thirdlibrary.base.BaseResponse;
import com.wstro.thirdlibrary.base.Constant;
import com.wstro.thirdlibrary.entity.WeChatPayEntity;
import com.wstro.thirdlibrary.utils.RSAUtils;
import com.yuepeng.wxb.presenter.view.PayDetailView;


import rx.Subscriber;

/**
 * @author:create by Nico
 * company:余舒科技
 * createTime:2/4/21
 * Email:752497253@qq.com
 */
public class PayPresenter extends BasePresenter<PayDetailView> {

    public PayPresenter(PayDetailView view) {
        super(view);
    }

    public void getPayToken(){
        addSubscription(mApiService.getPayToken(), new Subscriber<BaseResponse>() {
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
                    mView.onGetPayTokenSuccess(baseResponse.getData().toString());
                }else {
                    mView.onfailed(baseResponse);
                }
            }
        });
    }

    public void getAlipayOrder(int custId,double price,int priceId,String payToken){
        map.put("custId",custId);
        map.put("orderPrice",price);
        map.put("payType","ALI_PAY");
        map.put("priceId",priceId);
        map.put("timestamp",System.currentTimeMillis());
        String afterencrypt = RSAUtils.encrypt(Constant.PUBLICKEY,gson.toJson(map));
        map.clear();
        map.put("data",afterencrypt);
        map.put("payToken",payToken);
        addSubscription(mApiService.getAlipayOrder(getBody(map)), new Subscriber<BaseResponse>() {
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
                    mView.onGetAlipayOrderSuccess(baseResponse.getData().toString());
                }else {
                    mView.onfailed(baseResponse);
                }
            }
        });
    }

    public void getWechatPayOrder(int custId,double price,int priceId,String payToken){
        map.clear();
        map.put("custId",custId);
        map.put("orderPrice",price);
        map.put("payType","WECHAT_PAY");
        map.put("priceId",priceId);
        map.put("timestamp",System.currentTimeMillis());
        String after = RSAUtils.encrypt(Constant.PUBLICKEY,gson.toJson(map));
        map.clear();
        map.put("data",after);
        map.put("payToken",payToken);
        addSubscription(mApiService.getWechatPayOrder(getBody(map)), new Subscriber<BaseResponse<WeChatPayEntity>>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                mView.onError(e);
            }

            @Override
            public void onNext(BaseResponse<WeChatPayEntity> baseResponse) {
                if (baseResponse.getCode() == SUCCESSCODE){
                    mView.onGetWechatPayOrderSuccess(baseResponse.getData());
                }else {
                    mView.onfailed(baseResponse);
                }
            }
        });
    }
}
