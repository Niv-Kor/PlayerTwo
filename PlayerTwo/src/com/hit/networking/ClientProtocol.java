package com.hit.networking;
import java.io.IOException;
import java.net.SocketException;
import com.hit.game_launch.Game;
import com.hit.game_launch.Game.GameMode;
import com.hit.players.PlayerStatus;
import javaNK.util.communication.JSON;
import javaNK.util.communication.NetworkInformation;
import javaNK.util.communication.Protocol;
import server_information.ServerData;

public class ClientProtocol extends Protocol
{
	private PlayerStatus status;
	
	/**
	 * @param status - The status of the participant that uses this protocol
	 * @throws IOException when the socket cannot connect to the host.
	 */
	public ClientProtocol(PlayerStatus status) throws IOException {
		this.status = status;
	}
	
	/**
	 * @param status - The status of the participant that uses this protocol
	 * @param port - The port this protocol will use
	 * @param targetPort - The port this protocol will communicate with
	 * @throws IOException when the socket cannot connect to the host.
	 */
	public ClientProtocol(PlayerStatus status, NetworkInformation local, NetworkInformation remote) throws IOException {
		super(local, remote);
		this.status = status;
	}
	
	/**
	 * Connect to the server that's responsible for the game.
	 * @param game - The current game the client wants to play
	 * @param reserved - True if the player is reserved to a pending game (that's waiting for him)
	 * @param reservations - Array of all the reserved players this player invites to a new game
	 * @param mode - The game mode to play
	 * @return true if the connection to the server is successful.
	 * @throws IOException when the server port is unavailable.
	 */
	public boolean connectServer(Game game, boolean reserved, String[] reservations, GameMode mode) throws IOException {
		//try connection if the socket is closed
		try { bind(); }
		catch (SocketException ex) {}
		
		setRemoteNetworkInformation(getServer());
		
		JSON message = new JSON("new_client");
		message.put("game", game.name());
		message.put("name", status.getNickname());
		message.put("avatar", status.getAvatar().getID());
		message.put("reserved", reserved);
		message.put("client", getLocalNetworkInformation().composeJSON());
		message.putArray("reservations", reservations);
		message.put("single_player", mode == GameMode.SINGLE_PLAYER);
		JSON answer = request(message);
		
		if (answer.getBoolean("available")) {
			setRemoteNetworkInformation(answer);
			return true;
		}
		else return false;
	}
	
	/**
	 * Disconnect from the server  that's responsible for the game.
	 * @param game - The current game the client wants to leave
	 * @throws IOException when the server port is unavailable.
	 */
	
	public void disconnectServer(Game game) throws IOException {
		setRemoteNetworkInformation(getServer());
		
		JSON message = new JSON("leaving_client");
		message.put("game", game.name());
		message.put("client", getLocalNetworkInformation().composeJSON());
		send(message);
		
		unbind();
	}

	public boolean renewGame(Game game) throws IOException {
		NetworkInformation oldRemote = getRemoteNetworkInformation();
		setRemoteNetworkInformation(getServer());
		
		JSON message = new JSON("happy_client");
		message.put("game", game.name());
		message.put("client", getLocalNetworkInformation().composeJSON());
		send(message);
		setRemoteNetworkInformation(oldRemote);
		
		String[] keys = { "start_game" };
		JSON answer = waitFor(keys);
		
		return answer != null && answer.getBoolean("available");
	}
	
	private NetworkInformation getServer() throws IOException {
		return new NetworkInformation(ServerData.PORT, ServerData.IP_ADDRESS);
	}
}