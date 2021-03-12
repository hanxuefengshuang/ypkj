package com.yuepeng.wxb.base;

import android.content.Context;
import android.widget.EditText;

import com.github.nukc.stateview.StateView;
import com.lxj.xpopup.core.CenterPopupView;
import com.lxj.xpopup.impl.LoadingPopupView;
import com.tbruyelle.rxpermissions2.RxPermissions;
import com.wstro.thirdlibrary.base.BasePresenter;
import com.wstro.thirdlibrary.event.FragmentEvent;
import com.yuepeng.wxb.action.ToastAction;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

//import com.rain.crow.bean.MediaData;

public abstract class MyBaseCenterPop<DB extends ViewDataBinding,T extends BasePresenter> extends CenterPopupView implements Consumer<Disposable>, ToastAction {

    protected RxPermissions rxPermissions;
    protected T mPresenter;
    protected StateView mStateView;//用于显示加载中、网络异常，空布局、内容布局
    protected AppCompatButton submit;
    protected RecyclerView rvPhoto;
    protected DB mBinding;
    private CompositeDisposable mDisposables = new CompositeDisposable();
    LoadingPopupView loadingPopup;
    protected int position = 0;

    public MyBaseCenterPop(@NonNull Context context) {
        super(context);
        rxPermissions = new RxPermissions((FragmentActivity) context);

    }

    @Override
    protected void onCreate() {
        super.onCreate();
        mPresenter = createPresenter();
        mBinding = DataBindingUtil.bind(getPopupImplView());
    }


    public void dismissDialog(){
        if (loadingPopup!=null)
            loadingPopup.dismiss();
    }

    //用于创建Presenter和判断是否使用MVP模式(由子类实现)
    protected abstract T createPresenter();

    @Override
    public void accept(Disposable disposable) throws Exception {
        mDisposables.add(disposable);
    }

    @Override
    protected void onDismiss() {
        super.onDismiss();
        mDisposables.clear();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(FragmentEvent event) {

    }

    protected String getEditText(EditText editText){
        return editText.getText().toString().trim();
    }


}
