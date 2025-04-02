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
import com.badlogic.gdx.utils.Array;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import io.trunkcat.fullplate.components.ItemID;

public class FoodCombinationsManager {
    private final HashMap<ItemID, FoodCombination> combinations = new HashMap<>();

    public FoodCombinationsManager() {
    }

    public void addCombination(FoodCombination combination) {
        // TODO: 01/04/25
        //  Make this class abstract and call the abstract .validate() method to validate the
        //  requirements of the ingredients according to the utensil that this manager is linked with.
        //  For example, frying pan starts cooking automatically after all ingredients for a combination
        //  is added. So, those combinations must be unique (the highest priority ingredient needs to be required)
        //  But plates don't care about the requirements probably.
        combinations.put(combination.getResultItemId(), combination);
    }

    public boolean canAddIngredient(Array<FoodCombination.PartialIngredient> existingIngredients,
                                    FoodCombination.PartialIngredient newIngredient) {
        if (newIngredient.getQuantity() <= 0) {
            Gdx.app.log("Combinations:canAddIngredient", "Invalid quantity");
            return false;
        }
        Array<FoodCombination.PartialIngredient> updatedIngredients = new Array<>(existingIngredients);
        updatedIngredients.add(newIngredient);
        FoodCombination combination = findOnePossibleCombination(updatedIngredients);
        return combination != null;
    }

    public FoodCombination findOnePossibleCombination(Array<FoodCombination.PartialIngredient> ingredientItems) {
        if (ingredientItems.isEmpty()) {
            return null;
        }
        HashMap<ItemID, Integer> ingredientQuantities = calculateIngredientQuantities(ingredientItems);
        for (FoodCombination combination : this.combinations.values()) {
            if (isCombinationPossible(combination, ingredientItems, ingredientQuantities)) {
                return combination;
            }
        }
        return null;
    }

    public HashMap<ItemID, FoodCombination> findAllPossibleCombinations(Array<FoodCombination.PartialIngredient> ingredientItems) {
        HashMap<ItemID, FoodCombination> possibleCombinations = new HashMap<>();

        if (ingredientItems.isEmpty()) {
            Gdx.app.log("Combinations:findAllPossibleCombinations", "No ingredient items");
            return possibleCombinations;
        }

        HashMap<ItemID, Integer> ingredientQuantities = calculateIngredientQuantities(ingredientItems);

        for (FoodCombination combination : this.combinations.values()) {
            if (isCombinationPossible(combination, ingredientItems, ingredientQuantities)) {
                possibleCombinations.put(combination.getResultItemId(), combination);
            }
        }

        return possibleCombinations;
    }


    public static HashMap<Integer, HashMap<ItemID, FoodCombination.PartialIngredient>> getItemsByPriority(FoodCombination combination, Array<FoodCombination.PartialIngredient> ingredientItems,
                                                                                                          HashMap<ItemID, Integer> ingredientQuantities) {
        HashMap<Integer, HashMap<ItemID, FoodCombination.PartialIngredient>> itemsByPriority = new HashMap<>();

        int lastPriority = 0;

        for (int i = 0; i < ingredientItems.size; i++) {
            FoodCombination.PartialIngredient ingredientItem = ingredientItems.get(i);

            if (!combination.hasIngredient(ingredientItem.getItemId())) {
                Gdx.app.log("Combinations:getItemsByPriority", "No such ingredient: " + ingredientItem.getItemId() + " in combination" + combination.getResultItemId());
                return null;
            }

            FoodCombination.Ingredient combinationIngredient = combination.getIngredient(ingredientItem.getItemId());

            if (combinationIngredient.getState() != ingredientItem.getState()) {
                Gdx.app.debug("Combinations:getItemsByPriority", "Ingredient state mismatch: " + ingredientItem.getItemId() + " with state " + ingredientItem.getState() + " in combination " + combination.getResultItemId());
                return null;
            }

            int requiredQuantity = combinationIngredient.getQuantity();
            int availableQuantity = ingredientQuantities.get(ingredientItem.getItemId());
            if (availableQuantity > requiredQuantity) {
                Gdx.app.log("Combinations:getItemsByPriority", "Ingredient quantity mismatch: " + ingredientItem.getItemId() + " with quantity " + availableQuantity + " / " + requiredQuantity + " in combination " + combination.getResultItemId());
                return null;
            }

            // It's the first item in the mix, so it must be a base item
            if (i == 0 && !combinationIngredient.isBase()) {
                Gdx.app.log("Combinations:getItemsByPriority", "First ingredient must be a base item: " + ingredientItem.getItemId() + " in combination " + combination.getResultItemId());
                return null;
            }

            int ingredientPriority = combinationIngredient.getPriority();
            if (ingredientPriority == lastPriority) {
            } else if (ingredientPriority == lastPriority + 1) {
                if (!isLastPriorityValid(combination, itemsByPriority, lastPriority, ingredientQuantities)) {
                    Gdx.app.log("Combinations:getItemsByPriority", "Invalid priority: " + ingredientItem.getItemId() + " in combination " + combination.getResultItemId());
                    return null;
                }
                lastPriority += 1;
            } else {
                return null;
            }

            itemsByPriority.computeIfAbsent(ingredientPriority, k -> new HashMap<>())
                .put(ingredientItem.getItemId(), ingredientItem);
        }

        if (lastPriority <= combination.getHighestPriority()) {
            return itemsByPriority;
        } else {
            Gdx.app.log("Combinations:getItemsByPriority", "Invalid priority: " + lastPriority + " / " + combination.getHighestPriority() + " in combination " + combination.getResultItemId());
            return null;
        }
    }

