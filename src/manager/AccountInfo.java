package manager;

public class AccountInfo {
    private String account;
    private String password;
    private int avatarId;

    // 使用二维数组存储 4 种模式的数据
    // [0=普通, 1=重力] [0=简单, 1=困难]
    private int[][] bestScore = new int[2][2];
    private int[][] shortestTime = new int[2][2];
    private int[][] clearedLevels = new int[2][2];
    private boolean isGuest;

    public AccountInfo(String account, String password, int avatarId, 
                       int[][] bestScore, int[][] shortestTime, int[][] clearedLevels, boolean isGuest) {
        this.account = account;
        this.password = password;
        this.avatarId = avatarId;
        this.bestScore = bestScore;
        this.shortestTime = shortestTime;
        this.clearedLevels = clearedLevels;
        this.isGuest = isGuest;
    }

    public String getAccount() { return account; }
    public String getPassword() { return password; }
    public int getAvatarId() { return avatarId; }
    public void setAvatarId(int avatarId) { this.avatarId = avatarId; }
    public boolean getIsGuest() {return isGuest;}
    public void setIsGuest(boolean isGuest) {this.isGuest = isGuest;}

    public int getBestScore(int type, int diff) { return bestScore[type][diff]; }
    public int getShortestTime(int type, int diff) { return shortestTime[type][diff]; }
    public int getClearedLevels(int type, int diff) { return clearedLevels[type][diff]; }

    public void setBestScore(int type, int diff, int newScore) {
        if (newScore > this.bestScore[type][diff]) {
            this.bestScore[type][diff] = newScore;
        }
    }

    public void setShortestTime(int type, int diff, int newTime) {
        if (this.shortestTime[type][diff] == -1 || newTime < this.shortestTime[type][diff]) {
            this.shortestTime[type][diff] = newTime;
        }
    }

    public void incrementClearedLevels(int type, int diff) {
        this.clearedLevels[type][diff]++;
    }

    public String getFormattedShortestTime(int type, int diff) {
        int time = shortestTime[type][diff];
        if (time == -1) return "--:--:--";
        int hours = time / 3600;
        int minutes = (time % 3600) / 60;
        int seconds = time % 60;
        return String.format("%02d:%02d:%02d", hours, minutes, seconds);
    }

    public String toFileString() {
        StringBuilder sb = new StringBuilder();
        sb.append(account).append(",").append(password).append(",").append(avatarId);
        // 依次存入：普通简单, 普通困难, 重力简单, 重力困难
        for (int t = 0; t < 2; t++) {
            for (int d = 0; d < 2; d++) {
                sb.append(",").append(bestScore[t][d])
                  .append(",").append(shortestTime[t][d])
                  .append(",").append(clearedLevels[t][d]);
            }
        }
        sb.append(",").append(isGuest);
        return sb.toString();
    }
}