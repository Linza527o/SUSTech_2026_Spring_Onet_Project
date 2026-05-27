package ui;

import java.awt.*;

import java.text.BreakIterator;
import java.util.ArrayList;
import java.util.Random;


public class ChessBoard{
    private int numOfRows;
    private int numOfColumns;
    private int numOficons;
    private boolean applyGrav = false;
    private boolean onCombo = false;
    private int[][] map;
    private int comboindex = 0;
    private int combocnt = 0;
    private boolean isSovable = true;
    private boolean isGoldenTime = false;
    private final Random random = new Random();
    int[] validY = new int[100];
    int[] validX = new int[100];
    int pairsset = 0;
    public int hasConnectedCnt = 50;
    public ArrayList<Point> currentPath = new ArrayList<>();

    public ChessBoard(int r, int c, int noi,boolean applyGrav) {
        this.numOfRows = r;
        this.numOfColumns = c;
        this.numOficons = noi;
        this.map = new int[r][c];
        this.applyGrav = applyGrav;
        for (int i = 0; i < c; i++) {
            for (int j = 0; j < r; j++) {
                map[i][j] = 0;
            }
        }
    }

    public int getCombocnt(){
        return combocnt;
    }
    public void applyGravity() {
//        for (int i = numOfRows - 1; i > 0; i--) {
//            for (int j = 0; j < numOfColumns; j++) {
//                if (map[i][j] == 0 && map[i - 1][j] != 0) {
//                    map[i][j] = map[i - 1][j];
//                    map[i - 1][j] = 0;
//
//
//                }
//            }
//
//        }
//        System.out.println("重力场启用1次");

            // 逐列处理，因为列与列之间的下落互不影响
            for (int j = 0; j < numOfColumns; j++) {

                // writePos 指针：记录当前这一列，最底部的“可用空位”在哪
                // 初始状态指向该列的最底端
                int writePos = numOfRows - 1;

                // readPos 指针 (即变量 i)：从下往上扫描这一列所有的方块
                for (int i = numOfRows - 1; i >= 0; i--) {

                    // 如果扫描到了一个非空的方块
                    if (map[i][j] != 0) {

                        // 如果这个方块不在最底部的可用位置，就把它“拉”下来
                        if (i != writePos) {
                            map[writePos][j] = map[i][j];
                            map[i][j] = 0; // 原位置设为空
                        }

                        // 无论方块有没有移动，底部的可用位置都要向上挪一格
                        writePos--;
                    }
                }
            }
            System.out.println("重力场启用1次 (双指针优化版)");

    }

    public void validSpotsX() {
        ArrayList<Integer> validSpotsX = new ArrayList<>();
        int[] validX = new int[numOfRows * numOfColumns];
        for (int i = 0; i < numOfRows; i++) {
            for (int j = 0; j < numOfColumns; j++) {
                if (map[i][j] == 0) {
                    validSpotsX.add(i);
                }
            }
        }
        for (int i = 0; i < validSpotsX.size(); i++) {
            validX[i] = validSpotsX.get(i);
        }
        this.validX = validX;

    }

    public void validSpotsY() {
        int[] validY = new int[numOfRows * numOfColumns];
        this.validY = validY;
        ArrayList<Integer> validSpotsY = new ArrayList<>();
        for (int i = 0; i < numOfRows; i++) {
            for (int j = 0; j < numOfColumns; j++) {
                if (map[i][j] == 0) {
                    validSpotsY.add(j);
                }
            }
        }
        for (int i = 0; i < validSpotsY.size(); i++) {

            validY[i] = validSpotsY.get(i);

        }

    }

