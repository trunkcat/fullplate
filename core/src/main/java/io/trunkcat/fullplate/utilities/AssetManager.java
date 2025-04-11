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

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.NinePatch;

import java.util.Arrays;
import java.util.HashMap;
import java.util.stream.Collectors;

public class AssetManager {
    private static final HashMap<String, Texture> textures = new HashMap<>();
    private static final HashMap<String, Texture> colorTextures = new HashMap<>();
    private static final HashMap<String, NinePatch> ninePatches = new HashMap<>();

    public static Texture loadTexture(String path) {
        if (!textures.containsKey(path)) {
            Texture loadedTexture = new Texture(Gdx.files.internal(path));
            textures.put(path, loadedTexture);
            return loadedTexture;
        }
        return textures.get(path);
    }

    public static Texture colorTexture(Color color) {
        String key = color.toString();
        if (!colorTextures.containsKey(key)) {
            Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
            pixmap.setColor(color);
            pixmap.fill();
            colorTextures.put(key, new Texture(pixmap));
            pixmap.dispose();
        }
        return colorTextures.get(key);
    }

    public static NinePatch ninePatchFromTexture(String path, int[] splits) {
        String key = path + "-" + Arrays.stream(splits)
            .mapToObj(String::valueOf)
            .collect(Collectors.joining("x"));
        if (!ninePatches.containsKey(key)) {
            Texture texture = loadTexture(path);
            NinePatch ninePatch = new NinePatch(texture, splits[0], splits[1], splits[2], splits[3]);
            ninePatches.put(key, ninePatch);
            return ninePatch;
        }
        return ninePatches.get(key);
    }
}
