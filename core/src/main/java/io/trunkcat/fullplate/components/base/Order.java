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

package io.trunkcat.fullplate.components.base;

import com.badlogic.gdx.utils.Array;

import io.trunkcat.fullplate.utilities.Constants;

public class Order {
    private final FoodCombination combination;
    private final int price;
    private final float processingTime;

    public Order(FoodCombination combination) {
        this.combination = combination;
        this.price = combination.getPrice();
        this.processingTime = combination.getProcessingTime() + Constants.ADDITIONAL_PROCESSING_TIME; // TODO: for now
    }

    public FoodCombination getCombination() {
        return combination;
    }

    public int getPrice() {
        return price;
    }

    public float getProcessingTime() {
        return processingTime;
    }

    public boolean canBeSatisfied(Array<FoodCombination.PartialIngredient> ingredients) {
        return combination.isSatisfied(ingredients);
    }
}
