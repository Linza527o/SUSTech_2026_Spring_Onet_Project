package manager;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class AccountManager {
    private static final String DIR_PATH = "save";
    private static final String FILE_PATH = DIR_PATH + "/users.txt";

    private static List<AccountInfo> userList = new ArrayList<>();

    public static void init() {
        try {
            File dir = new File(DIR_PATH);
            if (!dir.exists()) dir.mkdirs();
            File file = new File(FILE_PATH);
            if (!file.exists()) file.createNewFile();

            userList.clear();
            try (BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    String[] parts = line.split(",");
                    if (parts.length >= 2) {
                        int avatarId = (parts.length > 2) ? Integer.parseInt(parts[2]) : 1;
                        
                        int[][] scores = new int[2][2];
                        int[][] times = {{-1, -1}, {-1, -1}}; // 默认-1
                        int[][] levels = new int[2][2];

                        // 【安全兼容机制】：智能判断是旧存档还是新存档
                        int index = 3;
                        for (int t = 0; t < 2; t++) {
                            for (int d = 0; d < 2; d++) {
                                scores[t][d] = (parts.length > index) ? Integer.parseInt(parts[index++]) : 0;
                                times[t][d]  = (parts.length > index) ? Integer.parseInt(parts[index++]) : -1;
                                levels[t][d] = (parts.length > index) ? Integer.parseInt(parts[index++]) : 0;
                            }
                        }
                        boolean isGuest = (parts.length > 12) ? Boolean.parseBoolean(parts[12]) : false;
                        userList.add(new AccountInfo(parts[0], parts[1], avatarId, scores, times, levels, isGuest));
                    }
                }
            }
        } catch (IOException | NumberFormatException e) { e.printStackTrace(); }
    }

    public static boolean register(String account, String password) {
        for (AccountInfo info : userList) {
            if (info.getAccount().equals(account)) return false;
        }
        // 新用户初始数据
        int[][] scores = new int[2][2];
        int[][] times = {{-1, -1}, {-1, -1}};
        int[][] levels = new int[2][2];
        
        AccountInfo newUser = new AccountInfo(account, password, 1, scores, times, levels, false);
        userList.add(newUser);

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH, true))) {
            writer.write(newUser.toFileString());
            writer.newLine();
            return true;
        } catch (IOException e) { return false; }
    }
    
    public static AccountInfo login(String account, String password) {
        for (AccountInfo info : userList) {
            if (info.getAccount().equals(account) && info.getPassword().equals(password)) return info;
        }
        return null;
    }

    public static void updateUser(AccountInfo updatedUser) {
        try {
            File file = new File(FILE_PATH);
            List<String> lines = new ArrayList<>();
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    if (!line.isEmpty() && line.split(",")[0].equals(updatedUser.getAccount())) {
                        lines.add(updatedUser.toFileString());
                    } else {
                        lines.add(line);
                    }
                }
            }
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
                for (String line : lines) { writer.write(line); writer.newLine(); }
            }
        } catch (IOException e) { e.printStackTrace(); }
    }

    public static void deleteAccount(String account){
        try{
            File file = new File(FILE_PATH);
            List<String> list = new ArrayList<>();
            try (BufferedReader reader = new BufferedReader(new FileReader(file))){
                String line;
                while ((line = reader.readLine()) != null){
                    if(!line.isEmpty() && !line.split(",")[0].equals(account)){
                        list.add(line);
                    }
                }
            }

            try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))){
                for (String line : list) {
                    writer.write(line);
                    writer.newLine();
                }
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        

    }

    // =========================================================
    // 排行榜方法 (增加了 type 和 diff 参数)
    // =========================================================
    public static List<AccountInfo> getLeaderboardByScore(int type, int diff) {
        List<AccountInfo> leaderboard = new ArrayList<>();
        for (AccountInfo info : userList) {
            if (info.getBestScore(type, diff) != 0) leaderboard.add(info);
        }
        leaderboard.sort((a, b) -> {
            if (b.getBestScore(type, diff) != a.getBestScore(type, diff)) 
                return Integer.compare(b.getBestScore(type, diff), a.getBestScore(type, diff));
            return a.getAccount().compareTo(b.getAccount());
        });
        return leaderboard;
    }

    public static List<AccountInfo> getLeaderboardByTime(int type, int diff) {
        List<AccountInfo> leaderboard = new ArrayList<>();
        for (AccountInfo info : userList) {
            if (info.getShortestTime(type, diff) != -1) leaderboard.add(info);
        }
        leaderboard.sort((a, b) -> {
            if (a.getShortestTime(type, diff) != b.getShortestTime(type, diff))
                return Integer.compare(a.getShortestTime(type, diff), b.getShortestTime(type, diff));
            return a.getAccount().compareTo(b.getAccount());
        });
        return leaderboard;
    }

    public static List<AccountInfo> getLeaderboardByLevels(int type, int diff) {
        List<AccountInfo> leaderboard = new ArrayList<>();
        for (AccountInfo info : userList) {
            if (info.getClearedLevels(type, diff) != 0) leaderboard.add(info);
        }
        leaderboard.sort((a, b) -> {
            if (b.getClearedLevels(type, diff) != a.getClearedLevels(type, diff))
                return Integer.compare(b.getClearedLevels(type, diff), a.getClearedLevels(type, diff));
            return a.getAccount().compareTo(b.getAccount());
        });
        return leaderboard;
    }

    public static int getRankByScore(AccountInfo user, int type, int diff) {
        List<AccountInfo> board = getLeaderboardByScore(type, diff);
        for (int i = 0; i < board.size(); i++) if (board.get(i).getAccount().equals(user.getAccount())) return i + 1;
        return -1;
    }

    public static int getRankByTime(AccountInfo user, int type, int diff) {
        List<AccountInfo> board = getLeaderboardByTime(type, diff);
        for (int i = 0; i < board.size(); i++) if (board.get(i).getAccount().equals(user.getAccount())) return i + 1;
        return -1;
    }

    public static int getRankByLevels(AccountInfo user, int type, int diff) {
        List<AccountInfo> board = getLeaderboardByLevels(type, diff);
        for (int i = 0; i < board.size(); i++) if (board.get(i).getAccount().equals(user.getAccount())) return i + 1;
        return -1;
    }

    // --- 为了不让你之前的 LeaderboardPanel 报错，保留旧的默认方法（默认返回普通简单模式） ---
    public static List<AccountInfo> getLeaderboardByScore() { return getLeaderboardByScore(0, 0); }
    public static List<AccountInfo> getLeaderboardByTime() { return getLeaderboardByTime(0, 0); }
    public static List<AccountInfo> getLeaderboardByLevels() { return getLeaderboardByLevels(0, 0); }
    public static int getRankByScore(AccountInfo user) { return getRankByScore(user, 0, 0); }
    public static int getRankByTime(AccountInfo user) { return getRankByScore(user, 0, 0); } // 占位
    public static int getRankByLevels(AccountInfo user) { return getRankByScore(user, 0, 0); } // 占位
}