package com.hit.client_side.game_controlling;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.concurrent.Callable;

import javax.swing.JLabel;
import javax.swing.JPanel;

import com.hit.client_side.UI.VSPanel;
import com.hit.client_side.UI.Window;
import com.hit.client_side.UI.launcher.ClientSideGame;
import com.hit.client_side.UI.launcher.ClientSideGame.GameMode;
import com.hit.client_side.UI.launcher.Launcher;
import com.hit.client_side.UI.states.State;
import com.hit.client_side.player.AITurn;
import com.hit.client_side.player.Participant;
import com.hit.client_side.player.TurnManager;
import com.hit.server_side.game_algo.GameBoard.GameMove;
import com.hit.utility.InteractiveIcon;
import com.hit.utility.math.Percentage;

public abstract class ClientSideController extends State
{
	protected TurnManager turnManager;
	protected InteractiveIcon dice;
	protected VSPanel vsPanel;
	protected GridBagConstraints constraints;
	protected HiddenProcessThread hiddenProcess;
	protected VisibleProcessThread visibleProcess;
	protected Cube[][] cubes;
	
	public ClientSideController(Window window) {
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
				Participant.PLAYER_1.getStatus().getProtocol().send("random:");
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
		
		//init all cubes of the correct game
		initCubes();
		
		/*
		 * Init the hidden process listener thread,
		 * to be notified about the flow of other processes in the game.
		 */
		this.hiddenProcess = new HiddenProcessThread(this);
		hiddenProcess.start();
		
		/*
		 * Init the visible process listener thread,
		 * to send and receive information from the server during the player's turn.
		 */
		this.visibleProcess = new VisibleProcessThread(this);
		visibleProcess.start();
		
		//trigger the computer's first move manually if needed
		if (mode == GameMode.SINGLE_PLAYER) triggerCompMove();
	}
	
	/**
	 * Manually trigger the computer's move.
	 * If it isn't the computer's turn, do nothing.
	 */
	protected void triggerCompMove() {
		//if the first turn goes to a computer participant, trigger his move here
		if (turnManager.is(Participant.COMPUTER)) {
			enableRandomButton(false);
			AITurn compTurn = new AITurn(turnManager, this);
			compTurn.thinkAndExecute();
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
		Cube cube = cubes[move.getRow()][move.getColumn()];
		
		switch(player) {
			case PLAYER_1: cube.updateHuman(move); break;
			case PLAYER_2:
			case COMPUTER: cube.updateOtherPlayer(move); break;
		}
	}
	
	/**
	 * Reset every move that took action in the current game,
	 * and start anew.
	 */
	public void restart() {
		/*
		//restart GameBoard components
		initGameBoardMoves();
		
		//restart cubes
		for (Cube[] cubeRow : cubes)
			for (Cube cube : cubeRow)
				cube.reset(this);
		
		//enable all components if they're stopped
		stop(false);
		
		//restart turns
		turnManager.set();
		*/
		
		Launcher.setState(window, getRelatedGame().getSubstate());
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
		
		//enable or disable all cubes
		for (Cube[] cubeRow : cubes)
			for (Cube cube : cubeRow)
				cube.enable(!flag);
		
		//enable or disable random button
		dice.enableFunction(!flag);
		dice.enableHover(!flag);
	}
	
	/**
	 * @return the center panel that the actual game takes place in
	 */
	public JPanel getGamePanel() { return panes[1]; }
	
	/**
	 * @param row - The cube's row in the matrix
	 * @param col - The cube's column in the matrix
	 * @return the cube in that position
	 */
	public Cube getCube(GameMove spot) { return cubes[spot.getRow()][spot.getColumn()]; }
	
	/**
	 * @return the game that this controller controls.
	 */
	public abstract ClientSideGame getRelatedGame();
	
	/**
	 * @return the compatible game's child class that extands Cube
	 */
	protected abstract Class<? extends Cube> getCubeChildClass();
	
	public VisibleProcessThread getVisibleProcess() { return visibleProcess; }
	
	private void initCubes() {
		Class<? extends Cube> cubeClass = getCubeChildClass();
		
		int rows = getRelatedGame().getBoardSize().height;
		int cols = getRelatedGame().getBoardSize().width;
		
		//define the cubes' size
		Dimension overallDim = Percentage.createDimension(panes[1].getPreferredSize(), 80, 80);
		Dimension cubeDim = new Dimension(overallDim.width / rows, overallDim.height / cols);
		
		//init cubes array
		this.cubes = new Cube[rows][cols];
		constraints.gridx = 0;
		constraints.gridy = 0;
		constraints.insets = new Insets(1, 1, 1, 1);
		
		//init every cube and place it
		for (int i = 0; i < rows; i++, constraints.gridy++, constraints.gridx = 0) {
			for (int j = 0; j < cols; j++, constraints.gridx++) {
				//create the cube using reflection
				try { cubes[i][j] = cubeClass.asSubclass(Cube.class).
					  getConstructor(int.class, int.class, TurnManager.class, ClientSideController.class).
					  newInstance(i, j, turnManager, this);
				}
				catch(Exception e) {
					System.err.println("Could not initialize cubes for " + getClass() + ".");
					e.printStackTrace();
				}
				
				cubes[i][j].setPreferredSize(cubeDim);
				getGamePanel().add(cubes[i][j], constraints);
			}
		}
	}
}