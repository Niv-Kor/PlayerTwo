package com.hit.UI.states;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.List;
import java.util.concurrent.Callable;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextPane;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

import com.hit.UI.fixed_panels.GuidePanel.Flow;
import com.hit.UI.windows.Window;
import com.hit.game_launch.Game;
import com.hit.game_launch.Launcher.Substate;

import javaNK.util.files.FontHandler;
import javaNK.util.files.ImageHandler;
import javaNK.util.files.FontHandler.FontStyle;
import javaNK.util.graphics.InteractiveIcon;
import javaNK.util.math.Percentage;

public class GamePickerState extends ConfigState
{
	/**
	 * An object that containts the image, label and rules of a game.
	 * There must be a "picker.png" and "rules.txt" files in resources folder,
	 * otherwise this object will throw an exception at runtime.
	 * @author Niv Kor
	 */
	private static class PickerHolder
	{
		private JLabel label, icon;
		private GameRules rules;
		
		public PickerHolder() {
			this.label = new JLabel();
			this.icon = new JLabel();
			this.rules = new GameRules();
			rules.setForeground(Color.WHITE);
		}
		
		/**
		 * Set a game and change the label, icon and rules accordingly.
		 * @param game - The game to set
		 */
		public void setGame(Game game) {
			label.setText(game.formalName());
			icon.setIcon(loadIcon(game));
			rules.setGame(game);
		}
		
		private static ImageIcon loadIcon(Game game) {
			return ImageHandler.loadIcon("games/" + game.formalName() + "/picker.png");
		}
		
		/**
		 * @return label with the game's name.
		 */
		public JLabel getLabel() { return label; }
		
		/**
		 * @return the game's icon.
		 */
		public JLabel getIcon() { return icon; }
		
		/**
		 * @return the game's rules.
		 */
		public GameRules getRules() { return rules; }
	}
	
	/**
	 * A JTextPane object that contains the rules to a game.
	 * There must be a "rules.txt" file in resources folder,
	 * otherwise this object will throw an exception at runtime.
	 * @author Niv Kor
	 */
	private static class GameRules extends JTextPane
	{
		private static final long serialVersionUID = 2249043666968656594L;
		private static final Font FONT = FontHandler.load("Comfortaa", FontStyle.PLAIN, 12.4);
		
		private List<String> rules;
		
		public GameRules() {
			setOpaque(false);
			setEditable(false);
			setFont(FONT);

			StyledDocument doc = getStyledDocument();
			SimpleAttributeSet center = new SimpleAttributeSet();
			StyleConstants.setAlignment(center, StyleConstants.ALIGN_JUSTIFIED);
			doc.setParagraphAttributes(0, doc.getLength(), center, false);
		}
		
		/**
		 * @param game - The game to load the rules of
		 */
		public void setGame(Game game) {
			String oneLine = "";
			rules = game.getGameRules();
			String[] lines = (String[]) rules.toArray(new String[rules.size()]);
			
			for (String line : lines) {
				oneLine = oneLine.concat(line + "\n");
			}
			
			setText(oneLine);
		}
	}
	
	public static Game pick = Game.values()[0];
	private PickerHolder pickerHolder;
	private int gameIndex;
	
	public GamePickerState(Window window) {
		super(window, 1);
		
		GridBagConstraints constraints = new GridBagConstraints();
		//index in Games.values(), start with the first one
		this.gameIndex = 0;
		
		//headline modification
		header.addTitleLine("Pick a game");
		
		JPanel pickerPane = new JPanel(new GridBagLayout());
		pickerPane.setPreferredSize(Percentage.createDimension(window.getDimension(), 100, 70));
		pickerPane.setOpaque(false);
		
		//game picker holder
		this.pickerHolder = new PickerHolder();
		pickerHolder.setGame(pick);
		
		//game name
		pickerHolder.getLabel().setForeground(Color.WHITE);
		pickerHolder.getLabel().setFont(LABEL_FONT);
		constraints.gridx = 1;
		constraints.gridy = 0;
		pickerPane.add(pickerHolder.getLabel(), constraints);
		
		//browse to the left
		InteractiveIcon leftArrow = new InteractiveIcon("miscellaneous/left_arrow.png");
		leftArrow.setHoverIcon("miscellaneous/left_arrow_hover.png");
		leftArrow.setFunction(new Callable<Void>() {
			@Override
			public Void call() throws Exception {
				//decrement game index
				if (gameIndex > 0) {
					pick = Game.values()[--gameIndex];
					pickerHolder.setGame(pick);
				}
				
				return null;
			}
		});
		
		constraints.insets = new Insets(10, 10, 10, 10);
		constraints.gridx = 0;
		constraints.gridy = 1;
		pickerPane.add(leftArrow, constraints);
		
		//game icon
		constraints.gridx = 1;
		pickerPane.add(pickerHolder.getIcon(), constraints);
		
		//browse to the right
		InteractiveIcon rightArrow = new InteractiveIcon("miscellaneous/right_arrow.png");
		rightArrow.setHoverIcon("miscellaneous/right_arrow_hover.png");
		rightArrow.setFunction(new Callable<Void>() {
			@Override
			public Void call() throws Exception {
				//increment game index
				if (Game.values().length > gameIndex + 1) {
					pick = Game.values()[++gameIndex];
					pickerHolder.setGame(pick);
				}
				
				return null;
			}
		});
		
		constraints.gridx = 2;
		pickerPane.add(rightArrow, constraints);
		
		//game rules
		pickerHolder.getRules().setPreferredSize(Percentage.createDimension(window.getDimension(), 60, 30));
		
		constraints.gridx = 1;
		constraints.gridy = 2;
		pickerPane.add(pickerHolder.getRules(), constraints);
		
		panel.add(pickerPane, BorderLayout.CENTER);
		
		//disable back button because this should be the first page we see
		guide.enable(Flow.BACK, false);
		//"next" button should target the identification state
		guide.setTarget(Flow.NEXT, window, Substate.IDENTIFICATION);
	}
}