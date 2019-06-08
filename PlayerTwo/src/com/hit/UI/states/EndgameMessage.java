package com.hit.UI.states;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.concurrent.Callable;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.hit.UI.windows.Window;
import com.hit.game_launch.Game;
import com.hit.game_launch.Launcher;

import game_algo.IGameAlgo.GameState;
import javaNK.util.files.ImageHandler;
import javaNK.util.graphics.components.InteractiveLabel;
import javaNK.util.math.Percentage;

public class EndgameMessage extends State
{
	private static enum Situation {
		WIN("You win!", Icon.WIN),
		LOSE("You lose.", Icon.LOSE),
		TIE("It's a tie.", Icon.TIE),
		OPPONENT_ISSUES("Your opponent disconnected.", Icon.ERROR),
		UNEXPECTED_ISSUE("The server is down.", Icon.ERROR);
		
		static enum Icon { WIN, LOSE, TIE, ERROR; }
		
		private String message;
		private Icon iconType;
		private ImageIcon icon;
		
		private Situation(String message, Icon icon) {
			this.iconType = icon;
			this.message = new String(message);
			this.icon = ImageHandler.loadIcon("miscellaneous/" + icon.name() + "_ICO.png");
		}
		
		/**
		 * @return the message of the game's ending
		 */
		public String getMessage() { return message; }
		
		/**
		 * @return the icon to show with the message.
		 */
		public ImageIcon getIcon() { return icon; }
		
		/**
		 * @return the type of the message icon.
		 */
		public Icon getIconType() { return iconType; }
		
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
				case PARTNER_DISCONNECTED: return OPPONENT_ISSUES;
				case IN_PROGRESS: return UNEXPECTED_ISSUE;
				default: throw new Exception(); //should not occur
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
	
	public void build(Game game, GameState gameState) throws Exception {
		//remove everything from last time this state showed
		for (JPanel pane : panes) pane.removeAll();
		
		//add everything once again
		GridBagConstraints constraints = new GridBagConstraints();
		
		//create components
		Situation situation = Situation.convert(gameState);
		
		//big emoji icon
		JLabel emoji = new JLabel(situation.getIcon());
		constraints.gridx = 0;
		constraints.gridy = 0;
		constraints.insets.bottom = 10;
		panes[0].add(emoji, constraints);
		
		//message (Ex. "You win!")
		JLabel label = new JLabel(situation.getMessage());
		label.setForeground(Color.WHITE);
		label.setFont(State.LABEL_FONT);
		constraints.gridy = 1;
		panes[0].add(label, constraints);
		
		//button that enables the user to restart the game
		if (situation.getIconType() != Situation.Icon.ERROR) {
			InteractiveLabel playAgain = new InteractiveLabel("Play Again");
			playAgain.enableSelectionColor(false);
			playAgain.setForeground(Color.WHITE);
			playAgain.setFont(State.LABEL_FONT);
			playAgain.setFunction(new Callable<Void>() {
				@Override
				public Void call() throws Exception {
					window.dispose();
					Launcher.restartGame(game);
					return null;
				}
			});
			
			constraints.gridy = 0;
			constraints.insets.right = 50;
			constraints.insets.left = -10;
			panes[1].add(playAgain, constraints);
		}
		
		//button that exits the current game that's open
		InteractiveLabel quit = new InteractiveLabel("Quit");
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