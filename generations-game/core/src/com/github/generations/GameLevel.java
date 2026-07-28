package com.github.generations;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

public class GameLevel {
    private final int levelNum;
    private final SpeedManager speedManager;
    private GameWorld world;

    private Skin uiSkin;
    private Button runButton;
    private Button backButton;
    private Button reloadButton;
    private InfoSection infoSection;
    private ILevelSelector levelSelector;

    public GameLevel(int levelNum){
        this.levelNum = levelNum;
        this.world = GameWorldParser.parse("level" + levelNum + ".txt");
        this.uiSkin = GameSkin.getSkin();
        this.runButton = createRunButton();
        this.reloadButton = createReloadButton();
        this.backButton = createBackButton();
        this.infoSection = new InfoSection();
        this.infoSection.setGameInfoSupplier(world);
        this.speedManager = new SpeedManager();
        this.world.setSpeedManager(speedManager);
    }

    public void addToStage(Stage stage){
        stage.addActor(runButton);
        stage.addActor(reloadButton);
        stage.addActor(world);
        stage.addActor(infoSection);
        stage.addActor(backButton);
        stage.addActor(speedManager);
        speedManager.addChildrenActors(stage);
    }

    public void setBackCallback(ILevelSelector levelSelector){
        this.levelSelector = levelSelector;
    }

    private Button createRunButton(){
        final TextButton button2 = new TextButton("Start", uiSkin, "default");
        button2.getLabel().setFontScale(2);
        button2.setSize(150, 100);
        button2.setPosition(400, Gdx.graphics.getHeight()-150);
        button2.addListener(new InputListener(){
            @Override
            public void touchUp (InputEvent event, float x, float y, int pointer, int button) {
            }
            @Override
            public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
                world.toggleRunning();
                if(button2.getLabel().getText().toString().equals("Pause")){
                    button2.getLabel().setText("Resume");
                }else{
                    button2.getLabel().setText("Pause");
                }
                return true;
            }
        });
        return button2;
    }

    private Button createBackButton(){
        final Texture backBtnTexture = new Texture(Gdx.files.internal("back.png"));
        final TextureRegion region = new TextureRegion(backBtnTexture);
        final TextureRegionDrawable drwbl = new TextureRegionDrawable(region);
        final ImageButton button2 = new ImageButton(drwbl, drwbl, drwbl);
        button2.setSize(150, 100);
        button2.setPosition(100, Gdx.graphics.getHeight()-150);
        button2.addListener(new InputListener(){
            @Override
            public void touchUp (InputEvent event, float x, float y, int pointer, int button) {

            }

            @Override
            public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
                levelSelector.onLevelQuit();
                return true;
            }
        });
        return button2;
    }

    private Button createReloadButton(){
        final Texture reloadBtn = new Texture(Gdx.files.internal("reload-btn.png"));
        final TextureRegion region = new TextureRegion(reloadBtn);
        final TextureRegionDrawable drwbl = new TextureRegionDrawable(region);
        final ImageButton button2 = new ImageButton(drwbl, drwbl, drwbl);
        button2.setSize(150, 100);
        button2.setPosition(250, Gdx.graphics.getHeight()-150);
        button2.addListener(new InputListener(){
            @Override
            public void touchUp (InputEvent event, float x, float y, int pointer, int button) {

            }

            @Override
            public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
                levelSelector.onLevelSelected(levelNum);
                return true;
            }
        });
        return button2;
    }



    public GameWorld getWorld(){
        return world;
    }

    public int getScore(){
        return world.getScore();
    }

}
