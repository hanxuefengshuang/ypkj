package com.yuepeng.wxb.ui.pop;

import android.content.Context;
import android.view.View;

import com.wstro.thirdlibrary.base.BasePresenter;
import com.wstro.thirdlibrary.entity.KithEntity;
import com.yuepeng.wxb.R;
import com.yuepeng.wxb.base.MyBaseCenterPop;
import com.yuepeng.wxb.databinding.PopModifyNoteBinding;

import androidx.annotation.NonNull;

/**
 * @author:create by Nico
 * company:余舒科技
 * createTime:2/9/21
 * Email:752497253@qq.com
 */
public class ModifyNotePop extends MyBaseCenterPop<PopModifyNoteBinding, BasePresenter> implements View.OnClickListener {

    private onClickModifyNoteItemListener listener;
    private KithEntity kithEntity;

    public void setOnClickModifyNoteItemListener(onClickModifyNoteItemListener listener){
        this.listener = listener;
    }

    public ModifyNotePop(@NonNull Context context,KithEntity kithEntity) {
        super(context);
        this.kithEntity = kithEntity;
    }

    @Override
    protected BasePresenter createPresenter() {
        return null;
    }

    @Override
    protected int getImplLayoutId() {
        return R.layout.pop_modify_note;
    }

    @Override
    protected void onCreate() {
        super.onCreate();
        mBinding.btnCancel.setOnClickListener(this);
        mBinding.btnSave.setOnClickListener(this);
        mBinding.etNote.setText(kithEntity.getKithNote());
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.btnCancel){
            dismiss();
        }
        if (id == R.id.btnSave){
            if (getEditText(mBinding.etNote).isEmpty()){
                toast("备注不能为空");
                return;
            }
            if (listener != null){
                listener.onClickSave(kithEntity,getEditText(mBinding.etNote));
            }
        }
    }

    public interface onClickModifyNoteItemListener{

        void onClickSave(KithEntity kithEntity,String note);
    }
}
