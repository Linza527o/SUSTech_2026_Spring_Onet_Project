package ui;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class AnimatedBackgroundPanel extends JPanel {
    private List<Image> frames = new ArrayList<>();
    private int currentFrame = 0;
    private Timer timer;

    public AnimatedBackgroundPanel(String prefix, int frameCount, int delay) {
        setLayout(null);

        for (int i = 1; i <= frameCount; i++) {
            frames.add(new ImageIcon("resource/images/" + prefix + "_" + i + ".png").getImage());
        }

        // 创建定时器：每隔 delay 毫秒切换一帧
        timer = new Timer(delay, e -> {
            currentFrame = (currentFrame + 1) % frames.size();
            repaint(); // 关键：通知界面重新绘图
        });
        timer.start();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (frames.size() > 0) {
            // 将当前帧画满整个面板
            g.drawImage(frames.get(currentFrame), 0, 0, getWidth(), getHeight(), this);
        }
    }
    public void stopAnimation() {
        timer.stop();
    }
}