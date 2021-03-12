package com.yuepeng.wxb.ui.activity;

import android.graphics.Color;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;

import com.baidu.mapapi.model.LatLng;
import com.lxj.xpopup.XPopup;
import com.wstro.thirdlibrary.base.BaseResponse;
import com.wstro.thirdlibrary.entity.UserModel;
import com.wstro.thirdlibrary.utils.RegexUtil;
import com.wstro.thirdlibrary.utils.WechatShareModel;
import com.wstro.thirdlibrary.utils.WechatShareTools;
import com.yuepeng.wxb.R;
import com.yuepeng.wxb.base.App;
import com.yuepeng.wxb.base.BaseActivity;
import com.yuepeng.wxb.databinding.ActivityAddFriendBinding;
import com.yuepeng.wxb.presenter.AddFriendPresenter;
import com.yuepeng.wxb.presenter.view.AddFriendDetailView;
import com.yuepeng.wxb.ui.pop.OpenVipPop;
import com.yuepeng.wxb.utils.LocationTools;
import com.yuepeng.wxb.utils.PreUtils;

import static com.wstro.thirdlibrary.api.Constant.WECHATAPPID;

public class AddFriendActivity extends BaseActivity<ActivityAddFriendBinding, AddFriendPresenter> implements TextWatcher, AddFriendDetailView, View.OnClickListener {

    int Type = 0;   // 0为无点击事件   1为添加   2为邀请
    private OpenVipPop openVipPop;
    private int memberType;
    private UserModel userModel;
    private double[]gbLocation;
    @Override
    protected View injectTarget() {
        return null;
    }

    @Override
    protected AddFriendPresenter createPresenter() {
        return new AddFriendPresenter(this);
    }

    @Override
    protected void Retry() {

    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_add_friend;
    }

    @Override
    protected void initView() {
        WechatShareTools.init(this, WECHATAPPID);
    }

    @Override
    protected void onResume() {
        super.onResume();
        userModel = App.getInstance().getUserModel();
        LatLng myLocation = PreUtils.getLocation();
        gbLocation = LocationTools.calBD09toGCJ02(myLocation.latitude,myLocation.longitude);
        memberType = App.getInstance().getUserModel().getMemberType();
    }

    @Override
    protected void initData() {
    }

    @Override
    public void initListener() {
        super.initListener();
        mBinding.etPhone.addTextChangedListener(this);
        mBinding.btnCheck.setOnClickListener(this);
        mBinding.addFriend.setOnClickListener(this);
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        if (charSequence.length() == 11){
            if (RegexUtil.checkMobile(charSequence.toString())){
                mBinding.btnCheck.setTextColor(getResources().getColor(R.color.appThemeColor));
                mPresenter.checkIfIsRegister(charSequence.toString());
            }else {
                toast("请输入正确的手机号");
            }
        }else {
            mBinding.searchPhone.setText("");
            mBinding.btnCheck.setText("");
            mBinding.btnCheck.setVisibility(View.GONE);
        }
    }

    @Override
    public void afterTextChanged(Editable editable) {

    }

    @Override
    public void onCheckResultWithNoBind(String mobile) {
        mBinding.searchPhone.setText(mobile);
        mBinding.btnCheck.setVisibility(View.VISIBLE);
        mBinding.btnCheck.setText("立即添加");
        Type = 1;
    }

    @Override
    public void onCheckResultWithNotRegister(String mobile) {
        mBinding.searchPhone.setText(mobile);
        mBinding.btnCheck.setVisibility(View.VISIBLE);
        mBinding.btnCheck.setText("立即邀请");
        Type = 2;
    }

    @Override
    public void onCheckFailed(String mobile,String msg) {
        mBinding.searchPhone.setText(mobile);
        mBinding.btnCheck.setVisibility(View.VISIBLE);
        mBinding.btnCheck.setText(msg);
        mBinding.btnCheck.setTextColor(Color.parseColor("#FF999999"));
        Type = 0;
    }

    @Override
    public void onInviteSuccess() {
        showSuccessDialog("好友邀请发送成功");
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
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.btnCheck){
            hideSoftKeyboard();
            switch (Type){
                case 0:
                    break;
                case 1:
                    if (memberType == 0){
                        openVipPop = (OpenVipPop) new XPopup.Builder(this)
                                .dismissOnBackPressed(false)
                                .dismissOnTouchOutside(false)
                                .asCustom(new OpenVipPop(this))
                                .show();
                        return;
                    }
                    mPresenter.addFriend(mBinding.searchPhone.getText().toString());

                    break;
                case 2:
                    if (!WechatShareTools.isWechatInstall()){
                        showWarningDialog("您未安装微信，请先安装微信");
                        return;
                    }
                    WechatShareModel wechatShareModel = new WechatShareModel("https://wxb.bid-china.net/share/index.html#/?nickname="+userModel.getNickName()+"&pic="+userModel.getHeadImg()+"&latitude="+ gbLocation[0] +"&longitude="+gbLocation[1], "\""+ App.getInstance().getUserModel().getNickName()+"\""+"邀请你成为好友","下载就可以关注他的行程啦！守护亲友安全");
                    WechatShareTools.shareURL(wechatShareModel, WechatShareTools.SharePlace.Friend,this);
                    break;
            }
        }
        if (id == R.id.addFriend){
            if (!WechatShareTools.isWechatInstall()){
                showWarningDialog("您未安装微信，请先安装微信");
                return;
            }
            WechatShareModel wechatShareModel = new WechatShareModel("https://wxb.bid-china.net/share/index.html#/?nickname="+userModel.getNickName()+"&pic="+userModel.getHeadImg()+"&latitude="+ gbLocation[0] +"&longitude="+gbLocation[1], "\""+ App.getInstance().getUserModel().getNickName()+"\""+"邀请你成为好友","下载就可以关注他的行程啦！守护亲友安全");
            WechatShareTools.shareURL(wechatShareModel, WechatShareTools.SharePlace.Friend,this);
        }
    }


}