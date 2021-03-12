package com.yuepeng.wxb.ui.activity;

import android.view.View;

import com.wstro.thirdlibrary.base.BaseResponse;
import com.wstro.thirdlibrary.entity.EmergencyMsgEntity;
import com.yuepeng.wxb.R;
import com.yuepeng.wxb.base.BaseActivity;
import com.yuepeng.wxb.databinding.ActivityDirectionsForUseBinding;
import com.yuepeng.wxb.presenter.EmergencyMsgPresenter;
import com.yuepeng.wxb.presenter.view.EmergencyMsgDetailView;

public class DirectionsForUseActivity extends BaseActivity<ActivityDirectionsForUseBinding, EmergencyMsgPresenter> implements EmergencyMsgDetailView {


    @Override
    protected View injectTarget() {
        return null;
    }

    @Override
    protected EmergencyMsgPresenter createPresenter() {
        return new EmergencyMsgPresenter(this);
    }

    @Override
    protected void Retry() {

    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_directions_for_use;
    }

    @Override
    protected void initView() {
        mBinding.title.titlebar.setTitle("使用说明");
    }

    @Override
    protected void initData() {
        mPresenter.getEmergencyMsg();
    }

    @Override
    public void onGetEmergencyMsgSuccess(EmergencyMsgEntity emergencyMsgEntity) {
        mBinding.content.setText(emergencyMsgEntity.getTempContent());
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
}