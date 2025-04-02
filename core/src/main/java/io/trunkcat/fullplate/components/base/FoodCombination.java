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
import com.badlogic.gdx.utils.Array;

import java.util.HashMap;

import io.trunkcat.fullplate.components.ItemID;
import io.trunkcat.fullplate.utilities.Constants;

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
            return new FoodCombination.PartialIngredient(food.getItemId(), 1, food.getCurrentState());
        }
    }

    public static class Ingredient extends PartialIngredient {
        // Negatively indexed priority: base=0,then goes above
        private final int priority;
        private final boolean required;

        public Ingredient(ItemID id, int priority, boolean required, int quantity, Food.State state) {
            super(id, quantity, state);
            if (priority < 0) {
                throw new IllegalArgumentException("Priority must be non-negative");
            }
            this.priority = priority;
            this.required = required;
        }

        // base item constructor
        public Ingredient(ItemID id, int quantity, Food.State state) {
            this(id, 0, true, quantity, state);
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
    }


    private final ItemID resultItemId;
    private final Food.State resultState;
    private final HashMap<ItemID, Ingredient> ingredients = new HashMap<>();
    private final IngredientsRenderer ingredientsRenderer;
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

    public float getCookingTime() {
        return cookingTime;
    }

    public FoodCombination setCookingTime(float cookingTime) {
        this.cookingTime = cookingTime;
        return this;
    }

    public float getOvercookingTime() {
        return overcookingTime;
    }

    public FoodCombination setOvercookingTime(float overcookingTime) {
        this.overcookingTime = overcookingTime;
        return this;
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

    public boolean isRuiningPossible() {
        return ruiningPossible;
    }

    public FoodCombination setRuiningPossible(boolean ruiningPossible) {
        this.ruiningPossible = ruiningPossible;
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

    HashMap<ItemID, Ingredient> getIngredientsOfPriority(int priority) {
        HashMap<ItemID, Ingredient> ingredientsOfPriority = new HashMap<>();
        for (Ingredient ingredient : ingredients.values()) {
            if (ingredient.getPriority() == priority) {
                ingredientsOfPriority.put(ingredient.getItemId(), ingredient);
            }
        }
        return ingredientsOfPriority;
    }

    public void render(Batch batch, Array<FoodCombination.PartialIngredient> ingredientItems, float x, float y) {
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
        ingredientsRenderer.render(ingredients, x, y);
    }
}
