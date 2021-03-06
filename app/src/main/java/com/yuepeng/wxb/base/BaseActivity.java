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
    private static Activity mCurrentActivity;// ?????????activity????????????
    public static List<Activity> mActivities = new LinkedList<Activity>();
    protected Bundle savedInstanceState;
    protected InputMethodManager inputMethodManager;
    protected WeakReference<BaseActivity> activity;
    protected DB mBinding;
    /** ??????????????? */
    private TitleBar mTitleBar;
    /** ??????????????? */
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
     * ??????????????????????????? super.onCreate(savedInstanceState) ?????????????????????
     */
    private void initSwipeBackFinish() {
        mSwipeBackHelper = new BGASwipeBackHelper(this, this);
        mSwipeBackHelper.setIsOnlyTrackingLeftEdge(false);
    }

    protected  boolean checkEdit(EditText et){
        return (!et.getText().toString().trim().isEmpty());
    }

    /**
     * ????????????????????????????????????????????????????????? true ?????????????????????????????????????????????????????????????????????????????????????????? false ??????
     *
     * @return
     */
    @Override
    public boolean isSupportSwipeBack() {
        return true;
    }

    /**
     * ??????????????????
     *
     * @param slideOffset ??? 0 ??? 1
     */
    @Override
    public void onSwipeBackLayoutSlide(float slideOffset) {
    }

    /**
     * ??????????????????????????????????????????????????????????????????????????????
     */
    @Override
    public void onSwipeBackLayoutCancel() {
    }

    /**
     * ??????????????????????????????????????? Activity
     */
    @Override
    public void onSwipeBackLayoutExecuted() {
        mSwipeBackHelper.swipeBackward();
    }

    @Override
    public void onBackPressed() {
        // ???????????????????????????????????????????????????
        if (mSwipeBackHelper.isSliding()) {
            return;
        }
        mSwipeBackHelper.backward();
    }

    /**
     * ???????????????
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
     * ?????? app ????????????????????????????????????
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
     * ???????????? ID
     */
    protected abstract int getLayoutId();

    protected abstract void initView();

    protected abstract void initData();

    public void initListener() {
    }
    /**
     * ??????????????????
     */
    protected void initSoftKeyboard() {
        // ????????????????????????????????????????????????
        getContentView().setOnClickListener(v -> hideSoftKeyboard());
    }

    /**
     * ??????????????????
     */
    protected void initImmersion() {
        // ???????????????????????????
        if (isStatusBarEnabled()) {
            createStatusBarConfig().init();
        }
        // ?????????????????????
        if (mTitleBar != null) {
            ImmersionBar.with(this).statusBarDarkFont(true,0.2f).titleBar(mTitleBar).init();

        }
    }

    public void showProgressDialog(){

        if (isAlive() == false) {
            return;
        }

        WaitDialog.show("?????????!").setOnBackPressedListener(new OnBackPressedListener() {
            @Override
            public boolean onBackPressed() {
//                PopTip.show("????????????");
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
     * ??????????????????????????????
     */
    protected boolean isStatusBarEnabled() {
        return true;
    }

    /**
     * ???????????????????????????
     */
    protected ImmersionBar createStatusBarConfig() {
        // ???BaseActivity????????????
        mImmersionBar = ImmersionBar.with(this)
                // ????????????????????????????????????
                .statusBarDarkFont(false);
        return mImmersionBar;
    }

    /**
     * ???UI?????????????????????????????????????????????runOnUiThread
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
     * ??? setContentView ???????????????
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
     * ????????????????????????????????????
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
        //?????????????????????????????????
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
     * ?????????????????????
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
        return isAlive && activity != null;// & ! isFinishing();??????finish???onDestroy???runUiThread?????????
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
                    showErrorDialog("??????????????????????????????????????????????????????");
                } else if (ex instanceof ConnectException) {
                    showErrorDialog("??????????????????????????????????????????????????????");
                    return;
                } else if (ex instanceof TimeoutException) {
                    showErrorDialog("??????????????????????????????????????????????????????");
                    return;
                } else if (ex instanceof UnknownHostException){
                    showErrorDialog("??????????????????????????????????????????");
                }else if (ex instanceof SocketException) {
                    showErrorDialog("????????????????????????????????????");
                    return;
                } else if (ex instanceof SocketTimeoutException) {
                    showErrorDialog("??????????????????????????????????????????");
                    return;
                } else if (ex instanceof JsonSyntaxException) {
                    showErrorDialog("????????????????????????????????????????????????");
                } else if (ex instanceof NullPointerException) {
                    showErrorDialog("??????????????????");
                } else if (ex instanceof IllegalArgumentException) {
                    showErrorDialog("???????????????????????????");
                } else if (ex instanceof NumberFormatException) {
                    showErrorDialog("?????????????????????????????????");
                } else if (ex instanceof NetworkErrorException) {
                    showErrorDialog("????????????????????????");
                } else if (ex instanceof NetworkOnMainThreadException) {
                    showErrorDialog("???????????????????????????");
                }else if (ex instanceof FileNotFoundException) {
                    showErrorDialog("???????????????????????????");
                } else if (ex instanceof ArrayIndexOutOfBoundsException) {
                    showErrorDialog("?????????????????????");
                }else if (ex instanceof IOException) {
                    showErrorDialog("???????????????????????????");
                }  else {
                    showErrorDialog("?????????????????????");
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
            toast("token?????????????????????");
            openActivity(LoginActivity.class);
            finish();
            return;
        }
        if (response.getCode() == 1402){
            toast("??????????????????????????????");
            openActivity(LoginActivity.class);
            finish();
            return;
        }
        if (response.getCode() == 1403){
            toast("??????????????????????????????");
            openActivity(LoginActivity.class);
            finish();
            return;
        }
        showErrorDialog(response.getMsg());
    }
}
