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

public class FoodRecipe {
    public static class Ingredient {
        private final ItemID itemId;
        // Negatively indexed priority: base=0,then goes above
        private final int priority;
        private final boolean required;
        private final int quantity;

        public Ingredient(ItemID id, int priority, boolean required, int quantity) {
            if (priority < 0) {
                throw new IllegalArgumentException("Priority must be non-negative");
            }
            if (quantity <= 0) {
                throw new IllegalArgumentException("Quantity must be positive");
            }
            this.itemId = id;
            this.priority = priority;
            this.required = required;
            this.quantity = quantity;
        }

        // base item constructor
        public Ingredient(ItemID id, int quantity) {
            this(id, 0, true, quantity);
        }

        public ItemID getItemId() {
            return itemId;
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

        public int getQuantity() {
            return quantity;
        }
    }

    public static class PartialIngredient {
        private final ItemID itemId;
        private int quantity;
        private Food.State state;

        public PartialIngredient(ItemID itemId, int quantity, Food.State state) {
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
    }

    private final ItemID resultItemId;
    private final HashMap<ItemID, Ingredient> ingredients = new HashMap<>();
    private final FoodRecipeRenderer renderer;

    public FoodRecipe(ItemID resultItem, FoodRecipeRenderer renderer) {
        this.resultItemId = resultItem;
        this.renderer = renderer;
    }

    public ItemID getResultItemId() {
        return resultItemId;
    }

    public void addIngredient(Ingredient ingredient) {
        ingredients.put(ingredient.getItemId(), ingredient);
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

    public void render(Batch batch, Array<FoodRecipe.PartialIngredient> ingredientItems, float x, float y) {
        HashMap<ItemID, PartialIngredient> ingredients = new HashMap<>();

        for (FoodRecipe.PartialIngredient ingredient : ingredientItems) {
            if (ingredients.containsKey(ingredient.getItemId())) {
                PartialIngredient partialIngredient = ingredients.get(ingredient.getItemId());
                partialIngredient.setQuantity(partialIngredient.getQuantity() + 1);
            } else {
                ingredients.put(ingredient.getItemId(), ingredient);
            }
        }

        renderer.setBatch(batch);
        renderer.render(ingredients, x, y);
    }
}
