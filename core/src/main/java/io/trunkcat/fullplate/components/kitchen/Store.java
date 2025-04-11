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

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop;

import io.trunkcat.fullplate.utilities.AnimationUtils;
import io.trunkcat.fullplate.utilities.Constants;

public abstract class Store extends UpgradableKitchenEntity {
    protected int stock;
    protected StockState stockState;
    protected final Recipe recipe;

    public Store(ID id, int level, int maxLevel, int initialStock, Recipe recipe) {
        super(id, Type.STORE, level, maxLevel);
        this.stock = initialStock;
        this.stockState = StockState.fromStockCount(initialStock);
        this.recipe = recipe;

        // todo: add rest of the animations
        animationController.addAnimation(
            StockState.LOT,
            AnimationUtils.createSingleFrameAnimation(getAnimationPath(StockState.LOT))
        );
        animationController.setAnimation(stockState, true);

        TextureRegion currentFrame = animationController.getCurrentFrame();
        if (currentFrame != null) {
            float actualWidth = currentFrame.getRegionWidth() * getScaleX();
            float actualHeight = currentFrame.getRegionHeight() * getScaleY();
            setSize(actualWidth, actualHeight);
        }

        dragSource = new DragAndDrop.Source(this) {
            @Override
            public DragAndDrop.Payload dragStart(InputEvent event, float x, float y, int pointer) {
                if (!hasStock()) return null;

                DragAndDrop.Payload payload = new DragAndDrop.Payload();
                Item producedItem = produce();
                producedItem.setZIndex(Constants.DRAG_ACTOR_Z_INDEX);
                payload.setDragActor(producedItem);
                getStage().addActor(producedItem);

                stock--;

                return payload;
            }

            @Override
            public void dragStop(InputEvent event, float x, float y, int pointer, DragAndDrop.Payload payload, DragAndDrop.Target target) {
                super.dragStop(event, x, y, pointer, payload, target);

                if (payload == null || payload.getDragActor() == null) return;

                if (target == null) {
                    stock++;
                    payload.getDragActor().remove();
                }
            }
        };
    }

    public boolean hasStock() {
        return stock > 0;
    }

    abstract public Item produce();

    @Override
    public void act(float delta) {
        super.act(delta);

        if (stock < 0) stock = 0;
        stockState = StockState.fromStockCount(stock);
        animationController.setAnimation(stockState, true);
    }

    public enum StockState {
        EMPTY(0),
        ONE(1),
        TWO(2),
        FEW(5),
        LOT(Integer.MAX_VALUE);

        final int stockCount;

        StockState(int stockCount) {
            this.stockCount = stockCount;
        }

        static StockState fromStockCount(int stockCount) {
            for (StockState state : values()) {
                if (stockCount <= state.stockCount) {
                    return state;
                }
            }
            return LOT;
        }
    }
}
