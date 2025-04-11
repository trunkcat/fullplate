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
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop;

import io.trunkcat.fullplate.utilities.AnimationUtils;
import io.trunkcat.fullplate.utilities.Constants;
import io.trunkcat.fullplate.utilities.Rect;
import io.trunkcat.fullplate.utilities.TransformData;

public abstract class Holder extends IngredientAssembler {
    public enum State {
        CLEAN,
        DIRTY
    }

    private State state = State.CLEAN;

    public Holder(ID id, int level, int maxLevel, RecipeCollection recipeCollection) {
        super(id, Type.HOLDER, level, maxLevel, recipeCollection);

        animationController.addAnimation(
            State.CLEAN,
            AnimationUtils.createSingleFrameAnimation(getAnimationPath(State.CLEAN))
        );
        animationController.addAnimation(
            State.DIRTY,
            AnimationUtils.createSingleFrameAnimation(getAnimationPath(State.DIRTY))
        );
        animationController.setAnimation(State.CLEAN, true);

        dragSource = new DragAndDrop.Source(this) {
            @Override
            public DragAndDrop.Payload dragStart(InputEvent event, float x, float y, int pointer) {
                DragAndDrop.Payload payload = new DragAndDrop.Payload();
                payload.setObject(new TransformData(Holder.this));
                payload.setDragActor(Holder.this);
                setZIndex(Constants.DRAG_ACTOR_Z_INDEX);
                return payload;
            }

            @Override
            public void dragStop(InputEvent event, float x, float y, int pointer, DragAndDrop.Payload payload, DragAndDrop.Target target) {
                super.dragStop(event, x, y, pointer, payload, target);

                if (target == null || target.getActor() == null) {
                    TransformData initialTransform = (TransformData) payload.getObject();
                    initialTransform.apply(payload.getDragActor());
                }
            }
        };

        dropTarget = new DragAndDrop.Target(this) {
            @Override
            public boolean drag(DragAndDrop.Source source, DragAndDrop.Payload payload, float x, float y, int pointer) {
                if (payload == null) return false;
                Actor dragActor = payload.getDragActor();
                if (dragActor == null) return false;

                if (dragActor instanceof Item) {
                    Item item = (Item) dragActor;
                    return recipeCollection.isPartialIngredientsAddable(ingredientAssembly, item.getIngredientAssembly());
                }

                // todo: handle food
                return false;
            }

            @Override
            public void drop(DragAndDrop.Source source, DragAndDrop.Payload payload, float x, float y, int pointer) {
                Actor dragActor = payload.getDragActor();

                if (dragActor instanceof Item) {
                    Item item = (Item) dragActor;
                    ingredientAssembly.addIngredients(item.getIngredientAssembly());
                    dispatchStageEvent(new KitchenEvent.ItemConsumeEvent(item, source.getActor(), Holder.this));
                }

                dragActor.remove();
            }
        };
    }

    @Override
    public void reset() {
        super.reset();
        this.state = State.CLEAN;
    }

    @Override
    public void act(float delta) {
        super.act(delta);

        animationController.setAnimation(state, true);
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        TextureRegion currentFrame = animationController.getCurrentFrame();

        super.draw(batch, parentAlpha);

        Rect assembler = new Rect(getX(), getY(), currentFrame.getRegionWidth(), currentFrame.getRegionHeight());
        if (positionState != PositionState.TAKEN_OUT) {
            Rect assembly = renderAssembly(batch);
            Rect merged = assembler.union(assembly);
            setSize(merged.getWidth(), merged.getHeight());
        } else {
            setSize(assembler.getWidth(), assembler.getHeight());
        }

        // drawTexture(batch, currentFrame);
        // todo: for a more better 3d look, backdrop could be drawn first, then the assembly,
        //  finally the texture that can be drawn on it. this gives fake, but real feeling 3d feel.
    }
}
