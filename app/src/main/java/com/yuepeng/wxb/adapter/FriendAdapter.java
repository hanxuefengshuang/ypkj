package com.yuepeng.wxb.adapter;

import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseDataBindingHolder;
import com.wstro.thirdlibrary.entity.KithEntity;
import com.wstro.thirdlibrary.utils.GlideUtils;
import com.yuepeng.wxb.R;
import com.yuepeng.wxb.databinding.ItemKithBinding;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * @author:create by Nico
 * company:余舒科技
 * createTime:1/31/21
 * Email:752497253@qq.com
 */
public class FriendAdapter extends BaseQuickAdapter<KithEntity, BaseDataBindingHolder<ItemKithBinding>> {

    private OnEditClickListener listener;

    public void setOnEditClickListener(OnEditClickListener listener){
        this.listener = listener;
    }

    public FriendAdapter( @Nullable List<KithEntity> data) {
        super(R.layout.item_kith, data);
    }

    @Override
    protected void convert(@NotNull BaseDataBindingHolder<ItemKithBinding> helper, KithEntity kithEntity) {
        ItemKithBinding mBinding = helper.getDataBinding();
        GlideUtils.load(getContext(),kithEntity.getHeadImg(),mBinding.avatar, R.mipmap.avatar);
        if (helper.getLayoutPosition() == 0){
            mBinding.edit.setVisibility(View.GONE);
        }else {
            mBinding.edit.setVisibility(View.VISIBLE);
        }
        mBinding.kithNote.setText(kithEntity.getKithNote());
        if (kithEntity.getLocation() != null){
            mBinding.location.setText(kithEntity.getLocation().toString());
        }else {
            mBinding.location.setText("暂无位置");
        }
        mBinding.kithNote.setText(kithEntity.getKithNote());
        if (kithEntity.getTimeDesc() != null){
            mBinding.timeDesc.setText(kithEntity.getTimeDesc().toString());
        }else {
            mBinding.timeDesc.setText("暂无");
        }
        mBinding.rlEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (listener != null){
                    listener.onEditClick(kithEntity);
                }
            }
        });
    }

    public interface OnEditClickListener{

        void onEditClick(KithEntity kithEntity);
    }
}
