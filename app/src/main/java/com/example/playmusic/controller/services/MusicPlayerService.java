package com.example.playmusic.controller.services;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import com.example.playmusic.R;
import com.example.playmusic.controller.fragment.PlayerFragment;
import com.example.playmusic.model.Music;

import java.io.IOException;

public class MusicPlayerService extends Service implements MediaPlayer.OnPreparedListener {
    public static final String EXTRA_MUSIC = "com.example.playmusic.extraMusic";
    public static final String ACTION_PLAY = "com.example.playmusic.actionPlay";
    public static final String ACTION_NEXT_PREV = "com.example.playmusic.actionNextPrev";

    private Music mMusic;

    private MediaPlayer mMediaPlayer = null;
    private IBinder mIBinder =new MusicPlayerBinder() ;

    public static Intent newIntent(Context context, Music music,@NonNull String action) {
        Intent intent = new Intent(context, MusicPlayerService.class);
        intent.setAction(action);
        intent.putExtra(EXTRA_MUSIC, music);
        return intent;
    }

    public MusicPlayerService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        mMusic = (Music) intent.getSerializableExtra(EXTRA_MUSIC);
        mMediaPlayer = new MediaPlayer();
      setUpMediaPlayer(mMusic);

        return mIBinder;
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        switch (intent.getAction()) {
            case ACTION_PLAY:
                if (mMediaPlayer.isPlaying()) {
                    mMediaPlayer.pause();
                } else {
                    mMediaPlayer.start();
                }
                break;

            default:

                break;

        }
        return START_STICKY;
    }

/*    @RequiresApi(api = Build.VERSION_CODES.O)
    private void createNotification(Intent playerFragmentIntent, PendingIntent playPendingIntent) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            NotificationManager notificationManager=getSystemService(NotificationManager.class);
            NotificationChannel channel=new NotificationChannel("default",
                    "channel name",
                    NotificationManager.IMPORTANCE_DEFAULT);
            channel.setDescription("channel description");
            notificationManager.createNotificationChannel(channel);
            Notification notification=new NotificationCompat.Builder(this,"default")
                    .setSmallIcon(R.drawable.ic_music_notes__1_)
                    .setContentTitle("Music player")
                    .setOngoing(true)
                    .setContentText("")
                    .setContentIntent(PendingIntent.getActivity(this,0,
                            playerFragmentIntent,0))
                    .addAction(android.R.drawable.ic_media_play,"play",playPendingIntent)
                    .build();
            startForeground(101,notification);
        }
    }*/

    public void setUpMediaPlayer(Music music) {


        try {

            AssetFileDescriptor afd = this.getAssets().openFd(music.getAssetPath());
            mMediaPlayer.setDataSource(afd.getFileDescriptor(),
                    afd.getStartOffset(),
                    afd.getLength());

            //ToDO : prepareAsync
            //ToDo : I can't use prepareAsync
            //mMediaPlayer.prepareAsync(); // prepare async to not block main thread
            mMediaPlayer.prepare();
            mMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    mMediaPlayer.start();

                }
            });
            mMediaPlayer.setOnCompletionListener(mp -> {
                stopForeground(true);
                stopSelf();
            });
        } catch (IOException e) {
            Log.d("MPS",e.getMessage(),e);

        }

    }

    @Override
    public void onPrepared(MediaPlayer mp) {

    }


    @Override
    public void onDestroy() {
        super.onDestroy();

    }

    public class MusicPlayerBinder extends Binder {
        public MusicPlayerService getService() {
            return MusicPlayerService.this;
        }
    }

    public MediaPlayer getMediaPlayer() {
        return this. mMediaPlayer;
    }

}
