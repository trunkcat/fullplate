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
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop;
import com.badlogic.gdx.utils.Array;

import io.trunkcat.fullplate.components.common.StageActor;
import io.trunkcat.fullplate.utilities.AssetManager;
import io.trunkcat.fullplate.utilities.Constants;

public class Customer extends StageActor { // extend item / entity
    public enum Type {
        NORMAL,
        BEGGAR,
        BARTERER,
        CRITIC,
        INSPECTOR
    }

    public enum Emotion {
        SURPRISED("surprised", 4),
        HAPPY("happy", 3),
        NEUTRAL("neutral", 2),
        SAD("sad", 1),
        ANGRY("angry", 0);

        private final String value;
        private final int tip;

        Emotion(String value, int tip) {
            this.value = value;
            this.tip = tip;
        }

        public int getTip() {
            return tip;
        }

        public String getValue() {
            return value;
        }
    }

    // TODO: could implement the customer changing seat randomly (to mess with player)
    public enum State {
        SPAWNED,
        ENTERING,
        SEATED,
        LEAVING,
        LEFT
    }

    public enum DragOverState {
        VERY_EXCITED, // the very last order
        EXCITED,
        DISAPPOINTED,
        VERY_DISAPPOINTED, // the first order of many
    }

    private final Type type;
    private final Seat seat;
    private final Array<Order> orders; // TODO: preserve orders, and calculate completed and failed ones when customer leaves
    private Emotion emotion = Emotion.SURPRISED;
    private State state;
    private float waitingTime;
    private float INITIAL_WAITING_TIME;
    private Texture currentTexture = AssetManager.loadTexture("items/debug.png");
    private final float WALKING_SPEED;
    private DragOverState dragOverState = null;
    // TODO: make coins, tips and (future) rating as "bill" instance
    private int coins = 0;
    private int tip = 0;

    public Customer(Type type, Seat seat, Array<Order> orders) {
        this.type = type;
        this.seat = seat;
        this.orders = orders;
        this.state = State.SPAWNED;

        float waitingTime = 0f;
        for (Order order : new Array.ArrayIterator<>(orders)) {
            waitingTime += order.getProcessingTime();
        }
        this.waitingTime = waitingTime;
        this.INITIAL_WAITING_TIME = waitingTime;

        this.WALKING_SPEED = 250f;  // TODO: could modify

        Vector2 position = seat.getPosition();
        setPosition(0, position.y);
    }

    public int getCoins() {
        return coins;
    }

    public int getTip() {
        return tip;
    }

    private void updateCurrentEmotion() {
        if (waitingTime <= 0) {
            return;
        }
        float timeLeft = Math.min(INITIAL_WAITING_TIME - waitingTime, INITIAL_WAITING_TIME); // just in case
        float timeProgress = timeLeft / INITIAL_WAITING_TIME;
        int emotionOrdinal = (int) Math.floor(timeProgress * (Emotion.values().length - 1));
        emotion = Emotion.values()[emotionOrdinal];
    }

    private Texture updateTexture() {
        if (dragOverState == DragOverState.EXCITED || dragOverState == DragOverState.VERY_EXCITED) {
            return AssetManager.loadTexture("items/debug-green.png");
        } else if (dragOverState == DragOverState.DISAPPOINTED || dragOverState == DragOverState.VERY_DISAPPOINTED) {
            return AssetManager.loadTexture("items/debug-red.png");
        } else {
            return AssetManager.loadTexture("items/debug.png"); // TODO: include emotions
        }
    }

    public void leave() {
        // TODO: send events when leaving and left and manage them
        dispatchStageEvent(new CustomerEvent.CustomerLeftEvent(this));
        state = State.LEAVING;
    }

    @Override
    public void act(float delta) {
        super.act(delta);

        Vector2 position = seat.getPosition();

        if (orders.isEmpty() && state.ordinal() < State.LEAVING.ordinal()) {
            leave();
        }

        // TODO: animations and translations

        if (state == State.SPAWNED) {
            setPosition(-getWidth(), position.y);
            state = State.ENTERING;
        } else if (state == State.ENTERING) {
            float currentX = getX();
            // TODO: we need serious hit boxes
            if (currentX < position.x) { // TODO: add exception to position param, if its less than screen boundaries.
                setX(currentX + WALKING_SPEED * delta);
            } else {
                state = State.SEATED;
            }
        } else if (state == State.SEATED) {
            // spawn animation must have set the position already, but for confirmation:
            setPosition(position.x, position.y);

            waitingTime -= delta;

            if (waitingTime <= 0) {
                leave();
            }
        } else if (state == State.LEAVING) {
            float currentX = getX();
            float worldWidth = getStage().getViewport().getWorldWidth();
            if (currentX < worldWidth + getWidth()) {
                setX(currentX + WALKING_SPEED * delta);
            } else {
                seat.empty();
                state = State.LEFT;
            }
        } else if (state == State.LEFT) {
            remove();
            // TODO: animate customer out.
            //  when out of screen, remove from stage.
        } else {
            throw new IllegalStateException("Unknown customer state: " + state);
        }

        if (state != State.LEFT) {
            updateCurrentEmotion();
            currentTexture = updateTexture();
            if (currentTexture != null) {
                setSize(
                    currentTexture.getWidth() * getScaleX(),
                    currentTexture.getHeight() * getScaleY()
                );
            }
        }
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        if (currentTexture != null) {
            batch.draw(
                currentTexture, getX(), getY(), getOriginX(), getOriginY(),
                getWidth(), getHeight(), getScaleX(), getScaleY(), getRotation(),
                0, 0, currentTexture.getWidth(), currentTexture.getHeight(), false, false
            );
        }

        super.draw(batch, parentAlpha);
    }

