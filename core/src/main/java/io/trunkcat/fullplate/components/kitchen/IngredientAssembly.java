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

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.utils.Array;

import java.util.HashMap;
import java.util.TreeMap;

import io.trunkcat.fullplate.utilities.Rect;

public class IngredientAssembly {
    private final Array<PartialIngredient> ingredients;
    private FoodState state = FoodState.UNCOOKED;
    private Recipe lockedRecipe = null;

    public IngredientAssembly() {
        this.ingredients = new Array<>();
    }

    public IngredientAssembly(Array<PartialIngredient> ingredients) {
        this.ingredients = ingredients; // question: should i copy?
    }

    public FoodState getState() {
        return state;
    }

    public void setState(FoodState state) {
        this.state = state;
        for (PartialIngredient ingredient : new Array.ArrayIterator<>(ingredients)) {
            ingredient.setState(state);
        }
    }

    public boolean hasLockedRecipe() {
        return lockedRecipe != null;
    }

    public Recipe getLockedRecipe() {
        return lockedRecipe;
    }

    public void lockRecipe(Recipe recipe) {
        this.lockedRecipe = recipe;
    }

    public void unlockRecipe() {
        this.lockedRecipe = null;
    }

    public boolean isEmpty() {
        return ingredients.isEmpty();
    }

    public Array<PartialIngredient> getIngredients() {
        return ingredients;
    }

    public void reset() {
        this.ingredients.clear();
        this.state = FoodState.UNCOOKED;
        unlockRecipe();
    }

    public IngredientAssembly addIngredient(PartialIngredient ingredient) {
        if (hasLockedRecipe()) {
            throw new IllegalStateException("Ingredients cannot be added if the recipe is locked.");
        }
        this.ingredients.add(ingredient);
        return this;
    }

    public IngredientAssembly addIngredients(Array<PartialIngredient> ingredients) {
        if (hasLockedRecipe()) {
            throw new IllegalStateException("Ingredients cannot be added if the recipe is locked.");
        }
        this.ingredients.addAll(ingredients);
        return this;
    }

    public IngredientAssembly addIngredients(IngredientAssembly assembly) {
        return addIngredients(assembly.getIngredients());
    }

    public HashMap<KitchenEntity.ID, Integer> getQuantities() {
        HashMap<KitchenEntity.ID, Integer> quantities = new HashMap<>();
        for (PartialIngredient ingredient : new Array.ArrayIterator<>(ingredients)) {
            quantities.merge(ingredient.getId(), ingredient.getQuantity(), Integer::sum);
        }
        return quantities;
    }

    /**
     * Checks whether or not the assembly is building up to the recipe.
     * Not all the conditions has to be met.
     * Just up until the last resolved priority.
     * That is, if the ingredients in the assembly can be ingredients can be grouped by priority in reference to a recipe.
     *
     * @param recipe The recipe to satisfy
     * @return True if the the assembly is building up to the assembly.
     */
    public boolean satisfiable(Recipe recipe) {
        return groupByPriority(recipe) != null;
    }

    /**
     * Groups the ingredients in the assembly by priority based on the provided recipe.
     * <b>This does not strictly check the entire conditions of the recipe</b>.
     * This method only checks if what so far added can be grouped by priority.
     *
     * @param recipe The recipe to reference
     * @return Ingredients grouped by priority if the recipe can be satisfied, otherwise null.
     */
    public TreeMap<Integer, HashMap<KitchenEntity.ID, PartialIngredient>> groupByPriority(Recipe recipe) {
        // todo: can be removed by just adding 1 to partial ingredients while merging them.
        HashMap<KitchenEntity.ID, Integer> quantities = getQuantities();
        // todo: can be changed to a tree map and simplify the last priority.
        TreeMap<Integer, HashMap<KitchenEntity.ID, PartialIngredient>> byPriority = new TreeMap<>();

        int lastPriority = 0;

        for (int i = 0; i < ingredients.size; i++) {
            PartialIngredient partialIngredient = ingredients.get(i);
            if (!recipe.hasIngredient(partialIngredient.getId())) {
                return null;
            }

            Ingredient ingredient = recipe.getIngredient(partialIngredient.getId());

            if (ingredient.getState() != partialIngredient.getState()) {
                return null;
            }

            int maximumQuantity = ingredient.getQuantity();
            int availableQuantity = quantities.get(partialIngredient.getId());
            if (availableQuantity > maximumQuantity) {
                return null;
            }

            if (i == 0 && !ingredient.isBaseIngredient()) {
                return null;
            }

            int ingredientPriority = ingredient.getPriority();
            if (ingredientPriority == lastPriority + 1) {
                // before adding to the new priority, make sure that the last priority is all cool.
                HashMap<KitchenEntity.ID, PartialIngredient> lastPriorityPartialIngredients = byPriority.get(lastPriority);

                if (lastPriorityPartialIngredients == null || lastPriorityPartialIngredients.isEmpty()) {
                    return null; // that's not cool. or is it?
                }

                for (Ingredient lastPriorityIngredient : recipe.getIngredientsOfPriority(lastPriority).values()) {
                    if (!lastPriorityIngredient.isRequired()) continue;
                    // 1. It needs to be in the previous priority
                    if (!lastPriorityPartialIngredients.containsKey(lastPriorityIngredient.getId())) {
                        return null;
                    }
                    if (quantities.get(lastPriorityIngredient.getId()) != lastPriorityIngredient.getQuantity()) {
                        // 2. The quantity must match
                        return null;
                    }
                }

                lastPriority++;
            } else if (ingredientPriority != lastPriority) {
                return null;
            }

            partialIngredient.setQuantity(availableQuantity);
            byPriority.computeIfAbsent(ingredientPriority, k -> new HashMap<>())
                .put(ingredient.getId(), partialIngredient);
        }

        if (lastPriority <= recipe.getHighestPriority()) {
            return byPriority;
        } else {
            return null;
        }
    }

    public Rect render(Batch batch, Recipe recipe, float x, float y) {
        if (recipe != null) {
            // todo: this needs to be implemented again. priority based rendering.
//            TreeMap<Integer, HashMap<KitchenEntity.ID, PartialIngredient>> grouped = groupByPriority(recipe);

            HashMap<KitchenEntity.ID, PartialIngredient> merged = new HashMap<>();

            for (PartialIngredient ingredient : new Array.ArrayIterator<>(ingredients)) {
                if (merged.containsKey(ingredient.getId())) {
                    PartialIngredient partialIngredient = merged.get(ingredient.getId());
                    partialIngredient.setQuantity(partialIngredient.getQuantity() + 1);
                } else {
                    merged.put(ingredient.getId(), ingredient);
                }
            }

            RecipeRenderer renderer = recipe.getRenderer();
            renderer.setBatch(batch);
            return renderer.render(merged, x, y);
        }
        return new Rect(x, y, 0, 0);
    }

    public static IngredientAssembly from(IngredientAssembly ingredientAssembly) {
        return new IngredientAssembly(new Array<>(ingredientAssembly.getIngredients()));
    }
}
