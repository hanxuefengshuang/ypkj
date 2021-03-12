package com.yuepeng.wxb.ui.activity;

import android.content.pm.PackageManager;
import android.view.View;

import com.wstro.thirdlibrary.base.BasePresenter;
import com.yuepeng.wxb.R;
import com.yuepeng.wxb.base.BaseActivity;
import com.yuepeng.wxb.databinding.ActivityAboutUsBinding;

public class AboutUsActivity extends BaseActivity<ActivityAboutUsBinding, BasePresenter> {


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
        return R.layout.activity_about_us;
    }

    @Override
    protected void initView() {
        mBinding.title.titlebar.setTitle("关于我们");
        String versionName = null;
        String pkName = this.getPackageName();
        try {
            versionName= this.getPackageManager().getPackageInfo(
                    pkName, 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        mBinding.versionName.setText("V"+versionName);
    }

    @Override
    protected void initData() {

    }
}