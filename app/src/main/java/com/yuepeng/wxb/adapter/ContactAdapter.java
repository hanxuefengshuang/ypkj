package com.yuepeng.wxb.adapter;

import android.graphics.Color;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseDataBindingHolder;
import com.wstro.thirdlibrary.entity.ContactEntity;
import com.yuepeng.wxb.R;
import com.yuepeng.wxb.databinding.ItemContactBinding;
import com.yuepeng.wxb.ui.pop.AddEditContactPop;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * @author:create by Nico
 * createTime:2/6/21
 * Email:752497253@qq.com
 */
public class ContactAdapter extends BaseQuickAdapter<ContactEntity, BaseDataBindingHolder<ItemContactBinding>> {

    private onClickAddEditListener listener;
    private AddEditContactPop addEditContactPop;

    public void setOnClickAddEditListener(onClickAddEditListener listener){
        this.listener = listener;
    }

    public ContactAdapter( @Nullable List<ContactEntity> data) {
        super(R.layout.item_contact, data);
    }

    @Override
    protected void convert(@NotNull BaseDataBindingHolder<ItemContactBinding> helper, ContactEntity contactEntity) {
        ItemContactBinding mBinding = helper.getDataBinding();
        if (contactEntity.getAddTime() != null){
            mBinding.contactName.setText(contactEntity.getContactName());
            mBinding.contactMobile.setText(contactEntity.getContactMobile());
            mBinding.btn.setBackgroundResource(R.drawable.shape_safety_modify);
            mBinding.btn.setText("修改");
            mBinding.contactName.setTextColor(Color.parseColor("#FF333333"));
            mBinding.btnDelete.setVisibility(View.VISIBLE);
            mBinding.contactMobile.setVisibility(View.VISIBLE);
            mBinding.btnDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (listener != null){
                        listener.onCLickDeleteBtn(helper.getLayoutPosition());
                    }
                }
            });
        }else {
            mBinding.contactName.setText("紧急联系人"+(helper.getLayoutPosition()+1));
            mBinding.contactName.setTextColor(Color.parseColor("#FF999999"));
            mBinding.btn.setBackgroundResource(R.drawable.shape_safety_add);
            mBinding.btn.setText("添加");
            mBinding.btnDelete.setVisibility(View.GONE);
            mBinding.contactMobile.setVisibility(View.GONE);
        }

        mBinding.btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (listener != null){
                    listener.onClickAddEditBtn(helper.getLayoutPosition());
                }
            }
        });
    }

    public interface onClickAddEditListener{
        void onClickAddEditBtn(int position);

        void onCLickDeleteBtn(int position);
    }
}
