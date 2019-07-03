package com.study91.audiobook.media;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;

import com.study91.audiobook.dict.SoundType;

/**
 * 书媒体播放器
 */
class BookMediaPlayer implements IBookMediaPlayer{
    private Field m = new Field(); //私有变量类

    /**
     * 构造器
     * @param context 应用程序上下文
     */
    BookMediaPlayer(Context context) {
        m.context = context;
    }

    @Override
    public void setAudioFile(String filename, String title, String iconFilename) {
        //媒体文件名有变化时才设置语音文件
        if (filename != null && !filename.trim().equals(getAudioMediaPlayer().getFilename())) {
            if (getAudioMediaPlayer().isPlaying()) getAudioMediaPlayer().pause(); //如果正在播放，先暂停播放
            getAudioMediaPlayer().setFilename(filename); //设置语音文件名
            getAudioMediaPlayer().setIconFilename(iconFilename); //设置图标文件名
            getAudioMediaPlayer().setTitle(title); //设置标题
        }
    }

    @Override
    public String getAudioFilename() {
        return getAudioMediaPlayer().getFilename();
    }

    @Override
    public String getAudioTitle() {
        return getAudioMediaPlayer().getTitle();
    }

    @Override
    public String getIconFilename() {
        return getAudioMediaPlayer().getIconFilename();
    }

    @Override
    public Drawable getIconDrawable() {
        return getAudioMediaPlayer().getIconDrawable();
    }

    @Override
    public void setMusicFile(String filename, String title) {
        //媒体文件名有变化时才设置背景音乐文件
        if (filename != null && !filename.trim().equals(getMusicMediaPlayer().getFilename())) {
            if (getMusicMediaPlayer().isPlaying()) getMusicMediaPlayer().pause(); //如果正在播放，先暂停播放
            getMusicMediaPlayer().setFilename(filename); //设置背景音乐文件名
            getMusicMediaPlayer().setTitle(title); //设置标题
        }
    }

    @Override
    public String getMusicFilename() {
        return getMusicMediaPlayer().getFilename();
    }

    @Override
    public String getMusicTitle() {
        return getMusicMediaPlayer().getTitle();
    }

    @Override
    public void setSoundType(SoundType soundType) {
        //如果需要设置的声音类型没有背景音乐时，检查当前是否正在播放背景音乐，如果正在播放，先暂停播放
        if (soundType != SoundType.AUDIO_AND_MUSIC) {
            if (getMusicMediaPlayer().isPlaying()) {
                getMusicMediaPlayer().pause();
            }
        }

        m.soundType = soundType; //设置声音类型变量
        resetVolume(); //重置音量
    }

    @Override
    public SoundType getSoundType() {
        return m.soundType;
    }

    @Override
    public void setAudioVolume(float volume) {
        m.audioVolume = volume; //语音音量
        resetVolume(); //重置音量
    }

    @Override
    public float getAudioVolume() {
        return m.audioVolume;
    }

    @Override
    public void setMusicVolume(float volume) {
        m.musicVolume = volume;
        resetVolume(); //重置音量
    }

    @Override
    public float getMusicVolume() {
        return m.musicVolume;
    }

    @Override
    public void play() {
        //播放语音
        if (!getAudioMediaPlayer().isPlaying()) {
            getAudioMediaPlayer().play();
        }

        //播放背景音乐
        if (getSoundType() == SoundType.AUDIO_AND_MUSIC) {
            if (!getMusicMediaPlayer().isPlaying()) {
                getMusicMediaPlayer().play();
            }
        }
    }

    @Override
    public void pause() {
        //暂停语音
        if (getAudioMediaPlayer().isPlaying()) {
            getAudioMediaPlayer().pause();
        }

        //暂停背景音乐
        if (getSoundType() == SoundType.AUDIO_AND_MUSIC) {
            if (getMusicMediaPlayer().isPlaying()) {
                getMusicMediaPlayer().pause();
            }
        }
    }

    @Override
    public boolean isPlaying() {
        return getAudioMediaPlayer().isPlaying();
    }

    @Override
    public void setIsLooping(boolean isLooping) {
        m.isLooping = isLooping;
    }

    @Override
    public void seekTo(int position) {
        getAudioMediaPlayer().seekTo(position);
    }

    @Override
    public int getPosition() {
        return getAudioMediaPlayer().getPosition();
    }

    @Override
    public int getLength() {
        return getAudioMediaPlayer().getLength();
    }

    @Override
    public void release() {
        if(m.audioMediaPlayer != null) getAudioMediaPlayer().release(); //释放语音媒体播放器
        if(m.musicMediaPlayer != null) getMusicMediaPlayer().release(); //释放背景音乐媒体播放器
    }

