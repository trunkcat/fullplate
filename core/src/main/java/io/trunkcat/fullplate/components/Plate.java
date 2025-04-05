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

package io.trunkcat.fullplate.components;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Array;

import java.util.HashMap;

import io.trunkcat.fullplate.components.base.Food;
import io.trunkcat.fullplate.components.base.FoodCombination;
import io.trunkcat.fullplate.components.base.FoodCombinationsManager;
import io.trunkcat.fullplate.components.base.FoodHolder;
import io.trunkcat.fullplate.components.base.IngredientsRenderer;

public class Plate extends FoodHolder {
    public static FoodCombinationsManager COMBINATION_MANAGER = new FoodCombinationsManager();

    static {
        IngredientsRenderer burgerRenderer = new IngredientsRenderer() {
            @Override
            public Rect render(HashMap<ItemID, FoodCombination.PartialIngredient> ingredients, float worldX, float worldY) {
                Rect rect = super.render(ingredients, worldX, worldY);

                float x = 0, y = 0;

                FoodCombination.PartialIngredient burgerBun = ingredients.get(ItemID.BURGER_BUN);
                if (burgerBun != null) {
                    draw(rect, burgerBun, x, y);
                } else {
                    return rect;
                }

                FoodCombination.PartialIngredient patty = ingredients.get(ItemID.BURGER_PATTY);
                if (patty != null) {
                    y += 5;
                    draw(rect, patty, x, y);
                }

                FoodCombination.PartialIngredient tomato = ingredients.get(ItemID.TOMATO);
                if (tomato != null) {
                    y += 5;
                    draw(rect, tomato, x, y);
                }

                y += 5;
                draw(rect, burgerBun, x, y);

                return rect;
            }
        };

        FoodCombination burger = new FoodCombination(ItemID.BURGER, Food.State.PREPARED, burgerRenderer)
            .addIngredient(new FoodCombination.Ingredient(ItemID.BURGER_BUN, 1, Food.State.PREPARED, 2f))
            .addIngredient(new FoodCombination.Ingredient(ItemID.BURGER_PATTY, 1, true, 1, Food.State.PREPARED, 10f));
//            .addIngredient(new FoodCombination.Ingredient(ItemID.LETTUCE, 1, false, 1, Food.State.PREPARED, 0f))
//            .addIngredient(new FoodCombination.Ingredient(ItemID.CHEESE, 1, false, 1, Food.State.PREPARED, 0f))
//            .addIngredient(new FoodCombination.Ingredient(ItemID.TOMATO, 1, false, 1, Food.State.PREPARED, 0f));

        COMBINATION_MANAGER.addCombination(burger);
    }

    public Plate(int level) {
        super(ItemID.PLATE, level, Plate.COMBINATION_MANAGER);

        Array<FoodCombination.PartialIngredient> items = new Array<>(new FoodCombination.PartialIngredient[]{
            new FoodCombination.PartialIngredient(ItemID.BURGER_BUN, 1, Food.State.PREPARED),
            new FoodCombination.PartialIngredient(ItemID.BURGER_PATTY, 1, Food.State.PREPARED),
        });
        HashMap<ItemID, FoodCombination> combinations = this.combinationsManager.findAllPossibleCombinations(items);
        for (FoodCombination combination : combinations.values()) {
            Gdx.app.log("combinations manager", combination.getResultItemId().getId());
            Gdx.app.log("combinations manager", String.valueOf(combination.isSatisfied(items)));
        }
    }
}
