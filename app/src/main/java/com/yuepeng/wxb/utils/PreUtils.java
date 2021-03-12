package com.yuepeng.wxb.utils;

import android.content.Context;
import android.text.TextUtils;

import com.baidu.location.Address;
import com.baidu.location.BDLocation;
import com.baidu.mapapi.model.LatLng;
import com.blankj.utilcode.util.SPUtils;
import com.wstro.thirdlibrary.utils.AppCache;

public class PreUtils {

    public static final String CONFIG_SEARCH_HISTORY = "search_history";
    public static final String CONFIG_TOKEN_NAME = "token_config";
    public static final String CONFIG_USER_CONFIG = "user_config";
    public static final String CONFIG_USER_LOCATION = "user_loaction";
    public static final String KEY_TOKEN = "access_token";
    public static final String KEY_EXPIRES = "expires_in"; //token过期时间
    public static final String KEY_LAT = "lat";
    public static final String KEY_LON = "lon";
    public static final String KEY_CITY = "city";
    public static final String KEY_ADDRESS = "address";
    public static final String KEY_READ_MSG_COUNT = "readMessageCount";
    public static final String KEY_FIRSTLOGIN = "firstLogin";

    /**
     * 保存搜索记录
     *
     * @param keyword
     */
    public static void saveHistory(String keyword){
        SPUtils sp = SPUtils.getInstance(CONFIG_SEARCH_HISTORY, Context.MODE_PRIVATE);
        String old_text = sp.getString("history", "");
        // 利用StringBuilder.append新增内容，逗号便于读取内容时用逗号拆分开
        StringBuilder builder = new StringBuilder(old_text);
        builder.append(keyword + ",");

        // 判断搜索内容是否已经存在于历史文件，已存在则不重复添加
        if (!old_text.contains(keyword + ",")) {
            sp.put("history", builder.toString());
        }
    }

    public static String[] getHistoryList(){
        SPUtils sp = SPUtils.getInstance(CONFIG_SEARCH_HISTORY, Context.MODE_PRIVATE);
        String history = sp.getString("history", "");
        String[] history_arr = history.split(",");
        if (history_arr.length > 50) {
            String[] newArrays = new String[50];
            System.arraycopy(history_arr, 0, newArrays, 0, 50);
        }
        return history_arr;
    }

    public static void cleanHistory() {
        SPUtils sp = SPUtils.getInstance(CONFIG_SEARCH_HISTORY, Context.MODE_PRIVATE);
        sp.clear();
    }

//    public static void putTokenInfo(BaseLoginResponse.Metadata tokenInfo) {
//        SPUtils sp = SPUtils.getInstance(CONFIG_TOKEN_NAME, Context.MODE_PRIVATE);
//        System.out.println("===re==="+tokenInfo.getAccessToken());
//        AppCache.setAccessToken(tokenInfo.getAccessToken());
//        sp.put(KEY_TOKEN,tokenInfo.getAccessToken());
//        sp.put(KEY_EXPIRES,tokenInfo.getExpiresIn());
//    }

    public static void putTokenInfo(String tokenInfo) {
        SPUtils sp = SPUtils.getInstance(CONFIG_TOKEN_NAME, Context.MODE_PRIVATE);
        AppCache.setAccessToken(tokenInfo);
        sp.put(KEY_TOKEN,tokenInfo);
    }

    public static String getTokenInfo() {
        SPUtils sp = SPUtils.getInstance(CONFIG_TOKEN_NAME, Context.MODE_PRIVATE);
        String tokenInfo = sp.getString(KEY_TOKEN);
        if (tokenInfo != null && !tokenInfo.isEmpty()){
            AppCache.setAccessToken(tokenInfo);
        }
        return tokenInfo;
    }

    public static void putAddress(BDLocation location){
        SPUtils sp = SPUtils.getInstance(CONFIG_USER_LOCATION, Context.MODE_PRIVATE);
        if (location != null){
            sp.put(KEY_LAT,location.getLatitude()+"");
            sp.put(KEY_LON,location.getLongitude()+"");
            sp.put(KEY_CITY,location.getCity()+"");
            Address address = location.getAddress();
            String addString = address.city+address.district+address.town+address.street;
            sp.put(KEY_ADDRESS,addString);
        }else {
            sp.put(KEY_LAT,"");
            sp.put(KEY_LON,"");
            sp.put(KEY_ADDRESS,"");
            sp.put(KEY_CITY,"");
        }

    }

