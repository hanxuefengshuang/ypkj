package com.yuepeng.wxb.presenter.view;

import com.wstro.thirdlibrary.base.BaseDetailView;
import com.wstro.thirdlibrary.entity.WeChatPayEntity;

/**
 * @author:create by Nico
 * createTime:2/4/21
 * Email:752497253@qq.com
 */
public interface PayDetailView extends BaseDetailView {

        void onGetPayTokenSuccess(String msg);

        void onGetAlipayOrderSuccess(String aliOrder);

        void onGetWechatPayOrderSuccess(WeChatPayEntity wechatOrder);
}
