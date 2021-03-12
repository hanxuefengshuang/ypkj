package com.yuepeng.wxb.ui.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

import com.wstro.thirdlibrary.base.BasePresenter;
import com.wstro.thirdlibrary.utils.AppCache;
import com.xuexiang.xui.widget.dialog.materialdialog.DialogAction;
import com.xuexiang.xui.widget.dialog.materialdialog.MaterialDialog;
import com.yuepeng.wxb.R;
import com.yuepeng.wxb.base.App;
import com.yuepeng.wxb.base.BaseActivity;
import com.yuepeng.wxb.databinding.ActivitySettingBinding;
import com.yuepeng.wxb.utils.PreUtils;

import androidx.annotation.NonNull;

public class SettingActivity extends BaseActivity<ActivitySettingBinding, BasePresenter> implements View.OnClickListener {


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
        return R.layout.activity_setting;
    }

    @Override
    protected void initView() {
        mBinding.title.titlebar.setTitle("设置");
    }

    @Override
    protected void initData() {

    }

    @Override
    public void initListener() {
        super.initListener();
        mBinding.rlUserAgreement.setOnClickListener(this);
        mBinding.rlPrivacy.setOnClickListener(this);
        mBinding.rlAboutUs.setOnClickListener(this);
        mBinding.logOut.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.rl_user_agreement){
            Bundle bundle = new Bundle();
            bundle.putInt("TYPE",0);
            openActivity(PrivacyPolicyActivity.class,bundle);
        }
        if (id == R.id.rl_privacy){
            Bundle bundle = new Bundle();
            bundle.putInt("TYPE",1);
            openActivity(PrivacyPolicyActivity.class,bundle);
        }
        if (id == R.id.rl_about_us){
            openActivity(AboutUsActivity.class);
        }
        if (id == R.id.logOut){
            new MaterialDialog.Builder(this)
                    .title("温馨提示")
                    .content("确定退出登录吗")
                    .positiveText("确定")
                    .positiveColor(getResources().getColor(R.color.appThemeColor))
                    .negativeColor(getResources().getColor(R.color.adadad))
                    .negativeText("取消")
                    .onPositive(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                            dialog.dismiss();
                            PreUtils.putTokenInfo("");
                            App.getInstance().saveUserModel(null);
                            AppCache.IsLogin = false;
                            App.getInstance().userModel = null;
                            PreUtils.putAddress(null);
                            PreUtils.putInt(PreUtils.KEY_READ_MSG_COUNT,0);
                            for (Activity activity:mActivities){
                                if (activity.getClass() == MainActivity.class){
                                    activity.finish();
                                }
                            }
                            openActivity(LoginActivity.class);
                            finish();
                        }
                    })
                    .show();

        }
    }
}