/*
 * Copyright (c) 2024-2025 Trunk Cat Studios
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package io.trunkcat.fullplate.screens.restaurant;

public class LevelProgress {
    private int maximumPossibleCoins;
    private int maximumPossibleTips;
    private int coins = 0;
    private int tip = 0;
    private int experiencePoints = 0;

    private int customersSpawned = 0;
    private int customersDespawned = 0;
    private int customersServed = 0;
    private int customersFailed = 0;
    private int ordersServed = 0;
    private int ordersFailed = 0;

    public int getCoins() {
        return coins;
    }

    public void setCoins(int coins) {
        this.coins = coins;
    }

    public int getTip() {
        return tip;
    }

    public void setTip(int tips) {
        this.tip = tips;
    }

    public int getExperiencePoints() {
        return experiencePoints;
    }

    public void setExperiencePoints(int experiencePoints) {
        this.experiencePoints = experiencePoints;
    }

    public int getCustomersSpawned() {
        return customersSpawned;
    }

    public void setCustomersSpawned(int customersSpawned) {
        this.customersSpawned = customersSpawned;
    }

    public int getCustomersDespawned() {
        return customersDespawned;
    }

    public void setCustomersDespawned(int customersDespawned) {
        this.customersDespawned = customersDespawned;
    }

    public int getCustomersServed() {
        return customersServed;
    }

    public void setCustomersServed(int customersServed) {
        this.customersServed = customersServed;
    }

    public int getCustomersFailed() {
        return customersFailed;
    }

    public void setCustomersFailed(int customersFailed) {
        this.customersFailed = customersFailed;
    }

    public int getOrdersServed() {
        return ordersServed;
    }

    public void setOrdersServed(int ordersServed) {
        this.ordersServed = ordersServed;
    }

    public int getOrdersFailed() {
        return ordersFailed;
    }

    public void setOrdersFailed(int ordersFailed) {
        this.ordersFailed = ordersFailed;
    }

    public int getMaximumPossibleCoins() {
        return maximumPossibleCoins;
    }

    public void setMaximumPossibleCoins(int maximumPossibleCoins) {
        this.maximumPossibleCoins = maximumPossibleCoins;
    }

    public int getMaximumPossibleTips() {
        return maximumPossibleTips;
    }

    public void setMaximumPossibleTips(int maximumPossibleTips) {
        this.maximumPossibleTips = maximumPossibleTips;
    }
}
