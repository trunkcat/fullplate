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

package io.trunkcat.fullplate.components.common;

import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Table;

import io.trunkcat.fullplate.CookGame;

public class StageActor extends Group implements EventListener {
    protected final CookGame game;

    public enum StageActorType {
        ENTITY,
        SYSTEM
    }

    protected final StageActorType actorType;

    public StageActor(StageActorType actorType) {
        game = CookGame.getInstance();
        this.actorType = actorType;
    }

    public StageActorType getActorType() {
        return actorType;
    }

    protected void dispatchStageEvent(Event event) {
        getStage().getRoot().fire(event);
    }

    @Override
    public boolean handle(Event event) {
        return false;
    }

    protected void setStage(Stage stage) {
        super.setStage(stage);

        if (stage != null) {
            stage.addListener(this);
        }
    }

    public Table getDebugTable() {
        Table table = new Table();
        table.padLeft(15);
        table.defaults().expandX().left();
        return table;
    }
}
