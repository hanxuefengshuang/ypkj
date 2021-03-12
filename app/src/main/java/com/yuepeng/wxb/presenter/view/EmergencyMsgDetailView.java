package com.yuepeng.wxb.presenter.view;

import com.wstro.thirdlibrary.base.BaseDetailView;
import com.wstro.thirdlibrary.entity.EmergencyMsgEntity;

/**
 * @author:create by Nico
 * company:余舒科技
 * createTime:2/19/21
 * Email:752497253@qq.com
 */
public interface EmergencyMsgDetailView extends BaseDetailView {

        void onGetEmergencyMsgSuccess(EmergencyMsgEntity emergencyMsgEntity);
}
