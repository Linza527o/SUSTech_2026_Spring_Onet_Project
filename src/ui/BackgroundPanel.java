package ui;

import utils.FontManager;
import utils.SoundManager;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class BackgroundPanel extends JPanel {
    public Image backgroundImage;

    // 【新增】保存需要画的连线路径和格子信息
    private ArrayList<Point> path = null;
    private Cell cellInfo;
    private JLabel comboDisplayLabel;
    private JLabel remainingLabel;

    private boolean isGoldenTime = false;
    private Timer goldenTimer;
    private int goldenSecondsLeft = 0;

    private Image[] comboFrames = new Image[8];
    private Image[] countdownFrames = new Image[10];

    private boolean showComboAnim = false;
    private int currentComboFrame = -1;
    private int currentCountdownFrame = -1;
    // 构造函数稍微改一下，把 Cell 传进来，因为画线需要格子的坐标数据
    public BackgroundPanel(String imagePath, Cell cellInfo) {
        this.backgroundImage = new ImageIcon(imagePath).getImage();
        this.cellInfo = cellInfo;
        setOpaque(false);
        setLayout(null);
        comboDisplayLabel = new JLabel("Yet to come");
        comboDisplayLabel.setIcon(new ImageIcon("resource/images/btn_bg.png"));
        comboDisplayLabel.setFont(FontManager.pixelFontSubTitle);
        comboDisplayLabel.setForeground(new Color(255, 150, 100)); // 亮橙色
        comboDisplayLabel.setHorizontalTextPosition(JLabel.CENTER);
        comboDisplayLabel.setVerticalTextPosition(JLabel.CENTER);
        comboDisplayLabel.setBounds(1030, 110, 200, 50);
        this.add(comboDisplayLabel);// 设置为绝对布局，方便在上面摆放按钮

        remainingLabel = new JLabel("待消除：50对");
        remainingLabel.setIcon(new ImageIcon("resource/images/btn_bg.png"));
        remainingLabel.setFont(FontManager.pixelFontSubTitle);
        remainingLabel.setForeground(Color.WHITE); 
        remainingLabel.setHorizontalTextPosition(JLabel.CENTER);
        remainingLabel.setVerticalTextPosition(JLabel.CENTER);
        // 放在计时器(200)和分数之间，设为 y=260
        remainingLabel.setBounds(1030, 250, 200, 50);
        this.add(remainingLabel);

        /*for (int i = 0; i < comboFrames.length; i++) {
            comboFrames[i] = new ImageIcon("resource/images/combo/F" + i + ".png").getImage();
        }*/

        for (int i = 0; i < 8; i++) {
            comboFrames[i] = new ImageIcon("resource/images/combo/F" + i + ".png").getImage();
        }
        for (int i = 0; i < countdownFrames.length; i++) {
            countdownFrames[i] = new ImageIcon("resource/images/countdown/F" + i + ".png").getImage();
        }
    }

    int iconOrder;
    // 【新增】对外开放的画线并消除方法
    public void showPathAndClear(ArrayList<Point> path, JButton btn1, JButton btn2,int iconO) {
        this.path  = path;
        setBtnColor(iconO);
        this.repaint(); // 触发重绘（画线）

        Timer timer = new Timer(300, e -> {this.path = null;btn1.setVisible(false);  // 隐藏方块1
            btn2.setVisible(false);this.repaint();});



        timer.setRepeats(false);
        timer.start();
    }


// 1. 【画底图】留在 paintComponent 里，它是最底层的

    public Color btnColor = new Color(128, 0, 255);


    public void triggerGoldenTime(ChessBoard mainBoard) {
        // 1. 播放音效，压低 BGM
        /* SoundManager.lowerBgmVolume();
        Timer delayTimer = new Timer(200, null);
        delayTimer.setRepeats(false);
        delayTimer.start();*/
        SoundManager.playSFX("combo");
        /*Timer lowerTimer = new Timer(500, null);
        lowerTimer.addActionListener(ev ->{
            SoundManager.restoreBgmVolume();
        });
        lowerTimer.setRepeats(false);
        lowerTimer.start();*/

        currentComboFrame = 0;
        showComboAnim = true;
        
        Timer comboAnimTimer = new Timer(70, null);
        comboAnimTimer.addActionListener(e -> {
            currentComboFrame++;
            if (currentComboFrame >= comboFrames.length) { // Combo 动画播放完毕
                comboAnimTimer.stop();
                showComboAnim = false;
                currentComboFrame = -1;
                
                // 4. 动画播完后，正式开启黄金时段！
                startGoldenTime(mainBoard);
            }
            repaint();
        });
        comboAnimTimer.start();
    }

    // 黄金时段开启逻辑
    private void startGoldenTime(ChessBoard mainBoard) {
        isGoldenTime = true;
        mainBoard.setGoldenTime(isGoldenTime);

        // 启动 10 秒黄金倒计时，每 1 秒播一帧你画的 Countdown 进度条
        goldenSecondsLeft = 10;
        currentCountdownFrame = 0;

        if (goldenTimer != null && goldenTimer.isRunning()) goldenTimer.stop();

        goldenTimer = new Timer(1000, e -> {
            goldenSecondsLeft--;
            currentCountdownFrame++;

            // 如果已经到了倒计时最后一张图，就让它一直定格在那张图上
            if (getCountdownFrame(currentCountdownFrame) == null) {
                currentCountdownFrame--; 
            }

            if (goldenSecondsLeft <= 0) {
                // 10秒结束，一切恢复原样
                goldenTimer.stop();
                isGoldenTime = false;
                mainBoard.setGoldenTime(isGoldenTime);;
                currentCountdownFrame = -1;
            }
            repaint();
        });
        goldenTimer.start();
        repaint();
    }

    // 获取当前有效的 Countdown 帧
    private Image getCountdownFrame(int index) {
        int lastValid = 0;
        for (int i = 0; i < countdownFrames.length; i++) {
            if (countdownFrames[i] != null) lastValid = i;
        }
        if (index > lastValid) index = lastValid;
        return countdownFrames[index];
    }


@Override
protected void paintComponent(Graphics g) {
    super.paintComponent(g);

    // 只画背景图
    if (backgroundImage != null) {
        g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
    }

    //黄金时段加金色滤镜
    if (isGoldenTime) {
        g.setColor(new Color(255, 215, 0, 40)); 
         g.fillRect(0, 0, getWidth(), getHeight());
    }
}
    public void setBtnColor(int iconO){
        Color tmp = new Color(128, 0, 255);System.out.println("Click on a button, color not chosen");
        if(iconO == 1){tmp = Color.BLACK;System.out.println("Click on a button, color 1");}
        if(iconO == 2){tmp = Color.GREEN;System.out.println("Click on a button, color 2");}
        if(iconO == 3){tmp = Color.MAGENTA;System.out.println("Click on a button, color 3");}
        if(iconO == 4){tmp = Color.ORANGE;System.out.println("Click on a button, color 4");}
        if(iconO == 5){tmp = Color.BLUE;System.out.println("Click on a button, color 5");}
        if(iconO == 6){tmp = Color.BLUE;System.out.println("Click on a button, color 6");}
        if(iconO == 7){tmp = Color.ORANGE;System.out.println("Click on a button, color 7");}
        if(iconO == 8){tmp = Color.MAGENTA;System.out.println("Click on a button, color 8");}
        if(iconO == 9){tmp = Color.BLACK;System.out.println("Click on a button, color 9");}
        if(iconO == 10){tmp = Color.WHITE;System.out.println("Click on a button, color 10");}
        if(iconO == 11){tmp = Color.YELLOW;System.out.println("Click on a button, color 11");}
        if(iconO == 12){tmp = Color.WHITE;System.out.println("Click on a button, color 12");}
        btnColor = tmp;
    }
    // 2. 【新增画顶层】重写 paint 方法，它负责统筹全局
    @Override
    public void paint(Graphics g) {
        // 这句话非常关键！它会依次去调用 paintComponent(画底图) -> 画子组件(画按钮)
        super.paint(g);


        // 在所有东西都画完之后，我们最后来画红线！这样它就处于绝对的“最上层”了
        if (path != null && path.size() >= 2 && cellInfo != null) {
            Graphics2D g2d = (Graphics2D) g;

            // 开启抗锯齿，让线和圆更平滑好看
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.setColor(btnColor);
            g2d.setStroke(new BasicStroke(4.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

            // 画线段
            for (int i = 0; i < path.size() - 1; i++) {
                Point p1 = path.get(i);
                Point p2 = path.get(i + 1);

                int px1 = cellInfo.getLeftbound() + p1.y * cellInfo.getEdgelength() + cellInfo.getEdgelength() / 2;
                int py1 = cellInfo.getUpbound()   + p1.x * cellInfo.getEdgelength() + cellInfo.getEdgelength() / 2;
                int px2 = cellInfo.getLeftbound() + p2.y * cellInfo.getEdgelength() + cellInfo.getEdgelength() / 2;
                int py2 = cellInfo.getUpbound()   + p2.x * cellInfo.getEdgelength() + cellInfo.getEdgelength() / 2;

                g2d.drawLine(px1, py1, px2, py2);
            }

            // 画拐点圆圈
            int dotRadius = 8;
            for (int i = 0; i < path.size() ; i++) {
                Point corner = path.get(i);
                int cx = cellInfo.getLeftbound() + corner.y * cellInfo.getEdgelength() + cellInfo.getEdgelength() / 2;
                int cy = cellInfo.getUpbound()   + corner.x * cellInfo.getEdgelength() + cellInfo.getEdgelength() / 2;
                g2d.fillOval(cx - dotRadius, cy - dotRadius, dotRadius * 2, dotRadius * 2);
            }
        }

        if (showComboAnim && currentComboFrame >= 0 && currentComboFrame < 8) {
            Image img = comboFrames[currentComboFrame];
            if (img != null) {
                g.drawImage(img, 0,0, this);
            }
        }

        // 3. 【新增】画出 1秒1帧 的 Countdown 倒计时动画 (放置在屏幕正上方)
        if (isGoldenTime && currentCountdownFrame >= 0) {
            Image img = getCountdownFrame(currentCountdownFrame);
            if (img != null) {
                g.drawImage(img, 0,0, this);
            }
        }
    }
    public void updateComboDisplay(int combocnt) {
        if(combocnt >= 2) {
            comboDisplayLabel.setText("Combo: " + combocnt);
            comboDisplayLabel.setForeground(new Color(255, 50, 50)); // 连击时可以变成红色
        } else {
            comboDisplayLabel.setText("Yet to come");
            comboDisplayLabel.setForeground(new Color(255, 150, 100)); // 断连恢复橙色
        }
        comboDisplayLabel.revalidate();
        comboDisplayLabel.repaint();
    }

    public void updateRemainingDisplay(int count) {
        remainingLabel.setText("待消除：" + count + " 对");
        remainingLabel.repaint();
    }
}