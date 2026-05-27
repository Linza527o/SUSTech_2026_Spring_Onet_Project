package ui;

import javax.swing.*;

import utils.FontManager;
import utils.SoundManager;

import java.awt.*;
import java.awt.event.*;

public class LoginFrame extends JFrame {
    
    public LoginFrame() {
        // 1. 基础设置：600x500，无边框，居中
        setSize(600, 500);
        setUndecorated(true); // 去掉默认的系统边框（为了像素风纯粹性）
        setLocationRelativeTo(null); // 窗口在屏幕居中
        setLayout(null); // 绝对布局，方便我们根据像素坐标精确摆放控件
        getContentPane().setBackground(new Color(40, 40, 40)); // 临时给个深灰背景，等你画好图换掉

        // 3. 初始化并添加各种控件
        initComponents();
    }

    private void initComponents() {
        // === 标题 ===
        JLabel titleLabel = new JLabel("连 连 看");
        titleLabel.setFont(FontManager.pixelFontTitle);
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setBounds(230, 50, 200, 50);
        add(titleLabel);

        // === 账号部分 ===
        JLabel userLabel = new JLabel("账号：");
        userLabel.setFont(FontManager.pixelFontSubTitle);
        userLabel.setForeground(Color.WHITE);
        userLabel.setBounds(100, 150, 100, 40);
        add(userLabel);

        JTextField userField = new JTextField();
        userField.setFont(FontManager.pixelFontBody);
        userField.setBounds(180, 150, 250, 40);
        add(userField);

        // === 密码部分 ===
        JLabel pwdLabel = new JLabel("密码：");
        pwdLabel.setFont(FontManager.pixelFontSubTitle);
        pwdLabel.setForeground(Color.WHITE);
        pwdLabel.setBounds(100, 220, 100, 40);
        add(pwdLabel);

        JPasswordField pwdField = new JPasswordField(); // 密码框，自动把输入变成小黑点
        pwdField.setFont(FontManager.pixelFontBody);
        pwdField.setBounds(180, 220, 250, 40);
        add(pwdField);

        // === 错误提示 Label (平常隐藏) ===
        JLabel errorLabel = new JLabel("账号或密码不能为空！");
        errorLabel.setFont(FontManager.pixelFontBody);
        errorLabel.setForeground(Color.WHITE);
        errorLabel.setBounds(180, 270, 300, 30);
        errorLabel.setVisible(false); // 默认隐藏
        add(errorLabel);

        // === 登录按钮 ===
        JButton loginBtn = new JButton("登录");
        loginBtn.setFont(FontManager.pixelFontSubTitle);
        loginBtn.setBounds(245, 320, 110, 45);
        add(loginBtn);

        // === 自定义红叉关闭按钮 ===
        JButton closeBtn = new JButton();
        add(closeBtn);

        ImageIcon closeIcon = new ImageIcon("resource/images/btn_close1.png"); 
        closeBtn.setIcon(closeIcon);

        closeBtn.setContentAreaFilled(false);
        closeBtn.setBorderPainted(false);
        closeBtn.setFocusPainted(false); // 去掉点击后的虚线框
        closeBtn.setBounds(550, 10, closeIcon.getIconWidth(), closeIcon.getIconHeight());

        // ============================
        // 4. 交互逻辑 (事件监听器)
        // ============================

        // 保存一下最开始的小黑点是什么符号，方便后面恢复
        char defaultChar = pwdField.getEchoChar();

        pwdField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                String currentText = new String(pwdField.getPassword());
                if (currentText.equals("密码不能为空！")) {
                    pwdField.setText("");             // 清空错误提示文本
                    pwdField.setEchoChar(defaultChar); // 恢复原来的小黑点遮罩
                }
            }
        });
        userField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (userField.getText().equals("账号不能为空！")) {
                    userField.setText(""); // 清空错误提示文本
                }
            }
        });

        // 登录按钮点击事件
        loginBtn.addActionListener(e -> {
            SoundManager.playSFX("mouse");
            String account = userField.getText();
            String password = new String(pwdField.getPassword());
            boolean success = true;

            // 判空
            if (account.isEmpty() || account.equals("账号不能为空！")) {
                userField.setText("账号不能为空！");
                success = false;
            }
            if (password.isEmpty() || password.equals("密码不能为空！")) {
                pwdField.setEchoChar('\0');
                pwdField.setText("密码不能为空！");
                success = false;
            }
            if (!success) {
                return; // 如果有输入错误，直接返回，不继续验证账号密码了
            }

            // 读AccountManager比对信息，验证账号密码是否正确
            manager.AccountInfo currentUser = manager.AccountManager.login(account, password);
            if (currentUser != null) {
                // 验证成功，进入游戏界面
                new InitialFrame(currentUser).setVisible(true);
                this.dispose(); // 关闭登录界面
                //关闭StartFrame界面
                for (Frame frame : Frame.getFrames()) {
                    if (frame instanceof StartFrame) {
                        frame.dispose();
                    }
                }
            } else {
                // 验证失败，显示错误提示
                errorLabel.setText("账号或密码错误！");
                errorLabel.setVisible(true);
                return;
            }
        });

        // 关闭按钮逻辑
        closeBtn.addActionListener(e1 -> {
            SoundManager.playSFX("mouse");
            this.dispose();
        });
    }
}