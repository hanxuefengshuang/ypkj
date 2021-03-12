package com.yuepeng.wxb.ui.activity;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.wstro.thirdlibrary.base.BasePresenter;
import com.yuepeng.wxb.R;
import com.yuepeng.wxb.base.BaseActivity;
import com.yuepeng.wxb.databinding.ActivityPrivacyPolicyBinding;

public class PrivacyPolicyActivity extends BaseActivity<ActivityPrivacyPolicyBinding, BasePresenter> {

   private int type;
    private WebView web_view;
    @Override
    protected View injectTarget() {
        return null;
    }

    @Override
    protected BasePresenter createPresenter() {
        return null;
    }

    @Override
    protected void Retry() {

    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_privacy_policy;
    }

    @Override
    protected void initView() {
        web_view = new WebView(getApplicationContext());
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        web_view.setLayoutParams(params);
        web_view.setWebViewClient(new WebViewClient());
        mBinding.webViewContainer.addView(web_view);
       Bundle bundle =  getIntent().getBundleExtra("bundle");
       type = bundle.getInt("TYPE");
       switch (type){
           case 0:
               mBinding.title.titlebar.setTitle("用户协议");
               web_view.loadUrl("file:///android_asset/userAgreement.html");
               break;
           case 1:
               mBinding.title.titlebar.setTitle("隐私条例");
               web_view.loadUrl("file:///android_asset/privacy.html");
               break;
       }
    }

    @Override
    protected void initData() {

    }
}