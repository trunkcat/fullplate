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

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop;

import io.trunkcat.fullplate.components.ItemID;
import io.trunkcat.fullplate.utilities.Constants;
import io.trunkcat.fullplate.utilities.TransformData;

public abstract class FoodCooker extends Item {
    public enum State {
        IDLE("idle"),
        ACTIVE("active");

        public final String value;

        State(String value) {
            this.value = value;
        }
    }

    protected enum CompositeFoodState {
        LOCKED,
        UNLOCKED,
        TAKEN_OUT
    }

    protected State state = State.IDLE;
    protected final CompositeFood compositeFood;
    protected CompositeFoodState compositeFoodState;
    protected final FoodCombinationsManager combinationsManager;

    protected float timeLeft = 0f;

    public FoodCooker(ItemID itemId, int level, FoodCombinationsManager combinationsManager) {
        super(itemId, level);
        this.combinationsManager = combinationsManager;
        this.compositeFood = new CompositeFood(combinationsManager);
        this.compositeFoodState = CompositeFoodState.LOCKED;
    }

    @Override
    public Texture getTexture() {
        // TODO: return current frame in animation
        return loadTexture(itemId, state.value);
    }

    public State getState() {
        return state;
    }

    private float getCookTime(FoodCombination combination) {
        float baseCookTime = combination.getCookingTime();
        float leastPossibleTime = baseCookTime * Constants.COOK_TIME_MIN_FACTOR;
        float cookerDependentTime = baseCookTime / (1 + (level * Constants.FOOD_COOKER_LEVEL_SPEED_MODIFIER));
        return Math.min(combination.getCookingTime(), Math.max(cookerDependentTime, leastPossibleTime));
    }

    private float getOvercookTime(FoodCombination combination) {
        float baseOvercookTime = combination.getOvercookingTime();
        // TODO: can have a little bit more complex time resolution
        return baseOvercookTime * 1;
    }

    private FoodCombination getCookableCombination() {
        for (FoodCombination combination :
            combinationsManager
                .findAllPossibleCombinations(compositeFood.getIngredients())
                .values()) {
            if (combination.isCookingPossible()) {
                return combination;
            }
        }
        return null;
    }

    private void cook(float delta) {
        if (compositeFood.isEmpty()) return;

        if (state == State.IDLE) {
            FoodCombination resolvedCombination = getCookableCombination();
            if (resolvedCombination == null) {
                return;
            }
            if (combinationsManager.isSatisfied(compositeFood.getIngredients(), resolvedCombination)) {
                compositeFoodState = CompositeFoodState.LOCKED;
                state = State.ACTIVE;
                compositeFood.setCurrentState(Food.State.UNDER_PREPARED);
                compositeFood.lockCombination(resolvedCombination);
                timeLeft = getCookTime(resolvedCombination);
                Gdx.app.log("Food Cooker", "Started cooking: " + resolvedCombination.getResultItemId() + " for " + timeLeft + " seconds.");
            }
        } else if (state == State.ACTIVE) {
            if (compositeFoodState == CompositeFoodState.TAKEN_OUT) {
                return;
            }

            Food.State foodState = compositeFood.getCurrentState();
            FoodCombination resolvedCombination = compositeFood.getLockedCombination();

            if (foodState == Food.State.UNDER_PREPARED) {
                timeLeft -= delta;
                if (timeLeft <= 0) {
                    Gdx.app.log("Food Cooker", "Finished cooking: " + resolvedCombination.getResultItemId());
                    compositeFoodState = CompositeFoodState.UNLOCKED;
                    compositeFood.setCurrentState(Food.State.PREPARED); // TODO: resolvedCombination.getResultState() ??

                    if (resolvedCombination.isOvercookingPossible()) {
                        timeLeft = getOvercookTime(resolvedCombination);
                        Gdx.app.log("Food Cooker", "Started overcooking");
                    }
                }
            } else if (foodState == Food.State.PREPARED) {
                if (resolvedCombination.isOvercookingPossible()) {
                    timeLeft -= delta;
                    if (timeLeft <= 0) {
                        Gdx.app.log("Food Cooker", "Food is now ruined");
                        compositeFood.setCurrentState(Food.State.RUINED);
                    }
                }
            }
        }
    }

    @Override
    public void act(float delta) {
        super.act(delta);

        cook(delta);

        if (compositeFoodState != CompositeFoodState.TAKEN_OUT) {
            // TODO: have safe areas for each holder, and pass the x and y of that area
            alignCompositeFood();
        }
    }

    private void alignCompositeFood() {
        compositeFood.setPosition(getX(), getY());
        compositeFood.setScale(getScaleX(), getScaleY());
        compositeFood.setZIndex(getZIndex());
    }

    private void reset() {
        compositeFood.reset();
        compositeFood.remove();
        getStage().addActor(compositeFood);
        compositeFoodState = CompositeFoodState.LOCKED;
        state = State.IDLE;
    }

    @Override
    public boolean handle(Event event) {
        super.handle(event);

        if (event instanceof KitchenEvent.FoodConsumeEvent) {
            KitchenEvent.FoodConsumeEvent e = (KitchenEvent.FoodConsumeEvent) event;
            if (e.getProvider() == this) {
                reset();
                return true;
            }
        }

        return false;
    }

    @Override
    protected void setStage(Stage stage) {
        super.setStage(stage);
        if (stage != null) {
            stage.addActor(compositeFood);
        }
    }

    @Override
    public DragAndDrop.Source getDragSource() {
        return new DragAndDrop.Source(this) {
            @Override
            public DragAndDrop.Payload dragStart(InputEvent event, float x, float y, int pointer) {
                if (compositeFood.isEmpty()) {
                    return null;
                }
                if (compositeFoodState != CompositeFoodState.UNLOCKED) {
                    return null;
                }

                DragAndDrop.Payload payload = new DragAndDrop.Payload();

                TransformData position = new TransformData(compositeFood);
                payload.setObject(position);

                compositeFood.setZIndex(getStage().getActors().size + 1);
                payload.setDragActor(compositeFood);
                compositeFoodState = CompositeFoodState.TAKEN_OUT;

                return payload;
            }

            @Override
            public void dragStop(InputEvent event, float x, float y, int pointer, DragAndDrop.Payload payload, DragAndDrop.Target target) {
                super.dragStop(event, x, y, pointer, payload, target);

                if (target == null || target.getActor() == null) {
                    TransformData startPosition = (TransformData) payload.getObject();
                    startPosition.apply(compositeFood);
                    compositeFoodState = CompositeFoodState.UNLOCKED; // you could only drag it if it was UNLOCKED in the first place.
                    return;
                }
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
                if (state == State.ACTIVE) {
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
                if (state == State.ACTIVE) {
                    return; // this is already checked in .drag(), but still.
                }

                Actor dragActor = payload.getDragActor();

                if (dragActor instanceof Food) {
                    Food food = (Food) dragActor;
                    compositeFood.addIngredient(FoodCombination.PartialIngredient.from(food));

                    dispatchStageEvent(new KitchenEvent.FoodConsumeEvent(
                        food,
                        source.getActor(),
                        FoodCooker.this
                    ));
                }
            }
        };
    }
}
