package com.hit.client_side.game_controlling.catch_the_bunny;
import java.io.IOException;

import com.hit.client_side.UI.Window;
import com.hit.client_side.UI.launcher.ClientSideGame;
import com.hit.client_side.UI.launcher.ClientSideGame.GameMode;
import com.hit.client_side.game_controlling.ClientSideController;
import com.hit.client_side.game_controlling.Cube;
import com.hit.client_side.player.Participant;

import game_algo.GameBoard.GameMove;
import general_utility.math.Range;

public class CatchTheBunnyController extends ClientSideController
{
	private GameMove otherPlayerSpot;
	
	public CatchTheBunnyController(Window window) {
		super(window);
		initPositions();
	}
	
	/**
	 * Initiate the position of the player at the start of the game randomly.
	 * If played at a single-player mode, the same thing goes with the computer player.
	 */
	private void initPositions() {
		int rows = getRelatedGame().getBoardSize().height - 1;
		int cols = getRelatedGame().getBoardSize().width - 1;
		
		//find a free spot for player
		GameMove freeSpot;
		CatchTheBunnyCube bunnyCube;
		Range<Integer> rowsRange = new Range<Integer>(0, getRelatedGame().getBoardSize().height - 1);
		Range<Integer> colsRange = new Range<Integer>(0, getRelatedGame().getBoardSize().width - 1);
		freeSpot = new GameMove((int) rowsRange.generate(), (int) colsRange.generate());
		
		//save the cube that the player is on
		bunnyCube = (CatchTheBunnyCube) getCube(freeSpot);
		
		//remove key listener from all windows, in case the game was restarted
		for (int i = 0; i < rows; i++)
			for (int j = 0; j < cols; j++)
				window.removeKeyListener((CatchTheBunnyCube) getCube(new GameMove(i, j)));
		
		//set the window's key listener to the current active player cube
		window.addKeyListener(bunnyCube);
		
		//update the player's spot in the UI as well as in the server
		GameMove playerSpot = null;
		try {
			playerSpot = new GameMove(freeSpot.getRow(), freeSpot.getColumn());
			bunnyCube.requestFocus();
			bunnyCube.placePlayer(Participant.PLAYER_1);
			visibleProcess.makeMove(playerSpot);
		}
		catch(IOException e) { e.printStackTrace(); }
		
		//also find a free spot for the computer if needed
		if (getRelatedGame().getGameMode() == GameMode.SINGLE_PLAYER) {
			int tempRow, tempColumn;
			do {
				tempRow = (int) rowsRange.generate();
				tempColumn = (int) colsRange.generate();
				freeSpot = new GameMove(tempRow, tempColumn);
			}
			//check that the board is clean at that spot
			while(tempRow == playerSpot.getRow() && tempColumn == playerSpot.getColumn());
			
			//save the cube that the computer is on
			bunnyCube = (CatchTheBunnyCube) getCube(freeSpot);
			
			//update the player's spot in the UI as well as in the server
			try {
				GameMove compSpot = new GameMove(freeSpot.getRow(), freeSpot.getColumn());
				bunnyCube.placePlayer(Participant.COMPUTER);
				visibleProcess.placeComp(compSpot);
				setOtherPlayerSpot(compSpot);
			}
			catch(IOException e) { e.printStackTrace(); }
		}
	}

	@Override
	public void restart() {
		super.restart();
		initPositions();
		triggerCompMove();
	}
	
	/**
	 * @return the spot of the other player on the board.
	 */
	public GameMove getOtherPlayerSpot() { return otherPlayerSpot; }
	
	/**
	 * Mark the spot of the other player.
	 * This method does not actually change the other player's spot,
	 * but rather mark it for later use.
	 * 
	 * @param spot - The spot of the other player
	 */
	public void setOtherPlayerSpot(GameMove spot) { otherPlayerSpot = spot; }
	
	@Override
	public ClientSideGame getRelatedGame() { return ClientSideGame.CATCH_THE_BUNNY; }
	
	@Override
	protected Class<? extends Cube> getCubeChildClass() { return CatchTheBunnyCube.class; }
}