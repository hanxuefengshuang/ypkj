package com.yuepeng.wxb.base;

import android.accounts.NetworkErrorException;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.os.NetworkOnMainThreadException;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.alibaba.android.arouter.launcher.ARouter;
import com.github.nukc.stateview.StateView;
import com.google.gson.JsonSyntaxException;
import com.gyf.immersionbar.ImmersionBar;
import com.hjq.bar.TitleBar;
import com.kongzue.dialogx.dialogs.TipDialog;
import com.kongzue.dialogx.dialogs.WaitDialog;
import com.kongzue.dialogx.interfaces.DialogLifecycleCallback;
import com.kongzue.dialogx.interfaces.OnBackPressedListener;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.wstro.thirdlibrary.base.BasePresenter;
import com.wstro.thirdlibrary.base.BaseResponse;
import com.wstro.thirdlibrary.event.ActivityEvent;
import com.yuepeng.wxb.R;
import com.yuepeng.wxb.action.HandlerAction;
import com.yuepeng.wxb.action.TitleBarAction;
import com.yuepeng.wxb.action.ToastAction;
import com.yuepeng.wxb.ui.activity.LoginActivity;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.net.ConnectException;
import java.net.HttpRetryException;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.concurrent.TimeoutException;

import androidx.annotation.CallSuper;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import cn.bingoogolapple.swipebacklayout.BGASwipeBackHelper;


public abstract class BaseActivity<DB extends ViewDataBinding,T extends BasePresenter>extends AppCompatActivity implements TitleBarAction, ToastAction, HandlerAction, BGASwipeBackHelper.Delegate {
    protected T mPresenter;
    private static long mPreTime;
    private static Activity mCurrentActivity;// 对所有activity进行管理
    public static List<Activity> mActivities = new LinkedList<Activity>();
    protected Bundle savedInstanceState;
    protected InputMethodManager inputMethodManager;
    protected WeakReference<BaseActivity> activity;
    protected DB mBinding;
    /** 标题栏对象 */
    private TitleBar mTitleBar;
    /** 状态栏沉浸 */
    private ImmersionBar mImmersionBar;
    private boolean isAlive = false;
    protected StateView mStateView;
    protected SmartRefreshLayout refreshLayout;
    protected BGASwipeBackHelper mSwipeBackHelper;
    protected Context mContext;
    public BaseActivity me = this;
    protected boolean firstVisible = true;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ARouter.getInstance().inject(this);
        initSwipeBackFinish();
        mContext = this;
        activity = new WeakReference<BaseActivity>(this);
        this.savedInstanceState = savedInstanceState;
        EventBus.getDefault().register(this);
        synchronized (mActivities) {
            mActivities.add(this);
        }
        isAlive = true;
        mPresenter = createPresenter();
        inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        initLayout();
        initView();
        initData();

