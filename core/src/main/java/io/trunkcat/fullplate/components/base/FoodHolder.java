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
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop;

import io.trunkcat.fullplate.components.ItemID;
import io.trunkcat.fullplate.utilities.TransformData;

// TODO: make servable food holder!
public abstract class FoodHolder extends Item {
    protected final CompositeFood compositeFood;
    protected FoodCombinationsManager combinationsManager;

    public FoodHolder(ItemID itemId, int level, FoodCombinationsManager combinationsManager) {
        super(itemId, level);
        this.combinationsManager = combinationsManager;
        this.compositeFood = new CompositeFood(combinationsManager);
    }

    @Override
    public Texture getTexture() {
        return loadTexture(itemId);
    }

    @Override
    public void act(float delta) {
        super.act(delta);

        // TODO: have safe areas for each holder, and pass the x and y of that area
        alignCompositeFood();
    }

    private void alignCompositeFood() {
        compositeFood.setPosition(getX(), getY());
        compositeFood.setScale(getScaleX(), getScaleY());
        compositeFood.setZIndex(getZIndex());
    }

    @Override
    protected void setStage(Stage stage) {
        super.setStage(stage);
        if (stage != null) {
            stage.addActor(compositeFood);
        } else if (compositeFood != null) {
            // remove the composite food assigned with this holder once this holder is removed.
            compositeFood.remove(); // TODO: make them groups
        }
    }

    @Override
    public DragAndDrop.Source getDragSource() {
        return new DragAndDrop.Source(this) {
            @Override
            public DragAndDrop.Payload dragStart(InputEvent event, float x, float y, int pointer) {
                DragAndDrop.Payload payload = new DragAndDrop.Payload();

                TransformData position = new TransformData(FoodHolder.this);
                payload.setObject(position);

                setZIndex(getStage().getActors().size + 1);
                payload.setDragActor(FoodHolder.this);

                return payload;
            }

            @Override
            public void dragStop(InputEvent event, float x, float y, int pointer, DragAndDrop.Payload payload, DragAndDrop.Target target) {
                super.dragStop(event, x, y, pointer, payload, target);

                if (target == null || target.getActor() == null) {
                    TransformData startPosition = (TransformData) payload.getObject();
                    startPosition.apply(payload.getDragActor());
                    return;
                }

                // TODO: else, if its a customer...?
                //  check orders, choose satisfying order's combination, fulfill order
            }
        };
    }

    @Override
    public DragAndDrop.Target getDropTarget() {
        return new DragAndDrop.Target(this) {
            @Override
            public boolean drag(DragAndDrop.Source source, DragAndDrop.Payload payload, float x, float y, int pointer) {
                if (payload == null) {
                    return false;
                }
                Actor dragActor = payload.getDragActor();

                if (dragActor instanceof Food) {
                    Food food = (Food) dragActor;
                    return combinationsManager.canAddIngredient(
                        compositeFood.getIngredients(),
                        FoodCombination.PartialIngredient.from(food)
                    );
                }

                return false;
            }

            @Override
            public void drop(DragAndDrop.Source source, DragAndDrop.Payload payload, float x, float y, int pointer) {
                if (payload == null) {
                    return;
                }
                Actor dragActor = payload.getDragActor();

                if (dragActor instanceof Food) {
                    Food food = (Food) dragActor;
                    compositeFood.addIngredient(FoodCombination.PartialIngredient.from(food));
                    dispatchStageEvent(new KitchenEvent.FoodConsumeEvent(
                        food,
                        source.getActor(),
                        FoodHolder.this
                    ));
                }
            }
        };
    }
}
