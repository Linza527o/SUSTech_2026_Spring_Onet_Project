package ui;

import javax.swing.*;
import javax.swing.border.MatteBorder;

import utils.FontManager;
import utils.SoundManager;

import java.awt.*;

// 1. 继承 JDialog 而不是 JFrame
public class SettingsDialog extends JDialog {

    private CardLayout mainCardLayout;
    private JPanel mainCardContainer;
    private manager.AccountInfo currentUser;

    // 【新增】临时记录玩家在小网格里点中了第几个头像
    private int tempSelectedAvatarIndex; 
    // 【新增】存那 6 个小头像按钮，方便我们稍后循环取消它们的白框
    private JButton[] smallAvatarBtns = new JButton[6];

    // 2. 构造函数传入父窗口 (JFrame parent)
    public SettingsDialog(JFrame parent, manager.AccountInfo currentUser,Cell cells) {
        // 3. super(parent, true) 开启模态：不关掉设置，就点不了后面的主界面！
        super(parent, true);
        this.currentUser = currentUser;

        setSize(500, 450);
        setUndecorated(true); // 无边框
        setLocationRelativeTo(parent); // 永远在父窗口正中间弹出
        setLayout(new BorderLayout());
        getContentPane().setBackground(new Color(40, 40, 40));

        BackgroundPanel bg = new BackgroundPanel("resource/images/panel_bg.png",cells);
        bg.setLayout(new BorderLayout());
        this.setContentPane(bg);
        
        initComponents(bg);
    }

