package com.github.generations;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

public class SpeedManager extends Actor implements ISpeedManager {

    private final Button speedDownButton;
    private final Button speedUpButton;
    private double speed = 1;
    private static final double MAX_SPEED = 1000;
    private static final double MIN_SPEED = 0.001;

    private Label speedLabel;

    @Override
    public double getSpeed() {
        return speed;
    }

    public SpeedManager(){
        speedLabel= new Label("x" + speed, new Label.LabelStyle(GameSkin.getFont(40), Color.TEAL));
        speedLabel.setPosition(Gdx.graphics.getWidth()-870, Gdx.graphics.getHeight()-150);
        speedLabel.setWidth(500);
        speedLabel.setHeight(100);
        speedLabel.setWrap(false);

        this.speedUpButton = createSpeedButton(true);
        this.speedDownButton = createSpeedButton(false);
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);
        speedLabel.setText("x" + speed);
        speedLabel.draw(batch, parentAlpha);
    }

    private Button createSpeedButton(boolean isUp){
        String icon = (isUp) ? "speed-up-btn.png" : "speed-down-btn.png";
        final Texture btnTexture = new Texture(Gdx.files.internal(icon));
        final TextureRegion region = new TextureRegion(btnTexture);
        final TextureRegionDrawable drwbl = new TextureRegionDrawable(region);
        final ImageButton button2 = new ImageButton(drwbl, drwbl, drwbl);
        button2.setSize(150, 100);
        int heightOffset = (isUp) ? 50 : -50;
        final double speedMultFactor = (isUp) ? 2 : 0.5;
        button2.setPosition(Gdx.graphics.getWidth()-1000, Gdx.graphics.getHeight()-150 + heightOffset);
        button2.addListener(new InputListener(){
            @Override
            public void touchUp (InputEvent event, float x, float y, int pointer, int button) {

            }

            @Override
            public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
                setSpeed(getSpeed()*speedMultFactor);
                return true;
            }
        });
        return button2;
    }

    void setSpeed(double speed){
        if(speed>=MIN_SPEED && speed<=MAX_SPEED){
            this.speed = speed;
        }
    }

    public void addChildrenActors(Stage stage) {
        stage.addActor(speedDownButton);
        stage.addActor(speedUpButton);
    }
}
