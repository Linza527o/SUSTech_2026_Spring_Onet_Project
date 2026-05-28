package ui;

import javax.swing.*;

import manager.GameSaveManager;
import utils.FontManager;
import utils.SoundManager;
import utils.TransitionManager;

import java.awt.*;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;

public class GameFrame extends JFrame {
    private manager.AccountInfo currentUser;
    private TimerPanel timer;
    public int mode;
    public int totalScore = 0;
    public int currentScoreOverall = 0;
    public int currentTimeOverall = 0;
    public boolean applyGravity;

    public int type;
    public void setApplyGravity(boolean b){
        applyGravity = b;
    }
    Cell cells = new Cell(320, 40, 64);
    public GameFrame(manager.AccountInfo currentUser,boolean playAnimation,boolean applyGravity, int mode) {

        this.currentUser = currentUser;
        this.applyGravity = applyGravity;
        this.mode = mode;
        if(applyGravity){
            type = 1;
        }else{type = 0;}
        

        setSize(1280, 720);
        setUndecorated(true); // 去掉默认的系统边框（为了像素风纯粹性）
        setLocationRelativeTo(null); // 窗口在屏幕居中

        
        GameEntrancePanel entrancePanel = new GameEntrancePanel("game_start/", 19, 80);
        entrancePanel.applySFX(2, "ascend");
         // 1. 必须手动设置大小，铺满全屏
        entrancePanel.setBounds(0, 0, 1280, 720); 
        
        // 2. 把它加到 LayeredPane 的 MODAL_LAYER (模态层)，这层在所有的按钮和棋盘之上！
        this.getLayeredPane().add(entrancePanel, JLayeredPane.MODAL_LAYER);
        
        entrancePanel.setVisible(true);
        this.setVisible(true); // 最后才显示窗口，确保一打开就能看到开场动画
        
        BackgroundPanel bgPanel = new BackgroundPanel("resource/images/game_panel.png", cells);
        bgPanel.setBounds(0, 0, 1000, 800);
        this.setContentPane(bgPanel); // 把背景设置为内容面板
        initComponents(bgPanel);


        if (playAnimation) {
            // 如果是从大地图第一次进来，看完整动画
            entrancePanel.playSequence(() -> {
                entrancePanel.setVisible(false); 
                SoundManager.playBGM("resource/music/gaming_bgm.wav"); 

                timer.startTimer();
            });
        } else {
            // 如果是重新开始或继续游戏，直接 321 倒计时
            entrancePanel.playCountdownOnly(() -> {
                entrancePanel.setVisible(false); 
                SoundManager.playBGM("resource/music/gaming_bgm.wav"); 

                timer.startTimer();
            });
        }
        
        setVisible(true);
    }



