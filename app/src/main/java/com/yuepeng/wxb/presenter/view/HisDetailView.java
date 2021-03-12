package com.yuepeng.wxb.presenter.view;

import com.wstro.thirdlibrary.base.BaseDetailView;
import com.wstro.thirdlibrary.entity.HisTimeResponse;
import com.wstro.thirdlibrary.entity.TjDateResponse;

import java.util.List;

/**
 * Created by wangjun on 2021/2/7.
 */
public interface HisDetailView extends BaseDetailView {

    void onTjDateSuccess(HisTimeResponse response);

    void onTjDateListSuccess(List<TjDateResponse> data);
}
