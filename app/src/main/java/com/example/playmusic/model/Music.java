package com.example.playmusic.model;

import java.io.File;
import java.io.Serializable;

public class Music implements Serializable {
    private String mName;
    private String mSongName;
    private String mSingerName;
    private String mAssetPath;
    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }



    public String getSongName() {
        return mSongName;
    }

    public void setSongName(String songName) {
        mSongName = songName;
    }

    public String getSingerName() {
        return mSingerName;
    }

    public void setSingerName(String singerName) {
        mSingerName = singerName;
    }



    public String getAssetPath() {
        return mAssetPath;
    }

    public void setAssetPath(String assetPath) {
        mAssetPath = assetPath;
    }

    public Music(String assetPath) {
        mAssetPath = assetPath;
        String[] sections = assetPath.split(File.separator);
        String fileNameWithExtension = sections[sections.length - 1];
        int lastDotIndex = fileNameWithExtension.lastIndexOf(".");

         mName = fileNameWithExtension.substring(0, lastDotIndex);
        mSongName=mName.substring(mName.lastIndexOf("-")+1);
        mSingerName="mName.substring(0,mName.lastIndexOf(-));";
    }

    public Music() {
    }
}
