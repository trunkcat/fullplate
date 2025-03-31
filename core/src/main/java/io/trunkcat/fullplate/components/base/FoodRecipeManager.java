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

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

import io.trunkcat.fullplate.components.ItemID;

public class FoodRecipeManager {
    private final HashMap<ItemID, FoodRecipe> recipes = new HashMap<>();

    public FoodRecipeManager() {
    }

    public void addRecipe(FoodRecipe recipe) {
        recipes.put(recipe.getResultItemId(), recipe);
    }

    public FoodRecipe getRecipe(ItemID itemId) {
        return recipes.get(itemId);
    }

    public boolean canAddIngredient(Array<FoodRecipe.PartialIngredient> existingIngredients,
                                    FoodRecipe.PartialIngredient newIngredient) {
        if (newIngredient.getState() != Food.State.PREPARED) {
            return false;
        }
        if (newIngredient.getQuantity() <= 0) {
            return false;
        }
        Array<FoodRecipe.PartialIngredient> updatedIngredients = new Array<>(existingIngredients);
        updatedIngredients.add(newIngredient);
        HashMap<ItemID, FoodRecipe> recipes = getPossibleRecipes(updatedIngredients);
        return !recipes.isEmpty();
    }

    public FoodRecipe getFirstMatchingRecipe(Array<FoodRecipe.PartialIngredient> ingredientItems) {
        if (ingredientItems.size == 0) {
            return null;
        }
        HashMap<ItemID, Integer> ingredientQuantities = calculateIngredientQuantities(ingredientItems);
        for (FoodRecipe recipe : this.recipes.values()) {
            if (isRecipePossible(recipe, ingredientItems, ingredientQuantities)) {
                return recipe;
            }
        }
        return null;
    }

    public HashMap<ItemID, FoodRecipe> getPossibleRecipes(Array<FoodRecipe.PartialIngredient> ingredientItems) {
        HashMap<ItemID, FoodRecipe> possibleRecipes = new HashMap<>();

        if (ingredientItems.size == 0) {
            return possibleRecipes;
        }

        HashMap<ItemID, Integer> ingredientQuantities = calculateIngredientQuantities(ingredientItems);

        for (FoodRecipe recipe : this.recipes.values()) {
            if (isRecipePossible(recipe, ingredientItems, ingredientQuantities)) {
                possibleRecipes.put(recipe.getResultItemId(), recipe);
            }
        }

        return possibleRecipes;
    }


    public static HashMap<Integer, HashSet<ItemID>> getItemsByPriority(FoodRecipe recipe, Array<FoodRecipe.PartialIngredient> ingredientItems,
                                                                       HashMap<ItemID, Integer> ingredientQuantities) {
        HashMap<Integer, HashSet<ItemID>> itemsByPriority = new HashMap<>();

        int lastPriority = 0;

        for (int i = 0; i < ingredientItems.size; i++) {
            FoodRecipe.PartialIngredient ingredientItem = ingredientItems.get(i);

            if (!recipe.hasIngredient(ingredientItem.getItemId())) {
                return null;
            }

            FoodRecipe.Ingredient recipeIngredient = recipe.getIngredient(ingredientItem.getItemId());

            int requiredQuantity = recipeIngredient.getQuantity();
            int availableQuantity = ingredientQuantities.get(ingredientItem.getItemId());
            if (availableQuantity > requiredQuantity) {
                return null;
            }

            // It's the first item in the mix, so it must be a base item
            if (i == 0 && !recipeIngredient.isBase()) {
                return null;
            }

            int ingredientPriority = recipeIngredient.getPriority();
            if (ingredientPriority == lastPriority) {
            } else if (ingredientPriority == lastPriority + 1) {
                if (!isLastPriorityValid(recipe, itemsByPriority, lastPriority, ingredientQuantities)) {
                    return null;
                }
                lastPriority += 1;
            } else {
                return null;
            }

            itemsByPriority.computeIfAbsent(ingredientPriority, k -> new HashSet<>())
                    .add(ingredientItem.getItemId());
        }

        if (lastPriority <= recipe.getHighestPriority()) {
            return itemsByPriority;
        } else {
            return null;
        }
    }

