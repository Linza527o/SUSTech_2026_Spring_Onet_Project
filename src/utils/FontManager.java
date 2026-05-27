package utils;
import java.awt.Font;
import java.io.File;

import javax.swing.UIManager;

public class FontManager {
    //导入像素风字体，设置三种字体大小：标题（36pt），小标题（24pt),正文（18pt）
    public static Font pixelFontTitle;
    public static Font pixelFontSubTitle;
    public static Font pixelFontBody;

    public static void loadFonts() {
        try {
            Font baseFont = Font.createFont(Font.TRUETYPE_FONT, new File("resource/zpix.ttf"));
            
            pixelFontTitle = baseFont.deriveFont(36f);
            pixelFontSubTitle = baseFont.deriveFont(24f);
            pixelFontBody = baseFont.deriveFont(18f);

            UIManager.put("OptionPane.messageFont", pixelFontBody);
            // 把弹窗上的“确定”、“取消”等按钮也换成像素正文字体
            UIManager.put("OptionPane.buttonFont", pixelFontBody);

        } catch (Exception e) {
            e.printStackTrace();
            // 如果加载失败，使用默认字体（避免程序崩溃）
            pixelFontTitle = new Font("黑体", Font.BOLD, 36);
            pixelFontSubTitle = new Font("黑体", Font.BOLD, 24);
            pixelFontBody = new Font("黑体", Font.PLAIN, 18);
        }
    }
}