package com.yuepeng.wxb.presenter.view;

import com.wstro.thirdlibrary.base.BaseDetailView;
import com.wstro.thirdlibrary.entity.MemberEntity;
import com.wstro.thirdlibrary.entity.VipNumEntity;

import java.util.List;

/**
 * @author:create by Nico
 * company:余舒科技
 * createTime:2/4/21
 * Email:752497253@qq.com
 */
public interface VipDetailView extends BaseDetailView {

        void onGetMemberNumSuccess(VipNumEntity vipNumList);

        void onGetMemberListSuccess(List<MemberEntity.PriceListdata>memberList);
}