    private static boolean isRecipePossible(FoodRecipe recipe, Array<FoodRecipe.PartialIngredient> ingredientItems,
                                            HashMap<ItemID, Integer> ingredientQuantities) {
        return getItemsByPriority(recipe, ingredientItems, ingredientQuantities) != null;
    }

    public static HashMap<ItemID, Integer> calculateIngredientQuantities(Array<FoodRecipe.PartialIngredient> ingredientItems) {
        HashMap<ItemID, Integer> quantities = new HashMap<>();
        for (FoodRecipe.PartialIngredient ingredientItem : ingredientItems) {
            quantities.merge(ingredientItem.getItemId(), ingredientItem.getQuantity(), Integer::sum);
        }
        return quantities;
    }

    private static boolean isLastPriorityValid(FoodRecipe recipe, HashMap<Integer, HashSet<ItemID>> itemsByPriority,
                                               int lastPriority, HashMap<ItemID, Integer> ingredientQuantities) {
        HashSet<ItemID> lastPriorityItems = itemsByPriority.get(lastPriority);
        if (lastPriorityItems == null || lastPriorityItems.isEmpty()) {
            return false;
        }
        for (FoodRecipe.Ingredient lastPriorityIngredient : recipe.getIngredientsOfPriority(lastPriority).values()) {
            if (!lastPriorityIngredient.isRequired()) continue;
            // 1. It needs to be in the previous priority
            if (!lastPriorityItems.contains(lastPriorityIngredient.getItemId())) {
                return false;
            }
            if (ingredientQuantities.get(lastPriorityIngredient.getItemId()) != lastPriorityIngredient.getQuantity()) {
                // 2. The quantity must match
                return false;
            }
        }
        return true;
    }

    public boolean isSatisfied(Array<FoodRecipe.PartialIngredient> ingredientItems, FoodRecipe recipe) {
        HashMap<ItemID, Integer> ingredientQuantities = calculateIngredientQuantities(ingredientItems);
        HashMap<Integer, HashSet<ItemID>> itemsByPriority = getItemsByPriority(recipe, ingredientItems, ingredientQuantities);

        // shows that this recipe is invalid.
        if (itemsByPriority == null) {
            return false;
        }

        // check each priority level, and compare with the recipe, and what we have.
        for (int currentPriority = 0; currentPriority <= recipe.getHighestPriority(); currentPriority++) {
            HashSet<ItemID> itemsWithPriority = itemsByPriority.getOrDefault(currentPriority, new HashSet<>());
            Collection<FoodRecipe.Ingredient> ingredientsWithPriority = recipe.getIngredientsOfPriority(currentPriority).values();

            if (ingredientsWithPriority.isEmpty()) {
                // there are no expected ingredients for this priority, yet we have some in hand.
                if (!itemsWithPriority.isEmpty()) {
                    return false;
                }
                continue;
            }

            if (ingredientsWithPriority.size() < itemsWithPriority.size()) {
                return false;
            }

            List<FoodRecipe.Ingredient> requiredIngredients = ingredientsWithPriority.stream()
                    .filter(io.trunkcat.fullplate.components.base.FoodRecipe.Ingredient::isRequired)
                    .collect(Collectors.toList());

            if (!requiredIngredients.isEmpty() &&
                    !hasAllRequiredIngredients(requiredIngredients, itemsWithPriority)) {
                return false;
            }

            for (FoodRecipe.Ingredient ingredient : ingredientsWithPriority) {
                // ignore the optional ones that aren't present in plate.
                if (ingredient.isOptional() && !itemsWithPriority.contains(ingredient.getItemId())) {
                    continue;
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

    private static boolean hasAllRequiredIngredients(List<FoodRecipe.Ingredient> requiredIngredients, HashSet<ItemID> ingredients) {
        // there are required ones, but we don't have any.
        if (ingredients == null || ingredients.isEmpty()) {
            return false;
        }
        for (FoodRecipe.Ingredient requiredIngredient : requiredIngredients) {
            if (!ingredients.contains(requiredIngredient.getItemId())) {
                return false;
            }
        }
        return true;
    }
}
