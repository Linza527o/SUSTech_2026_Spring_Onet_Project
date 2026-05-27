package manager;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class GameSaveManager {
    public static String getDirOfSave(int type){
        return (type == 1)? "gravitysaves" : "save";
    }

    public static void saveGame(String account, int[][] map, int score, int time,boolean applyGrav,int type, int diff) throws IOException {
        String dirOfSave = getDirOfSave(type);
        File file = new File(dirOfSave);
        if (!file.exists()) {
            file.mkdirs();
        }

        File saveFile = new File(dirOfSave + "/" + account + "_" + type + "_" + diff + "_save.txt");
        if(!saveFile.exists()) {
            saveFile.createNewFile();
        }
        try (PrintWriter writer = new PrintWriter(new FileWriter(saveFile))) {
            writer.println(score + "," + time);


            for (int[] row : map) {
                StringBuilder rowStr = new StringBuilder();
                for (int i = 0; i < row.length; i++) {
                    rowStr.append(row[i]).append(i == row.length - 1 ? "" : ",");
                }
                writer.println(rowStr.toString());
            }
            System.out.println("存档成功：" + saveFile.getPath());
        }
    }

    public static void saveInitialState(String account, int[][] map, int score, int time, boolean applyGrav, int type, int diff) throws IOException{
        String dirOfSave = getDirOfSave(type);
        File file = new File(dirOfSave);
        if(!file.exists()){
            file.mkdirs();
        }

        File initFile = new File(dirOfSave + "/" + account + "_" + type + "_" + diff + "_initial.txt");
        if(!initFile.exists()){
            initFile.createNewFile();
        }
        try (PrintWriter writer = new PrintWriter(new FileWriter(initFile))){
            writer.println(score + "," + time);

            for(int[] row : map){
                StringBuilder rowStr = new StringBuilder();
                for (int i = 0; i < row.length; i++) {
                    rowStr.append(row[i]).append(i == row.length? "" : ",");
                }
                writer.println(rowStr.toString());
            }
            System.out.println("初始战局信息保存成功：" + initFile.getPath());
        }
    }



    public static class SaveData {
        public int[][] map;
        public int score;
        public int timeLeft;

        public SaveData(int[][] map, int score, int time) {
            this.map = map;
            this.score = score;
            this.timeLeft = time;
        }

    }

    public SaveData loadGame(String account, int type, int diff) {
        String dirOfSave = getDirOfSave(type);
        boolean isValidSave = true;
        File saveFile = new File(dirOfSave + "/" + account + "_" + type + "_" + diff + "_save.txt");
        if (!saveFile.exists()) {
            return null;
        }
        try (BufferedReader reader = new BufferedReader(new FileReader(saveFile))) {
            // 1. 读取基础信息
            String[] firstLine = reader.readLine().split(",");
            if(firstLine.length != 2){
                System.err.println("未正确读取分数或时间！");
                isValidSave = false;
                //return null;
            }
            int score = Integer.parseInt(firstLine[0]);
            int timeConsumed = Integer.parseInt(firstLine[1]);

            // 2. 读取棋盘
            ArrayList<int[]> mapList = new ArrayList<>();
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if(parts.length != 10){
                    System.err.println("存档格式错误！");
                    isValidSave = false;
                    //return null;
                }
                int[] row = new int[parts.length];
                for (int i = 0; i < parts.length; i++) {
                    row[i] = Integer.parseInt(parts[i]);
                }
                mapList.add(row);

                //return null;
            }
            if(mapList.size() != 10){
                System.err.println("存档格式错误！");
                isValidSave = false;
            }
            //mapList转换到二维数组🚬这太难想了
            //666 读取的来了
            int[][] map = mapList.toArray(new int[0][]);
            if(isValidSave){
                return new SaveData(map, score, timeConsumed);
            }

            //return 一个saveData 类的 东西 存有数据
        } catch (FileNotFoundException e) {
            System.out.println("找不到文档了🚬");
            isValidSave = false;
            //return null;

        } catch (IOException e) {
            System.out.println("运行超时了🚬");
            //throw new RuntimeException(e);
            //return null;
        } catch (Exception e) {
            System.err.println("存档损坏或被篡改！");
            deleteSave(account, type, diff);
            //throw new RuntimeException(e);
            isValidSave = false;
            //return null;
        }
        if(!isValidSave){
            System.err.println("检测到存档被破坏，自动删除存档" + saveFile.getName());
            saveFile.delete();
        }
        return null;
    }

    public SaveData loadInitialState(String account, int type, int diff) {
        String dirOfSave = getDirOfSave(type);
        File initFile = new File(dirOfSave + "/" + account + "_" + type + "_" + diff + "_initial.txt");

        if (!initFile.exists()) return null;

        try (BufferedReader reader = new BufferedReader(new FileReader(initFile))) {
            String firstLineStr = reader.readLine();
            if (firstLineStr == null || firstLineStr.trim().isEmpty()) return null;

            String[] firstLine = firstLineStr.split(",");
            if (firstLine.length != 2) return null;

            int score = Integer.parseInt(firstLine[0]);
            int timeConsumed = Integer.parseInt(firstLine[1]);

            ArrayList<int[]> mapList = new ArrayList<>();
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                String[] parts = line.split(",");
                if (parts.length != 10) return null;
                int[] row = new int[parts.length];
                for (int i = 0; i < parts.length; i++) row[i] = Integer.parseInt(parts[i]);
                mapList.add(row);
            }
            if (mapList.size() != 10) return null;

            int[][] map = mapList.toArray(new int[0][]);
            return new SaveData(map, score, timeConsumed);
        } catch (Exception e) {
            return null; // 如果坏档了，直接返回null
        }
    }

    public static void deleteSave(String account, int type, int diff) {
        // 1. 补全 type 和 diff 参数，确保文件名与保存、读取时完全一致
        File saveFile = new File(getDirOfSave(type) + "/" + account + "_" + type + "_" + diff + "_save.txt");

        // 2. 判断文件是否存在，存在则删除并输出提示
        if (saveFile.exists()) {
            boolean isDeleted = saveFile.delete();
            if (isDeleted) {
                System.out.println("存档已成功删除：" + saveFile.getPath());
            } else {
                System.out.println("删除存档失败，可能文件正被占用：" + saveFile.getPath());
            }
        }
        //else {
        //    System.out.println("未找到该存档，无需删除：" + saveFile.getPath());
        //}
    }

    public static void deleteInitialState(String account){
        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < 2; j++) {
                File initialFile = new File(getDirOfSave(i) + "/" + account + "_" + i + "_" + j + "_initial.txt");

                if(initialFile.exists()){
                    boolean isDeleted = initialFile.delete();
                    if (isDeleted) {
                        System.out.println("存档已成功删除：" + initialFile.getPath());
                    } else {
                        System.out.println("删除存档失败，可能文件正被占用：" + initialFile.getPath());
                    }
                }
                // else {
                //    System.out.println("未找到该存档，无需删除：" + initialFile.getPath());
                //}

            }

        }
    }
}