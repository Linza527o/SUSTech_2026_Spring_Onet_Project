package ui;

import javax.swing.*;

import utils.FontManager;
import utils.SoundManager;

import java.awt.*;
import java.awt.event.*;

public class StartFrame extends JFrame {

    //使窗口可以被拖动
    private int mouseX, mouseY;

    
    public StartFrame() {
        // 1. 基础设置：600x500，无边框，居中
        setSize(600, 500);
        setUndecorated(true); // 去掉默认的系统边框（为了像素风纯粹性）
        setLocationRelativeTo(null); // 窗口在屏幕居中
        setLayout(null); // 绝对布局，方便我们根据像素坐标精确摆放控件
        getContentPane().setBackground(new Color(40, 40, 40)); // 临时给个深灰背景，等你画好图换掉
        // 3. 初始化并添加各种控件
        // 1. 监听鼠标“按下”的动作
        this.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                // e.getX() 和 e.getY() 获取的是鼠标相对于当前窗口左上角的坐标
                mouseX = e.getX(); 
                mouseY = e.getY();
            }
        });

        // 2. 监听鼠标“拖拽”的动作
        this.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                // 获取窗口目前在电脑屏幕上的坐标
                int currentWindowX = getLocation().x;
                int currentWindowY = getLocation().y;

                // 计算新位置：当前窗口位置 + 鼠标移动的偏移量 (当前鼠标位置 - 初始鼠标位置)
                int newWindowX = currentWindowX + e.getX() - mouseX;
                int newWindowY = currentWindowY + e.getY() - mouseY;

                // 设置窗口移动到新的位置
                setLocation(newWindowX, newWindowY);
            }
        });

        initComponents();
    }
    //创建初始界面有登录、注册、游客模式三个按钮，分别对应LoginFrame, RegisterFrame和直接进入游戏
    public void initComponents(){
        // === 标题 ===
        JLabel titleLabel = new JLabel("Welcome!");
        titleLabel.setFont(FontManager.pixelFontTitle);
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setBounds(220, 50, 200, 50);
        add(titleLabel);

        JButton loginButton = new JButton("登录");
        loginButton.setFont(FontManager.pixelFontSubTitle); 
        loginButton.setBounds(200, 180, 200, 50);
         // 等你画好图后，在这里替换背景图：
         // loginButton.setIcon(new ImageIcon("resource/btn_login.png"));
         // loginButton.setPressedIcon(new ImageIcon("resource/btn_login_pressed.png"));
        add(loginButton);

        JButton registerButton = new JButton("注册");
        registerButton.setFont(FontManager.pixelFontSubTitle);  
        registerButton.setBounds(200, 250, 200, 50);
        add(registerButton);

        JButton guestButton = new JButton("游客模式");
        guestButton.setFont(FontManager.pixelFontSubTitle);
        guestButton.setBounds(200, 320, 200, 50);
        add(guestButton);

        JButton closeBtn = new JButton();
         ImageIcon closeIcon = new ImageIcon("resource/images/btn_close1.png"); // 
        closeBtn.setIcon(closeIcon);

        closeBtn.setContentAreaFilled(false);
        closeBtn.setBorderPainted(false);
        closeBtn.setFocusPainted(false); // 去掉点击后的虚线框
        closeBtn.setBounds(550, 10, closeIcon.getIconWidth(), closeIcon.getIconHeight());
        add(closeBtn);

        loginButton.addActionListener(e -> {
            SoundManager.playSFX("mouse");
            new LoginFrame().setVisible(true);
        });
        registerButton.addActionListener(e -> {
            SoundManager.playSFX("mouse");
            new RegisterFrame().setVisible(true);
        });
        guestButton.addActionListener(e -> {
            SoundManager.playSFX("mouse");
            int guestId= (int)(Math.random()*100000); // 生成一个随机的游客ID
            String guestUsername = "游客" + guestId;
            boolean isCreated = manager.AccountManager.register(guestUsername, ""); 
            if (isCreated) {
                manager.AccountInfo guestInfo = manager.AccountManager.login(guestUsername, ""); // 直接登录这个游客账号
                guestInfo.setIsGuest(true); // 标记为游客账号
                new InitialFrame(guestInfo).setVisible(true); // 传入游客信息
            } else {
                while(!isCreated) { // 如果生成的游客ID已存在，继续生成新的ID直到成功
                    guestId = (int)(Math.random()*100000);
                    guestUsername = "游客" + guestId;
                    isCreated = manager.AccountManager.register(guestUsername, "000000");
                }
                manager.AccountInfo guestInfo = manager.AccountManager.login(guestUsername, ""); // 直接登录这个游客账号
                guestInfo.setIsGuest(true); // 标记为游客账号
                new InitialFrame(guestInfo).setVisible(true); // 传入游客信息
            }
            this.dispose();
        });
        closeBtn.addActionListener(e -> {
            SoundManager.playSFX("mouse");
            this.dispose(); // 关闭当前界面
        });
    }
}
