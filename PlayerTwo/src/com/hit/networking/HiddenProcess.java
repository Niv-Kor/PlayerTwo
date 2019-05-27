package com.hit.networking;
import java.io.IOException;

import com.hit.UI.windows.EndgameProtocol;
import com.hit.game_launch.Game;
import com.hit.game_session_control.Controller;

import game_algo.GameBoard.GameMove;
import game_algo.IGameAlgo.GameState;
import javaNK.util.networking.JSON;

public class HiddenProcess extends Process implements Runnable
{
	private volatile boolean flush;
	
	public HiddenProcess(Controller controller) {
		super(controller);
	}
	
	@Override
	public void run() {
		while(true) {
			try {
				String[] requests = {"player2_move", "end_game"};
				JSON msg = protocol.waitFor(requests);
				
				switch(msg.getType()) {
					case "player2_move": {
						int row = msg.getInt("row");
						int col = msg.getInt("column");
						GameMove player2move = new GameMove(row, col);
						controller.getCell(player2move).updateOtherPlayer(player2move);
						
						break;
					}
					case "end_game": {
						//check if it's THIS game's ending
						Game referencedGame = Game.valueOf(msg.getString("game"));
						if (referencedGame != game) break;
						
						GameState gameState = GameState.valueOf(msg.getString("state"));
						
						//pop an endgame window
						EndgameProtocol.pop(referencedGame, gameState);
						break;
					}
				}
			}
			catch(IOException e) { e.printStackTrace(); }
		}
	}
	
	public void restart() {
		flush = true;
	}
}