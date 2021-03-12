package com.yuepeng.wxb.ui.pop;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.provider.Settings;
import android.view.View;

import com.wstro.thirdlibrary.base.BasePresenter;
import com.yuepeng.wxb.R;
import com.yuepeng.wxb.base.MyBaseCenterPop;
import com.yuepeng.wxb.databinding.PopGpsSetBinding;
import com.yuepeng.wxb.presenter.view.ContactDetailView;

import androidx.annotation.NonNull;

import static com.yuepeng.wxb.ui.fragment.MapFragment.REQUEST_CODE_OPEN_GPS;

/**
 * @author:create by Nico
 * company:余舒科技
 * createTime:2/6/21
 * Email:752497253@qq.com
 */
public class GPSSetPop extends MyBaseCenterPop<PopGpsSetBinding, BasePresenter> implements View.OnClickListener {

    public GPSSetPop(@NonNull Context context) {
        super(context);
    }

    @Override
    protected BasePresenter<ContactDetailView> createPresenter() {
        return null;
    }

    @Override
    protected int getImplLayoutId() {
        return R.layout.pop_gps_set;
    }

    @Override
    protected void onCreate() {
        super.onCreate();

        mBinding.btnCancel.setOnClickListener(this);
        mBinding.btnSave.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.btnCancel){
            dismiss();
        }
        if (id == R.id.btnSave){
            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            ((Activity)getContext()).startActivityForResult(intent, REQUEST_CODE_OPEN_GPS);
            dismiss();
        }
    }

}
