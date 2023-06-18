package com.hescha.game;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.physics.box2d.Box2D;

public class GameAngryBird extends Game {
	public static final AssetManager assetManager = new AssetManager();

	@Override
	public void create () {
		Box2D.init();
		setScreen(new LoadingScreen(this));
	}

	public AssetManager getAssetManager() {
		return assetManager;
	}

}
