package ui;

import utils.FontManager;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.awt.event.ActionEvent;

public class Cell {
    private int leftbound;
    private int upbound;
    private int edgelength;

    // 【新增】用来存储当前需要画的线段
    public ArrayList<Point> lineToDraw = null;

    public Cell(int lb, int ub, int ed) {
        this.leftbound = lb;
        this.upbound = ub;
        this.edgelength = ed;
    }

    public JButton[][] cellArrays(int[][] map) {
        JButton[][] buttons = new JButton[map.length][map[0].length];
        for (int i = 0; i < map.length; i++) {
            for (int j = 0; j < map[0].length; j++) {
                buttons[i][j] = new JButton("" + map[i][j]);
                buttons[i][j].setHorizontalTextPosition(JButton.CENTER);
                buttons[i][j].setVerticalTextPosition(JButton.CENTER);
                buttons[i][j].setContentAreaFilled(false);
                buttons[i][j].setBorderPainted(true);
                buttons[i][j].setFocusPainted(false);
                buttons[i][j].setFont(FontManager.pixelFontSubTitle);
                buttons[i][j].setForeground(Color.WHITE);
                buttons[i][j].setIcon(new ImageIcon("resource/icons/ic" + map[i][j] + ".png"));
//                System.out.println("放置一个棋子");
                int x = leftbound + j * edgelength;
                int y = upbound + i * edgelength;
                buttons[i][j].setBounds(x, y, edgelength, edgelength);
            }
        }
        return buttons;
    }


    public void showPathAndClear(ArrayList<Point> path, JButton btn1, JButton btn2, JPanel panel) {
        // 步骤A：把路径记到自己的本子上
        this.lineToDraw = path;

        // 步骤B：命令面板重绘（这会让面板去读 lineToDraw 然后画出红线）
        panel.repaint();

        // 步骤C：设一个 300 毫秒的定时器
        Timer timer = new Timer(300, e -> {
            this.lineToDraw = null;  // 擦掉本子上的路径

            btn1.setVisible(false);  // 隐藏按下的第一个方块
            btn2.setVisible(false);  // 隐藏按下的第二个方块

            panel.repaint();         // 再次命令面板重绘（因为路径是 null 了，线就消失了）
        });
        timer.setRepeats(false); // 只执行一次
        timer.start();
    }
    // 注意：第三个参数改成了 BackgroundPanel
    public void setCellsFunction(JButton[][] buttons, ChessBoard main, BackgroundPanel panel, ActionListener victoryListener, boolean applygrav) {
        ArrayList<Integer> tmpX = new ArrayList<>();
        ArrayList<Integer> tmpY = new ArrayList<>();

        for (int i = 0; i < buttons.length; i++) {
            for (int j = 0; j < buttons[i].length; j++) {
                panel.add(buttons[i][j]);

                //隐藏消除的格子
                if(main.getMap()[i][j]==0){
                    buttons[i][j].setVisible(false);
                }
                else{
                    buttons[i][j].setVisible(true);
                }

                final int finalI = i;
                final int finalJ = j;

                buttons[i][j].addActionListener(ch -> {
                    buttons[finalI][finalJ].setEnabled(false);
                    tmpX.add(finalI);
                    tmpY.add(finalJ);

                    if (tmpX.size() == 2) {
                        int x1 = tmpX.get(0);
                        int y1 = tmpY.get(0);
                        int x2 = tmpX.get(1);
                        int y2 = tmpY.get(1);
                        int tmpIconOrder = main.getMap()[x1][y1];

                        if (main.isValidConnection(x1, y1, x2, y2,true) && applygrav == false) {
                            main.cellElimination(x1,y1,x2,y2,false,panel);
                            panel.updateRemainingDisplay(main.getRemainingPairs());
                            //
                            ArrayList<Point> path = main.currentPath;

                            //
                            panel.showPathAndClear(path, buttons[x1][y1], buttons[x2][y2],tmpIconOrder);
                            if (main.isGameCleared() || !main.chessboardSolvable()) {
                                if (victoryListener != null) {
                                    // 模拟触发一个 ActionEvent 传回给 GameFrame
                                    victoryListener.actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, "VICTORY"));
                                }
                            }
                            System.out.println("消除成功，当前棋盘:");
                            main.controlPanelPrintln();


                        }
                        else if((main.isValidConnection(x1, y1, x2, y2,true) && applygrav)){
                            main.cellElimination(x1,y1,x2,y2,true,panel);
                            panel.updateRemainingDisplay(main.getRemainingPairs());
                            ArrayList<Point> path = main.currentPath;

                            //

                            panel.showPathAndClear(path, buttons[x1][y1], buttons[x2][y2],tmpIconOrder);
                            if (main.isGameCleared()|| !main.chessboardSolvable()) {
                                System.out.println("CHECK SOVABLITY");
                                if (victoryListener != null) {
                                    // 模拟触发一个 ActionEvent 传回给 GameFrame
                                    victoryListener.actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, "VICTORY"));
                                }
                            }
                            System.out.println("消除成功，当前棋盘:");
                            Timer timer = new Timer(300, e -> { /* 0.3秒后执行的清除代码 */
                            main.applyGravity();
                            main.controlPanelPrintln();

                                for (int k = 0; k < buttons.length; k++) {
                                    for (int l = 0; l < buttons[0].length; l++) {
                                        panel.remove(buttons[k][l]);
                                    }
                                }

                            JButton[][] Buttons = cellArrays(main.getMap());
                            setCellsFunction(Buttons, main, panel, victoryListener, applygrav);
                            });
                            timer.setRepeats(false);
                            timer.start();

                        }
                        else {
                            buttons[x1][y1].setEnabled(true);
                            buttons[x2][y2].setEnabled(true);
                        }
                        tmpX.clear();
                        tmpY.clear();
                    }
                });
            }
        }
    }


    public void setLeftbound(int leftbound) { this.leftbound = leftbound; }
    public void setUpbound(int upbound) { this.upbound = upbound; }
    public void setEdgelength(int edgelength) { this.edgelength = edgelength; }
    public int getEdgelength() { return edgelength; }
    public int getUpbound() { return upbound; }
    public int getLeftbound() { return leftbound; }
    public ArrayList<Point> getlineToDraw(){return  lineToDraw;}
}