package com.yuepeng.wxb.ui.pop;

import android.content.Context;
import android.view.View;

import com.wstro.thirdlibrary.base.BasePresenter;
import com.wstro.thirdlibrary.entity.ContactEntity;
import com.wstro.thirdlibrary.event.ActivityEvent;
import com.wstro.thirdlibrary.event.EventCode;
import com.wstro.thirdlibrary.utils.RegexUtil;
import com.yuepeng.wxb.R;
import com.yuepeng.wxb.base.MyBaseCenterPop;
import com.yuepeng.wxb.databinding.PopAddeditcontactBinding;
import com.yuepeng.wxb.presenter.view.ContactDetailView;

import org.greenrobot.eventbus.EventBus;

import androidx.annotation.NonNull;

/**
 * @author:create by Nico
 * company:余舒科技
 * createTime:2/6/21
 * Email:752497253@qq.com
 */
public class AddEditContactPop extends MyBaseCenterPop<PopAddeditcontactBinding, BasePresenter> implements View.OnClickListener {

    private ContactEntity contactEntity;
    private int type;

    public AddEditContactPop(@NonNull Context context, ContactEntity contactEntity) {
        super(context);
        this.contactEntity = contactEntity;
    }

    @Override
    protected BasePresenter<ContactDetailView> createPresenter() {
        return null;
    }

    @Override
    protected int getImplLayoutId() {
        return R.layout.pop_addeditcontact;
    }

    @Override
    protected void onCreate() {
        super.onCreate();
        if (contactEntity.getAddTime() == null){
            mBinding.title.setText("添加紧急联系人");
            type = 1;
        }else {
            mBinding.title.setText("编辑紧急联系人");
            type = 2;
            mBinding.etName.setText(contactEntity.getContactName());
            mBinding.etMobile.setText(contactEntity.getContactMobile());
        }
        mBinding.btnCancel.setOnClickListener(this);
        mBinding.btnSave.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.btnCancel){
            dismiss();
        }
        if (id == R.id.btnSave){
            if (getEditText(mBinding.etName).isEmpty()){
                toast("紧急联系人姓名不能为空");
                return;
            }
            if (getEditText(mBinding.etMobile).isEmpty()){
                toast("紧急联系人电话不能为空");
                return;
            }
            if (!RegexUtil.checkMobile(getEditText(mBinding.etMobile))){
                toast("请输入正确的手机号");
                return;
            }
            contactEntity.setContactName(getEditText(mBinding.etName));
            contactEntity.setContactMobile(getEditText(mBinding.etMobile));
            if (type == 1){
                EventBus.getDefault().post(new ActivityEvent(EventCode.Mine.ADDCUSTCONTACT,contactEntity));
                dismiss();
            }else {
                EventBus.getDefault().post(new ActivityEvent(EventCode.Mine.EDITCUSTCONTACT,contactEntity));
                dismiss();
            }
//            mPresenter.addCustContact(getEditText(mBinding.etMobile),getEditText(mBinding.etName));
        }
    }

}
