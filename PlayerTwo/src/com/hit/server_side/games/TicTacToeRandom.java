package com.hit.server_side.games;
import com.hit.utility.math.RNG;

public class TicTacToeRandom extends TicTacToe
{
	private boolean cloned;
	
	public TicTacToeRandom(int rowLength, int colLength) {
		super(rowLength, colLength);
	}

	public boolean updatePlayerMove(GameMove move) {
		cloneBoardState();
		boolean success;
		int playerIndex = getRelatedGame().lastMovePlayerIndex;
		GameMove tempSpot = findFreeSpot();
		
		if (tempSpot != null) {
			boardState[tempSpot.getRow()][tempSpot.getColumn()] = (char) (BoardSigns.PLAYER.getSign() + playerIndex);
			success = true;
		}
		else success = false;
		
		return success;
	}
	
	public void calcComputerMove() {
		cloneBoardState();
		GameMove tempSpot = findFreeSpot();
		
		if (tempSpot != null) {
			//controller.updateRandomMove(tempSpot, Participant.COMPUTER);
			boardState[tempSpot.getRow()][tempSpot.getColumn()] = BoardSigns.COMPUTER.getSign();
		}
	}
	
	private GameMove findFreeSpot() {
		//check if everything's occupied
		boolean loopBroken = false;
		
		outerLoop:
		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < cols; j++) {
				if (boardState[i][j] == BoardSigns.BLANK.getSign()) {
					loopBroken = true;
					break outerLoop;
				}
			}
		}
		
		if (!loopBroken) return null; //everything's occupied
		else {
			int row, col;
			
			//generate a free spot on the board
			do {
				row = RNG.generate(0, rows - 1);
				col = RNG.generate(0, cols - 1);
			}
			while (boardState[row][col] != BoardSigns.BLANK.getSign());
			
			//return the random spot we found
			return new GameMove(row, col);
		}
	}
	
	//need to share the same boardState across smart and random
	//clone boardState array if it didn't already occure
	private void cloneBoardState() {
		if (cloned) return;
		
		boardState = getRelatedGame().getSmartModel().getBoardState().clone();
		cloned = true;
	}
}