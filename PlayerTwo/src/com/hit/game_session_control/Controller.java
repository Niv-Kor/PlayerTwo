package com.hit.game_session_control;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.io.IOException;
import java.util.concurrent.Callable;

import javax.swing.JLabel;
import javax.swing.JPanel;

import com.hit.UI.VSPanel;
import com.hit.UI.states.State;
import com.hit.UI.windows.Window;
import com.hit.game_launch.Game;
import com.hit.game_launch.Game.GameMode;
import com.hit.game_launch.Launcher;
import com.hit.networking.ServerCommunicator;
import com.hit.players.AITurn;
import com.hit.players.Participant;

import game_algo.GameBoard.GameMove;
import javaNK.util.graphics.InteractiveIcon;
import javaNK.util.math.Percentage;

public abstract class Controller extends State
{
	protected TurnManager turnManager;
	protected InteractiveIcon dice;
	protected VSPanel vsPanel;
	protected GridBagConstraints constraints;
	protected ServerCommunicator serverCommunicator;
	protected BoardCell[][] cells;
	
	public Controller(Window window) throws IOException {
		super(window, 3);
		this.constraints = new GridBagConstraints();
		
		//players panel (panes[0])
		createPanel(new BorderLayout(), Percentage.createDimension(window.getDimension(), 100, 18), new Color(55, 55, 55));
		//game panel (panes[1])
		createPanel(new GridBagLayout(), Percentage.createDimension(window.getDimension(), 100, 75), new Color(73, 144, 137));
		//random selection panel (panes[2])
		createPanel(new GridBagLayout(), Percentage.createDimension(window.getDimension(), 100, 7), new Color(73, 144, 137));
		
		//decide which participants play the game
		GameMode mode = getRelatedGame().getGameMode();
		
		Participant[] participants = new Participant[2];
		participants[0] = Participant.PLAYER_1;
		switch (mode) {
			case SINGLE_PLAYER: participants[1] = Participant.COMPUTER; break;
			case MULTIPLAYER: participants[1] = Participant.PLAYER_2; break;
		}
		
		//build top player panel
		this.vsPanel = new VSPanel(participants[0], participants[1], panes[0]);
		this.turnManager = new TurnManager(vsPanel, participants);
		turnManager.set(getRelatedGame().getFirstTurnParticipant());
		panes[0].add(vsPanel, BorderLayout.CENTER);
		
		//build south random move panel
		//dice icon
		this.dice = new InteractiveIcon("miscellaneous/dice.png");
		dice.setHoverIcon("miscellaneous/dice_hover.png");
		dice.setFunction(new Callable<Void>() {
			@Override
			public Void call() throws Exception {
				serverCommunicator.randomMove();
				return null;
			}
		});
		
		constraints.gridx = 0;
		constraints.gridy = 0;
		constraints.insets = new Insets(-5, -32, 5, 5);
		panes[2].add(dice, constraints);
		
		//'random' label next to dice
		JLabel randomBtn = new JLabel("Random!");
		randomBtn.setForeground(Color.WHITE);
		randomBtn.setFont(LABEL_FONT);
		
		constraints.gridx = 1;
		constraints.insets.top = 5;
		constraints.insets.left = 5;
		panes[2].add(randomBtn, constraints);
		
		//initiate all cells of the correct game
		initCells();
		
		/*
		 * Initiate the server communicator thread,
		 * to send and receive information from the server during the game's session.
		 */
		this.serverCommunicator = new ServerCommunicator(this);
		
		//trigger the computer's first move manually if needed
		if (mode == GameMode.SINGLE_PLAYER) triggerCompMove();
	}
	
	/**
	 * Manually trigger the computer's move.
	 * If it isn't the computer's turn, do nothing.
	 */
	protected void triggerCompMove() {
		triggerCompMove(-1);
	}
	
	/**
	 * @see triggerCompMove()
	 * @param sec - Seconds until the computer makes the move
	 */
	protected void triggerCompMove(double sec) {
		//if the first turn goes to a computer participant, trigger his move here
		if (turnManager.is(Participant.COMPUTER)) {
			enableRandomButton(false);
			AITurn compTurn = new AITurn(turnManager, this);
			
			if (sec != -1) compTurn.thinkAndExecute(sec);
			else compTurn.thinkAndExecute();
			
			enableRandomButton(true);
		}
	}
	
