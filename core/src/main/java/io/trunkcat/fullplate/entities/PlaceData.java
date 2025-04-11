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

package io.trunkcat.fullplate.entities;

import com.badlogic.gdx.math.Vector2;

public class PlaceData {
    private String id;
    private PlaceType type;
    private String name;
    private String description;
    private final Integer cost;
    private final int levelCount;
    private final int requiredLevel;
    private boolean locked;
    private Vector2 position;

    public enum PlaceType {
        RESTAURANT,
        EVENT
    }

    public PlaceData(String id,
                     PlaceType type,
                     String name,
                     String description,
                     Integer cost,
                     int requiredLevel,
                     int levelCount,
                     boolean locked,
                     Vector2 position) {
        this.id = id;
        this.type = type;
        this.name = name;
        this.description = description;
        this.cost = cost;
        this.requiredLevel = requiredLevel;
        this.levelCount = levelCount;
        this.locked = locked;
        this.position = position;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public PlaceType getType() {
        return type;
    }

    public void setType(PlaceType type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getCost() {
        return cost;
    }

    public int getRequiredLevel() {
        return requiredLevel;
    }

    public int getLevelCount() {
        return levelCount;
    }

    public boolean isLocked() {
        return locked;
    }

    public void setLocked(boolean locked) {
        this.locked = locked;
    }

    public Vector2 getPosition() {
        return position;
    }

    public void setPosition(Vector2 position) {
        this.position = position;
    }
}
