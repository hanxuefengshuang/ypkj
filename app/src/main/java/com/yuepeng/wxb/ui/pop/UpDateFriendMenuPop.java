package com.yuepeng.wxb.ui.pop;

import android.content.Context;
import android.widget.CompoundButton;

import com.wstro.thirdlibrary.base.BasePresenter;
import com.wstro.thirdlibrary.entity.KithEntity;
import com.yuepeng.wxb.R;
import com.yuepeng.wxb.base.MyBaseCenterPop;
import com.yuepeng.wxb.databinding.PopUpdateFriendMenuBinding;

import androidx.annotation.NonNull;

/**
 * @author:create by Nico
 * createTime:2/9/21
 * Email:752497253@qq.com
 */
public class UpDateFriendMenuPop extends MyBaseCenterPop<PopUpdateFriendMenuBinding, BasePresenter> {

    private KithEntity kithEntity;
    private onClickPopItemListener listener;

    public void setOnUpDateFriendMenuPop(onClickPopItemListener listener){
        this.listener = listener;
    }

    public UpDateFriendMenuPop(@NonNull Context context,KithEntity kithEntity) {
        super(context);
        this.kithEntity = kithEntity;
    }

    @Override
    protected BasePresenter createPresenter() {
        return null;
    }

    @Override
    protected int getImplLayoutId() {
        return R.layout.pop_update_friend_menu;
    }

    @Override
    protected void onCreate() {
        super.onCreate();
        mBinding.rlRelationship.setOnClickListener(view -> {
            if (listener != null){
                listener.onClickRemoveRelationShip(kithEntity);
            }
        });
        switch (kithEntity.getHideLocation()){
            case 0:
                mBinding.sbHideLocation.setSwitchIsChecked(false);
                break;
            case 1:
                mBinding.sbHideLocation.setSwitchIsChecked(true);
                break;
        }

        mBinding.rlNote.setOnClickListener(view -> {
            if (listener != null){
                listener.onClickModifyNote(kithEntity);
            }
        });
        mBinding.sbHideLocation.setOnClickListener(view -> {
            if (listener != null){
                if (mBinding.sbHideLocation.getSwitchIsChecked()){
                    listener.onClickHideLocation(kithEntity,false);
                }else {
                    listener.onClickHideLocation(kithEntity,true);
                }
            }
        });
        mBinding.sbHideLocation.setSwitchCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (listener != null){
                    if (isChecked){
                        listener.onClickHideLocation(kithEntity,true);
                    }else {
                        listener.onClickHideLocation(kithEntity,false);
                    }
                }
            }
        });
    }

    public interface onClickPopItemListener{

        void onClickRemoveRelationShip(KithEntity kithEntity);

        void onClickModifyNote(KithEntity kithEntity);

        void onClickHideLocation(KithEntity kithEntity,boolean needHide);
    }
}
