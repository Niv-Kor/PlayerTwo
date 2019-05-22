package com.hit.server_side.games;
import com.hit.server_side.game_algo.GameBoard;
import com.hit.server_side.game_controlling.ServerSideGame;

public abstract class CatchTheBunny extends GameBoard
{
	public static enum BoardSigns {
		BLANK('-'),
		COMPUTER('C'),
		PLAYER('P');
		
		private char sign;
		
		private BoardSigns(char c) {
			this.sign = c;
		}
		
		/**
		 * @return the player's type sign.
		 */
		public char getSign() { return sign; }
	}
	
	public CatchTheBunny(int rowLength, int colLength) {
		super(9, 9);
	}

	@Override
	public boolean updatePlayerMove(GameMove move) {
		removeLastMove(BoardSigns.PLAYER.getSign());
		boardState[move.getRow()][move.getColumn()] = BoardSigns.PLAYER.getSign();
		return true;
	}

	@Override
	public GameState getGameState(GameMove move) {
		GameMove playerSpot = getLastMove(BoardSigns.PLAYER.getSign());
		GameMove compSpot = getLastMove(BoardSigns.COMPUTER.getSign());
		
		int playerRow = playerSpot.getRow();
		int playerCol = playerSpot.getColumn();
		int compRow = compSpot.getRow();
		int compCol = compSpot.getColumn();
		
		//player and comp are on the same spot on the board
		if (playerRow == compRow && playerCol == compCol) return GameState.PLAYER_WON;
		//different spots on the board
		else return GameState.IN_PROGRESS;
	}
	
	@Override
	protected ServerSideGame getRelatedGame() { return ServerSideGame.CATCH_THE_BUNNY; }
	
	protected GameMove removeLastMove(char sign) {
		GameMove lastMove = getLastMove(sign);
		
		if (lastMove != null) boardState[lastMove.getRow()][lastMove.getColumn()] = BoardSigns.BLANK.getSign();
		return lastMove;
	}
	
	protected GameMove getLastMove(char sign) {
		for (int i = 0; i < boardState.length; i++)
			for (int j = 0; j < boardState[i].length; j++)
				if (boardState[i][j] == sign) return new GameMove(i, j);
		
		return null;
	}
}