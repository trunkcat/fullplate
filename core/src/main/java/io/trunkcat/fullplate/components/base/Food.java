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

import com.badlogic.gdx.graphics.Texture;

import io.trunkcat.fullplate.components.ItemID;
import io.trunkcat.fullplate.utilities.AssetManager;

public abstract class Food extends SellableItem {
    protected State currentState;

    public enum State {
        UNPREPARED("unprepared"),
        PREPARING("preparing"),
        UNDER_PREPARED("under-prepared"),
        PREPARED("prepared"),
        //        OVER_PREPARED("over-prepared"), // TODO: add this state
        RUINED("ruined");

        public final String value;

        State(String value) {
            this.value = value;
        }
    }

    public Food(ItemID itemId, int level, State defaultState) {
        super(itemId, level);
        this.currentState = defaultState;
    }

    public State getCurrentState() {
        return currentState;
    }

    public void setCurrentState(State newState) {
        if (newState.ordinal() < currentState.ordinal()) {
            return; // Shouldn't be able to go back.
        }
        currentState = newState;
    }

    public Texture getTexture() {
        return loadStateTexture(itemId, currentState);
    }

    public static Texture loadStateTexture(ItemID itemId, State state) {
        // TODO: consider level
        return AssetManager.loadTexture("items/" + itemId.id + "_" + state.value + ".png");
    }
}
