package com.desktoppet.system;

import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;

/**
 * 背景音乐管理类（单例模式）
 * 负责人：
 * 功能：
 *     程序启动时自动循环播放背景音乐
 *     支持播放、暂停、切换开关
 *     播放 assets/music 目录下的 WAV 音乐
 */
public class MusicManager {

    private static MusicManager instance;
    private Clip clip;
    private boolean playing;
    private static final String MUSIC_PATH = "assets/music/菊次郎的夏天.wav";

    private MusicManager() {
        playing = false;
    }

    public static MusicManager getInstance() {
        if (instance == null) {
            instance = new MusicManager();
        }
        return instance;
    }

    /**
     * 播放音乐（循环播放）
     */
    public void play() {
        try {
            if (clip != null && clip.isRunning()) {
                return; // 已经在播放，不重复启动
            }
            File musicFile = new File(MUSIC_PATH);
            if (!musicFile.exists()) {
                System.out.println("[MusicManager] 音乐文件不存在: " + MUSIC_PATH);
                return;
            }
            AudioInputStream audioIn = AudioSystem.getAudioInputStream(musicFile);
            clip = AudioSystem.getClip();
            clip.open(audioIn);
            clip.loop(Clip.LOOP_CONTINUOUSLY);  // 无限循环
            playing = true;
            System.out.println("[MusicManager] 背景音乐开始播放");
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            System.out.println("[MusicManager] 音乐播放失败: " + e.getMessage());
        }
    }

    /**
     * 暂停音乐
     */
    public void stop() {
        if (clip != null && clip.isRunning()) {
            clip.stop();
            playing = false;
            System.out.println("[MusicManager] 背景音乐已暂停");
        }
    }

    /**
     * 切换播放/暂停
     */
    public void toggle() {
        if (playing) {
            stop();
        } else {
            play();
        }
    }

    public boolean isPlaying() {
        return playing;
    }
}

