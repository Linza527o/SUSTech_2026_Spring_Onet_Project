package ui;

import javax.swing.*;

import manager.AccountInfo;
import utils.FontManager;
import utils.SoundManager;

import java.awt.*;
import java.awt.event.*;

public class RegisterFrame extends JFrame {


    public RegisterFrame() {
        setSize(600, 500);
        setUndecorated(true);
        setLocationRelativeTo(null);
        setLayout(null);
        getContentPane().setBackground(new Color(40, 40, 40));

        initComponents();
    }

    private void initComponents() {
        // === 标题 ===
        JLabel titleLabel = new JLabel("注 册 账 号");
        titleLabel.setFont(FontManager.pixelFontTitle);
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setBounds(210, 40, 220, 50);
        add(titleLabel);

        // === 账号部分 ===
        JLabel userLabel = new JLabel("新建账号：");
        userLabel.setFont(FontManager.pixelFontSubTitle);
        userLabel.setForeground(Color.WHITE);
        userLabel.setBounds(80, 120, 200, 40);
        add(userLabel);

        JTextField userField = new JTextField();
        userField.setFont(FontManager.pixelFontBody);
        userField.setBounds(280, 120, 250, 40);
        add(userField);

        // === 密码部分 ===
        JLabel pwdLabel = new JLabel("密 码：");
        pwdLabel.setFont(FontManager.pixelFontSubTitle);
        pwdLabel.setForeground(Color.WHITE);
        pwdLabel.setBounds(80, 190, 200, 40);
        add(pwdLabel);

        JPasswordField pwdField1 = new JPasswordField();
        pwdField1.setFont(FontManager.pixelFontBody);
        pwdField1.setBounds(280, 190, 250, 40);
        add(pwdField1);

        // === 确认密码部分 (多加一个密码框) ===
        JLabel confirmPwdLabel = new JLabel("确认密码：");
        confirmPwdLabel.setFont(FontManager.pixelFontSubTitle);
        confirmPwdLabel.setForeground(Color.WHITE);
        confirmPwdLabel.setBounds(80, 260, 200, 40);
        add(confirmPwdLabel);

        JPasswordField pwdField2 = new JPasswordField();
        pwdField2.setFont(FontManager.pixelFontBody);
        pwdField2.setBounds(280, 260, 250, 40);
        add(pwdField2);
        
        // === 注册按钮 ===
        JButton registerBtn = new JButton("立即注册");
        registerBtn.setFont(FontManager.pixelFontSubTitle);
        registerBtn.setBounds(230, 360, 140, 50);
        add(registerBtn);

        // === 自定义关闭按钮 ===
        JButton closeBtn = new JButton();

        add(closeBtn); 
        ImageIcon closeIcon = new ImageIcon("resource/images/btn_close1.png"); 
        closeBtn.setIcon(closeIcon);

        closeBtn.setContentAreaFilled(false);
        closeBtn.setBorderPainted(false);
        closeBtn.setFocusPainted(false); // 去掉点击后的虚线框
        closeBtn.setBounds(550, 10, closeIcon.getIconWidth(), closeIcon.getIconHeight());

        // ============================
        // --- 为所有输入框添加“点击后清空错误提示”的监听器 ---

        // 1. 账号输入框
        userField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (userField.getText().equals("账号不能为空！") || userField.getText().equals("该账号已被占用！")) {
                    userField.setText("");
                }
            }
        });

        // 2. 第一个密码框
        char defaultEchoChar1 = pwdField1.getEchoChar(); // 保存原始的小黑点字符
        pwdField1.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                String currentText = new String(pwdField1.getPassword());
                if (currentText.equals("密码不能为空！") || currentText.equals("两次密码不一致！")) {
                    pwdField1.setText("");
                    pwdField1.setEchoChar(defaultEchoChar1); // 恢复小黑点显示
                }
            }
        });

        // 3. 第二个确认密码框
        char defaultEchoChar2 = pwdField2.getEchoChar();
        pwdField2.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                String currentText = new String(pwdField2.getPassword());
                if (currentText.equals("密码不能为空！") || currentText.equals("两次密码不一致！")) {
                    pwdField2.setText("");
                    pwdField2.setEchoChar(defaultEchoChar2);
                }
            }
        });


        // --- 【核心】注册按钮的点击事件 ---
        registerBtn.addActionListener(e -> {
            SoundManager.playSFX("mouse");
            
            // 获取三个输入框的内容
            String account = userField.getText();
            String password = new String(pwdField1.getPassword());
            String confirmPassword = new String(pwdField2.getPassword());
            boolean successEntered = true;

            // --- 步骤1：输入验证 ---

            // 验证账号是否为空
            if (account.isEmpty() || account.equals("账号不能为空！")) {
                userField.setText("账号不能为空！");
                successEntered = false;
            }

            // 验证第一个密码框是否为空
            if (password.isEmpty() || password.equals("密码不能为空！")) {
                pwdField1.setEchoChar('\0'); // 暂时取消小黑点，让提示文字可见
                pwdField1.setText("密码不能为空！");
                successEntered = false;
            }

            // 验证第二个密码框是否为空
            if (confirmPassword.isEmpty() || confirmPassword.equals("密码不能为空！")) {
                pwdField2.setEchoChar('\0');
                pwdField2.setText("密码不能为空！");
                successEntered = false;
            }
            
            // 验证两次密码是否一致
            if (!password.equals(confirmPassword)) {
                // 两个密码框都显示错误
                pwdField1.setEchoChar('\0');
                pwdField1.setText("两次密码不一致！");
                pwdField2.setEchoChar('\0');
                pwdField2.setText("两次密码不一致！");
                successEntered = false;
            }

            // 如果输入验证没通过，就直接返回，不继续注册逻辑了
            if (!successEntered) {
                return;
            }

            // --- 步骤2：调用 AccountManager 进行注册 ---
            boolean success = manager.AccountManager.register(account, password);

            // --- 步骤3：根据结果给出反馈 ---
            if (success) {
                new Start_PixelDialog(this, "注册成功！请返回登录界面登录。").setVisible(true);
                // 注册成功后，关闭自己，显示登录界面
                this.dispose();
            } else {
                // 如果注册失败（账号重名），在账号框给出提示
                userField.setText("该账号已被占用！");
                pwdField1.setText("");
                pwdField1.setEchoChar(defaultEchoChar1);
                pwdField2.setText("");
                pwdField2.setEchoChar(defaultEchoChar2);
            }
        });

        // 关闭按钮
        closeBtn.addActionListener(e -> {
            SoundManager.playSFX("mouse");
            this.dispose();
        });
    }
}