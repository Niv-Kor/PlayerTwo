package com.hit.game_session_control.catch_the_bunny;
import java.awt.event.KeyListener;
import java.io.IOException;
import com.hit.UI.windows.Window;
import com.hit.game_launch.Game;
import com.hit.game_launch.Game.GameMode;
import com.hit.game_session_control.BoardCell;
import com.hit.game_session_control.Controller;
import com.hit.game_session_control.KeyboardControl;
import com.hit.players.Participant;
import game_algo.GameBoard.GameMove;
import javaNK.util.math.Range;

public class CatchTheBunnyController extends Controller
{
	private GameMove otherPlayerSpot;
	private KeyboardControl keyboardControl;
	
	public CatchTheBunnyController(Window window) throws IOException {
		super(window);
	}
	
	@Override
	protected void init() {
		this.keyboardControl = new KeyboardControl(this, turnManager, getRelatedGame().getBoardSize());
		window.addKeyListener(keyboardControl);
	}
	
	@Override
	protected void initPositions() {
		window.addKeyListener(keyboardControl);
		
		int rows = getRelatedGame().getBoardSize().height - 1;
		int cols = getRelatedGame().getBoardSize().width - 1;
		
		//find a free spot for player
		GameMove freeSpot;
		CatchTheBunnyCell bunnyCube;
		Range<Integer> rowsRange = new Range<Integer>(0, rows);
		Range<Integer> colsRange = new Range<Integer>(0, cols);
		freeSpot = new GameMove((int) rowsRange.generate(), (int) colsRange.generate());
		
		//save the cube that the player is on
		bunnyCube = (CatchTheBunnyCell) getCell(freeSpot);
		
		//update the player's spot in the UI as well as in the server
		GameMove playerSpot = null;
		try {
			playerSpot = new GameMove(freeSpot.getRow(), freeSpot.getColumn());
			bunnyCube.requestFocus();
			bunnyCube.placePlayer(Participant.PLAYER_1, false);
			keyboardControl.setCurrentSpot(playerSpot);
			serverCommunicator.placePlayer(playerSpot);
		}
		catch (IOException e) { e.printStackTrace(); }
		
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
			bunnyCube = (CatchTheBunnyCell) getCell(freeSpot);
			
			//update the computer's spot in the UI as well as in the server
			try {
				GameMove compSpot = new GameMove(freeSpot.getRow(), freeSpot.getColumn());
				bunnyCube.placePlayer(Participant.COMPUTER, false);
				setOtherPlayerSpot(compSpot);
				serverCommunicator.placeComp(compSpot);
			}
			catch (IOException e) { e.printStackTrace(); }
		}
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
}