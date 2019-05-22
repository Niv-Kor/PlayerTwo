package com.hit.client_side.game_controlling;
import java.io.IOException;
import com.hit.client_side.UI.EndgameProtocol;
import com.hit.client_side.UI.launcher.ClientSideGame;
import com.hit.server_side.game_algo.GameBoard.GameMove;
import com.hit.server_side.game_algo.IGameAlgo.GameState;

public class HiddenProcessThread extends ProcessThread
{
	public HiddenProcessThread(ClientSideController controller) {
		super(controller);
	}
	
	@Override
	public void run() {
		while(!isInterrupted()) {
			try {
				String[] requests = {"p2move", "end"};
				String[] msg = protocol.waitFor(requests);
				
				switch(msg[0]) {
					case "p2move": {
						int row = Integer.parseInt(msg[1]);
						int col = Integer.parseInt(msg[2]);
						GameMove player2move = new GameMove(row, col);
						controller.getCube(player2move).updateOtherPlayer(player2move);
						break;
					}
					case "end": {
						//check if it's THIS game's ending
						ClientSideGame referencedGame = ClientSideGame.valueOf(msg[1]);
						if (referencedGame != game) break;
						
						GameState gameState = GameState.valueOf(msg[2]);
						
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