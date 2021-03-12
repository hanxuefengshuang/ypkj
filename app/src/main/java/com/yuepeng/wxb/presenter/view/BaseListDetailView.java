package com.yuepeng.wxb.presenter.view;

public interface BaseListDetailView {

    void onSuccess();

    void onError(Throwable e);

    void onFailed(String msg);

    void onFinishRefreshAndLoadMore();

    void onFinishRefreshAndLoadMoreWithNoMoreData();

//    void dismissProgressDialog();
}
