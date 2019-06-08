package com.hit.UI.states;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.concurrent.Callable;

import com.hit.UI.fixed_panels.GuidePanel.Flow;
import com.hit.UI.windows.Window;
import com.hit.game_launch.Game.GameMode;
import com.hit.game_launch.Launcher;
import com.hit.game_launch.Launcher.Substate;

import javaNK.util.graphics.components.InteractiveIcon;

public class GameModeState extends ConfigState
{
	public GameModeState(Window window) {
		super(window, 1);
		GridBagConstraints constraints = new GridBagConstraints();
		
		//panels modification
		header.addTitleLine("Choose your game mode");
		panel.setLayout(new GridBagLayout());
		
		InteractiveIcon singleplayer = new InteractiveIcon("miscellaneous/singleplayer_mode_off.png");
		singleplayer.setHoverIcon("miscellaneous/singleplayer_mode_on.png");
		singleplayer.setFunction(new Callable<Void>() {
			@Override
			public Void call() throws Exception {
				Launcher.launchGame(GamePickerState.pick, GameMode.SINGLE_PLAYER);
				return null;
			}
		});
		
		constraints.gridx = 0;
		constraints.gridy = 0;
		panel.add(singleplayer, constraints);
		
		InteractiveIcon multiplayer = new InteractiveIcon("miscellaneous/multiplayer_mode_off.png");
		multiplayer.setHoverIcon("miscellaneous/multiplayer_mode_on.png");
		multiplayer.setFunction(new Callable<Void>() {
			@Override
			public Void call() throws Exception {
				Launcher.launchGame(GamePickerState.pick, GameMode.MULTIPLAYER);
				return null;
			}
		});
		
		constraints.gridy = 1;
		constraints.insets.top = 90;
		panel.add(multiplayer, constraints);
		
		//buttons
		guide.enable(Flow.NEXT, false);
		guide.setTarget(Flow.BACK, window, Substate.IDENTIFICATION);
	}
}