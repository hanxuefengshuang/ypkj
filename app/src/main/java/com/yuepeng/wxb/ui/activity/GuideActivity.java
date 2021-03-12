package com.yuepeng.wxb.ui.activity;

import android.Manifest;
import android.os.Build;
import android.view.View;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.gyf.immersionbar.ImmersionBar;
import com.tbruyelle.rxpermissions2.RxPermissions;
import com.wstro.thirdlibrary.base.BasePresenter;
import com.yuepeng.wxb.R;
import com.yuepeng.wxb.adapter.GuidePagerAdapter;
import com.yuepeng.wxb.base.BaseActivity;
import com.yuepeng.wxb.databinding.ActivityGuideBinding;
import com.yuepeng.wxb.router.RouterPath;
import com.yuepeng.wxb.utils.PreUtils;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import androidx.annotation.RequiresApi;
import androidx.viewpager.widget.ViewPager;

@RequiresApi(api = Build.VERSION_CODES.N)
@Route(path = RouterPath.Main.F_GUIDE)
public class GuideActivity extends BaseActivity<ActivityGuideBinding, BasePresenter> implements ViewPager.OnPageChangeListener, View.OnClickListener {

    private static final int[] pics = new int[]{ R.mipmap.guide1,
            R.mipmap.guide2, R.mipmap.guide3};
    List<Integer> picList = Arrays.stream(pics).boxed().collect(Collectors.toList());
    private GuidePagerAdapter pagerAdapter = new GuidePagerAdapter(picList);
    RxPermissions rxPermissions;
    @Override
    protected View injectTarget() {
        return null;
    }

    @Override
    protected boolean isStatusBarEnabled() {
        return false;
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
        return R.layout.activity_guide;
    }

    @Override
    protected void initView() {
        ImmersionBar.with(this)
                .titleBar(mBinding.toolbar, false)
                .transparentBar()
                .init();
        mBinding.viewPager.setAdapter(pagerAdapter);
        mBinding.viewPager.addOnPageChangeListener(this);
//        initMagicIndicator();

//        if (rxPermissions.isGranted(Manifest.permission.ACCESS_FINE_LOCATION))

    }

    private void ToLogin() {
        openActivity(LoginActivity.class);
    }

    @Override
    protected void initData() {

    }

    @Override
    public void initListener() {
        super.initListener();
        mBinding.btnStart.setOnClickListener(this);
    }

    @Override
    public boolean isSupportSwipeBack() {
        return false;
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        if (position == picList.size()-1){
            toast("为了获得更好的体验，建议同意始终允许开启定位");
            mBinding.btnStart.setVisibility(View.VISIBLE);
        }else {
            mBinding.btnStart.setVisibility(View.GONE);
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.btnStart){
            checkPermissions();
            PreUtils.putBoolean("FIRSTINSTALL",false);
        }
    }

    private void checkPermissions() {
        rxPermissions = new RxPermissions(this);
        rxPermissions.request(Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION,Manifest.permission.ACCESS_BACKGROUND_LOCATION)
                .subscribe(granted -> {
                    if (granted){
                        ToLogin();
                    }else {
                        ToLogin();
                    }
//                    if (granted) {
//                        ToLogin();
//                    } else {
//
////                        ToMain();
//                    }
//                    ToLogin();
                });
    }
}