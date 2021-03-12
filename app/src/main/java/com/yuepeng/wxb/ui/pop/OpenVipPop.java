package com.yuepeng.wxb.ui.pop;

import android.content.Context;
import android.content.Intent;
import android.view.View;

import com.wstro.thirdlibrary.base.BasePresenter;
import com.yuepeng.wxb.R;
import com.yuepeng.wxb.base.MyBaseCenterPop;
import com.yuepeng.wxb.databinding.PopOpenvipBinding;
import com.yuepeng.wxb.ui.activity.VipActivity;

import androidx.annotation.NonNull;

/**
 * @author:create by Nico
 * company:余舒科技
 * createTime:2/10/21
 * Email:752497253@qq.com
 */
public class OpenVipPop extends MyBaseCenterPop<PopOpenvipBinding, BasePresenter> implements View.OnClickListener {

    public OpenVipPop(@NonNull Context context) {
        super(context);
    }

    @Override
    protected BasePresenter createPresenter() {
        return null;
    }

    @Override
    protected int getImplLayoutId() {
        return R.layout.pop_openvip;
    }

    @Override
    protected void onCreate() {
        super.onCreate();
        mBinding.btnOpenVip.setOnClickListener(this);
        mBinding.btnClose.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.btnOpenVip){
            getContext().startActivity(new Intent(getContext(), VipActivity.class));
            dismiss();
        }
        if (id == R.id.btnClose){
            dismiss();
        }
    }
}
