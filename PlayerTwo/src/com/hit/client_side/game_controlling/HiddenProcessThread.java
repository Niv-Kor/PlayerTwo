package com.hit.client_side.game_controlling;
import java.io.IOException;

import com.hit.client_side.UI.EndgameProtocol;
import com.hit.client_side.UI.launcher.ClientSideGame;
import com.hit.client_side.connection.JSON;

import game_algo.GameBoard.GameMove;
import game_algo.IGameAlgo.GameState;

public class HiddenProcessThread extends ProcessThread
{
	public HiddenProcessThread(ClientSideController controller) {
		super(controller);
	}
	
	@Override
	public void run() {
		while(!isInterrupted()) {
			try {
				String[] requests = {"player2_move", "end_game"};
				JSON msg = protocol.waitFor(requests);
				
				switch(msg.getType()) {
					case "player2_move": {
						int row = msg.getInt("row");
						int col = msg.getInt("column");
						GameMove player2move = new GameMove(row, col);
						controller.getCube(player2move).updateOtherPlayer(player2move);
						
						break;
					}
					case "end_game": {
						//check if it's THIS game's ending
						ClientSideGame referencedGame = ClientSideGame.valueOf(msg.getString("game"));
						if (referencedGame != game) break;
						
						GameState gameState = GameState.valueOf(msg.getString("state"));
						System.out.println("game state is " + gameState);
						
						//pop an endgame window
						EndgameProtocol.pop(referencedGame, gameState);
						break;
					}
				}
			}
			catch(IOException e) { e.printStackTrace(); }
		}
	}
}