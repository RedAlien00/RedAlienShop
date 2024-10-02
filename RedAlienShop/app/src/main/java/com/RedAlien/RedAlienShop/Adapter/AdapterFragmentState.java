package com.RedAlien.RedAlienShop.Adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import java.util.ArrayList;

public class AdapterFragmentState extends androidx.viewpager2.adapter.FragmentStateAdapter {
    private final static String TAG = "FragmentStateAdapter";

    private ArrayList<Fragment> mFragments;

    public AdapterFragmentState(@NonNull FragmentActivity fragmentActivity, ArrayList<Fragment> list) {
        super(fragmentActivity);
        this.mFragments = list;
    }
    @NonNull
    @Override
    public Fragment createFragment(int position) {
        return this.mFragments.get(position);
    }

    @Override
    public int getItemCount() {
        return this.mFragments.size();
    }
}
