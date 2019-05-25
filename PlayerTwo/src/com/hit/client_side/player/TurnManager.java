package com.hit.client_side.player;
import com.hit.client_side.UI.VSPanel;

import general_utility.math.RNG;

/**
 * Manage turn changing.
 * This class supports multiplaying, but however, normally it relies on an object
 * of VSPanel, which currently only supports two players.
 * @author Niv Kor
 */
public class TurnManager
{
	private Participant[] participants;
	private Participant currentPlayer;
	private VSPanel vsPanel;
	private boolean stop;
	
	/**
	 * Choose a player to get the first turn.
	 * @param vsPanel - a VSPanel object that containts the players
	 * @param participants - Array of participants in the game
	 * @param player - The player that gets to play first
	 */
	public TurnManager(VSPanel vsPanel, Participant[] participants, Participant player) {
		this.vsPanel = vsPanel;
		this.participants = participants;
		this.stop = false;
		set(player);
	}
	
	/**
	 * Choose a player randomly.
	 * @param vsPanel - a VSPanel object that containts the players
	 * @param participants - Array of participants in the game
	 */
	public TurnManager(VSPanel vsPanel, Participant[] participants) {
		this(vsPanel, participants, (Participant) RNG.select(participants));
	}
	
	/**
	 * Set the turn manualy.
	 * @param player - the player that gets to play next
	 */
	public void set(Participant player) {
		if (stop) return;
		
		currentPlayer = player;
		vsPanel.setPlayerTurn(currentPlayer);
	}
	
	/**
	 * Set the turn randomly.
	 */
	public void set() {
		set((Participant) RNG.select(participants));
	}
	
	/**
	 * Give the turn to the next player in line
	 */
	public void next() {
		for (int i = 0; i < participants.length; i++) {
			if (currentPlayer == participants[i]) {
				if (i + 1 < participants.length) set(participants[i + 1]);
				else set(participants[0]);
				break;
			}
		}
	}
	
	/**
	 * Return the turn back to the previous player in line.
	 */
	public void previous() {
		for (int i = 0; i < participants.length; i++) {
			if (currentPlayer == participants[i]) {
				if (i > 0) set(participants[i - 1]);
				else set(participants[participants.length - 1]);
				break;
			}
		}
	}
	
	/**
	 * Check if the current turn belongs to a player.
	 * @param player - The player to check if the turn belongs to
	 * @return true if the turn belons to the argument player
	 */
	public boolean is(Participant player) { return currentPlayer == player; }
	
	/**
	 * Check if the current turn belongs to a human.
	 * @return true if it belongs to a human or false if it's a computer's turn
	 */
	public boolean isHuman() { return currentPlayer.isHuman(); }
	
	/**
	 * Get the players that plays now.
	 * @return the player that owns the current turn
	 */
	public Participant getCurrent() { return currentPlayer; }
	
	/**
	 * Stop changing turns.
	 * @param flag - True to stop changing turns or false to continue
	 */
	public void stop(boolean flag) { stop = flag; }
}