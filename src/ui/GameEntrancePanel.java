package ui;

import utils.FontManager;
import utils.SoundManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.util.ArrayList;
import java.util.List;

public class GameEntrancePanel extends JPanel {

    private int state = 0; // 0: 播放开场动画, 1: 倒计时阶段
    
    // 动画序列参数
    private List<Image> frames = new ArrayList<>();
    private int currentFrame = 0;
    private int frameDelay;
    
    private int triggerSFX=0;
    private String sfxName;
    // 倒计时参数
    private String[] countdownTexts = {"准备？", "3", "2", "1", "开 始 ！"};
    private int countdownIndex = 0;
    private JLabel textLabel;

    public GameEntrancePanel(String imagePrefix, int frameCount, int delay) {
        this.frameDelay = delay;
        setOpaque(false); // 必须是透明的
        setLayout(new GridBagLayout()); // 绝对居中布局

        // 【核心】：吸收掉所有鼠标点击！只要这个面板还在，玩家点什么都没反应
        addMouseListener(new MouseAdapter() {}); 

        // 1. 加载第二段开场动画帧 (比如: game_enter/E0.png ...)
        for (int i = 0; i < frameCount; i++) {
            frames.add(new ImageIcon("resource/images/" + imagePrefix + i + ".png").getImage());
        }

        // 2. 初始化中间的巨大倒计时文字
        textLabel = new JLabel("");
        textLabel.setFont(FontManager.pixelFontTitle); // 标题大小
        textLabel.setForeground(Color.WHITE);          // 白色字体
        add(textLabel);
    }

    /**
     * 呼叫开演！
     * @param onGameStart 倒计时全部结束后，要执行的逻辑
     */
    public void playSequence(Runnable onGameStart) {
        state = 0; // 阶段 0：先播动画
        
        Timer animTimer = new Timer(frameDelay, null);
        animTimer.addActionListener(e -> {
            currentFrame++;
            if(triggerSFX==currentFrame&&sfxName!=null) {
                SoundManager.playSFX(sfxName);
            }
            if (currentFrame >= frames.size()) {
                animTimer.stop();
                startCountdown(onGameStart); // 动画播完了，立刻无缝进入倒计时！
            }
            repaint();
        });
        animTimer.start();
    }

    // 阶段 1：倒计时
    private void startCountdown(Runnable onGameStart) {
        state = 1; 
        textLabel.setText(countdownTexts[0]); // 出现 "准备？"
        repaint(); // 刷新一下，画出半透明黑底

        Timer countdownTimer = new Timer(800, null); // 每 0.8秒 跳一个字
        countdownTimer.addActionListener(e -> {
            countdownIndex++;
            if (countdownIndex < countdownTexts.length) {
                textLabel.setText(countdownTexts[countdownIndex]);
                
            } else {
                // 倒计时全部结束！
                countdownTimer.stop();
                if (onGameStart != null) {
                    onGameStart.run(); // 执行开始游戏的指令！
                }
            }
        });
        countdownTimer.start();
    }

    // ==========================================
    // 直接跳过幕布动画，只播放倒计时
    public void playCountdownOnly(Runnable onGameStart) {
        // 直接进入状态 1 (半透明黑屏)，并调用倒计时逻辑
        startCountdown(onGameStart); 
    }
    public void applySFX(int startcount,String sfxName) {
        triggerSFX=startcount;
        this.sfxName=sfxName;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        if (state == 0) {
            // 阶段0：绘制开场序列帧动画
            if (currentFrame < frames.size()) {
                g.drawImage(frames.get(currentFrame), 0, 0, getWidth(), getHeight(), this);
            }
        } else if (state == 1) {
            // 阶段1：画一层半透明的黑色遮罩，烘托倒计时的紧张感
            Graphics2D g2d = (Graphics2D) g;
            g2d.setColor(new Color(0, 0, 0, 150));
            g2d.fillRect(0, 0, getWidth(), getHeight());
        }
    }
}