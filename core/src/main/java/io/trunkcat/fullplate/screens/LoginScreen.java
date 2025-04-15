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

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;

import java.util.HashMap;

import io.trunkcat.fullplate.models.responses.SignInData;
import io.trunkcat.fullplate.network.ResponseHandler;
import io.trunkcat.fullplate.utilities.AssetManager;
import io.trunkcat.fullplate.utilities.Constants;

public class LoginScreen extends SimpleScreen {
	public LoginScreen() {
		super(ScreenID.LOGIN_SCREEN);
	}

	public int getActorPosition(Table table, Actor actor) {
		table.layout(); // Ensure layout is up to date
		Array<Actor> children = table.getChildren();
		return children.indexOf(actor, true);
	}

	@Override
	public void show() {
		super.show();

		if (game.httpClient.hasAuthSessionToken()) {
			game.setScreen(new LoadingScreen());
			return;
		}

		Table table = new Table();
		table.setDebug(false);
		table.setFillParent(true);
		stage.addActor(table);

		final boolean[] register = {true};

		BitmapFont font38 = game.PallyFont.getSafe(38);
		Label.LabelStyle style = new Label.LabelStyle(font38, Color.WHITE);
		Label title = new Label("", style);

		table.add(title).colspan(2).pad(10);
		table.row();

		BitmapFont font24 = game.PallyFont.getSafe(28);
		Label.LabelStyle formLabelStyle = new Label.LabelStyle(font24, Color.WHITE);
		TextField.TextFieldStyle formInputStyle = new TextField.TextFieldStyle(
				font24, Color.WHITE, null, null,
				new TextureRegionDrawable(
						new TextureRegion(AssetManager.colorTexture(Color.DARK_GRAY)))
		);

		Label emailLabel = new Label("Email", formLabelStyle);
		TextField emailText = new TextField("", formInputStyle);

		table.add(emailLabel).pad(20).align(Align.right);
		table.add(emailText).pad(20).align(Align.left).prefWidth(200);
		table.row();

		Label usernameLabel = new Label("Username", formLabelStyle);
		TextField usernameText = new TextField("", formInputStyle);

		table.add(usernameLabel).pad(20).align(Align.right);
		table.add(usernameText).pad(20).align(Align.left).prefWidth(200);
		table.row();

		Label passwordLabel = new Label("Password", formLabelStyle);
		TextField passwordText = new TextField("", formInputStyle);
		passwordText.setPasswordMode(true);
		passwordText.setPasswordCharacter('*');

		table.add(passwordLabel).pad(20).align(Align.right);
		table.add(passwordText).pad(20).align(Align.left).prefWidth(200);
		table.row();

		TextButton signInButton = new TextButton("", game.skin);
		TextButton createAccountButton = new TextButton("", game.skin);

		// todo: make this a container and switch actors instead
		Runnable setupUI = () -> {
			if (register[0]) {
				title.setText("Create account");
				signInButton.setText("Create account");
				createAccountButton.setText("Login with existing account");
				emailLabel.setVisible(true);
				emailText.setVisible(true);
				table.invalidate();
			} else {
				title.setText("Login");
				signInButton.setText("Sign in");
				createAccountButton.setText("Don't have an account? Create one");
				emailLabel.setVisible(false);
				emailText.setVisible(false);
			}
			signInButton.setDisabled(false);
		};

		setupUI.run();

		onChange(
				createAccountButton, () -> {
					register[0] = !register[0];
					setupUI.run();
				}
		);

		BitmapFont font12 = game.PallyFont.getSafe(24);
		Label.LabelStyle messageLabelStyle = new Label.LabelStyle(font12, Color.RED);
		Label messageLabel = new Label("", messageLabelStyle);

		table.add(signInButton).colspan(2).pad(10);
		table.row();
		table.add(createAccountButton).colspan(2).pad(10);
		table.row();
		table.add(messageLabel).colspan(2);

		onChange(
				signInButton, () -> {
					if (signInButton.isDisabled()) {
						return;
					}

					messageLabel.setText("");
					signInButton.setDisabled(true);
					signInButton.setText("Logging in...");

					HashMap<String, String> data = new HashMap<>();
					if (register[0]) {
						data.put("email", emailText.getText());
					}
					data.put("username", usernameText.getText());
					data.put("password", passwordText.getText());

					final String apiPath = register[0] ? "/player/register" : "/player/login";

					game.httpClient.post(
							apiPath, data, new ResponseHandler<SignInData>() {
								public void success(SignInData data) {
									String sessionToken = data.getSessionToken();
									game.httpClient.setAuthSessionToken(sessionToken);
									game.preferences.putString(
											Constants.PREF_KEY_SESSION_TOKEN, sessionToken);
									game.preferences.flush();
									game.setScreen(new LoadingScreen());
								}

								public void failure(String message) {
									messageLabel.setText(message);
									setupUI.run();
								}
							}, SignInData.class
					);
				}
		);
	}
}
