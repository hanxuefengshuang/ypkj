package com.yuepeng.wxb.ui.fragment;

import android.content.Intent;
import android.graphics.Color;
import android.view.View;

import com.hjq.bar.OnTitleBarListener;
import com.lxj.xpopup.XPopup;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadMoreListener;
import com.wstro.thirdlibrary.entity.KithEntity;
import com.wstro.thirdlibrary.event.ActivityEvent;
import com.wstro.thirdlibrary.event.EventCode;
import com.wstro.thirdlibrary.event.FragmentEvent;
import com.wstro.thirdlibrary.utils.AppCache;
import com.xuexiang.xui.widget.dialog.materialdialog.DialogAction;
import com.xuexiang.xui.widget.dialog.materialdialog.MaterialDialog;
import com.xuexiang.xui.widget.textview.supertextview.SuperTextView;
import com.yuepeng.wxb.R;
import com.yuepeng.wxb.adapter.FriendAdapter;
import com.yuepeng.wxb.base.App;
import com.yuepeng.wxb.base.BaseFragment;
import com.yuepeng.wxb.databinding.FragmentFriendBinding;
import com.yuepeng.wxb.presenter.KithPresenter;
import com.yuepeng.wxb.presenter.view.KithDetailView;
import com.yuepeng.wxb.ui.activity.AddFriendActivity;
import com.yuepeng.wxb.ui.activity.HisSportActivity;
import com.yuepeng.wxb.ui.activity.VipActivity;
import com.yuepeng.wxb.ui.pop.ModifyNotePop;
import com.yuepeng.wxb.ui.pop.OpenVipPop;
import com.yuepeng.wxb.ui.pop.UpDateFriendMenuPop;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;


/**
 * @author:create by Nico
 * createTime:1/30/21
 * Email:752497253@qq.com
 */
public class FriendFragment extends BaseFragment<FragmentFriendBinding, KithPresenter> implements KithDetailView, OnRefreshLoadMoreListener, View.OnClickListener, FriendAdapter.OnEditClickListener, UpDateFriendMenuPop.onClickPopItemListener, ModifyNotePop.onClickModifyNoteItemListener {

    private int page = 1;
    private int pageSize = 1000;
    private FriendAdapter mAdapter;
    private List<KithEntity>kithList = new ArrayList<>();
    private View footView;
    private UpDateFriendMenuPop menuPop;
    private ModifyNotePop modifyNotePop;
    private OpenVipPop openVipPop;
    private int memberType;

    @Override
    protected int onBindLayout() {
        return R.layout.fragment_friend;
    }

    @Override
    protected KithPresenter createPresenter() {
        return new KithPresenter(this);
    }

    @Override
    protected void initView() {
        memberType = App.getInstance().getUserModel().getMemberType();
        if (memberType != 0){
            mBinding.openVip.setImageResource(R.mipmap.isvipback);
        }
        mBinding.titlebar.setOnTitleBarListener(new OnTitleBarListener() {
            @Override
            public void onLeftClick(View v) {

            }

            @Override
            public void onTitleClick(View v) {

            }

            @Override
            public void onRightClick(View v) {
                openActivity(AddFriendActivity.class);
            }
        });
        mBinding.appbarLayout.addOnOffsetChangedListener((appBarLayout, verticalOffset) -> {
            if (verticalOffset <= -mBinding.c1.getHeight() / 2) {
                mBinding.appbarLayoutToolbar.setVisibility(View.VISIBLE);
                mBinding.collapseLayout.setCollapsedTitleTextColor(Color.WHITE);
            } else {
                mBinding.appbarLayoutToolbar.setVisibility(View.GONE);
            }
        });
        refreshLayout = mBinding.refreshLayout;
        mAdapter = new FriendAdapter(kithList);
        mBinding.rv.setLayoutManager(new LinearLayoutManager(getActivity()));
        mBinding.rv.setAdapter(mAdapter);
        mBinding.refreshLayout.setOnRefreshLoadMoreListener(this);
        mAdapter.setOnEditClickListener(this);
        mAdapter.setOnItemClickListener((adapter, view, position) -> {
            if (memberType == 0 && position != 0){
                openVipPop = (OpenVipPop) new XPopup.Builder(getActivity())
                        .dismissOnBackPressed(false)
                        .dismissOnTouchOutside(false)
                        .asCustom(new OpenVipPop(getActivity()))
                        .show();
                return;
            }
            Intent intent = new Intent(getActivity(), HisSportActivity.class);
            intent.putExtra("KithEntity",kithList.get(position));
            startActivity(intent);
        });
    }


    @Override
    protected void Retry() {

    }

    @Override
    protected View injectTarget() {
        return mBinding.cbottom;
    }

