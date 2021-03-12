package com.yuepeng.wxb.adapter;

import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseDataBindingHolder;
import com.wstro.thirdlibrary.entity.MessageEntity;
import com.wstro.thirdlibrary.event.ActivityEvent;
import com.wstro.thirdlibrary.event.EventCode;
import com.yuepeng.wxb.R;
import com.yuepeng.wxb.databinding.ItemMessgaeDetailBinding;

import org.greenrobot.eventbus.EventBus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * @author:create by Nico
 * company:余舒科技
 * createTime:2/8/21
 * Email:752497253@qq.com
 */
public class MessageDetailAdapter extends BaseQuickAdapter<MessageEntity, BaseDataBindingHolder<ItemMessgaeDetailBinding>> {

    public MessageDetailAdapter(@Nullable List<MessageEntity> data) {
        super(R.layout.item_messgae_detail, data);
    }

    @Override
    protected void convert(@NotNull BaseDataBindingHolder<ItemMessgaeDetailBinding> helper, MessageEntity messageEntity) {
        ItemMessgaeDetailBinding mBinding = helper.getDataBinding();
        switch (messageEntity.getMessageType()){
            case 1:
                mBinding.msgType.setVisibility(View.VISIBLE);
                mBinding.inviteType.setVisibility(View.GONE);
                mBinding.msgType.setText("安全区域提醒");
                mBinding.pass.setVisibility(View.GONE);
                mBinding.refused.setVisibility(View.GONE);
                mBinding.pass.setVisibility(View.GONE);
                break;
            case 2:
                mBinding.msgType.setVisibility(View.VISIBLE);
                mBinding.msgType.setText("好友请求");
                if (messageEntity.getInviteStatus() != null){
                    switch (messageEntity.getInviteStatus()){
                        case 1:
                            mBinding.inviteType.setVisibility(View.GONE);
                            mBinding.refused.setVisibility(View.VISIBLE);
                            mBinding.pass.setVisibility(View.VISIBLE);
                            break;
                        case 2:
                            mBinding.inviteType.setVisibility(View.VISIBLE);
                            mBinding.inviteType.setText("已通过该请求");
                            mBinding.refused.setVisibility(View.GONE);
                            mBinding.pass.setVisibility(View.GONE);
                            break;
                        case 3:
                            mBinding.inviteType.setVisibility(View.VISIBLE);
                            mBinding.inviteType.setText("已拒绝该请求");
                            mBinding.refused.setVisibility(View.GONE);
                            mBinding.pass.setVisibility(View.GONE);
                            break;
                    }
                }
                mBinding.pass.setOnClickListener(view -> {
                    EventBus.getDefault().post(new ActivityEvent(EventCode.AddFriend.PASS,messageEntity.getMessageId()));
                });
                mBinding.refused.setOnClickListener(view -> {
                    EventBus.getDefault().post(new ActivityEvent(EventCode.AddFriend.REFUSED,messageEntity.getMessageId()));
                });
                break;
            case 3:
                if (messageEntity.getInviteStatus() != null){
                    switch (messageEntity.getInviteStatus()){
                        case 1:
                            mBinding.msgType.setText("待确认提醒");
                            break;
                        case 2:
                            mBinding.msgType.setText("通过提醒");
                            break;
                        case 3:
                            mBinding.msgType.setText("拒绝提醒");
                            break;
                    }
                }
                mBinding.inviteType.setVisibility(View.GONE);
                mBinding.pass.setVisibility(View.GONE);
                mBinding.msgType.setVisibility(View.GONE);
                mBinding.refused.setVisibility(View.GONE);
                mBinding.pass.setVisibility(View.GONE);
                break;
        }
        mBinding.content.setText(messageEntity.getContent());
    }

}
