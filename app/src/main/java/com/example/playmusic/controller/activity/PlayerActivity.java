package com.example.playmusic.controller.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.widget.Toast;

import com.example.playmusic.R;
import com.example.playmusic.controller.fragment.PlayerFragment;
import com.example.playmusic.model.Music;

public class PlayerActivity extends AppCompatActivity  {
    public static final String EXTRA_ACTIVITY_PLAYER_MUSIC =
            " com.example.playmusic.extraActivityPlayerMusic";
    public static final String EXTRA_MUSIC_POSITION = "com.example.playmusic.extraMusicPosition";
    private Music mMusic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);
         mMusic= (Music) getIntent().getSerializableExtra(EXTRA_ACTIVITY_PLAYER_MUSIC);
         int position=getIntent().getIntExtra(EXTRA_MUSIC_POSITION,0);
        FragmentManager fragmentManager=getSupportFragmentManager();
        PlayerFragment playerFragment =PlayerFragment.newInstance(mMusic,position);
        Fragment fragment=fragmentManager.findFragmentById(R.id.fragment_container);
        if (fragment==null) {
            fragmentManager.beginTransaction()
                    .add(R.id.fragment_container, playerFragment)
                    .commit();
        }

    }
    public static Intent newIntent(Context context, Music music, int position){
        Intent intent=new Intent(context,PlayerActivity.class);
        intent.putExtra(EXTRA_ACTIVITY_PLAYER_MUSIC,music);
        intent.putExtra(EXTRA_MUSIC_POSITION,position);
        return intent;
    }




}