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

import com.badlogic.gdx.scenes.scene2d.Event;

public abstract class CustomerEvent extends Event {
    private final Customer customer;

    public CustomerEvent(Customer customer) {
        this.customer = customer;
    }

    public Customer getCustomer() {
        return customer;
    }

    public static class CustomerPositionChangeEvent extends CustomerEvent {
        private final Customer.PositionState prevState;
        private final Customer.PositionState currentState;

        public CustomerPositionChangeEvent(
            Customer customer,
            Customer.PositionState prevState,
            Customer.PositionState currentState
        ) {
            super(customer);
            this.prevState = prevState;
            this.currentState = currentState;
        }

        public Customer.PositionState getPrevState() {
            return prevState;
        }

        public Customer.PositionState getCurrentState() {
            return currentState;
        }
    }

    public static class OrderOfferEvent extends CustomerEvent {
        private final IngredientAssembly assembly;
        private final Recipe matchingOrder;

        public OrderOfferEvent(Customer customer, IngredientAssembly assembly, Recipe matchingOrder) {
            super(customer);
            this.matchingOrder = matchingOrder;
            this.assembly = assembly;
        }

        public IngredientAssembly getAssembly() {
            return assembly;
        }

        public Recipe getMatchingOrder() {
            return matchingOrder;
        }
    }

    public static class OrderWithdrawEvent extends CustomerEvent {
        private final Recipe matchingOrder;
        private final IngredientAssembly assembly;

        public OrderWithdrawEvent(Customer customer, IngredientAssembly assembly, Recipe matchingOrder) {
            super(customer);
            this.matchingOrder = matchingOrder;
            this.assembly = assembly;
        }

        public IngredientAssembly getAssembly() {
            return assembly;
        }

        public Recipe getMatchingOrder() {
            return matchingOrder;
        }
    }

    public static class OrderAcceptEvent extends CustomerEvent {
        private final IngredientAssembly assembly;

        public OrderAcceptEvent(Customer customer, IngredientAssembly assembly) {
            super(customer);
            this.assembly = assembly;
        }

        public IngredientAssembly getAssembly() {
            return assembly;
        }
    }

    public static class OrderFulfillEvent extends CustomerEvent {
        private final Recipe order;

        public OrderFulfillEvent(Customer customer, Recipe order) {
            super(customer);
            this.order = order;
        }

        public Recipe getOrder() {
            return order;
        }
    }
}
