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
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.utils.Align;

import java.util.HashMap;

import io.trunkcat.fullplate.models.responses.SignInData;
import io.trunkcat.fullplate.network.ResponseHandler;
import io.trunkcat.fullplate.utilities.Constants;

public class LoginScreen extends SimpleScreen {
    public LoginScreen() {
        super(ScreenID.LOGIN_SCREEN);
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

        BitmapFont font38 = game.PallyFont.getSafe(38);
        Label.LabelStyle style = new Label.LabelStyle(font38, Color.WHITE);
        Label title = new Label("Login or register", style);

        table.add(title).colspan(2).pad(10);
        table.row();

        BitmapFont font24 = game.PallyFont.getSafe(28);
        Label.LabelStyle formLabelStyle = new Label.LabelStyle(font24, Color.WHITE);
        TextField.TextFieldStyle formInputStyle = new TextField.TextFieldStyle(font24, Color.WHITE, null, null, null);

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

        TextButton signInButton = new TextButton("Login", game.skin);

        BitmapFont font12 = game.PallyFont.getSafe(24);
        Label.LabelStyle messageLabelStyle = new Label.LabelStyle(font12, Color.RED);
        Label messageLabel = new Label("", messageLabelStyle);

        onChange(signInButton, () -> {
            if (signInButton.isDisabled()) return;

            messageLabel.setText("");
            signInButton.setDisabled(true);
            signInButton.setText("Logging in...");

            HashMap<String, String> data = new HashMap<>();
            data.put("username", usernameText.getText());
            data.put("password", passwordText.getText());

            game.httpClient.post("/sign-in", data, new ResponseHandler<SignInData>() {
                public void success(SignInData data) {
                    String sessionToken = data.getSessionToken();
                    game.httpClient.setAuthSessionToken(sessionToken);
                    game.preferences.putString(Constants.PREF_KEY_SESSION_TOKEN, sessionToken);
                    game.preferences.flush();
                    game.setScreen(new LoadingScreen());
                }

                public void failure(String message) {
                    messageLabel.setText(message);
                    signInButton.setText("Login");
                    signInButton.setDisabled(false);
                }
            }, SignInData.class);
        });

        table.add(signInButton).colspan(2).pad(10);
        table.row();
        table.add(messageLabel).colspan(2);
    }
}
