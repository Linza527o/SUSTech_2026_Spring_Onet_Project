package utils;

import javax.swing.*;
import java.awt.*;

public class TransitionManager {

    /**
     * 渐隐旧画面和音乐，完毕后执行跳转逻辑
     * @param currentFrame 当前要关闭的窗口
     * @param durationMs   渐隐耗时 (毫秒)
     * @param onComplete   全黑之后要执行的代码 (比如打开新窗口)
     */
    private static class FadePanel extends JPanel {
        float alpha = 0f; // 透明度：0(全透) 到 1(全黑)
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;
            g2d.setColor(new Color(0f, 0f, 0f, alpha));
            g2d.fillRect(0, 0, getWidth(), getHeight());
        }
        public void setAlpha(float a) {
            this.alpha = Math.max(0f, Math.min(1f, a));
            repaint();
        }
    }
    public static void fadeOutAndSwitch(JFrame currentFrame, int durationMs, Runnable onComplete) {
        // 1. 触发音乐渐隐
        SoundManager.fadeOutBGM(durationMs-100);

        // 2. 准备一块用来“变黑”的玻璃板
        FadePanel fadePane = new FadePanel();
        float alpha = 0f; // 透明度：0(全透) 到 1(全黑)

        fadePane.setOpaque(false);
        currentFrame.setGlassPane(fadePane);
        fadePane.setVisible(true);
        
        long startTime = System.currentTimeMillis();
        Timer timer = new Timer(30, null);
        timer.addActionListener(e -> {
            long elapsed = System.currentTimeMillis() - startTime;
            if (elapsed >= durationMs) {

                if (onComplete != null) onComplete.run(); 
                timer.stop();
                //延迟100毫秒确保新界面打开后再关闭旧界面，避免过快切换导致的闪烁问题
                new Timer(100, ev -> {

                currentFrame.dispose(); // 关闭旧窗口
                }).start();
            } else {
                // 计算进度并变黑
                fadePane.setAlpha((float) elapsed / durationMs);
            }
        });
        timer.start();
    }

    /**
     * 新画面打开时，从全黑逐渐变亮 (淡入)
     */
    public static void fadeIn(JFrame newFrame, int durationMs) {
        FadePanel fadePane = new FadePanel();
        fadePane.setAlpha(1f); // 从全黑开始
        fadePane.setOpaque(false);
        newFrame.setGlassPane(fadePane);
        fadePane.setVisible(true);

        long startTime = System.currentTimeMillis();
        Timer timer = new Timer(30, null);
        timer.addActionListener(e -> {
            long elapsed = System.currentTimeMillis() - startTime;
            if (elapsed >= durationMs) {
                timer.stop();
                fadePane.setVisible(false); // 变亮结束，撤掉遮罩
            } else {
                fadePane.setAlpha(1f - ((float) elapsed / durationMs));
            }
        });
        timer.start();
    }
}


