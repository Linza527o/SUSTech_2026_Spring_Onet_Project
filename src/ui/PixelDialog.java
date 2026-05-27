package ui;

import javax.swing.*;

import utils.FontManager;
import utils.SoundManager;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;

public class PixelDialog extends JDialog {

    private int mouseX, mouseY;

    /**
     * 自定义像素风弹窗
     * @param parent  依附的父窗口（LoginFrame 或 RegisterFrame）
     * @param message 要提示的文字内容
     */
    public PixelDialog(JFrame parent, String message) {
        // super(parent, true) 这里的 true 表示它是“模态”的（阻塞父窗口）
        super(parent, true); 

        setSize(400, 200);
        setUndecorated(true); // 去除系统边框
        setLocationRelativeTo(parent); // 在父窗口正中间弹出
        setLayout(null);
        
        BackgroundPanel bgPanel=new BackgroundPanel("resource/images/dialog_bg.png", null);
        this.setContentPane(bgPanel);


        // === 实现拖拽功能 ===
        addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                mouseX = e.getX();
                mouseY = e.getY();
            }
        });
        addMouseMotionListener(new MouseMotionAdapter() {
            public void mouseDragged(MouseEvent e) {
                setLocation(getLocation().x + e.getX() - mouseX, getLocation().y + e.getY() - mouseY);
            }
        });

        // === 提示文字 ===
        // 使用 HTML 标签让文字可以自动换行，并且居中对齐
        JLabel msgLabel = new JLabel("<html><div style='text-align: center;'>" + message + "</div></html>", SwingConstants.CENTER);
        msgLabel.setFont(FontManager.pixelFontBody);
        msgLabel.setForeground(Color.WHITE);
        msgLabel.setBounds(20, 40, 360, 60);
        bgPanel.add(msgLabel);

        // === 确定按钮 ===
        JButton okBtn = new JButton("确 定");
        okBtn.setFont(FontManager.pixelFontSubTitle);
        ImageIcon okIcon = new ImageIcon("resource/images/dialog_btn.png");
        okBtn.setIcon(okIcon);
        okBtn.setForeground(Color.WHITE);
        okBtn.setHorizontalTextPosition(JButton.CENTER);
        okBtn.setVerticalTextPosition(JButton.CENTER);
         // okBtn.setPressedIcon(new ImageIcon("resource/btn_ok_pressed.png"));
        okBtn.setBounds(140, 120, okIcon.getIconWidth(), okIcon.getIconHeight());
        okBtn.setFocusPainted(false);
        // 点击确定按钮，关闭这个弹窗
        okBtn.addActionListener(e -> {
            SoundManager.playSFX("mouse");
            this.dispose();
        });
        bgPanel.add(okBtn);
    }
}
