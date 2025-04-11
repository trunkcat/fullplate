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

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;

import java.util.Collection;
import java.util.HashMap;

import io.trunkcat.fullplate.components.ItemID;
import io.trunkcat.fullplate.utilities.Constants;

// TODO: make this class abstract and have children: cookable, order-able, holdable for respective classes.
public class FoodCombination {
    public static class PartialIngredient {
        protected final ItemID itemId;
        protected int quantity;
        protected Food.State state;

        public PartialIngredient(ItemID itemId, int quantity, Food.State state) {
            if (quantity <= 0) {
                throw new IllegalArgumentException("Quantity must be positive");
            }
            this.itemId = itemId;
            this.quantity = quantity;
            this.state = state;
        }

        public ItemID getItemId() {
            return itemId;
        }

        public int getQuantity() {
            return quantity;
        }

        public void setQuantity(int quantity) {
            this.quantity = quantity;
        }

        public Food.State getState() {
            return state;
        }

        public void setState(Food.State state) {
            this.state = state;
        }

        public static PartialIngredient from(Food food) {
            return new FoodCombination.PartialIngredient(
                food.getItemId(),
                1,
                food.getCurrentState()
            );
        }

        public static PartialIngredient from(Ingredient ingredient) {
            return new FoodCombination.PartialIngredient(
                ingredient.getItemId(),
                ingredient.getQuantity(),
                ingredient.getState()
            );
        }
    }

    public static class Ingredient extends PartialIngredient {
        // Negatively indexed priority: base=0,then goes above
        protected final int priority;
        protected final boolean required;
        // TODO: remove for base ingredient. (only for order-able). have cooking time and stuff for cookable.
        //  ingredients of holdable combination should not have processing time at all.
        protected float processingTime;
        protected int cost; // TODO: same here, only for what is needed.

        public Ingredient(ItemID id, int priority, boolean required, int quantity, Food.State state, float processingTime) {
            super(id, quantity, state);
            if (priority < 0) {
                throw new IllegalArgumentException("Priority must be non-negative");
            }
            this.priority = priority;
            this.required = required;
            this.processingTime = processingTime;
            this.cost = id.getCost(); // TODO: change this, add levels and stuff.
        }

        // base item constructor
        public Ingredient(ItemID id, int quantity, Food.State state, float processingTime) {
            this(id, 0, true, quantity, state, processingTime);
        }

        public boolean isBase() {
            return priority == 0;
        }

        public int getPriority() {
            return priority;
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

        public float getProcessingTime() {
            return processingTime;
        }
    }

    private final ItemID resultItemId;
    private final Food.State resultState;
    private final HashMap<ItemID, Ingredient> ingredients = new HashMap<>();
    private final IngredientsRenderer ingredientsRenderer;

    // TODO: must extend food combination for cookable food combinations
    private float cookingTime = 0f;
    private float overcookingTime = Constants.DEFAULT_OVERCOOKING_TIME;
    private boolean cookingPossible = true;
    private boolean overcookingPossible = true;
    private boolean ruiningPossible = true;

    public FoodCombination(ItemID resultItem, Food.State resultState, IngredientsRenderer renderer) {
        this.resultItemId = resultItem;
        this.resultState = resultState;
        this.ingredientsRenderer = renderer;
    }

    public ItemID getResultItemId() {
        return resultItemId;
    }

    public Food.State getResultState() {
        return resultState;
    }

    // TODO: only for order-able combinations
    public float getProcessingTime() {
        float processingTime = 0f;
        for (Ingredient ingredient : ingredients.values()) {
            processingTime += ingredient.getProcessingTime();
        }
        return processingTime;
    }

    public float getOvercookingTime() {
        return overcookingTime;
    }

    public boolean isCookingPossible() {
        return cookingPossible;
    }

    public FoodCombination setCookingPossible(boolean cookingPossible) {
        this.cookingPossible = cookingPossible;
        return this;
    }

    public boolean isOvercookingPossible() {
        return overcookingPossible;
    }

    public FoodCombination setOvercookingPossible(boolean overcookingPossible) {
        this.overcookingPossible = overcookingPossible;
        return this;
    }

    public FoodCombination addIngredient(Ingredient ingredient) {
        ingredients.put(ingredient.getItemId(), ingredient);
        return this;
    }

    public boolean hasIngredient(ItemID ingredientItemId) {
        return ingredients.containsKey(ingredientItemId);
    }

    public Ingredient getIngredient(ItemID ingredientItemId) {
        return ingredients.get(ingredientItemId);
    }

    public int getHighestPriority() {
        int highestPriority = 0;
        for (Ingredient ingredient : ingredients.values()) {
            if (ingredient.getPriority() > highestPriority) {
                highestPriority = ingredient.getPriority();
            }
        }
        return highestPriority;
    }

    public HashMap<ItemID, Ingredient> getIngredients() {
        return ingredients;
    }

    public IngredientsRenderer getIngredientsRenderer() {
        return ingredientsRenderer;
    }

    HashMap<ItemID, Ingredient> getIngredientsOfPriority(int priority) {
        HashMap<ItemID, Ingredient> ingredientsOfPriority = new HashMap<>();
        for (Ingredient ingredient : ingredients.values()) {
            if (ingredient.getPriority() == priority) {
                ingredientsOfPriority.put(ingredient.getItemId(), ingredient);
            }
        }
        return ingredientsOfPriority;
    }

