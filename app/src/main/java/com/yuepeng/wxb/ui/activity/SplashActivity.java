package com.yuepeng.wxb.ui.activity;

import android.content.Intent;
import android.os.Bundle;

import com.wstro.thirdlibrary.utils.AppCache;
import com.yuepeng.wxb.utils.PreUtils;

import androidx.appcompat.app.AppCompatActivity;


public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        checkFirstInstall();
    }

    private void checkFirstInstall() {
        if (PreUtils.getBoolean("FIRSTINSTALL")){
            startActivity(new Intent(this, GuideActivity.class));
            finish();
        }else {

            ToMain();
        }
    }

    private void ToMain() {
        if (PreUtils.getTokenInfo()!=null && !PreUtils.getTokenInfo().isEmpty()){
            AppCache.IsLogin = true;

            startActivity(new Intent(this, MainActivity.class));
        }else {
            startActivity(new Intent(this, LoginActivity.class));
        }
//        RouterUtil.navigateTo(RouterPath.Main.F_GUIDE);
        finish();
    }
}