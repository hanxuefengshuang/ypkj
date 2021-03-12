package com.yuepeng.wxb.presenter;

import android.text.TextUtils;

import com.baidu.mapapi.search.core.PoiInfo;
import com.wstro.thirdlibrary.base.BasePresenter;
import com.wstro.thirdlibrary.base.BaseResponse;
import com.wstro.thirdlibrary.entity.SafetyEntity;
import com.yuepeng.wxb.presenter.view.SafetyDetailView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import rx.Subscriber;

/**
 * @author:create by Nico
 * createTime:2/5/21
 * Email:752497253@qq.com
 */
public class SafetyPlacePresent extends BasePresenter<SafetyDetailView> {

    public SafetyPlacePresent(SafetyDetailView view) {
        super(view);
    }

    public void getSafetyPlaceList(){
        addSubscription(mApiService.getSafeAreaList(), new Subscriber<BaseResponse<List<SafetyEntity>>>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                mView.onError(e);
            }

            @Override
            public void onNext(BaseResponse<List<SafetyEntity>> response) {
                if (response.getCode() == SUCCESSCODE){
                    mView.onGetSafetyPlaceSuccess(response.getData());
                }else {
                    mView.onfailed(response);
                }
            }
        });
    }


    public void addSafeArea(PoiInfo poiItem, String name, int raduis, String safeAreaId) {
        Map<String,Object> map = new HashMap<>();
        map.put("addressLat",poiItem.getLocation().latitude);
        map.put("addressLng",poiItem.getLocation().longitude);
        map.put("areaAddress",poiItem.getAddress());
        map.put("areaAlias",name);
        map.put("areaName",poiItem.getName());
        map.put("remindRange",raduis);
        if (!TextUtils.isEmpty(safeAreaId))
            map.put("safeAreaId",safeAreaId);

        addSubscription(mApiService.addSafeArea(getBody(map)), new Subscriber<BaseResponse>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                mView.onError(e);
            }

            @Override
            public void onNext(BaseResponse response) {
                if (response.getCode() == SUCCESSCODE){
                    mView.onSuccess();
                }else {
                    mView.onfailed(response);
                }
            }
        });
    }

    public void addSafeArea(SafetyEntity safetyEntity) {
        Map<String,Object> map = new HashMap<>();
        map.put("addressLat",safetyEntity.getAddressLat());
        map.put("addressLng",safetyEntity.getAddressLng());
        map.put("areaAddress",safetyEntity.getAreaAddress());
        map.put("areaAlias",safetyEntity.getAreaAlias());
        map.put("areaName",safetyEntity.getAreaName());
        map.put("remindRange",safetyEntity.getRemindRange());
        if (safetyEntity.getSafeAreaId()==null || safetyEntity.getSafeAreaId() == 0) {
            addSubscription(mApiService.addSafeArea(getBody(map)), new Subscriber<BaseResponse>() {
                @Override
                public void onCompleted() {

                }

                @Override
                public void onError(Throwable e) {
                    mView.onError(e);
                }

                @Override
                public void onNext(BaseResponse response) {
                    if (response.getCode() == SUCCESSCODE) {
                        mView.onAddSafetyPlaceSuccess();
                    } else {
                        mView.onfailed(response);
                    }
                }
            });
        }else {
            map.put("safeAreaId", safetyEntity.getSafeAreaId());
            addSubscription(mApiService.updateSafeArea(getBody(map)), new Subscriber<BaseResponse>() {
                @Override
                public void onCompleted() {

                }

                @Override
                public void onError(Throwable e) {
                    mView.onError(e);
                }

                @Override
                public void onNext(BaseResponse response) {
                    if (response.getCode() == SUCCESSCODE) {
                        mView.onEditSafetyPlaceSuccess();
                    } else {
                        mView.onfailed(response);
                    }
                }
            });
        }
    }

    public void deleteSafety(Integer safeAreaId) {
        Map<String,Object> map = new HashMap<>();
        map.put("safeAreaId",safeAreaId);
        addSubscription(mApiService.deleteSafety(safeAreaId), new Subscriber<BaseResponse>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                mView.onError(e);
            }

            @Override
            public void onNext(BaseResponse response) {
                if (response.getCode() == SUCCESSCODE){
                    mView.onDelSafetyPlaceSuccess();
                }else {
                    mView.onfailed(response);
                }
            }
        });
    }
}