    public void initComponents(BackgroundPanel bgPanel) {
        ChessBoard mainBoard = new ChessBoard(10, 10, mode == 0 ? 5 : 12, applyGravity);
        GameSaveManager gameSaveManager = new GameSaveManager();


        //添加左边LeaderboardPanel信息
        LeaderboardPanel leaderboardPanel = new LeaderboardPanel(currentUser, type, mode);
        bgPanel.add(leaderboardPanel);

        JLabel leaderboardTitle = new JLabel("排 行 榜");
        leaderboardTitle.setFont(FontManager.pixelFontSubTitle);
        leaderboardTitle.setForeground(new Color(230, 195, 215));
        leaderboardTitle.setBounds(92, 44, 200, 30);
        bgPanel.add(leaderboardTitle);


        //右上方关卡数显示
        // ==========================================
        int level = 0;
        if (!applyGravity) {
            level = currentUser.getClearedLevels(0,mode) + 1;
        } else {
            level = currentUser.getClearedLevels(1,mode) + 1;
        }

        JLabel levelDisplayLabel = new JLabel("第 " + level + " 关");
        levelDisplayLabel.setIcon(new ImageIcon("resource/images/levels_bg.png"));
        levelDisplayLabel.setFont(FontManager.pixelFontSubTitle);
        levelDisplayLabel.setForeground(new Color(255, 230, 180)); // 浅金色

        levelDisplayLabel.setHorizontalTextPosition(JLabel.CENTER);
        levelDisplayLabel.setVerticalTextPosition(JLabel.CENTER);

        levelDisplayLabel.setBounds(1030, 40, 200, 50);
        bgPanel.add(levelDisplayLabel);
        /// ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        timer = new TimerPanel("/resource/images/btn_bg.png");
        timer.setBounds(1030, 200, 200, 50);

        bgPanel.add(timer.getTimeButton());
        //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        JButton saveButton = new JButton("保存进度");

        saveButton.setFont(FontManager.pixelFontSubTitle);
        saveButton.setForeground(Color.WHITE);
        saveButton.setIcon(new ImageIcon("resource/images/btn_bg.png"));
        saveButton.setHorizontalTextPosition(JButton.CENTER);
        saveButton.setVerticalTextPosition(JButton.CENTER);
        saveButton.setContentAreaFilled(false);
        saveButton.setBorderPainted(false);
        saveButton.setFocusPainted(false);
        saveButton.setBounds(1030, 390, 200, 50);
        bgPanel.add(saveButton);


        saveButton.addActionListener(e -> {
            SoundManager.playSFX("confirm");

            int[][] currentMap = mainBoard.getMap();
            int currentScore = mainBoard.cnt.getScore();

            int currentTime = timer.getSecondsPassed();


            try {
                GameSaveManager.saveGame(currentUser.getAccount(), currentMap, currentScore, currentTime, applyGravity, type, mode);
                PixelDialog pixelDialog = new PixelDialog(this, "进度已保存！");
                pixelDialog.setVisible(true);
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }

        });


//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////


/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////主要游戏界面
        

        JButton backButton = new JButton("返回地图");
        backButton.setFont(FontManager.pixelFontSubTitle);
        backButton.setForeground(Color.WHITE);
        backButton.setIcon(new ImageIcon("resource/images/btn_bg.png"));
        backButton.setHorizontalTextPosition(JButton.CENTER);
        backButton.setVerticalTextPosition(JButton.CENTER);
        backButton.setContentAreaFilled(false);
        backButton.setBorderPainted(false);
        backButton.setFocusPainted(false);
        backButton.setBounds(1030, 530, 200, 50);
        bgPanel.add(backButton);

        JButton restartButton = new JButton("重新开始");
        restartButton.setFont(FontManager.pixelFontSubTitle);
        restartButton.setForeground(Color.WHITE);
        restartButton.setIcon(new ImageIcon("resource/images/btn_bg.png"));
        restartButton.setHorizontalTextPosition(JButton.CENTER);
        restartButton.setVerticalTextPosition(JButton.CENTER);
        restartButton.setContentAreaFilled(false);
        restartButton.setBorderPainted(false);
        restartButton.setFocusPainted(false);
        restartButton.setBounds(1030, 460, 200, 50);
        bgPanel.add(restartButton);

        JButton settingsButton = new JButton("设置");
        settingsButton.setFont(FontManager.pixelFontSubTitle);
        settingsButton.setForeground(Color.WHITE);
        settingsButton.setIcon(new ImageIcon("resource/images/btn_bg.png"));
        settingsButton.setHorizontalTextPosition(JButton.CENTER);
        settingsButton.setVerticalTextPosition(JButton.CENTER);
        settingsButton.setContentAreaFilled(false);
        settingsButton.setBorderPainted(false);
        settingsButton.setFocusPainted(false);
        settingsButton.setBounds(1030, 600, 200, 50);
        bgPanel.add(settingsButton);
        ///  ///////////////////////////////////////////////////////////////////


        /// ////////////////////////////////////////////////////////////////////////////////////////////
        AtomicBoolean pass = new AtomicBoolean(false);
        ActionListener onVictory = e -> {
            int scoreThisGame = mainBoard.cnt.getScore();
            int timeThisGame = timer.getSecondsPassed();


            timer.stopTimer();
            manager.UserDataManager.saveProgress(currentUser, scoreThisGame, timeThisGame, mode, type);

            gameSaveManager.deleteSave(currentUser.getAccount(),type,mode);


            System.out.println("GameFrame 收到胜利信号<(￣ c￣)y▂ξhahaxixihuhu111");
            pass.set(true);
            JPanel dimPane = new JPanel() {
                @Override
                protected void paintComponent(Graphics g) {
                    // 这里才是真正实现“半透明黑色”的地方
                    g.setColor(new Color(0, 0, 0, 150));
                    g.fillRect(0, 0, getWidth(), getHeight());
                    super.paintComponent(g);
                }
            };
            dimPane.setOpaque(false);

            // 2. 利用 GlassPane (玻璃层) 把它铺在最顶层
            this.setGlassPane(dimPane);
            dimPane.setVisible(true);
            totalScore = mainBoard.cnt.getScore();
            //先停止背景bgm，再播放胜利音乐
            SoundManager.fadeOutBGM(0); // 渐隐当前BGM
            SoundManager.playSFX("victory");
            VictoryDialog dialog = new VictoryDialog(this, currentUser, cells, scoreThisGame,applyGravity, mode, mainBoard.getVictoryStatus());
            dialog.setVisible(true);


            dimPane.setVisible(false);
            this.getGlassPane().setVisible(false);
            this.setGlassPane(new JPanel());

        };


        /////////////////////////////////////////////////////////////////////////////////////////////
        if (gameSaveManager.loadGame(currentUser.getAccount(), type, mode) != null && !pass.get()) {
            mainBoard.setMap(gameSaveManager.loadGame(currentUser.getAccount(), type, mode).map);
            currentScoreOverall = gameSaveManager.loadGame(currentUser.getAccount(), type, mode).score;
            currentTimeOverall = gameSaveManager.loadGame(currentUser.getAccount(),type ,mode).timeLeft;
            timer.setSecondsPassed(currentTimeOverall);
        } else {
            mainBoard.gameSetting();

            try{
               manager.GameSaveManager.saveInitialState(currentUser.getAccount(), mainBoard.getMap(), currentScoreOverall, currentTimeOverall, applyGravity, type, mode); 
               System.out.println("初始棋盘已保存！");
            }catch(IOException e){
                e.printStackTrace();
            }
        }

        // 【新增】在这里初始化加载剩余的对数！
        bgPanel.updateRemainingDisplay(mainBoard.getRemainingPairs());

        JButton[][] buttonGrid = cells.cellArrays(mainBoard.getMap());
        cells.setCellsFunction(buttonGrid, mainBoard, bgPanel, onVictory, applyGravity);

//            int combocnt = mainBoard.getCombocnt();

            /// //////////////////////////////////////////////////////////////////////////////////////////////////////

            mainBoard.cnt.setScore(currentScoreOverall);
            mainBoard.cnt.setBounds(1030, 320, 200, 50);
            bgPanel.add(mainBoard.cnt);


            //返回菜单
            
            //该按钮仅作为测试胜利界面正确弹出用，写好游戏底层玩法代码后删除
//        JButton victoryButton = new JButton("测试");
//        victoryButton.setFont(FontManager.pixelFontBody);
//        victoryButton.setForeground(Color.WHITE);
//        victoryButton.setIcon(new ImageIcon("resource/images/btn_bg.png"));
//        victoryButton.setHorizontalTextPosition(JButton.CENTER);
//        victoryButton.setVerticalTextPosition(JButton.CENTER);
//        victoryButton.setContentAreaFilled(false);
//        victoryButton.setBorderPainted(false);
//        victoryButton.setFocusPainted(false);
//        victoryButton.setBounds(600, 330, 200, 50);
//        bgPanel.add(victoryButton);



            backButton.addActionListener(e -> {
                SoundManager.playSFX("confirm");

                backButton.setEnabled(false); // 防止重复点击

                TransitionManager.fadeOutAndSwitch(this, 1000, () -> {
                    MapFrame mapFrame = new MapFrame(currentUser);
                    TransitionManager.fadeIn(mapFrame, 500);
                    mapFrame.setVisible(true);
                });
            });

            restartButton.addActionListener(e -> {
                // 这里可以添加重置游戏状态的逻辑
                // 目前只是简单地重新打开一个新的 GameFrame
                SoundManager.playSFX("confirm");
                GameSaveManager.SaveData initData = gameSaveManager.loadInitialState(currentUser.getAccount(), type, mode);
                if(initData != null){
                    try{
                        GameSaveManager.saveGame(currentUser.getAccount(), initData.map, initData.score, initData.timeLeft, applyGravity, type, mode);
                    }catch(IOException ex){
                        ex.printStackTrace();
                    }
                }else{
                    GameSaveManager.deleteSave(currentUser.getAccount(),type,mode);
                }
                new GameFrame(currentUser, false, applyGravity, mode).setVisible(true);
                dispose(); // 关闭当前游戏界面
            });

            settingsButton.addActionListener(e -> {
                SoundManager.playSFX("confirm");
                // 1. 定义背景变暗的“保鲜膜”层
                JPanel dimPane = new JPanel() {
                    @Override
                    protected void paintComponent(Graphics g) {
                        // 这里才是真正实现“半透明黑色”的地方
                        g.setColor(new Color(0, 0, 0, 150));
                        g.fillRect(0, 0, getWidth(), getHeight());
                        super.paintComponent(g);
                    }
                };
                dimPane.setOpaque(false); // 必须设为 false 才能看到底下的内容

                // 2. 利用 GlassPane (玻璃层) 把它铺在最顶层
                this.setGlassPane(dimPane);
                dimPane.setVisible(true);

                // 3. 打开弹窗，注意这里一定要传 this！
                SettingsDialog dialog = new SettingsDialog(this, currentUser, cells);
                dialog.setVisible(true);

                // 4. 弹窗关闭后（代码运行到这一行说明弹窗关了），撤掉变暗效果
                dimPane.setVisible(false);
            });
        }
        //胜利界面弹出（仅作测试）
//        victoryButton.addActionListener(e -> {
//             JPanel dimPane = new JPanel() {
//                @Override
//                protected void paintComponent(Graphics g) {
//                    // 这里才是真正实现“半透明黑色”的地方
//                    g.setColor(new Color(0, 0, 0, 150));
//                    g.fillRect(0, 0, getWidth(), getHeight());
//                    super.paintComponent(g);
//                }
//            };
//            dimPane.setOpaque(false); // 必须设为 false 才能看到底下的内容
//
//            // 2. 利用 GlassPane (玻璃层) 把它铺在最顶层
//            this.setGlassPane(dimPane);
//            dimPane.setVisible(true);
//
//            //先停止背景bgm，再播放胜利音乐
//            SoundManager.fadeOutBGM(0); // 渐隐当前BGM
//            SoundManager.playSFX("victory");
//
//            VictoryDialog dialog = new VictoryDialog(this, currentUser);
//
//            dialog.setVisible(true);
//
//            dimPane.setVisible(false);
//        });
//    }


    }



