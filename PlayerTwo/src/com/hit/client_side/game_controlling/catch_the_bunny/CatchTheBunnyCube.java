package com.hit.client_side.game_controlling.catch_the_bunny;
import java.awt.Dimension;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.IOException;

import javax.swing.Icon;

import com.hit.client_side.UI.Window;
import com.hit.client_side.UI.launcher.ClientSideGame.GameMode;
import com.hit.client_side.game_controlling.ClientSideController;
import com.hit.client_side.game_controlling.Cube;
import com.hit.client_side.player.AITurn;
import com.hit.client_side.player.Participant;
import com.hit.client_side.player.TurnManager;

import game_algo.GameBoard.GameMove;
import general_utility.math.Percentage;

public class CatchTheBunnyCube extends Cube implements KeyListener
{
	private static final long serialVersionUID = 8229266456667930185L;
	
	public CatchTheBunnyCube(int row, int col, TurnManager turnManager, ClientSideController controller) {
		super(row, col, turnManager, controller);
		addKeyListener(this);
	}

	@Override
	public void updateHuman(GameMove move) {
		if (!turnManager.is(Participant.PLAYER_1) || !enabled) return;
		
		try {
			//remove the player's icon from this cube
			erase();
			
			//add the player's icon to the target cube
			CatchTheBunnyCube targetCube = (CatchTheBunnyCube) controller.getCube(move);
			targetCube.placePlayer(Participant.PLAYER_1);
			
			//make the move
			controller.getVisibleProcess().makeMove(move);
			
			//move focus to the target cube
			Window gameWindow = controller.getWindow();
			gameWindow.removeKeyListener(this);
			gameWindow.addKeyListener(targetCube);
			
			//end turn
			turnManager.next();
			controller.enableRandomButton(false);
			controller.getVisibleProcess().tryEndgame();
		}
		catch(IOException e) { e.printStackTrace(); }
		
		//trigger computer move if needed
		if (controller.getRelatedGame().getGameMode() == GameMode.SINGLE_PLAYER) {
			AITurn compTurn = new AITurn(turnManager, controller);
			compTurn.thinkAndExecute();
		}
	}

	@Override
	public void updateOtherPlayer(GameMove move) {
		if (turnManager.is(Participant.PLAYER_1) || !enabled) return;
		
		try {
			//erase the last cube the other player was on
			CatchTheBunnyController bunnyController = (CatchTheBunnyController) controller;
			GameMove lastCompMove = bunnyController.getOtherPlayerSpot();
			if (lastCompMove != null) controller.getCube(lastCompMove).erase();
			
			//add the other player's icon to this cube
			placePlayer(Participant.COMPUTER);
			bunnyController.setOtherPlayerSpot(move);
			
			//end turn
			turnManager.next();
			controller.enableRandomButton(true);
			controller.getVisibleProcess().tryEndgame();
		}
		catch(IOException e) { e.printStackTrace(); }
	}
	
	/**
	 * Locate a player's avatar on this cube.
	 * @param player - The player that the avatar belongs to
	 */
	public void placePlayer(Participant player) {
		CatchTheBunnyCube targetCube = (CatchTheBunnyCube) controller.getCube(new GameMove(row, col));
		Dimension iconDim = Percentage.createDimension(getPreferredSize(), 80, 80);
		Icon playerIcon = player.getStatus().getAvatar().getIcon(iconDim);
		targetCube.getSignLabel().setIcon(playerIcon);
		
		if (player == Participant.PLAYER_1) setBackground(PLAYER_1_COLOR.brighter());
		else setBackground(PLAYER_2_COLOR.brighter());
	}
	
	@Override
	public void erase() {
		sign.setIcon(null);
		setBackground(BACKGROUND_COLOR);
	}
	
	@Override
	public void keyPressed(KeyEvent e) {
		//find the exact key that was pressed
		Keys pressedKey = Keys.getKey(e.getKeyCode());
		
		//the key that was pressed is ilegal
		if (pressedKey == null) return;
		
		//check if the key is part of the key set,
		//and then apply the move if it's legal
		switch(pressedKey) {
			case UP: {
				if (row > 0) updateHuman(new GameMove(row - 1, col));
				return;
			}
			case DOWN: {
				if (row < controller.getRelatedGame().getBoardSize().height - 1)
					updateHuman(new GameMove(row + 1, col));
				
				return;
			}
			case LEFT: {
				if (col > 0) updateHuman(new GameMove(row , col - 1));
				return;
			}
			case RIGHT: {
				if (col < controller.getRelatedGame().getBoardSize().width - 1)
					updateHuman(new GameMove(row, col + 1));
				
				return;
			}
			default: return;
		}
	}
	
	@Override
	public void keyReleased(KeyEvent e) {}
	
	@Override
	public void keyTyped(KeyEvent e) {}
}