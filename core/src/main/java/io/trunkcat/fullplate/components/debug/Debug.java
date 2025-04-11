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

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

import java.util.HashMap;
import java.util.Map;

// TODO: make Debug an actor that is added to the StageActor group.
public class Debug {
    public final GlyphLayout glyphLayout;
    private final Map<String, Float> textWidthCache = new HashMap<>();
    public final BitmapFont font;
    public final ShapeRenderer shapes = new ShapeRenderer();
    private Batch batch;

    public Debug() {
        glyphLayout = new GlyphLayout();
        font = new BitmapFont();
    }

    public BitmapFont getFont() {
        return font;
    }

    public void setBatch(Batch batch) {
        this.batch = batch;
    }

    public float getLineWidth(String text) {
        return textWidthCache.computeIfAbsent(text, t -> {
            glyphLayout.setText(font, text);
            return glyphLayout.width;
        });
    }

    public float getLineHeight() {
        return font.getLineHeight();
    }

    public float getFontScaleX() {
        return font.getScaleX();
    }

    public float getFontScaleY() {
        return font.getScaleY();
    }

    public void setFontScale(float scale) {
        font.getData().setScale(scale);
    }

    public void scaleFont(float scale) {
        font.getData().scale(scale);
    }

    public void text(String text, float x, float y) {
        font.draw(batch, text, x, y);
    }

    public void dispose() {
        font.dispose();
        shapes.dispose();
    }
}
