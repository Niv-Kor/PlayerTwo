package com.hit.UI;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.hit.players.Participant;

import javaNK.util.files.ImageHandler;
import javaNK.util.math.Percentage;
import javaNK.util.threads.DiligentThread;

public class VSPanel extends JPanel
{
	/**
	 * Used as a holder for each player's fields.
	 * 
	 * @author Niv Kor
	 */
	private static class PlayerProperty
	{
		private Participant player;
		private TurnArrow turnArrow;
		private ThinkingDots thinkingDots;
		private boolean active;
		
		public PlayerProperty(Participant player, TurnArrow turnArrow, ThinkingDots thinkingDots) {
			this.player = player;
			this.turnArrow = turnArrow;
			this.thinkingDots = thinkingDots;
		}
		
		/**
		 * Turn all the player's components (turn arrow and dots) on or off.
		 * 
		 * @param flag - True to activate or false to deactivate
		 */
		public void activate(boolean flag) {
			turnArrow.activate(flag);
			thinkingDots.activate(flag);
			active = flag;
		}
		
		/**
		 * @return this property's player.
		 */
		public Participant getPlayer() { return player; }
		
		/**
		 * @return true if the player's components are active or false otherwise.
		 */
		public boolean isActive() { return active; }
		
		public void close() {
			activate(false);
			thinkingDots.close();
		}
	}
	
	/**
	 * The dots that appear above the avatar whenever it's that player's turn.
	 * Used to stimulate a thinking progress.
	 * 
	 * @author Niv Kor
	 */
	private static class ThinkingDots extends JLabel
	{
		private static class DotAnimation extends DiligentThread
		{
			private static final double REPEAT_PULSE = 0.5;
			
			private ThinkingDots thinkingDots;
			private int currentDot;
			
			public DotAnimation(ThinkingDots thinkingDots) {
				super(REPEAT_PULSE);
				this.thinkingDots = thinkingDots;
				this.currentDot = 0;
			}

			@Override
			protected void diligentFunction() throws Exception {
				if (++currentDot >= DOTS_AMOUNT + 1) currentDot = 1;
				thinkingDots.setIcon(thinkingDots.dots[currentDot]);
			}
		}
		
		private static final long serialVersionUID = -1092244521217893757L;
		private static final String FILE_NAME = "miscellaneous/thinking_dot_";
		private static final int DOTS_AMOUNT = 3;
		
		private ImageIcon[] dots;
		private DotAnimation animation;
		
		public ThinkingDots() {
			this.dots = new ImageIcon[DOTS_AMOUNT + 1];
			for (int i = 0; i < DOTS_AMOUNT + 1; i++)
				dots[i] = ImageHandler.loadIcon(FILE_NAME + i + ".png");
			
			this.animation = new DotAnimation(this);
			animation.start();
		}
		
		/**
		 * Turn the dots on (they start to animate) or off (disappear).
		 * 
		 * @param flag - True to activate or false to deactivate
		 */
		public void activate(boolean flag) {
			animation.pause(!flag);
			if (!flag) setIcon(dots[0]);
		}
		
		public void close() { animation.kill(); }
	}
	
	/**
	 * The arrow next to the "VS" picture.
	 * Used to point at the player that has the current turn.
	 * 
	 * @author Niv Kor
	 */
	private static class TurnArrow extends JLabel
	{
		private static final long serialVersionUID = 7732709067178463189L;
		
		private ImageIcon offIcon, onIcon;
		
		public TurnArrow(ImageIcon offIcon, ImageIcon onIcon) {
			this.offIcon = offIcon;
			this.onIcon = onIcon;
			
			activate(false);
		}
		
		/**
		 * Turn the arrow on (changes icon/color) or off (default icon).
		 * 
		 * @param flag - True to activate or false to deactivate
		 */
		public void activate(boolean flag) {
			if (flag) setIcon(onIcon);
			else setIcon(offIcon);
		}
	}
	
	private static final long serialVersionUID = 7537359943742140967L;
	
	private PlayerProperty[] playersProperty;
	private Participant nextPlayerBackup;
	private boolean stopped;
	
