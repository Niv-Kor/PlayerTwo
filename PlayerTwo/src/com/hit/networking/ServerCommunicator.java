package com.hit.networking;
import java.io.IOException;
import com.hit.UI.windows.EndgameProtocol;
import com.hit.game_launch.Game;
import com.hit.game_session_control.Controller;
import com.hit.players.Participant;
import game_algo.GameBoard.GameMove;
import game_algo.IGameAlgo.GameState;
import javaNK.util.networking.JSON;
import javaNK.util.networking.RespondCase;
import javaNK.util.networking.RespondEngine;

public class ServerCommunicator extends RespondEngine
{
	private Controller controller;
	private Game game;
	
	public ServerCommunicator(Controller controller) throws IOException {
		super(Participant.PLAYER_1.getStatus().getProtocol());
		
		this.controller = controller;
		this.game = controller.getRelatedGame();
		new Thread(this).start();
	}
	
	public boolean makeMove(GameMove move) throws IOException {
		JSON message = new JSON("player_move");
		message.put("row", move.getRow());
		message.put("column", move.getColumn());
		return protocol.request(message).getBoolean("success");
	}
	
	public void randomMove() throws IOException {
		JSON message = new JSON("player_random");
		protocol.send(message);
	}
	
	public void makeCompMove() throws IOException {
		JSON message = new JSON("computer_move");
		protocol.send(message);
	}
	
	public void placePlayer(GameMove move) throws IOException {
		JSON message = new JSON("place_player");
		message.put("row", move.getRow());
		message.put("column", move.getColumn());
		protocol.send(message);
	}
	
	public void placeComp(GameMove move) throws IOException {
		JSON message = new JSON("place_computer");
		message.put("row", move.getRow());
		message.put("column", move.getColumn());
		protocol.send(message);
	}
	
	public char getSign() throws IOException {
		JSON message = new JSON("player_sign");
		return protocol.request(message).getChar("sign");
	}
	
	public char getOtherPlayerSign() throws IOException {
		JSON message = new JSON("player2_sign");
		return protocol.request(message).getChar("sign");
	}
	
	public boolean tryEndgame() throws IOException {
		JSON message = new JSON("is_over");
		
		System.out.println("requesting is_over");
		JSON ans = protocol.request(message);
		return ans.getBoolean("over");
	}

	@Override
	protected void initCases() {
		//respond to a move, made by the other player
		addCase(new RespondCase() {
			@Override
			public String getType() { return "player2_move"; }

			@Override
			public void respond(JSON msg) throws Exception {
				int row = msg.getInt("row");
				int col = msg.getInt("column");
				GameMove player2move = new GameMove(row, col);
				controller.getCell(player2move).updateOtherPlayer(player2move);
			}
		});
		
		//respond to the end of the game
		addCase(new RespondCase() {
			@Override
			public String getType() { return "end_game"; }

			@Override
			public void respond(JSON msg) throws Exception {
				//check if it's THIS game's ending
				Game referencedGame = Game.valueOf(msg.getString("game"));
				if (referencedGame != game) return;
				
				GameState gameState = GameState.valueOf(msg.getString("state"));
				
				//pop an endgame window
				EndgameProtocol.pop(referencedGame, gameState);
			}
		});
	}
}