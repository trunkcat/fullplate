package io.trunkcat.fullplate.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Net;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.utils.Align;

import java.util.HashMap;

import io.trunkcat.fullplate.CookGame;
import io.trunkcat.fullplate.models.responses.SignInData;
import io.trunkcat.fullplate.network.ApiResponse;

public class LoginScreen extends Screen {
    public LoginScreen(final CookGame cookGame) {
        super(cookGame);
    }

    @Override
    public void show() {
        super.show();

        if (game.httpClient.hasAuthSessionToken()) {
            game.setScreen(new LoadingScreen(game));
            return;
        }

        Table table = new Table();
        table.setDebug(false);
        table.setFillParent(true);
        stage.addActor(table);

        BitmapFont font38 = game.SigmarFont.getSafe(38);
        Label.LabelStyle style = new Label.LabelStyle(font38, Color.WHITE);
        Label title = new Label("Login or register", style);

        table.add(title).colspan(2).pad(10);
        table.row();

        BitmapFont font24 = game.SigmarFont.getSafe(28);
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

        BitmapFont font12 = game.SigmarFont.getSafe(24);
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

            game.httpClient.post("/api/sign-in", data, new Net.HttpResponseListener() {
                @Override
                public void handleHttpResponse(Net.HttpResponse httpResponse) {
                    final String result = httpResponse.getResultAsString();
                    final ApiResponse<SignInData> response;

                    try {
                        response = ApiResponse.fromJson(result, SignInData.class);
                    } catch (Exception e) {
                        Gdx.app.error("HTTP", "Failed to parse response: " + e.getMessage());
                        messageLabel.setText("Something went wrong.");
                        return;
                    }

                    Gdx.app.postRunnable(() -> {
                        if (response.isOk()) {
                            SignInData data = response.getData();
                            String sessionToken = data.getSessionToken();
                            game.httpClient.setAuthSessionToken(sessionToken);
                            game.preferences.putString("sessionToken", sessionToken);
                            game.preferences.flush();
                            game.setScreen(new LoadingScreen(game));
                        } else {
                            messageLabel.setText(response.getMessage());
                        }
                    });
                }

                @Override
                public void failed(Throwable t) {
                    Gdx.app.log("HTTP", t.getMessage());
                    messageLabel.setText("Something went wrong.");
                }

                @Override
                public void cancelled() {
                    messageLabel.setText("Something went wrong.");
                }
            });

            signInButton.setText("Login");
            signInButton.setDisabled(false);
        });

        table.add(signInButton).colspan(2).pad(10);
        table.row();
        table.add(messageLabel).colspan(2);
    }
}
