package com.study91.audiobook.media;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.util.Log;

import com.study91.audiobook.dict.MediaState;
import com.study91.audiobook.tools.ImageTools;

import java.io.IOException;

/**
 * Asset资源媒体播放器
 */
class AssetMediaPlayer implements IMediaPlayer {
    private Field m = new Field(); //私有字段

    /**
     * 构造器
     * @param context 应用程序上下文
     */
    AssetMediaPlayer(Context context) {
        m.context = context;
    }

    @Override
    public void setFilename(String filename) {
        if (filename != null && !filename.trim().equals(getFilename())) {
            m.mediaFilename = filename.trim(); //设置文件名

            //如果媒体播放器不为null，重置媒体播放器
            if (m.mediaPlayer != null) {
                getMediaPlayer().reset();
                m.mediaState = MediaState.IDLE; //重置媒体播放器状态为空闲
            }

            AssetManager assetManager = getContext().getAssets(); //获取资源管理器

            try {
                AssetFileDescriptor assetFileDescriptor = assetManager.openFd(getFilename()); //打开文件

                //设置媒体播放器数据源
                getMediaPlayer().setDataSource(
                        assetFileDescriptor.getFileDescriptor(),
                        assetFileDescriptor.getStartOffset(),
                        assetFileDescriptor.getLength());

                getMediaPlayer().prepareAsync(); //准备媒体
                m.mediaState = MediaState.PREPARING; //设置状态为准备中状态
            } catch (IOException e) {
                Log.e(getClass().getName(), "setFilename：" + e.getMessage());
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public String getFilename() {
        return m.mediaFilename;
    }

    @Override
    public void setTitle(String title) {
        if (title != null && !title.trim().equals(getTitle())) {
            m.title = title.trim();
        }
    }

    @Override
    public String getTitle() {
        return m.title;
    }

    @Override
    public void play() {
        switch (getMediaState()) {
            case PREPARED: //媒体准备就绪
                if (!isPlaying()) {
                    getMediaPlayer().start();
                }
                break;
            case PREPARING: //媒体准备中
                m.mediaState = MediaState.WAITING_TO_PLAY; //设置为等待播放状态
                break;
            case WAITING_TO_PLAY: //等待播放状态
                //注：如果媒体处于等待播放状态，将在setOnPreparedListener.onPrepared中执行getMediaPlayer().start()进行播放
                break;
            case COMPLETED:
                m.mediaState = MediaState.PREPARED; //重置为媒体准备就绪状态
                getMediaPlayer().start();
                break;
        }
    }

    @Override
    public void pause() {
        switch (getMediaState()) {
            case PREPARED: //媒体准备就绪
                if (isPlaying()) {
                    getMediaPlayer().pause();
                }
                break;
        }
    }

    @Override
    public boolean isPlaying() {
        boolean isPlaying = false;

        switch (getMediaState()) {
            case PREPARED:  //媒体准备就绪
                isPlaying = getMediaPlayer().isPlaying();
                break;
        }

        return isPlaying;
    }

    @Override
    public void seekTo(int position) {
        switch (getMediaState()) {
            case PREPARED:  //媒体准备就绪
                getMediaPlayer().seekTo(position);
                break;
        }
    }

    @Override
    public int getLength() {
        int length = 0;

        switch (getMediaState()) {
            case PREPARED:  //媒体准备就绪
                length = getMediaPlayer().getDuration();
                break;
            case COMPLETED: //媒体播放完成
                length = getMediaPlayer().getDuration();
                break;
        }

        return length;
    }

    @Override
    public int getPosition() {
        int position = 0;

        switch (getMediaState()) {
            case PREPARED:  //媒体准备就绪
                position = getMediaPlayer().getCurrentPosition();
                break;
            case COMPLETED: //媒体播放完成
                position = getMediaPlayer().getCurrentPosition();
                break;
        }

        return position;
    }

    @Override
    public void setVolume(float leftVolume, float rightVolume) {
        m.leftVolume = leftVolume;
        m.rightVolume = rightVolume;
        getMediaPlayer().setVolume(leftVolume, rightVolume);
    }

    @Override
    public void setIconFilename(String filename) {
        if (filename != null && !filename.trim().equals(m.iconFilename)) {
            m.iconFilename = filename.trim(); //图标文件名
            m.iconDrawable = ImageTools.getDrawable(getContext(), filename); //图标Drawable
        }
    }

    @Override
    public String getIconFilename() {
        return m.iconFilename;
    }

    @Override
    public Drawable getIconDrawable() {
        return m.iconDrawable;
    }

    @Override
    public MediaState getMediaState() {
        return m.mediaState;
    }

    @Override
    public void setOnPreparedListener(MediaPlayer.OnPreparedListener listener) {
        m.preparedListener = listener;
    }

    @Override
    public void setOnCompletionListener(MediaPlayer.OnCompletionListener listener) {
        m.completionListener = listener;
    }

    @Override
    public void release() {
        getMediaPlayer().release();
        m.mediaState = MediaState.IDLE; //重置媒体播放器状态为空闲
    }

    /**
     * 获取应用程序上下文
     */
    private Context getContext() {
        return m.context;
    }

    /**
     * 获取媒体播放器
     */
    private MediaPlayer getMediaPlayer() {
        if (m.mediaPlayer == null) {
            m.mediaPlayer = new MediaPlayer();
            m.mediaState = MediaState.IDLE; //初始化媒体状态为空闲状态

            //设置媒体准备就绪事件监听器
            m.mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    switch (getMediaState()) {
                        case WAITING_TO_PLAY: //等待播放媒体
                            m.mediaState = MediaState.PREPARED; //设置媒体状态为准备就绪
                            play(); //播放
                            break;
                        case PREPARING: //准备媒体中
                            m.mediaState = MediaState.PREPARED; //设置媒体状态为准备就绪
                            break;
                    }

                    //执行自定义的媒体准备就绪事件
                    if (m.preparedListener != null) m.preparedListener.onPrepared(mp);
                }
            });

            //设置媒体播放完成事件监听器
            m.mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    m.mediaState = MediaState.COMPLETED; //设置媒体状态为播放完成

                    //执行自定义的媒体播放完成事件
                    if (m.completionListener != null) m.completionListener.onCompletion(mp);
                }
            });

            //设置媒体播放器错误事件监听器
            m.mediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
                @Override
                public boolean onError(MediaPlayer mp, int what, int extra) {
                    Log.d(getClass().getName(), "媒体播放器错误，错误代码：" + "what=" + what + "extra" + extra);
                    switch (what) {
                        case -1004:
                            Log.d(getClass().getName(), "MEDIA_ERROR_IO");
                            break;
                        case -1007:
                            Log.d(getClass().getName(), "MEDIA_ERROR_MALFORMED");
                            break;
                        case 200:
                            Log.d(getClass().getName(), "MEDIA_ERROR_NOT_VALID_FOR_PROGRESSIVE_PLAYBACK");
                            break;
                        case 100:
                            Log.d(getClass().getName(), "MEDIA_ERROR_SERVER_DIED");
                            break;
                        case -110:
                            Log.d(getClass().getName(), "MEDIA_ERROR_TIMED_OUT");
                            break;
                        case 1:
                            Log.d(getClass().getName(), "MEDIA_ERROR_UNKNOWN");
                            break;
                        case -1010:
                            Log.d(getClass().getName(), "MEDIA_ERROR_UNSUPPORTED");
                            break;
                    }

                    switch (extra) {
                        case 800:
                            Log.d(getClass().getName(), "MEDIA_INFO_BAD_INTERLEAVING");
                            break;
                        case 702:
                            Log.d(getClass().getName(), "MEDIA_INFO_BUFFERING_END");
                            break;
                        case 701:
                            Log.d(getClass().getName(), "MEDIA_INFO_METADATA_UPDATE");
                            break;
                        case 802:
                            Log.d(getClass().getName(), "MEDIA_INFO_METADATA_UPDATE");
                            break;
                        case 801:
                            Log.d(getClass().getName(), "MEDIA_INFO_NOT_SEEKABLE");
                            break;
                        case 1:
                            Log.d(getClass().getName(), "MEDIA_INFO_UNKNOWN");
                            break;
                        case 3:
                            Log.d(getClass().getName(), "MEDIA_INFO_VIDEO_RENDERING_START");
                            break;
                        case 700:
                            Log.d(getClass().getName(), "MEDIA_INFO_VIDEO_TRACK_LAGGING");
                            break;
                    }

                    return false;
                }
            });
        }

        return m.mediaPlayer;
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
         * 媒体文件名
         */
        String mediaFilename;

        /**
         * 图标文件名
         */
        String iconFilename;

        /**
         * 图标Drawable
         */
        Drawable iconDrawable;

        /**
         * 标题
         */
        String title;

        /**
         * 左声道音量
         */
        float leftVolume = 1f;

        /**
         * 右声道音量
         */
        float rightVolume = 1f;

        /**
         * 媒体播放器
         */
        MediaPlayer mediaPlayer;

        /**
         * 媒体状态（初始化默认为空闲状态）
         */
        MediaState mediaState = MediaState.IDLE;

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
