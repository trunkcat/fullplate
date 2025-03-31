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

// TODO: make this like item details, and have item type as a property as well: food holder, food cooker etc
public enum ItemID {
    SIMPLE_BURGER("simple-burger"),
    CHEESE_BURGER("cheese-burger"),
    DOUBLE_CHEESE_BURGER("double-cheese-burger"),
    VEGGIE_BURGER("veggie-burger"),

    BURGER_BUN("burger-bun"),
    BURGER_BUN_TRAY("burger-bun-tray"),
    BURGER_PATTY("burger-patty"),
    BURGER_PATTY_TRAY("burger-patty-tray"),

    CHEESE("cheese"),
    LETTUCE("lettuce"),
    TOMATO("tomato"),

    PLATE("plate");

    public final String id;

    ItemID(String itemId) {
        this.id = itemId;
    }
}
