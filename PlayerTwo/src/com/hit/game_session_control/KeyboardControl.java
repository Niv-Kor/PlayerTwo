package com.hit.game_session_control;
import java.awt.Dimension;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import com.hit.players.Participant;
import game_algo.GameBoard.GameMove;

public class KeyboardControl implements KeyListener
{
	public static enum Keys {
		UP(KeyEvent.VK_W),
		DOWN(KeyEvent.VK_S),
		LEFT(KeyEvent.VK_A),
		RIGHT(KeyEvent.VK_D);
		
		private int keyCode;
		
		private Keys(int key) {
			this.keyCode = key;
		}
		
		/**
		 * Get key using the key code it uses.
		 * @param code - KeyEvent constant
		 * @return the correct key, or null if not found.
		 */
		public static Keys getKey(int code) {
			for (Keys k : values())
				if (k.keyCode == code) return k;
			
			return null;
		}
	}
	
	private GameController controller;
	private TurnManager turnManager;
	private Dimension boundaries;
	private GameMove currentSpot;
	private boolean criticalSection;
	
	/**
	 * @param controller - The game's Controller object
	 * @param turnManager - The current manager of turns
	 * @param boundaries - The size of the board
	 */
	public KeyboardControl(GameController controller) {
		this.controller = controller;
		this.turnManager = controller.getTurnManager();
		this.boundaries = controller.getRelatedGame().getBoardSize();
		this.currentSpot = new GameMove(0, 0);
	}
	
	@Override
	public void keyPressed(KeyEvent e) {
		if (criticalSection || !turnManager.is(Participant.PLAYER_1)) return;
		else criticalSection = true;
		
		Keys pressedKey = Keys.getKey(e.getKeyCode());
		if (pressedKey == null) return;
		
		int currRow = currentSpot.getRow();
		int currCol = currentSpot.getColumn();
		GameMove nextSpot = null;
		
		/*
		 * Check if the key is part of the key set,
		 * and then apply the move if it's legal
		 */
		switch(pressedKey) {
			case UP: {
				if (currRow > 0)
					nextSpot = new GameMove(currRow - 1, currCol);
				
				break;
			}
			case DOWN: {
				if (currRow < boundaries.height - 1)
					nextSpot = new GameMove(currRow + 1, currCol);
				
				break;
			}
			case LEFT: {
				if (currCol > 0)
					nextSpot = new GameMove(currRow , currCol - 1);
				
				break;
			}
			case RIGHT: {
				if (currCol < controller.getRelatedGame().getBoardSize().width - 1)
					nextSpot = new GameMove(currRow, currCol + 1);
				
				break;
			}
		}
		
		//apply the move
		if (nextSpot != null) {
			controller.getCell(currentSpot).erase();
			controller.getCell(nextSpot).updateHuman();
			currentSpot = new GameMove(nextSpot.getRow(), nextSpot.getColumn());
		}
		
		criticalSection = false;
	}
	
	/**
	 * @param spot - The new spot
	 */
	public void setCurrentSpot(GameMove spot) { currentSpot = spot; }
	
	/**
	 * @return the current spot.
	 */
	public GameMove getCurrentSpot() { return currentSpot; }

	@Override
	public void keyReleased(KeyEvent arg0) {}

	@Override
	public void keyTyped(KeyEvent arg0) {}
}