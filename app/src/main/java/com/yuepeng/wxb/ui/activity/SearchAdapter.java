package com.yuepeng.wxb.ui.activity;

import android.app.Activity;
import android.content.Intent;

import com.baidu.mapapi.search.core.PoiInfo;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.yuepeng.wxb.R;

import static com.yuepeng.wxb.ui.activity.MapSearchActivity.RESPONSE_MAP_SEARCH_CODE;


/**
 * Created by wangjun on 2019-10-09.
 */
public class SearchAdapter extends BaseQuickAdapter<PoiInfo, BaseViewHolder> {
    private Activity activity;
    public SearchAdapter( Activity context) {
        super(R.layout.item_search_map);
//        View view = LayoutInflater.from(context).inflate(R.layout.item_clear_search,null,false);
//        view.setOnClickListener(v->{
//            setNewData(new ArrayList<>());
//        });
//        addFooterView(view);
        this.activity = context;
    }

    @Override
    protected void convert(BaseViewHolder helper, PoiInfo item) {

        helper.itemView.setOnClickListener(v->{
            if (getData().size() > 0) {
                Intent intent = new Intent();
                intent.putExtra("PoiItem",item);
                activity.setResult(RESPONSE_MAP_SEARCH_CODE,intent);
                activity.finish();
            }
        });
//        helper.getView(R.id.iv_delete).setOnClickListener(v->{
//
//            remove(helper.getLayoutPosition());
//        });
        //helper.setText(R.id.tv_desc,item.getAddress()+ " ");
        helper.setText(R.id.tv_mark,item.getName()+"");

    }


}
