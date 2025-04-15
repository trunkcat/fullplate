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

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;

import io.trunkcat.fullplate.CookGame;

public class WidgetFactory {
	private static final CookGame game = CookGame.getInstance();

	public static class WindowProps {
		private static final float DEFAULT_WINDOW_MARGIN = 15f;

		private float marginTop = DEFAULT_WINDOW_MARGIN;
		private float marginBottom = DEFAULT_WINDOW_MARGIN;
		private float marginLeft = DEFAULT_WINDOW_MARGIN;
		private float marginRight = DEFAULT_WINDOW_MARGIN;
		private float spaceY = 20f;
		private float minWidth;
		private float maxWidth;
		private float minHeight;
		private float maxHeight;
		private boolean closeButton = true;
		private String title;
		private String description;
		private float positionX;
		private float positionY;

		public WindowProps() {
		}

		public void setMargin(float margin) {
			this.marginTop = margin;
			this.marginBottom = margin;
			this.marginLeft = margin;
			this.marginRight = margin;
		}

		public float getMarginTop() {
			return marginTop;
		}

		public void setMarginTop(float marginTop) {
			this.marginTop = marginTop;
		}

		public float getMarginBottom() {
			return marginBottom;
		}

		public void setMarginBottom(float marginBottom) {
			this.marginBottom = marginBottom;
		}

		public float getMarginLeft() {
			return marginLeft;
		}

		public void setMarginLeft(float marginLeft) {
			this.marginLeft = marginLeft;
		}

		public float getMarginRight() {
			return marginRight;
		}

		public void setMarginRight(float marginRight) {
			this.marginRight = marginRight;
		}

		public float getSpaceY() {
			return spaceY;
		}

		public void setSpaceY(float spaceY) {
			this.spaceY = spaceY;
		}

		public float getMinWidth() {
			return minWidth;
		}

		public void setMinWidth(float minWidth) {
			this.minWidth = minWidth;
		}

		public float getMaxWidth() {
			return maxWidth;
		}

		public void setMaxWidth(float maxWidth) {
			this.maxWidth = maxWidth;
		}

		public float getMinHeight() {
			return minHeight;
		}

		public void setMinHeight(float minHeight) {
			this.minHeight = minHeight;
		}

		public float getMaxHeight() {
			return maxHeight;
		}

		public void setMaxHeight(float maxHeight) {
			this.maxHeight = maxHeight;
		}

		public boolean isCloseButton() {
			return closeButton;
		}

		public void setCloseButton(boolean closeButton) {
			this.closeButton = closeButton;
		}

		public String getTitle() {
			return title;
		}

		public void setTitle(String title) {
			this.title = title;
		}

		public String getDescription() {
			return description;
		}

		public void setDescription(String description) {
			this.description = description;
		}

		public float getPositionX() {
			return positionX;
		}

		public void setPositionX(float positionX) {
			this.positionX = positionX;
		}

		public float getPositionY() {
			return positionY;
		}

		public void setPosition(float x, float y) {
			this.positionX = x;
			this.positionY = y;
		}

		public void setPositionY(float positionY) {
			this.positionY = positionY;
		}
	}

