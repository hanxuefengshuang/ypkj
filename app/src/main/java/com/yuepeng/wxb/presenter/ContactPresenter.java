package com.yuepeng.wxb.presenter;

import com.wstro.thirdlibrary.base.BasePresenter;
import com.wstro.thirdlibrary.base.BaseResponse;
import com.wstro.thirdlibrary.entity.ContactEntity;
import com.yuepeng.wxb.presenter.view.ContactDetailView;

import java.util.List;

import rx.Subscriber;

/**
 * @author:create by Nico
 * company:余舒科技
 * createTime:2/6/21
 * Email:752497253@qq.com
 */
public class ContactPresenter extends BasePresenter<ContactDetailView> {

    public ContactPresenter(ContactDetailView view) {
        super(view);
    }

    public void getContactList(){
        addSubscription(mApiService.getCustContactList(), new Subscriber<BaseResponse<List<ContactEntity>>>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                mView.onError(e);
            }

            @Override
            public void onNext(BaseResponse<List<ContactEntity>> response) {
                if (response.getCode() == SUCCESSCODE){
                    mView.onGetContactListSuccess(response.getData());
                }else {
                    mView.onfailed(response);
                }
            }
        });
    }

    public void addCustContact(String contactMobile,String contactName){
        map.put("contactMobile",contactMobile);
        map.put("contactName",contactName);
        addSubscription(mApiService.addCustContact(getBody(map)), new Subscriber<BaseResponse>() {
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
                    mView.onAddCustContactSuccess();
                }else {
                    mView.onfailed(baseResponse);
                }
            }
        });
    }

    public void editCustContact(int custContactId,String contactMobile,String contactName){
        map.put("contactMobile",contactMobile);
        map.put("contactName",contactName);
        map.put("custContactId",custContactId);
        addSubscription(mApiService.editCustContact(getBody(map)), new Subscriber<BaseResponse>() {
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
                    mView.onEditCustContactSuccess();
                }else {
                    mView.onfailed(baseResponse);
                }
            }
        });
    }

    public void delCustContact(int custContactId){
        addSubscription(mApiService.delCustContact(custContactId), new Subscriber<BaseResponse>() {
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
                    mView.onDelCustContactSuccess();
                }else {
                    mView.onfailed(baseResponse);
                }
            }
        });
    }

    public void addSeekHelpRecord(String seekAddress,String seekLat,String seekLng){
        map.put("seekAddress",seekAddress);
        map.put("seekLat",seekLat);
        map.put("seekLng",seekLng);
        addSubscription(mApiService.addSeekHelpRecord(getBody(map)), new Subscriber<BaseResponse>() {
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
                    mView.onAddSeekHelpSuccess();
                }else {
                    mView.onfailed(baseResponse);
                }
            }
        });
    }
}