        initSoftKeyboard();
        initListener();
//        DialogSettings.style = DialogSettings.STYLE.STYLE_IOS;
//        DialogSettings.theme = DialogSettings.THEME.LIGHT;
//        Notification.mode = Notification.Mode.FLOATING_WINDOW;
    }

    protected  boolean enableStateView(){
        return false;
    };

    protected abstract View injectTarget();

    protected void finishRefreshAndLoadMore(){
        if (refreshLayout != null){
            refreshLayout.finishRefresh();
            refreshLayout.finishLoadMore();
        }
    }

    protected void finishRefreshAndLoadMoreWithNoMoreData(){
        if (refreshLayout != null){
            refreshLayout.finishRefresh();
            refreshLayout.finishLoadMoreWithNoMoreData();
        }
    }


    private void showNotification(String content) {

    }


    protected abstract T createPresenter();



    /**
     * 初始化滑动返回。在 super.onCreate(savedInstanceState) 之前调用该方法
     */
    private void initSwipeBackFinish() {
        mSwipeBackHelper = new BGASwipeBackHelper(this, this);
        mSwipeBackHelper.setIsOnlyTrackingLeftEdge(false);
    }

    protected  boolean checkEdit(EditText et){
        return (!et.getText().toString().trim().isEmpty());
    }

    /**
     * 是否支持滑动返回。这里在父类中默认返回 true 来支持滑动返回，如果某个界面不想支持滑动返回则重写该方法返回 false 即可
     *
     * @return
     */
    @Override
    public boolean isSupportSwipeBack() {
        return true;
    }

    /**
     * 正在滑动返回
     *
     * @param slideOffset 从 0 到 1
     */
    @Override
    public void onSwipeBackLayoutSlide(float slideOffset) {
    }

    /**
     * 没达到滑动返回的阈值，取消滑动返回动作，回到默认状态
     */
    @Override
    public void onSwipeBackLayoutCancel() {
    }

    /**
     * 滑动返回执行完毕，销毁当前 Activity
     */
    @Override
    public void onSwipeBackLayoutExecuted() {
        mSwipeBackHelper.swipeBackward();
    }

    @Override
    public void onBackPressed() {
        // 正在滑动返回的时候取消返回按钮事件
        if (mSwipeBackHelper.isSliding()) {
            return;
        }
        mSwipeBackHelper.backward();
    }

    /**
     * 初始化布局
     */
    protected void initLayout() {
        if (getLayoutId() > 0) {
            setContentView(getLayoutId());
            initSoftKeyboard();
            mBinding =  DataBindingUtil.setContentView(this,getLayoutId());
            if (enableStateView()){
                mStateView = StateView.inject(injectTarget());
                if (mStateView != null){
                    mStateView.setLoadingResource(R.layout.page_loading);
                    mStateView.setEmptyResource(R.layout.page_nodata);
                    mStateView.setRetryResource(R.layout.internet_retry);
                    mStateView.setOnRetryClickListener(new StateView.OnRetryClickListener() {
                        @Override
                        public void onRetryClick() {
                            Retry();
                        }
                    });
                }
            }
        }
        if (getTitleBar() != null) {
            getTitleBar().setOnTitleBarListener(this);
        }
        initImmersion();
    }

    protected abstract void Retry();

    /**
     * 设置 app 字体不随系统字体设置改变
     */
    @Override
    public Resources getResources() {
        Resources res = super.getResources();
        if (res != null) {
            Configuration config = res.getConfiguration();
            if (config != null && config.fontScale != 1.0f) {
                config.fontScale = 1.0f;
                res.updateConfiguration(config, res.getDisplayMetrics());
            }
        }
        return res;
    }

    /**
     * 获取布局 ID
     */
    protected abstract int getLayoutId();

    protected abstract void initView();

    protected abstract void initData();

    public void initListener() {
    }
    /**
     * 初始化软键盘
     */
    protected void initSoftKeyboard() {
        // 点击外部隐藏软键盘，提升用户体验
        getContentView().setOnClickListener(v -> hideSoftKeyboard());
    }

    /**
     * 初始化沉浸式
     */
    protected void initImmersion() {
        // 初始化沉浸式状态栏
        if (isStatusBarEnabled()) {
            createStatusBarConfig().init();
        }
        // 设置标题栏沉浸
        if (mTitleBar != null) {
            ImmersionBar.with(this).statusBarDarkFont(true,0.2f).titleBar(mTitleBar).init();

        }
    }

    public void showProgressDialog(){

        if (isAlive() == false) {
            return;
        }

        WaitDialog.show("加载中!").setOnBackPressedListener(new OnBackPressedListener() {
            @Override
            public boolean onBackPressed() {
//                PopTip.show("按下返回");
                dismissProgressDialog();
                return false;
            }
        });
    }

    public void showSuccessDialog(String msg){
        runUiThread(new Runnable() {
            @Override
            public void run() {
                TipDialog.show(msg, WaitDialog.TYPE.SUCCESS,800)
                .setDialogLifecycleCallback(new DialogLifecycleCallback<WaitDialog>() {
                    @Override
                    public void onDismiss(WaitDialog dialog) {
                        super.onDismiss(dialog);
                        doOnDismiss();
                    }
                });
            }
        });
    }

    public void doOnDismiss(){};

    public void showWarningDialog(String msg){
        runUiThread(new Runnable() {
            @Override
            public void run() {
                TipDialog.show(msg, WaitDialog.TYPE.WARNING,800);
            }
        });
    }


    public void showErrorDialog(String msg){
//        openActivity(LoginActivity.class);
        runUiThread(new Runnable() {
            @Override
            public void run() {
                TipDialog.show(msg, WaitDialog.TYPE.ERROR,800);
            }
        });
    }

    public void dismissProgressDialog(){
        WaitDialog.dismiss();
    }

    /**
     * 是否使用沉浸式状态栏
     */
    protected boolean isStatusBarEnabled() {
        return true;
    }

    /**
     * 初始化沉浸式状态栏
     */
    protected ImmersionBar createStatusBarConfig() {
        // 在BaseActivity里初始化
        mImmersionBar = ImmersionBar.with(this)
                // 默认状态栏字体颜色为黑色
                .statusBarDarkFont(false);
        return mImmersionBar;
    }

    /**
     * 在UI线程中运行，建议用这个方法代替runOnUiThread
     *
     * @param action
     */
    public final void runUiThread(Runnable action) {
        if (isAlive() == false) {
            return;
        }
        runOnUiThread(action);
    }


    /**
     * 和 setContentView 对应的方法
     */
    public ViewGroup getContentView() {
        return findViewById(Window.ID_ANDROID_CONTENT);
    }

    protected void hideSoftKeyboard() {
        if (getWindow().getAttributes().softInputMode != WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN) {
            if (getCurrentFocus() != null)
                inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                        InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    @Nullable
    @Override
    public TitleBar getTitleBar() {
        if (mTitleBar == null) {
            mTitleBar = findTitleBar(getContentView());
        }
        return mTitleBar;
    }

    /**
     * 获取状态栏沉浸的配置对象
     */
    @Nullable
    public ImmersionBar getStatusBarConfig() {
        return mImmersionBar;
    }

    @Override
    protected void onResume() {
        super.onResume();
        mCurrentActivity = this;

    }

    @Override
    protected void onPause() {
        super.onPause();
        mCurrentActivity = null;
    }

    @Override
    public void onLeftClick(View v) {
        onBackPressed();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        isAlive = false;
        showKeyboard(false);
        //销毁的时候从集合中移除
        synchronized (mActivities) {
            mActivities.remove(this);
            unregisterEventBus(this);
        }

        if (mPresenter != null) {
            mPresenter.detachView();
        }
    }

    protected void showKeyboard(boolean isShow) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (isShow) {
            if (getCurrentFocus() == null) {
                imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
            } else {
                imm.showSoftInput(getCurrentFocus(), 0);
            }
        } else {
            if (getCurrentFocus() != null) {
                imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);

            }
        }
    }


    /**
     * 退出应用的方法
     */
    public static void exitApp() {

        ListIterator<Activity> iterator = mActivities.listIterator();

        while (iterator.hasNext()) {
            Activity next = iterator.next();
            next.finish();
        }
    }

    public boolean isEventBusRegisted(Object subscribe) {
        return EventBus.getDefault().isRegistered(subscribe);
    }

    public void registerEventBus(Object subscribe) {
        if (!isEventBusRegisted(subscribe)) {
            EventBus.getDefault().register(subscribe);
        }
    }

    public void unregisterEventBus(Object subscribe) {
        if (isEventBusRegisted(subscribe)) {
            EventBus.getDefault().unregister(subscribe);
        }
    }



    public final boolean isAlive() {
        return isAlive && activity != null;// & ! isFinishing();导致finish，onDestroy内runUiThread不可用
    }

    @CallSuper
    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void onEventSticky(ActivityEvent event) {
    }

    @CallSuper
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(ActivityEvent event) {

    }


    protected String getEditText(EditText editText){
        return editText.getText().toString().trim();
    }

    protected boolean checkIfEmpty(EditText editText){
        return editText.getText().toString().trim().isEmpty();
    }

    public void showException(final Throwable ex) {
//        isRefresh = false;
        runUiThread(new Runnable() {
            @Override
            public void run() {
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
        });
    }


    public void openActivity(Class<?> targetActivityClass, Bundle bundle) {
        Intent intent = new Intent(this, targetActivityClass);
        if (bundle != null) {
            intent.putExtra("bundle",bundle);
        }
        startActivity(intent);
    }

    public void openActivity(Class<?> targetActivityClass) {
        Intent intent = new Intent(this, targetActivityClass);
        startActivity(intent);
    }

    public void openActivityAndCloseThis(Class<?> targetActivityClass, Bundle bundle) {
        openActivity(targetActivityClass, bundle);
        this.finish();
    }
    public void runDelayed(Runnable runnable, long time) {
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                runnable.run();
            }
        }, time);
    }

    public void showErrorDialog(BaseResponse response){
        if (isAlive == false) {
            return;
        }
        if (response.getCode() == 1400 || response.getCode()== 1401){
            toast("token失效请重新登录");
            openActivity(LoginActivity.class);
            finish();
            return;
        }
        if (response.getCode() == 1402){
            toast("账号异常，请重新登录");
            openActivity(LoginActivity.class);
            finish();
            return;
        }
        if (response.getCode() == 1403){
            toast("该账号在其他设备登录");
            openActivity(LoginActivity.class);
            finish();
            return;
        }
        showErrorDialog(response.getMsg());
    }
}
