package com.oheers.fish.database.model.user;

import java.util.UUID;

public class UserReport {

    private int id;
    private final UUID uuid;
    private String firstFish;
    private String recentFish;
    private String largestFish;
    private String shortestFish;
    private int numFishCaught;
    private int competitionsWon;
    private int competitionsJoined;

    private float largestLength;
    private float shortestLength;
    private float totalFishLength;
    
    private int fishSold;
    private double moneyEarned;

    public UserReport(int id, UUID uuid, String firstFish, String recentFish, String largestFish, String shortestFish, int numFishCaught, int competitionsWon, int competitionsJoined, float largestLength, float shortestLength, float totalFishLength, int fishSold, double moneyEarned) {
        this.id = id;
        this.uuid = uuid;
        this.firstFish = firstFish;
        this.recentFish = recentFish;
        this.largestFish = largestFish;
        this.shortestFish = shortestFish;
        this.numFishCaught = numFishCaught;
        this.competitionsWon = competitionsWon;
        this.competitionsJoined = competitionsJoined;
        this.largestLength = largestLength;
        this.shortestLength = shortestLength;
        this.totalFishLength = totalFishLength;
        this.fishSold = fishSold;
        this.moneyEarned = moneyEarned;
    }

    public UserReport(UUID uuid, String firstFish, String recentFish, String largestFish, String shortestFish, int numFishCaught, int competitionsWon, int competitionsJoined, float largestLength, float shortestLength, float totalFishLength, int fishSold, double moneyEarned) {
        this.uuid = uuid;
        this.firstFish = firstFish;
        this.recentFish = recentFish;
        this.largestFish = largestFish;
        this.shortestFish = shortestFish;
        this.numFishCaught = numFishCaught;
        this.competitionsWon = competitionsWon;
        this.competitionsJoined = competitionsJoined;
        this.largestLength = largestLength;
        this.shortestLength = shortestLength;
        this.totalFishLength = totalFishLength;
        this.fishSold = fishSold;
        this.moneyEarned = moneyEarned;
    }

    public String getFirstFish() {
        return firstFish;
    }

    public void setFirstFish(String firstFish) {
        this.firstFish = firstFish;
    }

    public String getRecentFish() {
        return recentFish;
    }

    public void setRecentFish(String recentFish) {
        this.recentFish = recentFish;
    }

    public String getLargestFish() {
        return largestFish;
    }

    public void setLargestFish(String largestFish) {
        this.largestFish = largestFish;
    }

    public int getNumFishCaught() {
        return numFishCaught;
    }

    public void setNumFishCaught(int numFishCaught) {
        this.numFishCaught = numFishCaught;
    }

    public int getCompetitionsWon() {
        return competitionsWon;
    }

    public void setCompetitionsWon(int competitionsWon) {
        this.competitionsWon = competitionsWon;
    }

    public int getCompetitionsJoined() {
        return competitionsJoined;
    }

    public void setCompetitionsJoined(int competitionsJoined) {
        this.competitionsJoined = competitionsJoined;
    }

    public float getTotalFishLength() {
        return totalFishLength;
    }

    public void setTotalFishLength(float totalFishLength) {
        this.totalFishLength = totalFishLength;
    }

    public float getLargestLength() {
        return largestLength;
    }

    public void setLargestLength(float largestLength) {
        this.largestLength = largestLength;
    }

    /**
     * Increases the number of fish caught by a set amount.
     *
     * @param magnitude How many fish to increase the number caught by.
     */
    public void incrementFishCaught(final int magnitude) {
        this.numFishCaught += magnitude;
    }

    /**
     * Increases the total length of fish the player has caught.
     *
     * @param magnitude The size to increase the total size by.
     */
    public void incrementTotalLength(final float magnitude) {
        this.totalFishLength += magnitude;
    }

    /**
     * Adds a specified number to the number of competitions the user has joined.
     *
     * @param magnitude The size to increase the total number joined by.
     */
    public void incrementCompetitionsJoined(final int magnitude) {
        this.competitionsJoined += magnitude;
    }

    /**
     * Adds a specified number to the number of competitions the user has won.
     *
     * @param magnitude The size to increase the total number won by.
     */
    public void incrementCompetitionsWon(final int magnitude) {
        this.competitionsWon += magnitude;
    }

    public int getId() {
        return id;
    }

    public UUID getUUID() {
        return uuid;
    }
    
    public void incrementFishSold(final int fishSold) {
        this.fishSold += fishSold;
    }
    
    public void incrementMoneyEarned(final double moneyEarned) {
        this.moneyEarned += moneyEarned;
    }
    
    public int getFishSold() {
        return fishSold;
    }
    
    public double getMoneyEarned() {
        return moneyEarned;
    }

    public String getShortestFish() {
        return shortestFish;
    }

    public float getShortestLength() {
        return shortestLength;
    }

    public void setShortestFish(String shortestFish) {
        this.shortestFish = shortestFish;
    }

    public void setShortestLength(float shortestLength) {
        this.shortestLength = shortestLength;
    }
}
