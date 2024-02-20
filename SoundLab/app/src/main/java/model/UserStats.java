package model;

public class UserStats {
    private String username;
    private String preferredTime;
    private int totalListens;

    public UserStats(String username, String preferredTime, int totalListens) {
        this.username = username;
        this.preferredTime = preferredTime;
        this.totalListens = totalListens;
    }

    public String getUsername() {
        return username;
    }

    public String getPreferredTime() {
        return preferredTime;
    }

    public int getTotalListens() {
        return totalListens;
    }
}
