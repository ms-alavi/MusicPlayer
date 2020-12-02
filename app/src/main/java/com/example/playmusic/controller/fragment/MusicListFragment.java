package com.example.playmusic.controller.fragment;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.playmusic.R;
import com.example.playmusic.controller.activity.PlayerActivity;
import com.example.playmusic.databinding.MusicListFragmentBinding;
import com.example.playmusic.databinding.RowOfListBinding;
import com.example.playmusic.model.Music;
import com.example.playmusic.repository.PlayMusicRepository;

import java.io.IOException;
import java.util.List;


public class MusicListFragment extends Fragment  {
    public static final String MLF = "MLF";
    private MusicListFragmentBinding mMusicListFragmentBinding;
    private PlayMusicRepository mPlayMusicRepository;

    public MusicListFragment() {
        // Required empty public constructor
    }


    public static MusicListFragment newInstance() {
        MusicListFragment fragment = new MusicListFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPlayMusicRepository = PlayMusicRepository.getInstance(getContext());

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mMusicListFragmentBinding = DataBindingUtil.inflate(LayoutInflater.from(getContext()),
                R.layout.music_list_fragment,
                container,
                false);
        Log.d(MLF,"music List of repository : "+ mPlayMusicRepository.getMusics());


        setUpAdapter();

        return mMusicListFragmentBinding.getRoot();
    }

    private void setUpAdapter() {

        mMusicListFragmentBinding.musicRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        MusicAdapter adapter = new MusicAdapter(mPlayMusicRepository.getMusics());

        mMusicListFragmentBinding.musicRecyclerView.setAdapter(adapter);
    }




    private class MusicHolder extends RecyclerView.ViewHolder {
        private RowOfListBinding mBiding;
        private int mPosition;

        public MusicHolder(RowOfListBinding binding) {
            super(binding.getRoot());
            mBiding = binding;
            mBiding.getRoot().setOnClickListener(v -> {
                /*Music music = mBiding.getMusic();
               mPlayMusicRepository.play(music);*/
               Intent intent=PlayerActivity.newIntent(getContext(),mBiding.getMusic(), mPosition);
               startActivity(intent);

            });
        }

        public void bindTask(Music music,int position) {
            mBiding.setMusic(music);
            mPosition=position;
            if (position%2!=0){
                mBiding.relativeRaw.setBackgroundResource(R.color.gray_dark);
            }

        }
    }

    private class MusicAdapter extends RecyclerView.Adapter<MusicHolder> {
        private List<Music> mMusics;

        public MusicAdapter(List<Music> musics) {

            mMusics = musics;
        }

        public List<Music> getMusics() {
            return mMusics;
        }

        public void setMusics(List<Music> musics) {
            mMusics = musics;
        }

        @NonNull
        @Override
        public MusicHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            RowOfListBinding binding = DataBindingUtil.inflate(LayoutInflater.from(getContext()),
                    R.layout.row_of_list
                    , parent
                    , false);
            return new MusicHolder(binding);
        }

        @Override
        public void onBindViewHolder(@NonNull MusicHolder holder, int position) {

            Music music = mMusics.get(position);
            holder.bindTask(music,position);
        }

        @Override
        public int getItemCount() {
            return mMusics.size();
        }
    }
}