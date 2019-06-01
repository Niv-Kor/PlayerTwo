package com.hit.game_session_control.tic_tac_toe;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.IOException;
import com.hit.game_launch.Game.GameMode;
import com.hit.game_session_control.BoardCell;
import com.hit.game_session_control.Controller;
import com.hit.game_session_control.TurnManager;
import com.hit.players.AITurn;
import com.hit.players.Participant;
import game_algo.GameBoard.GameMove;

public class TicTacToeCell extends BoardCell implements MouseListener
{
	private static final long serialVersionUID = -3294294011709456886L;
	
	public TicTacToeCell(int row, int col, TurnManager turnManager, Controller controller) {
		super(row, col, turnManager, controller);
		addMouseListener(this);
	}

	@Override
	public void updateHuman(GameMove move) {
		if (!turnManager.is(Participant.PLAYER_1) || !enabled) return;
		boolean gameEnded = false;
		
		try {
			boolean success = controller.getCommunicator().makeMove(move);
			
			if (success) {
				//end turn
				turnManager.next();
				controller.enableRandomButton(false);
				
				sign.setForeground(PLAYER_1_COLOR);
				sign.setText("" + controller.getCommunicator().getSign());
				
				//try to finish game
				gameEnded = controller.getCommunicator().tryEndgame();
				
				//trigger computer move if needed
				if (!gameEnded && controller.getRelatedGame().getGameMode() == GameMode.SINGLE_PLAYER) {
					AITurn compTurn = new AITurn(turnManager, controller);
					compTurn.thinkAndExecute();
				}
			}
		}
		catch(IOException e) { e.printStackTrace(); }
	}
	
	@Override
	public void updateOtherPlayer(GameMove move) {
		if (turnManager.is(Participant.PLAYER_1) || !enabled) return;
		
		try {
			//end turn
			turnManager.next();
			controller.enableRandomButton(true);
			
			sign.setForeground(PLAYER_2_COLOR);
			sign.setText("" + controller.getCommunicator().getOtherPlayerSign());
			
			//try to finish game
			controller.getCommunicator().tryEndgame();
		}
		catch(IOException e) { e.printStackTrace(); }
	}
	
	@Override
	public void erase() { sign.setText(""); }
	
	@Override
	public void mouseEntered(MouseEvent e) { if (enabled) setBackground(SELECTED_BACKGROUND_COLOR); }
	
	@Override
	public void mouseExited(MouseEvent e) { setBackground(BACKGROUND_COLOR); }
	
	@Override
	public void mousePressed(MouseEvent e) { if (enabled) updateHuman(new GameMove(row, col)); }
	public void mouseClicked(MouseEvent e) {}
	public void mouseReleased(MouseEvent e) {}
}