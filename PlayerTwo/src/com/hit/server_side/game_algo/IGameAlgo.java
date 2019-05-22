package com.hit.server_side.game_algo;

import com.hit.server_side.game_algo.GameBoard.GameMove;

public interface IGameAlgo
{
	enum GameState {
		ILLEGAL_PLAYER_MOVE,
		IN_PROGRESS,
		PLAYER_LOST,
		PLAYER_WON,
		TIE;
	}
	
	/**
	 * Calculates the copmuter's move and updates the game board.
	 */
	void calcComputerMove();
	
	/**
	 * Updates the player's move on the board.
	 * in case the move is not legal - nothing is done.
	 * @param move - the player's move
	 * @return true if the move is legal and false otherwise
	 */
	boolean updatePlayerMove(GameMove move);
	
	/**
	 * @param move - the last move made
	 * @return the game state: PLAYER_WON \ PLAYER_LOST \ TIE \ IN PROGRESS
	 */
	GameState getGameState(GameMove move);
	
	/**
	 * @return the game board state (each cell's content)
	 */
	char[][] getBoardState(); 
}