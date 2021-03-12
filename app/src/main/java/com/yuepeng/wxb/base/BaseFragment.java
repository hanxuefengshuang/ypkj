package com.yuepeng.wxb.base;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.FrameLayout;

import com.alibaba.android.arouter.launcher.ARouter;
import com.blankj.utilcode.util.BarUtils;
import com.github.nukc.stateview.StateView;
import com.gyf.immersionbar.ImmersionBar;
import com.hjq.bar.OnTitleBarListener;
import com.hjq.bar.TitleBar;
import com.kingja.loadsir.callback.Callback;
import com.kingja.loadsir.callback.SuccessCallback;
import com.kingja.loadsir.core.LoadService;
import com.kingja.loadsir.core.LoadSir;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.wstro.thirdlibrary.base.BasePresenter;
import com.wstro.thirdlibrary.base.BaseResponse;
import com.wstro.thirdlibrary.base.LazyLoadFragment;
import com.wstro.thirdlibrary.event.FragmentEvent;
import com.yuepeng.wxb.R;
import com.yuepeng.wxb.action.ToastAction;
import com.yuepeng.wxb.ui.activity.LoginActivity;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.lang.ref.WeakReference;

import androidx.annotation.DrawableRes;
import androidx.annotation.LayoutRes;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

public abstract class BaseFragment<DB extends ViewDataBinding,T extends BasePresenter> extends LazyLoadFragment implements Consumer<Disposable>, ToastAction {

    protected T mPresenter;
    //根部局
    protected View rootView;
    protected StateView mStateView;//用于显示加载中、网络异常，空布局、内容布局
    //Disposable容器
    private CompositeDisposable mDisposables = new CompositeDisposable();
    protected ARouter mRouter = ARouter.getInstance();
    protected DB mBinding;
    private Handler mLoadingHandler = new Handler();
    protected WeakReference<BaseActivity> activity;
    protected SmartRefreshLayout refreshLayout;

    protected WeakReference<BaseActivity> context = null;
    private boolean isAlive = false;
    protected Application mApplication;
    protected abstract @LayoutRes
    int onBindLayout();
    /** 状态栏沉浸 */
    protected ImmersionBar mImmersionBar;
    /** 标题栏对象 */
    protected TitleBar mSimpleTitleBar;
    //状态页管理
    protected LoadService mBaseLoadService;
    protected Activity mActivity;
    protected boolean enableState;
    protected boolean isFirstVisible = true;
    @Override
    public void accept(Disposable disposable) throws Exception {
        mDisposables.add(disposable);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        isAlive = true;
        context = new WeakReference<BaseActivity>((BaseActivity) getActivity());
        mRouter.inject(this);
        EventBus.getDefault().register(this);
        mPresenter = createPresenter();
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mActivity = (Activity) context;
    }


    //用于创建Presenter和判断是否使用MVP模式(由子类实现)
    protected abstract T createPresenter();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.common_layout_root,container,false);
        initImmersion();
        initCommonView();
        if (enableStateView()){
            enableState = true;
        }
        //不采用懒加载
        if (!enableLazy()) {
            loadView();
            initView();
            initData();
            initListener();
        }
        return rootView;
    }

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


    @Override
    public void onFragmentFirstVisible() {
        //采用懒加载
        if (enableLazy()) {
            loadView();
            initView();
            initListener();
            initData();
        }
    }

    //    /**
