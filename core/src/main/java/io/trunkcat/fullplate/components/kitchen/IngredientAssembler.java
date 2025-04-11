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

import static io.trunkcat.fullplate.components.debug.DebugLabelStyles.label;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Array;

import io.trunkcat.fullplate.utilities.Rect;

public abstract class IngredientAssembler extends UpgradableKitchenEntity {
    public enum PositionState {
        LOCKED,
        UNLOCKED,
        TAKEN_OUT
    }

    protected final IngredientAssembly ingredientAssembly;
    protected final RecipeCollection recipeCollection;
    protected PositionState positionState = PositionState.LOCKED;
    private final Vector2 assemblyOrigin = new Vector2(0, 0);

    public IngredientAssembler(ID id, Type type, int level, int maxLevel, RecipeCollection recipeCollection) {
        super(id, type, level, maxLevel);
        this.recipeCollection = recipeCollection;
        this.ingredientAssembly = new IngredientAssembly();
    }

    public IngredientAssembly getIngredientAssembly() {
        return ingredientAssembly;
    }

    protected void setAssemblyOffset(float x, float y) {
        if (x < 0 || y < 0) {
            throw new IllegalArgumentException("Offsets cannot be negative.");
        }
        assemblyOrigin.set(getX() + x, getY() + y);
    }

    public void reset() {
        ingredientAssembly.reset();
        positionState = PositionState.LOCKED;
    }

    public Rect renderAssembly(Batch batch) {
        Recipe recipe = ingredientAssembly.hasLockedRecipe()
            ? ingredientAssembly.getLockedRecipe()
            : recipeCollection.findSatisfiableRecipe(ingredientAssembly);
        if (recipe != null) {
            return ingredientAssembly.render(batch, recipe, assemblyOrigin.x, assemblyOrigin.y);
        }
        return new Rect(assemblyOrigin.x, assemblyOrigin.y, 0, 0);
    }


    @Override
    public Table getDebugTable() {
        Table table = super.getDebugTable();

        table.add(label("recipe locked: " + ingredientAssembly.hasLockedRecipe())).row();
        Recipe recipe = ingredientAssembly.hasLockedRecipe()
            ? ingredientAssembly.getLockedRecipe()
            : recipeCollection.findSatisfiableRecipe(ingredientAssembly);
        table.add(label("recipe: " + (recipe == null ? null : recipe.getResultItem()))).row();
        table.add(label("ingredients: " + ingredientAssembly.getIngredients().size)).row();
        for (PartialIngredient ingredient : new Array.ArrayIterator<>(ingredientAssembly.getIngredients())) {
            table.add(label("     - " + ingredient.getId())).row();
        }

        return table;
    }
}
