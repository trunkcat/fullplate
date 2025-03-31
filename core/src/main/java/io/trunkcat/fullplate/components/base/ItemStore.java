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
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop;

import io.trunkcat.fullplate.components.ItemID;
import io.trunkcat.fullplate.utilities.AssetManager;

public abstract class ItemStore extends Item {
    private final ItemID stockItemId;
    private int stock;
    private State currentState;

    public enum State {
        EMPTY("empty", 0),
        ONE("one", 1),
        TWO("two", 2),
        FEW("few", 5),
        LOT("lot", Integer.MAX_VALUE);

        public final String value;
        public final int count;

        State(String value, int count) {
            this.value = value;
            this.count = count;
        }

        static State fromStock(int stock) {
            for (State state : values()) {
                if (stock <= state.count) {
                    return state;
                }
            }
            return LOT;
        }
    }

    public ItemStore(ItemID itemId, int level,
                     ItemID stockItemId, int initialStock) {
        super(itemId, level);
        this.stockItemId = stockItemId;
        this.stock = initialStock;
        this.currentState = State.fromStock(initialStock);
        // TODO: implement max stock based on levels
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    public ItemID getStockItemId() {
        return stockItemId;
    }

    public boolean hasStock() {
        return stock > 0;
    }

    protected abstract Item produceItem();

    @Override
    public void act(float delta) {
        super.act(delta);

        if (stock < 0) {
            stock = 0;
        }

        // Compute the current state from the stock left.
        setCurrentState(State.fromStock(stock));
    }

    public State getCurrentState() {
        return currentState;
    }

    private void setCurrentState(State state) {
        this.currentState = state;
    }

    @Override
    public Texture getTexture() {
        return loadStateTexture(itemId, currentState);
    }

    public static Texture loadStateTexture(ItemID itemId, State state) {
        // TODO: consider level
        return AssetManager.loadTexture("items/" + itemId.id + "_" + state.value + ".png");
    }

    @Override
    public DragAndDrop.Source getDragSource() {
        return new DragAndDrop.Source(this) {
            @Override
            public DragAndDrop.Payload dragStart(InputEvent event, float x, float y, int pointer) {
                if (!hasStock()) {
                    return null;
                }
                DragAndDrop.Payload payload = new DragAndDrop.Payload();
                Item producedItem = produceItem();
                stock -= 1;
                payload.setDragActor(producedItem);
                getStage().addActor(producedItem);
                return payload;
            }

            @Override
            public void dragStop(InputEvent event, float x, float y, int pointer, DragAndDrop.Payload payload, DragAndDrop.Target target) {
                super.dragStop(event, x, y, pointer, payload, target);
                if (payload.getDragActor() == null) {
                    return;
                }

                if (target == null) {
                    // TODO: make a smooth interpolation, translating the item from the position back to the item store.
                    stock += 1;
                    payload.getDragActor().remove();
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
                Actor dragActor = payload.getDragActor();
                if (!(dragActor instanceof Item)) {
                    return false;
                }
                return ((Item) dragActor).getItemId().equals(stockItemId);
            }

            @Override
            public void drop(DragAndDrop.Source source, DragAndDrop.Payload payload, float x, float y, int pointer) {
                stock += 1;
                payload.getDragActor().remove();
            }
        };
    }
}
