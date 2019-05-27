package com.hit.networking;
import java.io.IOException;

import com.hit.game_launch.Game;
import com.hit.game_session_control.Controller;
import com.hit.players.Participant;

import javaNK.util.networking.JSON;

public abstract class Process
{
	protected Controller controller;
	protected ClientProtocol protocol;
	protected Game game;
	
	public Process(Controller controller) {
		this.controller = controller;
		this.protocol = Participant.PLAYER_1.getStatus().getProtocol();
		this.game = controller.getRelatedGame();
	}
	
	public boolean tryEndgame() throws IOException {
		JSON message = new JSON("is_over");
		return protocol.request(message).getBoolean("over");
	}
	
	protected void delay(long millisec) {
		try { Thread.sleep(millisec); }
		catch (InterruptedException e) { e.printStackTrace(); }
	}
}