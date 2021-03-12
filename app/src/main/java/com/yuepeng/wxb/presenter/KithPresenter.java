package com.yuepeng.wxb.presenter;

import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.wstro.thirdlibrary.base.BasePresenter;
import com.wstro.thirdlibrary.base.BaseResponse;
import com.wstro.thirdlibrary.base.PageEntity;
import com.wstro.thirdlibrary.entity.KithEntity;
import com.yuepeng.wxb.presenter.view.KithDetailView;

import org.greenrobot.eventbus.EventBus;

import rx.Subscriber;

/**
 * @author:create by Nico
 * company:余舒科技
 * createTime:1/31/21
 * Email:752497253@qq.com
 */
public class KithPresenter extends BasePresenter<KithDetailView> {

    public KithPresenter(KithDetailView view) {
        super(view);
    }

    public void getKithList(int page,int pageSize){
        map.put("page",page);
        map.put("pageSize",pageSize);
        addSubscription(mApiService.getKithList(getBody(map)), new Subscriber<BaseResponse<PageEntity<KithEntity>>>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                mView.onError(e);
            }

            @Override
            public void onNext(BaseResponse<PageEntity<KithEntity>> baseResponse) {
                //Log.d("KithEntity2:", JSON.toJSONString(baseResponse));
                if (baseResponse.getCode() == SUCCESSCODE){
                    Log.d("KithEntity3:", JSON.toJSONString(baseResponse));
                    mView.onGetKithListSuccess(baseResponse.getData().getRecords());
                }else {
                    Log.d("KithEntity4:", JSON.toJSONString(baseResponse));
                    mView.onFailed(baseResponse.getMsg());
                }
                if (page < baseResponse.getData().getPages()){
                    mView.onFinishRefreshAndLoadMore();
                }else {
                    EventBus.getDefault().post(baseResponse.getData().getRecords());
                    mView.onFinishRefreshAndLoadMoreWithNoMoreData();
                }
                Log.d("KithEntity2:", JSON.toJSONString(baseResponse));
            }
        });
    }

    public void delFriend(int custKithId){
        addSubscription(mApiService.delFriend(custKithId), new Subscriber<BaseResponse>() {
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
                    mView.onDelFriendSuccess(custKithId);
                }else {
                    mView.onFailed(baseResponse.getMsg());
                }
            }
        });
    }

    public void updateFriendWithNote(int custKithId,String note){
        map.put("custKithId",custKithId);
        map.put("kithNote",note);
        addSubscription(mApiService.updateFriend(getBody(map)), new Subscriber<BaseResponse>() {
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
                    mView.onUpdateNoteSuccess(custKithId,note);
                }else {
                    mView.onFailed(baseResponse.getMsg());
                }
            }
        });
    }

    public void updateFriendWithHide(int custKithId,boolean needHide){
        map.put("custKithId",custKithId);
        map.put("hideLocation",needHide);
        addSubscription(mApiService.updateFriend(getBody(map)), new Subscriber<BaseResponse>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                mView.onError(e);
                mView.onUpdateLocationHideFail("修改失败",custKithId,needHide);
            }

            @Override
            public void onNext(BaseResponse baseResponse) {
                if (baseResponse.getCode() == SUCCESSCODE){
                    mView.onUpdateLocationHideSuccess(custKithId,needHide);
                }else {
                    mView.onUpdateLocationHideFail(baseResponse.getMsg(),custKithId,needHide);
                }
            }
        });
    }
}
