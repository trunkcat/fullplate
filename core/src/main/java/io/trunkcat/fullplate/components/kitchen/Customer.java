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

import static io.trunkcat.fullplate.components.debug.DebugLabelStyles.label;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop;
import com.badlogic.gdx.utils.Array;

import io.trunkcat.fullplate.components.common.Entity;
import io.trunkcat.fullplate.utilities.AnimationController;
import io.trunkcat.fullplate.utilities.AnimationUtils;
import io.trunkcat.fullplate.utilities.Constants;

public class Customer extends Entity {
    private final Type type;
    private final Seat assignedSeat;
    private final Array<Recipe> ordersLeft;
    private final Array<Recipe> ordersServed;
    private final AnimationController animationController = new AnimationController();
    private Emotion emotion = Emotion.SURPRISED;
    private PositionState positionState;
    private OrderOfferState orderOfferState = null;
    private float waitingTime;
    private float initialWaitingTime;
    private float walkingSpeed;
    private int coins = 0;
    private int tip = 0;

    private final OrderBox orderBox;

    public Customer(Type type, Seat seat, Array<Recipe> orders) {
        this.type = type;
        this.assignedSeat = seat;
        this.ordersLeft = orders;
        this.ordersServed = new Array<>();
        this.positionState = PositionState.SPAWNED;

        float waitingTime = 0f;
        for (Recipe order : new Array.ArrayIterator<>(orders)) {
            waitingTime += order.getOrderProcessingTime();
        }
        this.waitingTime = waitingTime;
        this.initialWaitingTime = waitingTime;

        this.walkingSpeed = Constants.CUSTOMER_WALKING_SPEED;

        this.orderBox = new OrderBox(ordersLeft);

        // todo: animation composer
        animationController.addAnimation(
            Emotion.SURPRISED,
            AnimationUtils.createSingleFrameAnimation("items/debug-green.png")
        );
        animationController.addAnimation(
            Emotion.HAPPY,
            AnimationUtils.createSingleFrameAnimation("items/debug.png")
        );
        animationController.addAnimation(
            Emotion.NEUTRAL,
            AnimationUtils.createSingleFrameAnimation("items/debug-blue.png")
        );
        animationController.addAnimation(
            Emotion.SAD,
            AnimationUtils.createSingleFrameAnimation("items/debug.png")
        );
        animationController.addAnimation(
            Emotion.ANGRY,
            AnimationUtils.createSingleFrameAnimation("items/debug-red.png")
        );
        animationController.addAnimation(
            OrderOfferState.VERY_EXCITED,
            AnimationUtils.createSingleFrameAnimation("items/debug-green.png")
        );
        animationController.addAnimation(
            OrderOfferState.EXCITED,
            AnimationUtils.createSingleFrameAnimation("items/debug-green.png")
        );
        animationController.addAnimation(
            OrderOfferState.DISAPPOINTED,
            AnimationUtils.createSingleFrameAnimation("items/debug-red.png")
        );
        animationController.addAnimation(
            OrderOfferState.VERY_DISAPPOINTED,
            AnimationUtils.createSingleFrameAnimation("items/debug-red.png")
        );

        dropTarget = new DragAndDrop.Target(this) {
            @Override
            public boolean drag(DragAndDrop.Source source, DragAndDrop.Payload payload, float x, float y, int pointer) {
                if (payload == null) return false;
                if (positionState != PositionState.SEATED) return false;

                Actor dragActor = payload.getDragActor();
                if (dragActor == null || source == null) return false;

                final IngredientAssembly assembly = resolveIngredientAssembly(source.getActor(), dragActor);
                final Recipe matchingOrder = findMatchingOrder(assembly);
                dispatchStageEvent(new CustomerEvent.OrderOfferEvent(Customer.this, assembly, matchingOrder));
                return matchingOrder != null;
            }

            @Override
            public void reset(DragAndDrop.Source source, DragAndDrop.Payload payload) {
                super.reset(source, payload);
                if (payload == null) return;
                if (positionState != PositionState.SEATED) return;

                Actor dragActor = payload.getDragActor();
                if (dragActor == null || source == null) return;

                final IngredientAssembly assembly = resolveIngredientAssembly(source.getActor(), dragActor);
                final Recipe matchingOrder = findMatchingOrder(assembly);
                dispatchStageEvent(new CustomerEvent.OrderWithdrawEvent(Customer.this, assembly, matchingOrder));
            }

            @Override
            public void drop(DragAndDrop.Source source, DragAndDrop.Payload payload, float x, float y, int pointer) {
                final IngredientAssembly assembly = resolveIngredientAssembly(source.getActor(), payload.getDragActor());
                dispatchStageEvent(new CustomerEvent.OrderAcceptEvent(Customer.this, assembly));
                payload.getDragActor().remove();
            }
        };
    }

