package com.yuepeng.wxb.ui.activity;

import android.view.View;

import com.lxj.xpopup.XPopup;
import com.wstro.thirdlibrary.base.BaseResponse;
import com.wstro.thirdlibrary.entity.ContactEntity;
import com.wstro.thirdlibrary.event.ActivityEvent;
import com.wstro.thirdlibrary.event.EventCode;
import com.xuexiang.xui.widget.dialog.materialdialog.DialogAction;
import com.xuexiang.xui.widget.dialog.materialdialog.MaterialDialog;
import com.yuepeng.wxb.R;
import com.yuepeng.wxb.adapter.ContactAdapter;
import com.yuepeng.wxb.base.App;
import com.yuepeng.wxb.base.BaseActivity;
import com.yuepeng.wxb.databinding.ActivityContactBinding;
import com.yuepeng.wxb.presenter.ContactPresenter;
import com.yuepeng.wxb.presenter.view.ContactDetailView;
import com.yuepeng.wxb.ui.pop.AddEditContactPop;
import com.yuepeng.wxb.ui.pop.OpenVipPop;
import com.yuepeng.wxb.utils.PreUtils;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;

/**
*@author:create by Nico
*createTime:2/8/21 9:37 AM
*Email:752497253@qq.com
*Des:  紧急联系人列表
*/
public class ContactActivity extends BaseActivity<ActivityContactBinding, ContactPresenter> implements ContactDetailView, ContactAdapter.onClickAddEditListener, View.OnClickListener {

    private List<ContactEntity>allContactList = new ArrayList<>();
    private ContactAdapter mAdapter;
    private AddEditContactPop addEditContactPop;
    private OpenVipPop openVipPop;
    private int memberType;
    @Override
    protected View injectTarget() {
        return null;
    }

    @Override
    protected ContactPresenter createPresenter() {
        return new ContactPresenter(this);
    }

    @Override
    protected void Retry() {

    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_contact;
    }

    @Override
    protected void initView() {
        mBinding.rv.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new ContactAdapter(allContactList);
        mBinding.rv.setAdapter(mAdapter);

    }

    @Override
    protected void onResume() {
        super.onResume();
        memberType = App.getInstance().getUserModel().getMemberType();
    }

    @Override
    protected void initData() {
        mPresenter.getContactList();
        mAdapter.setOnClickAddEditListener(this);
        mBinding.btnForHelp.setOnClickListener(this);
    }

    @Override
    public void onGetContactListSuccess(List<ContactEntity> contactList) {
        allContactList.clear();
        mAdapter.addData(contactList);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onRightClick(View v) {
        openActivity(DirectionsForUseActivity.class);
    }

    @Override
    public void onAddCustContactSuccess() {
//        allContactList.clear();
        if (addEditContactPop != null){
            addEditContactPop.dismiss();
        }
        showSuccessDialog("添加成功");
        mPresenter.getContactList();
    }

    @Override
    public void onEditCustContactSuccess() {
//        allContactList.clear();
        if (addEditContactPop != null){
            addEditContactPop.dismiss();
        }
        showSuccessDialog("修改成功");
        mPresenter.getContactList();
    }

    @Override
    public void onDelCustContactSuccess() {
        showSuccessDialog("删除成功");
        mPresenter.getContactList();
    }

    @Override
    public void onAddSeekHelpSuccess() {
        showSuccessDialog("求助成功");
    }

    @Override
    public void onSuccess() {

    }

    @Override
    public void onError(Throwable e) {
        showException(e);
    }

    @Override
    public void onfailed(BaseResponse baseResponse) {
        showErrorDialog(baseResponse);
    }

    @Override
    public void onClickAddEditBtn(int position) {
        if (memberType == 0){
            openVipPop = (OpenVipPop) new XPopup.Builder(this)
                    .dismissOnBackPressed(false)
                    .dismissOnTouchOutside(false)
                    .asCustom(new OpenVipPop(this))
                    .show();
            return;
        }
        addEditContactPop = (AddEditContactPop) new XPopup.Builder(this)
                .autoOpenSoftInput(true)
                .asCustom(new AddEditContactPop(this,allContactList.get(position)))
                .show();
    }

    @Override
    public void onCLickDeleteBtn(int position) {
        mPresenter.delCustContact(allContactList.get(position).getCustContactId());
    }

    @Override
    public void onEvent(ActivityEvent event) {
        super.onEvent(event);
        ContactEntity contactEntity = (ContactEntity) event.getData();
        switch (event.getCode()){
            case EventCode.Mine.ADDCUSTCONTACT:
                mPresenter.addCustContact(contactEntity.getContactMobile(),contactEntity.getContactName());
                break;
            case EventCode.Mine.EDITCUSTCONTACT:
                mPresenter.editCustContact(contactEntity.getCustContactId(),contactEntity.getContactMobile(),contactEntity.getContactName());
                break;
        }
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.btnForHelp){
            if (memberType == 0){
                openVipPop = (OpenVipPop) new XPopup.Builder(this)
                        .dismissOnBackPressed(false)
                        .dismissOnTouchOutside(false)
                        .asCustom(new OpenVipPop(this))
                        .show();
                return;
            }
            new MaterialDialog.Builder(this)
                    .title("紧急求助提示")
                    .content("确认对所有紧急联系人发送求助？")
                    .positiveText("确定")
                    .positiveColor(getResources().getColor(R.color.appThemeColor))
                    .negativeColor(getResources().getColor(R.color.adadad))
                    .negativeText("取消")
                    .onPositive(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                            String lat = PreUtils.getLocation().latitude+"";
                            String lon = PreUtils.getLocation().longitude+"";
                            String address = PreUtils.getAddress();
                            mPresenter.addSeekHelpRecord(address,lat,lon);
                            dialog.dismiss();
                        }
                    })
                    .show();

        }
    }
}