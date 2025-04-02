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

import java.util.HashMap;

import io.trunkcat.fullplate.components.base.Food;
import io.trunkcat.fullplate.components.base.FoodCombination;
import io.trunkcat.fullplate.components.base.FoodCombinationsManager;
import io.trunkcat.fullplate.components.base.FoodCooker;
import io.trunkcat.fullplate.components.base.IngredientsRenderer;

public class FryingPan extends FoodCooker {
    public FryingPan(int level) {
        super(ItemID.FRYING_PAN, level, FryingPan.COMBINATION_MANAGER);
    }

    static FoodCombinationsManager COMBINATION_MANAGER = new FoodCombinationsManager();

    static {
        IngredientsRenderer fryingPanRenderer = new IngredientsRenderer() {
            @Override
            public void render(HashMap<ItemID, FoodCombination.PartialIngredient> ingredients, float x, float y) {
                FoodCombination.PartialIngredient oil = ingredients.get(ItemID.OLIVE_OIL);
                if (oil != null) {
                    draw(oil, x, y);
                }
                FoodCombination.PartialIngredient burgerPatty = ingredients.get(ItemID.BURGER_PATTY);
                if (burgerPatty != null) {
                    y += 5;
                    draw(burgerPatty, x, y);
                }
            }
        };

        // NOTE: until combinations manager implements combination validation upon adding,
        // all frying pan combinations must top it of with a required ingredient.
        // i.e. the last item triggers the cooking process, so, make sure combinations are unique and
        // the very last item is required.

        FoodCombination friedBurgerPatty = new FoodCombination(ItemID.BURGER_PATTY, Food.State.PREPARED, fryingPanRenderer)
            .addIngredient(new FoodCombination.Ingredient(ItemID.BURGER_PATTY, 1, Food.State.UNPREPARED));
        friedBurgerPatty.setCookingTime(10f);

        COMBINATION_MANAGER.addCombination(friedBurgerPatty);
    }
}
