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
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import io.trunkcat.fullplate.components.common.Entity;
import io.trunkcat.fullplate.utilities.Constants;

public abstract class KitchenEntity extends Entity {
    protected final ID id;
    protected final Type type;

    public KitchenEntity(ID id, Type type) {
        this.id = id;
        this.type = type;
    }

    // todo: make this texture packs and atlases
    public String getAnimationPath() {
        return String.join("/",
            Constants.KITCHEN_ENTITIES_PATH,
            type.toString(), id.toString()
        ) + ".png";
    }

    public String getAnimationPath(Object argument) {
        return String.join("/",
            Constants.KITCHEN_ENTITIES_PATH,
            type.toString(), id.toString() + "-" + argument.toString()
        ) + ".png";
    }

    @Override
    public void act(float delta) {
        super.act(delta);

        animationController.update(delta);

        TextureRegion currentFrame = animationController.getCurrentFrame();
        if (currentFrame != null) {
            float actualWidth = currentFrame.getRegionWidth() * getScaleX();
            float actualHeight = currentFrame.getRegionHeight() * getScaleY();
            setSize(actualWidth, actualHeight);
        }
    }

    protected void drawTexture(Batch batch, TextureRegion textureRegion) {
        if (textureRegion != null) {
            batch.draw(
                textureRegion, getX(), getY(),
                getOriginX(), getOriginY(), getWidth(), getHeight(),
                getScaleX(), getScaleY(), getRotation()
            );
        }
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        drawTexture(batch, animationController.getCurrentFrame());

        super.draw(batch, parentAlpha);
    }

    public enum ID {
        BUN,
        BUN_CRATE,
        PATTY,
        PATTY_CRATE,
        PAN,
        PLATE,
        BURGER
    }

    // todo: should merge together with ID?
    public enum Type {
        FOOD,
        STORE,
        HOLDER,
        SERVABLE_HOLDER,
        COOKER,
    }
}
