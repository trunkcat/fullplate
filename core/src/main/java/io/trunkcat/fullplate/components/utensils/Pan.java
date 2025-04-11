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

package io.trunkcat.fullplate.components.utensils;

import java.util.HashMap;

import io.trunkcat.fullplate.components.kitchen.Cooker;
import io.trunkcat.fullplate.components.kitchen.FoodState;
import io.trunkcat.fullplate.components.kitchen.Ingredient;
import io.trunkcat.fullplate.components.kitchen.PartialIngredient;
import io.trunkcat.fullplate.components.kitchen.Recipe;
import io.trunkcat.fullplate.components.kitchen.RecipeCollection;
import io.trunkcat.fullplate.components.kitchen.RecipeRenderer;
import io.trunkcat.fullplate.utilities.Rect;

public class Pan extends Cooker {
    public static final RecipeCollection RECIPE_COLLECTION = new RecipeCollection();

    static {
        RecipeRenderer renderer = new RecipeRenderer() {
            @Override
            public Rect render(HashMap<ID, PartialIngredient> ingredients, float worldX, float worldY) {
                Rect rect = super.render(ingredients, worldX, worldY);
                float x = 0, y = 0;

                if (ingredients.containsKey(ID.PATTY)) {
                    draw(rect, ingredients.get(ID.PATTY), x, y);
                }

                return rect;
            }
        };
        Recipe recipe = new Recipe(ID.PATTY, FoodState.UNCOOKED, renderer);
        recipe.addIngredient(
            new Ingredient(ID.PATTY, 1, FoodState.UNCOOKED)
                .setCookingTime(5f)
                .setOvercookingTime(5f)
        );

        RECIPE_COLLECTION.addRecipe(recipe);
    }

    public Pan() {
        super(ID.PAN, 0, 1, RECIPE_COLLECTION);
    }

    @Override
    public void act(float delta) {
        super.act(delta);

        setAssemblyOffset(75, 75);
    }
}
