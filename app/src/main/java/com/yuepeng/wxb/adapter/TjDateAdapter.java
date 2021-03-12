package com.yuepeng.wxb.adapter;

import com.wstro.thirdlibrary.entity.HisTimeResponse;
import com.yuepeng.wxb.ui.fragment.TjDateFragment;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

/**
 * Created by wangjun on 2021/2/8.
 */
public class TjDateAdapter extends FragmentStatePagerAdapter {
    private List<HisTimeResponse.RecordsBean> titles;

    public TjDateAdapter(@NonNull FragmentManager fm, List<HisTimeResponse.RecordsBean> titles) {
        super(fm);
        this.titles = titles;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        return TjDateFragment.getIntance(titles.get(position));
    }

    @Override
    public int getCount() {
        return titles.size();
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return titles.get(position).getKithTjDate();
    }
}