package com.yuepeng.wxb.ui.pop;

import android.content.Context;
import android.view.View;

import com.wstro.thirdlibrary.base.BaseResponse;
import com.wstro.thirdlibrary.entity.MemberEntity;
import com.wstro.thirdlibrary.entity.WeChatPayEntity;
import com.wstro.thirdlibrary.event.ActivityEvent;
import com.wstro.thirdlibrary.event.EventCode;
import com.yuepeng.wxb.R;
import com.yuepeng.wxb.base.App;
import com.yuepeng.wxb.base.MyBaseBottomPop;
import com.yuepeng.wxb.databinding.PopChoosePaywayBinding;
import com.yuepeng.wxb.presenter.PayPresenter;
import com.yuepeng.wxb.presenter.view.PayDetailView;

import org.greenrobot.eventbus.EventBus;

import androidx.annotation.NonNull;

/**
 * @author:create by Nico
 * createTime:2/4/21
 * Email:752497253@qq.com
 */
public class ChoosePayWayPop extends MyBaseBottomPop<PopChoosePaywayBinding, PayPresenter> implements View.OnClickListener, PayDetailView {

    private MemberEntity.PriceListdata memberData;
    private int payType = 1;

    public ChoosePayWayPop(@NonNull Context context, MemberEntity.PriceListdata memberData) {
        super(context);
        this.memberData = memberData;
    }

    @Override
    protected PayPresenter createPresenter() {
        return new PayPresenter(this);
    }

    @Override
    protected int getImplLayoutId() {
        return R.layout.pop_choose_payway;
    }

    @Override
    protected void onCreate() {
        super.onCreate();
        mBinding.price.setText("¥ "+memberData.getPresentPrice());
        mBinding.rlAliPay.setOnClickListener(this);
        mBinding.rlWechatPay.setOnClickListener(this);
        mBinding.btnPay.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.rlAliPay){
            mBinding.ivAliChecked.setVisibility(View.VISIBLE);
            mBinding.ivWechatChecked.setVisibility(View.GONE);
            payType = 1;
        }
        if (id == R.id.rlWechatPay){
            mBinding.ivAliChecked.setVisibility(View.GONE);
            mBinding.ivWechatChecked.setVisibility(View.VISIBLE);
            payType = 2;
        }
        if (id == R.id.btnPay){
            mPresenter.getPayToken();
        }
    }

    @Override
    public void onGetPayTokenSuccess(String msg) {
        int custId = App.getInstance().getUserModel().getCustId();
        if (payType == 1){
            mPresenter.getAlipayOrder(custId,memberData.getPresentPrice(),memberData.getPriceId(),msg);
        }else {
            mPresenter.getWechatPayOrder(custId,memberData.getPresentPrice(),memberData.getPriceId(),msg);
        }
    }

    @Override
    public void onGetAlipayOrderSuccess(String aliOrder) {
        dismiss();
        EventBus.getDefault().post(new ActivityEvent(EventCode.Pay.ALIPAY,aliOrder));
    }

    @Override
    public void onGetWechatPayOrderSuccess(WeChatPayEntity wechatOrder) {
        dismiss();
        EventBus.getDefault().post(new ActivityEvent(EventCode.Pay.WECHATPAY,wechatOrder));
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

    }


    @Override
    public void showProgressDialog() {

    }

    @Override
    public void dismissProgressDialog() {

    }


}
