package app;

import manager.AccountManager;

import ui.*;
import utils.FontManager;
import utils.SoundManager;
//test sentence
//test sentence
//test sentence
//this is a test sentence aiming at testing the synchornization function of github to help collaborate
//succeeded attempt to synchornize
public class Main {
    public static void main(String[] args) {
        ChessBoard chessBoard = new ChessBoard(10,10,5, false);
        chessBoard.gameSetting();
        chessBoard.controlPanelPrintln();
//        调试用句
//                Scanner sc = new Scanner(System.in);
//        while(true){
//            int x1 = sc.nextInt();
//            int y1 = sc.nextInt();
//            int x2 = sc.nextInt();
//            int y2 = sc.nextInt();
//            if(x1 == -1){break;}
//            else{
//                chessBoard.cellElimination(x1,y1,x2,y2);
//                chessBoard.applyGravity();
//                chessBoard.controlPanelPrintln();
//            }
//
//        }

        // 1. 加载像素风字体（必须在创建任何界面之前）
        FontManager.loadFonts();

        SoundManager.preLoadSounds();

        // 2. 加载账号数据（必须在创建登录界面之前）
        AccountManager.init();


        new ui.StartFrame().setVisible(true);
    }
}
