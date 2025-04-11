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

import static io.trunkcat.fullplate.components.debug.DebugLabelStyles.label;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Array;

import java.util.Arrays;

import io.trunkcat.fullplate.components.common.System;
import io.trunkcat.fullplate.screens.restaurant.LevelData;
import io.trunkcat.fullplate.screens.restaurant.LevelEvent;
import io.trunkcat.fullplate.screens.restaurant.LevelProgress;
import io.trunkcat.fullplate.utilities.Constants;

public class CustomerSystem extends System {
    private final Array<Seat> seats;
    private final RecipeCollection recipeCollection;

    private final Array<Recipe> ordersLeft;
    private float spawnCooldown = 0f;

    private final LevelProgress levelProgress;

    public CustomerSystem(
        final RecipeCollection recipeCollection,
        final LevelData levelData,
        final LevelProgress levelProgress
    ) {
        if (recipeCollection.getRecipes().isEmpty()) {
            throw new IllegalArgumentException("Needs at least one recipe to generate orders");
        }
        boolean hasRecipeWithCost = recipeCollection.getRecipes()
            .values()
            .stream()
            .anyMatch(recipe -> recipe.getCost() > 0);
        if (!hasRecipeWithCost) {
            throw new IllegalArgumentException("Recipe collection has no recipe with cost > 0.");
        }

        this.seats = new Array<>();
        this.recipeCollection = recipeCollection;
        this.ordersLeft = new Array<>();

        this.levelProgress = levelProgress;

        final int HIGHEST_TIP_POSSIBLE = Arrays.stream(Customer.Emotion.values())
            .mapToInt(Customer.Emotion::getTip)
            .max()
            .orElse(0);

        final int offsetGoal = levelData.getCoinsGoal() + Constants.GOAL_OFFSET;

        int totalCost = 0;
        while (totalCost <= offsetGoal) {
            Recipe recipe = generateOrder();
            totalCost += recipe.getCost();
            ordersLeft.add(recipe);
        }

        levelProgress.setMaximumPossibleCoins(totalCost);
        levelProgress.setMaximumPossibleCoins(ordersLeft.size * HIGHEST_TIP_POSSIBLE);

        spawnCooldown = MathUtils.random(
            Constants.MIN_CUSTOMER_SPAWN_COOLDOWN,
            Constants.MAX_CUSTOMER_SPAWN_COOLDOWN
        );
    }

    public void addSeat(Seat seat) {
        seats.add(seat);
    }

    private Array<Seat> getAvailableSeats() {
        Array<Seat> availableSeats = new Array<>();
        for (Seat seat : new Array.ArrayIterator<>(seats)) {
            if (!seat.isOccupied()) availableSeats.add(seat);
        }
        return availableSeats;
    }

    private Recipe generateOrder() {
        return new Array<>(recipeCollection.getRecipes()
            .values()
            .toArray(new Recipe[0]))
            .random()
            .generateSubset();
    }

    private void spawnCustomer() {
        if (ordersLeft.isEmpty()) return;

        float delta = Gdx.graphics.getDeltaTime();
        spawnCooldown -= delta;

        if (spawnCooldown > 0f) return;

        Array<Seat> availableSeats = getAvailableSeats();
        if (availableSeats.isEmpty()) return;

        int numberOfOrders = Math.min(
            ordersLeft.size,
            MathUtils.random(
                Constants.MIN_ORDERS_PER_CUSTOMER,
                Constants.MAX_ORDERS_PER_CUSTOMER
            )
        );

        Array<Recipe> orders = new Array<>(numberOfOrders);
        for (int i = 0; i < numberOfOrders; i++) {
            int orderIndex = MathUtils.random(0, ordersLeft.size - 1);
            orders.add(ordersLeft.removeIndex(orderIndex));
        }

        Seat assignedSeat = availableSeats.random();
        Customer customer = new Customer(Customer.Type.NORMAL, assignedSeat, orders);
        assignedSeat.setOccupied(true);
        getStage().addActor(customer);

        spawnCooldown = MathUtils.random(
            Constants.MIN_CUSTOMER_SPAWN_COOLDOWN,
            Constants.MAX_CUSTOMER_SPAWN_COOLDOWN
        );
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        spawnCustomer();
    }

    @Override
    public boolean handle(Event event) {
        if (event instanceof CustomerEvent.CustomerPositionChangeEvent) {
            CustomerEvent.CustomerPositionChangeEvent e = (CustomerEvent.CustomerPositionChangeEvent) event;

            switch (e.getCurrentState()) {
                case SPAWNED:
                    levelProgress.setCustomersSpawned(levelProgress.getCustomersSpawned() + 1);
                    return true;

                case LEFT:
                    levelProgress.setCustomersDespawned(levelProgress.getCustomersDespawned() + 1);

                    if (levelProgress.getCustomersSpawned() == levelProgress.getCustomersDespawned() &&
                        ordersLeft.isEmpty()
                    ) {
                        dispatchStageEvent(new LevelEvent.LevelCompletedEvent());
                    }
                    return true;
            }
        }

        return super.handle(event);
    }

    @Override
    public Table getDebugTable() {
        Table table = super.getDebugTable();

        table.add(label("orders left: " + ordersLeft.size)).row();
        table.add(label("spawning next: " + (ordersLeft.isEmpty() ? "no need" : spawnCooldown < 0 ? "when available" : spawnCooldown))).row();
        table.add(label("customers spawned: " + levelProgress.getCustomersSpawned())).row();
        table.add(label("customers despawned: " + levelProgress.getCustomersDespawned())).row();

        return table;
    }
}
