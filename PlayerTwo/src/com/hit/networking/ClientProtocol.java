package com.hit.networking;
import java.io.IOException;
import java.net.SocketException;
import com.hit.game_launch.Game;
import javaNK.util.networking.JSON;
import javaNK.util.networking.PortGenerator;
import javaNK.util.networking.Protocol;

public class ClientProtocol extends Protocol
{
	/**
	 * @throws IOException when the socket cannot connect to the host.
	 */
	public ClientProtocol() throws IOException {
		super(true);
	}
	
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
	 * @throws IOException when the server port is unavailable.
	 */
	public void connectServer(Game game) throws IOException {
		//try connection if the socket is closed
		try { connect(); }
		catch(SocketException e) {}
		
		target = PortGenerator.getAllocated("server_port");
		JSON message = new JSON("new_client");
		message.put("game", game.name());
		message.put("port", port);
		
		JSON answer = request(message);
		target = answer.getInt("port");
	}
	
	/**
	 * Disconnect from the server  that's responsible for the game.
	 * @param game - The current game the client wants to leave
	 * @throws IOException when the server port is unavailable.
	 */
	public void disconnectServer(Game game) throws IOException {
		target = PortGenerator.getAllocated("server_port");
		JSON message = new JSON("leaving_client");
		message.put("game", game.name());
		message.put("port", port);
		send(message);
		
		disconnect();
	}
	
	public void renewGame(Game game) throws IOException {
		int oldProtocol = target;
		
		target = PortGenerator.getAllocated("server_port");
		JSON message = new JSON("happy_client");
		message.put("game", game.name());
		message.put("port", port);
		send(message);
		
		target = oldProtocol;
	}
}