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

package io.trunkcat.fullplate.components.common;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop;

import java.util.function.Function;

import io.trunkcat.fullplate.components.debug.Debug;
import io.trunkcat.fullplate.screens.CustomStage;
import io.trunkcat.fullplate.utilities.AnimationController;

public class Entity extends StageActor {
    protected final Debug debug; // TODO: debug should be an actor within the group
    protected DragAndDrop.Source dragSource = null;
    protected DragAndDrop.Target dropTarget = null;
    protected AnimationController animationController = new AnimationController();

    public Entity() {
        super(StageActorType.ENTITY);
        debug = game.debug;
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);

        debug.setBatch(batch);
        debug.font.setColor(Color.WHITE);

        float visibleWidth = getWidth() * getScaleX();

        Function<String, Float> centerX = (String text) -> {
            float lineWidth = debug.getLineWidth(text);
            return getX() + (visibleWidth - lineWidth) / 2;
        };

        float y = getY();

        y -= 10;
        String className = getClass().getSimpleName();
        debug.text(className, centerX.apply(className), y);

        y -= debug.getLineHeight() + 5;
        String position = "(" + getX() + ", " + getY() + ", " + getZIndex() + ")";
        debug.text(position, centerX.apply(position), y);

        // todo: enhance this
    }

    @Override
    protected void setStage(Stage stage) {
        super.setStage(stage);

        if (stage instanceof CustomStage) {
            CustomStage customStage = (CustomStage) stage;

            DragAndDrop.Source dragSource = getDragSource();
            if (dragSource != null) {
                customStage.getDragAndDrop().addSource(dragSource);
            }
            DragAndDrop.Target dropTarget = getDropTarget();
            if (dropTarget != null) {
                customStage.getDragAndDrop().addTarget(dropTarget);
            }
        }
    }

    public DragAndDrop.Source getDragSource() {
        return dragSource;
    }

    public DragAndDrop.Target getDropTarget() {
        return dropTarget;
    }
}