//     * 初始化基本布局
//     */
    private void initCommonView() {
        if (enableSimplebar()) {
            ViewStub viewStubBar = rootView.findViewById(R.id.vs_bar);
            viewStubBar.setLayoutResource(R.layout.common_layout_simplebar);
            mSimpleTitleBar = viewStubBar.inflate().findViewById(R.id.ctb_simple);
            initSimpleBar(mSimpleTitleBar);
        }
    }

    public void initListener() {
    }

    public void showProgressDialog(){

        if (isAlive == false) {
            return;
        }
        context.get().showProgressDialog();

    }

    public void dismissDialog(){
        if (isAlive == false) {
            return;
        }
        context.get().dismissProgressDialog();
    }

    /**
     * 是否开启懒加载,默认true
     *
     * @return
     */
    protected boolean enableLazy() {
        return true;
    }

    protected boolean enableStateView(){
        return false;
    }

    protected abstract void initView();

    /**
     * 初始化沉浸式
     */
    protected void initImmersion() {
        // 初始化沉浸式状态栏
        if (isStatusBarEnabled()) {
            createStatusBarConfig().init();
        }
        // 设置标题栏沉浸
        if (mSimpleTitleBar != null) {
            ImmersionBar.with(this).statusBarColor(R.color.appThemeColor);
//            ImmersionBar.setTitleBar(this, mSimpleTitleBar);
        }
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
                .statusBarDarkFont(true);
        return mImmersionBar;
    }



    /**
     * 填充布局(布局懒加载)
     */
    protected View view;
    protected void loadView() {
        view = rootView;
        ViewStub mViewStubContent = rootView.findViewById(R.id.vs_content);
        mViewStubContent.setLayoutResource(onBindLayout());

        View contentView = mViewStubContent.inflate();
        mBinding = DataBindingUtil.bind(contentView);
        LoadSir.Builder builder = new LoadSir.Builder()
//                .addCallback(getInitStatus())
//                .addCallback(getEmptyStatus())
//                .addCallback(getErrorStatus())
//                .addCallback(getLoadingStatus())
                .setDefaultCallback(SuccessCallback.class);
        if (enableState){
            mStateView = StateView.inject(injectTarget());
        }
        if (mStateView!=null){
            mStateView.setLoadingResource(R.layout.page_loading);
            mStateView.setRetryResource(R.layout.internet_retry);
            mStateView.setEmptyResource(R.layout.page_nodata);

//           ImageView imageView =  mStateView.getLoadingView().findViewById(R.id.loadView);
            mStateView.setOnRetryClickListener(new StateView.OnRetryClickListener() {
                @Override
                public void onRetryClick() {
                    Retry();
                }
            });
        }
        FrameLayout.LayoutParams layoutParams=null;
        if(enableSimplebar()){
            layoutParams = new FrameLayout.LayoutParams((FrameLayout.LayoutParams) contentView.getLayoutParams());
//            boolean b = StatusBarUtils.supportTransparentStatusBar();
//            int barHeight=b? BarUtils.getStatusBarHeight() :0;
            initImmersion();
//            int barHeight=b? BarUtils.getStatusBarHeight() :0;
            layoutParams.topMargin=getResources().getDimensionPixelOffset(R.dimen.BaseBarHeight)+ BarUtils.getStatusBarHeight();
            ImmersionBar.setTitleBar(this, mSimpleTitleBar);
        }
        mBaseLoadService = builder.build().register(contentView,layoutParams, (Callback.OnReloadListener) this::onReload);

    }

    protected abstract void Retry();

    protected abstract View injectTarget();

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(FragmentEvent event) {

    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void onEventSticky(FragmentEvent event) {
    }

    //    /**
//     * 点击状态页默认执行事件
//     */
    protected void onReload(View v) {
//        showInitView();
        initData();
    }

    public abstract void initData();

    /**
     * 是否开启通用标题栏,默认true
     *
     * @return
     */
    protected boolean enableSimplebar() {
        return true;
    }

    /**
     * 初始化通用标题栏
     */
    protected void initSimpleBar(TitleBar mSimpleTitleBar) {
        // 中间
        if (onBindBarCenterStyle() == SimpleBarStyle.CENTER_TITLE) {
            String strings = onBindBarTitleText();
            if (strings != null && strings.length() > 0) {
                mSimpleTitleBar.setTitle(strings);
            }
        }
        //左边
        if (onBindBarLeftStyle() == SimpleBarStyle.LEFT_BACK) {
            mSimpleTitleBar.setLeftIcon(R.mipmap.icon_back);
//            mSimpleTitleBar.setLeftIcon(R.mipmap.icon_back);
//            mSimpleTitleBar.setOnClickListener(v -> onSimpleBackClick());
        } else if (onBindBarLeftStyle() == SimpleBarStyle.LEFT_BACK_TEXT) {

        } else if (onBindBarLeftStyle() == SimpleBarStyle.LEFT_ICON && onBindBarLeftIcon() != null) {

        }
        //右边
        switch (onBindBarRightStyle()) {
            case RIGHT_TEXT:
                String strings = onBindBarRightText();
                if (strings == null || strings.length() == 0) {
                    break;
                }
                if (null != strings && strings.trim().length() > 0) {
                    mSimpleTitleBar.setRightTitle(strings);
                }
                int color = onBindBarRightTextColor();
                if (color == 0){
                    break;
                }
                mSimpleTitleBar.setRightColor(color);
                break;
            case RIGHT_ICON:
                Integer ints = onBindBarRightIcon();
                if (ints != null){
                    mSimpleTitleBar.setRightIcon(ints);

                }
                break;
            case RIGHT_CUSTOME:

                break;
        }
        mSimpleTitleBar.setOnTitleBarListener(new OnTitleBarListener() {
            @Override
            public void onLeftClick(View v) {
                if (onBindBarLeftStyle()== SimpleBarStyle.LEFT_BACK){
//                    hideSoftInput();
                    getActivity().finish();
                }
            }

            @Override
            public void onTitleClick(View v) {

            }

            @Override
            public void onRightClick(View v) {
                if (onBindBarRightStyle() == SimpleBarStyle.RIGHT_ICON || onBindBarRightStyle() == SimpleBarStyle.RIGHT_TEXT)
                    doRightClick();
            }
        });

    }

    protected void doRightClick(){};

    /**
     * 初始化中间标题栏类型
     *
     * @return
     */
    protected SimpleBarStyle onBindBarCenterStyle() {
        return SimpleBarStyle.CENTER_TITLE;
    }

    /**
     * 初始化标题文本
     *
     * @return
     */
    protected String onBindBarTitleText() {
        return null;
    }

    /**
     * 初始化左边标题栏类型
     *
     * @return
     */
    protected SimpleBarStyle onBindBarLeftStyle() {
        return null;
    }

    /**
     * 初始化标题栏左边附加图标
     *
     * @return
     */
    protected @DrawableRes
    Integer onBindBarLeftIcon() {
        return null;
    }

    /**
     * 初始化右边标题栏类型
     *
     * @return
     */
    protected SimpleBarStyle onBindBarRightStyle() {
        return SimpleBarStyle.RIGHT_ICON;
    }

    /**
     * 初始化标题栏右边文本
     *
     * @return
     */
    protected String onBindBarRightText() {
        return null;
    }
    protected int onBindBarRightTextColor() {
        return 0;
    }

    /**
     * 初始化标题栏右边图标
     *
     * @return
     */
    protected @DrawableRes
    Integer onBindBarRightIcon() {
        return null;
    }

    protected enum SimpleBarStyle {
        /**
         * 返回图标(默认)
         */
        LEFT_BACK,
        /**
         * 返回图标+文字
         */
        LEFT_BACK_TEXT,
        /**
         * 附加图标
         */
        LEFT_ICON,
        /**
         * 标题(默认)
         */
        CENTER_TITLE,
        /**
         * 自定义布局
         */
        CENTER_CUSTOME,
        /**
         * 文字
         */
        RIGHT_TEXT,
        /**
         * 自定义文字
         */
        RIGHT_TEXT_CUSTOM,
        /**
         * 图标(默认)
         */
        RIGHT_ICON,
        /**
         * 自定义布局
         */
        RIGHT_CUSTOME,
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        isAlive = false;
        view = null;
    }

    /**
     * 异常处理
     * @param e
     */
    public void showException(Throwable e){
//        isRefresh = false;
        if (isAlive == false) {
            return;
        }
        context.get().showException(e);

    }

    public void showWarningDialog(String msg){
        if (isAlive == false) {
            return;
        }
        context.get().showWarningDialog(msg);
    }

    public void showErrorDialog(String msg){
        if (isAlive == false) {
            return;
        }
        context.get().showErrorDialog(msg);
    }

    public void showErrorDialog(BaseResponse response){
        if (isAlive == false) {
            return;
        }
        if (response.getCode() == 1400 || response.getCode()== 1401){
            toast("token失效请重新登录");
            openActivity(LoginActivity.class);
            getActivity().finish();
            return;
        }
        if (response.getCode() == 1402){
            toast("账号异常，请重新登录");
            openActivity(LoginActivity.class);

            getActivity().finish();
            return;
        }
        if (response.getCode() == 1403){
            toast("该账号在其他设备登录");
            openActivity(LoginActivity.class);
            getActivity().finish();
            return;
        }
        context.get().showErrorDialog(response.getMsg());
    }

    public void showSuccessDialog(String msg){
        if (isAlive == false) {
            return;
        }
        context.get().showSuccessDialog(msg);
    }

    public void openActivity(Class<?> targetActivityClass) {
        Intent intent = new Intent(getActivity(), targetActivityClass);
        startActivity(intent);
    }

    public void openActivityForResult(Class<?> targetActivityClass,int code) {
        Intent intent = new Intent(getActivity(), targetActivityClass);
        startActivityForResult(intent,code);
    }

    @Override
    public void onPause() {
        super.onPause();
        isFirstVisible = false;
    }
}
