package com.hit.game_session_control;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagLayout;
import java.io.IOException;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.BevelBorder;

import com.hit.game_launch.Game.GameMode;
import com.hit.players.Participant;

import javaNK.util.debugging.Logger;

public abstract class BoardCell extends JPanel
{
	private static final long serialVersionUID = -2124065331585321737L;
	protected static final Font SIGN_FONT = new Font("Segoe Script", Font.PLAIN, 100);
	protected static final Color BACKGROUND_COLOR = new Color(255, 246, 217);
	protected static final Color SELECTED_BACKGROUND_COLOR = new Color(230, 218, 179);
	protected static final Color PLAYER_1_COLOR = new Color(35, 154, 255);
	protected static final Color PLAYER_2_COLOR = new Color(250, 37, 37);
	
	protected GameController controller;
	protected TurnManager turnManager;
	protected JLabel sign;
	protected int row, col;
	protected boolean enabled;
	
	/**
	 * @param row - The row of the cell
	 * @param col - The column of the cell
	 * @param turnManager - The turn manager of the current game session
	 * @param controller - The Controller object of the game
	 */
	public BoardCell(int row, int col, GameController controller) {
		super(new GridBagLayout());
		setBorder(new BevelBorder(BevelBorder.RAISED));
		setBackground(BACKGROUND_COLOR);
		
		this.controller = controller;
		this.row = row;
		this.col = col;
		this.turnManager = controller.getTurnManager();
		this.enabled = true;
		
		this.sign = new JLabel();
		sign.setFont(SIGN_FONT);
		add(sign);
	}
	
	/**
	 * Enable or disable the cube's functionality.
	 * Once disabled, it does not react to any mouse events.
	 * @param flag - True to enable or false to continue
	 */
	public void enable(boolean flag) {
		enabled = flag;
		setBackground(BACKGROUND_COLOR);
	}
	
	/**
	 * Erase the cube's content.
	 */
	public abstract void erase();
	
	/**
	 * Reset cube.
	 * @param control - Game's controller
	 */
	public void reset(GameController control) {
		enable(true);
		controller = control;
		erase();
	}
	
	/**
	 * Update a human's move, involving this cube.
	 */
	public abstract void updateHuman();
	
	/**
	 * Update another player's move, from across the server, in the current cube.
	 */
	public abstract void updateOtherPlayer();
	
	/**
	 * Place a player's sign on this cube.
	 * 
	 * @param player - The player that the sign belongs to
	 * @param proceedTurn - True to proceed to the next turn after the placement
	 */
	final public void placePlayer(Participant player, boolean proceedTurn) {
		try {
			placement(player);
			if (proceedTurn) proceedTurn();
		}
		catch (Exception e) { Logger.error(e); }
	}
	
	/**
	 * Abstract algorithm for placing a player on the board.
	 * 
	 * @param player - The player that the sign belongs to
	 * @throws Exception when something goes wrong with the placement.
	 */
	protected abstract void placement(Participant player) throws Exception;
	
	/**
	 * Proceed to the next turn.
	 */
	protected void proceedTurn() {
		try {
			turnManager.next();
			controller.enableRandomButton(turnManager.is(Participant.PLAYER_1));
			controller.getCommunicator().tryEndgame();
			
			if (controller.getRelatedGame().getGameMode() == GameMode.SINGLE_PLAYER)
				controller.triggerCompMove();
		}
		catch (IOException e) { Logger.error(e); }
	}
	
	/**
	 * @return the sign label that's placed over the cube.
	 */
	public JLabel getSignLabel() { return sign; }
}