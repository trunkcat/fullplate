package io.trunkcat.fullplate.utilities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;

import java.util.HashMap;

public class GameFont {
    String fontPath;
    HashMap<Integer, BitmapFont> fonts;

    static final int DEFAULT_SIZE = 12;

    public GameFont(String fontPath) {
        fonts = new HashMap<>();
        this.fontPath = fontPath;
        generate(DEFAULT_SIZE);
    }

    public GameFont(String fontPath, int[] requestSizes) {
        this(fontPath);
        for (int size : requestSizes) {
            generate(size);
        }
        generate(DEFAULT_SIZE);
    }

    public BitmapFont get() {
        return fonts.get(DEFAULT_SIZE);
    }

    public BitmapFont get(int size) {
        return fonts.get(size);
    }

    public BitmapFont getSafe(int size) {
        if (fonts.containsKey(size)) {
            return fonts.get(size);
        }

        return generate(size);
    }

    public BitmapFont generate(int size) {
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal(fontPath));
        FreeTypeFontParameter parameter = new FreeTypeFontParameter();
        parameter.size = size;
        BitmapFont font = generator.generateFont(parameter);
        generator.dispose();
        fonts.put(size, font);
        return font;
    }
}
