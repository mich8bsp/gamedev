package com.github.generations;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;

import java.util.LinkedList;
import java.util.List;

public class LevelSelector {

    private Skin uiSkin = GameSkin.getSkin();
    private List<Button> levelButtons = new LinkedList<>();
    private Label title;

    private static final String TITLE = "Generations";

    public LevelSelector(ILevelSelector callback, int numOfLevels){
        for(int i=0; i<numOfLevels; i++){
            levelButtons.add(createLevelButton(i+1, callback));
        }
        this.title = createTitle();
    }

    private Label createTitle() {
        Label titleLabel = new Label(TITLE, new Label.LabelStyle(GameSkin.getFont(150), Color.TEAL));
        titleLabel.setPosition(Gdx.graphics.getWidth()/2f-600, 800);
        titleLabel.setWidth(500);
        titleLabel.setHeight(100);
        titleLabel.setWrap(false);
        return titleLabel;
    }

    private Button createLevelButton(final int i, final ILevelSelector callback){
        final TextButton button2 = new TextButton("Level " + i, uiSkin, "default");
        button2.getLabel().setFontScale(2);
        button2.setSize(150, 100);
        button2.setPosition(Gdx.graphics.getWidth()/2f-150, 800 - i*200);
        button2.addListener(new InputListener(){
            @Override
            public void touchUp (InputEvent event, float x, float y, int pointer, int button) {
            }
            @Override
            public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
                callback.onLevelSelected(i);
                return true;
            }
        });
        return button2;
    }

    public void addToStage(Stage mainStage) {
        for(Button b : levelButtons){
            mainStage.addActor(b);
        }
        mainStage.addActor(title);
    }
}
