package com.yuepeng.wxb.ui.activity;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.gyf.immersionbar.ImmersionBar;
import com.lxj.xpopup.XPopup;
import com.wstro.thirdlibrary.base.BaseResponse;
import com.wstro.thirdlibrary.utils.RegexUtil;
import com.yuepeng.wxb.R;
import com.yuepeng.wxb.base.BaseActivity;
import com.yuepeng.wxb.databinding.ActivityLoginBinding;
import com.yuepeng.wxb.presenter.LoginPresenter;
import com.yuepeng.wxb.presenter.view.LoginDetailView;
import com.yuepeng.wxb.router.RouterPath;
import com.yuepeng.wxb.ui.pop.AgreementPop;
import com.yuepeng.wxb.utils.PreUtils;


@Route(path = RouterPath.Main.F_LOGIN)
public class LoginActivity extends BaseActivity<ActivityLoginBinding, LoginPresenter> implements LoginDetailView, View.OnClickListener {

    AgreementPop pop;
    @Override
    protected View injectTarget() {
        return null;
    }

    @Override
    protected LoginPresenter createPresenter() {
        return new LoginPresenter(this);
    }

    @Override
    protected void Retry() {

    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_login;
    }

    @Override
    protected void initView() {
        ImmersionBar.with(this)
                .statusBarDarkFont(true)
                .navigationBarDarkIcon(true)
                .init();
    }

    @Override
    protected void initData() {
        boolean first = PreUtils.getBoolean(PreUtils.KEY_FIRSTLOGIN);
        if (first){
            pop = (AgreementPop) new XPopup.Builder(this)
                    .dismissOnBackPressed(false)
                    .dismissOnTouchOutside(false)
                    .asCustom(new AgreementPop(this));
            pop.show();
            pop.setOnClickFinishListener(new AgreementPop.onClickFinish() {
                @Override
                public void onClickFinish() {
                    finish();
                }
            });
        }
    }

    @Override
    public void initListener() {
        super.initListener();
        mBinding.cvLoginCountdown.setOnClickListener(this);
        mBinding.btnLogin.setOnClickListener(this);
        mBinding.serviceAgreement.setOnClickListener(this);
        mBinding.privacyAgreement.setOnClickListener(this);
        mBinding.etMobile.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }


    @Override
    public void onGetCodeSuccess(String code) {
       // toast(code);
        showSuccessDialog("发送成功");
        mBinding.cvLoginCountdown.start();
    }

    @Override
    public void onGetLoginInfoSuccess() {
        toast("登录成功");
        openActivity(MainActivity.class);
        finish();
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
        if (id == R.id.cv_login_countdown){
            if (!RegexUtil.checkMobile(getEditText(mBinding.etMobile))){
                mBinding.tilMobile.setError("请输入正确的手机号");
                return;
            }
            if (getEditText(mBinding.etMobile).isEmpty()){
                mBinding.tilMobile.setError("手机号不能为空");
                return;
            }
            mBinding.tilMobile.setErrorEnabled(false);
            mPresenter.getCode(getEditText(mBinding.etMobile));
        }
        if (id == R.id.btnLogin){
            if (!RegexUtil.checkMobile(getEditText(mBinding.etMobile))){
                mBinding.tilMobile.setError("请输入正确的手机号");
                return;
            }
            if (getEditText(mBinding.etCode).length()<4){
                mBinding.tilCode.setError("请正确输入验证码");
                return;
            }
            mPresenter.getLoginInfo(getEditText(mBinding.etCode),getEditText(mBinding.etMobile));
        }
        if (id == R.id.service_agreement){
            Bundle bundle = new Bundle();
            bundle.putInt("TYPE",0);
            openActivity(PrivacyPolicyActivity.class,bundle);
        }
        if (id == R.id.privacy_agreement){
            Bundle bundle = new Bundle();
            bundle.putInt("TYPE",1);
            openActivity(PrivacyPolicyActivity.class,bundle);
        }
    }

    @Override
    public boolean isSupportSwipeBack() {
        return false;
    }
}