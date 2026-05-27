package ui;

import manager.AccountInfo;
import utils.FontManager;
import utils.SoundManager;
import utils.TransitionManager;

import javax.swing.*;
import java.awt.*;

public class VictoryDialog extends JDialog {

    public VictoryDialog(JFrame parent, AccountInfo currentUser,Cell cells,int totalScore,boolean applyGravity, int diff, boolean status) {
        // super(parent, true) -> 开启模态，锁死背后的游戏界面
        super(parent, true);

        // 【画布大小】500x400
        setSize(500, 400);
        setUndecorated(true);
        setLocationRelativeTo(parent); // 在父窗口（GameFrame）正中间

        // 使用带背景图的画板作为底座
        BackgroundPanel bgPanel = new BackgroundPanel("resource/images/panel_bg.png",cells);
        bgPanel.setLayout(null); // 绝对布局
        this.setContentPane(bgPanel);

        initComponents(currentUser,totalScore,applyGravity,diff,status);
    }

    private void initComponents(AccountInfo currentUser,int totalScore,boolean applyGravity, int diff,boolean status) {
        // "恭喜通关！" 标题

        String NOTE = new String();
        if(status) NOTE = "恭喜通关";
        if(!status) NOTE = "游戏结束";
        JLabel titleLabel = new JLabel(NOTE, SwingConstants.CENTER);
        titleLabel.setFont(FontManager.pixelFontTitle);
        titleLabel.setForeground(Color.YELLOW);
        titleLabel.setBounds(0, 80, 500, 50);
        this.add(titleLabel);
        // 得分详情
        JLabel scoreLabel = new JLabel("",SwingConstants.CENTER);
        scoreLabel.setText("当前得分: " + totalScore);
        scoreLabel.setFont(FontManager.pixelFontTitle);
        scoreLabel.setForeground(Color.YELLOW);
        scoreLabel.setBounds(0, 160, 500, 50);
        this.add(scoreLabel);
        // "继续" 按钮 (逻辑：重新开始一局游戏)
        JButton continueButton = new JButton("继 续");
        styleButton(continueButton); // 应用统一的按钮样式
        continueButton.setBounds(150, 200, 200, 50);
        this.add(continueButton);

        // "返回主菜单" 按钮
        JButton backButton = new JButton("返回主菜单");
        styleButton(backButton);
        backButton.setBounds(150, 280, 200, 50);
        this.add(backButton);

        // --- 按钮点击事件 ---
        continueButton.addActionListener(e -> {
            SoundManager.playSFX("confirm");
            // 找到父窗口 (GameFrame)
            JFrame parentFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
            
            // 使用我们之前写的转场魔法！
            TransitionManager.fadeOutAndSwitch(parentFrame, 1000, () -> {
                // 等待渐隐结束后，打开一个新的游戏界
                GameFrame newGame = new GameFrame(currentUser,false,applyGravity, diff);
                TransitionManager.fadeIn(newGame, 1000);
            });
            this.dispose(); // 关闭自己
        });

        backButton.addActionListener(e -> {
            SoundManager.playSFX("confirm");
            JFrame parentFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
            
            TransitionManager.fadeOutAndSwitch(parentFrame, 1000, () -> {
                InitialFrame initialFrame = new InitialFrame(currentUser);
                TransitionManager.fadeIn(initialFrame, 1000);
                initialFrame.setVisible(true);
            });
            this.dispose();
        });
    }

    // 抽取一个方法来统一设置按钮样式，避免代码重复
    private void styleButton(JButton button) {
        button.setFont(FontManager.pixelFontSubTitle);
        button.setForeground(Color.WHITE);
        button.setIcon(new ImageIcon("resource/images/btn_bg.png"));
        button.setHorizontalTextPosition(JButton.CENTER);
        button.setVerticalTextPosition(JButton.CENTER);
        button.setContentAreaFilled(false);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
    }
}