	public VSPanel(Participant player1, Participant player2, JPanel container) {
		super(new BorderLayout());
		setOpaque(false);
		setPreferredSize(container.getPreferredSize());
		GridBagConstraints constraints = new GridBagConstraints();
		
		this.stopped = false;
		
		//initiate panes
		Dimension playerPaneDim = Percentage.createDimension(getPreferredSize(), 25, 85);
		Dimension centerPaneDim = Percentage.createDimension(getPreferredSize(), 50, 85);
		
		//north pane for dots above avatars when it's their turn
		JPanel northPane = new JPanel(new GridBagLayout());
		northPane.setPreferredSize(Percentage.createDimension(getPreferredSize(), 100, 15));
		northPane.setOpaque(false);
		
		//west pane for player 1 gauge
		JPanel westPane = new JPanel(new GridBagLayout());
		westPane.setPreferredSize(playerPaneDim);
		westPane.setOpaque(false);
		
		//center pane for everything else
		JPanel centerPane = new JPanel(new GridBagLayout());
		centerPane.setPreferredSize(centerPaneDim);
		centerPane.setOpaque(false);
		
		//east pane for player 2 gauge
		JPanel eastPane = new JPanel(new GridBagLayout());
		eastPane.setPreferredSize(playerPaneDim);
		eastPane.setOpaque(false);
		
		//upper panel setting
		ThinkingDots[] thinkingDots = new ThinkingDots[2];
		thinkingDots[0] = new ThinkingDots();
		constraints.gridx = 0;
		constraints.gridy = 0;
		constraints.insets.top = 10;
		constraints.insets.left = -353;
		northPane.add(thinkingDots[0], constraints);
		
		thinkingDots[1] = new ThinkingDots();
		constraints.gridx = 1;
		constraints.insets.left = 0;
		constraints.insets.right = -353;
		northPane.add(thinkingDots[1], constraints);
		
		//lower panel setting
		TurnArrow[] turnArrows = new TurnArrow[2];
		
		JPanel player1Gauge = player1.getStatus().getPlayerGauge(playerPaneDim);
		constraints.gridx = 0;
		constraints.gridy = 0;
		constraints.insets.top = -8;
		constraints.insets.left = 20;
		constraints.insets.right = 0;
		westPane.add(player1Gauge, constraints);
		
		ImageIcon player1Off = ImageHandler.loadIcon("miscellaneous/player_turn_off.png");
		ImageIcon player1On = ImageHandler.loadIcon("miscellaneous/player_turn_on.png");
		turnArrows[0] = new TurnArrow(player1Off, player1On);
		constraints.gridx = 0;
		constraints.insets.top = 0;
		constraints.insets.left = 0;
		centerPane.add(turnArrows[0], constraints);
		
		JLabel vsLab = new JLabel(ImageHandler.loadIcon("miscellaneous/vs.png"));
		constraints.gridx = 1;
		centerPane.add(vsLab, constraints);
		
		ImageIcon player2Off = ImageHandler.loadIcon("miscellaneous/rival_turn_off.png");
		ImageIcon player2On = ImageHandler.loadIcon("miscellaneous/rival_turn_on.png");
		turnArrows[1] = new TurnArrow(player2Off, player2On);
		constraints.gridx = 2;
		centerPane.add(turnArrows[1], constraints);
		
		JPanel player2Gauge = player2.getStatus().getPlayerGauge(playerPaneDim);
		constraints.gridx = 0;
		constraints.insets.top = -8;
		constraints.insets.right = 20;
		eastPane.add(player2Gauge, constraints);
		
		add(northPane, BorderLayout.NORTH);
		add(westPane, BorderLayout.WEST);
		add(centerPane, BorderLayout.CENTER);
		add(eastPane, BorderLayout.EAST);
		
		this.playersProperty = new PlayerProperty[2];
		playersProperty[0] = new PlayerProperty(player1, turnArrows[0], thinkingDots[0]);
		playersProperty[1] = new PlayerProperty(player2, turnArrows[1], thinkingDots[1]);
	}
	
	/**
	 * Activate all components of a player, and deactivate all other players.
	 * 
	 * @param player - The player that gets the next turn
	 */
	public void setPlayerTurn(Participant player) {
		for (PlayerProperty property : playersProperty)
			property.activate(property.getPlayer() == player);
	}
	
	/**
	 * Turn off all players or turn them on again.
	 * When turning back on after a suspension,
	 * the player that was suppose to be next in line gets his turn. 
	 *  
	 * @param flag - True to stop or false to continue
	 */
	public void stop(boolean flag) {
		if (flag && !stopped) {
			for (PlayerProperty property : playersProperty) {
				//deactivate all players
				property.activate(false);
				
				//save the next player in case we'll need to continue later
				if (!property.isActive()) nextPlayerBackup = property.getPlayer();
			}
		}
		else if (!flag && stopped) {
			//give the next turn to the player we saved earlier
			setPlayerTurn(nextPlayerBackup);
		}
		
		stopped = flag;
	}
	
	public void close() {
		for (PlayerProperty property : playersProperty)
			property.close();
	}
	
	@Override
	protected void paintComponent(Graphics g) {
		int width = getPreferredSize().width;
		int height = getPreferredSize().height;
		
		g.setColor(Color.GRAY.brighter());
		g.drawLine(0,  height - 2, width, height - 2);
	}
}