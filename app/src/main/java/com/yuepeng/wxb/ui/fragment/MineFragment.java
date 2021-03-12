package com.yuepeng.wxb.ui.fragment;

import android.view.View;

import com.gyf.immersionbar.ImmersionBar;
import com.lxj.xpopup.XPopup;
import com.wstro.thirdlibrary.api.Constant;
import com.wstro.thirdlibrary.base.BaseDetailView;
import com.wstro.thirdlibrary.base.BaseResponse;
import com.wstro.thirdlibrary.entity.UserModel;
import com.wstro.thirdlibrary.event.EventCode;
import com.wstro.thirdlibrary.event.FragmentEvent;
import com.wstro.thirdlibrary.utils.AppCache;
import com.wstro.thirdlibrary.utils.GlideUtils;
import com.wstro.thirdlibrary.utils.WechatShareTools;
import com.yuepeng.wxb.R;
import com.yuepeng.wxb.base.App;
import com.yuepeng.wxb.base.BaseFragment;
import com.yuepeng.wxb.databinding.FragmentMineBinding;
import com.yuepeng.wxb.presenter.MinePresenter;
import com.yuepeng.wxb.ui.activity.ContactActivity;
import com.yuepeng.wxb.ui.activity.MineInformationActivity;
import com.yuepeng.wxb.ui.activity.NewMsgActivity;
import com.yuepeng.wxb.ui.activity.SafetyPlaceActivity;
import com.yuepeng.wxb.ui.activity.SettingActivity;
import com.yuepeng.wxb.ui.activity.VipActivity;
import com.yuepeng.wxb.ui.pop.SharePop;

import org.greenrobot.eventbus.EventBus;


public class MineFragment extends BaseFragment<FragmentMineBinding, MinePresenter> implements BaseDetailView, View.OnClickListener {

    SharePop sharePop;

    @Override
    protected int onBindLayout() {
        return R.layout.fragment_mine;
    }

    @Override
    protected MinePresenter createPresenter() {
        return new MinePresenter(this);
    }

    @Override
    protected void initView() {
        ImmersionBar.with(this).setTitleBar(getActivity(),mBinding.title);
    }

    @Override
    protected void Retry() {

    }

    @Override
    protected View injectTarget() {
        return null;
    }

    @Override
    public void initData() {
        WechatShareTools.init(getContext(), Constant.WECHATAPPID);
        sharePop = (SharePop) new XPopup.Builder(getContext()).asCustom(new SharePop(getContext()));
    }


    @Override
    protected boolean enableSimplebar() {
        return false;
    }

    @Override
    protected boolean isStatusBarEnabled() {
        return false;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisible()){
            initUserData();
        }
    }

    private void initUserData() {
//        mPresenter.getUserInfo();
        if (isVisible()){
        if (AppCache.IsLogin){
            UserModel userModel = App.getInstance().getUserModel();
            GlideUtils.load(getActivity(),userModel.getHeadImg(),mBinding.avatar, R.mipmap.avatar);
            mBinding.nickName.setText(userModel.getNickName());
            if (userModel.getMemberType() == 0){
                mBinding.time.setVisibility(View.GONE);
                mBinding.rlOpenVip.setBackgroundResource(R.mipmap.mine_vipback);
            }else {
                mBinding.rlOpenVip.setBackgroundResource(R.mipmap.mine_isvip);
                mBinding.time.setVisibility(View.VISIBLE);
                if (userModel.getUseEndTime() == null){
                    mBinding.time.setText("会员到期时间：已过期");
                }else {
                    mBinding.time.setText("会员到期时间："+userModel.getUseEndTime());
                }
            }
        }
        }
    }

    @Override
    public void initListener() {
        super.initListener();
        mBinding.cUser.setOnClickListener(this);
        mBinding.rlOpenVip.setOnClickListener(this);
        mBinding.rlSafety.setOnClickListener(this);
        mBinding.rlContact.setOnClickListener(this);
        mBinding.rlShare.setOnClickListener(this);
        mBinding.rlSetting.setOnClickListener(this);
        mBinding.rlMsg.setOnClickListener(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (isVisible() && AppCache.IsLogin){
            mPresenter.getUserInfo();
        }else {
            initUserData();
        }
    }

    @Override
    public void onSuccess() {
        if (isFirstVisible){
            EventBus.getDefault().post(new FragmentEvent(EventCode.Main.ALREADYSAVEUSERINFO));
        }
        initUserData();

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
    public void dismissProgressDialog() {

    }

    @Override
    protected boolean enableLazy() {
        return false;
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.cUser){
            openActivity(MineInformationActivity.class);
        }
        if (id == R.id.rlOpenVip){
            openActivity(VipActivity.class);
        }
        if (id == R.id.rl_safety){
            openActivity(SafetyPlaceActivity.class);
        }
        if (id == R.id.rl_contact){
            openActivity(ContactActivity.class);
        }
        if (id == R.id.rl_share){
            sharePop.show();
        }
        if (id == R.id.rl_setting){
            openActivity(SettingActivity.class);
        }
        if (id == R.id.rl_msg){
            openActivity(NewMsgActivity.class);
        }
    }

    @Override
    public void onEvent(FragmentEvent event) {
        super.onEvent(event);
        switch (event.getCode()){
            case EventCode.Main.ALREADYISVIP:
                mPresenter.getUserInfo();
                break;
        }
    }
}
