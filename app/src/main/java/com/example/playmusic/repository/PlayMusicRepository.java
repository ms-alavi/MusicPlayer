package com.example.playmusic.repository;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import com.example.playmusic.model.Music;


import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class PlayMusicRepository {
    public static final String PATH = "musics";
    public static final String TAG = "PlayMusicRepository";
    private static PlayMusicRepository sInstance;

    private Context mContext;
    private MediaPlayer mMediaPlayer;
    private List<Music> mMusic = new ArrayList<>();

    public static PlayMusicRepository getInstance(Context context) {
        if (sInstance == null)
            sInstance = new PlayMusicRepository(context);

        return sInstance;
    }


    private PlayMusicRepository(Context context) {
        mContext = context.getApplicationContext();
        setMediaAttributes();
        AssetManager assetManager = mContext.getAssets();
        getMusicOfAsset(assetManager);


    }

    private void getMusicOfAsset(AssetManager assetManager) {
        try {
            String[] fileNames = assetManager.list(PATH);
            for (String fileName: fileNames) {
                String assetPath = PATH + File.separator + fileName;
                Music music = new Music(assetPath);
                mMusic.add(music);
            }

        } catch (IOException e) {
            Log.d(TAG,e.getMessage());
        }
    }

    private void setMediaAttributes() {
        mMediaPlayer=new MediaPlayer();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mMediaPlayer.setAudioAttributes(
                    new AudioAttributes.Builder()
                            .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                            .setUsage(AudioAttributes.USAGE_MEDIA)
                            .build()
            );
        }
    }

    public List<Music> getMusics() {

        return mMusic;
    }

    public void play(Music music) {

       if (music==null) Toast.makeText(mContext, "music is null", Toast.LENGTH_SHORT).show();;
        try {
            //player.setDataSource(afd.getFileDescriptor(),afd.getStartOffset(),afd.getLength());
            AssetFileDescriptor  afd = mContext.getAssets().openFd(music.getAssetPath());
            mMediaPlayer.setDataSource(afd.getFileDescriptor(),
                    afd.getStartOffset(),
                    afd.getLength());
            mMediaPlayer.prepare();
            mMediaPlayer.start();
        } catch (IOException e) {
            Toast.makeText(mContext, "Error is : "+music.getAssetPath(), Toast.LENGTH_SHORT).show();
        }

    }

}
