package com.yuepeng.wxb.presenter;

import com.wstro.thirdlibrary.base.BasePresenter;
import com.wstro.thirdlibrary.base.BaseResponse;
import com.wstro.thirdlibrary.entity.MessageEntity;
import com.wstro.thirdlibrary.entity.MessageResultDate;
import com.wstro.thirdlibrary.entity.MessageResultEntity;
import com.yuepeng.wxb.presenter.view.MessageDetailView;
import com.yuepeng.wxb.utils.PreUtils;

import rx.Subscriber;

/**
 * @author:create by Nico
 * company:余舒科技
 * createTime:2/8/21
 * Email:752497253@qq.com
 */
public class MessageListPresenter extends BasePresenter<MessageDetailView> {

    public MessageListPresenter(MessageDetailView view) {
        super(view);
    }

    public void getMessageList(int page,int pageSize){
        map.put("page",page);
        map.put("pageSize",pageSize);
        addSubscription(mApiService.getMessageList(getBody(map)), new Subscriber<BaseResponse<MessageResultEntity<MessageResultDate<MessageEntity>>>>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                mView.onError(e);
            }

            @Override
            public void onNext(BaseResponse<MessageResultEntity<MessageResultDate<MessageEntity>>> response) {
                if (response.getCode() == SUCCESSCODE){
                    if (page < response.getData().getPages()){
                        mView.onFinishRefreshAndLoadMore();
                    }else {
                        mView.onFinishRefreshAndLoadMoreWithNoMoreData();
                    }
                    PreUtils.putInt(PreUtils.KEY_READ_MSG_COUNT,response.getData().getTotal());
                    mView.onGetMessageListSuccess(response.getData().getResult());
                }else {
                    mView.onFailed(response.getMsg());
                }
            }
        });
    }

    public void updateMessage(int inviteStatus,int messageId){
        map.put("inviteStatus",inviteStatus);
        map.put("messageId",messageId);
        addSubscription(mApiService.updateMessage(getBody(map)), new Subscriber<BaseResponse>() {
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
                    if (inviteStatus == 2){
                        mView.onPassInviteSuccess(messageId);
                    }else {
                        mView.onRefusedInviteSuccess(messageId);
                    }
                }else {
                    mView.onFailed(baseResponse.getMsg());
                }
            }
        });
    }
}
