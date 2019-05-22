package com.hit.server_side.games;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.hit.client_side.game_controlling.catch_the_bunny.Keys;
import com.hit.utility.math.RNG;

public class CatchTheBunnySmart extends CatchTheBunny
{
	private static enum GeneralDirection {
		TOP_LEFT(Keys.UP, Keys.LEFT),
		TOP_RIGHT(Keys.UP, Keys.RIGHT),
		BOTTOM_RIGHT(Keys.DOWN, Keys.RIGHT),
		BOTTOM_LEFT(Keys.DOWN, Keys.LEFT);
		
		private Keys key1, key2;
		
		private GeneralDirection(Keys key1, Keys key2) {
			this.key1 = key1;
			this.key2 = key2;
		}
		
		public Keys generateSpecificDirection() {
			return RNG.unstableCondition() ? key1 : key2;
		}
		
		public Keys getOtherSpecificDirection(Keys failedDirection) {
			return (failedDirection == key1) ? key2 : key1;
		}
		
		public static GeneralDirection escapeCorner(GeneralDirection corner) {
			switch(corner) {
				case TOP_LEFT: {
					if (RNG.unstableCondition()) return BOTTOM_LEFT;
					else return TOP_RIGHT;
				}
				case TOP_RIGHT: {
					if (RNG.unstableCondition()) return TOP_LEFT;
					else return BOTTOM_RIGHT;
				}
				case BOTTOM_RIGHT: {
					if (RNG.unstableCondition()) return TOP_RIGHT;
					else return BOTTOM_LEFT;
				}
				case BOTTOM_LEFT: {
					if (RNG.unstableCondition()) return TOP_LEFT;
					else return BOTTOM_RIGHT;
				}
				default: return null; //formal return statement
			}
		}
	}
	
	public CatchTheBunnySmart(int rowLength, int colLength) {
		super(rowLength, colLength);
	}

	@Override
	public void calcComputerMove() {
		//generate a new move and update the matrix and its cube
		GameMove newMove = nextMove();
		boardState[newMove.getRow()][newMove.getColumn()] = BoardSigns.COMPUTER.getSign();
		getRelatedGame().anonymousMoveBuffer = newMove;
	}
	
	private GameMove nextMove() {
		GameMove playerSpot = getLastMove(BoardSigns.PLAYER.getSign());
		GameMove compSpot = removeLastMove(BoardSigns.COMPUTER.getSign());
		
		int playerRow = playerSpot.getRow();
		int playerCol = playerSpot.getColumn();
		int compRow = compSpot.getRow();
		int compCol = compSpot.getColumn();
		
		GeneralDirection generalDirection;
		
		//select a general direction to run towards
		if (playerRow <= compRow) {
			if (playerCol <= compCol) generalDirection = GeneralDirection.BOTTOM_RIGHT;
			else generalDirection = GeneralDirection.BOTTOM_LEFT;
		}
		else {
			if (playerCol <= compCol) generalDirection = GeneralDirection.TOP_RIGHT;
			else generalDirection = GeneralDirection.TOP_LEFT;
		}
		
		//find one available move and return it
		return getAnyAvailableMove(compRow, compCol, playerSpot, generalDirection);
	}
	
	private GameMove getAnyAvailableMove(int compRow, int compCol, GameMove playerSpot, GeneralDirection general) {
		Keys direction = general.generateSpecificDirection();
		GameMove newMove;
		
		//try moving towards the general direction, using any move it's allowing us
		if ((newMove = tryMoving(compRow, compCol, playerSpot, direction)) != null) return newMove;
		else {
			//try other specific direction available
			direction = general.getOtherSpecificDirection(direction);
			if ((newMove = tryMoving(compRow, compCol, playerSpot, direction)) != null) return newMove;
		}
		
		//computer is stuck in the corner, do a random arrogant move
		//shuffle the Keys enum values to make it more random
		List<Keys> keyList = Arrays.asList(Keys.values());
		Collections.shuffle(keyList);
		
		//pick any legal move
		for (Keys key : keyList)
			if ((newMove = tryMoving(compRow, compCol, playerSpot, key)) != null) return newMove;
		
		//computer can no longer move
		//this scenario is theoretically impossible with a rectangle board 
		return null;
	}
	
	private GameMove tryMoving(int compRow, int compCol, GameMove playerSpot, Keys direction) {
		GameMove candidateMove;
		
		switch(direction) {
			case UP: {
				if (checkNotCorner(compRow, compCol, direction)) {
					candidateMove = new GameMove(compRow - 1, compCol);
					break;
				}
				else return null;
			}
			case DOWN: {
				if (checkNotCorner(compRow, compCol, direction)) {
					candidateMove = new GameMove(compRow + 1, compCol);
					break;
				}
				else return null;
			}
			case LEFT: {
				if (checkNotCorner(compRow, compCol, direction)) {
					candidateMove = new GameMove(compRow, compCol - 1);
					break;
				}
				else return null;
			}
			case RIGHT: {
				if (checkNotCorner(compRow, compCol, direction)) {
					candidateMove = new GameMove(compRow, compCol + 1);
					break;
				}
				else return null;
			}
			default: return null;
		}
		
		//check that the computer won't land on the player's cube after that move
		if (candidateMove.getRow() != playerSpot.getRow() || candidateMove.getColumn() != playerSpot.getColumn())
			return candidateMove;
		else
			return null;
	}
	
	private boolean checkNotCorner(int compRow, int compCol, Keys direction) {
		switch(direction) {
			case UP: return compRow > 0;
			case DOWN: return compRow < boardState.length - 1;
			case LEFT: return compCol > 0;
			case RIGHT: return compCol < boardState[0].length - 1;
			default: return false;
		}
	}
}