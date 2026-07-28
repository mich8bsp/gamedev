package com.github.generations;

import com.badlogic.gdx.Gdx;


public class GenerationsGameMain extends GameBeta implements ILevelSelector {
	GameLevel[] levels = new GameLevel[2];

	private static final boolean DEBUG_MODE = true;
	LevelSelector levelSelector;

	private FrameRate fps;

	@Override
	public void initialize() {
		Gdx.input.setInputProcessor(mainStage);
		if(DEBUG_MODE) {
			fps = new FrameRate();
		}
		this.levelSelector = new LevelSelector(this, levels.length);
		this.levelSelector.addToStage(mainStage);
	}

	@Override
	public void render() {
		super.render();
		if(fps!=null){
			fps.render();
		}
	}

	@Override
	public void update(float dt) {
		if(fps!=null){
			fps.update();
		}
	}


	@Override
	public void onLevelSelected(int levelNum) {
		clear();
		levels[levelNum-1] = new GameLevel(levelNum);
		levels[levelNum-1].setBackCallback(this);
		levels[levelNum-1].addToStage(mainStage);
	}

	@Override
	public void onLevelQuit() {
		clear();
		this.levelSelector.addToStage(mainStage);
	}
}
