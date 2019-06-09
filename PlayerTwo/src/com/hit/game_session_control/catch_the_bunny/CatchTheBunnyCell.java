package com.hit.game_session_control.catch_the_bunny;
import java.awt.Dimension;
import java.io.IOException;

import javax.swing.Icon;

import com.hit.game_session_control.BoardCell;
import com.hit.game_session_control.Controller;
import com.hit.game_session_control.TurnManager;
import com.hit.players.Participant;

import game_algo.GameBoard.GameMove;
import javaNK.util.debugging.Logger;
import javaNK.util.math.Percentage;

public class CatchTheBunnyCell extends BoardCell
{
	private static final long serialVersionUID = 8229266456667930185L;
	
	public CatchTheBunnyCell(int row, int col, TurnManager turnManager, Controller controller) {
		super(row, col, turnManager, controller);
	}
	
	@Override
	public void updateHuman() {
		if (turnManager.is(Participant.PLAYER_1) && enabled) {
			try {
				boolean success = controller.getCommunicator().makeMove(new GameMove(row, col));
				if (success) placePlayer(Participant.PLAYER_1, true);
			}
			catch (IOException e) { Logger.error(e); }
		}
	}

	@Override
	public void updateOtherPlayer() {
		if (!turnManager.is(Participant.PLAYER_1) && enabled) {
			//erase the last cube the other player was on
			CatchTheBunnyController bunnyController = (CatchTheBunnyController) controller;
			GameMove lastCompMove = bunnyController.getOtherPlayerSpot();
			if (lastCompMove != null) controller.getCell(lastCompMove).erase();
			
			//make the move
			bunnyController.setOtherPlayerSpot(new GameMove(row, col));
			placePlayer(Participant.COMPUTER, true);
		}
	}
	
	@Override
	public void placement(Participant player) throws Exception {
		Dimension iconDim = Percentage.createDimension(getPreferredSize(), 80, 80);
		Icon playerIcon = player.getStatus().getAvatar().resizeIcon(iconDim);
		sign.setIcon(playerIcon);
		
		if (player == Participant.PLAYER_1) setBackground(PLAYER_1_COLOR.brighter());
		else setBackground(PLAYER_2_COLOR.brighter());
	}
	
	@Override
	public void erase() {
		sign.setIcon(null);
		setBackground(BACKGROUND_COLOR);
	}
}