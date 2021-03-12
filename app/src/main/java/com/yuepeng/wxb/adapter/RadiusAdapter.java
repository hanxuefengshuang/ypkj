package com.yuepeng.wxb.adapter;

import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.yuepeng.wxb.R;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * Created by wangjun on 2021/2/10.
 */
public class RadiusAdapter extends BaseQuickAdapter<Integer, BaseViewHolder> {
    private int oldIndex = 0;
    public RadiusAdapter(@Nullable List<Integer> data, int raduis) {
        super(R.layout.item_radius, data);
        for (int i = 0; i < data.size() ; i++) {
            if (data.get(i) == raduis){
                oldIndex = i;
            }
        }
    }

    @Override
    protected void convert(@NotNull BaseViewHolder holder, Integer integer) {
        TextView tv = holder.getView(R.id.tv);
        if (oldIndex == holder.getAdapterPosition()){
            tv.setTextColor(holder.itemView.getContext().getResources().getColor(R.color.white));
            tv.setBackground(getContext().getResources().getDrawable(R.drawable.radius_back));
        }else {
            tv.setTextColor(holder.itemView.getContext().getResources().getColor(R.color.color999999));
            tv.setBackgroundColor(holder.itemView.getContext().getResources().getColor(R.color.white));
        }
        holder.setText(R.id.tv,integer >= 1000 ? integer/1000 + "km" : integer+"m");
        holder.getView(R.id.tv).setOnClickListener(view -> {
            if (oldIndex == holder.getAdapterPosition()){
                return;
            }else {
                oldIndex = holder.getAdapterPosition();
                notifyDataSetChanged();
            }
        });
    }

    public int getOldIndex() {
        return oldIndex;
    }
}