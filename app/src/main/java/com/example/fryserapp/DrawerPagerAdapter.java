package com.example.fryserapp;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class DrawerPagerAdapter extends FragmentStateAdapter {
    public DrawerPagerAdapter(@NonNull FragmentActivity fa) { super(fa); }
    @NonNull @Override public Fragment createFragment(int position) { return new DrawerFragment(); }
    @Override public int getItemCount() { return 1; } // Enkel liste

}
