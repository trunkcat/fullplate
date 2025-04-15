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
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Array;

import io.trunkcat.fullplate.models.responses.Place;
import io.trunkcat.fullplate.models.responses.PlayerData;
import io.trunkcat.fullplate.network.ResponseHandler;
import io.trunkcat.fullplate.screens.home.HomeScreen;

public class LoadingScreen extends SimpleScreen {
	public LoadingScreen() {
		super(ScreenID.LOADING_SCREEN);
	}

	@Override
	public void show() {
		super.show();

		Table table = new Table();
		table.setFillParent(true);
		stage.addActor(table);

		BitmapFont font = game.PallyFont.getSafe(38);
		Label.LabelStyle style = new Label.LabelStyle(font, Color.WHITE);
		Label loadStatusLabel = new Label("Loading...", style);

		table.add(loadStatusLabel);

		game.httpClient.get(
				"/player", new ResponseHandler<PlayerData>() {
					public void success(PlayerData playerData) {
						game.player = playerData;
						Gdx.app.log("Load", "Logged in as " + game.player.getUsername());

						game.httpClient.get(
								"/data/places", new ResponseHandler<Array<Place>>() {
									public void success(Array<Place> places) {
										game.data.setPlaces(places);
										game.setScreen(new HomeScreen());
									}

									public void failure(String message) {
										loadStatusLabel.setText(message);
									}
								}, Place.class, true
						);
					}

					public void failure(String message) {
						loadStatusLabel.setText(message);
//						game.player.logout();
					}
				}, PlayerData.class
		);
	}
}
