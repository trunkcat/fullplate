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

/**
 * Represents a possible ingredient in a recipe.
 */
public class Ingredient {
    protected final KitchenEntity.ID id;
    protected final int quantity;
    protected final FoodState state;
    protected final int priority;
    protected final boolean required;

    // Order details
    protected int cost = 0;
    protected float orderProcessingTime = 0f;

    // Cooking details
    protected float cookingTime = 0f;
    protected float overcookingTime = 0f;

    public Ingredient(KitchenEntity.ID id, int quantity, FoodState state, int priority, boolean required) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be positive");
        }
        if (priority < 0) {
            throw new IllegalArgumentException("Priority must be non-negative");
        }
        this.id = id;
        this.quantity = quantity;
        this.state = state;
        this.priority = priority;
        this.required = required;
    }

    public Ingredient(KitchenEntity.ID id, int quantity, FoodState state) {
        this(id, quantity, state, 0, true);
    }

    public KitchenEntity.ID getId() {
        return id;
    }

    public int getQuantity() {
        return quantity;
    }

    public FoodState getState() {
        return state;
    }

    public int getPriority() {
        return priority;
    }

    public boolean isBaseIngredient() {
        return priority == 0;
    }

    public boolean isRequired() {
        return required;
    }

    public boolean isOptional() {
        return !required;
    }

    public int getCost() {
        return cost;
    }

    public Ingredient setCost(int cost) {
        this.cost = cost;
        return this;
    }

    public float getOrderProcessingTime() {
        return orderProcessingTime;
    }

    public Ingredient setOrderProcessingTime(float orderProcessingTime) {
        this.orderProcessingTime = orderProcessingTime;
        return this;
    }

    public float getCookingTime() {
        return cookingTime;
    }

    public Ingredient setCookingTime(float cookingTime) {
        this.cookingTime = cookingTime;
        return this;
    }

    public float getOvercookingTime() {
        return overcookingTime;
    }

    public Ingredient setOvercookingTime(float overcookingTime) {
        this.overcookingTime = overcookingTime;
        return this;
    }

    public PartialIngredient partial() {
        return new PartialIngredient(id, quantity, state);
    }

    public Ingredient copy() {
        return copy(this.quantity);
    }

    public Ingredient copy(int quantity) {
        return new Ingredient(this.id, quantity, this.state, this.priority, this.required)
            .setCost(this.cost)
            .setCookingTime(this.cookingTime)
            .setOvercookingTime(this.overcookingTime)
            .setOrderProcessingTime(this.orderProcessingTime);
    }
}
