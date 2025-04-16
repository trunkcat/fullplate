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

package io.trunkcat.fullplate.screens.restaurant;

import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.scenes.scene2d.EventListener;

import io.trunkcat.fullplate.components.kitchen.Customer;
import io.trunkcat.fullplate.components.kitchen.CustomerEvent;
import io.trunkcat.fullplate.models.responses.Place;

public class LevelEventListener implements EventListener {
	private final Place.Level levelData;
	private final LevelProgress levelProgress;

	LevelEventListener(Place.Level levelData, LevelProgress levelProgress) {
		this.levelData = levelData;
		this.levelProgress = levelProgress;
	}

	@Override
	public boolean handle(Event event) {
		if (event instanceof CustomerEvent) {
			CustomerEvent customerEvent = (CustomerEvent) event;
			Customer customer = customerEvent.getCustomer();

			if (event instanceof CustomerEvent.CustomerPositionChangeEvent) {
				CustomerEvent.CustomerPositionChangeEvent e = (CustomerEvent.CustomerPositionChangeEvent) event;
				if (e.getCurrentState() == Customer.PositionState.LEFT) {
					levelProgress.setCoins(levelProgress.getCoins() + customer.getCoins());
					levelProgress.setTip(levelProgress.getTip() + customer.getTip());
					return true;
				}
			}
		}
		return false;
	}
}