    private void initComponents(BackgroundPanel bg) {
        // ==========================================
        // 1. 顶部导航栏 (主选项卡)
        // ==========================================
        JPanel topNavPanel = new JPanel(new GridLayout(1, 2));
        topNavPanel.setPreferredSize(new Dimension(500, 50));
        topNavPanel.setOpaque(false);


        JButton audioTabBtn = new JButton("音量控制");
        audioTabBtn.setFont(FontManager.pixelFontSubTitle);
        audioTabBtn.setFocusPainted(false);
         audioTabBtn.setBackground(new Color(24, 56, 103));
        audioTabBtn.setBackground(new Color(80, 80, 80));
        audioTabBtn.setForeground(Color.WHITE);

        JButton userTabBtn = new JButton("角色信息"); 
        userTabBtn.setFont(FontManager.pixelFontSubTitle);
        userTabBtn.setFocusPainted(false);
        userTabBtn.setBackground(new Color(15, 35, 65)); 
        userTabBtn.setForeground(Color.LIGHT_GRAY);

        topNavPanel.add(audioTabBtn);
        topNavPanel.add(userTabBtn);
        bg.add(topNavPanel, BorderLayout.NORTH);

        // ==========================================
        // 2. 中间的主内容区域 (主卡片容器)
        // ==========================================
        mainCardLayout = new CardLayout();
        mainCardContainer = new JPanel(mainCardLayout);
        mainCardContainer.setOpaque(false);

        mainCardContainer.add(createAudioPanel(), "AUDIO");
        mainCardContainer.add(createUserPanel(), "USER");
        bg.add(mainCardContainer, BorderLayout.CENTER);

        // ==========================================
        // 3. 底部返回按钮
        // ==========================================
        JPanel bottomPanel = new JPanel();
        bottomPanel.setOpaque(false);
        JButton closeBtn = new JButton("返 回");
        closeBtn.setFont(FontManager.pixelFontSubTitle);
        closeBtn.setForeground(Color.WHITE);
        closeBtn.setIcon(new ImageIcon("resource/images/dialog_btn.png"));
        closeBtn.setHorizontalTextPosition(JButton.CENTER);
        closeBtn.setVerticalTextPosition(JButton.CENTER);
        closeBtn.setFocusPainted(false);
        closeBtn.setContentAreaFilled(false);
        closeBtn.setBorderPainted(false);
        // 点击返回时，关闭这个 Dialog
        closeBtn.addActionListener(e -> {
            SoundManager.playSFX("confirm");
            this.dispose();
        });
        bottomPanel.add(closeBtn);
        //bottomPanel在 BorderLayout.SOUTH 稍微偏上一些的位置
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 25, 0));
        bg.add(bottomPanel, BorderLayout.SOUTH);

        // 选项卡切换逻辑
        audioTabBtn.addActionListener(e -> {
            SoundManager.playSFX("confirm");
            mainCardLayout.show(mainCardContainer, "AUDIO");
            audioTabBtn.setBackground(new Color(24, 56, 103));
            audioTabBtn.setForeground(Color.WHITE);
            userTabBtn.setBackground(new Color(15, 35, 65));
            userTabBtn.setForeground(Color.LIGHT_GRAY);
        });

        userTabBtn.addActionListener(e -> {
            SoundManager.playSFX("confirm");
            mainCardLayout.show(mainCardContainer, "USER");
            userTabBtn.setBackground(new Color(24, 56, 103));
            userTabBtn.setForeground(Color.WHITE);
            audioTabBtn.setBackground(new Color(15, 35, 65));
            audioTabBtn.setForeground(Color.LIGHT_GRAY);
        });
    }

    // ==========================================
    // 音量控制界面
    // ==========================================
    private JPanel createAudioPanel() {
        JPanel panel = new JPanel(null);
        panel.setOpaque(false);

        JLabel musicLabel = new JLabel("音 乐");
        musicLabel.setFont(FontManager.pixelFontSubTitle);
        musicLabel.setForeground(Color.WHITE);
        musicLabel.setBounds(60, 80, 80, 30);
        panel.add(musicLabel);

        JLabel musicValLabel = new JLabel(String.valueOf(SoundManager.getBgmVolume()));
        musicValLabel.setFont(FontManager.pixelFontBody);
        musicValLabel.setForeground(Color.WHITE);
        musicValLabel.setBounds(390, 80, 50, 30);
        panel.add(musicValLabel);

        JSlider musicSlider = new JSlider(0, 100, SoundManager.getBgmVolume());
        musicSlider.setOpaque(false);
        musicSlider.setPaintTicks(false);
        musicSlider.setBounds(130, 80, 240, 30);
        musicSlider.addChangeListener(e -> {
            SoundManager.playSFX("mouse");
            musicValLabel.setText(String.valueOf(musicSlider.getValue()));
            SoundManager.setBgmVolume(musicSlider.getValue());
        });
        panel.add(musicSlider);

        JLabel sfxLabel = new JLabel("音 效");
        sfxLabel.setFont(FontManager.pixelFontSubTitle);
        sfxLabel.setForeground(Color.WHITE);
        sfxLabel.setBounds(60, 160, 80, 30);
        panel.add(sfxLabel);

        JLabel sfxValLabel = new JLabel(String.valueOf(SoundManager.getSfxVolume()));
        sfxValLabel.setFont(FontManager.pixelFontBody);
        sfxValLabel.setForeground(Color.WHITE);
        sfxValLabel.setBounds(390, 160, 50, 30);
        panel.add(sfxValLabel);

        JSlider sfxSlider = new JSlider(0, 100, SoundManager.getSfxVolume());
        sfxSlider.setOpaque(false);
        sfxSlider.setPaintTicks(false);
        sfxSlider.setBounds(130, 160, 240, 30);
        sfxSlider.addChangeListener(e -> {
            SoundManager.playSFX("mouse");
            sfxValLabel.setText(String.valueOf(sfxSlider.getValue()));
            SoundManager.setSfxVolume(sfxSlider.getValue());
        });
        panel.add(sfxSlider);

        return panel;
    }

    // ==========================================
    // 角色信息 & 头像选择界面
    // ==========================================
    private JPanel createUserPanel() {
        JPanel panel = new JPanel(null);
        panel.setOpaque(false);

        JButton avatarBtn = new JButton();
        avatarBtn.setIcon(scaleIcon("resource/images/profile_" + currentUser.getAvatarId() + ".png", 100, 100));
        avatarBtn.setBounds(50, 30, 100, 100);
        avatarBtn.setContentAreaFilled(false);
        avatarBtn.setBorderPainted(false);
        avatarBtn.setFocusPainted(false);
        panel.add(avatarBtn);

        JLabel nameLabel = new JLabel("名称 : " + currentUser.getAccount());
        nameLabel.setFont(FontManager.pixelFontSubTitle);
        nameLabel.setForeground(Color.WHITE);
        nameLabel.setBounds(180, 70, 280, 40);
        panel.add(nameLabel);

        CardLayout bottomCardLayout = new CardLayout();
        JPanel bottomContainer = new JPanel(bottomCardLayout);
        bottomContainer.setBounds(50, 140, 400, 180);
        bottomContainer.setOpaque(false);

        CardLayout statsCardLayout = new CardLayout();
        JPanel statsPanel = new JPanel(statsCardLayout);
        statsPanel.setOpaque(false);

        JPanel modeSelectPanel = new JPanel(null);
        modeSelectPanel.setOpaque(false);

        JLabel selectMode = new JLabel("请选择需要查看战绩的模式：");
        selectMode.setBounds(0,10,300,30);
        selectMode.setFont(FontManager.pixelFontBody);
        selectMode.setForeground(Color.WHITE);
        modeSelectPanel.add(selectMode);

        JButton normalBtn = new JButton("普通模式");
        normalBtn.setBounds(100,60,200,40);

        JButton gravBtn = new JButton("重力模式");
        gravBtn.setBounds(100,120,200,40);

        for(JButton Btn : new JButton[]{normalBtn, gravBtn}){
            Btn.setFont(FontManager.pixelFontSubTitle);
            Btn.setIcon(scaleIcon("resource/images/dialog_btn.png", this.getWidth(), this.getHeight()));
            Btn.setForeground(Color.WHITE);
            Btn.setHorizontalTextPosition(JButton.CENTER);
            Btn.setVerticalTextPosition(JButton.CENTER);
            Btn.setFocusPainted(false);
            Btn.setContentAreaFilled(false);
            Btn.setBorderPainted(false);
        }

        modeSelectPanel.add(normalBtn);
        modeSelectPanel.add(gravBtn);

        JPanel showStatsPanel = new JPanel(null);
        showStatsPanel.setOpaque(false);

        JLabel easyLabel = new JLabel("简单",SwingConstants.CENTER);
        JLabel hardLabel = new JLabel("困难",SwingConstants.CENTER);
        
        easyLabel.setFont(FontManager.pixelFontSubTitle);
        easyLabel.setForeground(Color.CYAN);
        easyLabel.setBounds(0, 0, 200, 30);
        showStatsPanel.add(easyLabel);
        
        hardLabel.setFont(FontManager.pixelFontSubTitle);
        hardLabel.setForeground(Color.PINK);
        hardLabel.setBounds(200, 0, 200, 30);
        showStatsPanel.add(hardLabel);

        JLabel scoreLabelEasy = new JLabel("",SwingConstants.CENTER);
        JLabel timeLabelEasy = new JLabel("",SwingConstants.CENTER);
        JLabel levelLabelEasy = new JLabel("",SwingConstants.CENTER);
        JLabel scoreLabelHard = new JLabel("",SwingConstants.CENTER);
        JLabel timeLabelHard = new JLabel("",SwingConstants.CENTER);
        JLabel levelLabelHard = new JLabel("",SwingConstants.CENTER);

        for(JLabel label : new JLabel[]{scoreLabelEasy,timeLabelEasy,levelLabelEasy,scoreLabelHard,timeLabelHard,levelLabelHard}){
            label.setFont(FontManager.pixelFontBody);
            label.setForeground(Color.WHITE);
            showStatsPanel.add(label);
        }

        int y0 = 35;
        int gap = 35;
        scoreLabelEasy.setBounds(0, y0, 200, 30);
        timeLabelEasy.setBounds(0, y0 + gap, 200, 30);
        levelLabelEasy.setBounds(0, y0 + gap * 2, 200, 30);
        scoreLabelHard.setBounds(200, y0, 200, 30);
        timeLabelHard.setBounds(200, y0 + gap, 200, 30);
        levelLabelHard.setBounds(200, y0 + gap * 2, 200, 30);
        
        JButton confirmStatsBtn = new JButton("确定");
        confirmStatsBtn.setFont(FontManager.pixelFontBody);
        confirmStatsBtn.setForeground(Color.WHITE);
        confirmStatsBtn.setIcon(scaleIcon("resource/images/dialog_btn.png", 80, 40));
        confirmStatsBtn.setHorizontalTextPosition(JButton.CENTER);
        confirmStatsBtn.setVerticalTextPosition(JButton.CENTER);
        confirmStatsBtn.setFocusPainted(false);
        confirmStatsBtn.setContentAreaFilled(false);
        confirmStatsBtn.setBounds(160, 135, 80, 40);
        confirmStatsBtn.setBorderPainted(false);
        showStatsPanel.add(confirmStatsBtn); 

        normalBtn.addActionListener(e ->{
            scoreLabelEasy.setText("最高分: " + currentUser.getBestScore(0, 0));
            timeLabelEasy.setText("最短时间: " + currentUser.getFormattedShortestTime(0, 0));
            levelLabelEasy.setText("通关数: " + currentUser.getClearedLevels(0, 0));
            scoreLabelHard.setText("最高分: " + currentUser.getBestScore(0, 1));
            timeLabelHard.setText("最短时间: " + currentUser.getFormattedShortestTime(0, 1));
            levelLabelHard.setText("通关数: " + currentUser.getClearedLevels(0, 1));

            statsCardLayout.show(statsPanel, "SHOW_STATS");
        });

        gravBtn.addActionListener(e -> {
            scoreLabelEasy.setText("最高分: " + currentUser.getBestScore(1, 0));
            timeLabelEasy.setText("最短时间: " + currentUser.getFormattedShortestTime(1, 0));
            levelLabelEasy.setText("通关数: " + currentUser.getClearedLevels(1, 0));
            scoreLabelHard.setText("最高分: " + currentUser.getBestScore(1, 1));
            timeLabelHard.setText("最短时间: " + currentUser.getFormattedShortestTime(1, 1));
            levelLabelHard.setText("通关数: " + currentUser.getClearedLevels(1, 1));

            statsCardLayout.show(statsPanel, "SHOW_STATS");
        });
        
        confirmStatsBtn.addActionListener(e ->{
            statsCardLayout.show(statsPanel, "SELECT_MODE");
        });

        statsPanel.add(modeSelectPanel,"SELECT_MODE");
        statsPanel.add(showStatsPanel,"SHOW_STATS");
        

        /*JLabel levelLabel = new JLabel("总关卡数 : " + currentUser.getClearedLevels(0,0));
        levelLabel.setFont(FontManager.pixelFontBody);
        levelLabel.setForeground(Color.WHITE);
        levelLabel.setBounds(0, 20, 200, 30);
        statsPanel.add(levelLabel);

        JLabel rankLabel = new JLabel("最佳成绩 : " + currentUser.getBestScore(0,0));
        rankLabel.setFont(FontManager.pixelFontBody);
        rankLabel.setForeground(Color.WHITE);
        rankLabel.setBounds(0, 80, 200, 30);
        statsPanel.add(rankLabel);

        JLabel timeLabel = new JLabel("最短用时 : " + currentUser.getFormattedShortestTime(0,0));
        timeLabel.setFont(FontManager.pixelFontBody);
        timeLabel.setForeground(Color.WHITE);
        timeLabel.setBounds(0, 140, 200, 30);
        statsPanel.add(timeLabel);*/



        JPanel avatarSelectPanel = new JPanel(null);
        avatarSelectPanel.setOpaque(false);

        JLabel selectHint = new JLabel("头像选择:");
        selectHint.setFont(FontManager.pixelFontBody);
        selectHint.setForeground(Color.WHITE);
        selectHint.setBounds(0, 0, 100, 30);
        avatarSelectPanel.add(selectHint);

        JPanel gridPanel = new JPanel(new GridLayout(2, 3, 15, 15));
        gridPanel.setOpaque(false);
        gridPanel.setBounds(0, 40, 220, 120);

        tempSelectedAvatarIndex = currentUser.getAvatarId(); // 初始化为当前头像的索引

        for (int i = 0; i < 6; i++) {
            int currentAvatarId = i + 1; // 头像 ID 从 1 开始
            JButton imgBtn = new JButton(); 
            imgBtn.setIcon(scaleIcon("resource/images/profile_" + currentAvatarId + ".png", 50, 50));
            imgBtn.setContentAreaFilled(false);
            imgBtn.setFocusPainted(false);

            if (currentAvatarId == tempSelectedAvatarIndex) {
                imgBtn.setBorder(BorderFactory.createLineBorder(Color.WHITE, 3)); // 当前选中头像有白色边框
                imgBtn.setBorderPainted(true);
            } else {
                imgBtn.setBorderPainted(false);
            }

            smallAvatarBtns[i] = imgBtn; // 存起来，方便后面取消边框

            imgBtn.addActionListener(e -> {
                SoundManager.playSFX("confirm");
                tempSelectedAvatarIndex = currentAvatarId; // 更新临时选中头像的索引
                avatarBtn.setIcon(scaleIcon("resource/images/profile_" + tempSelectedAvatarIndex + ".png", 100, 100)); // 更新主头像显示

                // 切换边框：先全部取消，再给当前选中头像加边框
                for (int j = 0; j < 6; j++) {
                    smallAvatarBtns[j].setBorderPainted(false);
                }
                imgBtn.setBorder(BorderFactory.createLineBorder(Color.WHITE, 3));
                imgBtn.setBorderPainted(true);
            });

            gridPanel.add(imgBtn);
        }
        avatarSelectPanel.add(gridPanel);

        JButton confirmAvatarBtn = new JButton("确定");
        confirmAvatarBtn.setFont(FontManager.pixelFontBody);
        confirmAvatarBtn.setForeground(Color.WHITE);
        confirmAvatarBtn.setIcon(scaleIcon("resource/images/dialog_btn.png", 80, 40));
        confirmAvatarBtn.setHorizontalTextPosition(JButton.CENTER);
        confirmAvatarBtn.setVerticalTextPosition(JButton.CENTER);
        confirmAvatarBtn.setFocusPainted(false);
        confirmAvatarBtn.setContentAreaFilled(false);
        confirmAvatarBtn.setBounds(280, 100, 80, 40);
        confirmAvatarBtn.setBorderPainted(false);
        avatarSelectPanel.add(confirmAvatarBtn);

        bottomContainer.add(statsPanel, "STATS");
        bottomContainer.add(avatarSelectPanel, "AVATAR_SELECT");
        panel.add(bottomContainer);

        avatarBtn.addActionListener(e -> {
            SoundManager.playSFX("confirm");
            bottomCardLayout.show(bottomContainer, "AVATAR_SELECT");
        });
        confirmAvatarBtn.addActionListener(e -> {
            SoundManager.playSFX("confirm");
            currentUser.setAvatarId(tempSelectedAvatarIndex);
            manager.AccountManager.updateUser(currentUser);
            avatarBtn.setIcon(scaleIcon("resource/images/profile_" + tempSelectedAvatarIndex + ".png", 100, 100)); // 更新主头像显示
        
            bottomCardLayout.show(bottomContainer, "STATS");
        });
        return panel;
    }
    // ==========================================
    // 缩放图片的工具方法 (专门解决图片只显示一半的问题)
    // ==========================================
    private ImageIcon scaleIcon(String path, int width, int height) {
        ImageIcon icon = new ImageIcon(path);
        // 如果找不到图片（比如拼写错误），直接返回原 icon 避免崩溃
        if (icon.getIconWidth() == -1) return icon; 
        
        Image img = icon.getImage();
        // 将图片缩放到指定的宽高，SCALE_DEFAULT 对像素图比较友好
        Image newImg = img.getScaledInstance(width, height, Image.SCALE_DEFAULT);
        return new ImageIcon(newImg);
    }
}