    @Override
    public void setOnPreparedListener(MediaPlayer.OnPreparedListener listener) {
        m.preparedListener = listener;
    }

    @Override
    public void setOnCompletionListener(MediaPlayer.OnCompletionListener listener) {
        m.completionListener = listener;
    }

    /**
     * 获取语音媒体播放器
     * @return 语音媒体播放器
     */
    private IMediaPlayer getAudioMediaPlayer() {
        if (m.audioMediaPlayer == null) {
            m.audioMediaPlayer = MediaManager.createMediaPlayer(getContext());

            //语音准备就绪事件监听器
            m.audioMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    onMediaPrepared(mp); //执行媒体准备就绪方法
                }
            });

            //语音播放完成事件监听器
            m.audioMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    onMediaCompletion(mp);
                }
            });
        }

        return m.audioMediaPlayer;
    }

    /**
     * 获取背景音乐媒体播放器
     * @return 背景音乐媒体播放器
     */
    private IMediaPlayer getMusicMediaPlayer() {
        if (m.musicMediaPlayer == null) {
            m.musicMediaPlayer = MediaManager.createMediaPlayer(getContext());
            m.musicMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    //TODO 背景音乐播放完成暂时处理为继续循环播放，后期可能处理为按需播放
                    mp.seekTo(0);
                    m.musicMediaPlayer.play();
                }
            });
        }

        return m.musicMediaPlayer;
    }

    /**
     * 重置音量
     */
    private void resetVolume() {
        switch (getSoundType()) {
            case AUDIO_AND_MUSIC: //有语音和背景音乐
                getAudioMediaPlayer().setVolume(getAudioVolume(), getAudioVolume());
                getMusicMediaPlayer().setVolume(getMusicVolume(), getMusicVolume());
                break;
            case ONLY_AUDIO: //只有语音
                getAudioMediaPlayer().setVolume(getAudioVolume(), getAudioVolume());
                break;
            case AUDIO_LEFT: //语音左声道，背景音乐右声道
                getAudioMediaPlayer().setVolume(getAudioVolume(), getMusicVolume());
                break;
            case AUDIO_RIGHT: //语音右声道，背景音乐左声道
                getAudioMediaPlayer().setVolume(getMusicVolume(), getAudioVolume());
                break;
        }
    }

    /**
     * 是否循环播放
     * @return 循环值（true=循环播放，false=不循环播放）
     */
    private boolean isLooping() {
        return m.isLooping;
    }

    /**
     * 媒体准备就绪
     * @param mp 媒体播放器
     */
    private void onMediaPrepared(MediaPlayer mp) {
        if (m.preparedListener != null){
            m.preparedListener.onPrepared(mp);
        }
    }

    /**
     * 媒体播放完成事件监听器
     * @param mp 媒体播放器
     */
    private void onMediaCompletion(MediaPlayer mp) {
        //TODO 载入语音电子书时通过具体算法播放后续内容

        mp.seekTo(0); //重新定位到媒体头部位置

        if (isLooping()) {
            //如果循环播放时执行
            play();
        } else {
            //不循环播放时，如果有背景音乐，暂停音乐播放
            if (getSoundType() == SoundType.AUDIO_AND_MUSIC) {
                if (getMusicMediaPlayer().isPlaying()) {
                    getMusicMediaPlayer().pause();
                }
            }
        }

        if (m.completionListener != null) {
            m.completionListener.onCompletion(mp);
        }
    }

    /**
     * 获取应用程序上下文
     * @return 应用程序上下文
     */
    private Context getContext() {
        return m.context;
    }

    /**
     * 私有字段类
     */
    private class Field {
        /**
         * 应用程序上下文
         */
        Context context;

        /**
         * 语音媒体播放器
         */
        IMediaPlayer audioMediaPlayer;

        /**
         * 语音音量（默认为最大音量）
         */
        float audioVolume = 1;

        /**
         * 背景音乐媒体播放器
         */
        IMediaPlayer musicMediaPlayer;

        /**
         * 背景音乐音量（默认为最大音量）
         */
        float musicVolume = 1;

        /**
         * 循环值（默认为循环播放）
         */
        boolean isLooping = true;

        /**
         * 声音类型（默认为有语音和背景音乐）
         */
        SoundType soundType = SoundType.AUDIO_AND_MUSIC;

        /**
         * 媒体准备就绪事件监听器
         */
        MediaPlayer.OnPreparedListener preparedListener;

        /**
         * 媒体播放完成事件监听器
         */
        MediaPlayer.OnCompletionListener completionListener;
    }
}
