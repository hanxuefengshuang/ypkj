package com.yuepeng.wxb.ui.activity;

import android.os.Bundle;
import android.view.View;

import com.lxj.xpopup.XPopup;
import com.wstro.thirdlibrary.base.BaseResponse;
import com.wstro.thirdlibrary.entity.SafetyEntity;
import com.wstro.thirdlibrary.event.ActivityEvent;
import com.wstro.thirdlibrary.event.EventCode;
import com.yuepeng.wxb.R;
import com.yuepeng.wxb.adapter.SafetyPlaceAdapter;
import com.yuepeng.wxb.base.App;
import com.yuepeng.wxb.base.BaseActivity;
import com.yuepeng.wxb.databinding.ActivitySafetyPlaceBinding;
import com.yuepeng.wxb.presenter.SafetyPlacePresent;
import com.yuepeng.wxb.presenter.view.SafetyDetailView;
import com.yuepeng.wxb.ui.pop.OpenVipPop;

import java.util.ArrayList;
import java.util.List;

import androidx.recyclerview.widget.LinearLayoutManager;

public class SafetyPlaceActivity extends BaseActivity<ActivitySafetyPlaceBinding, SafetyPlacePresent> implements SafetyDetailView, SafetyPlaceAdapter.onClickAddSafetyPlaceListener {

    private List<SafetyEntity>allSafetyList = new ArrayList<>();
    private SafetyPlaceAdapter mAdapter;
    private int memberType = 0;
    private OpenVipPop openVipPop;

    @Override
    protected View injectTarget() {
        return null;
    }

    @Override
    protected SafetyPlacePresent createPresenter() {
        return new SafetyPlacePresent(this);
    }

    @Override
    protected void Retry() {

    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_safety_place;
    }

    @Override
    protected void initView() {
        mAdapter = new SafetyPlaceAdapter(allSafetyList);
        mBinding.rv.setLayoutManager(new LinearLayoutManager(this));
        mBinding.rv.setAdapter(mAdapter);
        memberType = App.getInstance().getUserModel().getMemberType();
        mAdapter.setOnClickAddSafetyPlaceListener(this);
        memberType = App.getInstance().getUserModel().getMemberType();

    }
    @Override
    protected void initData() {
        mPresenter.getSafetyPlaceList();
    }

    @Override
    public void onGetSafetyPlaceSuccess(List<SafetyEntity> safetyList) {
        mAdapter.setNewInstance(safetyList);
    }

    @Override
    public void onAddSafetyPlaceSuccess() {
//        showSuccessDialog("添加成功");
    }

    @Override
    public void onEditSafetyPlaceSuccess() {
//        showSuccessDialog("修改成功");
    }

    @Override
    public void onDelSafetyPlaceSuccess() {
//        showSuccessDialog("已删除");
    }

    @Override
    public void onSuccess() {

    }

    @Override
    public void onError(Throwable e) {
        showException(e);
    }

    @Override
    public void onfailed(BaseResponse baseResponse) {
        showErrorDialog(baseResponse);
    }


    @Override
    public void onClickAddSafetyPlace(int position) {
        if (memberType == 0){
            openVipPop = (OpenVipPop) new XPopup.Builder(this)
                    .dismissOnBackPressed(false)
                    .dismissOnTouchOutside(false)
                    .asCustom(new OpenVipPop(this))
                    .show();
            return;
        }else {
            Bundle bundle = new Bundle();
            if (mAdapter.getData() != null && mAdapter.getData().get(position).getSafeAreaId()!=null) {
                bundle.putSerializable("SAFETIENTITY", mAdapter.getData().get(position));
            }
                openActivity(AddSafetyPlaceActivity.class, bundle);
        }
    }

    @Override
    public void onEvent(ActivityEvent event) {
        super.onEvent(event);
        switch (event.getCode()){
            case EventCode.SafePlace.UPDATE:
                mPresenter.getSafetyPlaceList();
                break;
        }

    }
}