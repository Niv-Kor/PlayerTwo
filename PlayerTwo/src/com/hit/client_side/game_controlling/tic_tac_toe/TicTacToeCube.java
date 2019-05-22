package com.hit.client_side.game_controlling.tic_tac_toe;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.IOException;

import com.hit.client_side.UI.launcher.ClientSideGame.GameMode;
import com.hit.client_side.game_controlling.ClientSideController;
import com.hit.client_side.game_controlling.Cube;
import com.hit.client_side.player.AITurn;
import com.hit.client_side.player.Participant;
import com.hit.client_side.player.TurnManager;
import com.hit.server_side.game_algo.GameBoard.GameMove;

public class TicTacToeCube extends Cube implements MouseListener
{
	private static final long serialVersionUID = -3294294011709456886L;
	
	public TicTacToeCube(int row, int col, TurnManager turnManager, ClientSideController controller) {
		super(row, col, turnManager, controller);
		addMouseListener(this);
	}

	@Override
	public void updateHuman(GameMove move) {
		if (!turnManager.is(Participant.PLAYER_1) || !enabled) return;
		
		try {
			controller.getVisibleProcess().makeMove(move);
			sign.setForeground(PLAYER_1_COLOR);
			sign.setText("" + controller.getVisibleProcess().getSign());
			
			//end turn
			turnManager.next();
			controller.enableRandomButton(false);
			controller.getVisibleProcess().tryEndgame();
		}
		catch(IOException e) { e.printStackTrace(); }
		
		//trigger computer move if needed
		if (controller.getRelatedGame().getGameMode() == GameMode.SINGLE_PLAYER) {
			AITurn compTurn = new AITurn(turnManager, controller);
			compTurn.thinkAndExecute();
		}
	}
	
	@Override
	public void updateOtherPlayer(GameMove move) {
		if (turnManager.is(Participant.PLAYER_1) || !enabled) return;
		
		try {
			sign.setForeground(PLAYER_2_COLOR);
			sign.setText("" + controller.getVisibleProcess().getOtherPlayerSign());
			
			//end turn
			turnManager.next();
			controller.enableRandomButton(true);
			controller.getVisibleProcess().tryEndgame();
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