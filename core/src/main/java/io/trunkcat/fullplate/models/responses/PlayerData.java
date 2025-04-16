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


import java.util.Collection;

public class PlayerData {
	private String playerId;
	private String username;
	private String email;
	private Stats stats;
	private Collection<UnlockedPlace> unlockedPlaces;

	public PlayerData() {
	}

	public String getPlayerId() {
		return playerId;
	}

	public String getUsername() {
		return username;
	}

	public String getEmail() {
		return email;
	}

	public Stats getStats() {
		return stats;
	}

	public Collection<UnlockedPlace> getUnlockedPlaces() {
		return unlockedPlaces;
	}

	public UnlockedPlace getUnlockedPlace(int placeId) {
		for (UnlockedPlace place : unlockedPlaces) {
			if (placeId == place.getPlaceId()) {
				return place;
			}
		}
		return null;
	}

	public static class Stats {
		private int playerLevel;
		private int coins;
		private int experiencePoints;

		public Stats() {
		}

		public int getPlayerLevel() {
			return playerLevel;
		}

		public int getCoins() {
			return coins;
		}

		public int getExperiencePoints() {
			return experiencePoints;
		}
	}

	public static class UnlockedPlace {
		private int placeId;
		private Collection<CompletedLevel> completedLevels;
		private Collection<UpgradedItem> upgradedItems;

		public UnlockedPlace() {
		}

		public int getPlaceId() {
			return placeId;
		}

		public void setPlaceId(int placeId) {
			this.placeId = placeId;
		}

		public Collection<CompletedLevel> getCompletedLevels() {
			return completedLevels;
		}

		public void setCompletedLevels(Collection<CompletedLevel> completedLevels) {
			this.completedLevels = completedLevels;
		}

		public CompletedLevel getCompletedLevel(int levelId) {
			for (CompletedLevel level : completedLevels) {
				if (placeId == level.getLevelId()) {
					return level;
				}
			}
			return null;
		}

		public Collection<UpgradedItem> getUpgradedItems() {
			return upgradedItems;
		}

		public void setUpgradedItems(Collection<UpgradedItem> upgradedItems) {
			this.upgradedItems = upgradedItems;
		}
	}

	public static class CompletedLevel {
		private int levelId;
		private Collection<GoalProgress> goalProgresses;

		public CompletedLevel() {
		}

		public int getLevelId() {
			return levelId;
		}

		public void setLevelId(int levelId) {
			this.levelId = levelId;
		}

		public Collection<GoalProgress> getGoalProgresses() {
			return goalProgresses;
		}

		public void setGoalProgresses(Collection<GoalProgress> goalProgresses) {
			this.goalProgresses = goalProgresses;
		}
	}

	public static class GoalProgress {
		private int goalId;
		private int obtainedValue;

		public GoalProgress() {
		}

		public int getGoalId() {
			return goalId;
		}

		public int getObtainedValue() {
			return obtainedValue;
		}
	}

	public static class UpgradedItem {
		private int itemId;
		private int level;

		public UpgradedItem() {
		}

		public int getItemId() {
			return itemId;
		}

		public int getLevel() {
			return level;
		}
	}
}
