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

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.utils.Array;

import java.util.HashMap;

import io.trunkcat.fullplate.components.common.Entity;
import io.trunkcat.fullplate.utilities.AssetManager;
import io.trunkcat.fullplate.utilities.Rect;

public class OrderBox extends Entity {

    private final Array<Recipe> ordersLeft;
    private float timeProgress;

    public OrderBox(Array<Recipe> ordersLeft) {
        this.ordersLeft = ordersLeft;
    }

    public void setTimeProgress(float timeProgress) {
        this.timeProgress = timeProgress;
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        Rect rect = new Rect(getX(), getY(), 0, 0);

        int[] splits2 = {7, 7, 7, 7};
        NinePatch background = AssetManager.ninePatchFromTexture("orderbox-background.png", splits2);
        background.draw(batch, rect.x, rect.y, getWidth(), getHeight());

        int paddingX = 20;
        int paddingY = 20;
        for (Recipe order : new Array.ArrayIterator<>(ordersLeft)) {
            RecipeRenderer renderer = order.getRenderer();
            renderer.setBatch(batch);
            HashMap<KitchenEntity.ID, PartialIngredient> ingredients = new HashMap<>();
            for (Ingredient ingredient : order.getIngredients().values()) {
                ingredients.put(ingredient.getId(), ingredient.partial());
            }
            Rect orderRect = renderer.render(
                ingredients,
                rect.x + paddingX,
                rect.y + rect.getHeight() + paddingY
            );
            rect.merge(orderRect);
        }

        // todo: less bright colors
        Color timeProgressColor = new Color(1, 1, 0.3f, 1);
        float doubled = timeProgress * 2;
        if (timeProgress < 0.5f) {
            timeProgressColor.r = doubled;
        } else {
            timeProgressColor.g = 2 - doubled;
        }

        float maxHeight = rect.getHeight() - paddingY;
        float height = (1 - timeProgress) * maxHeight;

        int splitY = 7;
        if (height < splitY * 2) {
            splitY = (int) Math.floor((double) height / 2);
        }

        int[] splits = {7, 7, splitY, splitY};
        NinePatch ninePatch = AssetManager.ninePatchFromTexture("progress-bar.png", splits);
        ninePatch.setColor(timeProgressColor);

        int spaceX = 20;
        ninePatch.draw(batch, rect.x + rect.getWidth() + spaceX, rect.y + paddingY, 10, height);

        rect.right += spaceX + 10 + paddingX;
        rect.top += paddingY;
        setSize(rect.getWidth(), rect.getHeight());

        super.draw(batch, parentAlpha);
    }
}
