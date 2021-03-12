package com.yuepeng.wxb.location;

import android.annotation.SuppressLint;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.blankj.utilcode.util.LogUtils;

/**
 * Created by wangjun on 2021/2/20.
 */
public class MyBroadcastReceiver extends BroadcastReceiver {


    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO Auto-generated method stub
        LogUtils.d("mBroadcastReceiver:", "已进入广播");
        /**
         * 自定义广播，服务关闭时
         */
        if (intent.getAction().equals("com.yuepeng.wxb.service.destroy")) {

           // DebugUtil.debug("mBroadcastReceiver:", "自定义广播，服务关闭重启程序11");
            // intentService = new Intent(context, mService.class);
            // context.startService(intentService);
            appIntent(context);
        }
        /**
         * 手机开机
         */
        if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {
            //DebugUtil.debug("mBroadcastReceiver:", "开机启动程序");
            appIntent(context);
            // if (!mUtils.isServiceRunning(context, mService.class)) {
            // intentService = new Intent(context, mService.class);
            // context.startService(intentService);
            // }
        }

        /**
         * 按电源键
         */
        if (intent.getAction().equals(Intent.ACTION_USER_PRESENT)) {
           // DebugUtil.debug("mBroadcastReceiver:", "唤醒屏幕启动程序");
            appIntent(context);
            // if (!mUtils.isServiceRunning(context, mService.class)) {
            // intentService = new Intent(context, mService.class);
            // context.startService(intentService);
            // }
        }
        /**
         * 开锁
         */
        if (intent.getAction().equals(Intent.ACTION_SCREEN_ON)) {
           // DebugUtil.debug("mBroadcastReceiver:", "正在解锁");
            appIntent(context);
        }
        /**
         * 锁屏
         */
        if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {
           // DebugUtil.debug("mBroadcastReceiver:", "正在锁屏");
        }
        /**
         * SD挂载
         */
        if (intent.getAction().equals(Intent.ACTION_MEDIA_MOUNTED)) {
           // DebugUtil.debug("mBroadcastReceiver:", "SD挂载");
            appIntent(context);
        }
        /**
         * SD
         */
        if (intent.getAction().equals(Intent.ACTION_MEDIA_UNMOUNTED)) {
           // DebugUtil.debug("mBroadcastReceiver:", "SD不挂载");
        }
    }

    @SuppressLint("InlinedApi")
    public void appIntent(Context context) {
        try {
            Intent intent = context.getPackageManager()
                    .getLaunchIntentForPackage("com.example.mlocation");
            if (intent != null) {
                intent.addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES
                        | Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }else {
                // 开启服务--上传位置信息到服务器
                Intent intentService = new Intent(context, ForegroundService.class);
                context.startService(intentService);
            }

            LogUtils.d("mBroadcastReceiver:", "广播打开程序");
        } catch (ActivityNotFoundException e) {
            LogUtils.d("mBroadcastReceiver:", "广播打开程序失败");
        }
    }

} 