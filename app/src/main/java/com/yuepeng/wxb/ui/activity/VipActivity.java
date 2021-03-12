package com.yuepeng.wxb.ui.activity;

import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.View;

import com.alibaba.fastjson.JSON;
import com.blankj.utilcode.util.LogUtils;
import com.lxj.xpopup.XPopup;
import com.wstro.thirdlibrary.base.BaseResponse;
import com.wstro.thirdlibrary.entity.MemberEntity;
import com.wstro.thirdlibrary.entity.UserModel;
import com.wstro.thirdlibrary.entity.VipNumEntity;
import com.wstro.thirdlibrary.entity.WeChatPayEntity;
import com.wstro.thirdlibrary.event.ActivityEvent;
import com.wstro.thirdlibrary.event.EventCode;
import com.wstro.thirdlibrary.event.FragmentEvent;
import com.wstro.thirdlibrary.utils.GlideUtils;
import com.xgr.alipay.alipay.AliPay;
import com.xgr.alipay.alipay.AlipayInfoImpli;
import com.xgr.easypay.EasyPay;
import com.xgr.easypay.callback.IPayCallback;
import com.xgr.wechatpay.wxpay.WXPay;
import com.xgr.wechatpay.wxpay.WXPayInfoImpli;
import com.yuepeng.wxb.R;
import com.yuepeng.wxb.base.App;
import com.yuepeng.wxb.base.BaseActivity;
import com.yuepeng.wxb.databinding.ActivityVipBinding;
import com.yuepeng.wxb.presenter.VipPresenter;
import com.yuepeng.wxb.presenter.view.VipDetailView;
import com.yuepeng.wxb.ui.pop.ChoosePayWayPop;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

public class VipActivity extends BaseActivity<ActivityVipBinding, VipPresenter> implements View.OnClickListener, VipDetailView {

    private ChoosePayWayPop payPop;
    private List<MemberEntity.PriceListdata> memberList;
    private int checked = 1;
    @Override
    protected View injectTarget() {
        return null;
    }

    @Override
    protected VipPresenter createPresenter() {
        return new VipPresenter(this);
    }

    @Override
    protected void Retry() {

    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_vip;
    }

    @Override
    protected void initView() {
        mBinding.monthOriginalPrice.getPaint().setFlags(Paint. STRIKE_THRU_TEXT_FLAG|Paint.ANTI_ALIAS_FLAG);  // 设置中划线并加清晰 ); //中划线
        mBinding.quarterOriginalPrice.getPaint().setFlags(Paint. STRIKE_THRU_TEXT_FLAG|Paint.ANTI_ALIAS_FLAG);  // 设置中划线并加清晰 ); //中划线
        mBinding.yearOriginalPrice.getPaint().setFlags(Paint. STRIKE_THRU_TEXT_FLAG|Paint.ANTI_ALIAS_FLAG);  // 设置中划线并加清晰 ); //中划线
        bindViewData();
    }

    private void bindViewData() {
        UserModel user = App.getInstance().getUserModel();
        LogUtils.d("User:"+ JSON.toJSONString(user));
        GlideUtils.load(this,user.getHeadImg(),mBinding.myAvatar, R.mipmap.avatar);
        mBinding.nickName.setText(user.getNickName());
        switch (user.getMemberOpenType()){
            case 1:
                mBinding.memberOpenType.setImageResource(R.mipmap.not_openvip);
                mBinding.ivMemberLevel.setImageResource(R.mipmap.single_member);
                mBinding.btnOpenVip.setText("立即开通");
                break;
            case 2:
                mBinding.useEndTime.setVisibility(View.VISIBLE);
                mBinding.useEndTime.setText("到期时间:"+user.getUseEndTime());
                mBinding.memberOpenType.setImageResource(R.mipmap.opentype2);
                mBinding.ivMemberLevel.setImageResource(R.mipmap.vip_member);
                mBinding.btnOpenVip.setText("立即续费");
                break;
            case 3:
                mBinding.useEndTime.setVisibility(View.VISIBLE);
                mBinding.useEndTime.setText("已过期"+user.getExpireDay()+"天");
                mBinding.memberOpenType.setImageResource(R.mipmap.opentype3);
                mBinding.ivMemberLevel.setImageResource(R.mipmap.single_member);
                mBinding.btnOpenVip.setText("立即开通");
                break;
        }
        mBinding.cardNo.setText("NO."+user.getCardNo());
    }

