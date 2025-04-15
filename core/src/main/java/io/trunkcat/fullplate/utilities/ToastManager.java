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

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;

// todo: make actually use of this
public class ToastManager {
	private final Stage stage;
	private final LabelStyle labelStyle;
	private final Pool<ToastActor> toastPool = new Pool<ToastActor>() {
		@Override
		protected ToastActor newObject() {
			return new ToastActor();
		}
	};

	public enum ToastType {
		INFO(0xF0F8FFFF, 0xD3E0FDFF, 0x0973DCFF),
		WARNING(0xFFFCF0FF, 0xFDF5D3FF, 0xDC7609FF),
		ERROR(0xFFF0F0FF, 0xFFE0E1FF, 0xE60000FF),
		SUCCESS(0xECFDF3FF, 0xD3FDE5FF, 0x008A2EFF);

		public final Color color;
		public final Color bgColor;
		public final Color borderColor;

		ToastType(int bgColor, int borderColor, int color) {
			this.color = new Color(color);
			this.bgColor = new Color(bgColor);
			this.borderColor = new Color(borderColor);
		}
	}

	private float toastDuration = 2.5f;
	private float animationDuration = 0.5f;
	private float spacing = 10f;
	private int screenPadding = 20;

	public ToastManager(Stage stage, BitmapFont font) {
		this.stage = stage;
		this.labelStyle = new LabelStyle(font, Color.WHITE);
	}

	public void showToast(String text) {
		showToast(text, ToastType.INFO);
	}

	public void showToast(String text, ToastType type) {
		showToast(text, type, toastDuration);
	}

	public void showToast(String text, ToastType type, float duration) {
		ToastActor toast = toastPool.obtain();
		toast.init(text, type, duration);

		float yPosition = screenPadding;
		for (Actor actor : new Array.ArrayIterator<>(stage.getActors())) {
			if (actor instanceof ToastActor) {
				yPosition = Math.max(yPosition, actor.getY() + actor.getHeight() + spacing);
			}
		}

		toast.setPosition(stage.getWidth() / 2, yPosition, Align.bottom);
		stage.addActor(toast);

		toast.addAction(Actions.sequence(
				Actions.alpha(0),
				Actions.moveBy(0, 20),
				Actions.parallel(
						Actions.fadeIn(animationDuration),
						Actions.moveBy(0, -20, animationDuration)
				),
				Actions.delay(duration),
				Actions.parallel(
						Actions.fadeOut(animationDuration),
						Actions.moveBy(0, -20, animationDuration)
				),
				Actions.run(() -> {
					stage.getActors().removeValue(toast, true);
					toastPool.free(toast);
				})
		));
	}

	private class ToastActor extends Label {
		public ToastActor() {
			super("", labelStyle);
			setAlignment(Align.center);
		}

		public void init(String text, ToastType type, float duration) {
			setText(text);
			setColor(type.color);

			float textWidth = getGlyphLayout().width;
			setSize(textWidth + 40, getStyle().font.getLineHeight() + 20);
		}

		@Override
		public void act(float delta) {
			super.act(delta);
			setX(stage.getWidth() / 2 - getWidth() / 2);
		}
	}

	public void setToastDuration(float toastDuration) {
		this.toastDuration = toastDuration;
	}

	public void setAnimationDuration(float animationDuration) {
		this.animationDuration = animationDuration;
	}

	public void setSpacing(float spacing) {
		this.spacing = spacing;
	}

	public void setScreenPadding(int screenPadding) {
		this.screenPadding = screenPadding;
	}
}
