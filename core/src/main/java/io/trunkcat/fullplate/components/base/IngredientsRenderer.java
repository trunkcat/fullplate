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
import com.badlogic.gdx.graphics.g2d.Batch;

import java.util.HashMap;

import io.trunkcat.fullplate.components.ItemID;

public abstract class IngredientsRenderer {
    protected Batch batch;

    protected IngredientsRenderer() {
    }

    public void setBatch(Batch batch) {
        this.batch = batch;
    }

    public static class Rect {
        public float x;
        public float y;
        public float right;
        public float left;
        public float top;
        public float bottom;

        public Rect(float x, float y, float right, float left, float top, float bottom) {
            this.x = x;
            this.y = y;
            this.right = right;
            this.left = left;
            this.top = top;
            this.bottom = bottom;
        }

        public Rect(float x, float y, float width, float height) {
            this(x, y, x + width, x, y + height, y);
        }

        public float getWidth() {
            return Math.abs(right - left);
        }

        public float getHeight() {
            return Math.abs(top - bottom);
        }

        @Override
        public String toString() {
            return "Rect{" +
                "x=" + x +
                ", y=" + y +
                ", right=" + right +
                ", left=" + left +
                ", top=" + top +
                ", bottom=" + bottom +
                ", width=" + getWidth() +
                ", height=" + getHeight() +
                '}';
        }
    }

    protected void draw(Rect rect, FoodCombination.PartialIngredient item, float relativeX, float relativeY) {
        Texture texture = Food.loadTexture(item.getItemId(), item.getState().value);

        if (texture != null) {
            float textureWidth = texture.getWidth();
            float textureHeight = texture.getHeight();

            // TODO: include composite food scale as well

            // bounds of the item in world terms
            float left = rect.x + relativeX;
            float right = left + textureWidth;
            float bottom = rect.y + relativeY;
            float top = bottom + textureHeight;

            if (right > rect.right) {
                rect.right = right;
            }
            if (left < rect.left) {
                rect.left = left;
            }
            if (top > rect.top) {
                rect.top = top;
            }
            if (bottom < rect.bottom) {
                rect.bottom = bottom;
            }

            batch.draw(texture, rect.x + relativeX, rect.y + relativeY);
        }
    }

    public Rect render(HashMap<ItemID, FoodCombination.PartialIngredient> ingredients, float worldX, float worldY) {
        if (batch == null) {
            throw new IllegalStateException("Batch is not set");
        }

        return new Rect(worldX, worldY, 0, 0);
    }
}
