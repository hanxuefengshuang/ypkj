package com.yuepeng.wxb.presenter.view;

import com.wstro.thirdlibrary.base.BaseDetailView;

/**
 * @author:create by Nico
 * company:余舒科技
 * createTime:1/30/21
 * Email:752497253@qq.com
 */
public interface LoginDetailView extends BaseDetailView {

        void onGetCodeSuccess(String code);

        void onGetLoginInfoSuccess();
}
