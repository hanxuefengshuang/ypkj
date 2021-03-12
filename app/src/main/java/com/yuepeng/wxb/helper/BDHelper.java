package com.yuepeng.wxb.helper;

import android.app.Application;
import android.content.Intent;
import android.os.Build;
import android.os.Environment;
import android.util.Log;

import com.baidu.mapapi.CoordType;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.navisdk.adapter.BaiduNaviManagerFactory;
import com.baidu.navisdk.adapter.IBaiduNaviManager;
import com.baidu.navisdk.adapter.struct.BNTTsInitConfig;
import com.squareup.leakcanary.RefWatcher;

import java.io.File;

//import com.rain.crow.PhotoPick;
//import com.rain.crow.PhotoPickOptions;

public class BDHelper {
    private static final String TAG = "ThirdHelper";
    private static Application mApplication;
    private static volatile BDHelper instance;
    public static RefWatcher refWatcher;
    public static final String APP_FOLDER_NAME = TAG;
    private BDHelper() {
    }

    public static BDHelper getInstance(Application application) {
        if (instance == null) {
            synchronized (BDHelper.class) {
                if (instance == null) {
                    mApplication = application;
                    instance = new BDHelper();
                }
            }
        }
        return instance;
    }



    public BDHelper initBaiduLBS(){
        SDKInitializer.initialize(mApplication);
        SDKInitializer.setCoordType(CoordType.BD09LL);
        initNavi();
        return this;
    }



    private void initNavi() {
        if (BaiduNaviManagerFactory.getBaiduNaviManager().isInited()) {
            return;
        }
        BaiduNaviManagerFactory.getBaiduNaviManager().init(mApplication.getApplicationContext(),
                mApplication.getExternalFilesDir(null).getPath(),
                APP_FOLDER_NAME, new IBaiduNaviManager.INaviInitListener() {

                    @Override
                    public void onAuthResult(int status, String msg) {
                        String result;
                        if (0 == status) {
                            result = "key校验成功!";
                        } else {
                            result = "key校验失败, " + msg;
                        }
                        Log.e(TAG, result);
                    }

                    @Override
                    public void initStart() {
                        Log.e(TAG, "initStart");
                    }

                    @Override
                    public void initSuccess() {
                        Log.e(TAG, "initSuccess");
                        BaiduNaviManagerFactory.getBaiduNaviManager().enableOutLog(true);
                        String cuid = BaiduNaviManagerFactory.getBaiduNaviManager().getCUID();
                        Log.e(TAG, "cuid = " + cuid);
                        // 初始化tts
                        initTTS();
                        mApplication.sendBroadcast(new Intent("com.navi.ready"));
                    }

                    @Override
                    public void initFailed(int errCode) {
                        Log.e(TAG, "initFailed-" + errCode);
                    }
                });
    }

    private void initTTS() {
        // 使用内置TTS
        BNTTsInitConfig config = new BNTTsInitConfig.Builder()
                .context(mApplication.getApplicationContext())
                .sdcardRootPath(getSdcardDir())
                .appFolderName(APP_FOLDER_NAME)
                .appId(getTTSAppID())
                .appKey(getTTSAppKey())
                .secretKey(getTTSsecretKey())
                .build();
        BaiduNaviManagerFactory.getTTSManager().initTTS(config);
    }

    public static String getTTSAppID() {
        return "23535719";
    }

    public static String getTTSAppKey() {
        return "nBG8i0NPIz2PhaT5rslgmQ4PqScHPnoi";
    }
    public static String getTTSsecretKey() {
        return "yIzmur0guVSmXuCmz9g5mbufCCrUQfy5";
    }

//    public static String getTTSAppID() {
//        return "23654000";
//    }
//
//    public static String getTTSAppKey() {
//        return "Ahz8Gn9zlzZ08vBo3WI2sKkTo4k0wFia";
//    }
//    public static String getTTSsecretKey() {
//        return "XslO1gT2HrFfBGmi7jcLgbfNqVxmlixU";
//    }

    private String getSdcardDir() {
        if (Build.VERSION.SDK_INT >= 29) {
            // 如果外部储存可用 ,获得外部存储路径
            File file = mApplication.getExternalFilesDir(null);
            if (file != null && file.exists()) {
                return file.getPath();
            } else {
                return mApplication.getFilesDir().getPath();
            }
        } else {
            return Environment.getExternalStorageDirectory().getAbsolutePath();
        }
    }




//    public static PhotoPickOptions getPhotoPickOptions(Context context) {
//        PhotoPickOptions options = new PhotoPickOptions();
//        //自定义文件存储路径
//        options.filePath = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "pic";
//        //自定义文件夹名称
//        options.imagePath = options.filePath +  "wxb/";
//        //自定义FileProvider地址
//        options.photoPickAuthority = context.getString(R.string.file_provider_authorities);
//        //自定义主题颜色 同步APP
//        options.photoPickThemeColor = R.color.appThemeColor;
//        return options;
//    }
}
