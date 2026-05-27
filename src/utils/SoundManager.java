package utils;

import javax.sound.sampled.*;
import javax.swing.AbstractButton;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import javax.swing.Timer;

public class SoundManager {
    // 专门保管背景音乐的播放器
    private static Clip bgmClip;
    
    // 全局音量变量 (0 到 100)
    private static int bgmVolume = 80;
    private static int sfxVolume = 30;

    private static Map<String, Clip> sfxCache = new HashMap<>(); // 可选：缓存已经加载过的音效，避免重复加载

    private static String currentBgmPath = ""; // 记录当前正在播放的BGM路径，方便切换时判断是否需要重新加载
    private static long bgmPausePosition = 0;

    private static boolean isBgmLowered;

    public static void preLoadSounds(){
        loadSound("mouse", "resource/music/mouse_click.wav");
        loadSound("confirm","resource/music/confirm_click.wav");
        loadSound("victory", "resource/music/victory.wav");
        loadSound("descend", "resource/music/descend.wav");
        loadSound("ascend", "resource/music/ascend.wav");
        loadSound("combo", "resource/music/combo.wav");
    }

    public static void loadSound(String key, String filePath) {
        try {
            File audioFile = new File(filePath);
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(audioFile);
            Clip clip = AudioSystem.getClip();
            clip.open(audioStream);
            sfxCache.put(key, clip);
        } catch (Exception e) {
            System.err.println("加载音效失败：" + filePath);
        }
    }

    // ==========================================
    // 1. 播放背景音乐 (无限循环)
    // ==========================================
    public static void playBGM(String filePath) {
        if(filePath.equals(currentBgmPath) && bgmClip != null && bgmClip.isRunning()) {
            // 如果要播放的BGM已经在放了，就不重复加载了
            return;
        }
        try {
            if(bgmClip!=null&&bgmClip.isRunning()) {

                bgmClip.stop();
            }
            File audioFile = new File(filePath);
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(audioFile);
            bgmClip = AudioSystem.getClip();
            bgmClip.open(audioStream);

            currentBgmPath = filePath; // 更新当前BGM路径

            applyVolume(bgmClip, bgmVolume); // 应用当前音量
            bgmClip.loop(Clip.LOOP_CONTINUOUSLY); // 让BGM无限循环
        } catch (Exception e) {
            System.err.println("找不到背景音乐文件或格式不正确：" + filePath);
        }
    }

    public static void lowerBgmVolume(){
        if(bgmClip != null && !isBgmLowered){
            isBgmLowered = true;
            applyVolume(bgmClip, bgmVolume/3);
        }
    }

    public static void restoreBgmVolume(){
        if(bgmClip != null && isBgmLowered){
            isBgmLowered = false;
            applyVolume(bgmClip, bgmVolume);
        }
    }

    // ==========================================
    // 2. 播放一次性音效 (比如点击声)
    // ==========================================
    public static void playSFX(String name) {
        Clip clip = sfxCache.get(name);
        if (clip != null) {
            try {
                clip.setFramePosition(0);
                applyVolume(clip, (name == "combo")? Math.min(100, sfxVolume *3) : sfxVolume); // 应用当前音量
                clip.start();
            } catch (Exception e) {
                System.err.println("播放音效失败：" + name);
            }
        } else {
            System.err.println("未找到音效：" + name);
        }
    }

    // ==========================================
    // 4. 调节音量的接口 (供 SettingsDialog 调用)
    // ==========================================
    public static void setBgmVolume(int volume) {
        bgmVolume = Math.max(0, Math.min(volume, 100));
        if (bgmClip != null) {
            applyVolume(bgmClip, bgmVolume); // 如果音乐正在放，立刻改变音量
        }
    }

    public static void setSfxVolume(int volume) {
        sfxVolume = Math.max(0, Math.min(volume, 100)); // 音效下一次点击时生效
    }

    public static int getBgmVolume() { return bgmVolume; }
    public static int getSfxVolume() { return sfxVolume; }

    // ==========================================
    // 核心黑科技：将 0-100 的数值转化为真实分贝 (dB)
    // ==========================================
    private static void applyVolume(Clip clip, int volume) {
        try {
            FloatControl gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
            if (volume == 0) {
                gainControl.setValue(gainControl.getMinimum());
            } else {
                float dB = (float) (Math.log10(volume / 100.0) * 20.0);
                gainControl.setValue(dB);
            }
        } catch (IllegalArgumentException e) {
            // 某些系统可能不支持音量调节，忽略报错
        }
    }
    public static void fadeOutBGM(int durationMs) {
        if (bgmClip == null || !bgmClip.isRunning()) return;

        Clip fadingClip = bgmClip; // 为了在定时器里访问
        currentBgmPath = ""; // 立刻清空当前BGM路径，允许新的BGM加载

        FloatControl gainControl = (FloatControl) fadingClip.getControl(FloatControl.Type.MASTER_GAIN);
        float startVolume = gainControl.getValue();
        float endVolume = gainControl.getMinimum(); // 目标是静音
        
        // 我们用一个定时器，每 50 毫秒降一点音量
        long startTime = System.currentTimeMillis();
        Timer timer = new Timer(50, null);
        timer.addActionListener(e -> {
            // 计算已经过去了多少时间
            long elapsed = System.currentTimeMillis() - startTime;
            
            if (elapsed >= durationMs) {
                // 时间到了，彻底静音并停止
                gainControl.setValue(endVolume);
                bgmClip.stop();
                timer.stop();
            } else {
                // 还在渐隐过程中，按比例降低音量
                float progress = (float) elapsed / durationMs;
                float newVolume = startVolume + (endVolume - startVolume) * progress;
                gainControl.setValue(newVolume);
            }
        });

        timer.start();
    }
}