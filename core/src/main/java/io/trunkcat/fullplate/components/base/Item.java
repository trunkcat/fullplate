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

import io.trunkcat.fullplate.components.ItemID;
import io.trunkcat.fullplate.components.common.StageActor;
import io.trunkcat.fullplate.utilities.AssetManager;

public abstract class Item extends StageActor {
    protected ItemID itemId;
    protected int level;

    public Item(ItemID itemId, int level) {
        this.itemId = itemId;
        this.level = level;
    }

    public static Texture loadTexture(ItemID itemId) {
        // TODO: consider level
        return AssetManager.loadTexture("items/" + itemId.getId() + ".png");
    }

    public static Texture loadTexture(ItemID itemId, String param) {
        // TODO: consider level
        return AssetManager.loadTexture("items/" + itemId.getId() + "_" + param + ".png");
    }

    public ItemID getItemId() {
        return itemId;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public abstract Texture getTexture();

    @Override
    public void act(float delta) {
        super.act(delta);

        // TODO: figure out animations
        Texture currentTexture = getTexture();
        if (currentTexture != null) {
            setSize(
                currentTexture.getWidth() * getScaleX(),
                currentTexture.getHeight() * getScaleY()
            );
        }
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        Texture currentTexture = getTexture();
        if (currentTexture != null) {
            batch.draw(
                currentTexture, getX(), getY(), getOriginX(), getOriginY(),
                getWidth(), getHeight(), getScaleX(), getScaleY(), getRotation(),
                0, 0, currentTexture.getWidth(), currentTexture.getHeight(), false, false
            );
        }

        super.draw(batch, parentAlpha);
    }
}
