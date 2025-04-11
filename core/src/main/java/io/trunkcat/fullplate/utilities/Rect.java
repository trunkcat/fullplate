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

package io.trunkcat.fullplate.utilities;

public class Rect {
    public float x;
    public float y;
    public float right;
    public float left;
    public float top;
    public float bottom;

    public Rect(float x, float y, float right, float left, float top, float bottom) {
        this.x = x;
        this.y = y;
        this.right = right;
        this.left = left;
        this.top = top;
        this.bottom = bottom;
    }

    public Rect(float x, float y, float width, float height) {
        this(x, y, x + width, x, y + height, y);
    }

    public float getWidth() {
        return Math.abs(right - left);
    }

    public float getHeight() {
        return Math.abs(top - bottom);
    }

    @Override
    public String toString() {
        return "Rect{" +
            "x=" + x +
            ", y=" + y +
            ", right=" + right +
            ", left=" + left +
            ", top=" + top +
            ", bottom=" + bottom +
            ", width=" + getWidth() +
            ", height=" + getHeight() +
            '}';
    }

    public Rect union(Rect rect) {
        float left = Math.min(this.left, rect.left);
        float bottom = Math.min(this.bottom, rect.bottom);
        float right = Math.max(this.right, rect.right);
        float top = Math.max(this.top, rect.top);
        return new Rect(left, bottom, right, left, top, bottom);
    }

    public void merge(Rect rect) {
        this.left = Math.min(this.left, rect.left);
        this.bottom = Math.min(this.bottom, rect.bottom);
        this.right = Math.max(this.right, rect.right);
        this.top = Math.max(this.top, rect.top);
        this.x = left;
        this.y = bottom;
    }
}
