package ui;

import utils.FontManager;
import javax.swing.*;
import java.awt.*;

public class ScoreCounter extends JPanel {

    // 设为 private 保护组件，外部只能通过方法修改它
    private JButton accumulateButton = new JButton();
    private int accumulator = 0;

    // 【核心修复】：构造函数，在创建这个类时只执行一次初始化
    public ScoreCounter() {
        // 1. 设置 ScoreCounter 面板自身透明，并且使用 BorderLayout 让内部按钮填满面板
        this.setOpaque(false);
        this.setLayout(new BorderLayout());

        // 2. 初始化按钮外观
        accumulateButton.setFont(FontManager.pixelFontSubTitle);
        accumulateButton.setForeground(Color.WHITE);
        accumulateButton.setIcon(new ImageIcon("resource/images/btn_bg.png"));
        accumulateButton.setHorizontalTextPosition(JButton.CENTER);
        accumulateButton.setVerticalTextPosition(JButton.CENTER);
        accumulateButton.setContentAreaFilled(false);
        accumulateButton.setBorderPainted(false);
        accumulateButton.setFocusPainted(false);

        // 3. 将按钮添加到当前这个 JPanel 上
        this.add(accumulateButton, BorderLayout.CENTER);

        // 4. 【关键】：立刻显示初始分数（0分）
        updateDisplay();
    }

    public void resetAccumulator() {
        accumulator = 0;
        updateDisplay(); // 重置后别忘了刷新显示
    }

    public void addScore(int score) {
        accumulator += score;
        updateDisplay();
        System.out.println("accumulator: " + accumulator);
    }
    public void setScore(int score) {
        accumulator = score;
        updateDisplay();
        System.out.println("setscore: " + accumulator);
    }

    public JButton getAccumulator() {
        return accumulateButton;
    }
    public int getScore(){
        return accumulator;
    }

    // 内部方法：专门负责刷新按钮上的文字
    private void updateDisplay(){
        accumulateButton.setText("得分：" + accumulator);
        this.repaint();
    }
}