package com.hit.client_side.UI.states;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.concurrent.Callable;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.hit.client_side.UI.Window;
import com.hit.client_side.UI.launcher.ClientSideGame;
import com.hit.client_side.UI.launcher.Launcher;
import com.hit.server_side.game_algo.IGameAlgo.GameState;
import com.hit.utility.OptionLabel;
import com.hit.utility.math.Percentage;

import files.ImageHandler;

public class EndgameMessage extends State
{
	private static enum Situation {
		WIN("You win!"),
		LOSE("You lose."),
		TIE("It's a tie.");
		
		public String message;
		public ImageIcon emoji;
		
		private Situation(String message) {
			this.message = new String(message);
			this.emoji = ImageHandler.loadIcon("miscellaneous/" + name().toLowerCase() + ".png");
		}
		
		/**
		 * Convert a GameState enum constant to its compatible Situation enum constant.
		 * @param gameState - The GameState enum to convert
		 * @return a compatible Situation constant
		 * @throws Exception when the GameState constant cannot be converted.
		 */
		public static Situation convert(GameState gameState) throws Exception {
			switch(gameState) {
				case PLAYER_WON: return WIN;
				case PLAYER_LOST: return LOSE;
				case TIE: return TIE;
				default: throw new Exception(); //should not occure
			}
		}
	}
	
	public EndgameMessage(Window window) {
		super(window, 2);
		
		//create panels
		//emoji and message panel (panes[0])
		createPanel(new GridBagLayout(), Percentage.createDimension(window.getDimension(), 100, 70), null);
		//options panel (panes[1])
		createPanel(new GridBagLayout(), Percentage.createDimension(window.getDimension(), 100, 20), null);
	}
	
	public void build(ClientSideGame game, GameState gameState) throws Exception {
		//remove everything from last time this state showed
		for (JPanel pane : panes) pane.removeAll();
		
		//add everything once again
		GridBagConstraints constraints = new GridBagConstraints();
		
		//create components
		Situation situation = Situation.convert(gameState);
		
		//big emoji icon
		JLabel emoji = new JLabel(situation.emoji);
		constraints.gridx = 0;
		constraints.gridy = 0;
		constraints.insets.bottom = 10;
		panes[0].add(emoji, constraints);
		
		//message (Ex. "You win!")
		JLabel label = new JLabel(situation.message);
		label.setForeground(Color.WHITE);
		label.setFont(State.LABEL_FONT);
		constraints.gridy = 1;
		panes[0].add(label, constraints);
		
		//button that enables the user to restart the game
		OptionLabel playAgain = new OptionLabel("Play Again");
		playAgain.enableSelectionColor(false);
		playAgain.setForeground(Color.WHITE);
		playAgain.setFont(State.LABEL_FONT);
		playAgain.setFunction(new Callable<Void>() {
			@Override
			public Void call() throws Exception {
				window.dispose();
				Launcher.getRunningGameController(game).restart();
				return null;
			}
		});
		
		constraints.gridy = 0;
		constraints.insets.right = 50;
		constraints.insets.left = -10;
		panes[1].add(playAgain, constraints);
		
		//button that exits the current game that's open
		OptionLabel quit = new OptionLabel("Quit");
		quit.enableSelectionColor(false);
		quit.setForeground(Color.WHITE);
		quit.setFont(State.LABEL_FONT);
		quit.setFunction(new Callable<Void>() {
			@Override
			public Void call() throws Exception {
				window.dispose();
				Launcher.closeGame(game);
				return null;
			}
		});
		
		constraints.gridx = 1;
		constraints.insets.right = 0;
		panes[1].add(quit, constraints);
		
		//show everything in window
		window.setVisible(true);
	}
	
	@Override
	public void insertPanels() {
		window.insertPanel(panes[0], BorderLayout.CENTER);
		window.insertPanel(panes[1], BorderLayout.SOUTH);
	}
}