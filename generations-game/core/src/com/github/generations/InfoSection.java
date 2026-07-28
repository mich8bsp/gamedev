package com.github.generations;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Label;

public class InfoSection extends Actor {
    private IGameInfoSupplier infoSupplier;

    private String scoreText = "Score: 0";
    private Label scoreLabel;

    private String generationText = "Generation: 0";
    private Label generationLabel;


    public InfoSection(){
        scoreLabel = new Label(scoreText, new Label.LabelStyle(GameSkin.getFont(60), Color.TEAL));
        scoreLabel.setPosition(Gdx.graphics.getWidth()-500, Gdx.graphics.getHeight()-150);
        scoreLabel.setWidth(500);
        scoreLabel.setHeight(100);
        scoreLabel.setWrap(false);

        generationLabel = new Label(generationText, new Label.LabelStyle(GameSkin.getFont(30), Color.TEAL));
        generationLabel.setPosition(Gdx.graphics.getWidth()-500, Gdx.graphics.getHeight()-250);
        generationLabel.setWidth(500);
        generationLabel.setHeight(100);
        generationLabel.setWrap(false);
    }

    public void setGameInfoSupplier(IGameInfoSupplier scoreSupplier) {
        this.infoSupplier = scoreSupplier;
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);
        scoreLabel.setText(scoreText);
        scoreLabel.draw(batch, parentAlpha);

        generationLabel.setText(generationText);
        generationLabel.draw(batch, parentAlpha);
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        this.scoreText = "Score: " + infoSupplier.getScore();
        this.generationText = "Generation: " + infoSupplier.getGeneration() + "/" + GameWorld.LAST_GENERATION;
    }
}
