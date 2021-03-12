package com.yuepeng.wxb.ui.activity;

import android.os.Parcelable;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.PoiInfo;
import com.baidu.mapapi.search.poi.OnGetPoiSearchResultListener;
import com.baidu.mapapi.search.poi.PoiCitySearchOption;
import com.baidu.mapapi.search.poi.PoiDetailResult;
import com.baidu.mapapi.search.poi.PoiDetailSearchResult;
import com.baidu.mapapi.search.poi.PoiIndoorResult;
import com.baidu.mapapi.search.poi.PoiResult;
import com.baidu.mapapi.search.poi.PoiSearch;
import com.gyf.immersionbar.ImmersionBar;
import com.wstro.thirdlibrary.base.BasePresenter;
import com.yuepeng.wxb.R;
import com.yuepeng.wxb.base.BaseActivity;
import com.yuepeng.wxb.databinding.ActivityMapSearchBinding;
import com.yuepeng.wxb.utils.PreUtils;

import java.util.List;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


/**
 * Created by wangjun on 2019-10-09.
 */
public class MapSearchActivity extends BaseActivity<ActivityMapSearchBinding, BasePresenter> implements OnGetPoiSearchResultListener {

    private PoiSearch mPoiSearch = null;
    private SearchAdapter adapter;
    private List<PoiInfo> poiInfoList;
    public static final int RESPONSE_MAP_SEARCH_CODE = 1111;
    private int radius;
    private LatLng cneter;
    private String city;
    //private String city;

    @Override
    protected View injectTarget() {
        return null;
    }

    @Override
    protected BasePresenter createPresenter() {
        return null;
    }

    @Override
    protected void Retry() {

    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_map_search;
    }

    @Override
    protected void initView() {
//        ImmersionBar.with(this).keyboardEnable(true).titleBar(mBinding.llTitle);
        ImmersionBar.with(this)
                .statusBarDarkFont(true)
                .navigationBarDarkIcon(true).titleBar(mBinding.llTitle)
                .init();
        adapter = getAdapter();
        radius = getIntent().getIntExtra("radius",100);
        Parcelable parcelable = getIntent().getParcelableExtra("latlng");
        if (parcelable == null){
            cneter = PreUtils.getLocation();
        }else {
            cneter = (LatLng) parcelable;
        }
        View empty = LayoutInflater.from(this).inflate(R.layout.layout_empty_redlist, null,
                false);
        TextView tv = empty.findViewById(R.id.empty_order_tv);
        tv.setText("暂无数据");
        adapter.setEmptyView(empty);
        LinearLayoutManager lm = new LinearLayoutManager(this);
        lm.setOrientation(RecyclerView.VERTICAL);
        mBinding.recycleView.setLayoutManager(lm);
        mBinding.recycleView.setAdapter(adapter);
        city = PreUtils.getCity();
    }

    @Override
    protected void initData() {
        mBinding.back.setOnClickListener(view -> finish());
        mPoiSearch = PoiSearch.newInstance();
        mPoiSearch.setOnGetPoiSearchResultListener(this);
        afterInitView();
    }

    protected void afterInitView() {
        mBinding.editSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String keyWord = s.toString().trim();
                if (!TextUtils.isEmpty(keyWord)) {
                    int currentPage = 0;
                    mPoiSearch.searchInCity(new PoiCitySearchOption()
                            //.radius(radius)
                            //.location(cneter)
                            .city(TextUtils.isEmpty(city)?"南昌":city)
                            .keyword(keyWord)
                            //.pageNum(20)// 分页编号
                            .cityLimit(false)
                            .scope(2));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {


            }
        });
    }
    protected SearchAdapter getAdapter() {
        return new SearchAdapter(this);
    }

    @Override
    public void onGetPoiResult(PoiResult poiResult) {
        //解析result获取POI信息
        poiInfoList = poiResult.getAllPoi();
       // tvText.setText("搜索结果");
        adapter.setNewInstance(poiInfoList);
    }

    @Override
    public void onGetPoiDetailResult(PoiDetailResult poiDetailResult) {

    }

    @Override
    public void onGetPoiDetailResult(PoiDetailSearchResult poiDetailSearchResult) {

    }

    @Override
    public void onGetPoiIndoorResult(PoiIndoorResult poiIndoorResult) {

    }
}
