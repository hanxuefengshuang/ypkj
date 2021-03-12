package com.yuepeng.wxb.adapter;

import android.text.TextPaint;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseDataBindingHolder;
import com.wstro.thirdlibrary.entity.SafetyEntity;
import com.yuepeng.wxb.R;
import com.yuepeng.wxb.databinding.ItemSafeplaceBinding;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * @author:create by Nico
 * company:余舒科技
 * createTime:2/5/21
 * Email:752497253@qq.com
 */
public class SafetyPlaceAdapter extends BaseQuickAdapter<SafetyEntity, BaseDataBindingHolder<ItemSafeplaceBinding>> {

    private onClickAddSafetyPlaceListener listener;

    public void setOnClickAddSafetyPlaceListener(onClickAddSafetyPlaceListener listener){
        this.listener = listener;
    }

    public SafetyPlaceAdapter( @Nullable List<SafetyEntity> data) {
        super(R.layout.item_safeplace, data);
    }

    @Override
    protected void convert(@NotNull BaseDataBindingHolder<ItemSafeplaceBinding> helper, SafetyEntity safetyEntity) {
        ItemSafeplaceBinding mBinding = helper.getDataBinding();
        if (safetyEntity.getAddTime() != null){
            mBinding.areaName.setVisibility(View.VISIBLE);
            mBinding.areaAlias.setText(safetyEntity.getAreaAlias());
            String radius = safetyEntity.getRemindRange()>= 1000? safetyEntity.getRemindRange()/1000 + "km":safetyEntity.getRemindRange()+"m";
            mBinding.remindRange.setText("("+radius+")");
            mBinding.areaName.setText(safetyEntity.getAreaName());
            mBinding.btn.setBackgroundResource(R.drawable.shape_safety_modify);
            mBinding.btn.setText("修改");
            TextPaint tp = mBinding.areaAlias.getPaint();
            tp.setFakeBoldText(true);
        }else {
            mBinding.areaName.setVisibility(View.GONE);
            TextPaint tp = mBinding.areaAlias.getPaint();
            tp.setFakeBoldText(false);
            mBinding.areaAlias.setText("安全位置");
            mBinding.btn.setBackgroundResource(R.drawable.shape_safety_add);
            mBinding.btn.setText("添加");
        }
        mBinding.btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (listener != null){
                    listener.onClickAddSafetyPlace(helper.getLayoutPosition());
                }
            }
        });
    }
    public interface onClickAddSafetyPlaceListener{

        void onClickAddSafetyPlace(int position);
    }
}
