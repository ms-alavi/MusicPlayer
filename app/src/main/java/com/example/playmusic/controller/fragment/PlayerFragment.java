package com.example.playmusic.controller.fragment;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.os.Bundle;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import android.os.IBinder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;

import com.example.playmusic.R;
import com.example.playmusic.controller.services.MusicPlayerService;
import com.example.playmusic.databinding.FragmentPlayerBinding;
import com.example.playmusic.model.Music;
import com.example.playmusic.repository.PlayMusicRepository;

import java.io.IOException;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;


public class PlayerFragment extends Fragment implements ServiceConnection {
    public static final String TAG_MUSIC_PLAYER = "com.example.playmusic.tagMusicPlayer";
    public static final String MEDIA_PLAYER = "mediaPlayer";
    public static final String TAG_MUSIC_POSITION = "com.example.playmusic.tagMusicPosition";
    public static final String PF = "PF";
    FragmentPlayerBinding mBinding;
    private Music mMusic;
    private int mPosition;
    private MediaPlayer mMediaPlayer;
    private int currentPosition;
    private boolean mIsServiceConnected;
    private Timer mTimer;
    private PlayMusicRepository mPlayMusicRepository;
    private List<Music> mMusics;

    public PlayerFragment() {
        // Required empty public constructor
    }


    public static PlayerFragment newInstance(Music music, int position) {
        PlayerFragment fragment = new PlayerFragment();
        Bundle args = new Bundle();
        args.putSerializable(TAG_MUSIC_PLAYER, music);
        args.putInt(TAG_MUSIC_POSITION, position);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mMusic = (Music) getArguments().getSerializable(TAG_MUSIC_PLAYER);
            mPosition = getArguments().getInt(TAG_MUSIC_POSITION);
        }

        mPlayMusicRepository = PlayMusicRepository.getInstance(getActivity());
        mMusics = mPlayMusicRepository.getMusics();
        mTimer = new Timer();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mBinding = DataBindingUtil.inflate(inflater
                , R.layout.fragment_player
                , container
                , false);

        setSongPicture();

        return mBinding.getRoot();
    }



    private void initViews() {
        mBinding.txtTimer.setText(showMusicTime(mMediaPlayer.getDuration()));
        mBinding.seekBarPlayer.setMax(mMediaPlayer.getDuration());

        mTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                mBinding.seekBarPlayer.setProgress(mMediaPlayer.getCurrentPosition());
            }
        }, 0, 1000);
    }

    private void setListeners() {

        mBinding.btnPausePlay.setOnClickListener(v -> {
            getActivity().startService(MusicPlayerService.newIntent(getActivity(), mMusic
                    , MusicPlayerService.ACTION_PLAY));
            if (mMediaPlayer.isPlaying()) {
                mBinding.btnPausePlay.setImageResource(R.drawable.ic_play_f);

            } else {
                mBinding.btnPausePlay.setImageResource(R.drawable.ic_pause_f);
            }

        });

        mBinding.btnBackward.setOnClickListener(v -> {
            if (mPosition == 0) {
                mPosition = mMusics.size() - 1;
            }
            else mPosition--;
            setUpPlayer();
        });
        mBinding.btnForward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mPosition == mMusics.size()-1) {
                    mPosition = 0;
                }
                else mPosition++;
                setUpPlayer();
            }
        });
        mBinding.seekBarPlayer.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                mBinding.txtTimer.setText(showMusicTime(mMediaPlayer.getDuration() - progress));
                if (mMediaPlayer != null && fromUser) {
                    mMediaPlayer.seekTo(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

    }

    private void setUpPlayer() {
        mMusic = mMusics.get(mPosition);
        getActivity().unbindService(this);
        mIsServiceConnected = false;
        mMediaPlayer.release();
        mTimer.purge();
        mTimer.cancel();
        mTimer=new Timer();
        setSongPicture();
        getActivity().stopService(MusicPlayerService.newIntent(getActivity(), mMusic
                , ""));
        getActivity().bindService(MusicPlayerService.newIntent(getActivity(),
                mMusic, "")
                , this
                , Context.BIND_AUTO_CREATE);
    }
    private String showMusicTime(int duration) {
        String time = String.format("%2d : %02d ",
                TimeUnit.MILLISECONDS.toMinutes(duration),
                TimeUnit.MILLISECONDS.toSeconds(duration) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(duration))
        );
        return time;
    }
    private void setSongPicture() {
        MediaMetadataRetriever mmr = setDataSourceForMmr();
        byte[] artBytes = mmr.getEmbeddedPicture();
        if (artBytes != null) {
            Bitmap bm = BitmapFactory.decodeByteArray(artBytes, 0, artBytes.length);
            mBinding.imgPlayer.setImageBitmap(bm);
        } else {
            mBinding.imgPlayer.setImageDrawable(getResources().getDrawable(R.mipmap.pic_plalyer));
        }
        mBinding.txtSongNamePlayer.setText(mMusic.getName());
    }

    private MediaMetadataRetriever setDataSourceForMmr() {
        MediaMetadataRetriever mmr = new MediaMetadataRetriever();
        AssetFileDescriptor afd = null;
        try {
            afd = getActivity().getAssets().openFd(mMusic.getAssetPath());
        } catch (IOException e) {
            Log.d("PF", e.getMessage());
        }
        mmr.setDataSource(afd.getFileDescriptor(),
                afd.getStartOffset(),
                afd.getLength());
        return mmr;
    }



    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mMediaPlayer != null) mMediaPlayer.release();
        mTimer.purge();
        mTimer.cancel();
        getActivity().unbindService(this);
        mIsServiceConnected = false;
    }

    @Override
    public void onStart() {
        super.onStart();
        getActivity().bindService(MusicPlayerService.newIntent(getActivity(), mMusic, "")
                , this
                , Context.BIND_AUTO_CREATE);

    }

    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        MusicPlayerService musicPlayerService = ((MusicPlayerService.MusicPlayerBinder) service).getService();
        mMediaPlayer = musicPlayerService.getMediaPlayer();
        mIsServiceConnected = true;
        initViews();
        setListeners();
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
        getActivity().unbindService(this);
        mIsServiceConnected=false;

    }


}