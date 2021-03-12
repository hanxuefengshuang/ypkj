package com.yuepeng.wxb.presenter.view;

import com.wstro.thirdlibrary.base.BaseDetailView;

/**
 * @author:create by Nico
 * company:余舒科技
 * createTime:2/7/21
 * Email:752497253@qq.com
 */
public interface AddFriendDetailView extends BaseDetailView {

    void onCheckResultWithNoBind(String mobile);

    void onCheckResultWithNotRegister(String mobile);

    void onCheckFailed(String mobile,String msg);

    void onInviteSuccess();
}
