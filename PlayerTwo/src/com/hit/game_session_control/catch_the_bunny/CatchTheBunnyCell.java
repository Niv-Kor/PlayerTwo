package com.hit.game_session_control.catch_the_bunny;
import java.awt.Dimension;
import java.io.IOException;
import javax.swing.Icon;
import com.hit.game_session_control.BoardCell;
import com.hit.game_session_control.GameController;
import com.hit.game_session_control.countdown.CountdownFacility;
import com.hit.players.Participant;
import game_algo.GameBoard.GameMove;
import javaNK.util.debugging.Logger;
import javaNK.util.math.DimensionalHandler;

public class CatchTheBunnyCell extends BoardCell
{
	private static final long serialVersionUID = 8229266456667930185L;
	
	private CountdownFacility countdown;
	
	public CatchTheBunnyCell(int row, int col, GameController controller) {
		super(row, col, controller);
		this.countdown = controller.getCountdownFacility();
	}
	
	@Override
	public void updateHuman() {
		if (turnManager.is(Participant.PLAYER_1) && enabled) {
			try {
				boolean success = controller.getCommunicator().makeMove(new GameMove(row, col));
				if (success) {
					placePlayer(Participant.PLAYER_1, true);
					countdown.decremenet();
					
					if (!controller.getCommunicator().tryEndgame() && countdown.isOver())
						controller.getCommunicator().forceLoss();
				}
			}
			catch (IOException ex) { Logger.error(ex); }
		}
	}

	@Override
	public void updateOtherPlayer() {
		if (!turnManager.is(Participant.PLAYER_1) && enabled) {
			//erase the last cell the other player was on
			GameMove lastMove = controller.getMoveFromBuffer(controller.getOtherPlayer());
			if (lastMove != null) controller.getCell(lastMove).erase();
			
			//make the move
			controller.setMoveInBuffer(controller.getOtherPlayer(), new GameMove(row, col));
			placePlayer(controller.getOtherPlayer(), true);
		}
	}
	
	@Override
	protected void placement(Participant player) throws Exception {
		Dimension iconDim = DimensionalHandler.adjust(getPreferredSize(), 80, 80);
		Icon playerIcon = player.getStatus().getAvatar().resizeIcon(iconDim);
		sign.setIcon(playerIcon);
		
		if (player == Participant.PLAYER_1) setBackground(PLAYER_1_COLOR.brighter());
		else setBackground(PLAYER_2_COLOR.brighter());
	}
	
	@Override
	public void erase() {
		sign.setIcon(null);
		setBackground(BACKGROUND_COLOR);
	}
}