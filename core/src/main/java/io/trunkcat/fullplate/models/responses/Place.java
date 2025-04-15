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

package io.trunkcat.fullplate.models.responses;

import com.badlogic.gdx.utils.Array;

import java.util.Collection;

public class Place {
	private int placeId;
	private String type;
	private String name;
	private String description;
	private Array<Integer> position;
	private int price;
	private int unlocksAt;
	private Collection<Level> levels;
	private Collection<Item> items;

	public Place() {
	}

	public int getPlaceId() {
		return placeId;
	}

	public String getType() {
		return type;
	}

	public String getName() {
		return name;
	}

	public String getDescription() {
		return description;
	}

	public Array<Integer> getPosition() {
		return position;
	}

	public int getPrice() {
		return price;
	}

	public int getUnlocksAt() {
		return unlocksAt;
	}

	public Collection<Item> getItems() {
		return items;
	}

	public Collection<Level> getLevels() {
		return levels;
	}

	public static class Level {
		private int levelId;
		private int levelNo;
		private Collection<Goal> goals;

		public Level() {
		}

		public int getLevelId() {
			return levelId;
		}

		public int getLevelNo() {
			return levelNo;
		}

		public static class Goal {
			private int goalId;
			private String goalType;
			private int goalValue;

			public Goal() {
			}

			public int getGoalId() {
				return goalId;
			}


			public String getGoalType() {
				return goalType;
			}

			public int getGoalValue() {
				return goalValue;
			}
		}
	}

	public static class Item {
		private int itemId;
		private String gameItem;
		private int maxLevel;
		private int unlocksIn;

		public Item() {
		}

		public int getItemId() {
			return itemId;
		}

		public String getGameItem() {
			return gameItem;
		}

		public int getMaxLevel() {
			return maxLevel;
		}

		public int getUnlocksIn() {
			return unlocksIn;
		}
	}
}
