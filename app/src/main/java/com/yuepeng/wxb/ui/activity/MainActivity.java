package com.yuepeng.wxb.ui.activity;

import android.content.Intent;
import android.view.View;

import com.lxj.xpopup.XPopup;
import com.next.easynavigation.constant.Anim;
import com.next.easynavigation.view.EasyNavigationBar;
import com.wstro.thirdlibrary.base.BasePresenter;
import com.wstro.thirdlibrary.event.ActivityEvent;
import com.wstro.thirdlibrary.event.EventCode;
import com.yuepeng.wxb.R;
import com.yuepeng.wxb.base.BaseActivity;
import com.yuepeng.wxb.databinding.ActivityMainBinding;
import com.yuepeng.wxb.location.ForegroundService;
import com.yuepeng.wxb.ui.fragment.FriendFragment;
import com.yuepeng.wxb.ui.fragment.MapFragment;
import com.yuepeng.wxb.ui.fragment.MineFragment;
import com.yuepeng.wxb.ui.pop.InviteFriendPop;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.DrawableRes;
import androidx.fragment.app.Fragment;

public class MainActivity extends BaseActivity<ActivityMainBinding, BasePresenter> implements EasyNavigationBar.OnCenterTabSelectListener, EasyNavigationBar.OnTabClickListener, EasyNavigationBar.OnTabLoadListener {

    private String[]tabText = {"好友","我的"};
    private @DrawableRes
    int[] normalIcon = {R.mipmap.friend_unselect, R.mipmap.mine_unselect };
    private @DrawableRes
    int[] selectIcon = {R.mipmap.friend_select, R.mipmap.mine_select};
    private InviteFriendPop inviteFriendPop;
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
        return R.layout.activity_main;
    }

    @Override
    protected void initView() {
        List<Fragment> fragments = new ArrayList<>();
        fragments.add(new FriendFragment());
        fragments.add(new MineFragment());
        fragments.add(new MapFragment());
        mBinding.enb.titleItems(tabText)
                .normalIconItems(normalIcon)
                .selectIconItems(selectIcon)
                .fragmentList(fragments)
                .fragmentManager(getSupportFragmentManager())
                .mode(EasyNavigationBar.NavigationMode.MODE_ADD)
                .centerImageRes(R.mipmap.map_frag)
                .centerIconSize(54)
                .centerLayoutHeight(74)
                .anim(Anim.BounceIn)
                .normalTextColor(getResources().getColor(R.color.buttonUnClick))   //Tab未选中时字体颜色
                .selectTextColor(getResources().getColor(R.color.appThemeColor))   //Tab选中时字体颜色
                .tabTextSize(10)   //Tab文字大小
                .iconSize(20)
                .setOnCenterTabClickListener(this)
                .lineHeight(5)
                .lineColor(0xeeeeee)
                .navigationHeight(50)
                .setOnTabClickListener(this)
                .setOnTabLoadListener(this)
                .build();
    }

    @Override
    protected void initData() {
        startService(new Intent(this, ForegroundService.class));
    }

    @Override
    public boolean onCenterTabSelectEvent(View view) {
        mBinding.enb.selectTab(2,false);
        return false;
    }

    @Override
    public boolean onTabSelectEvent(View view, int position) {
        return false;
    }

    @Override
    public boolean onTabReSelectEvent(View view, int position) {
        return false;
    }

    @Override
    public void onTabLoadCompleteEvent() {
        mBinding.enb.selectTab(2,false);
    }

    @Override
    public boolean isSupportSwipeBack() {
        return false;
    }

    @Override
    public void onEvent(ActivityEvent event) {
        super.onEvent(event);
        switch (event.getCode()){
            case EventCode.Main.FINISHTHIS:
                finish();
                break;
            case EventCode.Main.NOFRIEND:
                if (firstVisible){
                    inviteFriendPop = (InviteFriendPop) new XPopup.Builder(this)
                            .dismissOnBackPressed(false)
                            .dismissOnTouchOutside(false)
                            .asCustom(new InviteFriendPop(this))
                            .show();
                }
                firstVisible = false;
                break;

            case EventCode.Main.GOTOFRIENDLIST:
                mBinding.enb.selectTab(0,false);
                break;
        }
    }
}