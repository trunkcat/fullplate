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

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.math.Vector2;

public class MapGestureListener extends GestureDetector.GestureAdapter {
    private final OrthographicCamera camera;
    public static float INITIAL_ZOOM = 1f;
    private final Vector2 boundaries;

    MapGestureListener(OrthographicCamera camera, float boundaryX, float boundaryY) {
        this.camera = camera;
        camera.zoom = INITIAL_ZOOM;
        this.boundaries = new Vector2(boundaryX, boundaryY);
    }

    @Override
    public boolean pan(float x, float y, float deltaX, float deltaY) {
        Vector2 delta = new Vector2(-deltaX * camera.zoom, deltaY * camera.zoom);
        Vector2 dest = new Vector2(camera.position.x + delta.x, camera.position.y + delta.y);

        float effectiveViewportWidth = camera.viewportWidth * camera.zoom;
        float effectiveViewportHeight = camera.viewportHeight * camera.zoom;

        if (dest.x < effectiveViewportWidth / 2f || dest.x > boundaries.x - effectiveViewportWidth / 2) {
            delta.x = 0;
        }
        if (dest.y < effectiveViewportHeight / 2f || dest.y > boundaries.y - effectiveViewportHeight / 2) {
            delta.y = 0;
        }
        camera.translate(delta);
        return true;
    }

    // TODO: implement and finish zoom
    @Override
    public boolean zoom(float initialDistance, float distance) {
//        Vector2 pointA = new Vector2(Gdx.input.getX(0), Gdx.input.getY(0));
//        Vector2 pointB = new Vector2(Gdx.input.getX(1), Gdx.input.getY(1));
//        Vector2 zoomCenter = new Vector2((pointA.x + pointB.x) / 2f, (pointA.y + pointB.y) / 2f);
//
//        float zoomFactor = camera.zoom * (initialDistance / distance);
//        zoomFactor = Math.max(0.5f, Math.min(Math.min(boundaries.x / camera.viewportWidth, boundaries.y / camera.viewportHeight), zoomFactor));
//        Gdx.app.log("Camera", zoomFactor + " " + camera.viewportWidth / boundaries.x);
//        camera.zoom = zoomFactor;
//
////        float targetZoom = camera.zoom * (initialDistance / distance);
////        float maxZoom = boundaries.x / camera.viewportWidth * targetZoom;
////        targetZoom = Math.max(0.5f, Math.min(maxZoom, targetZoom));
////        camera.zoom = MathUtils.lerp(camera.zoom, targetZoom, 0.05f);
////        camera.zoom = Math.max(0.5f, Math.min(maxZoom, camera.zoom));
        return true;
    }
}