    @Override
    public boolean handle(Event event) {
        super.handle(event);

        if (event instanceof CustomerEvent.CombinationOfferEvent) {
            CustomerEvent.CombinationOfferEvent e = (CustomerEvent.CombinationOfferEvent) event;
            if (e.getCustomer() == this) {
                if (e.hasMatchingOrder()) {
                    if (orders.size == 1) { // The very last order, very excited.
                        dragOverState = DragOverState.VERY_EXCITED;
                    } else {
                        dragOverState = DragOverState.EXCITED;
                    }
                } else {
                    if (orders.size > 1) {
                        //
                        dragOverState = DragOverState.VERY_DISAPPOINTED;
                    } else {
                        dragOverState = DragOverState.DISAPPOINTED;
                    }
                }
                return true;
            }
        } else if (event instanceof CustomerEvent.CombinationWithdrawEvent) {
            CustomerEvent.CombinationWithdrawEvent e = (CustomerEvent.CombinationWithdrawEvent) event;
            if (e.getCustomer() == this) {
                dragOverState = null;
                return true;
            }
        } else if (event instanceof CustomerEvent.CombinationAcceptEvent) {
            CustomerEvent.CombinationAcceptEvent e = (CustomerEvent.CombinationAcceptEvent) event;
            if (e.getCustomer() == this) {
                fulfillOrder(e.getIngredients());
                return true;
            }
        }

        return false;
    }

    private void fulfillOrder(Array<FoodCombination.PartialIngredient> ingredients) {

        for (int i = 0; i < orders.size; i++) {
            Order order = orders.get(i);
            if (order.canBeSatisfied(ingredients)) {
                // TODO: there are two ways to handle this:
                // 1. keep the INITIAL wait time the same. that way, the patience rate doesn't
                // change. cap the emotion time calculation so that it always stick to INITIAL wait time.
                // 2. add the bonus to both INITIAL and waitingTime. makes more sense that
                // after an order delivery the customer could be a little bit more patient.
                float timeBonus = order.getProcessingTime() * Constants.TIME_BONUS_FACTOR;
                waitingTime += timeBonus;
                INITIAL_WAITING_TIME += timeBonus;

                coins += order.getPrice();
                tip += emotion.getTip();
                dispatchStageEvent(new CustomerEvent.OrderFulfillEvent(this, order));
                orders.removeIndex(i);
                return;
            }
        }
    }

    private boolean hasOrderCombination(FoodCombination combination) {
        Array<FoodCombination.PartialIngredient> ingredients = new Array<>(
            combination.getIngredients()
                .values()
                .stream()
                .map(FoodCombination.PartialIngredient::from)
                .toArray(FoodCombination.PartialIngredient[]::new));
        return hasOrderCombination(ingredients);
    }

    private boolean hasOrderCombination(Array<FoodCombination.PartialIngredient> ingredients) {
        for (Order order : new Array.ArrayIterator<>(orders)) {
            if (order.canBeSatisfied(ingredients)) {
                return true;
            }
        }
        return false;
    }


    private Array<FoodCombination.PartialIngredient> resolveIngredientsDrop(Actor source, Actor dragActor) {
        if (dragActor instanceof FoodHolder) {
            return ((FoodHolder) dragActor).compositeFood.getIngredients();
        } else if (source instanceof ItemStore) {
            // TODO: include item stores.
            //  item store should provide a combination (of just one item (or many idk)) when dragged
        }
        return new Array<>();
    }

    @Override
    public DragAndDrop.Target getDropTarget() {
        return new DragAndDrop.Target(this) {
            @Override
            public boolean drag(DragAndDrop.Source source, DragAndDrop.Payload payload, float x, float y, int pointer) {
                if (payload == null) {
                    return false;
                }

                if (state != State.SEATED) {
                    return false;
                }

                Actor dragActor = payload.getDragActor();
                if (dragActor == null || source == null) {
                    return false;
                }

                Array<FoodCombination.PartialIngredient> ingredients = resolveIngredientsDrop(source.getActor(), dragActor);
                boolean hasMatchingOrder = hasOrderCombination(ingredients);
                dispatchStageEvent(
                    new CustomerEvent.CombinationOfferEvent(
                        Customer.this,
                        ingredients,
                        hasMatchingOrder
                    )
                );
                return hasMatchingOrder;
            }

            @Override
            public void reset(DragAndDrop.Source source, DragAndDrop.Payload payload) {
                super.reset(source, payload);
                if (payload == null) {
                    return;
                }
                Actor dragActor = payload.getDragActor();
                if (dragActor == null || source == null) {
                    return;
                }

                Array<FoodCombination.PartialIngredient> ingredients = resolveIngredientsDrop(source.getActor(), dragActor);
                boolean hasMatchingOrder = hasOrderCombination(ingredients);
                dispatchStageEvent(
                    new CustomerEvent.CombinationWithdrawEvent(
                        Customer.this,
                        ingredients,
                        hasMatchingOrder
                    )
                );
            }

            @Override
            public void drop(DragAndDrop.Source source, DragAndDrop.Payload payload, float x, float y, int pointer) {
                if (payload == null) {
                    return;
                }
                // implied that ingredients matches an order.

                Actor dragActor = payload.getDragActor();
                Array<FoodCombination.PartialIngredient> ingredients = resolveIngredientsDrop(source.getActor(), dragActor);
                dispatchStageEvent(
                    new CustomerEvent.CombinationAcceptEvent(
                        Customer.this,
                        ingredients
                    )
                );
                dragActor.remove();
            }
        };
    }
}
