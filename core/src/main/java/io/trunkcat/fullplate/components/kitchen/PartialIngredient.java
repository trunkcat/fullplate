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

package io.trunkcat.fullplate.components.kitchen;

public class PartialIngredient {
    private final KitchenEntity.ID id;
    private int quantity;
    private FoodState state;
    private final int level;
    private final int maxLevel;

    public PartialIngredient(KitchenEntity.ID id, int quantity, FoodState state) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be positive");
        }
        this.id = id;
        this.quantity = quantity;
        this.state = state;
        // todo: get level from game data.
        this.level = 0;
        this.maxLevel = 1;
    }

    public KitchenEntity.ID getId() {
        return id;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public FoodState getState() {
        return state;
    }

    public void setState(FoodState state) {
        this.state = state;
    }

    public int getLevel() {
        return level;
    }

    public int getMaxLevel() {
        return maxLevel;
    }
}
