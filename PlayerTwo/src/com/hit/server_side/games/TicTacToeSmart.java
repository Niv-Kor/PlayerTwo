package com.hit.server_side.games;
import com.hit.utility.math.RNG;
public class TicTacToeSmart extends TicTacToe
{
	public TicTacToeSmart(int rowLength, int colLength) {
		super(rowLength, colLength);
	}

	public void calcComputerMove() {
		GameMove tempSpot = findFreeSpot();
		if (tempSpot != null) {
			boardState[tempSpot.getRow()][tempSpot.getColumn()] = BoardSigns.COMPUTER.getSign();
			getRelatedGame().anonymousMoveBuffer = tempSpot;
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
			
			//apply the move on the random spot found
			return new GameMove(row, col);
		}
	}
}