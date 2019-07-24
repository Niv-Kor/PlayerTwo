package com.hit.game_session_control.tic_tac_toe;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import com.hit.UI.windows.Window;
import com.hit.game_launch.Game;
import com.hit.game_session_control.BoardCell;
import com.hit.game_session_control.GameView;
import com.hit.game_session_control.countdown.CountdownClock;
import com.hit.game_session_control.countdown.CountdownFacility;
import com.hit.players.Participant;

public class TicTacToeView extends GameView implements PropertyChangeListener
{
	public TicTacToeView(Window window) throws IOException {
		super(window);
		
		((CountdownClock) countdown).subscribePropertyChange(this);
		
		if (controller.getTurnManager().is(Participant.PLAYER_1))
			((CountdownClock) countdown).run();
	}
	
	@Override
	public Game getRelatedGame() { return Game.TIC_TAC_TOE; }

	@Override
	protected Class<? extends BoardCell> getCellChildClass() { return TicTacToeCell.class; }

	@Override
	protected void init() {}
	
	@Override
	protected void initPositions() {}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		if (evt.getPropertyName().equals("clock over")) {
			CountdownClock clock = (CountdownClock) countdown;
			clock.pause();
			clock.hide(true);
			controller.getTurnManager().set(controller.getOtherPlayer());
			controller.triggerCompMove();
		}
	}
	
	@Override
	protected CountdownFacility createCountdownFacility() {
		return new CountdownClock(getRelatedGame().getCountdownTime());
	}
}