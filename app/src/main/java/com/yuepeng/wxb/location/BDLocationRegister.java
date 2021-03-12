package com.yuepeng.wxb.location;

import android.text.TextUtils;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.blankj.utilcode.util.LogUtils;
import com.wstro.thirdlibrary.utils.manager.NetManager;

import java.util.HashMap;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.RequestBody;

import static com.wstro.thirdlibrary.api.ApiService.ADDLOCATION;


/**
 * Created by wangjun on 2021/1/24.
 */
public class BDLocationRegister extends BDAbstractLocationListener {
    private OnLocationListener listener;
    private BDLocation bdLocation = null;


    public void setOnlocationListener(OnLocationListener listener) {
        this.listener = listener;
        if (bdLocation !=null)
            listener.sucessLocation(bdLocation);
    }
    /**
     * 获取到location 成功的回调
     */
    public interface OnLocationListener{
        void sucessLocation(BDLocation location);
        void failedLocation(String msg);
    }

    @Override
    public void onReceiveLocation(BDLocation location) {
        //获取定位结果
        StringBuffer sb = new StringBuffer(256);
        LogUtils.i("定位："+location.getAddrStr());
        sb.append("time : ");
        sb.append(location.getTime());    //获取定位时间

        sb.append("\nerror code : ");
        sb.append(location.getLocType());    //获取类型类型

        sb.append("\nlatitude : ");
        sb.append(location.getLatitude());    //获取纬度信息

        sb.append("\nlontitude : ");
        sb.append(location.getLongitude());    //获取经度信息

        sb.append("\nradius : ");
        sb.append(location.getRadius());    //获取定位精准度

        if (location.getLocType() == BDLocation.TypeGpsLocation){

            // GPS定位结果
            sb.append("\nspeed : ");
            sb.append(location.getSpeed());    // 单位：公里每小时

            sb.append("\nsatellite : ");
            sb.append(location.getSatelliteNumber());    //获取卫星数

            sb.append("\nheight : ");
            sb.append(location.getAltitude());    //获取海拔高度信息，单位米

            sb.append("\ndirection : ");
            sb.append(location.getDirection());    //获取方向信息，单位度

            sb.append("\naddr : ");
            sb.append(location.getAddrStr());    //获取地址信息
            sb.append("\ndescribe : ");
            sb.append("gps定位成功");

        } else if (location.getLocType() == BDLocation.TypeNetWorkLocation){

            // 网络定位结果
            sb.append("\naddr : ");
            sb.append(location.getAddrStr());    //获取地址信息

            sb.append("\noperationers : ");
            sb.append(location.getOperators());    //获取运营商信息

            sb.append("\ndescribe : ");
            sb.append("网络定位成功");

        } else if (location.getLocType() == BDLocation.TypeOffLineLocation) {

            // 离线定位结果
            sb.append("\ndescribe : ");
            sb.append("离线定位成功，离线定位结果也是有效的");

        } else if (location.getLocType() == BDLocation.TypeServerError) {

            sb.append("\ndescribe : ");
            sb.append("服务端网络定位失败，可以反馈IMEI号和大体定位时间到loc-bugs@baidu.com，会有人追查原因");
            listener.failedLocation("定位失败，百度服务端网络定位失败");
            return;
        } else if (location.getLocType() == BDLocation.TypeNetWorkException) {

            sb.append("\ndescribe : ");
            sb.append("网络不同导致定位失败，请检查网络是否通畅");
            listener.failedLocation("定位失败，请检查网络是否通畅");
            return;
        } else if (location.getLocType() == BDLocation.TypeCriteriaException) {

            sb.append("\ndescribe : ");
            sb.append("无法获取有效定位依据导致定位失败，一般是由于手机的原因，处于飞行模式下一般会造成这种结果，可以试着重启手机");
            listener.failedLocation("定位失败，无法获取有效定位数据");
            return;
        }

        sb.append("\nlocationdescribe : ");
        sb.append(location.getLocationDescribe());    //位置语义化信息

        Log.i("BaiduLocationApiDem", sb.toString());
        if (!TextUtils.isEmpty(location.getAddrStr())) {
            bdLocation = location;
            if (listener!=null){
                listener.sucessLocation(location);
            }
            Map<String, Object> map = new HashMap<>();
            map.put("lat",String.valueOf(location.getLatitude()));
            map.put("lng",String.valueOf(location.getLongitude()));
            map.put("location",bdLocation.getAddrStr());
           NetManager.http_post(ADDLOCATION,map);
           // HttpManager.getInstance().post(map,ADDLOCATION,0);
//            OkHttpUtils
//                    .post()//also can use delete() ,head() , patch()
//                    .addHeader("Content-Type","application/json; charset=utf-8")
//                    .addHeader("token", AppCache.getAccessToken())
//                    .url(ADDLOCATION)
//                    //.requestBody(getBody(map))//
//                    .addParams("lat",String.valueOf(bdLocation.getLatitude()))
//                    .addParams("lng",String.valueOf(bdLocation.getLongitude()))
//                    .build()
//                    .execute(new StringCallback() {
//                        @Override
//                        public void onError(Call call, Exception e, int id) {
//                            LogUtils.d(e.getMessage());
//                        }
//
//                        @Override
//                        public void onResponse(String response, int id) {
//                            LogUtils.d(response);
//                        }
//                    });
        }else {
            listener.failedLocation("定位失败，错误："+location.getLocType());
        }

    }

    protected RequestBody getBody(Map<String, Object> map){
        RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), JSON.toJSONString(map));
        return body;
    }
}
