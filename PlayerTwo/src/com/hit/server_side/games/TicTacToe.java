package com.hit.server_side.games;
import com.hit.server_side.game_algo.GameBoard;
import com.hit.server_side.game_controlling.ServerSideGame;
import com.hit.utility.math.RNG;

public abstract class TicTacToe extends GameBoard
{
	public static enum BoardSigns {
		BLANK,
		COMPUTER,
		PLAYER;
		
		private static int playerRandom = RNG.generate(0, 1);
		private char sign;
		
		private BoardSigns() {
			this.sign = initSign(name());
		}
		
		/**
		 * @return the player's type sign.
		 */
		public char getSign() { return sign; }
		
		private static char initSign(String name) {
			//make player and computer get different, but random signs
			switch(name) {
				case "BLANK": return '-';
				case "COMPUTER": return (playerRandom == 1) ? 'O' : 'X';
				case "PLAYER": return (playerRandom == 1) ? 'X' : 'O';
			}
			
			return '?'; //formal return statement
		}
	}
	
	public TicTacToe(int rowLength, int colLength) {
		super(3, 3);
		
		//init board state
		for (int i = 0; i < rows; i++)
			for (int j = 0; j < cols; j++)
				boardState[i][j] = BoardSigns.BLANK.getSign();
	}
	
	@Override
	public boolean updatePlayerMove(GameMove move) {
		boolean success;
		int playerIndex = getRelatedGame().lastMovePlayerIndex;
		
		if (boardState[move.getRow()][move.getColumn()] != BoardSigns.BLANK.getSign()) success = false;
		else {
			boardState[move.getRow()][move.getColumn()] = (char) (BoardSigns.PLAYER.getSign() + playerIndex);
			success = true;
		}

		return success;
	}

	public GameState getGameState(GameMove move) {
		//using the last move index to know who made the move
		int lastMovePlayer = getRelatedGame().lastMovePlayerIndex;
		char runningSign;
		int counter;
		
		//scan horizontally for wins
		horizontalScanner:
		for (int i = 0; i < rows; i++) {
			runningSign = boardState[i][0];
			
			//if first cube is blank continue searching
			if (runningSign == BoardSigns.BLANK.getSign()) continue;
			else counter = 1;
			
			for (int j = 1; j < cols; j++) {
				if (boardState[i][j] != runningSign) continue horizontalScanner;
				else counter++;
			}
			
			//found a win
			if (counter == 3) return getLastMoveState(runningSign, lastMovePlayer);
		}
		
		//scan vertically for wins
		verticalScanner:
		for (int i = 0; i < cols; i++) {
			runningSign = boardState[0][i];
			
			//if first cube is blank continue searching
			if (runningSign == BoardSigns.BLANK.getSign()) continue;
			else counter = 1;
			
			for (int j = 1; j < rows; j++) {
				if (boardState[j][i] != runningSign) continue verticalScanner;
				else counter++;
			}
			
			//found a win
			if (counter == 3) return getLastMoveState(runningSign, lastMovePlayer);
		}
		
		//scan descending diagonal for wins
		runningSign = boardState[0][0];
		
		//if first cube is blank finish searching
		if (runningSign != BoardSigns.BLANK.getSign()) {
			counter = 1;
			for (int i = 1, j = 1; i < rows && j < cols; i++, j++) {
				if (boardState[j][i] != runningSign) break;
				else counter++;
			}
			
			//found a win
			if (counter == 3) return getLastMoveState(runningSign, lastMovePlayer);
		}
		
		//scan ascending diagonal for wins
		runningSign = boardState[0][cols - 1];
		
		//if first cube is blank finish searching
		if (runningSign != BoardSigns.BLANK.getSign()) {
			counter = 1;
			for (int i = 1, j = cols - 2; i >= 0 && j >= 0; i++, j--) {
				if (boardState[i][j] != runningSign) break;
				else counter++;
			}
			
			//found a win
			if (counter == 3) return getLastMoveState(runningSign, lastMovePlayer);
		}
		
		//scan for a tie
		boolean tie = true;
		
		outerLoop:
		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < cols; j++) {
				if (boardState[i][j] == BoardSigns.BLANK.getSign()) {
					tie = false;
					break outerLoop;
				}
			}
		}
		if (tie) return GameState.TIE;
		
		//found nothing
		return GameState.IN_PROGRESS;
	}
	
	/**
	 * Update the GameState buffer in Game enum,
	 * and return the state of the player that played last.
	 * @param winningSign - The sign that has a streak on the board
	 * @param lastPlayerIndex - The index of the player that played last
	 * @return the player that played last's game state.
	 */
	private GameState getLastMoveState(char winningSign, int lastPlayerIndex) {
		feedGameStateBuffer(winningSign);
		return getRelatedGame().playerGameState[lastPlayerIndex];
	}
	
	protected ServerSideGame getRelatedGame() { return ServerSideGame.TIC_TAC_TOE; }
}