package com.hit.client_side.game_controlling;
import com.hit.client_side.UI.launcher.ClientSideGame;
import com.hit.client_side.connection.ClientSideProtocol;
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
}