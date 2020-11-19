package com.example.playmusic.controller.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.example.playmusic.R;
import com.example.playmusic.controller.fragment.MusicListFragment;
import com.example.playmusic.databinding.MusicPagerActivityBinding;

import com.google.android.material.tabs.TabLayoutMediator;

public class MusicPager extends AppCompatActivity {
    private MusicPagerActivityBinding mMusicPagerActivityBinding;


    public Intent newIntent(Context context){
        Intent intent=new Intent(context, MusicPager.class);
        return intent;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mMusicPagerActivityBinding= DataBindingUtil.setContentView(this, R.layout.music_pager_activity);

        FixedTabsPagerAdapter adapter=new FixedTabsPagerAdapter(this);
        mMusicPagerActivityBinding.viewPager.setAdapter(adapter);
        // TODO : Why i can't inflate custom view ?
        /*mTabViewBinding=DataBindingUtil.inflate(LayoutInflater.from(this),R.layout.tab_view,
                null,false);*/
        new TabLayoutMediator(mMusicPagerActivityBinding.tabLayout,
                mMusicPagerActivityBinding.viewPager,
                (tab, position) -> {
                    switch (position) {
                        case 0:
                            tab.setCustomView(R.layout.tab_view_song);

                            break;
                        case 1:
                            tab.setCustomView(R.layout.tab_view_singer);

                            break;
                        case 2:
                            tab.setCustomView(R.layout.tab_view_album);
                            break;

                    }
                }).attach();

    }
    private class FixedTabsPagerAdapter extends FragmentStateAdapter {

        public FixedTabsPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
            super(fragmentActivity);
        }

        @NonNull
        @Override
        public Fragment createFragment(int position) {
            MusicListFragment musicListFragment= MusicListFragment.newInstance();
            return musicListFragment;

        }


        @Override
        public int getItemCount() {
            return 3;
        }


    }

}