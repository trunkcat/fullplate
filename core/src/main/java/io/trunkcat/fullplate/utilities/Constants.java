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

package io.trunkcat.fullplate.utilities;

public interface Constants {
    String PREF_KEY_SESSION_TOKEN = "sessionToken";

    /**
     * <code>Base cook time x Min Factor</code> gives the very minimum time the food cooking time possible, for capping the time.
     * This helps preventing the food cooking speed from going to shorter than the minimum.
     */
    float COOK_TIME_MIN_FACTOR = 0.3f; // 30% of the base time


    /**
     * Sets the speed modifier per level for food cookers.
     * <code>Level * Speed Modifier</code> gives the speed modifier for the food cooker.
     * Food cooker levels start from 0.
     * For level 0, the speed modifier is 0x.
     * For level 1, the speed modifier is 0.2x.
     * For level 2, the speed modifier is 0.4x, etc.
     */
    float FOOD_COOKER_LEVEL_SPEED_MODIFIER = 0.2f;

    /**
     * Combinations without a set overcooking time will default to this value.
     * <p>
     * Unit: seconds
     */
    float DEFAULT_OVERCOOKING_TIME = 15f;

    // TODO: could make this randomised based on min and max, or based on the combination.
    float ADDITIONAL_PROCESSING_TIME = 10f;

    float TIME_BONUS_FACTOR = 0.2f; // 20% of the order processingTime is added to wait time.

    int GOAL_OFFSET = 20; // this worth of extra orders are generated.

    float MIN_CUSTOMER_SPAWN_COOLDOWN = 5f;
    float MAX_CUSTOMER_SPAWN_COOLDOWN = 15f;

    int MIN_ORDERS_PER_CUSTOMER = 1;
    int MAX_ORDERS_PER_CUSTOMER = 3;
}
