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

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import java.util.HashMap;
import java.util.Map;

public class AnimationController {
    private final Map<String, Animation<TextureRegion>> animations;
    private String currentAnimation;
    private float stateTime;
    private boolean looping;

    public AnimationController() {
        animations = new HashMap<>();
        currentAnimation = null;
        stateTime = 0f;
        looping = false;
    }

    public void addAnimation(Object animationId, Animation<TextureRegion> animation) {
        String name = animationId.toString();
        animations.put(name, animation);
        if (currentAnimation == null) {
            currentAnimation = name;
        }
    }

    public void setAnimation(Object animationId, boolean looping) {
        String name = animationId.toString();
        if (!animations.containsKey(name)) {
            throw new IllegalArgumentException("No animation found");
        }
        if (!name.equals(currentAnimation)) {
            this.currentAnimation = name;
            this.stateTime = 0f;
            this.looping = looping;
        }
    }

    public void update(float delta) {
        stateTime += delta;
    }

    public TextureRegion getCurrentFrame() {
        if (currentAnimation == null || !animations.containsKey(currentAnimation)) {
            return null;
        }
        Animation<TextureRegion> animation = animations.get(currentAnimation);
        return animation.getKeyFrame(stateTime, looping);
    }

    public void reset() {
        stateTime = 0f;
    }

    public String getCurrentAnimationName() {
        return currentAnimation;
    }
}
