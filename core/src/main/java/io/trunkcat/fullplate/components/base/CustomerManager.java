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

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.utils.Array;

import java.util.Arrays;

import io.trunkcat.fullplate.components.common.StageActor;
import io.trunkcat.fullplate.screens.restaurant.LevelScreen;
import io.trunkcat.fullplate.utilities.Constants;

public class CustomerManager extends StageActor {
    private final Array<Seat> seats;
    private final FoodCombinationsManager combinationsManager;
    private final Array<Order> ordersLeft = new Array<>();
    private float spawnCooldown = 0f;

    private float customersSpawned = 0;
    private float customersDespawned = 0;

    public CustomerManager(
        Vector2[] seatPositions,
        FoodCombinationsManager combinationsManager,
        LevelScreen.LevelData levelData,
        LevelScreen.LevelProgress levelProgress
    ) {
        if (seatPositions.length == 0) {
            throw new IllegalArgumentException("At least one seat is required for manager to act.");
        }
        if (combinationsManager.getCombinations().isEmpty()) {
            throw new IllegalArgumentException("Combination manager must have at least one combination");
        }

        seats = new Array<>(seatPositions.length);
        for (Vector2 position : seatPositions) {
            seats.add(new Seat(position));
        }
        this.combinationsManager = combinationsManager;

        // Orders are supposed to be delivered at least by customer neutral emotion.
        // That way we get some tip which we can add to the total.
        // TODO: is this really needed? decide. if so, add to total when order is generated.
        final int EXPECTED_TIP_PER_ORDER = Customer.Emotion.NEUTRAL.getTip();
        final int HIGHEST_TIP_POSSIBLE = Arrays.stream(Customer.Emotion.values())
            .mapToInt(Customer.Emotion::getTip)
            .max()
            .orElse(0); // TODO: consider restaurant tip modifier (more interior, more tips)

        final int goal = levelData.getCoinsGoal() + Constants.GOAL_OFFSET;

        int totalPrice = 0;
        while (totalPrice <= goal) {
            Order order = generateOrder();
            totalPrice += order.getPrice();
            ordersLeft.add(order);
        }

        levelProgress.setMaximumPossibleCoins(totalPrice);
        levelProgress.setMaximumPossibleTips(ordersLeft.size * HIGHEST_TIP_POSSIBLE);
    }

    @Override
    public void act(float delta) {
        super.act(delta);

        spawnCustomer();
    }

    @Override
    public boolean handle(Event event) {
        super.handle(event);

        if (event instanceof CustomerEvent.CustomerLeftEvent) {
            CustomerEvent.CustomerLeftEvent e = (CustomerEvent.CustomerLeftEvent) event;
            this.customersDespawned += 1;

            if (customersDespawned == customersSpawned && ordersLeft.isEmpty()) {
                dispatchStageEvent(new LevelEvent.LevelCompletedEvent());
            }

            Gdx.app.log("e", customersDespawned + " " + customersSpawned);
            return true;
        }

        return false;
    }

    private Array<Seat> getAvailableSeats() {
        Array<Seat> availableSeats = new Array<>();
        for (Seat seat : new Array.ArrayIterator<>(seats)) {
            if (!seat.isOccupied()) {
                availableSeats.add(seat);
            }
        }
        return availableSeats;
    }

    private void spawnCustomer() {
        if (ordersLeft.isEmpty()) {
            return;
        }

        float delta = Gdx.graphics.getDeltaTime();
        spawnCooldown -= delta;

        if (spawnCooldown > 0f) {
            return;
        }

        Array<Seat> availableSeats = getAvailableSeats();
        if (availableSeats.isEmpty()) {
            return;
        }

        int numberOfOrders = Math.min(
            ordersLeft.size,
            MathUtils.random(
                Constants.MIN_ORDERS_PER_CUSTOMER,
                Constants.MAX_ORDERS_PER_CUSTOMER
            )
        );
        Array<Order> orders = new Array<>(numberOfOrders);
        for (int i = 0; i < numberOfOrders; i++) {
            int orderIndex = MathUtils.random(0, ordersLeft.size - 1);
            orders.add(ordersLeft.removeIndex(orderIndex));
        }

        Seat seat = availableSeats.random();
        Customer customer = new Customer(Customer.Type.NORMAL, seat, orders);
        this.customersSpawned += 1;
        getStage().addActor(customer);
        seat.occupy();

        spawnCooldown = MathUtils.random(
            Constants.MIN_CUSTOMER_SPAWN_COOLDOWN,
            Constants.MAX_CUSTOMER_SPAWN_COOLDOWN
        );
    }

    private Order generateOrder() {
        FoodCombination[] combinations = combinationsManager
            .getCombinations()
            .values()
            .toArray(new FoodCombination[0]);

        int randomIndex = MathUtils.random(0, combinations.length - 1);
        FoodCombination chosenCombination = combinations[randomIndex];

        Array<FoodCombination.Ingredient> generatedIngredients = chosenCombination.generateSatisfiable();
        FoodCombination orderCombination = new FoodCombination(
            chosenCombination.getResultItemId(),
            chosenCombination.getResultState(),
            chosenCombination.getIngredientsRenderer()
        );
        for (FoodCombination.Ingredient ingredient : new Array.ArrayIterator<>(generatedIngredients)) {
            orderCombination.addIngredient(ingredient);
        }
        return new Order(orderCombination);
    }
}
