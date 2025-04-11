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

import com.badlogic.gdx.utils.Array;

import java.util.HashMap;

public class RecipeCollection {
    private final HashMap<String, Recipe> recipes;

    public RecipeCollection() {
        this.recipes = new HashMap<>();
    }

    public HashMap<String, Recipe> getRecipes() {
        return recipes;
    }

    public void addRecipe(Recipe recipe) {
        // todo: make RecipeValidator which validates the recipe.
        // each validator will be linked with the right utensil.
        // Validator should check if the recipe being added is well, "valid".
        // Like how the cooking recipes are unique and stuff.

        final String key = recipe.getResultItem() + "-" + recipe.getResultState();
        if (recipes.containsKey(key)) {
            throw new IllegalStateException("Recipe with matching result item ID and state already exists.");
        }
        recipes.put(key, recipe);
    }

    public boolean isPartialIngredientAddable(IngredientAssembly ingredientAssembly, PartialIngredient partialIngredientToAdd) {
        if (partialIngredientToAdd.getQuantity() <= 0) {
            return false; // question: should i throw?
        }
        IngredientAssembly updatedIngredientAssembly = IngredientAssembly.from(ingredientAssembly);
        updatedIngredientAssembly.addIngredient(partialIngredientToAdd);
        Recipe recipe = findSatisfiableRecipe(updatedIngredientAssembly);
        return recipe != null;
    }

    public boolean isPartialIngredientsAddable(IngredientAssembly ingredientAssembly, Array<PartialIngredient> partialIngredientsToAdd) {
        // question: what happens if the array is empty?
        for (PartialIngredient partialIngredient : new Array.ArrayIterator<>(partialIngredientsToAdd)) {
            if (partialIngredient.getQuantity() <= 0) {
                return false; // question: should i throw?
            }
        }
        IngredientAssembly updatedIngredientAssembly = IngredientAssembly.from(ingredientAssembly);
        updatedIngredientAssembly.addIngredients(partialIngredientsToAdd);
        Recipe recipe = findSatisfiableRecipe(updatedIngredientAssembly);
        return recipe != null;
    }

    public boolean isPartialIngredientsAddable(IngredientAssembly ingredientAssembly, IngredientAssembly assemblyToAdd) {
        return isPartialIngredientsAddable(ingredientAssembly, new Array<>(assemblyToAdd.getIngredients()));
    }

    public Recipe findSatisfiableRecipe(IngredientAssembly ingredientAssembly) {
        if (ingredientAssembly.isEmpty()) {
            return null;
        }
        for (Recipe recipe : this.recipes.values()) {
            if (ingredientAssembly.satisfiable(recipe)) {
                return recipe;
            }
        }
        return null;
    }

    public Array<Recipe> findSatisfiableRecipes(IngredientAssembly ingredientAssembly) {
        Array<Recipe> possible = new Array<>();

        if (ingredientAssembly.isEmpty()) {
            return possible;
        }

        for (Recipe recipe : this.recipes.values()) {
            if (ingredientAssembly.satisfiable(recipe)) {
                possible.add(recipe);
            }
        }
        return possible;
    }

    public static RecipeCollection from(RecipeCollection... collections) {
        RecipeCollection combined = new RecipeCollection();
        for (RecipeCollection collection : collections) {
            for (Recipe recipe : collection.getRecipes().values()) {
                combined.addRecipe(recipe);
            }
        }
        return combined;
    }
}
