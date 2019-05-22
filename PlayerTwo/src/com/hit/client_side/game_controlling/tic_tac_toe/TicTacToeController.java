package com.hit.client_side.game_controlling.tic_tac_toe;
import com.hit.client_side.UI.Window;
import com.hit.client_side.UI.launcher.ClientSideGame;
import com.hit.client_side.game_controlling.ClientSideController;
import com.hit.client_side.game_controlling.Cube;

public class TicTacToeController extends ClientSideController
{
	public TicTacToeController(Window window) {
		super(window);
	}
	
	@Override
	public void restart() {
		super.restart();
		triggerCompMove();
	}
	
	@Override
	public ClientSideGame getRelatedGame() { return ClientSideGame.TIC_TAC_TOE; }

	@Override
	protected Class<? extends Cube> getCubeChildClass() { return TicTacToeCube.class; }
}