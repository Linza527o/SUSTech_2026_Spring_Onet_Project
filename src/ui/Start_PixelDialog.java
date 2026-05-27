package ui;

import javax.swing.*;

import utils.FontManager;
import utils.SoundManager;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;

public class Start_PixelDialog extends JDialog {

    private int mouseX, mouseY;

    /**
     * 自定义像素风弹窗
     * @param parent  依附的父窗口（LoginFrame 或 RegisterFrame）
     * @param message 要提示的文字内容
     */
    public Start_PixelDialog(JFrame parent, String message) {
        // super(parent, true) 这里的 true 表示它是“模态”的（阻塞父窗口）
        super(parent, true); 

        setSize(400, 200);
        setUndecorated(true); // 去除系统边框
        setLocationRelativeTo(parent); // 在父窗口正中间弹出
        setLayout(null);
        getContentPane().setBackground(new Color(40, 40, 40)); // 深色背景

        // 给弹窗加一个白色的像素边框，看起来更像复古游戏的对话框
        ((JPanel) getContentPane()).setBorder(BorderFactory.createLineBorder(Color.WHITE, 3));

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
        add(msgLabel);

        // === 确定按钮 ===
        JButton okBtn = new JButton("确 定");
        okBtn.setFont(FontManager.pixelFontSubTitle);
         // 等你画好图后，在这里替换背景图：
         // okBtn.setIcon(new ImageIcon("resource/btn_ok.png"));
         // okBtn.setPressedIcon(new ImageIcon("resource/btn_ok_pressed.png"));
        okBtn.setBounds(140, 120, 120, 40);
        okBtn.setFocusPainted(false);
        // 点击确定按钮，关闭这个弹窗
        okBtn.addActionListener(e -> {
            SoundManager.playSFX("mouse");
            this.dispose();
        });
        add(okBtn);
    }
}
