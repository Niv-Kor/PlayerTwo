package com.hit.networking;
import java.io.IOException;
import java.net.SocketException;

import com.hit.game_launch.Game;
import com.hit.game_launch.Game.GameMode;

import javaNK.util.networking.JSON;
import javaNK.util.networking.PortGenerator;
import javaNK.util.networking.Protocol;

public class ClientProtocol extends Protocol
{
	public ClientProtocol() throws IOException {}
	
	/**
	 * @param port - The port this protocol will use
	 * @param targetPort - The port this protocol will communicate with
	 * @throws IOException when the socket cannot connect to the host.
	 */
	public ClientProtocol(Integer port, Integer targetPort) throws IOException {
		super(port, targetPort);
	}
	
	/**
	 * Connect to the server that's responsible for the game.
	 * @param game - The current game the client wants to play
	 * @param reserved - True if the player is reserved to a pending game (that's waiting for him)
	 * @param reservations - Array of all the reserved players this player invites to a new game
	 * @param mode - The game mode to play
	 * @throws IOException when the server port is unavailable.
	 */
	public void connectServer(Game game, boolean reserved, String[] reservations, GameMode mode) throws IOException {
		//try connection if the socket is closed
		try { connect(); }
		catch(SocketException e) {}
		
		setRemotePort(PortGenerator.getAllocated("server_port"));
		JSON message = new JSON("new_client");
		message.put("game", game.name());
		message.put("port", getLocalPort());
		message.put("reserved", reserved);
		message.putArray("reservations", reservations);
		message.put("single_player", mode == GameMode.SINGLE_PLAYER);
		JSON answer = request(message);
		
		setRemotePort(answer.getInt("port"));
	}
	
	/**
	 * Disconnect from the server  that's responsible for the game.
	 * @param game - The current game the client wants to leave
	 * @throws IOException when the server port is unavailable.
	 */
	public void disconnectServer(Game game) throws IOException {
		setRemotePort(PortGenerator.getAllocated("server_port"));
		JSON message = new JSON("leaving_client");
		message.put("game", game.name());
		message.put("port", getLocalPort());
		send(message);
		
		disconnect();
	}
	
	public boolean renewGame(Game game) throws IOException {
		int oldRemote = getRemotePort();
		
		setRemotePort(PortGenerator.getAllocated("server_port"));
		JSON message = new JSON("happy_client");
		message.put("game", game.name());
		message.put("port", getLocalPort());
		send(message);
		setRemotePort(oldRemote);
		
		String[] keys = { "start_game" };
		JSON answer = waitFor(keys);
		
		return answer != null && answer.getBoolean("available");
	}
}