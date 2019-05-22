package com.hit.client_side.player;
import java.io.IOException;
import com.hit.utility.math.RNG;

public enum Participant {
	PLAYER_1(true),
	PLAYER_2(true),
	COMPUTER(false);
	
	private boolean isHuman;
	private PlayerStatus playerStatus;
	
	private Participant(boolean human) {
		this.isHuman = human;
		
		try	{ this.playerStatus = new PlayerStatus(human); }
		catch (IOException e) {
			System.err.println("Unable to create player status for " + name() + ".");
			e.printStackTrace();
		}
	}
	
	/**
	 * Chooses a player randomly
	 * @return random player
	 */
	public static Participant generate() { return (Participant) RNG.select(values()); }
	
	/**
	 * Get the next player in line
	 * @param current - The current player
	 * @return the next player afterwards
	 */
	public static Participant getNext(Participant current) {
		Participant[] all = values();
		
		//get index of the current argument player (in values())
		int currentPlayerIndex = getEnumIndex(current);
		
		//return the next player in line
		if (currentPlayerIndex == 0) return all[all.length - 1];
		else return all[currentPlayerIndex - 1];
	}
	
	/**
	 * Get the previous player in line
	 * @param current - The current player
	 * @return the previous player beforehand
	 */
	public static Participant getPrev(Participant current) {
		Participant[] all = values();
		
		//get index of the current argument player (in values())
		int currentPlayerIndex = getEnumIndex(current);
		
		//return the next player in line
		if (all.length - 1 == currentPlayerIndex) return all[0];
		else return all[currentPlayerIndex + 1];
	}
	
	/**
	 * Check if the player is human
	 * @return true the player is human or false if it's a computer
	 */
	public boolean isHuman() { return isHuman; }
	
	/**
	 * @return PlayerStatus object of the player
	 */
	public PlayerStatus getStatus() { return playerStatus; }
	
	private static int getEnumIndex(Participant constant) {
		//get index of the current argument player (in values())
		for (int i = 0; i < values().length; i++)
			if (values()[i] == constant) return i;
		
		return -1; //formal return statement
	}
}