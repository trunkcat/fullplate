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

import static io.trunkcat.fullplate.components.debug.DebugLabelStyles.label;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Array;

import io.trunkcat.fullplate.components.ItemID;

public class CompositeFood extends Food {
    private final Array<FoodCombination.PartialIngredient> ingredients = new Array<>();
    private final FoodCombinationsManager combinationsManager;
    private FoodCombination lockedCombination;

    public CompositeFood(FoodCombinationsManager combinationsManager) {
        super(ItemID.COMPOSITE_FOOD, 0, State.UNPREPARED);
        this.combinationsManager = combinationsManager;
    }

    @Override
    public void setCurrentState(State newState) {
        super.setCurrentState(newState);

        for (FoodCombination.PartialIngredient ingredient : new Array.ArrayIterator<>(ingredients)) {
            // TODO: should I only apply the state if it's .ordinal is greater?
            ingredient.setState(newState);
        }
    }

    public void reset() {
        this.itemId = ItemID.COMPOSITE_FOOD;
        this.level = 0;
        this.currentState = State.UNPREPARED;
        this.lockedCombination = null;
        this.ingredients.clear();
    }

    @Override
    public Texture getTexture() {
        return null;
    }

    public boolean isEmpty() {
        return ingredients.isEmpty();
    }

    public void addIngredient(FoodCombination.PartialIngredient ingredient) {
        if (!isCombinationLocked()) {
            ingredients.add(ingredient);
        }
    }

    public Array<FoodCombination.PartialIngredient> getIngredients() {
        return ingredients;
    }

    public void lockCombination(FoodCombination combination) {
        this.lockedCombination = combination;
        this.itemId = combination.getResultItemId();
    }

    public FoodCombination getLockedCombination() {
        return lockedCombination;
    }

    public boolean isCombinationLocked() {
        return lockedCombination != null;
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        FoodCombination combination = isCombinationLocked()
            ? lockedCombination
            : combinationsManager.findOnePossibleCombination(ingredients);

        // FIXME: it's not really recommended to setSize inside draw(). find a way to make this happen in act()
        if (combination != null) {
            IngredientsRenderer.Rect rect = combination.render(batch, ingredients, getX(), getY());
            setSize(rect.getWidth(), rect.getHeight()); // setSize inexpensive if the size haven't changed
        } else {
            setSize(0, 0); // setSize inexpensive if the size haven't changed
        }

        super.draw(batch, parentAlpha);
    }

    @Override
    public Table getDebugTable() {
        Table table = super.getDebugTable();

        table.add(label("combination locked: " + isCombinationLocked())).row();
        FoodCombination combination = combinationsManager.findOnePossibleCombination(ingredients);
        table.add(label("combination: " + (combination == null ? null : combination.getResultItemId()))).row();
        table.add(label("ingredients: " + ingredients.size)).row();
        for (FoodCombination.PartialIngredient ingredient : new Array.ArrayIterator<>(ingredients)) {
            table.add(label("     - " + ingredient.itemId.toString())).row();
        }

        return table;
    }
}
