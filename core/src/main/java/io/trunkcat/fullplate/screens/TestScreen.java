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

package io.trunkcat.fullplate.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.viewport.FitViewport;

import io.trunkcat.fullplate.utilities.WidgetFactory;

public class TestScreen extends BaseScreen {
	private final Stage stage;

	public TestScreen() {
		super(ScreenID.HOME_SCREEN);

		stage = new Stage(new FitViewport(Gdx.graphics.getWidth(), Gdx.graphics.getHeight()));

		Table content = new Table();

		Table main = new Table();
		ScrollPane scrollPane = new ScrollPane(main, game.skin);
		scrollPane.setOverscroll(false, false);
		scrollPane.setFadeScrollBars(false);
		scrollPane.setScrollingDisabled(true, false);

		for (int i = 0; i < 50; i++) {
			Label label = new Label("Audio settings", game.skin, "h2");
			main.add(label).expandX().left().row();
		}

		content.add(scrollPane).fill().expand().grow().left();
		content.row();

		Table footer = new Table();
		content.add(footer).padTop(20f).fill().expand().grow();

		TextButton textButton = new TextButton("Movie", game.skin);
		footer.add(textButton).fill().expand().grow().left();

		TextButton textButton2 = new TextButton("Movie", game.skin);
		footer.add(textButton2).spaceLeft(10f).fill().expand().grow().left();

		float maxWidth = stage.getWidth() / 2f;
//		showTestWindow(content, 15f, 30f, 700, maxWidth, true);

		WidgetFactory.WindowProps props = new WidgetFactory.WindowProps();
		props.setTitle("Settings");
		props.setDescription("Game settings can be changed here.");
		props.setCloseButton(true);
		props.setMargin(15f);
		props.setSpaceY(30f);
		props.setMaxWidth(1000f);

		Window window = WidgetFactory.createWindow(
				game.skin.get(Window.WindowStyle.class),
				content, props, stage
		);
		stage.addActor(window);
	}

	@Override
	public void show() {
		InputMultiplexer inputMultiplexer = new InputMultiplexer();
		inputMultiplexer.addProcessor(new InputAdapter() {
			@Override
			public boolean keyDown(int keycode) {
				if (keycode == Input.Keys.BACK) {

					return true;
				}
				return false;
			}
		});
		inputMultiplexer.addProcessor(stage);
		Gdx.input.setInputProcessor(inputMultiplexer);
		Gdx.input.setCatchKey(Input.Keys.BACK, true);
	}

	@Override
	public void render(float delta) {
		Gdx.gl.glClearColor(0f, 0f, 0f, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		stage.act(delta);
		stage.draw();
	}

	@Override
	public void resize(int width, int height) {
		stage.getViewport().update(width, height, true);
	}

	@Override
	public void dispose() {
		stage.dispose();
	}

	private void showTestWindow(Table content, float margin, float spaceY, float minWidth,
	                            float maxWidth,
	                            boolean showCloseButton) {
		final Stage stage = this.stage;

		if (margin > 0) {
			maxWidth -= margin * 2f;
		}

		Window window = new Window("", game.skin);
		window.setMovable(false);
		window.setModal(true);
		window.setKeepWithinStage(true);
		window.setResizable(false);
//		window.setBackground(
//				new TextureRegionDrawable(AssetManager.loadTexture("items/debug-white.png")));

		if (minWidth > maxWidth) {
			minWidth = maxWidth;
		}

		minWidth = Math.min(stage.getWidth() - (margin > 0 ? margin * 2f : 0), minWidth);

		window.getTitleTable().clearChildren();
		window.getTitleTable().remove();

		Table table = new Table();
		float maxHeight = stage.getHeight() / 4 * 3;

		if (margin > 0) {
			maxHeight -= margin * 2f;
		}

		window.add(table)
		      .maxHeight(maxHeight)
		      .maxWidth(maxWidth)
		      .pad(margin)
		      .expandX()
		      .expandY()
		      .fill();

		Label label = new Label("Settings", game.skin, "h1");
		Label desc = new Label("Change your game settings here", game.skin);

		Table title = new Table();
		Table title2 = new Table();
		title2.defaults().expandX().left();
		title2.add(label).row();
		title2.add(desc).row();

		title.add(title2).padRight(20f);
		title.add().expandX();

		if (showCloseButton) {
			Button closeButton = new Button(
					game.skin.get("place-lock-button", Button.ButtonStyle.class));
			closeButton.addListener(new ChangeListener() {
				@Override
				public void changed(ChangeEvent event, Actor actor) {
					window.remove();
				}
			});
			title.add(closeButton).right().top();
		}

		table.add(title).padBottom(spaceY).expandX().top().fillX();
		table.row();

		table.add(content).expand().fill().grow()
		     .minWidth(minWidth)
		     .left();

		title.layout();
		table.layout();

		window.pack();

		table.setDebug(true, true);
		Gdx.app.log("dims", window.getWidth() + "x" + window.getHeight());
		window.setPosition(
				stage.getWidth() / 2f - window.getWidth() / 2f,
				stage.getHeight() / 2f - window.getHeight() / 2f
		);

		stage.addActor(window);
	}
}
