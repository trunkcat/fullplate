package io.trunkcat.fullplate.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Net;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;

import io.trunkcat.fullplate.CookGame;
import io.trunkcat.fullplate.models.PlayerData;
import io.trunkcat.fullplate.network.ApiResponse;

public class LoadingScreen extends Screen {
    public LoadingScreen(CookGame game) {
        super(game);
    }

    @Override
    public void show() {
        super.show();

        Table table = new Table();
        table.setFillParent(true);
        stage.addActor(table);

        BitmapFont font = game.SigmarFont.getSafe(38);
        Label.LabelStyle style = new Label.LabelStyle(font, Color.WHITE);
        Label loadStatusLabel = new Label("Loading...", style);

        table.add(loadStatusLabel);

        game.httpClient.get("/api/player", new Net.HttpResponseListener() {
            @Override
            public void handleHttpResponse(Net.HttpResponse httpResponse) {
                final String result = httpResponse.getResultAsString();
                final ApiResponse<PlayerData> response;

                try {
                    response = ApiResponse.fromJson(result, PlayerData.class);
                } catch (Exception e) {
                    Gdx.app.error("HTTP", "Failed to parse response: " + e.getMessage());
                    loadStatusLabel.setText("Something went wrong.");
                    return;
                }

                Gdx.app.postRunnable(() -> {
                    if (response.isOk()) {
                        game.playerData = response.getData();
                        Gdx.app.log("LOAD SCREEN", game.playerData.getUsername());
                        game.setScreen(new MenuScreen(game));
                    } else {
                        loadStatusLabel.setText(response.getMessage());
                    }
                });
            }

            @Override
            public void failed(Throwable t) {
                Gdx.app.log("HTTP", t.getMessage());
                loadStatusLabel.setText("Load failed.");
            }

            @Override
            public void cancelled() {
                loadStatusLabel.setText("Load failed.");
            }
        });
    }


}