    public IngredientsRenderer.Rect render(Batch batch, Array<FoodCombination.PartialIngredient> ingredientItems, float x, float y) {
        HashMap<ItemID, PartialIngredient> ingredients = new HashMap<>();

        for (FoodCombination.PartialIngredient ingredient : new Array.ArrayIterator<>(ingredientItems)) {
            if (ingredients.containsKey(ingredient.getItemId())) {
                PartialIngredient partialIngredient = ingredients.get(ingredient.getItemId());
                partialIngredient.setQuantity(partialIngredient.getQuantity() + 1);
            } else {
                ingredients.put(ingredient.getItemId(), ingredient);
            }
        }

        ingredientsRenderer.setBatch(batch);
        return ingredientsRenderer.render(ingredients, x, y);
    }

    public Array<FoodCombination.Ingredient> generateSatisfiable() {
        Array<FoodCombination.Ingredient> orderIngredients = new Array<>();

        HashMap<Integer, Array<Ingredient>> ingredientsByPriority =
            FoodCombinationsManager.groupCombinationByIngredientPriority(this);

        if (ingredientsByPriority.isEmpty()) {
            throw new IllegalStateException("The combination seems invalid");
        }

        int highestPriority = 0;
        int highestRequiredPriority = 0;
        for (int priority : ingredientsByPriority.keySet()) {
            if (priority > highestPriority) {
                highestPriority = priority;
            }
            if (priority > highestRequiredPriority) {
                for (Ingredient ingredient : new Array.ArrayIterator<>(ingredientsByPriority.get(priority))) {
                    if (ingredient.isRequired()) {
                        highestRequiredPriority = priority;
                        break;
                    }
                }
            }
        }

        // Target priority is the last level of priority in the generated order combination.
        int targetPriority = MathUtils.random(highestRequiredPriority, highestPriority);

        for (int i = 0; i <= targetPriority; i++) {
            Array<Ingredient> possibleIngredients = ingredientsByPriority.get(i);
            for (Ingredient ingredient : new Array.ArrayIterator<>(possibleIngredients)) {
                if (ingredient.isRequired()) {
                    orderIngredients.add(
                        new Ingredient(
                            ingredient.getItemId(),
                            ingredient.getPriority(),
                            ingredient.isRequired(),
                            ingredient.getQuantity(),
                            ingredient.getState(),
                            ingredient.getProcessingTime()
                        )
                    );
                } else {
                    if (MathUtils.randomBoolean()) {
                        int quantity = ingredient.getQuantity() == 1
                            ? 1
                            : MathUtils.random(1, ingredient.getQuantity());
                        orderIngredients.add(
                            new Ingredient(
                                ingredient.getItemId(),
                                ingredient.getPriority(),
                                ingredient.isRequired(),
                                quantity,
                                ingredient.getState(),
                                ingredient.getProcessingTime()
                            )
                        );
                    }
                }
            }
        }

        return orderIngredients;
    }

    public int getPrice() {
        int price = 0;
        for (Ingredient ingredient : ingredients.values()) {
            price += ingredient.getCost();
        }
        return price;
    }

    public boolean isSatisfied(Array<PartialIngredient> ingredients) {
        HashMap<ItemID, Integer> ingredientQuantities = FoodCombinationsManager
            .calculateIngredientQuantities(ingredients);
        HashMap<Integer, HashMap<ItemID, FoodCombination.PartialIngredient>> itemsByPriority =
            FoodCombinationsManager.getItemsByPriority(this, ingredients, ingredientQuantities);

        // shows that this combination is invalid.
        if (itemsByPriority == null) {
            return false;
        }

        // check each priority level, and compare with the combination, and what we have.
        for (int currentPriority = 0; currentPriority <= getHighestPriority(); currentPriority++) {
            HashMap<ItemID, FoodCombination.PartialIngredient> itemsOfPriority =
                itemsByPriority.getOrDefault(currentPriority, new HashMap<>());
            Collection<Ingredient> ingredientsOfPriority = getIngredientsOfPriority(currentPriority).values();

            if (ingredientsOfPriority.isEmpty()) {
                // there are no expected ingredients for this priority, yet we have some in hand.
                if (!itemsOfPriority.isEmpty()) {
                    return false;
                }
                continue;
            }

            if (ingredientsOfPriority.size() < itemsOfPriority.size()) {
                return false;
            }

            Array<Ingredient> requiredIngredients = new Array<>(ingredientsOfPriority.stream()
                .filter(FoodCombination.Ingredient::isRequired)
                .toArray(Ingredient[]::new));

            if (!requiredIngredients.isEmpty() &&
                !hasAllRequiredIngredients(requiredIngredients, itemsOfPriority)) {
                return false;
            }

            for (FoodCombination.Ingredient ingredient : ingredientsOfPriority) {
                // ignore the optional ones that aren't present in plate.
                if (ingredient.isOptional() && !itemsOfPriority.containsKey(ingredient.getItemId())) {
                    continue;
                }

                FoodCombination.PartialIngredient ingredientItem = itemsOfPriority.get(ingredient.getItemId());
                if (ingredient.getState() != ingredientItem.getState()) {
                    return false;
                }

                int availableQuantity = ingredientQuantities.getOrDefault(ingredient.getItemId(), 0);
                // TODO: != or <
                //  Should we allow adding three cheese to a double cheese burger??
                if (availableQuantity != ingredient.getQuantity()) {
                    return false;
                }
            }
        }

        return true;
    }

    private boolean hasAllRequiredIngredients(
        Array<FoodCombination.Ingredient> requiredIngredients,
        HashMap<ItemID, FoodCombination.PartialIngredient> ingredients
    ) {
        for (FoodCombination.PartialIngredient requiredIngredient : new Array.ArrayIterator<>(requiredIngredients)) {
            if (!ingredients.containsKey(requiredIngredient.getItemId())) {
                return false;
            }
        }
        return true;
    }
}