    private static boolean isCombinationPossible(FoodCombination combination, Array<FoodCombination.PartialIngredient> ingredientItems,
                                                 HashMap<ItemID, Integer> ingredientQuantities) {
        return getItemsByPriority(combination, ingredientItems, ingredientQuantities) != null;
    }

    public static HashMap<ItemID, Integer> calculateIngredientQuantities(Array<FoodCombination.PartialIngredient> ingredientItems) {
        HashMap<ItemID, Integer> quantities = new HashMap<>();
        for (FoodCombination.PartialIngredient ingredientItem : new Array.ArrayIterator<>(ingredientItems)) {
            quantities.merge(ingredientItem.getItemId(), ingredientItem.getQuantity(), Integer::sum);
        }
        return quantities;
    }

    private static boolean isLastPriorityValid(FoodCombination combination,
                                               HashMap<Integer, HashMap<ItemID, FoodCombination.PartialIngredient>> itemsByPriority,
                                               int lastPriority,
                                               HashMap<ItemID, Integer> ingredientQuantities) {

        HashMap<ItemID, FoodCombination.PartialIngredient> lastPriorityItems = itemsByPriority.get(lastPriority);
        if (lastPriorityItems == null || lastPriorityItems.isEmpty()) {
            return false;
        }
        for (FoodCombination.Ingredient lastPriorityIngredient : combination.getIngredientsOfPriority(lastPriority).values()) {
            if (!lastPriorityIngredient.isRequired()) continue;
            // 1. It needs to be in the previous priority
            if (!lastPriorityItems.containsKey(lastPriorityIngredient.getItemId())) {
                return false;
            }
            if (ingredientQuantities.get(lastPriorityIngredient.getItemId()) != lastPriorityIngredient.getQuantity()) {
                // 2. The quantity must match
                return false;
            }
        }
        return true;
    }

    public boolean isSatisfied(Array<FoodCombination.PartialIngredient> ingredientItems, FoodCombination combination) {
        HashMap<ItemID, Integer> ingredientQuantities = calculateIngredientQuantities(ingredientItems);
        HashMap<Integer, HashMap<ItemID, FoodCombination.PartialIngredient>> itemsByPriority =
            getItemsByPriority(combination, ingredientItems, ingredientQuantities);

        // shows that this combination is invalid.
        if (itemsByPriority == null) {
            return false;
        }

        // check each priority level, and compare with the combination, and what we have.
        for (int currentPriority = 0; currentPriority <= combination.getHighestPriority(); currentPriority++) {
            HashMap<ItemID, FoodCombination.PartialIngredient> itemsOfPriority = itemsByPriority.getOrDefault(currentPriority, new HashMap<>());
            Collection<FoodCombination.Ingredient> ingredientsOfPriority = combination.getIngredientsOfPriority(currentPriority).values();

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

            List<FoodCombination.Ingredient> requiredIngredients = ingredientsOfPriority.stream()
                .filter(FoodCombination.Ingredient::isRequired)
                .collect(Collectors.toList());

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

    private static boolean hasAllRequiredIngredients(List<FoodCombination.Ingredient> requiredIngredients, HashMap<ItemID, FoodCombination.PartialIngredient> ingredients) {
        // there are required ones, but we don't have any.
        if (ingredients == null || ingredients.isEmpty()) {
            return false;
        }
        for (FoodCombination.Ingredient requiredIngredient : requiredIngredients) {
            if (!ingredients.containsKey(requiredIngredient.getItemId())) {
                return false;
            }
        }
        return true;
    }
}
