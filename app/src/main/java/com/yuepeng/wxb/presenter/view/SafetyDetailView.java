package com.yuepeng.wxb.presenter.view;

import com.wstro.thirdlibrary.base.BaseDetailView;
import com.wstro.thirdlibrary.entity.SafetyEntity;

import java.util.List;

/**
 * @author:create by Nico
 * company:余舒科技
 * createTime:2/5/21
 * Email:752497253@qq.com
 */
public interface SafetyDetailView extends BaseDetailView {

        void onGetSafetyPlaceSuccess(List<SafetyEntity>safetyList);

        void onAddSafetyPlaceSuccess();

        void onEditSafetyPlaceSuccess();

        void onDelSafetyPlaceSuccess();
}
