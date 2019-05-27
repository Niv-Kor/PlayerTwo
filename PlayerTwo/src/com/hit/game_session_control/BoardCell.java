package com.hit.game_session_control;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.BevelBorder;

import game_algo.GameBoard.GameMove;

public abstract class BoardCell extends JPanel
{
	private static final long serialVersionUID = -2124065331585321737L;
	protected static final Font SIGN_FONT = new Font("Segoe Script", Font.PLAIN, 100);
	protected static final Color BACKGROUND_COLOR = new Color(255, 246, 217);
	protected static final Color SELECTED_BACKGROUND_COLOR = new Color(230, 218, 179);
	protected static final Color PLAYER_1_COLOR = new Color(35, 154, 255);
	protected static final Color PLAYER_2_COLOR = new Color(250, 37, 37);
	
	protected Controller controller;
	protected int row, col;
	protected TurnManager turnManager;
	protected JLabel sign;
	protected boolean enabled;
	
	public BoardCell(int row, int col, TurnManager turnManager, Controller controller) {
		super(new GridBagLayout());
		setBorder(new BevelBorder(BevelBorder.RAISED));
		setBackground(BACKGROUND_COLOR);
		
		this.controller = controller;
		this.row = row;
		this.col = col;
		this.turnManager = turnManager;
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
	public void reset(Controller control) {
		enable(true);
		controller = control;
		erase();
	}
	
	/**
	 * Update a human's move, involving this cube.
	 * @param move - The next move to do (if known beforehand)
	 */
	public abstract void updateHuman(GameMove move);
	
	/**
	 * Update another player's move, from across the server, in the current cube.
	 * @param move - The next move to do (if known beforehand)
	 */
	public abstract void updateOtherPlayer(GameMove move);
	
	
	/**
	 * @return the sign label that's placed over the cube.
	 */
	public JLabel getSignLabel() { return sign; }
}