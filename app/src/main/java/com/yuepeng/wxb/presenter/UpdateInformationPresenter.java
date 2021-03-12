package com.yuepeng.wxb.presenter;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.wstro.thirdlibrary.base.BasePresenter;
import com.wstro.thirdlibrary.base.BaseResponse;
import com.wstro.thirdlibrary.entity.ImageResponse;
import com.wstro.thirdlibrary.entity.UserModel;
import com.wstro.thirdlibrary.utils.AppCache;
import com.yuepeng.wxb.action.ToastAction;
import com.yuepeng.wxb.base.App;
import com.yuepeng.wxb.presenter.view.UpdateDetailView;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.io.File;

import okhttp3.Call;
import rx.Subscriber;

/**
 * @author:create by Nico
 * company:余舒科技
 * createTime:2/2/21
 * Email:752497253@qq.com
 */
public class UpdateInformationPresenter extends BasePresenter<UpdateDetailView> implements ToastAction {

    public UpdateInformationPresenter(UpdateDetailView view) {
        super(view);
    }

    public void updateInfoormation(String headImg,String nickName){
        if (headImg.isEmpty()){
            map.put("nickName",nickName);
        }else {
            map.put("headImg",headImg);
            map.put("nickName",nickName);
        }
        addSubscription(mApiService.update(getBody(map)), new Subscriber<BaseResponse>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                mView.onError(e);
            }

            @Override
            public void onNext(BaseResponse baseResponse) {
                if (baseResponse.getCode() == SUCCESSCODE){
                    UserModel userModel = App.getInstance().getUserModel();
                    if (headImg.isEmpty()){
                        userModel.setNickName(nickName);
                    }else {
                        userModel.setNickName(nickName);
                        userModel.setHeadImg(headImg);
                    }
                    App.getInstance().saveUserModel(userModel);
                    mView.onSuccess();
                }else {
                    mView.onfailed(baseResponse);
                }
            }
        });
    }

    public void uploadImg(File file){
        OkHttpUtils.post().url(mApiService.UPLOADFILE)
                .addFile("file","headIcon.jpg",file)
                .addHeader("Content-Type","multipart/form-data;")
                .addHeader("token", AppCache.getAccessToken())
                .build().execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                mView.onError(e);
            }


            @Override
            public void onResponse(String response, int id) {
                ImageResponse imageResponse = new Gson().fromJson(response,new TypeToken<ImageResponse>(){}.getType());
                if (imageResponse.getCode() == SUCCESSCODE){
                    mView.onUploadImgSuccess(imageResponse.getData().getPath());
                }else {
//                    mView.onfailed(imageResponse.getMsg());
                    toast(imageResponse.getMsg());
                }
            }
        });
    }
}
