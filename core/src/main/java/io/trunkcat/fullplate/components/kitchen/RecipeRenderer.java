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

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;

import java.util.HashMap;

import io.trunkcat.fullplate.utilities.AssetManager;
import io.trunkcat.fullplate.utilities.Constants;
import io.trunkcat.fullplate.utilities.Rect;

public abstract class RecipeRenderer {
    protected Batch batch;

    public RecipeRenderer() {
    }

    public void setBatch(Batch batch) {
        this.batch = batch;
    }

    protected void draw(Rect rect, PartialIngredient item, float relativeX, float relativeY) {
        // todo: convert this to animation somehow
        String path = String.join("/",
            Constants.KITCHEN_ENTITIES_PATH,
            // todo: make partial ingredient and assembly and replace this with type.
            KitchenEntity.Type.FOOD.toString(),
            item.getId().toString() + "-" + item.getState().toString()
        ) + ".png";

        Texture texture = AssetManager.loadTexture(path);

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

    public Rect render(
//        TreeMap<Integer, HashMap<KitchenEntity.ID, PartialIngredient>> ingredients,
        HashMap<KitchenEntity.ID, PartialIngredient> ingredients,
        float worldX, float worldY
//        float scaleX, float scaleY
    ) {
        if (batch == null) {
            throw new IllegalStateException("Batch is not set");
        }
        return new Rect(worldX, worldY, 0, 0);
    }
}
