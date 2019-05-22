package com.hit.client_side.UI;
import java.awt.Color;
import java.awt.Dimension;

import com.hit.client_side.UI.fixed_panels.FixedPanel;
import com.hit.client_side.UI.launcher.ClientSideGame;
import com.hit.client_side.UI.launcher.Launcher;
import com.hit.client_side.UI.launcher.Launcher.Substate;
import com.hit.client_side.UI.states.EndgameMessage;
import com.hit.server_side.game_algo.IGameAlgo.GameState;

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
		
		private ClientSideGame game;
		private GameState gameState;
		
		public PopupWindow(ClientSideGame game, GameState gameState) {
			super("", DIM);
			setResizable(false);
			setLocationRelativeTo(null);
			setAlwaysOnTop(true);
			pack();
			
			//pause the game behind this window
			Launcher.getRunningGameController(game).stop(true);
			
			this.game = game;
			this.gameState = gameState;
		}
		
		/**
		 * Build the endgame state that's running on this window,
		 * according to the gameState that the constructor received.
		 * This method must be used in order to finish setting the state
		 * correctly.
		 * @throws Exception when the GameState enum cannot be converted to Situation enum.
		 */
		public void build() throws Exception {
			((EndgameMessage) Launcher.getWindowCache(this).getCurrentState()).build(game, gameState);
		}
		
		@Override
		public Color getColor() { return FixedPanel.COLOR; }
	}
	
	/**
	 * Pop a message on screen that announces a win or a tie.
	 * @param game - The game that is being played
	 * @param gameState - The state of the game
	 * @return true if the message popped successfuly
	 */
	public static boolean pop(ClientSideGame game, GameState gameState) {
		//pop a message only if the game ended
		if (gameState != GameState.IN_PROGRESS) {
			try {
				//pop a message on screen
				PopupWindow popup = new PopupWindow(game, gameState);
				Launcher.setState(popup, Substate.ENDGAME_MESSGE);
				popup.build();
				return true;
			}
			//this part should not happen
			catch (Exception e) {
				System.err.println("Could not pop an endgame message for the game state: '" + gameState + "'");
				e.printStackTrace();
				return false;
			}
		}
		else return false;
	}
}