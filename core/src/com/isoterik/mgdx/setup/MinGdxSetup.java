package com.isoterik.mgdx.setup;

import com.isoterik.mgdx.MinGdxGame;
import com.isoterik.mgdx.Scene;
import com.isoterik.mgdx.m2d.scenes.transition.SceneTransitions;
import com.isoterik.mgdx.setup.scenes.MainScene;

public class MinGdxSetup extends MinGdxGame 
{
	@Override
	protected Scene initGame()
	{
		minGdx.defaultSettings.VIEWPORT_WIDTH = Constants.GUI_WIDTH;
		minGdx.defaultSettings.VIEWPORT_HEIGHT = Constants.GUI_HEIGHT;
		
		minGdx.assets.enqueueSkin(Constants.SKIN_PATH);
		minGdx.assets.loadAssetsNow();

		splashTransition = SceneTransitions.fade(1f);
		return new MainScene();
	}
}