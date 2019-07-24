package com.hit.players;
import java.io.IOException;
import java.net.SocketException;
import com.hit.game_session_control.GameController;
import com.hit.game_session_control.TurnManager;
import javaNK.util.math.Range;
import javaNK.util.threads.QuickThread;

public class AITurn extends QuickThread
{
	private GameController controller;
	private TurnManager turnManager;
	private Range<Double> responseTime;
	
	/**
	 * @param turnManager - Turn manager of the game session
	 * @param control - Controller of the played game
	 */
	public AITurn(GameController controller) {
		this.responseTime = controller.getRelatedGame().getResponseTime();
		this.turnManager = controller.getTurnManager();
		this.controller = controller;
	}
	
	/**
	 * Simulate the computer's thinking process with a fixed delay.
	 * After the delay, the computer will make a move.
	 * 
	 * @param responseTime - The delay until the computer's makes its move
	 */
	public void thinkAndExecute(double responseTime) {
		//first check if current turn belongs to a non-human player
		if (turnManager.getCurrent().isHuman()) return;
		
		setDelay(responseTime);
		start();
	}
	
	/**
	 * Simulate the computer's thinking process with a random delay.
	 */
	public void thinkAndExecute() {
		thinkAndExecute(responseTime.generate());
	}
	
	@Override
	public void quickFunction() throws Exception {
		try { controller.getCommunicator().makeCompMove(); }
		catch (SocketException e) {} //socket is closed (doesn't matter, it means the game ended)
		catch (IOException e1) { e1.printStackTrace(); }
	}
}