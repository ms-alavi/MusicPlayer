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
            " com.example.playmusic.controller.activity.extraActivityPlayerMusic";
    private Music mMusic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);
         mMusic= (Music) getIntent().getSerializableExtra(EXTRA_ACTIVITY_PLAYER_MUSIC);
        FragmentManager fragmentManager=getSupportFragmentManager();
        PlayerFragment playerFragment =PlayerFragment.newInstance(mMusic);
        Fragment fragment=fragmentManager.findFragmentById(R.id.fragment_container);
        if (fragment==null) {
            fragmentManager.beginTransaction()
                    .add(R.id.fragment_container, playerFragment)
                    .commit();
        }

    }
    public static Intent newIntent(Context context, Music music){
        Intent intent=new Intent(context,PlayerActivity.class);
        intent.putExtra(EXTRA_ACTIVITY_PLAYER_MUSIC,music);
        return intent;
    }




}