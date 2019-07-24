package com.hit.game_session_control;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import com.hit.game_launch.Game;
import com.hit.game_session_control.countdown.CountdownFacility;
import com.hit.networking.ServerCommunicator;
import com.hit.players.AITurn;
import com.hit.players.Participant;
import game_algo.GameBoard.GameMove;

public class GameController
{
	private TurnManager turnManager;
	private AITurn aiTurn;
	private ServerCommunicator serverCommunicator;
	private CountdownFacility countdown;
	private Participant[] participants;
	private Map<String, GameMove> moveBuffer;
	private GameView gameView;
	
	public GameController(GameView gameView, Participant[] participants) throws IOException {
		this.gameView = gameView;
		this.participants = participants;
		this.countdown = gameView.getCountdownFacility();
		this.moveBuffer = new HashMap<String, GameMove>();
		this.turnManager = new TurnManager(gameView.getVSPanel(), participants);
		turnManager.set(gameView.getRelatedGame().getFirstTurnParticipant());
		
		/*
		 * Initiate the server communicator thread,
		 * to send and receive information from the server during the game's session.
		 */
		this.serverCommunicator = new ServerCommunicator(gameView);
	}
	
	public void randomPlayerMove() throws IOException {
		placePlayer(Participant.PLAYER_1, serverCommunicator.randomMove(), true);
	}
	
	public void placePlayer(Participant participant, GameMove spot, boolean proceedTurn) throws IOException {
		//erase the last cell before making another move
		if (!getRelatedGame().areSignsAdded()) {
			GameMove lastMove = getMoveFromBuffer(participant);
			if (lastMove != null) getCell(lastMove).erase();
		}
		
		//make the move
		setMoveInBuffer(participant, spot);
		getCell(spot).placePlayer(participant, proceedTurn);
		if (participant == Participant.PLAYER_1) serverCommunicator.placePlayer(spot);
		else serverCommunicator.placeComp(spot);
	}
	
	/**
	 * Manually trigger the computer's move.
	 * If it isn't the computer's turn, do nothing.
	 */
	public void triggerCompMove() { triggerCompMove(-1); }
	
	/**
	 * @see triggerCompMove()
	 * @param sec - Seconds until the computer makes the move
	 */
	public void triggerCompMove(double sec) {
		if (!turnManager.is(Participant.PLAYER_1)) {
			aiTurn = new AITurn(this);
			
			if (sec != -1) aiTurn.thinkAndExecute(sec);
			else aiTurn.thinkAndExecute();
		}
	}
	
	public void enableRandomButton(boolean flag) {
		gameView.enableRandomButton(flag);
	}
	
	/**
	 * @return the thread that communicates with the server.
	 */
	public ServerCommunicator getCommunicator() { return serverCommunicator; }
	
	public TurnManager getTurnManager() { return turnManager; }
	
	public Game getRelatedGame() { return gameView.getRelatedGame(); }
	
	public void setMoveInBuffer(Participant p, GameMove spot) { moveBuffer.put(p.name(), spot); }
	
	public GameMove getMoveFromBuffer(Participant p) { return moveBuffer.get(p.name()); }
	
	public BoardCell getCell(GameMove spot) {
		return gameView.getCell(spot);
	}

	public void stop(boolean flag) {
		turnManager.stop(flag);
		if (aiTurn != null) aiTurn.cancel();
	}

	public void close() {
		serverCommunicator.kill();
		if (aiTurn != null) aiTurn.cancel();
	}

	public void restart() {
		countdown.reset(getRelatedGame().getCountdownTime());
		turnManager.set();
		triggerCompMove(3);
	}
	
	public Participant getOtherPlayer() { return participants[1]; }

	public CountdownFacility getCountdownFacility() { return countdown; }
}