package ui;

import manager.AccountInfo;
import manager.AccountManager;
import utils.FontManager;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.List;

public class LeaderboardPanel extends JPanel {

    private CardLayout cardLayout;
    private JPanel cardContainer;
    private Image bgImage;
    private int type;
    private int diff;

    public LeaderboardPanel(AccountInfo currentUser, int type, int diff) {
        this.type = type;
        this.diff = diff;

        setBounds(40, 120, 204, 552); 
        
        // 把图片插入当作背景
        this.bgImage = new ImageIcon("resource/images/leaderboard_bg.png").getImage();
        setLayout(new BorderLayout(0, 5)); // 上下间距5像素
        setBorder(null); 
        setOpaque(false);


        // ==========================================
        // 1. 顶部 Tab 切换按钮
        // ==========================================
        JPanel topTabPanel = new JPanel(new GridLayout(1, 3));
        topTabPanel.setOpaque(false);


        JButton scoreTab = new JButton("分数");
        JButton timeTab = new JButton("时间");
        JButton levelTab = new JButton("关卡");

        // 把三个按钮放进一个数组里，方便后面写点击逻辑
        JButton[] tabs = {scoreTab, timeTab, levelTab};

        for (JButton tab : tabs) {
            tab.setFont(FontManager.pixelFontBody);
            tab.setFocusPainted(false);
            tab.setForeground(Color.LIGHT_GRAY);
            tab.setBackground(new Color(40, 30, 60)); // 未选中颜色
            tab.setBorder(BorderFactory.createLineBorder(new Color(158, 89, 181)));
        }

        // 默认选中第一个
        scoreTab.setForeground(Color.WHITE);
        scoreTab.setBackground(new Color(80, 60, 110)); // 选中颜色

        // ==========================================
        // 2. 中间 CardLayout 容器
        // ==========================================
        cardLayout = new CardLayout();
        cardContainer = new JPanel(cardLayout);
        cardContainer.setOpaque(false);

        // 创建三个不同的排行榜列表，并添加到卡片容器
        cardContainer.add(createRankListPanel(AccountManager.getLeaderboardByScore(type,diff), "score"), "SCORE");
        cardContainer.add(createRankListPanel(AccountManager.getLeaderboardByTime(type,diff), "time"), "TIME");
        cardContainer.add(createRankListPanel(AccountManager.getLeaderboardByLevels(type,diff), "levels"), "LEVELS");

        // ==========================================
        // 3. 底部固定的当前玩家信息
        // ==========================================
        // 这个可以根据切换的 Tab 动态更新，暂时先做一个简单的
        JPanel bottomPanel = new JPanel(); 
        bottomPanel.setOpaque(false);
        JLabel myRankLabel = new JLabel();
        myRankLabel.setFont(FontManager.pixelFontBody);
        myRankLabel.setForeground(Color.WHITE);
        updateMyRankLabel(myRankLabel, AccountManager.getRankByScore(currentUser,type,diff));
        bottomPanel.add(myRankLabel);

        // 拼装
        topTabPanel.add(scoreTab);
        topTabPanel.add(timeTab);
        topTabPanel.add(levelTab);
        add(topTabPanel, BorderLayout.NORTH);
        add(cardContainer, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);

        // ==========================================
        // 4. Tab 点击切换逻辑
        // ==========================================
        scoreTab.addActionListener(e -> {
            cardLayout.show(cardContainer, "SCORE");
            updateTabStyles(tabs, scoreTab); // 更新按钮样式
            updateMyRankLabel(myRankLabel, AccountManager.getRankByScore(currentUser,type,diff)); // 更新当前玩家排名
            
        });
        timeTab.addActionListener(e -> {
            cardLayout.show(cardContainer, "TIME");
            updateTabStyles(tabs, timeTab);
            updateMyRankLabel(myRankLabel, AccountManager.getRankByTime(currentUser,type,diff)); // 更新当前玩家排名
        });
        levelTab.addActionListener(e -> {
            cardLayout.show(cardContainer, "LEVELS");
            updateTabStyles(tabs, levelTab);
            updateMyRankLabel(myRankLabel, AccountManager.getRankByLevels(currentUser,type,diff)); // 更新当前玩家排名
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        // **关键**：这里**不要**调用 super.paintComponent(g);
        // 因为我们要通过绘制新的全覆盖背景层（图片或实心颜色）来彻底清除旧的绘图，从而消除叠加。

        // 获取组件当前的宽高
        int width = getWidth();
        int height = getHeight();

        // 1. 核心解决方案：绘制背景图（拉伸以填充整个面板）
        if (bgImage != null) {
            // 使用 drawImage 的拉伸重载：drawImage(Image img, int x, int y, int width, int height, ImageObserver observer)
            g.drawImage(bgImage, 0, 0, width, height, this);
        } else {
            // -- 叠消除：如果图片加载失败，绘制一个实心颜色作为备选背景，以彻底消除叠加 --
            // 这里用一个深实心紫色
            g.setColor(new Color(25, 15, 35)); 
            g.fillRect(0, 0, width, height);
        }
    }

    // --- 动态创建排行榜列表的工厂方法 ---
    private JScrollPane createRankListPanel(List<AccountInfo> data, String type) {
        JPanel listPanel = new JPanel();
        listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));
        listPanel.setOpaque(false);

        for (int i = 0; i < data.size(); i++) {
            AccountInfo user = data.get(i);
            int rank = i + 1;

            // ==========================================
            // 1. 为每一行创建一个专属的 JPanel，用 BorderLayout(用JLabel排版不清，容易导致错位/重叠)
            // ==========================================
            /*JPanel rowPanel = new JPanel(new BorderLayout(10, 0)); // 左右间距10像素
            rowPanel.setOpaque(false);
            
            // 2. 创建三个独立的 JLabel：名次、名字、数值
            JLabel rankLabel = new JLabel();
            JLabel nameLabel = new JLabel(user.getAccount(), SwingConstants.LEFT);
            JLabel valueLabel = new JLabel("", SwingConstants.RIGHT);

            rankLabel.setFont(FontManager.pixelFontBody);
            rankLabel.setForeground(Color.WHITE);
            nameLabel.setFont(FontManager.pixelFontBody);
            nameLabel.setForeground(Color.WHITE);
            valueLabel.setFont(FontManager.pixelFontBody);
            valueLabel.setForeground(Color.WHITE);

            // 3. 根据排名填充名次 JLabel (皇冠或数字)
            if (rank <= 3) {
                rankLabel.setIcon(scaleIcon("resource/images/crown_" + rank + ".png", 24, 24));
            } else {
                rankLabel.setText(String.valueOf(rank));
                rankLabel.setPreferredSize(new Dimension(24, 24)); // 给数字也留出皇冠那么宽
                rankLabel.setHorizontalAlignment(SwingConstants.CENTER);
            }

            // 4. 根据类型填充数值 JLabel，并设置颜色
            switch (type) {
                case "time": valueLabel.setText(user.getFormattedShortestTime()); break;
                case "levels": valueLabel.setText(user.getClearedLevels() + " 关"); break;
                default: valueLabel.setText(String.valueOf(user.getBestScore())); break;
            }

            // 5. 将三个 JLabel 添加到 rowPanel 中
            rowPanel.add(rankLabel, BorderLayout.WEST);
            rowPanel.add(nameLabel, BorderLayout.CENTER);
            rowPanel.add(valueLabel, BorderLayout.EAST);

            listPanel.add(rowPanel);*/
            //JLabel写法
            String value; // 根据类型显示不同的值
            switch (type) {
                case "time": value = user.getFormattedShortestTime(this.type,diff); break;
                case "levels": value = user.getClearedLevels(this.type, diff) + " 关"; break;
                default: value = String.valueOf(user.getBestScore(this.type, diff)); break;
            }

               // 1. 名字截断处理 (如果大于 5 个字，就截断并加省略号)
            // ==========================================
            String displayName = user.getAccount();
            if (displayName.length() > 5) {
                // 截取前 4 个字，加上 ".."
                displayName = displayName.substring(0, 4) + ".."; 
            }

            // ==========================================
            // 2. 获取对应的颜色
            // ==========================================
            String colorStr = "white"; // 默认白色
            if (rank == 1) colorStr = "yellow";

            // ==========================================
            // 3. 构建完全统一的 3 列 HTML 表格
            // ==========================================
            String htmlText;
            if (rank <= 3) {
                // 前三名：用 HTML 的 <img> 标签显示皇冠
                // 把本地文件路径转换为 HTML 能识别的 URL 格式
                java.io.File crownFile = new java.io.File("resource/images/crown_" + rank + ".png");
                String crownUrl = "";
                try { crownUrl = crownFile.toURI().toURL().toString(); } catch (Exception e) {}

                // 第一列放皇冠图片，第二列放名字，第三列放数值
                htmlText = "<html><table border='0' cellpadding='0' cellspacing='0' style='color:" + colorStr + ";'>" +
                           "<tr>" +
                           "<td width='35' align='center'><img src='" + crownUrl + "' width='20' height='20'></td>" +
                           "<td width='80'>" + displayName + "</td>" +
                           "<td width='80' align='right'>" + value + "</td>" +
                           "</tr></table></html>";
            } else {
                // 4名及以后：第一列直接写名次数字
                htmlText = "<html><table border='0' cellpadding='0' cellspacing='0' style='color:" + colorStr + ";'>" +
                           "<tr>" +
                           "<td width='35' align='center'>" + rank + "</td>" +
                           "<td width='80'>" + displayName + "</td>" +
                           "<td width='80' align='right'>" + value + "</td>" +
                           "</tr></table></html>";
            }

            // ==========================================
            // 应用到 JLabel
            // ==========================================
            JLabel rowLabel = new JLabel();
            rowLabel.setIcon(null); // 确保去掉了原生的图标，完全交给 HTML
            rowLabel.setText(htmlText);
            rowLabel.setFont(FontManager.pixelFontBody);
            // 给上下留一点间距，不至于太挤
            rowLabel.setBorder(new javax.swing.border.EmptyBorder(5, 5, 5, 5)); 

            listPanel.add(rowLabel);
        }

        JScrollPane scrollPane = new JScrollPane(listPanel);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setBorder(null);
        //禁止横向滚动条出现，保留垂直滚动功能（像素隐身）
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        scrollPane.getVerticalScrollBar().setPreferredSize(new Dimension(0, 0));
        scrollPane.getHorizontalScrollBar().setPreferredSize(new Dimension(0, 0)); 
        // ==========================================

        return scrollPane;
    }

    // --- 更新 Tab 按钮样式的工具方法 ---
    private void updateTabStyles(JButton[] allTabs, JButton selectedTab) {
        for (JButton tab : allTabs) {
            if (tab == selectedTab) {
                tab.setForeground(Color.WHITE);
                tab.setBackground(new Color(80, 60, 110)); // 选中色
            } else {
                tab.setForeground(Color.LIGHT_GRAY);
                tab.setBackground(new Color(40, 30, 60)); // 未选中色
            }
        }
    }

    private void updateMyRankLabel(JLabel myRankLabel, int rank) {
        if (rank > 0) {
            myRankLabel.setText("我的排名: " + rank);
        } else {
            myRankLabel.setText("我的排名: --");
        }
    }
}