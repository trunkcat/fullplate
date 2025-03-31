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
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop;
import com.badlogic.gdx.utils.Array;

import java.util.HashMap;

import io.trunkcat.fullplate.components.ItemID;

public abstract class FoodHolder extends Item {
    private final Array<FoodRecipe.PartialIngredient> ingredients = new Array<>();
    protected FoodRecipeManager recipeManager;

    public FoodHolder(ItemID itemId, int level, FoodRecipeManager recipeManager) {
        super(itemId, level);
        this.recipeManager = recipeManager;
    }

    @Override
    public Texture getTexture() {
        return loadTexture(itemId);
    }

    // TODO: figure out drag
    @Override
    public DragAndDrop.Source getDragSource() {
        return new DragAndDrop.Source(this) {
            @Override
            public DragAndDrop.Payload dragStart(InputEvent event, float x, float y, int pointer) {
                DragAndDrop.Payload payload = new DragAndDrop.Payload();
                payload.setDragActor(FoodHolder.this);
                Vector2 startPosition = new Vector2(
                    FoodHolder.this.getX(),
                    FoodHolder.this.getY()
                );
                payload.setObject(startPosition);
                return payload;
            }

            @Override
            public void dragStop(InputEvent event, float x, float y, int pointer, DragAndDrop.Payload payload, DragAndDrop.Target target) {
                super.dragStop(event, x, y, pointer, payload, target);
                if (target == null || target.getActor() == null) {
                    Vector2 startPosition = (Vector2) payload.getObject();
                    FoodHolder.this.setPosition(startPosition.x, startPosition.y);
                }
                // TODO: else, if its a customer...?
                //  check orders, choose satisfying order's recipe, fulfill order
            }
        };
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);

        FoodRecipe recipe = recipeManager.getFirstMatchingRecipe(ingredients);
        if (recipe != null) {
            recipe.render(batch, ingredients, FoodHolder.this.getX(), FoodHolder.this.getY());
        }
    }

    @Override
    public DragAndDrop.Target getDropTarget() {
        return new DragAndDrop.Target(this) {
            @Override
            public boolean drag(DragAndDrop.Source source, DragAndDrop.Payload payload, float x, float y, int pointer) {
                if (payload == null) {
                    return false;
                }
                Actor dragActor = payload.getDragActor();
                if (!(dragActor instanceof Food)) {
                    return false;
                }
                Food food = (Food) dragActor;
                Gdx.app.log("Food Holder", "Attempting to drop " + food.getItemId() + " into " + FoodHolder.this.itemId);
                if (food.getCurrentState() != Food.State.PREPARED) {
                    Gdx.app.log("Food Holder", "Food is not prepared");
                    return false;
                }
                return recipeManager.canAddIngredient(ingredients, FoodHolder.toPartialIngredient(food));
            }

            @Override
            public void drop(DragAndDrop.Source source, DragAndDrop.Payload payload, float x, float y, int pointer) {
                if (payload == null) {
                    return;
                }
                Actor dragActor = payload.getDragActor();
                if (!(dragActor instanceof Food)) {
                    return;
                }
                Food food = (Food) dragActor;
                ingredients.add(FoodHolder.toPartialIngredient(food));
                Gdx.app.log("Food Holder", "Dropped " + food.getItemId() + " into " + FoodHolder.this.itemId);
                food.remove();

                Gdx.app.log("Food Holder", "Ingredients: ");
                ingredients.forEach(item -> Gdx.app.log("Food Holder", "    " + item.getItemId()));

                HashMap<ItemID, FoodRecipe> possibleRecipes = recipeManager.getPossibleRecipes(ingredients);
                Gdx.app.log("Food Holder", "Possible recipes (" + possibleRecipes.size() + "): ");
                possibleRecipes.forEach((item, recipe) -> Gdx.app.log("Food Holder", "    " + item.id));

                FoodRecipe recipe = recipeManager.getFirstMatchingRecipe(ingredients);
                Gdx.app.log("Food Holder", "Using recipe: " + recipe.getResultItemId());
            }
        };
    }

    static FoodRecipe.PartialIngredient toPartialIngredient(Food food) {
        return new FoodRecipe.PartialIngredient(food.getItemId(), 1, food.getCurrentState());
    }
}
