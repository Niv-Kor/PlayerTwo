package com.hit.client_side.game_controlling;
import java.io.IOException;

import com.hit.client_side.UI.launcher.ClientSideGame;
import com.hit.client_side.connection.ClientSideProtocol;
import com.hit.client_side.connection.JSON;
import com.hit.client_side.player.Participant;

public abstract class ProcessThread extends Thread
{
	protected ClientSideController controller;
	protected ClientSideProtocol protocol;
	protected ClientSideGame game;
	
	public ProcessThread(ClientSideController controller) {
		this.controller = controller;
		this.protocol = Participant.PLAYER_1.getStatus().getProtocol();
		this.game = controller.getRelatedGame();
	}
	
	public void tryEndgame() throws IOException {
		JSON message = new JSON("is_over");
		protocol.send(message);
		delay(100);
	}
	
	protected void delay(long millisec) {
		try { Thread.sleep(millisec); }
		catch (InterruptedException e) { e.printStackTrace(); }
	}
}