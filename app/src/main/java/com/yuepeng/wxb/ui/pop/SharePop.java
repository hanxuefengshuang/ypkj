package com.yuepeng.wxb.ui.pop;

import android.content.Context;
import android.view.View;

import com.baidu.mapapi.model.LatLng;
import com.wstro.thirdlibrary.base.BasePresenter;
import com.wstro.thirdlibrary.entity.UserModel;
import com.wstro.thirdlibrary.utils.WechatShareModel;
import com.wstro.thirdlibrary.utils.WechatShareTools;
import com.yuepeng.wxb.R;
import com.yuepeng.wxb.base.App;
import com.yuepeng.wxb.base.MyBaseBottomPop;
import com.yuepeng.wxb.databinding.PopShareBinding;
import com.yuepeng.wxb.utils.LocationTools;
import com.yuepeng.wxb.utils.PreUtils;

import androidx.annotation.NonNull;


/**
 * @author:create by Nico
 * createTime:2/2/21
 * Email:752497253@qq.com
 */
public class SharePop extends MyBaseBottomPop<PopShareBinding, BasePresenter> implements View.OnClickListener {

    private UserModel userModel;
    private double[]gbLocation;

    public SharePop(@NonNull Context context) {
        super(context);
    }

    @Override
    protected BasePresenter createPresenter() {
        return null;
    }

    @Override
    protected int getImplLayoutId() {
        return R.layout.pop_share;
    }

    @Override
    protected void onCreate() {
        super.onCreate();
        userModel= App.getInstance().getUserModel();
        mBinding.rlWechat.setOnClickListener(this);
        mBinding.rlCircleFriend.setOnClickListener(this);
        LatLng myLocation = PreUtils.getLocation();
        gbLocation = LocationTools.calBD09toGCJ02(myLocation.latitude,myLocation.longitude);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.rlWechat){
            if (!WechatShareTools.isWechatInstall()){
                showWarningDialog("您未安装微信，请先安装微信");
                return;
            }
            WechatShareModel wechatShareModel = new WechatShareModel("https://wxb.bid-china.net/share/index.html#/?nickname="+userModel.getNickName()+"&pic="+userModel.getHeadImg()+"&latitude="+ gbLocation[0] +"&longitude="+gbLocation[1],  "亲友定位就用位寻宝","下载即可定位，守护亲友安全");
            WechatShareTools.shareURL(wechatShareModel, WechatShareTools.SharePlace.Friend,getContext());
            dismiss();
        }

        if (id == R.id.rlCircleFriend){
            if (!WechatShareTools.isWechatInstall()){
                showWarningDialog("您未安装微信，请先安装微信");
                return;
            }
            WechatShareModel wechatShareModel = new WechatShareModel("https://wxb.bid-china.net/share/index.html#/?nickname="+userModel.getNickName()+"&pic="+userModel.getHeadImg()+"&latitude="+ gbLocation[0] +"&longitude="+gbLocation[1],  "亲友定位就用位寻宝","下载即可定位，守护亲友安全");
            WechatShareTools.shareURL(wechatShareModel, WechatShareTools.SharePlace.Zone,getContext());
            dismiss();
        }

    }

    private void share(String name){

    }
}
