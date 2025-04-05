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

import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.utils.Array;

public class CustomerEvent extends Event {
    protected final Customer customer;

    public CustomerEvent(Customer customer) {
        this.customer = customer;
    }

    public Customer getCustomer() {
        return customer;
    }

    public static class CombinationOfferEvent extends CustomerEvent {
        private final Array<FoodCombination.PartialIngredient> ingredients;

        private final boolean matchingOrder;

        public CombinationOfferEvent(Customer customer, Array<FoodCombination.PartialIngredient> ingredients, boolean matchingOrder) {
            super(customer);
            this.ingredients = ingredients;
            this.matchingOrder = matchingOrder;
        }

        public Array<FoodCombination.PartialIngredient> getIngredients() {
            return ingredients;
        }

        public boolean hasMatchingOrder() {
            return matchingOrder;
        }
    }

    public static class CombinationWithdrawEvent extends CustomerEvent {
        private final Array<FoodCombination.PartialIngredient> ingredients;
        private final boolean matchingOrder;

        public CombinationWithdrawEvent(Customer customer, Array<FoodCombination.PartialIngredient> ingredients, boolean matchingOrder) {
            super(customer);
            this.ingredients = ingredients;
            this.matchingOrder = matchingOrder;
        }

        public Array<FoodCombination.PartialIngredient> getIngredients() {
            return ingredients;
        }

        public boolean hasMatchingOrder() {
            return matchingOrder;
        }
    }

    public static class CombinationAcceptEvent extends CustomerEvent {
        private final Array<FoodCombination.PartialIngredient> ingredients;

        public CombinationAcceptEvent(Customer customer, Array<FoodCombination.PartialIngredient> ingredients) {
            super(customer);
            this.ingredients = ingredients;
        }

        public Array<FoodCombination.PartialIngredient> getIngredients() {
            return ingredients;
        }
    }

    public static class OrderFulfillEvent extends CustomerEvent {
        private final Order order;

        public OrderFulfillEvent(Customer customer, Order order) {
            super(customer);
            this.order = order;
        }

        public Order getOrder() {
            return order;
        }
    }

    public static class CustomerLeftEvent extends CustomerEvent {
        public CustomerLeftEvent(Customer customer) {
            super(customer);
        }
    }
}
