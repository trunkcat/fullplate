package io.trunkcat.fullplate.models;

import io.trunkcat.fullplate.models.responses.PlayerStats;

public class PlayerData {
    private int id;
    private String username;
    private PlayerStats stats;

    public int getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public PlayerStats getStats() {
        return stats;
    }
}
