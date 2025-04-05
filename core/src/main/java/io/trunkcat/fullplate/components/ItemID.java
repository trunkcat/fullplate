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

package io.trunkcat.fullplate.components;

// TODO: make this like item details, and add the game asset name
public enum ItemID {
    COMPOSITE_FOOD("composite-food", Type._INTERNAL),

    BURGER("burger", Type.COMBINATION),

    BURGER_BUN("burger-bun", Type.FOOD, 2),
    CHEESE("cheese", Type.FOOD, 1),
    LETTUCE("lettuce", Type.FOOD, 1),
    TOMATO("tomato", Type.FOOD, 1),

    OLIVE_OIL("olive-oil", Type.COOKABLE_FOOD, 1),
    BURGER_PATTY("burger-patty", Type.COOKABLE_FOOD, 2),

    BURGER_BUN_TRAY("burger-bun-tray", Type.ITEM_STORE),
    BURGER_PATTY_TRAY("burger-patty-tray", Type.ITEM_STORE),
    TOMATO_BOWL("tomato-bowl", Type.ITEM_STORE),

    PLATE("plate-2", Type.FOOD_HOLDER),

    FRYING_PAN("frying-pan", Type.FOOD_COOKER),
    CUTTING_BOARD("cutting-board", Type.FOOD_COOKER);

    public enum Type {
        _INTERNAL,
        COMBINATION,
        FOOD,
        COOKABLE_FOOD,
        ITEM_STORE,
        FOOD_HOLDER,
        FOOD_COOKER
    }

    private final String id;
    private final Type type;
    private final int cost;

    ItemID(String itemId, Type type, int cost) {
        this.id = itemId;
        this.type = type;
        this.cost = cost;
    }

    ItemID(String itemId, Type type) {
        this.id = itemId;
        this.type = type;
        this.cost = 0;
    }

    public String getId() {
        return id;
    }

    public Type getType() {
        return type;
    }

    public int getCost() {
        return cost;
    }
}
