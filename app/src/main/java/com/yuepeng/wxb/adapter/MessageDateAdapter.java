package com.yuepeng.wxb.adapter;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseDataBindingHolder;
import com.wstro.thirdlibrary.entity.MessageEntity;
import com.wstro.thirdlibrary.entity.MessageResultDate;
import com.yuepeng.wxb.R;
import com.yuepeng.wxb.databinding.ItemMessageDateBinding;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

import androidx.recyclerview.widget.LinearLayoutManager;

/**
 * @author:create by Nico
 * company:余舒科技
 * createTime:2/8/21
 * Email:752497253@qq.com
 */
public class MessageDateAdapter extends BaseQuickAdapter<MessageResultDate, BaseDataBindingHolder<ItemMessageDateBinding>> {

    private MessageDetailAdapter mAdapter;
    private List<MessageEntity> messageEntityList;

    public MessageDateAdapter(@Nullable List<MessageResultDate> data) {
        super(R.layout.item_message_date, data);
    }

    @Override
    protected void convert(@NotNull BaseDataBindingHolder<ItemMessageDateBinding> helper, MessageResultDate messageResultDate) {
        ItemMessageDateBinding mBinding = helper.getDataBinding();
        mBinding.msgDate.setText(messageResultDate.getMsgDate());
        messageEntityList = messageResultDate.getMsgList();
        mAdapter = new MessageDetailAdapter(messageEntityList);
        mBinding.rvMsgList.setLayoutManager(new LinearLayoutManager(getContext()));
        mBinding.rvMsgList.setAdapter(mAdapter);
    }
}
