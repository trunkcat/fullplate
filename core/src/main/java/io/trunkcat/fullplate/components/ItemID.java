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

    FRYING_PAN("frying-pan", Type.FOOD_COOKER),
    CUTTING_BOARD("cutting-board", Type.FOOD_COOKER),

    BURGER("burger", Type.COMBINATION),

    BURGER_BUN("burger-bun", Type.FOOD),
    BURGER_BUN_TRAY("burger-bun-tray", Type.ITEM_STORE),

    OLIVE_OIL("olive-oil", Type.COOKABLE_FOOD),
    BURGER_PATTY("burger-patty", Type.COOKABLE_FOOD),
    BURGER_PATTY_TRAY("burger-patty-tray", Type.ITEM_STORE),

    CHEESE("cheese", Type.FOOD),
    LETTUCE("lettuce", Type.FOOD),
    TOMATO("tomato", Type.FOOD),
    TOMATO_BOWL("tomato-bowl", Type.ITEM_STORE),

    PLATE("plate-2", Type.FOOD_HOLDER);

    public enum Type {
        _INTERNAL,
        COMBINATION,
        FOOD,
        COOKABLE_FOOD,
        ITEM_STORE,
        FOOD_HOLDER,
        FOOD_COOKER
    }

    public final String id;
    public final Type type;

    ItemID(String itemId, Type type) {
        this.id = itemId;
        this.type = type;
    }

}
