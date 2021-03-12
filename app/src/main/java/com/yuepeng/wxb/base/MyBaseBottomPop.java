package com.yuepeng.wxb.base;

import android.accounts.NetworkErrorException;
import android.content.Context;
import android.os.NetworkOnMainThreadException;
import android.view.View;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;

import com.github.nukc.stateview.StateView;
import com.google.gson.JsonSyntaxException;
import com.kongzue.dialogx.dialogs.TipDialog;
import com.kongzue.dialogx.dialogs.WaitDialog;
import com.lxj.xpopup.core.BottomPopupView;
import com.lxj.xpopup.impl.LoadingPopupView;
import com.tbruyelle.rxpermissions2.RxPermissions;
import com.wstro.thirdlibrary.base.BasePresenter;
import com.wstro.thirdlibrary.event.FragmentEvent;
import com.yuepeng.wxb.action.ToastAction;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.ConnectException;
import java.net.HttpRetryException;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.concurrent.TimeoutException;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

//import com.rain.crow.bean.MediaData;

public abstract class MyBaseBottomPop<DB extends ViewDataBinding,T extends BasePresenter> extends BottomPopupView implements Consumer<Disposable>, ToastAction {

    protected RxPermissions rxPermissions;
    protected T mPresenter;
    protected View rootView;
    protected StateView mStateView;//用于显示加载中、网络异常，空布局、内容布局
    protected RadioGroup rgLane, rgEvent;
    protected AppCompatEditText etDetail;
    protected String laneInfo = "", eventInfo = "";
    protected AppCompatButton submit;
    //    protected List<MediaData> selectImageList = new ArrayList<>();
    protected RecyclerView rvPhoto;
    protected DB mBinding;
    //    protected ImagePickerAdapter adapter;
    private CompositeDisposable mDisposables = new CompositeDisposable();
    protected AppCompatImageView takeCamera;
    protected AppCompatTextView modifyloc;
    protected AppCompatTextView tvLocation;
    protected RelativeLayout rlOpencamera;
    LoadingPopupView loadingPopup;
    protected double lon, lat;
    protected String address;

    public MyBaseBottomPop(@NonNull Context context) {
        super(context);
        rxPermissions = new RxPermissions((FragmentActivity) context);

    }

    @Override
    protected void onCreate() {
        super.onCreate();
        mPresenter = createPresenter();
        mBinding = DataBindingUtil.bind(getPopupImplView());
    }


    public void dismissDialog() {
        if (loadingPopup != null)
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

    public void showErrorDialog(String msg) {
        TipDialog.show(msg, WaitDialog.TYPE.ERROR, 500);
        }

    public void showException(final Throwable ex) {
                if (ex instanceof HttpRetryException) {
                    showErrorDialog("错误：连接远程服务器重试，请检查网络");
                } else if (ex instanceof ConnectException) {
                    showErrorDialog("错误：连接远程服务器失败，请检查网络");
                    return;
                } else if (ex instanceof TimeoutException) {
                    showErrorDialog("错误：连接远程服务器超时，请检查网络");
                    return;
                } else if (ex instanceof UnknownHostException){
                    showErrorDialog("错误：网络信号差，请检查网络");
                }else if (ex instanceof SocketException) {
                    showErrorDialog("错误：远程服务器连接超时");
                    return;
                } else if (ex instanceof SocketTimeoutException) {
                    showErrorDialog("错误：远程服务器连接出现超时");
                    return;
                } else if (ex instanceof JsonSyntaxException) {
                    showErrorDialog("错误：远程服务器响应数据解析失败");
                } else if (ex instanceof NullPointerException) {
                    showErrorDialog("错误：空数据");
                } else if (ex instanceof IllegalArgumentException) {
                    showErrorDialog("错误：非法参数异常");
                } else if (ex instanceof NumberFormatException) {
                    showErrorDialog("错误：解析数据转换异常");
                } else if (ex instanceof NetworkErrorException) {
                    showErrorDialog("错误：网络未连接");
                } else if (ex instanceof NetworkOnMainThreadException) {
                    showErrorDialog("错误：网络进程异常");
                }else if (ex instanceof FileNotFoundException) {
                    showErrorDialog("错误：文件无法访问");
                } else if (ex instanceof ArrayIndexOutOfBoundsException) {
                    showErrorDialog("错误：数组越界");
                }else if (ex instanceof IOException) {
                    showErrorDialog("错误：数据上传失败");
                }  else {
                    showErrorDialog("错误：其他错误");
                }
//                CrashReport.postCatchedException(ex);

    }

    public void showWarningDialog(String msg){
          TipDialog.show(msg, WaitDialog.TYPE.WARNING,800);
    }

}