	public static Window createWindow(Window.WindowStyle windowStyle,
	                                  Table content,
	                                  WindowProps props,
	                                  Stage stage) {
		float minWidth = props.getMinWidth();
		float maxWidth = props.getMaxWidth();
		float minHeight = props.getMinHeight();
		float maxHeight = props.getMaxHeight();
		float marginTop = props.getMarginTop();
		float marginBottom = props.getMarginBottom();
		float marginLeft = props.getMarginLeft();
		float marginRight = props.getMarginRight();
		float spaceY = props.getSpaceY();

		final float DEFAULT_MAX_HEIGHT = stage.getHeight() * (3f / 4f);
		final float MAX_POSSIBLE_HEIGHT = stage.getHeight() - marginTop - marginBottom;
		maxHeight = Math.max(
				Math.min(minHeight, DEFAULT_MAX_HEIGHT),
				Math.max(maxHeight, MAX_POSSIBLE_HEIGHT)
		);

		final float DEFAULT_MAX_WIDTH = stage.getWidth() / 2f;
		final float MAX_POSSIBLE_WIDTH = stage.getWidth() - marginLeft - marginRight;
		maxWidth = Math.max(
				Math.max(minWidth, DEFAULT_MAX_WIDTH),
				Math.min(maxWidth, MAX_POSSIBLE_WIDTH)
		);

		if (minWidth > maxWidth) {
			minWidth = maxWidth;
		}
		if (marginTop > 0) {
			maxHeight -= marginTop;
		}
		if (marginBottom > 0) {
			maxHeight -= marginBottom;
		}
		if (minHeight > maxHeight) {
			minHeight = maxHeight;
		}

		Window window = new Window("", windowStyle) {
			@Override
			public void pack() {
				super.pack();
				setPosition(
						props.getPositionX() - getWidth() / 2f,
						props.getPositionY() - getHeight() / 2f
				);
			}
		};
		window.setMovable(false);
		window.setModal(true);
		window.setKeepWithinStage(true);
		window.setResizable(false);
		window.getTitleTable().clearChildren();
		window.getTitleTable().remove();

		Table table = new Table();
		window.add(table)
		      .minHeight(minHeight)
		      .maxHeight(maxHeight)
		      .maxWidth(maxWidth)
		      .padTop(marginTop)
		      .padBottom(marginBottom)
		      .padLeft(marginLeft)
		      .padRight(marginRight)
		      .expandX()
		      .expandY()
		      .fill();

		final float CLOSE_BTN_LEFT_PAD = 20f;

		Table titleBar = new Table();
		Table titleSection = new Table();
		titleSection.defaults().expandX().left();
		titleBar.add(titleSection);
		titleBar.add().expandX();

		boolean hasTitle = props.getTitle() != null && !props.getTitle().isEmpty(),
				hasDescription =
						props.getDescription() != null && !props.getDescription().isEmpty();

		Button.ButtonStyle closeButtonStyle =
				game.skin.get("place-lock-button", Button.ButtonStyle.class);

		float textWrapWidth = maxWidth - (marginLeft + marginRight);
		if (props.isCloseButton()) {
			textWrapWidth -= CLOSE_BTN_LEFT_PAD + closeButtonStyle.up.getMinWidth();
		}

		if (hasTitle) {
			Label windowTitle = new Label(props.getTitle(), game.skin, "h1");
			float width = Math.min(windowTitle.getPrefWidth(), textWrapWidth);
			windowTitle.setWrap(true);
			titleSection.add(windowTitle).width(width).row();
		}
		if (hasDescription) {
			Label windowDescription = new Label(props.getDescription(), game.skin);
			float width = Math.min(windowDescription.getPrefWidth(), textWrapWidth);
			windowDescription.setWrap(true);
			titleSection.add(windowDescription).width(width).row();
		}
		if (props.isCloseButton()) {
			Button closeButton = new Button(closeButtonStyle);
			closeButton.addListener(new ChangeListener() {
				@Override
				public void changed(ChangeEvent event, Actor actor) {
					window.remove();
				}
			});
			titleBar.add(closeButton).padLeft(CLOSE_BTN_LEFT_PAD).right().top();
		}

		if (hasTitle || hasDescription || props.isCloseButton()) {
			table.add(titleBar).padBottom(spaceY).expandX().top().fillX();
			table.row();
		}

		table.add(content).expand().fill().grow()
		     .minWidth(minWidth)
		     .left();

		window.pack();

		return window;
	}

	public static final Button.ButtonStyle PLACE_LOCKED_BUTTON_STYLE = game.skin.get(
			"place-lock-button", Button.ButtonStyle.class);

	public static final Button.ButtonStyle PLACE_PLAY_BUTTON_STYLE = game.skin.get(
			"place-play-button", Button.ButtonStyle.class);
}
