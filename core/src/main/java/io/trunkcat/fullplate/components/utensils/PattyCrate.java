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

import io.trunkcat.fullplate.components.kitchen.FoodState;
import io.trunkcat.fullplate.components.kitchen.Ingredient;
import io.trunkcat.fullplate.components.kitchen.Item;
import io.trunkcat.fullplate.components.kitchen.PartialIngredient;
import io.trunkcat.fullplate.components.kitchen.Recipe;
import io.trunkcat.fullplate.components.kitchen.RecipeRenderer;
import io.trunkcat.fullplate.components.kitchen.Store;
import io.trunkcat.fullplate.utilities.Rect;

public class PattyCrate extends Store {
    static Recipe RECIPE;

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

        // NOTE: until combinations manager implements combination validation upon adding,
        // all frying pan combinations must top it of with a required ingredient.
        // i.e. the last item triggers the cooking process, so, make sure combinations are unique and
        // the very last item is required.

        RECIPE = new Recipe(ID.PATTY, FoodState.COOKED, renderer);
        RECIPE.addIngredient(new Ingredient(ID.PATTY, 1, FoodState.UNCOOKED));
    }

    public PattyCrate(int initialStock) {
        // todo: read level of store item from player data
        super(ID.PATTY_CRATE, 0, 1, initialStock, RECIPE);
    }

    @Override
    public Item produce() {
        return new Item(ID.PATTY, Type.FOOD, RECIPE.assembly());
    }
}
