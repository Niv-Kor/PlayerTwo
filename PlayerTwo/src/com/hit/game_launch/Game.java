package com.hit.game_launch;
import java.awt.Color;
import java.awt.Dimension;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import com.hit.game_launch.Launcher.Substate;
import com.hit.players.Participant;
import javaNK.util.files.FileLoader;
import javaNK.util.math.Range;

/**
 * Enum that contains all the games.
 * Every game has its compatible state is 'Substate'.
 * Use this enum to tell Launcher which game you want it to refer to.
 * 
 * @author Niv Kor
 */
public enum Game {
	TIC_TAC_TOE(Substate.TIC_TAC_TOE, new Dimension(500, 700),
				new Range<Double>(0.8, 2.2), new Dimension(3, 3),
				new Color(73, 144, 137), new Range<Integer>(10, 10), true),
				
	CATCH_THE_BUNNY(Substate.CATCH_THE_BUNNY, new Dimension(500, 700),
					new Range<Double>(0.03, 0.15), new Dimension(9, 9),
					new Color(239, 142, 127), new Range<Integer>(15, 40), false);
	
	public static enum GameMode {
		SINGLE_PLAYER, MULTIPLAYER;
	}
	
	private Substate substate;
	private String formalName;
	private List<String> gameRules;
	private Range<Double> responseTime;
	private Range<Integer> countdownTime;
	private Dimension windowDim, boardSize;
	private Color color;
	private boolean running;
	private int participantsAmount;
	private boolean addedSigns;
	private GameMode gameMode;
	private Participant firstTurnParticipant;
	
	/**
	 * @param substate - The controller state of the game
	 * @param dim - Dimension of the game window
	 * @param responseTime - Time range in seconds within which the computer has to make a move
	 * @param boardSize - Size of the game board (rows X columns)
	 * @param color - Color theme of the game
	 */
	private Game(Substate substate, Dimension dim, Range<Double> responseTime,
				 Dimension boardSize, Color color, Range<Integer> countdownTime, boolean addedSigns) {
		
		this.substate = substate;
		this.formalName = generateFormalName();
		this.windowDim = new Dimension(dim);
		this.boardSize = new Dimension(boardSize);
		this.responseTime = responseTime;
		this.countdownTime = countdownTime;
		this.running = false;
		this.addedSigns = addedSigns;
		this.gameRules = readRulesFromFile();
		this.gameMode = GameMode.SINGLE_PLAYER; //temporary
		this.color = color;
	}
	
	/**
	 * Read the "rules.txt" that exists for each game in resources folder.
	 * 
	 * @return a list of String objects that each of them represents a line in the file.
	 */
	private List<String> readRulesFromFile() {
		List<String> lines = new ArrayList<String>();
		
		try { 
			String path = FileLoader.HEADER_PATH + "games/" + formalName + "/rules.txt";
	    	FileInputStream inputStream = new FileInputStream(path);
	    	DataInputStream dataInput = new DataInputStream(inputStream); 
	    	BufferedReader buffer = new BufferedReader(new InputStreamReader(dataInput)); 
	    	String currentLine; 
	    	
	    	//input to list, line by line
	    	while ((currentLine = buffer.readLine()) != null) {
	    		currentLine = currentLine.trim(); 
	    		lines.add(currentLine);
	    	}
	    	
	    	buffer.close();
	    }
		catch (IOException e) {
			System.err.println(formalName + "'s game rules file is missing or currupted!");
		}
		
		return lines;
	}
	
	/**
	 * Create the formal name of the game.
	 * This method is not very efficient to be used normally,
	 * so it's only used once to initialize 'formalName' variable.
	 * 
	 * @return a formal name of the game. (Ex. input: 'TIC_TAC_TOE'; output: 'Tic Tac Toe').
	 */
	private String generateFormalName() {
		String[] parts = name().split("_");
		String temp = "";
		
		for (int i = 0; i < parts.length; i++) {
			//first letter in uppercase and rest in lowercase
			String first = "" + Character.toUpperCase(parts[i].charAt(0));
			parts[i] = first + parts[i].substring(1, parts[i].length()).toLowerCase();
			
			//add to temp
			temp = temp.concat(parts[i]);
			if (i < parts.length - 1) temp = temp + " ";
		}
		
		return temp;
	}
	
	/**
	 * @return true if the game adds signs to board, or false if it moves them around. 
	 */
	public boolean areSignsAdded() { return addedSigns; }
	
	/**
	 * Get the computer's response time range in seconds,
	 * that generates a random number for each of its turn.
	 * 
	 * @return the computer's response time range in seconds.
	 */
	public Range<Double> getResponseTime() { return responseTime; }
	
	/**
	 * @return the countdown time for every player's turn.
	 */
	public Range<Integer> getCountdownTime() { return countdownTime; }
	
	/**
	 * Activate or deactivate a game.
	 * Can only be accessed from within Launcher.
	 * 
	 * @param flag - True to activate or false to deactivate
	 */
	void run(boolean flag) { running = flag; }
	
	/**
	 * @param p - The player to get the first turn in the upcoming session
	 */
	public void setFirstTurnParticipant(Participant p) { firstTurnParticipant = p; }
	
	/**
	 * @return the player that gets the first turn in the upcoming session.
	 */
	public Participant getFirstTurnParticipant() { return firstTurnParticipant; }
	
	/**
	 * @param mode - The game mode chosen for this game's session
	 */
	public void setGameMode(GameMode mode) { gameMode = mode; }
	
	/**
	 * @return the game mode chosen for this game's session.
	 */
	public GameMode getGameMode() { return gameMode; }
	
	/**
	 * @return true if the game is running now or false otherwise.
	 */
	public boolean isRunning() { return running; }
	
	/**
	 * @return the recommended window dimensions for this game.
	 */
	public Dimension getWindowDimension() { return windowDim; }
	
	/**
	 * @return the amount of rows and column the game uses.
	 */
	public Dimension getBoardSize() { return boardSize; }
	
	/**
	 * @return a formal name of the game. (Ex. input: 'TIC_TAC_TOE'; output: 'Tic Tac Toe').
	 */
	public String formalName() { return formalName; }
	
	/**
	 * @return the compatible Substate enum constant for the game.
	 */
	public Substate getSubstate() { return substate; }
	
	/**
	 * @return a list of String objects that each of them represents a line of the game's rules.
	 */
	public List<String> getGameRules(){ return gameRules; }
	
	/**
	 * @return the amount of players needed to start a game.
	 */
	public int getParticipantsAmount() { return participantsAmount; }
	
	/**
	 * @return the theme color of the game.
	 */
	public Color getColorTheme() { return color; }
}