package com.yuepeng.wxb.base;

import android.content.Context;
import android.text.TextUtils;

import com.baidu.trace.LBSTraceClient;
import com.baidu.trace.Trace;
import com.baidu.trace.api.entity.LocRequest;
import com.baidu.trace.model.BaseRequest;
import com.baidu.trace.model.OnCustomAttributeListener;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.DefaultRefreshInitializer;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.socks.library.KLog;
import com.wstro.thirdlibrary.base.BaseApp;
import com.wstro.thirdlibrary.base.Constant;
import com.wstro.thirdlibrary.entity.UserModel;
import com.wstro.thirdlibrary.utils.SerializedObjectsUtil;
import com.wstro.thirdlibrary.utils.manager.NetManager;
import com.yuepeng.wxb.helper.BDHelper;
import com.yuepeng.wxb.location.ReflectHelper;
import com.yuepeng.wxb.utils.PreUtils;

import org.xutils.x;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import androidx.annotation.NonNull;

public class App extends BaseApp {
    private AtomicInteger mSequenceGenerator = new AtomicInteger();
    private LocRequest locRequest = null;

    /**
     * 轨迹客户端
     */
    public LBSTraceClient mClient = null;
    /**
     * 轨迹服务
     */
    public Trace mTrace = null;
    /**
     * 轨迹服务ID
     */
    public final long serviceId = 226255;       //release
//    public final long serviceId = 225756;       //ceshi

//    selfID：226172

    /**
     * Entity标识
     */
    public String entityName = "myTrace";



    private Context appContext;
    private static App instance;
    public UserModel userModel;
    public static App getInstance() {
        return instance;
    }
    @Override
    public void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        ReflectHelper.unseal(base);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        com.wstro.thirdlibrary.helper.ThirdHelper.getInstance(this).init();
        BDHelper.getInstance(this).initBaiduLBS();
        appContext = this.getApplicationContext();
        initSmart();
        //initBaiduTrace();
        NetManager.init(this);
        x.Ext.init(this);
        x.Ext.setDebug(org.xutils.BuildConfig.DEBUG);
    }

    public void initBaiduTrace() {
        entityName = getUserModel() != null ? userModel.getCustCode() : entityName;
        mClient = new LBSTraceClient(this);
        mTrace = new Trace(serviceId, entityName);
        locRequest = new LocRequest(serviceId);
        mClient.setOnCustomAttributeListener(new OnCustomAttributeListener() {
            @Override
            public Map<String, String> onTrackAttributeCallback() {
                System.out.println("onTrackAttributeCallback, locTime : " );
                Map<String, String> map = new HashMap<>();
                map.put("key1", "value1");
                map.put("key2", "value2");
                return map;
            }

            @Override
            public Map<String, String> onTrackAttributeCallback(long locTime) {
                System.out.println("onTrackAttributeCallback, locTime : " + locTime);
                Map<String, String> map = new HashMap<>();
                map.put("key1", "value1");
                map.put("key2", "value2");
                return map;
            }
        });

    }
    /**
     * 初始化请求公共参数
     *
     * @param request
     */
    public void initRequest(BaseRequest request) {
        request.setTag(getTag());
        request.setServiceId(serviceId);
    }
    /**
     * 获取请求标识
     *
     * @return
     */
    public int getTag() {
        return mSequenceGenerator.incrementAndGet();
    }

    private void initSmart() {
        SmartRefreshLayout.setDefaultRefreshInitializer(new DefaultRefreshInitializer() {
            @Override
            public void initialize(@NonNull Context context, @NonNull RefreshLayout layout) {
                //全局设置（优先级最低）
                layout.setEnableAutoLoadMore(true);
                layout.setEnableOverScrollDrag(false);
                layout.setEnableOverScrollBounce(true);
                layout.setEnableLoadMoreWhenContentNotFull(true);
                layout.setEnableScrollContentWhenRefreshed(true);
                layout.setFooterMaxDragRate(4.0F);
                layout.setFooterHeight(45);
                layout.setEnableFooterFollowWhenLoadFinished(true);
            }
        });
    }

    /**
     * 保存登录信息至本地
     */
    public void saveUserModel(UserModel userModel) {

        String userModelStr = null;
        try {
            this.userModel = userModel;
            userModelStr = SerializedObjectsUtil.serialize(userModel);
            PreUtils.putString(Constant.USER_INFO_KEY,userModelStr);
        } catch (IOException e) {
            KLog.i("App", "saveLoginModel 序列化失败 e = " + e.getMessage());
        }
    }


    /**
     * 得到登录信息
     *
     * @return LoginModle实例
     */
    public UserModel getUserModel() {
        if (userModel != null) {
            return userModel;
        } else {
            String str =  PreUtils.getString(Constant.USER_INFO_KEY, "");
            if (!str.equals("")) {
                try {
                    userModel = (UserModel) SerializedObjectsUtil.deSerialization(str);
                    return userModel;
                } catch (ClassNotFoundException e) {
                    KLog.i("App", "UserModel e = " + e.getMessage());
                } catch (IOException e) {
                    KLog.i("App", "IOException e = " + e.getMessage());
                }
            }
        }
        return null;
    }



    /**
     * 获取进程号对应的进程名
     *
     * @param pid 进程号
     * @return 进程名
     */
    public String getProcessName(int pid) {
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader("/proc/" + pid + "/cmdline"));
            String processName = reader.readLine();
            if (!TextUtils.isEmpty(processName)) {
                processName = processName.trim();
            }
            return processName;
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        } finally {
            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (IOException exception) {
                exception.printStackTrace();
            }
        }
        return null;
    }
}
