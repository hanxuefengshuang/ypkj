package com.yuepeng.wxb.presenter.view;

import com.wstro.thirdlibrary.base.BaseDetailView;
import com.wstro.thirdlibrary.entity.ContactEntity;

import java.util.List;

/**
 * @author:create by Nico
 * company:余舒科技
 * createTime:2/6/21
 * Email:752497253@qq.com
 */
public interface ContactDetailView extends BaseDetailView {

        void onGetContactListSuccess(List<ContactEntity>contactList);

        void onAddCustContactSuccess();

        void onEditCustContactSuccess();

        void onDelCustContactSuccess();

        void onAddSeekHelpSuccess();
}
