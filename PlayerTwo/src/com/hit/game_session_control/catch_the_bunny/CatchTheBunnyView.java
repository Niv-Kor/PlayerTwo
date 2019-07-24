package com.hit.game_session_control.catch_the_bunny;
import java.awt.event.KeyListener;
import java.io.IOException;
import com.hit.UI.windows.Window;
import com.hit.game_launch.Game;
import com.hit.game_launch.Game.GameMode;
import com.hit.game_session_control.BoardCell;
import com.hit.game_session_control.GameView;
import com.hit.game_session_control.KeyboardControl;
import com.hit.game_session_control.countdown.CountdownFacility;
import com.hit.players.Participant;
import game_algo.GameBoard.GameMove;
import javaNK.util.math.Range;

public class CatchTheBunnyView extends GameView
{
	private KeyboardControl keyboardControl;
	
	public CatchTheBunnyView(Window window) throws IOException {
		super(window);
	}
	
	@Override
	protected void init() {
		this.keyboardControl = new KeyboardControl(controller);
		window.addKeyListener(keyboardControl);
	}
	
	@Override
	protected void initPositions() {
		try {
			window.addKeyListener(keyboardControl);
			
			int rows = getRelatedGame().getBoardSize().height - 1;
			int cols = getRelatedGame().getBoardSize().width - 1;
			
			//find a free spot for player
			GameMove freeSpot;
			Range<Integer> rowsRange = new Range<Integer>(0, rows);
			Range<Integer> colsRange = new Range<Integer>(0, cols);
			freeSpot = new GameMove((int) rowsRange.generate(), (int) colsRange.generate());
			
			//update the player's spot in the UI as well as in the server
			GameMove playerSpot = new GameMove(freeSpot.getRow(), freeSpot.getColumn());
			getCell(freeSpot).requestFocus();
			keyboardControl.setCurrentSpot(playerSpot);
			controller.placePlayer(Participant.PLAYER_1, playerSpot, false);
			
			//also find a free spot for the computer if needed
			if (getRelatedGame().getGameMode() == GameMode.SINGLE_PLAYER) {
				int tempRow, tempColumn;
				do {
					tempRow = (int) rowsRange.generate();
					tempColumn = (int) colsRange.generate();
					freeSpot = new GameMove(tempRow, tempColumn);
				}
				//check that the board is clean at that spot
				while (tempRow == playerSpot.getRow() && tempColumn == playerSpot.getColumn());
				
				//update the computer's spot in the UI as well as in the server
				controller.placePlayer(controller.getOtherPlayer(), freeSpot, false);
			}
		}
		catch (IOException ex) { ex.printStackTrace(); }
	}
	
	@Override
	public void restart() {
		for (KeyListener listener : window.getKeyListeners())
			window.removeKeyListener(listener);
		
		window.addKeyListener(keyboardControl);
		super.restart();
	}
	
	@Override
	public Game getRelatedGame() { return Game.CATCH_THE_BUNNY; }
	
	@Override
	protected Class<? extends BoardCell> getCellChildClass() { return CatchTheBunnyCell.class; }
	
	@Override
	protected CountdownFacility createCountdownFacility() {
		return new CountdownFacility(getRelatedGame().getCountdownTime());
	}
}