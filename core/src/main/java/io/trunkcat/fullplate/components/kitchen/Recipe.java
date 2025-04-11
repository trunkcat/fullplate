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

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;

import java.util.Collection;
import java.util.HashMap;
import java.util.TreeMap;

public class Recipe {
    protected final KitchenEntity.ID resultItem;
    protected final FoodState resultState;
    protected final HashMap<KitchenEntity.ID, Ingredient> ingredients;
    protected final RecipeRenderer renderer;

    protected boolean cookingPossible = false;
    protected boolean overcookingPossible = false;

    public Recipe(KitchenEntity.ID resultItem, FoodState resultState, RecipeRenderer renderer) {
        this.resultItem = resultItem;
        this.resultState = resultState;
        this.ingredients = new HashMap<>();
        this.renderer = renderer;
    }

    public KitchenEntity.ID getResultItem() {
        return resultItem;
    }

    public FoodState getResultState() {
        return resultState;
    }

    public HashMap<KitchenEntity.ID, Ingredient> getIngredients() {
        return ingredients;
    }

    public RecipeRenderer getRenderer() {
        return renderer;
    }

    public int getCost() {
        int cost = 0;
        for (Ingredient ingredient : ingredients.values()) {
            cost += ingredient.getCost();
        }
        return cost;
    }

    public float getOrderProcessingTime() {
        float orderProcessingTime = 0;
        for (Ingredient ingredient : ingredients.values()) {
            orderProcessingTime += ingredient.getOrderProcessingTime();
        }
        return orderProcessingTime;
    }

    public boolean isCookingPossible() {
        return cookingPossible;
    }

    public Recipe setCookingPossible(boolean cookingPossible) {
        this.cookingPossible = cookingPossible;
        return this;
    }

    public boolean isOvercookingPossible() {
        return overcookingPossible;
    }

    public Recipe setOvercookingPossible(boolean overcookingPossible) {
        this.overcookingPossible = overcookingPossible;
        return this;
    }

    public float getCookingTime() {
        float cookingTime = 0;
        for (Ingredient ingredient : ingredients.values()) {
            cookingTime += ingredient.getCookingTime();
        }
        return cookingTime;
    }

    public float getOvercookingTime() {
        float overcookingTime = 0;
        for (Ingredient ingredient : ingredients.values()) {
            overcookingTime += ingredient.getOvercookingTime();
        }
        return overcookingTime;
    }

    public Ingredient getIngredient(KitchenEntity.ID ingredient) {
        return ingredients.get(ingredient);
    }

    public boolean hasIngredient(KitchenEntity.ID ingredient) {
        return ingredients.containsKey(ingredient);
    }

    public Recipe addIngredient(Ingredient ingredient) {
        ingredients.put(ingredient.getId(), ingredient);
        return this;
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

    HashMap<KitchenEntity.ID, Ingredient> getIngredientsOfPriority(int priority) {
        HashMap<KitchenEntity.ID, Ingredient> ingredientsOfPriority = new HashMap<>();
        for (Ingredient ingredient : ingredients.values()) {
            if (ingredient.getPriority() == priority) {
                ingredientsOfPriority.put(ingredient.getId(), ingredient);
            }
        }
        return ingredientsOfPriority;
    }

    public boolean isFullySatisfiedBy(IngredientAssembly ingredientAssembly) {
        HashMap<KitchenEntity.ID, Integer> quantities = ingredientAssembly.getQuantities();
        TreeMap<Integer, HashMap<KitchenEntity.ID, PartialIngredient>> byPriority = ingredientAssembly.groupByPriority(this);

        // shows that this recipe is invalid.
        if (byPriority == null || byPriority.isEmpty()) {
            return false;
        }

        // check each priority level, and compare with the recipe, and what we have.
        for (int currentPriority = 0; currentPriority <= getHighestPriority(); currentPriority++) {
            HashMap<KitchenEntity.ID, PartialIngredient> itemsOfPriority = byPriority
                .getOrDefault(currentPriority, new HashMap<>());
            Collection<Ingredient> ingredientsOfPriority =
                getIngredientsOfPriority(currentPriority).values();

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
                .filter(Ingredient::isRequired)
                .toArray(Ingredient[]::new));

            if (!requiredIngredients.isEmpty()) {
                for (Ingredient requiredIngredient : new Array.ArrayIterator<>(requiredIngredients)) {
                    if (!itemsOfPriority.containsKey(requiredIngredient.getId())) {
                        return false;
                    }
                }
            }

            for (Ingredient ingredient : ingredientsOfPriority) {
                // ignore the optional ones that aren't present in plate.
                if (ingredient.isOptional() && !itemsOfPriority.containsKey(ingredient.getId())) {
                    continue;
                }

                PartialIngredient ingredientItem = itemsOfPriority.get(ingredient.getId());
                if (ingredient.getState() != ingredientItem.getState()) {
                    return false;
                }

                int availableQuantity = quantities.getOrDefault(ingredient.getId(), 0);
                // question: != or < should we allow adding three cheese to a double cheese burger??
                if (availableQuantity != ingredient.getQuantity()) {
                    return false;
                }
            }
        }

        return true;
    }

    public TreeMap<Integer, Array<Ingredient>> groupByPriority() {
        TreeMap<Integer, Array<Ingredient>> ingredientsByPriority = new TreeMap<>();
        for (Ingredient ingredient : ingredients.values()) {
            ingredientsByPriority
                .computeIfAbsent(ingredient.getPriority(), k -> new Array<>())
                .add(ingredient);
        }
        return ingredientsByPriority;
    }

    public IngredientAssembly assembly() {
        IngredientAssembly assembly = new IngredientAssembly();
        for (Ingredient ingredient : this.ingredients.values()) {
            assembly.addIngredient(ingredient.partial());
        }
        assembly.lockRecipe(this);
        return assembly;
    }

    public Recipe generateSubset() {
        Recipe order = new Recipe(resultItem, resultState, renderer);
        TreeMap<Integer, Array<Ingredient>> ingredientsByPriority = groupByPriority();

        if (ingredientsByPriority.isEmpty()) {
            throw new IllegalStateException("The recipe seems invalid");
        }

        final int highestPriority = ingredientsByPriority.lastKey();
        int highestRequiredPriority = 0;
        for (int priority : ingredientsByPriority.keySet()) {
            if (priority > highestRequiredPriority) {
                for (Ingredient ingredient : new Array.ArrayIterator<>(ingredientsByPriority.get(priority))) {
                    if (ingredient.isRequired()) {
                        highestRequiredPriority = priority;
                        break;
                    }
                }
            }
        }

        // Target priority is the last level of priority in the generated order recipe.
        int targetPriority = MathUtils.random(highestRequiredPriority, highestPriority);

        for (int i = 0; i <= targetPriority; i++) {
            Array<Ingredient> possibleIngredients = ingredientsByPriority.get(i);
            for (Ingredient ingredient : new Array.ArrayIterator<>(possibleIngredients)) {
                if (ingredient.isRequired()) {
                    order.addIngredient(ingredient.copy());
                } else {
                    if (MathUtils.randomBoolean()) {
                        int quantity = MathUtils.random(1, ingredient.getQuantity());
                        order.addIngredient(ingredient.copy(quantity));
                    }
                }
            }
        }

        return order;
    }
}
