package manager;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class UserDataManager {
    private static final String FILE_PATH = "save/users.txt";


    public static void saveProgress(AccountInfo user, int currentScore, int currentTime, int diff, int type) {
        user.setBestScore(type, diff, currentScore);
        user.setShortestTime(type, diff, currentTime);
        user.incrementClearedLevels(type, diff);
        syncToFile(user);
    }

    private static void syncToFile(AccountInfo updatedUser) {
        File file = new File(FILE_PATH);
        List<String> lines = new ArrayList<>();

        try {

            if (!file.exists()) {
                AccountManager.init();
            }


            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    if (line.trim().isEmpty()) continue;


                    String[] parts = line.split(",");
                    if (parts[0].equals(updatedUser.getAccount())) {

                        lines.add(updatedUser.toFileString());
                    } else {

                        lines.add(line);
                    }
                }
            }


            try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
                for (String l : lines) {
                    writer.write(l);
                    writer.newLine();
                }
            }

            System.out.println("[数据存盘] 用户 " + updatedUser.getAccount() + " 的进度已保存。");

        } catch (IOException e) {
            System.err.println("保存数据时发生错误: " + e.getMessage());
            e.printStackTrace();
        }
    }
}