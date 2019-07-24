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
import com.hit.game_session_control.countdown.CountdownFacility;
import com.hit.players.Participant;
import game_algo.GameBoard.GameMove;
import javaNK.util.GUI.swing.components.InteractiveIcon;
import javaNK.util.math.DimensionalHandler;

public abstract class GameView extends State
{
	protected InteractiveIcon dice;
	protected VSPanel vsPanel;
	protected GridBagConstraints constraints;
	protected GameController controller;
	protected CountdownFacility countdown;
	protected BoardCell[][] cells;
	
	/**
	 * @see State(Window)
	 * @throws IOException when one or more of the operating protocols fail to connect.
	 */
	public GameView(Window window) throws IOException {
		super(window, 3);
		this.constraints = new GridBagConstraints();
		
		//players panel (panes[0])
		createPanel(new BorderLayout(),
				    DimensionalHandler.adjust(window.getDimension(), 100, 18),
				    new Color(55, 55, 55));
		
		//game panel (panes[1])
		createPanel(new GridBagLayout(),
					DimensionalHandler.adjust(window.getDimension(), 100, 75),
					getRelatedGame().getColorTheme());
		
		//random selection panel (panes[2])
		createPanel(new GridBagLayout(),
					DimensionalHandler.adjust(window.getDimension(), 100, 7),
					getRelatedGame().getColorTheme());
		
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
		panes[0].add(vsPanel, BorderLayout.CENTER);
		
		//build south panel
		//countdown facility
		this.countdown = createCountdownFacility();
		countdown.setFont(LABEL_FONT.deriveFont((float) 50));
		countdown.setForeground(Color.WHITE);
		
		constraints.gridx = 0;
		constraints.gridy = 0;
		constraints.insets = new Insets(-5, -250, 5, 5);
		panes[2].add(countdown, constraints);
		
		//dice icon
		this.dice = new InteractiveIcon("miscellaneous/dice.png");
		dice.setHoverIcon("miscellaneous/dice_hover.png");
		dice.setFunction(new Callable<Void>() {
			@Override
			public Void call() throws Exception {
				controller.randomPlayerMove();
				return null;
			}
		});
		
		constraints.gridx = 1;
		constraints.insets = new Insets(-5, 5, 5, 5);
		panes[2].add(dice, constraints);
		
		//'random' label next to dice
		JLabel randomBtn = new JLabel("Random!");
		randomBtn.setForeground(Color.WHITE);
		randomBtn.setFont(LABEL_FONT);
		
		constraints.gridx = 2;
		constraints.insets.top = 5;
		constraints.insets.left = 5;
		panes[2].add(randomBtn, constraints);
		
		//establish controller
		this.controller = new GameController(this, participants);
		
		//initiate all cells of the correct game
		initCells();
		
		//initiate crucial parts before the beginning of a game
		init();
		initPositions();
		
		//trigger the computer's first move manually if needed
		//if (mode == GameMode.SINGLE_PLAYER) controller.triggerCompMove();
	}
	
	/**
	 * Enable or disable the random move button.
	 * 
	 * @param flag - True to enable or false to disable
	 */
	public void enableRandomButton(boolean flag) {
		dice.enableHover(flag);
		dice.enableFunction(flag);
	}
	
	/**
	 * Update every component that needs to be changed for the random move.
	 * 
	 * @param move - The random move that should take place
	 * @param player - Which player made that move
	 */
	public void updateRandomMove(GameMove move, Participant player) {
		BoardCell cell = cells[move.getRow()][move.getColumn()];
		
		switch(player) {
			case PLAYER_1: cell.updateHuman(); break;
			case PLAYER_2:
			case COMPUTER: cell.updateOtherPlayer(); break;
		}
	}
	
	/**
	 * Restart the game.
	 */
	public void restart() {
		stop(false);
		controller.restart();
		
		//erase all cells
		for (BoardCell[] cellRow : cells)
			for (BoardCell cell : cellRow)
				cell.erase();
		
		initPositions();
	}
	
	/**
	 * Close the controller permanently.
	 */
	public void close() {
		stop(true);
		controller.close();
		vsPanel.close();
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
	 * 
	 * @param flag - True to stop the game or false to continue
	 */
	public void stop(boolean flag) {
		controller.stop(flag);
		vsPanel.stop(flag);
		
		//enable or disable all cells
		for (BoardCell[] cellRow : cells)
			for (BoardCell cell : cellRow)
				cell.enable(!flag);
		
		enableRandomButton(!flag);
	}
	
	/**
	 * @return the center panel that the actual game takes place in
	 */
	public JPanel getGamePanel() { return panes[1]; }
	
	/**
	 * @return the panel of the currently playing clients.
	 */
	public VSPanel getVSPanel() { return vsPanel; }
	
	/**
	 * @param row - The cell's row in the matrix
	 * @param col - The cell's column in the matrix
	 * @return the cell in that position
	 */
	public BoardCell getCell(GameMove spot) { return cells[spot.getRow()][spot.getColumn()]; }
	
	public GameController getController() { return controller; }
	
	/**
	 * @return the game that this controller controls.
	 */
	public abstract Game getRelatedGame();
	
	/**
	 * @return the compatible game's child class that extends BoardCell.
	 */
	protected abstract Class<? extends BoardCell> getCellChildClass();
	
	protected abstract CountdownFacility createCountdownFacility();
	
	protected CountdownFacility getCountdownFacility() { return countdown; }
	
	/**
	 * Initiate class members.
	 */
	protected abstract void init();
	
	/**
	 * Initiate the position of the player at the start of the game randomly.
	 * If played at a single-player mode, the same thing goes with the computer player.
	 */
	protected abstract void initPositions();
	
	private void initCells() {
		Class<? extends BoardCell> cellClass = getCellChildClass();
		
		int rows = getRelatedGame().getBoardSize().height;
		int cols = getRelatedGame().getBoardSize().width;
		
		//define the cells' size
		Dimension overallDim = DimensionalHandler.adjust(panes[1].getPreferredSize(), 80, 80);
		Dimension cellDim = new Dimension(overallDim.width / rows, overallDim.height / cols);
		
		//initiate cells array
		this.cells = new BoardCell[rows][cols];
		constraints.gridx = 0;
		constraints.gridy = 0;
		constraints.insets = new Insets(1, 1, 1, 1);
		
		//initiate every cell and place it
		for (int i = 0; i < rows; i++, constraints.gridy++, constraints.gridx = 0) {
			for (int j = 0; j < cols; j++, constraints.gridx++) {
				//create the cell using reflection
				try { cells[i][j] = cellClass.asSubclass(BoardCell.class).
					  getConstructor(int.class, int.class, GameController.class).
					  newInstance(i, j, controller);
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