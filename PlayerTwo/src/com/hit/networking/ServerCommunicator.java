package com.hit.networking;
import java.io.IOException;
import com.hit.UI.windows.EndgameProtocol;
import com.hit.game_launch.Game;
import com.hit.game_session_control.Controller;
import com.hit.players.Participant;
import game_algo.GameBoard.GameMove;
import game_algo.IGameAlgo.GameState;
import javaNK.util.networking.JSON;
import javaNK.util.networking.ResponseCase;
import javaNK.util.networking.ResponseEngine;

public class ServerCommunicator extends ResponseEngine
{
	private Controller controller;
	private Game game;
	
	/**
	 * @param controller - The Controller object of the game
	 * @throws IOException when the client's protocol is unavailable.
	 */
	public ServerCommunicator(Controller controller) throws IOException {
		super(Participant.PLAYER_1.getStatus().getProtocol(), false);
		
		this.controller = controller;
		this.game = controller.getRelatedGame();
		start();
	}
	
	/**
	 * Ask the server to make a player move.
	 * 
	 * @param move - The move to make
	 * @return true if the move was successful.
	 * @throws IOException when the client's protocol is unavailable. 
	 */
	public boolean makeMove(GameMove move) throws IOException {
		JSON message = new JSON("player_move");
		message.put("row", move.getRow());
		message.put("column", move.getColumn());
		return protocol.request(message).getBoolean("success");
	}
	
	/**
	 * Ask the server to make a random player move.
	 * 
	 * @return the move that had been made, or null if nothing happened.
	 * @throws IOException when the client's protocol is unavailable.
	 */
	public GameMove randomMove() throws IOException {
		JSON message = new JSON("player_random");
		JSON answer = protocol.request(message);
		
		int row = answer.getInt("row");
		int col = answer.getInt("column");
		return new GameMove(row, col);
	}
	
	/**
	 * Ask the server to make a computer move.
	 * 
	 * @throws IOException when the client's protocol is unavailable.
	 */
	public void makeCompMove() throws IOException {
		JSON message = new JSON("computer_move");
		protocol.send(message);
	}
	
	/**
	 * Ask the server to place the player's sign on the board.
	 * 
	 * @param spot - The spot to place the player's sign at
	 * @throws IOException when the client's protocol is unavailable.
	 */
	public void placePlayer(GameMove spot) throws IOException {
		JSON message = new JSON("place_player");
		message.put("row", spot.getRow());
		message.put("column", spot.getColumn());
		protocol.send(message);
	}
	
	/**
	 * Ask the server to place the computer's sign on the board.
	 * 
	 * @param spot - The spot to place the computer's sign at
	 * @throws IOException when the client's protocol is unavailable.
	 */
	public void placeComp(GameMove spot) throws IOException {
		JSON message = new JSON("place_computer");
		message.put("row", spot.getRow());
		message.put("column", spot.getColumn());
		protocol.send(message);
	}
	
	/**
	 * Ask the server for the player's sign.
	 * 
	 * @return the player's sign
	 * @throws IOException when the client's protocol is unavailable.
	 */
	public char getSign() throws IOException {
		JSON message = new JSON("player_sign");
		return protocol.request(message).getChar("sign");
	}
	
	/**
	 * Ask the server for the other player's sign.
	 * 
	 * @return the other player's sign.
	 * @throws IOException when the client's protocol is unavailable.
	 */
	public char getOtherPlayerSign() throws IOException {
		JSON message = new JSON("player2_sign");
		return protocol.request(message).getChar("sign");
	}
	
	/**
	 * Ask the server if the game has reached to its end. 
	 * 
	 * @return true if the game is over (due to any reason).
	 * @throws IOException when the client's protocol is unavailable.
	 */
	public boolean tryEndgame() throws IOException {
		JSON message = new JSON("is_over");
		JSON ans = protocol.request(message);
		return ans.getBoolean("over");
	}

	@Override
	protected void initCases() {
		//respond to a move, made by the other player
		addCase(new ResponseCase() {
			@Override
			public String getType() { return "player2_move"; }

			@Override
			public void respond(JSON msg) throws Exception {
				int row = msg.getInt("row");
				int col = msg.getInt("column");
				GameMove player2move = new GameMove(row, col);
				controller.getCell(player2move).updateOtherPlayer();
			}
		});
		
		//respond to the end of the game
		addCase(new ResponseCase() {
			@Override
			public String getType() { return "end_game"; }

			@Override
			public void respond(JSON msg) throws Exception {
				Game referencedGame = Game.valueOf(msg.getString("game"));
				
				//check if it's THIS game's ending
				if (referencedGame != game) return;
				
				GameState gameState = GameState.valueOf(msg.getString("state"));
				
				//pop an endgame window
				EndgameProtocol.pop(referencedGame, gameState);
			}
		});
	}
}