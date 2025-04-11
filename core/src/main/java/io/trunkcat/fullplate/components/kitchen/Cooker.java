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

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop;
import com.badlogic.gdx.utils.Array;

import io.trunkcat.fullplate.utilities.AnimationUtils;
import io.trunkcat.fullplate.utilities.Constants;
import io.trunkcat.fullplate.utilities.Rect;

public class Cooker extends IngredientAssembler {
    public enum State {
        IDLE,
        ACTIVE
    }

    protected State state = State.IDLE;
    protected float timeLeft = 0f;

    public Cooker(ID id, int level, int maxLevel,
                  RecipeCollection recipeCollection) {
        super(id, Type.COOKER, level, maxLevel, recipeCollection);

        animationController.addAnimation(
            State.IDLE,
            AnimationUtils.createSingleFrameAnimation(getAnimationPath(State.IDLE))
        );
        animationController.addAnimation(
            State.ACTIVE,
            AnimationUtils.createAnimation(getAnimationPath(State.ACTIVE), 1, 3, 0.25f)
        );
        animationController.setAnimation(State.IDLE, true);

        TextureRegion currentFrame = animationController.getCurrentFrame();
        if (currentFrame != null) {
            float actualWidth = currentFrame.getRegionWidth() * getScaleX();
            float actualHeight = currentFrame.getRegionHeight() * getScaleY();
            setSize(actualWidth, actualHeight);
        }

        dragSource = new DragAndDrop.Source(this) {
            @Override
            public DragAndDrop.Payload dragStart(InputEvent event, float x, float y, int pointer) {
                if (ingredientAssembly.isEmpty()) return null;
                if (positionState != PositionState.UNLOCKED) return null;
                if (!ingredientAssembly.hasLockedRecipe()) return null;

                Recipe recipe = ingredientAssembly.getLockedRecipe();

                DragAndDrop.Payload payload = new DragAndDrop.Payload();
                Item item = new Item(recipe.getResultItem(), Type.FOOD, ingredientAssembly);
                item.setZIndex(Constants.DRAG_ACTOR_Z_INDEX);
                payload.setDragActor(item);
                positionState = PositionState.TAKEN_OUT;
                return payload;
            }

            @Override
            public void dragStop(InputEvent event, float x, float y, int pointer, DragAndDrop.Payload payload, DragAndDrop.Target target) {
                super.dragStop(event, x, y, pointer, payload, target);

                if (target == null || target.getActor() == null) {
                    payload.getDragActor().remove();
                    positionState = PositionState.UNLOCKED; // you could only drag it if it was UNLOCKED in the first place.
                }
            }
        };

        dropTarget = new DragAndDrop.Target(this) {
            @Override
            public boolean drag(DragAndDrop.Source source, DragAndDrop.Payload payload, float x, float y, int pointer) {
                if (payload == null) return false;

                // question: should i let it start cooking? and add stuff later on, which
                //  dynamically ruins the ingredients as time progresses?
                if (state == State.ACTIVE) return false;

                Actor dragActor = payload.getDragActor();

                if (dragActor instanceof Item) {
                    Item item = (Item) dragActor;
                    return recipeCollection.isPartialIngredientsAddable(ingredientAssembly, item.getIngredientAssembly());
                }

                return false;
            }

            @Override
            public void drop(DragAndDrop.Source source, DragAndDrop.Payload payload, float x, float y, int pointer) {
                Actor dragActor = payload.getDragActor();

                if (dragActor instanceof Item) {
                    Item item = (Item) dragActor;
                    ingredientAssembly.addIngredients(item.getIngredientAssembly());
                    dispatchStageEvent(new KitchenEvent.ItemConsumeEvent(item, source.getActor(), Cooker.this));
                }

                dragActor.remove();
            }
        };
    }

    private float getCookingTime(Recipe recipe) {
        float recipeCookingTime = recipe.getCookingTime();
        // todo: the constant cook time factor could be cooker dependent.
        float leastPossibleTime = recipeCookingTime * Constants.COOK_TIME_MIN_FACTOR;
        float cookerDependentTime = recipeCookingTime / (1 + (level * Constants.FOOD_COOKER_LEVEL_SPEED_MODIFIER));
        return Math.min(recipeCookingTime, Math.max(cookerDependentTime, leastPossibleTime));
    }

    private float getOvercookingTime(Recipe recipe) {
        return recipe.getOvercookingTime();
    }

    @Override
    public void reset() {
        super.reset();
        this.state = State.IDLE;
        this.timeLeft = 0f;
    }

    public Recipe getSatisfyingRecipe() {
        Array<Recipe> recipes = recipeCollection.findSatisfiableRecipes(ingredientAssembly);
        for (Recipe recipe : new Array.ArrayIterator<>(recipes)) {
            if (recipe.isFullySatisfiedBy(ingredientAssembly)) {
                return recipe;
            }
        }
        return null;
    }

    private void cook() {
        if (ingredientAssembly.isEmpty()) return;

        float delta = Gdx.graphics.getDeltaTime();

        if (state == State.IDLE) {
            Recipe recipe = getSatisfyingRecipe();
            if (recipe == null) return;

            state = State.ACTIVE;
            positionState = PositionState.LOCKED;
            ingredientAssembly.setState(FoodState.UNDER_COOKED);
            ingredientAssembly.lockRecipe(recipe);
            timeLeft = getCookingTime(recipe);
        } else if (state == State.ACTIVE) {
            if (positionState == PositionState.TAKEN_OUT) return;
            Recipe lockedRecipe = ingredientAssembly.getLockedRecipe();

            if (ingredientAssembly.getState() == FoodState.UNDER_COOKED) {
                timeLeft -= delta;
                if (timeLeft <= 0) {
                    positionState = PositionState.UNLOCKED;

                    // todo: there should be actually two sets of states:
                    //  1. cooking state, that is bound to the cooker: which shows the process
                    //     uncooked -> burnt
                    //  2. food state: what state the food is in. for example, for cheese:
                    //     raw (block), cut / slice, grated.
                    //  Need to figure out how to merge these two into the final visual representation.
                    //  After figuring this out, the assembly state would be set to the food state,
                    //  and the cooker-cooking state would be set to the cooking state.
                    //  This likely will require extending assembly to cooking assembly, etc.

                    ingredientAssembly.setState(FoodState.COOKED);

                    if (lockedRecipe.isOvercookingPossible()) {
                        timeLeft = getOvercookingTime(lockedRecipe);
                    }
                }
            } else if (ingredientAssembly.getState() == FoodState.COOKED) {
                if (lockedRecipe.isOvercookingPossible()) {
                    timeLeft -= delta;
                    if (timeLeft <= 0) {
                        ingredientAssembly.setState(FoodState.BURNT);
                    }
                }
            }
        }
    }

    @Override
    public void act(float delta) {
        super.act(delta);

        cook();

        animationController.setAnimation(state, true);
    }

    @Override
    public boolean handle(Event event) {
        super.handle(event);
        if (event instanceof KitchenEvent.ItemConsumeEvent) {
            KitchenEvent.ItemConsumeEvent e = (KitchenEvent.ItemConsumeEvent) event;
            if (e.getProvider() == this) {
                reset();
                return true;
            }
        }
        return false;
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

        // todo: for a more better 3d look, backdrop could be drawn first, then the assembly,
        //  finally the texture that can be drawn on it. this gives fake, but real feeling 3d feel.
        // drawTexture(batch, currentFrame);
    }
}