    public int getCoins() {
        return coins;
    }

    public int getTip() {
        return tip;
    }

    private void updateEmotion() {
        if (waitingTime <= 0) {
            return;
        }
        float timeLeft = Math.min(initialWaitingTime - waitingTime, initialWaitingTime); // just in case
        float timeProgress = Math.min(timeLeft / initialWaitingTime, 1);
        orderBox.setTimeProgress(timeProgress);
        int emotionOrdinal = (int) Math.floor(timeProgress * (Emotion.values().length - 1));
        emotion = Emotion.values()[emotionOrdinal];
    }

    private void leaveSeat() {
        dispatchStageEvent(new CustomerEvent.CustomerPositionChangeEvent(
            this,
            this.positionState,
            PositionState.LEAVING
        ));
        positionState = PositionState.LEAVING;
        orderBox.remove();
    }

    @Override
    public void act(float delta) {
        super.act(delta);

        if (ordersLeft.isEmpty() && positionState.ordinal() < PositionState.LEAVING.ordinal()) {
            leaveSeat();
        }

        Vector2 seatPosition = assignedSeat.getPosition();

        if (positionState == PositionState.SPAWNED) {
            setPosition(-getWidth(), seatPosition.y);
            positionState = PositionState.ENTERING;
            dispatchStageEvent(new CustomerEvent.CustomerPositionChangeEvent(
                this,
                PositionState.SPAWNED,
                PositionState.ENTERING
            ));
        } else if (positionState == PositionState.ENTERING) {
            float x = getX();
            if (x < seatPosition.x) {
                setX(x + walkingSpeed * delta);
            } else {
                setPosition(seatPosition.x, seatPosition.y);
                positionState = PositionState.SEATED;
                dispatchStageEvent(new CustomerEvent.CustomerPositionChangeEvent(
                    this,
                    PositionState.ENTERING,
                    PositionState.SEATED
                ));
                getStage().addActor(orderBox);
                orderBox.setPosition(getX() + getWidth() + 20, getY());
            }
        } else if (positionState == PositionState.SEATED) {
            waitingTime -= delta;
            if (waitingTime <= 0) leaveSeat();
        } else if (positionState == PositionState.LEAVING) {
            float x = getX();
            float stageWidth = getStage().getWidth();
            if (x < stageWidth + getWidth()) {
                setX(x + walkingSpeed * delta);
            } else {
                assignedSeat.setOccupied(false);
                positionState = PositionState.LEFT;
                dispatchStageEvent(new CustomerEvent.CustomerPositionChangeEvent(
                    this,
                    PositionState.LEAVING,
                    PositionState.LEFT
                ));
            }
        } else if (positionState == PositionState.LEFT) {
            this.remove();
        } else {
            throw new IllegalStateException("Unknown customer position state: " + positionState);
        }

        updateEmotion();
        if (orderOfferState != null) {
            animationController.setAnimation(orderOfferState, true);
        } else {
            animationController.setAnimation(emotion, true);
        }
        animationController.update(delta);

        TextureRegion currentFrame = animationController.getCurrentFrame();
        if (currentFrame != null) {
            float actualWidth = currentFrame.getRegionWidth() * getScaleX();
            float actualHeight = currentFrame.getRegionHeight() * getScaleY();
            setSize(actualWidth, actualHeight);
        }
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);

