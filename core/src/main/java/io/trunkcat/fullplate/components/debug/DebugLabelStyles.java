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

package io.trunkcat.fullplate.components.debug;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.ui.Label;

import java.util.HashMap;
import java.util.Map;


public class DebugLabelStyles {
    private static final Map<String, LabelStyle> labelStyles = new HashMap<>();
    private static final Color color = Color.WHITE;
    private static final float scale = 1f;
    private static final FileHandle fontFile = Gdx.files.internal("fonts/silver-debug.fnt");
    private static final FileHandle fontFileBold = Gdx.files.internal("fonts/silver-bold-debug.fnt");

    public static class LabelStyle extends Label.LabelStyle {
        public LabelStyle bold() {
            float scaleX = this.font.getData().scaleX,
                scaleY = this.font.getData().scaleY;
            this.font = new BitmapFont(fontFileBold);
            this.font.getData().setScale(scaleX, scaleY);
            return this;
        }
    }

    public static LabelStyle get(Color color, float scale) {
        return labelStyles.computeIfAbsent(color + "-" + scale, (name) -> {
            LabelStyle labelStyle = new LabelStyle();
            labelStyle.font = new BitmapFont(fontFile);
            labelStyle.fontColor = color;
            labelStyle.font.getData().setScale(scale);
            return labelStyle;
        });
    }

    public static LabelStyle get(Color color) {
        return get(color, scale);
    }

    public static LabelStyle get(float scale) {
        return get(color, scale);
    }

    public static LabelStyle get() {
        return get(color, scale);
    }

    public static Label label(String text) {
        return new Label(text, get());
    }

    public static Label label(String text, Color color) {
        return new Label(text, get(color));
    }

    public static Label label(String text, float scale) {
        return new Label(text, get(scale));
    }

    public static Label label(String text, Color color, float scale) {
        return new Label(text, get(color, scale));
    }
}