    public void gameSetting() {
        int pairsPlaced = 0;
//        map[4][4] = random.nextInt(5);
//        map[5][4] =  random.nextInt(5);
        int random0 = random.nextInt(numOficons)+1;
        setCell(4,4,random0);
        setCell(5,4,random0);
        for(int i = 0;i<=9;i++){
            if(i == 4) continue;
//            map[4][i] = random.nextInt(5);
//            map[4][i] = map[5][9-i];
//            map[5][i] = random.nextInt(5);
//            map[4][9-i] = map[5][i];
            int random1 = random.nextInt(numOficons)+1;
            int random2 = random.nextInt(numOficons)+1;
            setCell(4,i,random1);
            setCell(5,i,random2);
            setCell(4,9-i,random1);
            setCell(5,9-i,random2);
        }
        for(int j = 0;j<=9;j++){
            if(j == 4 || j == 5){
                continue;
            }
            for(int i = 0;i<=9;i++){
                int random3 = random.nextInt(numOficons)+1;
                setCell(j,i,random3);
                setCell(9-j,9-i,random3);
            }
        }





//
//
//                 try{
//                int ic = random.nextInt(numOficons) + 1;
//                setCell(validX[x1], validY[x1], ic);
//                setCell(validX[x2], validY[x2], ic);
//                pairsPlaced++;
//                System.out.printf("已将第%d,%d和%d,%d设置为%d,已设置%d对,当前失败次数总计%d\n", validX[x1], validY[x1], validX[x2], validY[x2], ic, pairsPlaced, failAttemps);
//                setHasConnectedCnt(pairsPlaced);
//                 } catch (Exception e) {
//
//
//            }


        }

    public void setHasConnectedCnt(int pairsset) {
        hasConnectedCnt = pairsset;
    }


    public boolean isValidConnection(int x1, int y1, int x2, int y2,Boolean showTrace) {

        if ((x1 == x2 && y1 == y2) || map[x1][y1] != map[x2][y2]) {
            return false;
        }


        return canConnectLine(x1, y1, x2, y2, showTrace) || canConnect1Fold(x1, y1, x2, y2, showTrace) || canConnect2Fold(x1, y1, x2, y2, showTrace);
    }


/// //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //
    private boolean isPathClear(int x1, int y1, int x2, int y2) {
        if (x1 == x2) {
            int min = Math.min(y1, y2);
            int max = Math.max(y1, y2);
            for (int i = min + 1; i < max; i++) {
                if (map[x1][i] != 0) return false;
            }
            return true;
        } else if (y1 == y2) {
            int min = Math.min(x1, x2);
            int max = Math.max(x1, x2);
            for (int i = min + 1; i < max; i++) {
                if (map[i][y1] != 0) return false;
            }
            return true;
        }
        return false;
    }


    // 0


    public boolean canConnectLine(int x1, int y1, int x2, int y2,boolean showTrace) {

        if (x1 != x2 && y1 != y2) return false;
        if (isPathClear(x1, y1, x2, y2)) {
            if(showTrace) {
                currentPath.clear(); // 清空旧数据
                currentPath.add(new Point(x1, y1)); // 起点
                currentPath.add(new Point(x2, y2)); // 终点
                return true;
            }
            if(!showTrace) {
                return true;
            }
        }
        return false;


    }
    
        // 1
    public boolean canConnect1Fold(int x1, int y1, int x2, int y2,boolean showTrace) {

        if (map[x1][y2] == 0 && isPathClear(x1, y1, x1, y2) && isPathClear(x1, y2, x2, y2)) {
            if(showTrace) {
            currentPath.clear();
            currentPath.add(new Point(x1, y1)); // 起点
            currentPath.add(new Point(x1, y2)); // 拐点
            currentPath.add(new Point(x2, y2));
            return true;}
            else{

                return true;}
        }

        // 检查拐角2 (x2, y1)
        if (map[x2][y1] == 0 && isPathClear(x1, y1, x2, y1) && isPathClear(x2, y1, x2, y2)) {
            if(showTrace) {
                currentPath.clear();
                currentPath.add(new Point(x1, y1)); // 起点
                currentPath.add(new Point(x2, y1)); // 拐点
                currentPath.add(new Point(x2, y2)); // 终点
                return true;
            }
            else{
                 // 清空旧数据
                return true;
            }

        }

        return false;
    }

    // 2
    public boolean canConnect2Fold(int x1, int y1, int x2, int y2, boolean showTrace) {


        for (int j = 0; j < numOfColumns; j++) {
            if (j == y1) continue;


            if (map[x1][j] == 0 && isPathClear(x1, y1, x1, j)) {

                if (showTrace && canConnect1Fold(x1, j, x2, y2, showTrace)) {
                    currentPath.add(0, new Point(x1, y1));
                    return true;
                }
                if (showTrace && canConnect1Fold(x1, j, x2, y2, showTrace)) {
                    return true;
                }
            }
        }


        for (int i = 0; i < numOfRows; i++) {
            if (i == x1) continue;

            if (map[i][y1] == 0 && isPathClear(x1, y1, i, y1)) {
                if (showTrace && canConnect1Fold(i, y1,x2, y2, true)) {
                    currentPath.add(0, new Point(x1, y1));
                    return true;
                }
                if (!showTrace && canConnect1Fold(i, y1, x2, y2, false)) {
                    return true;
                }
            }
        }

        return false;
    }


