package com.hit.client_side.game_controlling;
import java.io.IOException;
import com.hit.client_side.connection.JSON;
import game_algo.GameBoard.GameMove;

public class VisibleProcessThread extends ProcessThread
{
	public VisibleProcessThread(ClientSideController controller) {
		super(controller);
	}
	
	public void makeMove(GameMove move) throws IOException {
		JSON message = new JSON("player_move");
		message.put("row", move.getRow());
		message.put("column", move.getColumn());
		protocol.send(message);
		delay(100);
	}
	
	public void randomMove() throws IOException {
		JSON message = new JSON("player_random");
		protocol.send(message);
		delay(100);
	}
	
	public void makeCompMove() throws IOException {
		JSON message = new JSON("computer_move");
		protocol.send(message);
		delay(100);
	}
	
	public void placePlayer(GameMove move) throws IOException {
		JSON message = new JSON("place_player");
		message.put("row", move.getRow());
		message.put("column", move.getColumn());
		protocol.send(message);
		delay(100);
	}
	
	public void placeComp(GameMove move) throws IOException {
		JSON message = new JSON("place_computer");
		message.put("row", move.getRow());
		message.put("column", move.getColumn());
		protocol.send(message);
		delay(100);
	}
	
	public char getSign() throws IOException {
		JSON message = new JSON("player_sign");
		return protocol.request(message).getChar("sign");
	}
	
	public char getOtherPlayerSign() throws IOException {
		JSON message = new JSON("player2_sign");
		return protocol.request(message).getChar("sign");
	}
}