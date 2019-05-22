package com.hit.server_side.game_algo;
import com.hit.server_side.Main;
import com.hit.server_side.game_controlling.ServerSideGame;
import com.hit.server_side.games.TicTacToe.BoardSigns;

public abstract class GameBoard implements IGameAlgo
{
	protected char[][] boardState;
	protected int rows, cols;
	
	public static class GameMove {
		private int row, column;
		
		public GameMove(int row, int column) {
			this.row = row;
			this.column = column;
		}
		
		public int getColumn() { return column; }
		public int getRow() { return row; }
	}
	
	public GameBoard(int rowLength, int colLength) {
		this.rows = rowLength;
		this.cols = colLength;
		this.boardState = new char[rows][cols];
	}
	
	public char[][] getBoardState() {
		if (Main.debug) printStateToConsole();
		return boardState;
	}
	
	protected void printStateToConsole() {
		System.out.println("== BOARD STATE: ==");
		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < cols; j++) {
				System.out.print(" " + boardState[i][j] + " ");
			}
			System.out.println();
		}
		System.out.println();
	}
	
	/**
	 * This method is called when a win was found on the board.
	 * It gets the winning char as a parameter, and then reaches the
	 * playerGameState[] buffer to input the state of each player in the game.
	 * 
	 * Each player was granted with a temporary unique index, that matches
	 * his spot in the array. So, if he wants to check his state in the game
	 * he must check his index in the buffer.
	 * 
	 * @param sign - The sign that won the game
	 */
	protected void feedGameStateBuffer(char sign) {
		GameState[] gameStateArr = getRelatedGame().playerGameState;
		
		for (int i = 0; i < gameStateArr.length; i++) {
			System.out.println("winning char is " + sign);
			System.out.println("checked char is " + (char) (sign - i));
			if ((char) (sign - i) == BoardSigns.PLAYER.getSign())
				gameStateArr[i] = GameState.PLAYER_WON;
			else
				gameStateArr[i] = GameState.PLAYER_LOST;
		}
	}
	
	protected abstract ServerSideGame getRelatedGame();
}