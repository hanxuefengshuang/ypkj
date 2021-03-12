package com.yuepeng.wxb.presenter.view;

import com.wstro.thirdlibrary.entity.KithEntity;

import java.util.List;

/**
 * @author:create by Nico
 * company:余舒科技
 * createTime:1/31/21
 * Email:752497253@qq.com
 */
public interface KithDetailView extends BaseListDetailView {

    void onGetKithListSuccess(List<KithEntity>kithEntityList);

    void onDelFriendSuccess(int custKithId);

    void onUpdateNoteSuccess(int custKithId,String note);

    void onUpdateLocationHideSuccess(int custKithId,boolean needHide);

    void onUpdateLocationHideFail(String msg,int custKithId,boolean needHide);
}
