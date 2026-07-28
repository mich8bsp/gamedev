package com.github.generations;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

public class GameSkin {
    private static Skin uiSkin = new Skin(Gdx.files.internal("clean-crispy-ui.json"));

    public static BitmapFont getFont(int size){
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("celticmd.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = size;
        BitmapFont font12 = generator.generateFont(parameter);
        generator.dispose();
        return font12;
    }

    public static Skin getSkin(){
        return uiSkin;
    }
}
