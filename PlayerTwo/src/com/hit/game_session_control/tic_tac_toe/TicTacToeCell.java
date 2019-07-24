package com.hit.game_session_control.tic_tac_toe;
import java.awt.Color;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.IOException;
import com.hit.game_session_control.BoardCell;
import com.hit.game_session_control.GameController;
import com.hit.game_session_control.countdown.CountdownClock;
import com.hit.players.Participant;
import game_algo.GameBoard.GameMove;
import javaNK.util.debugging.Logger;

public class TicTacToeCell extends BoardCell implements MouseListener
{
	private static final long serialVersionUID = -3294294011709456886L;
	
	private CountdownClock clock;
	
	public TicTacToeCell(int row, int col, GameController controller) {
		super(row, col, controller);
		addMouseListener(this);
		this.clock = (CountdownClock) controller.getCountdownFacility();
	}

	@Override
	public void updateHuman() {
		if (turnManager.is(Participant.PLAYER_1) && enabled) {
			try {
				boolean success = controller.getCommunicator().makeMove(new GameMove(row, col));
				if (success) {
					placePlayer(Participant.PLAYER_1, true);
					clock.pause();
					clock.hide(true);
				}
			}
			catch (IOException e) { Logger.error(e); }
		}
	}
	
	@Override
	public void updateOtherPlayer() {
		if (!turnManager.is(Participant.PLAYER_1) && enabled) {
			placePlayer(Participant.PLAYER_2, true);
			clock.reset();
			clock.hide(false);
			clock.run();
		}
	}
	
	@Override
	public void placement(Participant player) throws Exception {
		char playerSign;
		Color foreground;
		
		if (player == Participant.PLAYER_1) {
			playerSign = controller.getCommunicator().getSign();
			foreground = PLAYER_1_COLOR;
		}
		else {
			playerSign = controller.getCommunicator().getOtherPlayerSign();
			foreground = PLAYER_2_COLOR;
		}
		
		sign.setForeground(foreground);
		sign.setText("" + playerSign);
	}
	
	@Override
	public void erase() { sign.setText(""); }
	
	@Override
	public void mouseEntered(MouseEvent e) { if (enabled) setBackground(SELECTED_BACKGROUND_COLOR); }
	
	@Override
	public void mouseExited(MouseEvent e) { setBackground(BACKGROUND_COLOR); }
	
	@Override
	public void mousePressed(MouseEvent e) { if (enabled) updateHuman(); }
	
	@Override
	public void mouseClicked(MouseEvent e) {}
	
	@Override
	public void mouseReleased(MouseEvent e) {}
}