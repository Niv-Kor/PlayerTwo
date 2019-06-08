package com.hit.UI.windows;
import java.awt.Color;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import com.hit.game_launch.Game;
import com.hit.game_launch.Launcher;

public class GameWindow extends Window
{
	private static final long serialVersionUID = -1657921267649562810L;
	
	private Game game;
	
	public GameWindow(Game game) {
		super(game.formalName(), game.getWindowDimension());
		addWindowListener(new WindowListener() {
			public void windowClosing(WindowEvent arg0) { Launcher.closeGame(game); }
			public void windowActivated(WindowEvent arg0) {}
			public void windowClosed(WindowEvent arg0) {}
			public void windowDeactivated(WindowEvent arg0) {}
			public void windowDeiconified(WindowEvent arg0) {}
			public void windowIconified(WindowEvent arg0) {}
			public void windowOpened(WindowEvent arg0) {}
		});
		
		this.game = game;
	}
	
	/**
	 * @return the game in this window.
	 */
	public Game getGame() { return game; }
	
	@Override
	public Color getColor() { return Window.COLOR; }
}