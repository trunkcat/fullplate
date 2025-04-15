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

package io.trunkcat.fullplate.screens.home;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.ButtonGroup;
import com.badlogic.gdx.scenes.scene2d.ui.Container;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.utils.Array;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import io.trunkcat.fullplate.CookGame;
import io.trunkcat.fullplate.models.responses.Place;
import io.trunkcat.fullplate.models.responses.PlayerData;
import io.trunkcat.fullplate.utilities.WidgetFactory;

class Tab {
	private String id;
	private Button.ButtonStyle style;
	private boolean visible;

	public Tab(String id, Button.ButtonStyle style, boolean visible) {
		this.id = id;
		this.style = style;
		this.visible = visible;
	}

	public String getId() {
		return id;
	}

	public Button.ButtonStyle getStyle() {
		return style;
	}

	public boolean isVisible() {
		return visible;
	}
}

public class PlacePointer extends Button {
	private final CookGame game;
	private final Place place;
	private PlayerData.UnlockedPlace unlockedPlace;

	public PlacePointer(Place place, Stage stage) {
		this.game = CookGame.getInstance();
		this.place = place;
		this.unlockedPlace = getUnlockedPlace();

		addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				Table content = new Table();
				Table main = new Table();
				content.add(main).fill().expand().grow().left();
				content.row();

				Array<Tab> tabsInfo = new Array<>();
				tabsInfo.addAll(
						new Tab("overview", WidgetFactory.PLACE_PLAY_BUTTON_STYLE, true),
						new Tab("levels", WidgetFactory.PLACE_PLAY_BUTTON_STYLE, true),
						new Tab("upgrades", WidgetFactory.PLACE_PLAY_BUTTON_STYLE, true),
						new Tab("inventory", WidgetFactory.PLACE_PLAY_BUTTON_STYLE, false)
				);

				Table tabItems = new Table();
				tabItems.defaults().growX();
				ScrollPane tabsScrollPane = new ScrollPane(tabItems, game.skin);
				tabsScrollPane.setOverscroll(false, false);
				tabsScrollPane.setScrollingDisabled(true, false);
				tabsScrollPane.setFadeScrollBars(false);

				Container<Table> view = new Container<>();
				view.padLeft(20f);
				view.padRight(20f);
				view.fillX();
				ScrollPane viewScrollPane = new ScrollPane(view, game.skin);
				viewScrollPane.setOverscroll(false, false);
				viewScrollPane.setFadeScrollBars(false);
				viewScrollPane.setScrollingDisabled(true, false);

				ButtonGroup<Button> group = new ButtonGroup<>();
				for (Tab tab : new Array.ArrayIterator<>(tabsInfo)) {
					Button tabButton = new Button(tab.getStyle());
					tabItems.add(tabButton).size(100f).row();
					tabButton.addListener(new ChangeListener() {
						@Override
						public void changed(ChangeEvent event, Actor actor) {
							if (tabButton.isChecked()) {
								view.setActor(getView(tab.getId()));
							}
						}
					});
					tabButton.setVisible(tab.isVisible());
					tabButton.setDisabled(!tab.isVisible());
					group.add(tabButton);
				}

				view.top();
				view.left();

				main.add(tabsScrollPane).width(100f).left();
				main.add(viewScrollPane).fill().expand().grow().left().padLeft(10f);

				if (!isPlaceUnlocked()) {
					Table footer = new Table();
					content.add(footer).padTop(20f).fill().expand().grow();

					TextButton textButton = new TextButton(
							"Buy for $" + place.getPrice(), game.skin);
					footer.add(textButton).fill().expand().grow();
				}

				WidgetFactory.WindowProps props = new WidgetFactory.WindowProps();
				props.setTitle(place.getName());
				props.setDescription(place.getType());
				props.setMinWidth(stage.getWidth() / 2f);
				props.setPosition(stage.getWidth() / 2f, stage.getHeight() / 2f);

				Window window = WidgetFactory.createWindow(
						game.skin.get(Window.WindowStyle.class), content, props, stage);
				stage.addActor(window);
			}
		});
	}

	@Override
	public void act(float delta) {
		super.act(delta);

		this.unlockedPlace = getUnlockedPlace();

		this.setBounds(
				place.getPosition().get(0),
				place.getPosition().get(1),
				100, 100
		);
		this.setStyle(isPlaceUnlocked()
		              ? WidgetFactory.PLACE_PLAY_BUTTON_STYLE
		              : WidgetFactory.PLACE_LOCKED_BUTTON_STYLE);
	}

	private Table getView(String id) {
		Table table = new Table();
		table.defaults().expand().fill().grow().left().top();

		if (!isPlaceUnlocked()) {
			Label title = new Label("Description", game.skin, "h2");
			table.add(title).spaceBottom(20f).row();
			Label description = new Label(place.getDescription(), game.skin);
			description.setWrap(true);
			table.add(description).spaceBottom(40f).row();

			Label levelRequirement = new Label(
					"Requires Level " + place.getUnlocksAt(), game.skin);
			table.add(levelRequirement);
			return table;
		}

		Map<Integer, Integer> levelIdToNo = place
				.getLevels()
				.stream()
				.collect(Collectors.toMap(Place.Level::getLevelId, Place.Level::getLevelNo));

		int nextPlayableLevel = unlockedPlace
				.getCompletedLevels()
				.stream()
				.map(level -> levelIdToNo.getOrDefault(level.getLevelId(), 1))
				.max(Comparator.naturalOrder())
				.orElse(1);

		switch (id) {
			case "levels": {
				Table levelsTable = new Table();
				levelsTable.defaults().space(10f);
				final int MAX_LEVELS_PER_ROW = 5;

				int remaining = place.getLevels().size() % MAX_LEVELS_PER_ROW;
				int toAdd = MAX_LEVELS_PER_ROW - remaining;

				int i = 0;

				List<Place.Level> levels = place
						.getLevels()
						.stream()
						.sorted(Comparator.comparingInt(Place.Level::getLevelNo))
						.collect(Collectors.toList());

				for (Place.Level level : levels) {
					TextButton playButton = new TextButton(
							String.valueOf(level.getLevelNo()), game.skin);

					if (level.getLevelNo() > nextPlayableLevel) {
						playButton.setDisabled(true);
					}
					levelsTable.add(playButton).expandX().fillX();
					if (++i % MAX_LEVELS_PER_ROW == 0) {
						levelsTable.row();
					}
				}

				for (i = 0; i < toAdd; i++) {
					TextButton playButton = new TextButton("", game.skin);
					playButton.setVisible(false);
					playButton.setDisabled(true);
					levelsTable.add(playButton).expandX().fillX();
				}

				table.add(levelsTable);
				break;
			}

			case "upgrades": {
				Table upgradesTable = new Table();
				upgradesTable.defaults().space(10f);

				NinePatchDrawable background = new NinePatchDrawable(
						game.skin.getPatch("window-rect"));

				List<Place.Item> items = place
						.getItems()
						.stream()
						.sorted((a, b) ->
								        Boolean.compare(
										        a.getUnlocksIn()
												        > game.player.getStats()
												                     .getPlayerLevel(),
										        b.getUnlocksIn()
												        > game.player.getStats()
												                     .getPlayerLevel()
								        )
						).collect(Collectors.toList());

				for (Place.Item item : items) {
					PlayerData.UpgradedItem upgrade = unlockedPlace
							.getUpgradedItems()
							.stream()
							.filter((upgradedItem -> upgradedItem.getItemId()
									== item.getItemId()))
							.findFirst()
							.orElse(null);

					boolean hasUnlocked = item.getUnlocksIn() <= nextPlayableLevel;
					int currentItemLevel = upgrade != null ? upgrade.getLevel() : 1;

					Table itemTable = new Table();
					itemTable.setBackground(background);

					Table itemDetails = new Table();
					itemDetails.defaults().expandX().fillX().left();
					itemTable.add(itemDetails).expandX().fillX().pad(2f);

					Label itemName = new Label(item.getGameItem(), game.skin, "h2");
					itemDetails.add(itemName).row();

					Label itemDescription = new Label("", game.skin);

					if (!hasUnlocked) {
						itemDescription.setText("Unlocks in level " + item.getUnlocksIn());
					} else if (currentItemLevel >= item.getMaxLevel()) {
						itemDescription.setText("Fully upgraded to level " + currentItemLevel);
					} else {
						itemDescription.setText("Upgradable from " + currentItemLevel + " to "
								                        + currentItemLevel + 1);
					}
					itemDetails.add(itemDescription);

					boolean upgradable = hasUnlocked && currentItemLevel < item.getMaxLevel();
					if (upgradable) {
						// todo: actual upgrading
						TextButton upgradeButton = new TextButton("Upgrade", game.skin);
						itemTable.add(upgradeButton);
					}

					upgradesTable.add(itemTable).pad(2f).expandX().fillX();
					upgradesTable.row();
				}

				table.add(upgradesTable);
				break;
			}

			case "inventory":
			case "overview":
			default: {
				Label title = new Label("Overview and Statistics", game.skin, "h2");
				table.add(title).spaceBottom(20f).row();
				Label description = new Label(place.getDescription(), game.skin);
				description.setWrap(true);
				table.add(description);
			}
		}
		return table;
	}

	private boolean isPlaceUnlocked() {
		return unlockedPlace != null;
	}

	private PlayerData.UnlockedPlace getUnlockedPlace() {
		for (PlayerData.UnlockedPlace unlockedPlace : game.player.getUnlockedPlaces()) {
			if (unlockedPlace.getPlaceId() == place.getPlaceId()) {
				return unlockedPlace;
			}
		}
		return null;
	}
}