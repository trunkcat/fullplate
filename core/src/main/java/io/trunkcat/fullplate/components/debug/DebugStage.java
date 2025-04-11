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

package io.trunkcat.fullplate.components.debug;

import static io.trunkcat.fullplate.components.debug.DebugLabelStyles.label;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.HorizontalGroup;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Tree;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

import io.trunkcat.fullplate.CookGame;
import io.trunkcat.fullplate.components.common.StageActor;
import io.trunkcat.fullplate.screens.CustomStage;

public class DebugStage extends CustomStage {
    private final Array<Node> rootNodes;

    public static class Node extends Tree.Node<Node, String, Table> {
        private final Actor stageActor;
        private final Table details;

        Node(Actor stageActor) {
            this(stageActor.getClass().getSimpleName(), stageActor);
        }

        Node(String name, Actor stageActor) {
            Table table = new Table();
            table.setDebug(false);
            table.defaults().expandX().space(2).left();
            table.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    super.clicked(event, x, y);
                    setExpanded(!isExpanded());
                }
            });

            String className = stageActor.getClass().getSimpleName();
            table.add(new Label(
                name + (name.equals(className) ? "" : " (" + className + ")"),
                DebugLabelStyles.get(1.3f).bold()
            )).row();

            details = new Table();
            details.defaults().expandX().space(2).left();
            table.add(details);
            update();

            setActor(table);

            this.stageActor = stageActor;

            if (stageActor instanceof Group) {
                for (Actor child : ((Group) stageActor).getChildren()) {
                    add(new Node(child));
                }
            }

            setExpanded(true);
        }

        public Actor getStageActor() {
            return stageActor;
        }

        public void update() {
            details.clear();

            if (stageActor == null) return;

            HorizontalGroup position = new HorizontalGroup();

            position.addActor(label("("));
            position.addActor(label("" + stageActor.getX(), Color.RED));
            position.addActor(label(", "));
            position.addActor(label("" + stageActor.getX(), Color.GREEN));
            position.addActor(label(", "));
            position.addActor(label("" + stageActor.getZIndex(), Color.SKY));
            position.addActor(label(")  /  "));

            position.addActor(label(
                stageActor.getWidth() + "x" + stageActor.getHeight()
            ));
            details.add(position).row();
            details.add(label(
                "scale: (" + stageActor.getScaleX() + ", " + stageActor.getScaleY() + "), " +
                    "origin: (" + stageActor.getOriginX() + ", " + stageActor.getOriginY() + ")"
            )).row();
            details.add(label(
                "top: " + stageActor.getTop() + ", right: " + stageActor.getRight() + " / " +
                    "rotation: " + stageActor.getRotation() + ", visible: " + stageActor.isVisible()
            )).row();

            if (stageActor instanceof StageActor) {
                Table debugTable = ((StageActor) stageActor).getDebugTable();
                if (debugTable != null) {
                    details.add(debugTable).colspan(details.getColumns()).padLeft(40).row();
                }
            }
        }
    }

    public DebugStage(CustomStage... targetStages) {
        super("debug", new ScreenViewport());

        CookGame game = CookGame.getInstance();

        Table layoutTable = new Table();
        layoutTable.setFillParent(true);

        Table panel = new Table();
        panel.pad(20);
        panel.defaults().space(10);

        Table topBar = new Table();
        panel.add(topBar).left().row();

        Table content = new Table();
        content.setBackground(game.testSkin.getDrawable("alpha-10-white-bg"));
        content.pad(15);
        content.defaults().expandX().space(5).left();

        Tree<Node, Node> rootTree = new Tree<>(game.testSkin);
        rootTree.setIconSpacing(10, 2);
        rootTree.setYSpacing(8);

        for (CustomStage stage : targetStages) {
            Node rootNode = new Node(stage.getStageName(), stage.getRoot());
            rootTree.add(rootNode);
        }
        rootNodes = rootTree.getRootNodes();

        content.add(rootTree);

        content.row();
        content.add().expandY(); // leave empty space (to flex the item to the top)
        content.pack();

        ScrollPane scrollPane = new ScrollPane(content, game.testSkin);
        scrollPane.setFadeScrollBars(false);
        scrollPane.setSmoothScrolling(true);
        scrollPane.setOverscroll(false, false);

        panel.add(scrollPane).grow();
        TextButton.TextButtonStyle buttonStyle = new TextButton.TextButtonStyle();
        buttonStyle.font = new BitmapFont();
        buttonStyle.fontColor = Color.WHITE;
        buttonStyle.font.getData().setScale(2);
        TextButton button = new TextButton("Close", buttonStyle);
        button.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                if (scrollPane.hasParent()) {
                    button.setText("Debug");
                    scrollPane.remove();
                } else {
                    button.setText("Close");
                    panel.addActorAt(panel.getRows() - 1, scrollPane);
                }
            }
        });
        topBar.add(button);
        layoutTable.add(panel).width(getWidth() / 4).pad(20).growY().expandX().right();

        addActor(layoutTable);
    }

    private Color color(Color color, float alpha) {
        return color.set(color, alpha);
    }

    private Drawable createColorDrawable(Color color) {
        return new TextureRegionDrawable(new TextureRegion(createColorTexture(color)));
    }

    private Texture createColorTexture(Color color) {
        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(color);
        pixmap.fill();
        Texture texture = new Texture(pixmap);
        pixmap.dispose();
        return texture;
    }

    @Override
    public void act(float delta) {
        super.act(delta);

        for (Node rootNode : new Array.ArrayIterator<>(rootNodes)) {
            update(rootNode);
        }
    }

    public void update(Node root) {
        root.update();

        // remove non-existent nodes
        for (int i = 0; i < root.getChildren().size; i++) {
            Node node = root.getChildren().get(i);
            if (!(node.getStageActor().getParent() == root.getStageActor())) {
                if (node.getParent() != null)
                    node.remove();
                else
                    root.getChildren().removeIndex(i);
                // if we removed a node, decrement the index to avoid skipping the next element
                i--;
            }
        }

        // add new nodes
        if (root.getStageActor() instanceof Group) {
            Group rootGroup = (Group) root.getStageActor();

            actorLoop:
            for (Actor actor : new Array.ArrayIterator<>(rootGroup.getChildren())) {
                if (actor instanceof Group) {
                    for (Node node : new Array.ArrayIterator<>(root.getChildren())) {
                        if (node.getStageActor() == actor) {
                            update(node);
                            continue actorLoop;
                        }
                    }
                    Node node = new Node(actor);
                    root.add(node);
                }
            }
        }
    }
}
