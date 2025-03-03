package io.trunkcat.fullplate.screens;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

import io.trunkcat.fullplate.CookGame;

public class MenuScreen extends Screen {
    public MenuScreen(CookGame cookGame) {
        super(cookGame);
    }

    @Override
    public void show() {
        super.show();

        Table table = new Table();
        table.setDebug(false);
        table.setFillParent(true);
        stage.addActor(table);

        BitmapFont font38 = game.SigmarFont.getSafe(38);
        Label.LabelStyle style = new Label.LabelStyle(font38, Color.WHITE);
        Label title = new Label("Logged in as " + game.playerData.getUsername(), style);

        table.add(title).colspan(2).pad(30);
        table.row();


        BitmapFont font24 = game.SigmarFont.getSafe(24);
        TextButton.TextButtonStyle buttonStyle = new TextButton.TextButtonStyle();
        buttonStyle.font = font24;
        buttonStyle.fontColor = Color.GRAY;

        TextButton logoutButton = new TextButton("Logout", buttonStyle);
        table.add(logoutButton).colspan(2).pad(30);
        table.row();

        logoutButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);

                game.httpClient.setAuthSessionToken(null);
                game.playerData = null;
                game.preferences.remove("sessionToken");
                game.preferences.flush();
                
                game.setScreen(new LoginScreen(game));
            }
        });
    }
}
