package com.yuepeng.wxb.ui.activity;

import android.view.View;

import com.hjq.bar.OnTitleBarListener;
import com.wstro.thirdlibrary.base.BaseResponse;
import com.wstro.thirdlibrary.entity.HisTimeResponse;
import com.wstro.thirdlibrary.entity.KithEntity;
import com.wstro.thirdlibrary.entity.TjDateResponse;
import com.yuepeng.wxb.R;
import com.yuepeng.wxb.adapter.TjDateAdapter;
import com.yuepeng.wxb.base.BaseActivity;
import com.yuepeng.wxb.databinding.ActivityHisSportBinding;
import com.yuepeng.wxb.presenter.HisSportPresenter;
import com.yuepeng.wxb.presenter.view.HisDetailView;

import java.util.List;

import androidx.viewpager.widget.ViewPager;

/**
 * Created by wangjun on 2021/2/5.
 */
public class HisSportActivity extends BaseActivity<ActivityHisSportBinding, HisSportPresenter> implements HisDetailView, ViewPager.OnPageChangeListener {

    private KithEntity kithEntity;

    @Override
    protected View injectTarget() {
        return null;
    }

    @Override
    protected HisSportPresenter createPresenter() {
        return new HisSportPresenter(this);
    }

    @Override
    protected void Retry() {

    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_his_sport;
    }

    @Override
    protected void initView() {
        mBinding.titlebar.setOnTitleBarListener(new OnTitleBarListener() {
            @Override
            public void onLeftClick(View v) {
                finish();
            }

            @Override
            public void onTitleClick(View v) {

            }

            @Override
            public void onRightClick(View v) {

            }
        });
    }

    @Override
    protected void initData() {
        kithEntity = (KithEntity) getIntent().getSerializableExtra("KithEntity");
        mPresenter.getTjdate(String.valueOf(kithEntity.getCustKithId()));
//        mBinding.srl.setOnRefreshListener(refreshLayout -> {
//            mPresenter.getTjdate(String.valueOf(kithEntity.getCustKithId()));
//        });
    }

    @Override
    public void onSuccess() {

    }

    @Override
    public void onError(Throwable e) {

    }

    @Override
    public void onfailed(BaseResponse baseResponse) {
        showErrorDialog(baseResponse);
    }

    @Override
    public void initListener() {
        super.initListener();
        mBinding.viewPager.setOnPageChangeListener(this);
    }


    @Override
    public void onTjDateSuccess(HisTimeResponse response) {
        mBinding.viewPager.setAdapter(new TjDateAdapter(getSupportFragmentManager(),response.getRecords()));
        mBinding.tablayout.setupWithViewPager(mBinding.viewPager);
    }

    @Override
    public void onTjDateListSuccess(List<TjDateResponse> data) {

    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        switch (position){
            case 0:
                mSwipeBackHelper.setSwipeBackEnable(true);
                break;
            default:
                mSwipeBackHelper.setSwipeBackEnable(false);
                break;
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }
}
