package com.hit.game_session_control.tic_tac_toe;
import com.hit.UI.windows.Window;
import com.hit.game_launch.Game;
import com.hit.game_session_control.Controller;
import com.hit.game_session_control.BoardCell;

public class TicTacToeController extends Controller
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
	public Game getRelatedGame() { return Game.TIC_TAC_TOE; }

	@Override
	protected Class<? extends BoardCell> getCellChildClass() { return TicTacToeCell.class; }
}