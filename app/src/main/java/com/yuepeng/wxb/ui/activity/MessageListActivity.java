package com.yuepeng.wxb.ui.activity;

import android.view.View;

import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadMoreListener;
import com.wstro.thirdlibrary.entity.MessageEntity;
import com.wstro.thirdlibrary.entity.MessageResultDate;
import com.wstro.thirdlibrary.event.ActivityEvent;
import com.wstro.thirdlibrary.event.EventCode;
import com.wstro.thirdlibrary.event.FragmentEvent;
import com.yuepeng.wxb.R;
import com.yuepeng.wxb.adapter.MessageDateAdapter;
import com.yuepeng.wxb.base.BaseActivity;
import com.yuepeng.wxb.databinding.ActivityMessageListBinding;
import com.yuepeng.wxb.presenter.MessageListPresenter;
import com.yuepeng.wxb.presenter.view.MessageDetailView;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;


public class MessageListActivity extends BaseActivity<ActivityMessageListBinding, MessageListPresenter> implements MessageDetailView, OnRefreshLoadMoreListener {

    private MessageDateAdapter mAdapter;
    private List<MessageResultDate>allDateList = new ArrayList<>();
    private int page = 1;
    private int pageSize = 10;

    @Override
    protected View injectTarget() {
        return mBinding.c1;
    }

    @Override
    protected MessageListPresenter createPresenter() {
        return new MessageListPresenter(this);
    }

    @Override
    protected void Retry() {

    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_message_list;
    }

    @Override
    protected void initView() {
        refreshLayout = mBinding.refreshLayout;
        mBinding.title.titlebar.setTitle("消息提醒");
        mAdapter = new MessageDateAdapter(allDateList);
        mBinding.rv.setLayoutManager(new LinearLayoutManager(this));
        mBinding.rv.setAdapter(mAdapter);
        mBinding.refreshLayout.setOnRefreshLoadMoreListener(this);
    }

    @Override
    protected void initData() {
        mPresenter.getMessageList(page,pageSize);
    }

    @Override
    public void onGetMessageListSuccess(List<MessageResultDate<MessageEntity>> messageList) {
        if (messageList != null){
            mStateView.showContent();
            if (page == 1 && messageList.size()==0){
                mStateView.showEmpty();
                return;
            }
            EventBus.getDefault().post(new FragmentEvent(EventCode.Main.CANCELREAD,true));

            mAdapter.addData(messageList);
            mAdapter.notifyDataSetChanged();
            page ++;
        }
    }

    @Override
    public void onPassInviteSuccess(int messageId) {
        for (MessageResultDate resultDate:allDateList){
            for (int i=0;i<resultDate.getMsgList().size();i++){
                MessageEntity entity = (MessageEntity) resultDate.getMsgList().get(i);
                if (entity.getMessageId() == messageId){
                    entity.setInviteStatus(2);
                }
            }
        }
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onRefusedInviteSuccess(int messageId) {
        for (MessageResultDate resultDate:allDateList){
            for (int i=0;i<resultDate.getMsgList().size();i++){
                MessageEntity entity = (MessageEntity) resultDate.getMsgList().get(i);
                if (entity.getMessageId() == messageId){
                    entity.setInviteStatus(3);
                }
            }
        }
        mAdapter.notifyDataSetChanged();
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
    }

    @Override
    protected boolean enableStateView() {
        return true;
    }

    @Override
    public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
        mPresenter.getMessageList(page,pageSize);
    }

    @Override
    public void onRefresh(@NonNull RefreshLayout refreshLayout) {
        page = 1;
        allDateList.clear();
        mStateView.showLoading();
        mAdapter.notifyDataSetChanged();
        mPresenter.getMessageList(page,pageSize);
    }

    @Override
    public void onEvent(ActivityEvent event) {
        super.onEvent(event);
        switch (event.getCode()){
            case EventCode.AddFriend.PASS:
                mPresenter.updateMessage(2,(int)event.getData());
                break;
            case EventCode.AddFriend.REFUSED:
                mPresenter.updateMessage(3,(int)event.getData());
                break;
        }
    }
}