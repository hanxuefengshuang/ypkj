package com.yuepeng.wxb.ui.pop;

import android.content.Context;
import android.content.Intent;
import android.view.View;

import com.wstro.thirdlibrary.base.BasePresenter;
import com.yuepeng.wxb.R;
import com.yuepeng.wxb.base.MyBaseCenterPop;
import com.yuepeng.wxb.databinding.PopInviteFriendBinding;
import com.yuepeng.wxb.ui.activity.AddFriendActivity;

import androidx.annotation.NonNull;

/**
 * @author:create by Nico
 * company:余舒科技
 * createTime:2/10/21
 * Email:752497253@qq.com
 */
public class InviteFriendPop extends MyBaseCenterPop<PopInviteFriendBinding, BasePresenter> implements View.OnClickListener {

    public InviteFriendPop(@NonNull Context context) {
        super(context);
    }

    @Override
    protected BasePresenter createPresenter() {
        return null;
    }

    @Override
    protected int getImplLayoutId() {
        return R.layout.pop_invite_friend;
    }

    @Override
    protected void onCreate() {
        super.onCreate();
        mBinding.btnClose.setOnClickListener(this);
        mBinding.btnInvite.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.btnClose){
            dismiss();
        }
        if (id == R.id.btnInvite){
               getContext().startActivity(new Intent(getContext(), AddFriendActivity.class));
               dismiss();
        }
    }
}
