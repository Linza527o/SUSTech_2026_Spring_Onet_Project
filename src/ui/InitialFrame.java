package ui;

import javax.swing.*;

import manager.GameSaveManager;
import utils.FontManager;
import utils.SoundManager;
import utils.TransitionManager;

import java.awt.*;

public class InitialFrame extends JFrame {
    Cell cells = new Cell(320, 40, 64);
    private manager.AccountInfo currentUser;

    public InitialFrame(manager.AccountInfo currentUser) {
        this.currentUser = currentUser;
        // 1. 基础设置：1280x720，无边框，居中
        setSize(1280, 720);
        setUndecorated(true); // 去掉默认的系统边框（为了像素风纯粹性）
        setLocationRelativeTo(null); // 窗口在屏幕居中

        SoundManager.playBGM("resource/music/bgm.wav");

        // 1. 创建动画背景 (假设你有 4 帧图片 bg_0.png 到 bg_3.png，每 200 毫秒换一帧)
        AnimatedBackgroundPanel bgPanel = new AnimatedBackgroundPanel("illu", 2, 800);
        this.setContentPane(bgPanel); // 把动画背景设置为内容面板

        initComponents(bgPanel); // 里面有你的按钮

    }


    public void initComponents(AnimatedBackgroundPanel bgPanel){

        JButton startButton = new JButton();
        boolean isNewGamer = true;
        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < 2; j++) {
                if(currentUser.getBestScore(i, j) != 0){
                    isNewGamer = false;
                    break;
                }
            }
        }
        if(!isNewGamer){
            startButton.setText("继续游戏");
        }else{
            startButton.setText("开始游戏");
        }
        startButton.setFont(FontManager.pixelFontSubTitle);

        ImageIcon btnBg = new ImageIcon("resource/images/btn_bg.png");

        startButton.setIcon(btnBg);
        // 核心设置：将文字对齐到图片的中心
        startButton.setHorizontalTextPosition(JButton.CENTER);
        startButton.setVerticalTextPosition(JButton.CENTER);

        // 同样去掉默认边框
        startButton.setContentAreaFilled(false);
        startButton.setBorderPainted(false);
        startButton.setFocusPainted(false); // 去掉点击后的虚线框
        startButton.setFont(FontManager.pixelFontSubTitle);
        startButton.setForeground(Color.WHITE);

        startButton.setBounds(540, 400, btnBg.getIconWidth(), btnBg.getIconHeight());
        bgPanel.add(startButton);

        JButton exitButton = new JButton("退出游戏");

        exitButton.setIcon(btnBg);
        exitButton.setHorizontalTextPosition(JButton.CENTER);
        exitButton.setVerticalTextPosition(JButton.CENTER);
        exitButton.setContentAreaFilled(false);
        exitButton.setBorderPainted(false);
        exitButton.setFocusPainted(false);
        exitButton.setFont(FontManager.pixelFontSubTitle);
        exitButton.setForeground(Color.WHITE);
        exitButton.setBounds(540, 480, btnBg.getIconWidth(), btnBg.getIconHeight());
        bgPanel.add(exitButton);

        JButton settingsButton = new JButton("设置");
        settingsButton.setIcon(btnBg);
        settingsButton.setHorizontalTextPosition(JButton.CENTER);
        settingsButton.setVerticalTextPosition(JButton.CENTER);
        settingsButton.setContentAreaFilled(false);
        settingsButton.setBorderPainted(false);
        settingsButton.setFocusPainted(false);
        settingsButton.setFont(FontManager.pixelFontSubTitle);
        settingsButton.setForeground(Color.WHITE);
        settingsButton.setBounds(540, 560, btnBg.getIconWidth(), btnBg.getIconHeight());
        bgPanel.add(settingsButton);

        startButton.addActionListener(e -> {
            SoundManager.playSFX("confirm");
            //打开连连看游戏页面
            //停止播放bgm
            TransitionManager.fadeOutAndSwitch(this, 1000, () -> {
                //渐出，进入游戏界面
                MapFrame mapFrame = new MapFrame(currentUser);
                TransitionManager.fadeIn(mapFrame, 1000);
                mapFrame.setVisible(true);

            });
        });

        exitButton.addActionListener(e -> {
            SoundManager.playSFX("confirm");
            boolean confirmExit = PixelDialog_Yes_No.showDialog(this, "确定要退出游戏吗？",cells);
            if (confirmExit) {

                //删除游客账户信息
                if(currentUser.getIsGuest()){
                    manager.AccountManager.deleteAccount(currentUser.getAccount());
                    for (int i = 0; i < 2; i++) {
                        for (int j = 0; j < 2; j++) {
                            manager.GameSaveManager.deleteSave(currentUser.getAccount(), i, j);
                            manager.GameSaveManager.deleteInitialState(currentUser.getAccount());
                        }

                    }
                }

                System.exit(0);
            }
        });


        //点击设置按钮执行：
        settingsButton.addActionListener(e -> {
            SoundManager.playSFX("confirm");
            // 1. 定义背景变暗的“保鲜膜”层
            JPanel dimPane = new JPanel() {
                @Override
                protected void paintComponent(Graphics g) {
                    // 这里才是真正实现“半透明黑色”的地方
                    g.setColor(new Color(0, 0, 0, 150));
                    g.fillRect(0, 0, getWidth(), getHeight());
                    super.paintComponent(g);
                }
            };
            dimPane.setOpaque(false); // 必须设为 false 才能看到底下的内容

            // 2. 利用 GlassPane (玻璃层) 把它铺在最顶层
            this.setGlassPane(dimPane);
            dimPane.setVisible(true);

            // 3. 打开弹窗，注意这里一定要传 this！
            SettingsDialog dialog = new SettingsDialog(this, currentUser,cells);
            dialog.setVisible(true);

            // 4. 弹窗关闭后（代码运行到这一行说明弹窗关了），撤掉变暗效果
            dimPane.setVisible(false);
        });
        // 如果是绝对布局，背景要在最后 add，或者设置 Z-Order
    }
}
