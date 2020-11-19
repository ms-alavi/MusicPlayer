package com.example.playmusic.controller.fragment;

import android.content.ComponentName;
import android.content.Context;
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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.example.playmusic.R;
import com.example.playmusic.controller.activity.MusicPlayerService;
import com.example.playmusic.databinding.FragmentPlayerBinding;
import com.example.playmusic.model.Music;
import com.example.playmusic.repository.PlayMusicRepository;

import java.io.IOException;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;


public class PlayerFragment extends Fragment implements ServiceConnection {
    public static final String TAG_MUSIC_PLAYER = "tagMusicPlayer";
    public static final String MEDIA_PLAYER = "mediaPlayer";
    FragmentPlayerBinding mBinding;
    private Music mMusic;
    private MediaPlayer mMediaPlayer;
    private int currentPosition;
    private boolean mIsServiceConnected;
    private Timer mTimer;
    private PlayMusicRepository mPlayMusicRepository;
    private List<Music> mMusics;

    public PlayerFragment() {
        // Required empty public constructor
    }


    public static PlayerFragment newInstance(Music music) {
        PlayerFragment fragment = new PlayerFragment();
        Bundle args = new Bundle();
        args.putSerializable(TAG_MUSIC_PLAYER, music);

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mMusic = (Music) getArguments().getSerializable(TAG_MUSIC_PLAYER);
        }
        getActivity().bindService(MusicPlayerService.newIntent(getActivity(), mMusic)
                , this
                , Context.BIND_AUTO_CREATE);
        mPlayMusicRepository=PlayMusicRepository.getInstance(getActivity());
        mMusics=mPlayMusicRepository.getMusics();
        mTimer = new Timer();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_player, container, false);
        if (mIsServiceConnected == true) {
            initViews();
            setListeners();
        }
         MediaMetadataRetriever mmr = extractMetadataOfMp3();
        String albumName = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUM);
        byte[] artBytes =  mmr.getEmbeddedPicture();
        if(artBytes!=null)
        {
            //     InputStream is = new ByteArrayInputStream(mmr.getEmbeddedPicture());
            Bitmap bm = BitmapFactory.decodeByteArray(artBytes, 0, artBytes.length);
            mBinding.imgPlayer.setImageBitmap(bm);
        }
        else
        {
            mBinding.imgPlayer.setImageDrawable(getResources().getDrawable(R.drawable.ic_fast_forward));
        }
        mBinding.txtSongNamePlayer.setText(mMusic.getName());
        // mBinding.txtSongNamePlayer.setText(albumName);
        return mBinding.getRoot();
    }

    private MediaMetadataRetriever extractMetadataOfMp3() {
        MediaMetadataRetriever  mmr = new MediaMetadataRetriever();
        AssetFileDescriptor afd = null;
        try {
            afd = getActivity().getAssets().openFd(mMusic.getAssetPath());
        } catch (IOException e) {
            e.printStackTrace();
        }
        mmr.setDataSource(afd.getFileDescriptor(),
                afd.getStartOffset(),
                afd.getLength());
        return mmr;
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
            if (mMediaPlayer.isPlaying()) {
                mBinding.btnPausePlay.setImageResource(R.drawable.ic_play_f);
                mMediaPlayer.pause();
                currentPosition = mMediaPlayer.getCurrentPosition();
            } else {
                mBinding.btnPausePlay.setImageResource(R.drawable.ic_pause_f);
                mMediaPlayer.start();
                // TODO : Service
              /*  getActivity().startService(new Intent(getActivity(),MusicPlayerService.class));
                mBinding.btnPausePlay.setBackgroundResource(R.drawable.ic_pause_f);
                mMediaPlayer.seekTo(currentPosition);
                mMediaPlayer.start();*/
            }

        });
        mBinding.btnBackward.setOnClickListener(v -> {

        });

    }


    private String showMusicTime(int duration) {
        String time = String.format("%2d : %02d ",
                TimeUnit.MILLISECONDS.toMinutes(duration),
                TimeUnit.MILLISECONDS.toSeconds(duration) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(duration))
        );
        return time;
    }


    @Override
    public void onStop() {
        mMediaPlayer.release();
        mTimer.purge();
        mTimer.cancel();
        getActivity().unbindService(this);
        super.onStop();
    }

    @Override
    public void onStart() {
        super.onStart();


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

    }
}