    @Override
    public void initListener() {
        super.initListener();
        mBinding.llMonthVip.setOnClickListener(this);
        mBinding.llQuarterVip.setOnClickListener(this);
        mBinding.llYearVip.setOnClickListener(this);
        mBinding.llRead.setOnClickListener(this);
        mBinding.btnOpenVip.setOnClickListener(this);
        mBinding.agreement.setOnClickListener(this);
    }

    @Override
    protected void initData() {
        mPresenter.getVipNumDetail();
        mPresenter.getMemberList();
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.agreement){
            Bundle bundle = new Bundle();
            bundle.putInt("TYPE",0);
            openActivity(PrivacyPolicyActivity.class,bundle);
        }
        if (id == R.id.llMonthVip){
            mBinding.llMonthVip.setBackgroundResource(R.drawable.shape_vippackage_select);
            mBinding.monthPresentPrice.setTextColor(Color.parseColor("#FFA9742E"));
            mBinding.llQuarterVip.setBackgroundResource(R.drawable.shape_vippackage_normal);
            mBinding.quarterPresentPrice.setTextColor(Color.parseColor("#FFF96F6F"));
            mBinding.llYearVip.setBackgroundResource(R.drawable.shape_vippackage_normal);
            mBinding.yearPresentPrice.setTextColor(Color.parseColor("#FFF96F6F"));
            checked = 0;
        }
        if (id == R.id.llQuarterVip){
            mBinding.llMonthVip.setBackgroundResource(R.drawable.shape_vippackage_normal);
            mBinding.monthPresentPrice.setTextColor(Color.parseColor("#FFF96F6F"));
            mBinding.llQuarterVip.setBackgroundResource(R.drawable.shape_vippackage_select);
            mBinding.quarterPresentPrice.setTextColor(Color.parseColor("#FFA9742E"));
            mBinding.llYearVip.setBackgroundResource(R.drawable.shape_vippackage_normal);
            mBinding.yearPresentPrice.setTextColor(Color.parseColor("#FFF96F6F"));
            checked = 1;
        }
        if (id == R.id.llYearVip){
            mBinding.llMonthVip.setBackgroundResource(R.drawable.shape_vippackage_normal);
            mBinding.monthPresentPrice.setTextColor(Color.parseColor("#FFF96F6F"));
            mBinding.llQuarterVip.setBackgroundResource(R.drawable.shape_vippackage_normal);
            mBinding.quarterPresentPrice.setTextColor(Color.parseColor("#FFF96F6F"));
            mBinding.llYearVip.setBackgroundResource(R.drawable.shape_vippackage_select);
            mBinding.yearPresentPrice.setTextColor(Color.parseColor("#FFA9742E"));
            checked = 2;
        }
        if (id == R.id.btnOpenVip){
            if (memberList != null){
                payPop = (ChoosePayWayPop) new XPopup.Builder(this).asCustom(new ChoosePayWayPop(this,memberList.get(checked)));
                payPop.show();
            }
        }
    }

    @Override
    public void onGetMemberNumSuccess(VipNumEntity vipNum) {
        mBinding.note.setText(vipNum.getNote());
        GlideUtils.load(this,vipNum.getPicList().get(0),mBinding.avatar1, R.mipmap.avatar);
        GlideUtils.load(this,vipNum.getPicList().get(1),mBinding.avatar2, R.mipmap.avatar);
        GlideUtils.load(this,vipNum.getPicList().get(2),mBinding.avatar3, R.mipmap.avatar);
        GlideUtils.load(this,vipNum.getPicList().get(3),mBinding.avatar4, R.mipmap.avatar);
        GlideUtils.load(this,vipNum.getPicList().get(4),mBinding.avatar5, R.mipmap.avatar);
    }

    @Override
    public void onGetMemberListSuccess(List<MemberEntity.PriceListdata> memberList) {
        mBinding.monthVipTitle.setText(memberList.get(0).getTitle());
        mBinding.monthOriginalPrice.setText("¥ "+memberList.get(0).getOriginalPrice());
        mBinding.monthPresentPrice.setText("¥ "+memberList.get(0).getPresentPrice());
        mBinding.quarterVipTitle.setText(memberList.get(1).getTitle());
        mBinding.quarterOriginalPrice.setText("¥ "+memberList.get(1).getOriginalPrice());
        mBinding.quarterPresentPrice.setText("¥ "+memberList.get(1).getPresentPrice());
        mBinding.yearVipTitle.setText(memberList.get(2).getTitle());
        mBinding.yearOriginalPrice.setText("¥ "+memberList.get(2).getOriginalPrice());
        mBinding.yearPresentPrice.setText("¥ "+memberList.get(2).getPresentPrice());
        this.memberList = memberList;
    }

    @Override
    public void onSuccess() {
        bindViewData();
    }

    @Override
    public void onError(Throwable e) {
        showException(e);
    }

    @Override
    public void onfailed(BaseResponse baseResponse) {
        showErrorDialog(baseResponse);
    }


    private void alipay(String oorderInfo){
        //实例化支付宝支付策略
        AliPay aliPay = new AliPay();
        //构造支付宝订单实体。一般都是由服务端直接返回。
        AlipayInfoImpli alipayInfoImpli = new AlipayInfoImpli();
        alipayInfoImpli.setOrderInfo(oorderInfo);
        //策略场景类调起支付方法开始支付，以及接收回调。
        EasyPay.pay(aliPay, this, alipayInfoImpli, new IPayCallback() {
            @Override
            public void success() {
                toast("支付成功");
                mPresenter.getUserInfo();
                mPresenter.getVipNumDetail();
                mPresenter.getMemberList();
                EventBus.getDefault().post(new FragmentEvent(EventCode.Main.ALREADYISVIP));
            }

            @Override
            public void failed(int code, String msg) {
                toast("支付失败");
            }

            @Override
            public void cancel() {
                toast("支付取消");
            }
        });
    }

    private void wxpay(WeChatPayEntity weChatPayEntity){
        //实例化微信支付策略
        WXPay wxPay = WXPay.getInstance();
        //构造微信订单实体。一般都是由服务端直接返回。
        WXPayInfoImpli wxPayInfoImpli = new WXPayInfoImpli();
        wxPayInfoImpli.setTimestamp(weChatPayEntity.getTimeStamp());
        wxPayInfoImpli.setSign(weChatPayEntity.getSign());
        wxPayInfoImpli.setPrepayId(weChatPayEntity.getPrepayId());
        wxPayInfoImpli.setPartnerid(weChatPayEntity.getPartnerId());
        wxPayInfoImpli.setAppid(weChatPayEntity.getAppId());
        wxPayInfoImpli.setNonceStr(weChatPayEntity.getNonceStr());
        wxPayInfoImpli.setPackageValue(weChatPayEntity.getPackageValue());
        //策略场景类调起支付方法开始支付，以及接收回调。
        EasyPay.pay(wxPay, this, wxPayInfoImpli, new IPayCallback() {
            @Override
            public void success() {
                toast("支付成功");
//                mPresenter.getUserInfo();
//                mPresenter.getVipNumDetail();
//                mPresenter.getMemberList();
                EventBus.getDefault().post(new FragmentEvent(EventCode.Main.ALREADYISVIP));
                EventBus.getDefault().post(new ActivityEvent(EventCode.Main.ALREADYISVIP));
            }

            @Override
            public void failed(int code, String msg) {
                toast("支付失败"+code+msg);
            }

            @Override
            public void cancel() {
                toast("支付取消");
            }
        });
    }

    @Override
    public void onEvent(ActivityEvent event) {
        super.onEvent(event);
        switch (event.getCode()){
            case EventCode.Pay.ALIPAY:
                alipay(event.getData().toString());
                break;
                case EventCode.Pay.WECHATPAY:
                wxpay((WeChatPayEntity)event.getData());
                break;
            case EventCode.Main.ALREADYISVIP:
                mPresenter.getUserInfo();
                break;
        }
    }


}