	/**
	 * Enable or disable the random move button.
	 * @param flag - True to enable or false to disable
	 */
	public void enableRandomButton(boolean flag) {
		dice.enableHover(flag);
		dice.enableFunction(flag);
	}
	
	/**
	 * Update every component that needs to be changed for the random move.
	 * @param move - The random move that should take place
	 * @param player - Which player made that move
	 */
	public void updateRandomMove(GameMove move, Participant player) {
		BoardCell cell = cells[move.getRow()][move.getColumn()];
		
		switch(player) {
			case PLAYER_1: cell.updateHuman(move); break;
			case PLAYER_2:
			case COMPUTER: cell.updateOtherPlayer(move); break;
		}
	}
	
	/**
	 * Restart the game.
	 */
	public void restart() {
		Launcher.restartGame(getRelatedGame());
		stop(false);
		
		//erase all cells
		for (BoardCell[] cellRow : cells)
			for (BoardCell cell : cellRow)
				cell.erase();
		
		turnManager.set();
		if (getRelatedGame().getGameMode() == GameMode.SINGLE_PLAYER) triggerCompMove(3);
	}
	
	@Override
	public void insertPanels() {
		window.insertPanel(panes[0], BorderLayout.NORTH);
		window.insertPanel(panes[1], BorderLayout.CENTER);
		window.insertPanel(panes[2], BorderLayout.SOUTH);
	}
	
	/**
	 * Stop the game entirely or continue its state.
	 * A paused game does not react to any mouse event.
	 * @param flag - True to stop the game or false to continue
	 */
	public void stop(boolean flag) {
		turnManager.stop(flag);
		vsPanel.stop(flag);
		
		//enable or disable all cells
		for (BoardCell[] cellRow : cells)
			for (BoardCell cell : cellRow)
				cell.enable(!flag);
		
		//enable or disable random button
		dice.enableFunction(!flag);
		dice.enableHover(!flag);
	}
	
	/**
	 * @return the center panel that the actual game takes place in
	 */
	public JPanel getGamePanel() { return panes[1]; }
	
	/**
	 * @param row - The cell's row in the matrix
	 * @param col - The cell's column in the matrix
	 * @return the cell in that position
	 */
	public BoardCell getCell(GameMove spot) { return cells[spot.getRow()][spot.getColumn()]; }
	
	/**
	 * @return the game that this controller controls.
	 */
	public abstract Game getRelatedGame();
	
	/**
	 * @return the compatible game's child class that extands cell
	 */
	protected abstract Class<? extends BoardCell> getCellChildClass();
	
	public ServerCommunicator getCommunicator() { return serverCommunicator; }
	
	private void initCells() {
		Class<? extends BoardCell> cellClass = getCellChildClass();
		
		int rows = getRelatedGame().getBoardSize().height;
		int cols = getRelatedGame().getBoardSize().width;
		
		//define the cells' size
		Dimension overallDim = Percentage.createDimension(panes[1].getPreferredSize(), 80, 80);
		Dimension cellDim = new Dimension(overallDim.width / rows, overallDim.height / cols);
		
		//init cells array
		this.cells = new BoardCell[rows][cols];
		constraints.gridx = 0;
		constraints.gridy = 0;
		constraints.insets = new Insets(1, 1, 1, 1);
		
		//init every cell and place it
		for (int i = 0; i < rows; i++, constraints.gridy++, constraints.gridx = 0) {
			for (int j = 0; j < cols; j++, constraints.gridx++) {
				//create the cell using reflection
				try { cells[i][j] = cellClass.asSubclass(BoardCell.class).
					  getConstructor(int.class, int.class, TurnManager.class, Controller.class).
					  newInstance(i, j, turnManager, this);
				}
				catch(Exception e) {
					System.err.println("Could not initialize cells for " + getClass() + ".");
					e.printStackTrace();
				}
				
				cells[i][j].setPreferredSize(cellDim);
				getGamePanel().add(cells[i][j], constraints);
			}
		}
	}
}