        TextureRegion currentFrame = animationController.getCurrentFrame();
        if (currentFrame != null) {
            batch.draw(
                currentFrame, getX(), getY(),
                getOriginX(), getOriginY(), getWidth(), getHeight(),
                getScaleX(), getScaleY(), getRotation()
            );
        }
    }

    @Override
    protected void setStage(Stage stage) {
        super.setStage(stage);

        if (stage != null) {
            dispatchStageEvent(new CustomerEvent.CustomerPositionChangeEvent(
                this,
                null,
                PositionState.SPAWNED
            ));
        }
    }

    private Recipe findMatchingOrder(IngredientAssembly assembly) {
        for (Recipe orderRecipe : new Array.ArrayIterator<>(ordersLeft)) {
            if (orderRecipe.isFullySatisfiedBy(assembly)) return orderRecipe;
        }
        return null;
    }


    private void fulfillOrder(IngredientAssembly assembly) {
        for (int i = 0; i < ordersLeft.size; i++) {
            Recipe order = ordersLeft.get(i);
            if (order.isFullySatisfiedBy(assembly)) {
                // todo: there are two ways to handle this:
                // 1. keep the INITIAL wait time the same. that way, the patience rate doesn't
                // change. cap the emotion time calculation so that it always stick to INITIAL wait time.
                // 2. add the bonus to both INITIAL and waitingTime. makes more sense that
                // after an order delivery the customer could be a little bit more patient.

                float timeBonus = order.getOrderProcessingTime() * Constants.TIME_BONUS_FACTOR;
                waitingTime += timeBonus;
                initialWaitingTime += timeBonus;

                coins += order.getCost();
                tip += emotion.getTip();

                dispatchStageEvent(new CustomerEvent.OrderFulfillEvent(this, order));
                ordersServed.add(ordersLeft.removeIndex(i));
                return;
            }
        }
    }

    private IngredientAssembly resolveIngredientAssembly(Actor source, Actor dragActor) {
        if (dragActor instanceof Item) {
            return ((Item) dragActor).getIngredientAssembly();
        } else if (source instanceof IngredientAssembler) {
            return ((IngredientAssembler) source).getIngredientAssembly();
        }
        return new IngredientAssembly();
    }

    @Override
    public boolean handle(Event event) {
        super.handle(event);

        if (event instanceof CustomerEvent) {
            if (((CustomerEvent) event).getCustomer() == this) {
                if (event instanceof CustomerEvent.OrderOfferEvent) {
                    CustomerEvent.OrderOfferEvent e = (CustomerEvent.OrderOfferEvent) event;
                    if (e.getMatchingOrder() != null) {
                        if (ordersLeft.size == 1) { // The very last order, very excited.
                            orderOfferState = OrderOfferState.VERY_EXCITED;
                        } else {
                            orderOfferState = OrderOfferState.EXCITED;
                        }
                    } else {
                        if (ordersLeft.size == 1) { // Not even the last one
                            orderOfferState = OrderOfferState.VERY_DISAPPOINTED;
                        } else {
                            orderOfferState = OrderOfferState.DISAPPOINTED;
                        }
                    }
                    return true;
                } else if (event instanceof CustomerEvent.OrderWithdrawEvent) {
                    orderOfferState = null;
                    return true;
                } else if (event instanceof CustomerEvent.OrderAcceptEvent) {
                    CustomerEvent.OrderAcceptEvent e = (CustomerEvent.OrderAcceptEvent) event;
                    fulfillOrder(e.getAssembly());
                    return true;
                }
            }
        }

        return false;
    }

    @Override
    public Table getDebugTable() {
        Table table = super.getDebugTable();

        table.add(label("type: " + type)).row();
        table.add(label("seat: " + assignedSeat.getPosition())).row();

        table.add(label("position state: " + positionState)).row();
        table.add(label("order offer state: " + orderOfferState)).row();

        table.add(label("coins: " + coins)).row();
        table.add(label("tip: " + tip)).row();

        table.add(label("waiting time: " + waitingTime)).row();
        table.add(label("initial waiting time: " + initialWaitingTime)).row();

        float cappedWaitingTime = Math.max(0, waitingTime);
        float timeLeft = Math.min(initialWaitingTime - cappedWaitingTime, initialWaitingTime); // just in case
        float timeProgress = timeLeft / initialWaitingTime;

        Color timeProgressColor = new Color(timeProgress, 1 - timeProgress, 0, 1);
        float doubled = timeProgress * 2;
        if (timeProgress < 0.5f) {
            timeProgressColor.set(doubled, 1, 0, 1);
        } else {
            timeProgressColor.set(1, 2 - doubled, 0, 1);
        }

        table.add(label("patience: " + timeProgress * 100)).row();
        StringBuilder progressMeter = new StringBuilder();
        int pieces = 30;
        for (int i = 1; i <= pieces - (timeProgress * pieces); i++) {
            progressMeter.append("-");
        }
        table.add(label(progressMeter.toString(), timeProgressColor)).row();

        table.add(label("emotion: " + emotion)).row();

        table.add(label("orders left: " + ordersLeft.size)).row();
        for (Recipe order : new Array.ArrayIterator<>(ordersLeft)) {
            table.add(label("- " + order.getResultItem()).indent(6)).row();
        }
        table.add(label("orders served: " + ordersServed.size)).row();
        for (Recipe order : new Array.ArrayIterator<>(ordersServed)) {
            table.add(label("- " + order.getResultItem()).indent(6)).row();
        }
        return table;
    }

    public enum Type {
        NORMAL,
        BEGGAR,
        BARTERER,
        CRITIC,
        INSPECTOR
    }

    public enum Emotion {
        SURPRISED(4),
        HAPPY(3),
        NEUTRAL(2),
        SAD(1),
        ANGRY(0);

        private final int tip;

        Emotion(int tip) {
            this.tip = tip;
        }

        public int getTip() {
            return tip;
        }
    }

    // TODO: could implement the customer changing seat randomly (to mess with player)
    public enum PositionState {
        SPAWNED,
        ENTERING,
        SEATED,
        LEAVING,
        LEFT
    }

    public enum OrderOfferState {
        VERY_EXCITED, // the very last order
        EXCITED,
        DISAPPOINTED,
        VERY_DISAPPOINTED, // the first order of many
    }
}
