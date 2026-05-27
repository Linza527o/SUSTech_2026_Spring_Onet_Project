package ui;

import javax.swing.*;
import manager.AccountInfo;
import utils.FontManager;
import utils.SoundManager;
import utils.TransitionManager;

import java.awt.*;

public class MapFrame extends JFrame {

    private AccountInfo currentUser;
    int selectMode = 0;

    public MapFrame(AccountInfo currentUser) {
        this.currentUser = currentUser;

        // 1. 基础设置：1280x720，无边框，居中
        setSize(1280, 720);
        setUndecorated(true);
        setLocationRelativeTo(null); 

        SoundManager.playBGM("resource/music/map_bgm.wav");

        // 2. 创建动画背景 (4帧动图，main_bg_1 到 main_bg_4)
        AnimatedBackgroundPanel bgPanel = new AnimatedBackgroundPanel("main_bg", 4, 800);
        this.setContentPane(bgPanel); 
        
        initComponents(bgPanel); 
    }
    
    public void initComponents(AnimatedBackgroundPanel bgPanel) {
        ImageIcon btnBg = new ImageIcon("resource/images/btn_bg.png");
        
        // 按钮统一的 X 坐标 (偏右侧，不挡住左边的星球)
        int btnX = 850; 

        JButton repairButton = new JButton("修理飞船");
        styleButton(repairButton, btnBg);
        repairButton.setBounds(btnX, 320, btnBg.getIconWidth(), btnBg.getIconHeight());
        bgPanel.add(repairButton);

        JButton GravityMode = new JButton("重力模式");
        styleButton(GravityMode, btnBg);
        GravityMode.setBounds(btnX, 400, btnBg.getIconWidth(), btnBg.getIconHeight());
        GravityMode.setEnabled(true); //
        bgPanel.add(GravityMode);

        JButton backButton = new JButton("返回菜单");
        styleButton(backButton, btnBg);
        backButton.setBounds(btnX, 560, btnBg.getIconWidth(), btnBg.getIconHeight());
        bgPanel.add(backButton);

        JButton modeButton = new JButton((selectMode == 0? "简单" : "困难") + "模式");
        styleButton(modeButton, btnBg);
        modeButton.setBounds(btnX, 480, btnBg.getIconWidth(), btnBg.getIconHeight());
        bgPanel.add(modeButton);

        // ==========================================
        // 交互逻辑
        // ==========================================
        
        // 点击“修理飞船” -> 去下棋消除
        repairButton.addActionListener(e -> {
            SoundManager.fadeOutBGM(500); // 渐隐当前BGM
            SoundManager.playSFX("confirm");

            bgPanel.stopAnimation();
    
            // 3. 按钮飞出动画 (每 20 毫秒向右移动 30 像素)
            Timer flyOutTimer = new Timer(20, null);
            flyOutTimer.addActionListener(event -> {
  
                repairButton.setLocation(repairButton.getX() + 30, repairButton.getY());
                GravityMode.setLocation(GravityMode.getX() + 30, GravityMode.getY());
                backButton.setLocation(backButton.getX() + 30, backButton.getY());
                modeButton.setLocation(modeButton.getX() + 30,modeButton.getY());
            
            // 当按钮彻底飞出屏幕右侧时 (假设屏幕宽1280)
                if (repairButton.getX() > 1280) {
                    flyOutTimer.stop(); // 停止飞行
                }
            });
            flyOutTimer.start(); // 启动飞行动画
                // 1. 创建 png 转场动画
                OneShotAnimationPanel mapToGame = new OneShotAnimationPanel("map_exit/F", 33, 80); // 建议设为 80ms，稍微流畅一点
                mapToGame.setBounds(0, 0, 1280, 720);
                mapToGame.applySFX(22, "descend"); // 在第 25 帧（大约还剩 8 帧全黑）时播放音效
                // 必须把动画画板添加到当前窗口的最顶层！
                bgPanel.add(mapToGame);
                bgPanel.setComponentZOrder(mapToGame, bgPanel.getComponentCount() - 1); // 让动画面板在最底层

            // 2. 开始播放动画
            mapToGame.play(() -> {
                // 这个大括号里的代码，会严格等到第 33 帧播完，屏幕全黑时才执行！
                
                // 打开新界面
                GameFrame gameFrame = new GameFrame(currentUser,true,false, selectMode);
                gameFrame.setVisible(true);
                //延迟关闭界面
                Timer delayCloseTimer = new Timer(300, ev -> {
                    this.dispose(); // 关闭当前界面
                });
                delayCloseTimer.setRepeats(false); // 只执行一次
                delayCloseTimer.start();
            });
        });
        
        GravityMode.addActionListener(e -> {
            SoundManager.fadeOutBGM(500); // 渐隐当前BGM
            SoundManager.playSFX("confirm");

            bgPanel.stopAnimation();

            // 3. 按钮飞出动画 (每 20 毫秒向右移动 30 像素)
            Timer flyOutTimer = new Timer(20, null);
            flyOutTimer.addActionListener(event -> {
                // 让三个按钮一起往右飞
                repairButton.setLocation(repairButton.getX() + 30, repairButton.getY());
                GravityMode.setLocation(GravityMode.getX() + 30, GravityMode.getY());
                backButton.setLocation(backButton.getX() + 30, backButton.getY());
                modeButton.setLocation(modeButton.getX() + 30,modeButton.getY());


                // 当按钮彻底飞出屏幕右侧时 (假设屏幕宽1280)
                if (repairButton.getX() > 1280) {
                    flyOutTimer.stop(); // 停止飞行
                }
            });
            flyOutTimer.start(); // 启动飞行动画
            // 1. 创建 png 转场动画
            OneShotAnimationPanel mapToGame = new OneShotAnimationPanel("map_exit/F", 33, 80); // 建议设为 80ms，稍微流畅一点
            mapToGame.setBounds(0, 0, 1280, 720);
            mapToGame.applySFX(22, "descend"); // 在第 25 帧（大约还剩 8 帧全黑）时播放音效
            // 必须把动画画板添加到当前窗口的最顶层！
            bgPanel.add(mapToGame);
            bgPanel.setComponentZOrder(mapToGame, bgPanel.getComponentCount() - 1); // 让动画面板在最底层

            // 2. 开始播放动画
            mapToGame.play(() -> {
                // 这个大括号里的代码，会严格等到第 33 帧播完，屏幕全黑时才执行！

                // 打开新界面
                GameFrame gameFrame = new GameFrame(currentUser,true,true, selectMode);

                gameFrame.setApplyGravity(true);
                gameFrame.setVisible(true);
                System.out.println("已设置为重力模式");
                //延迟关闭界面
                Timer delayCloseTimer = new Timer(300, ev -> {
                    this.dispose(); // 关闭当前界面
                });
                delayCloseTimer.setRepeats(false); // 只执行一次
                delayCloseTimer.start();
            });
        });

        modeButton.addActionListener(e ->{
            SoundManager.playSFX("confirm");
            boolean modeSwitch = PixelDialog_Yes_No.showDialog(this, "确定要把难度切换为" + (selectMode == 0? "困难" : "简单") + "吗？", null);
            if(modeSwitch){
                selectMode = (selectMode + 1) % 2;
                modeButton.setText((selectMode == 0? "简单":"困难") + "模式");
            }
        });

        // 点击“返回菜单” -> 回到 InitialFrame 主菜单
        backButton.addActionListener(e -> {
            SoundManager.playSFX("confirm");
            TransitionManager.fadeOutAndSwitch(this, 1000, () -> {
                InitialFrame initialFrame = new InitialFrame(currentUser);
                TransitionManager.fadeIn(initialFrame, 1000); // 新界面淡入
                initialFrame.setVisible(true);
            });
        });
    }

    // --- 统一设置像素按钮外观的工具方法 ---
    private void styleButton(JButton btn, ImageIcon bgIcon) {
        btn.setIcon(bgIcon);
        btn.setFont(FontManager.pixelFontSubTitle);
        btn.setForeground(Color.WHITE);
        btn.setHorizontalTextPosition(JButton.CENTER);
        btn.setVerticalTextPosition(JButton.CENTER);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
    }
}