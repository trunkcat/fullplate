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

import com.badlogic.gdx.graphics.g2d.Batch;

import io.trunkcat.fullplate.utilities.Rect;

public class Item extends KitchenEntity {
    private final IngredientAssembly ingredientAssembly;

    public Item(ID id, Type type, IngredientAssembly ingredientAssembly) {
        super(id, type);
        if (!ingredientAssembly.hasLockedRecipe()) {
            throw new IllegalArgumentException("Item assembly must have the recipe locked for proper rendering.");
        }
        this.ingredientAssembly = ingredientAssembly;
    }

    public IngredientAssembly getIngredientAssembly() {
        return ingredientAssembly;
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        if (ingredientAssembly.hasLockedRecipe()) { // must have it locked if its a `Food`.
            Recipe lockedRecipe = ingredientAssembly.getLockedRecipe();
            // todo: consider scale to rectangle scale.
            Rect rect = ingredientAssembly.render(batch, lockedRecipe, getX(), getY());
            setSize(rect.getWidth(), rect.getHeight());
        } else {
            throw new IllegalStateException("Item assembly must have the recipe locked for proper rendering.");
        }

        super.draw(batch, parentAlpha);
    }
}