    @Override
    public void initData() {
        mPresenter.getKithList(page,pageSize);
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
    public void onSuccess() {
    }

    @Override
    public void onError(Throwable e) {
        showException(e);
        if (page == 1){
            mStateView.showRetry();
        }
    }

    @Override
    public void onFailed(String msg) {
        showErrorDialog(msg);
        if (page == 1){
            mStateView.showRetry();
        }
    }

    @Override
    public void onFinishRefreshAndLoadMore() {
        finishRefreshAndLoadMore();
    }

    @Override
    public void onFinishRefreshAndLoadMoreWithNoMoreData() {
        finishRefreshAndLoadMoreWithNoMoreData();
        mAdapter.addFooterView(getFooterView(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        }));
    }
    private View getFooterView(View.OnClickListener listener) {
        footView = getLayoutInflater().inflate(R.layout.footer_view, mBinding.rv, false);
        footView.findViewById(R.id.addFriend).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openActivity(AddFriendActivity.class);
            }
        });
        return footView;
    }

    @Override
    public void onGetKithListSuccess(List<KithEntity> kithEntityList) {
        if (kithEntityList != null){
            mStateView.showContent();
            if (page == 1 && kithEntityList.size()==0){
                mStateView.showEmpty();
                return;
            }
            if (kithEntityList.size() == 1){
                AppCache.HaveFriend = false;
                EventBus.getDefault().post(new ActivityEvent(EventCode.Main.NOFRIEND));
            }else {
                AppCache.HaveFriend = true;
            }
            mAdapter.addData(kithEntityList);
            mAdapter.notifyDataSetChanged();
            page ++;
        }
    }

    @Override
    public void onDelFriendSuccess(int custKithId) {
        showSuccessDialog("已成功解除该好友关系");
        for (KithEntity kithEntity: kithList){
            if (kithEntity.getCustKithId() == custKithId){
                kithList.remove(kithEntity);
            }
        }
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onUpdateNoteSuccess(int custKithId, String note) {
        showSuccessDialog("修改成功");
        for (KithEntity kithEntity: kithList){
            if (kithEntity.getCustKithId() == custKithId){
                kithEntity.setKithNote(note);
            }
        }
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onUpdateLocationHideSuccess(int custKithId, boolean needHide) {
        for (KithEntity kithEntity: kithList){
            if (kithEntity.getCustKithId() == custKithId){
                if (needHide){
                    kithEntity.setHideLocation(1);
                }else {
                    kithEntity.setHideLocation(0);
                }
            }
        }
        if (menuPop != null){
            SuperTextView superTextView = menuPop.getPopupContentView().findViewById(R.id.sbHideLocation);
            if (needHide){
                superTextView.setSwitchIsChecked(true);
            }else {
                superTextView.setSwitchIsChecked(false);
            }
        }
    }

    @Override
    public void onUpdateLocationHideFail(String msg, int custKithId, boolean needHide) {
        toast(msg);
        if (menuPop != null){
            SuperTextView superTextView = menuPop.getPopupContentView().findViewById(R.id.sbHideLocation);
            if (needHide){
                superTextView.setSwitchIsChecked(false);
            }else {
                superTextView.setSwitchIsChecked(true);
            }
        }
    }

    @Override
    protected boolean enableStateView() {
        return true;
    }

    @Override
    public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
        mPresenter.getKithList(page,pageSize);
    }

    @Override
    public void onRefresh(@NonNull RefreshLayout refreshLayout) {
        page = 1;
        kithList.clear();
        mStateView.showLoading();
        if (footView != null){
            mAdapter.removeFooterView(footView);
        }
        mAdapter.notifyDataSetChanged();
        mPresenter.getKithList(page,pageSize);
    }

    @Override
    public void initListener() {
        super.initListener();
        mBinding.openVip.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.openVip){
            openActivity(VipActivity.class);
        }
    }

    @Override
    public void onEditClick(KithEntity kithEntity) {
        menuPop = (UpDateFriendMenuPop) new XPopup.Builder(getActivity())
                .asCustom(new UpDateFriendMenuPop(getActivity(),kithEntity))
                .show();
        menuPop.setOnUpDateFriendMenuPop(this);
    }

    @Override
    public void onClickRemoveRelationShip(KithEntity kithEntity) {
        menuPop.dismiss();
        new MaterialDialog.Builder(getContext())
                .title("解除好友关系")
                .content("好友关系解除后，就再也不可以查询到对方的位置信息啦。确定要这么做吗?")
                .positiveText("确定")
                .positiveColor(getResources().getColor(R.color.appThemeColor))
                .negativeColor(getResources().getColor(R.color.adadad))
                .negativeText("取消")
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        mPresenter.delFriend(kithEntity.getCustKithId());
                        dialog.dismiss();
                    }
                })
                .show();
//
    }

    @Override
    public void onClickModifyNote(KithEntity kithEntity) {
        menuPop.dismiss();
        modifyNotePop = (ModifyNotePop) new XPopup.Builder(getActivity())
                .autoOpenSoftInput(true)
                .asCustom(new ModifyNotePop(getActivity(),kithEntity))
                .show();
        modifyNotePop.setOnClickModifyNoteItemListener(this);
    }

    @Override
    public void onClickHideLocation(KithEntity kithEntity, boolean needHide) {
        mPresenter.updateFriendWithHide(kithEntity.getCustKithId(),needHide);
    }


    @Override
    public void onClickSave(KithEntity kithEntity, String note) {
        if (modifyNotePop != null){
            modifyNotePop.dismiss();
        }
        mPresenter.updateFriendWithNote(kithEntity.getCustKithId(),note);
    }

    @Override
    public void onEvent(FragmentEvent event) {
        super.onEvent(event);
        switch (event.getCode()){
            case EventCode.Main.ALREADYISVIP:
                mBinding.openVip.setImageResource(R.mipmap.isvipback);
                break;
        }
    }
}
