package com.hit.client_side.player;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import com.hit.client_side.game_controlling.ClientSideController;

import general_utility.math.Range;

public class AITurn
{
	private TimerTask task;
	private TurnManager turnManager;
	private Range<Double> responseTime;
	
	public AITurn(TurnManager turnManager, ClientSideController controller) {
		this.responseTime = controller.getRelatedGame().getResponseTime();
		this.turnManager = turnManager;
		this.task = new TimerTask() {
			@Override
			public void run() {
				System.out.println("ai turn started");
				try { controller.getVisibleProcess().makeCompMove(); }
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
	 * which is optimizely between 0.8 and 2.2 seconds.
	 */
	public void thinkAndExecute() {
		thinkAndExecute(responseTime.generate());
	}
}