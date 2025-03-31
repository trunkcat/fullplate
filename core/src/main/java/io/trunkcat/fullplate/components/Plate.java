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
import io.trunkcat.fullplate.components.base.FoodHolder;
import io.trunkcat.fullplate.components.base.FoodRecipe;
import io.trunkcat.fullplate.components.base.FoodRecipeManager;
import io.trunkcat.fullplate.components.base.FoodRecipeRenderer;

public class Plate extends FoodHolder {
    public Plate(int level) {
        super(ItemID.PLATE, level, new FoodRecipeManager());

        FoodRecipeRenderer burgerRenderer = new FoodRecipeRenderer() {
            @Override
            public void render(HashMap<ItemID, FoodRecipe.PartialIngredient> ingredients, float x, float y) {
                FoodRecipe.PartialIngredient burgerBun = ingredients.get(ItemID.BURGER_BUN);
                if (burgerBun != null) {
                    draw(burgerBun, x, y);
                } else {
                    return;
                }
                draw(burgerBun, x, y + 5);
            }
        };

        FoodRecipe simpleBurgerRecipe = new FoodRecipe(ItemID.SIMPLE_BURGER, burgerRenderer);
        simpleBurgerRecipe.addIngredient(new FoodRecipe.Ingredient(ItemID.BURGER_BUN, 1));
        simpleBurgerRecipe.addIngredient(new FoodRecipe.Ingredient(ItemID.BURGER_PATTY, 1, true, 1));

        FoodRecipe cheeseBurgerRecipe = new FoodRecipe(ItemID.CHEESE_BURGER, burgerRenderer);
        cheeseBurgerRecipe.addIngredient(new FoodRecipe.Ingredient(ItemID.BURGER_BUN, 1));
        cheeseBurgerRecipe.addIngredient(new FoodRecipe.Ingredient(ItemID.BURGER_PATTY, 1, true, 1));
        cheeseBurgerRecipe.addIngredient(new FoodRecipe.Ingredient(ItemID.CHEESE, 1, true, 1));
        cheeseBurgerRecipe.addIngredient(new FoodRecipe.Ingredient(ItemID.LETTUCE, 2, false, 1));

        FoodRecipe doubleCheeseBurgerRecipe = new FoodRecipe(ItemID.DOUBLE_CHEESE_BURGER, burgerRenderer);
        doubleCheeseBurgerRecipe.addIngredient(new FoodRecipe.Ingredient(ItemID.BURGER_BUN, 1));
        doubleCheeseBurgerRecipe.addIngredient(new FoodRecipe.Ingredient(ItemID.BURGER_PATTY, 1, true, 1));
        doubleCheeseBurgerRecipe.addIngredient(new FoodRecipe.Ingredient(ItemID.CHEESE, 1, true, 2));
        doubleCheeseBurgerRecipe.addIngredient(new FoodRecipe.Ingredient(ItemID.LETTUCE, 2, false, 1));

        FoodRecipe veggieBurgerRecipe = new FoodRecipe(ItemID.VEGGIE_BURGER, burgerRenderer);
        veggieBurgerRecipe.addIngredient(new FoodRecipe.Ingredient(ItemID.BURGER_BUN, 1));
        veggieBurgerRecipe.addIngredient(new FoodRecipe.Ingredient(ItemID.LETTUCE, 1, false, 1));
        veggieBurgerRecipe.addIngredient(new FoodRecipe.Ingredient(ItemID.TOMATO, 1, false, 1));
        veggieBurgerRecipe.addIngredient(new FoodRecipe.Ingredient(ItemID.CHEESE, 1, false, 1));

        this.recipeManager.addRecipe(simpleBurgerRecipe);
        this.recipeManager.addRecipe(cheeseBurgerRecipe);
        this.recipeManager.addRecipe(doubleCheeseBurgerRecipe);
        this.recipeManager.addRecipe(veggieBurgerRecipe);

        Array<FoodRecipe.PartialIngredient> items = new Array<>(new FoodRecipe.PartialIngredient[]{
            new FoodRecipe.PartialIngredient(ItemID.BURGER_BUN, 1, Food.State.PREPARED),
            new FoodRecipe.PartialIngredient(ItemID.BURGER_PATTY, 1, Food.State.PREPARED),
            new FoodRecipe.PartialIngredient(ItemID.CHEESE, 1, Food.State.PREPARED),
            new FoodRecipe.PartialIngredient(ItemID.LETTUCE, 1, Food.State.PREPARED),
        });
        HashMap<ItemID, FoodRecipe> recipes = this.recipeManager.getPossibleRecipes(items);
        for (FoodRecipe recipe : recipes.values()) {
            Gdx.app.log("recipe manager", recipe.getResultItemId().id);
            Gdx.app.log("recipe manager", String.valueOf(recipeManager.isSatisfied(items, recipe)));
//            recipe.render(null, items, 10, 10);
        }
    }
}
