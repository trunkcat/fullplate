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

package io.trunkcat.fullplate.utilities;

import com.badlogic.gdx.scenes.scene2d.Actor;

public class TransformData extends Bounds {
    public float width;
    public float height;
    public int zIndex;
    public float scaleX;
    public float scaleY;
    public float originX;
    public float originY;

    public TransformData(float x, float y, float width, float height, int zIndex, float scaleX, float scaleY, float originX, float originY) {
        super(x, y, width, height);
        this.zIndex = zIndex;
        this.scaleX = scaleX;
        this.scaleY = scaleY;
        this.originX = originX;
        this.originY = originY;
    }

    public TransformData(Actor actor) {
        this(
            actor.getX(),
            actor.getY(),
            actor.getWidth(),
            actor.getHeight(),
            actor.getZIndex(),
            actor.getScaleX(),
            actor.getScaleY(),
            actor.getOriginX(),
            actor.getOriginY()
        );
    }

    public void apply(Actor actor) {
        super.apply(actor);
        actor.setZIndex(zIndex);
        actor.setScale(scaleX, scaleY);
        actor.setOrigin(originX, originY);
    }
}
