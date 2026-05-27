package ui;

import javax.swing.*;

import utils.FontManager;
import utils.SoundManager;

import java.awt.*;

public class PixelDialog_Yes_No {
    public static boolean showDialog(Component parent, String message,Cell cells) {
        // 创建一个新的 JDialog 作为对话框
        JDialog dialog = new JDialog((JFrame) parent, true); // true 表示这是一个模态对话框，会阻塞父窗口
        
        dialog.setUndecorated(true); // 去掉标题栏和边框
        dialog.setSize(400, 200);
        dialog.setLocationRelativeTo(parent); // 在父组件中心显示
        dialog.setLayout(null);

        BackgroundPanel bgPanel = new BackgroundPanel("resource/images/dialog_bg.png",cells);
        dialog.setContentPane(bgPanel);

        // 显示消息的 JLabel
        JLabel messageLabel = new JLabel(message);
        messageLabel.setFont(FontManager.pixelFontBody);
        messageLabel.setForeground(Color.WHITE);
        messageLabel.setBounds(50, 30, 300, 30);
        bgPanel.add(messageLabel);

        // “是”按钮
        JButton yesButton = new JButton("是");
        ImageIcon yesIcon = new ImageIcon("resource/images/dialog_btn.png"); 
        yesButton.setFont(FontManager.pixelFontSubTitle);
        yesButton.setForeground(Color.WHITE);
        yesButton.setIcon(yesIcon);
        yesButton.setHorizontalTextPosition(JButton.CENTER);
        yesButton.setVerticalTextPosition(JButton.CENTER);
        yesButton.setBounds(80, 100, yesIcon.getIconWidth(), yesIcon.getIconHeight());
        bgPanel.add(yesButton);

        // “否”按钮
        JButton noButton = new JButton("否");
        ImageIcon noIcon = new ImageIcon("resource/images/dialog_btn.png"); 
        noButton.setIcon(noIcon);
        noButton.setHorizontalTextPosition(JButton.CENTER);
        noButton.setVerticalTextPosition(JButton.CENTER);
        noButton.setFont(FontManager.pixelFontSubTitle);
        noButton.setForeground(Color.WHITE);
        noButton.setBounds(220, 100, noIcon.getIconWidth(), noIcon.getIconHeight());
        bgPanel.add(noButton);

        final boolean[] result = {false};

        yesButton.addActionListener(e -> {
            SoundManager.playSFX("confirm");
            result[0] = true; 
            dialog.dispose(); 
        });

        noButton.addActionListener(e -> {
            SoundManager.playSFX("confirm");
            result[0] = false; 
            dialog.dispose(); 
        });

        dialog.setVisible(true); 

        return result[0]; // 返回用户的选择
    }
}
