package com.hit.server_side.games;
import com.hit.utility.math.RNG;

public class CatchTheBunnyRandom extends CatchTheBunny
{
	private boolean cloned;
	
	public CatchTheBunnyRandom(int rowLength, int colLength) {
		super(rowLength, colLength);
	}

	@Override
	public boolean updatePlayerMove(GameMove move) {
		return false;
	}
	
	@Override
	public void calcComputerMove() {
		System.out.println("random");
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