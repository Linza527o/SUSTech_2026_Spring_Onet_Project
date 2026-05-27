package ui;

import utils.FontManager;
import javax.swing.*;
import java.awt.*;

public class TimerPanel extends JPanel {

    private JButton timeButton;
    private Timer timer;
    private int secondsPassed = 0;



    public TimerPanel(String bgImagePath) {
        setOpaque(false);
        setLayout(new BorderLayout());

        // 读取背景图片


        // 初始化 JButton，同时传入初始文字和图标
        timeButton = new JButton("时间: 00:00");
        timeButton.setIcon(new ImageIcon("resource/images/btn_bg.png"));

        timeButton.setHorizontalTextPosition(JButton.CENTER);
        timeButton.setVerticalTextPosition(JButton.CENTER);

        timeButton.setContentAreaFilled(false);
        timeButton.setBorderPainted(false);
        timeButton.setFocusPainted(false);
        timeButton.setBounds(1030, 180, 200, 50);
        timeButton.setFont(FontManager.pixelFontSubTitle);
        timeButton.setForeground(Color.WHITE);

        add(timeButton, BorderLayout.CENTER);

        // 创建一个每 1000 毫秒（1秒）触发一次的定时器
        timer = new Timer(1000, e -> {
            secondsPassed++;
            updateDisplay();
        });
    }
    public JButton getTimeButton() {
        return timeButton;
    }
    // 格式化时间并更新显示
    private void updateDisplay() {
        int minutes = secondsPassed / 60;
        int seconds = secondsPassed % 60;
        // 【修改】更新 timeButton 的文字
        timeButton.setText(String.format("时间: %02d:%02d", minutes, seconds));
    }

    // ========== 供外部调用的控制方法 ==========

    public void startTimer() {
        if (!timer.isRunning()) {
            timer.start();
        }
    }

    public void stopTimer() {
        if (timer.isRunning()) {
            timer.stop();
        }
    }

    public void resetTimer() {
        timer.stop();
        secondsPassed = 0;
        updateDisplay();
    }

    public int getSecondsPassed() {
        return secondsPassed;
    }

    public void setSecondsPassed(int seconds){
        this.secondsPassed=seconds;
        updateDisplay();
    }
}