    public boolean isGameCleared() {
        if(hasConnectedCnt == 0){
            return true;
        }
        else{
            return false;
        }
    }
    public void controlPanelPrintln(){
        for(int i = 0;i<numOfRows;i++){
            for(int j = 0;j<numOfColumns;j++){
                System.out.print(map[i][j]+" ");
            }
            System.out.println();
        }
    }
    ScoreCounter cnt = new ScoreCounter();
    public boolean cellElimination(int x1, int y1, int x2, int y2, boolean applyGrav,BackgroundPanel bgPanel) {
        if(isValidConnection(x1,y1,x2,y2,true)) {

            int currentIcon = map[x1][y1];
            map[x1][y1] = 0;
            map[x2][y2] = 0;

            hasConnectedCnt--;
            /// ////////////////////
            // 3. 【关键修复】Combo 计算逻辑
            /*if (comboindex == 0) {
                // 游戏第一次消除
                comboindex = currentIcon;

                onCombo = true;
            } else {*/
                // 判断是否和上一次消除的图案一样
                if (currentIcon == comboindex) {
                    combocnt++;
                    System.out.println("连击次数加1");
                    onCombo = true; // 成功触发连击
                } else {
                    comboindex = currentIcon; // 换图案了，记录新图案
                    combocnt = 1;             // 连击中断
                    onCombo = false;
                }
                bgPanel.updateComboDisplay(combocnt);
            
                if(combocnt == 5){
                    bgPanel.triggerGoldenTime(this);
                }

                int baseScore = applyGrav? 120 : 100;
                int comboBonus = onCombo? (applyGrav? 80 : 50) : 0;
                int totalScore = baseScore + comboBonus;

                if(isGoldenTime){
                    totalScore *= 2;
                }

                cnt.addScore(totalScore);

                if(applyGrav){applyGravity();}
            /// ////////////////
//            if(applyGrav) {
//                cnt.addScore(120);
//                applyGravity();
//                if(onCombo){
//                    cnt.addScore(80);
//                }
//            }
//            if(!applyGrav) {cnt.addScore(100);
//                if(onCombo){
//                    cnt.addScore(50);
//                }
//            }

            return true;

        }
        else {
                System.out.println("无效连接");
                return false;
            }
        }




    public boolean chessboardSolvable(){
        ArrayList<Integer> x= new ArrayList<>();
        ArrayList<Integer> y= new ArrayList<>();
        for (int i = 0; i < numOfRows; i++) {
            for (int j = 0; j < numOfColumns; j++) {
                if(map[i][j] != 0){
                    x.add(i);
                    y.add(j);
                }
            }
        }
        for(int i = 0;i<x.size();i++){
            for(int j  = 0;j<y.size();j++){
            if(isValidConnection(x.get(i),y.get(i),x.get(j),y.get(j),false))
                return true;
            }

        }
        this.isSovable = false;
        return false;
    }




    //私有变量的setter
    public void setNumOfRows(int numOfRows) {
        this.numOfRows = numOfRows;
    }
    public void setNumOfColumns(int numOfColumns) {
        this.numOfColumns = numOfColumns;
    }
    public void setNumOficons(int numOficons) {
        this.numOficons = numOficons;
    }
    public void setMap(int[][] map) {
        this.map = map;

        int remainingPairs = 0;
        for (int i = 0; i < map.length; i++) {
            for (int j = 0; j < map[0].length; j++) {
                if(map[i][j] != 0){
                    remainingPairs++;
                }
            }           
        }
        this.hasConnectedCnt = remainingPairs/2;
    }
    public void setCell(int x,int y,int ic){
        map[x][y] = ic;
    }


    //私有变量的getters~
    public int getNumOfrows(){
        return numOfRows;
    }
    public int getNumOfcolumns(){
        return numOfColumns;
    }
    public int[][] getMap(){
        return map;
    }

    public boolean isGoldenTime(){return isGoldenTime;}

    public void setGoldenTime(boolean isGolden){isGoldenTime = isGolden;}

    public int getRemainingPairs() {
        return hasConnectedCnt;
    }
    public boolean getVictoryStatus(){
        return isSovable;
    }
}

