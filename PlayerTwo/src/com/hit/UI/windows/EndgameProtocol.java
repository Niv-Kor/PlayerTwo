package com.hit.UI.windows;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import com.hit.UI.fixed_panels.FixedPanel;
import com.hit.UI.states.EndgameMessage;
import com.hit.game_launch.Game;
import com.hit.game_launch.Launcher;
import com.hit.game_launch.Launcher.Substate;
import game_algo.IGameAlgo.GameState;

public class EndgameProtocol
{
	/**
	 * A window that pops on top the running game.
	 * This should pop whenever a game reached its end.
	 * PopupWindow should normally be used alongside with 'EndgameMessage' state.
	 * @author Niv Kor
	 */
	private static class PopupWindow extends Window
	{
		private static final long serialVersionUID = -371554534410606853L;
		private static final Dimension DIM = new Dimension(300, 200);
		
		private Game game;
		private GameState gameState;
		
		public PopupWindow(Game game, GameState gameState) {
			super("", DIM);
			setAlwaysOnTop(true);
			
			//pause the game behind this window
			Launcher.getRunningGameController(game).stop(true);
			
			this.game = game;
			this.gameState = gameState;
			
			this.addWindowListener(new WindowListener() {
				@Override
				public void windowClosing(WindowEvent arg0) {
					Launcher.closeGame(game);
					dispose();
				}
				
				@Override
				public void windowIconified(WindowEvent arg0) {}
				
				@Override
				public void windowDeiconified(WindowEvent arg0) {}
				
				@Override
				public void windowDeactivated(WindowEvent arg0) {}
				
				@Override
				public void windowOpened(WindowEvent arg0) {}
				
				@Override
				public void windowClosed(WindowEvent arg0) {}
				
				@Override
				public void windowActivated(WindowEvent arg0) {}
			});
		}
		
		/**
		 * Build the endgame state that's running on this window,
		 * according to the GameState that the constructor received.
		 * This method must be used in order to finish setting the state correctly.
		 * 
		 * @throws Exception when the GameState enum cannot be converted to Situation enum.
		 */
		public void build() throws Exception {
			((EndgameMessage) Launcher.getWindowCache(this).getCurrentState()).build(game, gameState);
		}
		
		@Override
		public Color getColor() { return FixedPanel.COLOR; }
		
		@Override
		public void dispose() {
			EndgameProtocol.windowOpen = null;
			super.dispose();
		}
	}
	
	private static PopupWindow windowOpen;
	
	/**
	 * Pop a message on screen that announces a win or a tie.
	 * @param game - The game that is being played
	 * @param gameState - The state of the game
	 * @return true if the message popped successfully
	 */
	public static boolean pop(Game game, GameState gameState) {
		if (game == null || gameState == null) return false;
		
		//pop a message on screen
		try {
			if (windowOpen == null) {
				PopupWindow popup = new PopupWindow(game, gameState);
				Launcher.setState(popup, Substate.ENDGAME_MESSGE);
				popup.build();
				windowOpen = popup;
			}
			else {
				Launcher.setState(windowOpen, Substate.ENDGAME_MESSGE);
				windowOpen.build();
			}
			
			return true;
		}
		catch (Exception e) {
			System.err.println("Could not pop an endgame message for the game state: '" + gameState + "'");
			e.printStackTrace();
			return false;
		}
	}
}