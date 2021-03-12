package com.yuepeng.wxb.presenter.view;

import com.wstro.thirdlibrary.entity.MessageEntity;
import com.wstro.thirdlibrary.entity.MessageResultDate;

import java.util.List;

/**
 * @author:create by Nico
 * company:余舒科技
 * createTime:2/8/21
 * Email:752497253@qq.com
 */
public interface MessageDetailView extends BaseListDetailView {

        void onGetMessageListSuccess(List<MessageResultDate<MessageEntity>>messageList);

        void onPassInviteSuccess(int messageId);

        void onRefusedInviteSuccess(int messageId);
}
