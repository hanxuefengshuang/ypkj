package com.yuepeng.wxb.ui.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.ViewGroup;

import com.baidu.mapapi.model.LatLng;
import com.baidu.navisdk.adapter.BaiduNaviManagerFactory;
import com.wstro.thirdlibrary.entity.KithEntity;
import com.yuepeng.wxb.R;
import com.yuepeng.wxb.location.BNDemoFactory;
import com.yuepeng.wxb.location.ForegroundService;
import com.yuepeng.wxb.ui.fragment.DemoRouteResultFragment;

import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

/**
 * Created by wangjun on 2021/2/7.
 */
public class MapRouteSelecter2Activity extends FragmentActivity {

    public boolean isGuideFragment = false;
    private BroadcastReceiver mReceiver;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 开启前台服务防止应用进入后台gps挂掉
        startService(new Intent(this, ForegroundService.class));

        setContentView(R.layout.activity_driving);
        ViewGroup mapContent = findViewById(R.id.map_container);
        BaiduNaviManagerFactory.getMapManager().attach(mapContent);
        initBroadCastReceiver();
        initFragment();
    }

    private void initBroadCastReceiver() {
        IntentFilter filter = new IntentFilter();
        filter.addAction("com.navi.ready");
        mReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                BNDemoFactory.getInstance().initCarInfo();
                KithEntity kithEntity = (KithEntity) getIntent().getSerializableExtra("KithEntity");
                LatLng latLng = new LatLng(Double.parseDouble(kithEntity.getLat()),Double.parseDouble(kithEntity.getLng()));
                BNDemoFactory.getInstance().initRoutePlanNode(latLng);
            }
        };
        registerReceiver(mReceiver, filter);
    }

    protected void initFragment() {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction tx = fm.beginTransaction();
        DemoRouteResultFragment fragment = new DemoRouteResultFragment();
        tx.add(R.id.fragment_content, fragment, "RouteResult");
        tx.commit();
    }


    @Override
    public void onBackPressed() {
        if (isGuideFragment) {
            BaiduNaviManagerFactory.getRouteGuideManager().onBackPressed(false);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        BaiduNaviManagerFactory.getMapManager().onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        BaiduNaviManagerFactory.getMapManager().onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mReceiver);
        stopService(new Intent(this, ForegroundService.class));
    }
}