    public static LatLng getLocation(){
        SPUtils sp = SPUtils.getInstance(CONFIG_USER_LOCATION, Context.MODE_PRIVATE);
        if (!TextUtils.isEmpty(sp.getString(KEY_LAT)) && !TextUtils.isEmpty(sp.getString(KEY_LON))) {
            return new LatLng(Double.parseDouble(sp.getString(KEY_LAT)), Double.parseDouble(sp.getString(KEY_LON)));
        }else {
            return new LatLng(0,0);
        }
    }

    public static String getAddress(){
        SPUtils sp = SPUtils.getInstance(CONFIG_USER_LOCATION, Context.MODE_PRIVATE);
        return sp.getString(KEY_ADDRESS,"信号不好");
    }

    public static String getCity() {
        SPUtils sp = SPUtils.getInstance(CONFIG_USER_LOCATION, Context.MODE_PRIVATE);
        return sp.getString(KEY_CITY,"南昌");
    }
//    public static void putUserInfo(User userInfo){
//        SPUtils sp = SPUtils.getInstance(CONFIG_USER_NAME,Context.MODE_PRIVATE);
//        if (userInfo.getNickname()!=null){
//            sp.put(KEY_NICK_NAME,userInfo.getNickname());
//        }
//        if (userInfo.getAvatar()!=null){
//            sp.put(KEY_AVATAR,userInfo.getAvatar());
//        }
//        sp.put(KEY_LEVEL,userInfo.getLevel());
//        sp.put(KEY_RECHARGE_BALANCE,userInfo.getRechargeBalance());
//        sp.put(KEY_INCOME_BALANCE,userInfo.getIncomeBalance());
//        sp.put(KEY_DEPOSIT,userInfo.getDeposit());
//    }

//    public static User getUserInfo(){
//        SPUtils sp = SPUtils.getInstance(CONFIG_USER_NAME,Context.MODE_PRIVATE);
//        User userInfo = new User();
//        userInfo.setNickname(sp.getString(KEY_NICK_NAME,""));
//        userInfo.setAvatar(sp.getString(KEY_AVATAR,""));
//        userInfo.setPhone(sp.getString(KEY_MOBILE,""));
//        userInfo.setLevel(sp.getString(KEY_LEVEL,""));
//        userInfo.setRechargeBalance(sp.getString(KEY_RECHARGE_BALANCE,""));
//        userInfo.setIncomeBalance(sp.getString(KEY_INCOME_BALANCE,""));
//        userInfo.setDeposit(sp.getString(KEY_DEPOSIT,""));
//        return userInfo;
//    }


    public static void putString(String key, String value) {
        SPUtils sp = SPUtils.getInstance(CONFIG_USER_CONFIG, Context.MODE_PRIVATE);
        sp.put(key,value);
    }



    public static void putInt(String key, int value) {
        SPUtils sp = SPUtils.getInstance(CONFIG_USER_CONFIG, Context.MODE_PRIVATE);
        sp.put(key,value);
    }

    public static int getInt(String key) {
        SPUtils sp = SPUtils.getInstance(CONFIG_USER_CONFIG, Context.MODE_PRIVATE);
        return sp.getInt(key,0);
    }

    public static String getString(String key,String defValue){
        SPUtils sp = SPUtils.getInstance(CONFIG_USER_CONFIG, Context.MODE_PRIVATE);
        return sp.getString(key,defValue);
    }

    public static boolean getBoolean(String key){
        SPUtils sp = SPUtils.getInstance(CONFIG_USER_CONFIG, Context.MODE_PRIVATE);
        return sp.getBoolean(key,true);
    }

    public static void putBoolean(String key,boolean value){
        SPUtils sp = SPUtils.getInstance(CONFIG_USER_CONFIG, Context.MODE_PRIVATE);
        sp.put(key,value);
    }

}
