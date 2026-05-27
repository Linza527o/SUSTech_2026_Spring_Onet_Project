package ui;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class GameBoardPanel extends JPanel {

    private ArrayList<Point> lineToDraw = null;
    private Cell cellInfo;

    public GameBoardPanel(Cell cellInfo) {
        this.cellInfo = cellInfo;
        this.setLayout(null); // 因为你用 setBounds 绝对定位，所以必须关闭布局管理器
    }

    // ==========================================
    // 供外部调用的画线方法
    // ==========================================
    public void showPath(Point start, Point end) {
        ArrayList<Point> path = new ArrayList<>();
        path.add(start);
        path.add(end);
        executeDrawing(path);
    }

    // （如果你算出了1折或2折的坐标，可以继续在这里加 showPath 的重载方法，和之前说的一样）

    private void executeDrawing(ArrayList<Point> path) {
        this.lineToDraw = path;
        this.repaint(); // 触发画线

        Timer timer = new Timer(300, e -> {
            this.lineToDraw = null;
            this.repaint(); // 300毫秒后线消失
        });
        timer.setRepeats(false);
        timer.start();
    }

    // ==========================================
    // 核心重绘方法
    // ==========================================
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (lineToDraw != null && lineToDraw.size() >= 2 && cellInfo != null) {
            Graphics2D g2d = (Graphics2D) g;
            g2d.setColor(Color.RED);
            g2d.setStroke(new BasicStroke(4.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

            for (int i = 0; i < lineToDraw.size() - 1; i++) {
                int px1 = getPixelX(lineToDraw.get(i));
                int py1 = getPixelY(lineToDraw.get(i));
                int px2 = getPixelX(lineToDraw.get(i + 1));
                int py2 = getPixelY(lineToDraw.get(i + 1));
                g2d.drawLine(px1, py1, px2, py2);
            }
        }
    }

    private int getPixelX(Point p) {
        return cellInfo.getLeftbound() + p.y * cellInfo.getEdgelength() + cellInfo.getEdgelength() / 2;
    }

    private int getPixelY(Point p) {
        return cellInfo.getUpbound() + p.x * cellInfo.getEdgelength() + cellInfo.getEdgelength() / 2;
    }

}