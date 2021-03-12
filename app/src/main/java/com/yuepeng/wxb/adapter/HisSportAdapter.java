package com.yuepeng.wxb.adapter;

import android.app.Activity;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.blankj.utilcode.util.TimeUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.wstro.thirdlibrary.entity.KithEntity;
import com.yuepeng.wxb.R;
import com.yuepeng.wxb.entity.HisSportEntity;
import com.yuepeng.wxb.ui.activity.MapSportActivity;

import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Created by wangjun on 2021/2/5.
 */
public class HisSportAdapter extends BaseQuickAdapter<HisSportEntity, HisSportAdapter.ViewHolder> {
    private Activity activity;
    public HisSportAdapter(List<HisSportEntity> list, Activity activity) {
        super(R.layout.item_his_tj,list);
        this.activity = activity;
    }


    @Override
    protected void convert(@NotNull ViewHolder viewHolder, HisSportEntity item) {
        if (item.getStartTime() != 0 && item.getEndTime()!=0) {
            viewHolder.setVisible(R.id.fl,true);
            viewHolder.startTime.setText("开始时间：" +TimeUtils.millis2String(item.getStartTime()*1000));
            viewHolder.endTime.setText("结束时间：" + TimeUtils.millis2String(item.getEndTime()*1000));
            if (!TextUtils.isEmpty(item.getStartAddress())) {
                viewHolder.startStay.setText(item.getStartAddress());
            }
            if (!TextUtils.isEmpty(item.getEndAddress())) {
                viewHolder.endStay.setText(item.getEndAddress());
            }
            viewHolder.itemView.setOnClickListener(view -> {
                Intent intent = new Intent(view.getContext(), MapSportActivity.class);
                intent.putExtra("start",item.getStartTime());
                intent.putExtra("end",item.getEndTime());
                KithEntity kithEntity = (KithEntity) activity.getIntent().getSerializableExtra("KithEntity");
                intent.putExtra("KithEntity",kithEntity);
                activity.startActivity(intent);
            });
        }else {
            viewHolder.setVisible(R.id.fl,false);
        }
    }

    public class ViewHolder extends BaseViewHolder{
        TextView startTime;
        TextView startStay;
        TextView endTime;
        TextView endStay;
        public ViewHolder(@NotNull View view) {
            super(view);
            startTime = view.findViewById(R.id.tv_start_time);
            endTime = view.findViewById(R.id.tv_end_time);
            startStay = view.findViewById(R.id.tv_start_location);
            endStay = view.findViewById(R.id.tv_end_location);
        }
    }
}
