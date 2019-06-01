package com.hit.players;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import com.hit.game_session_control.Controller;
import com.hit.game_session_control.TurnManager;

import javaNK.util.debugging.Logger;
import javaNK.util.math.Range;

public class AITurn
{
	private TimerTask task;
	private TurnManager turnManager;
	private Range<Double> responseTime;
	private static int counter = 0;
	
	public AITurn(TurnManager turnManager, Controller controller) {
		System.out.println("AITurn #" + counter++);
		
		this.responseTime = controller.getRelatedGame().getResponseTime();
		this.turnManager = turnManager;
		this.task = new TimerTask() {
			@Override
			public void run() {
				Logger.print("AI turn");
				try { controller.getCommunicator().makeCompMove(); }
				catch(IOException e) { e.printStackTrace(); }
			}
		};
	}
	
	/**
	 * Simulate the computer's thinking process with a fixed delay.
	 * After the delay, the computer will make a move. 
	 * @param responseTime - The delay until the computer's makes its move
	 */
	public void thinkAndExecute(double responseTime) {
		//first check if current turn belongs to a non-human player
		if (turnManager.getCurrent().isHuman()) return;
		
		long actualResponseTime = (long) (responseTime * 1000);
		Timer timer = new Timer();
		timer.schedule(task, actualResponseTime);
	}
	
	/**
	 * Simulate the computer's thinking process with a random delay,
	 * which is optimally between 0.8 and 2.2 seconds.
	 */
	public void thinkAndExecute() {
		thinkAndExecute(responseTime.generate());
	}
}