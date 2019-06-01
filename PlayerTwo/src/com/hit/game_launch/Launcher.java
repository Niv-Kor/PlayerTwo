package com.hit.game_launch;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import com.hit.UI.states.EndgameMessage;
import com.hit.UI.states.GameModeState;
import com.hit.UI.states.GamePickerState;
import com.hit.UI.states.IdentificationState;
import com.hit.UI.states.State;
import com.hit.UI.windows.ConfigWindow;
import com.hit.UI.windows.GameWindow;
import com.hit.UI.windows.Mutable;
import com.hit.UI.windows.Window;
import com.hit.game_launch.Game.GameMode;
import com.hit.game_session_control.Controller;
import com.hit.game_session_control.catch_the_bunny.CatchTheBunnyController;
import com.hit.game_session_control.tic_tac_toe.TicTacToeController;
import com.hit.networking.ClientProtocol;
import com.hit.players.Participant;
import javaNK.util.networking.JSON;

public class Launcher {
	/**
	 * Enum of specific states.
	 * Use this enum to tell Launcher which state to set and where.
	 * @author Niv Kor
	 */
	public static enum Substate {
		GAME_PICKER(GamePickerState.class),
		IDENTIFICATION(IdentificationState.class),
		GAME_MODE(GameModeState.class),
		TIC_TAC_TOE(TicTacToeController.class),
		CATCH_THE_BUNNY(CatchTheBunnyController.class),
		ENDGAME_MESSGE(EndgameMessage.class);
		
		private Class<? extends State> stateClass;
		
		private Substate(Class<? extends State> c) {
			this.stateClass = c;
		}
		
		/**
		 * Create an instance of the state.
		 * Every State instance needs a mutable window where it has the room to strech,
		 * and cannot exist without one.
		 * A state can take place in more than one window simultaneously.
		 * @param window - The window the will contain the state instance
		 * @return an instance of the state that fits the size of the argument window.
		 */
		public State createInstance(Mutable window) {
			//create instance
			try { return stateClass.asSubclass(State.class).getConstructor(Window.class).newInstance(window); }
			catch (Exception e) {
				System.err.println("Cannot create an instance of class " + stateClass.getName());
				
				//initialize the state in a different approach for debugging purposes
				try { printStateStackTrace(stateClass); }
				catch(Exception ex) { ex.printStackTrace(); }
			}
			
			return null;
		}
		
		/**
		 * @return a reflection of the Substate's compatible class.
		 */
		public Class<? extends State> getStateClass() { return stateClass; }
		
		private static void printStateStackTrace(Class<? extends State> stateClass) throws Exception {
			Window testWindow = new ConfigWindow();
			
			switch(stateClass.getSimpleName()) {
				case "GamePickerState": new GamePickerState(testWindow); break;
				case "IdentificationState": new IdentificationState(testWindow); break;
				case "GameModeState": new GameModeState(testWindow); break;
				case "TicTacToeController": new TicTacToeController(testWindow); break;
				case "CatchTheBunnyController": new CatchTheBunnyController(testWindow); break;
				case "EndgameMessage": new TicTacToeController(testWindow); break;
			}
		}
	}
	
	/**
	 * A way to save both the window and the state that's running on it, and easily find it.
	 * WindowCache should be created every time a state is applied to any window,
	 * and should be deleted every time the state on that window is changed,
	 * or perhaps when that window is closed. 
	 * @author Niv Kor
	 */
	public static class WindowCache
	{
		private Mutable window;
		private State currentState;
		
		public WindowCache(Mutable w) {
			this.window = w;
		}
		
		public Mutable getWindow() { return window; }
		public State getCurrentState() { return currentState; }
		public void setCurrentState(State s) { currentState = s; }
	}
	
	private static List<WindowCache> windowCacheList;
	
	public static void init() throws IOException {
		windowCacheList = new ArrayList<WindowCache>();
	}
	
	/**
	 * Set a state on a window.
	 * @param window - The window that needs to contain the state
	 * @param substate - The requested state to set
	 */
	public static void setState(Mutable window, Substate substate) {
		if (substate == null) return;
		
		State instance = substate.createInstance(window);
		
		//find the correct window cache
		WindowCache tempCache = null;
		for (WindowCache wm : windowCacheList)
			if (wm.getWindow() == window) tempCache = wm;
		
		//create a new window cache if couldn't find it
		if (tempCache == null) {
			tempCache = new WindowCache(window);
			windowCacheList.add(tempCache);
		}
		
		//apply state to window and update its window memory
		window.applyState(tempCache.getCurrentState(), instance);
		tempCache.setCurrentState(instance);
	}
	
	/**
	 * Launch a game on a new window.
	 * @param game - The game to launch
	 */
	public static void launchGame(Game game, GameMode mode) {
		if (game.isRunning()) return;
		
		try {
			//connect to the server
			Participant.PLAYER_1.getStatus().getProtocol().connectServer(game);
			
			//if client wants to play as single player mode, connect his computer
			if (mode == GameMode.SINGLE_PLAYER)
				Participant.COMPUTER.getStatus().getProtocol().connectServer(game);
			
			//otherwise, wait for another player to connect to the server
			//wait for a signal from server that the game can be started
			ClientProtocol player1Prot = Participant.PLAYER_1.getStatus().getProtocol();
			
			String[] requests = {"start_game"};
			JSON receivedMsg = player1Prot.waitFor(requests);
			System.out.println("in launcher its " + receivedMsg);
			
			//continue only if the correct game is starting
			if (receivedMsg.getString("game").equals(game.name())) {
				boolean firstTurn = receivedMsg.getBoolean("turn");
				if (firstTurn) game.setFirstTurnParticipant(Participant.PLAYER_1);
				else {
					switch(mode) {
						case SINGLE_PLAYER: game.setFirstTurnParticipant(Participant.COMPUTER); break;
						case MULTIPLAYER: game.setFirstTurnParticipant(Participant.PLAYER_2); break;
					}
				}
			}
		}
		catch (IOException e) { e.printStackTrace(); }
		
		game.run(true);
		game.setGameMode(mode);
		GameWindow window = new GameWindow(game);
		setState(window, game.getSubstate());
	}
	
	/**
	 * Get the cache that contains a currently open window with some state.
	 * @param window - Currently open window to get its cache
	 * @return cache of that window.
	 */
	public static WindowCache getWindowCache(Window window) {
		for (WindowCache wc : windowCacheList)
			if (wc.getWindow() == window) return wc;
		
		return null;
	}
	
	/**
	 * Get the cache that contains a currently open window with a running game in it.
	 * @param game - Running game to get its window's cache
	 * @return cache of that game's open window. null if the game is currently not running.
	 */
	public static WindowCache getWindowCache(Game game) {
		State temp;
		
		for (WindowCache wc : windowCacheList) {
			temp = wc.getCurrentState();
			if (temp.getClass() == game.getSubstate().getStateClass())
				return wc;
		}
		
		return null;
	}
	
	/**
	 * Get controller of a game that's currently running.
	 * @param game - Running game to get its controller
	 * @return controller of that game. null if the game is currently not running.
	 */
	public static Controller getRunningGameController(Game game) {
		WindowCache cache = getWindowCache(game);
		
		if (cache != null) return (Controller) cache.currentState;
		else return null;
	}
	
	/**
	 * Close the game's window and every reference to it.
	 * After closing, the game is no longer considered running.
	 * @param game - The game to close
	 */
	public static void closeGame(Game game) {
		WindowCache cache = getWindowCache(game);
		GameWindow gameWindow = (GameWindow) cache.getWindow();
		gameWindow.dispose();
		windowCacheList.remove(cache);
		
		//disconnect
		try {
			//disconnect player from the game's server
			Participant.PLAYER_1.getStatus().getProtocol().disconnectServer(game);
			
			//if needed, disconnect the computer player too
			if (game.getGameMode() == GameMode.SINGLE_PLAYER) 
				Participant.COMPUTER.getStatus().getProtocol().disconnectServer(game);
		}
		catch(IOException e) { e.printStackTrace(); }
		
		game.run(false);
	}
	
	public static void restartGame(Game game) {
		if (!game.isRunning()) return;
		
		try {
			switch(game.getGameMode()) {
				case SINGLE_PLAYER: {
					ClientProtocol playerProt = Participant.PLAYER_1.getStatus().getProtocol();
					playerProt.renewGame(game);
					break;
				}
				case MULTIPLAYER: {
					closeGame(game);
					launchGame(game, GameMode.MULTIPLAYER);
					break;
				}
			}
		}
		catch(IOException e) { e.printStackTrace(); }
	}
	
	/**
	 * Test all the states for debugging purposes.
	 * If a state can't be ran correctly, a stack trace will be printed.
	 * This test is needed because when trying to create an instance
	 * of a state using reflection (using Substate.createInstance()),
	 * the stack trace that's printed is not related to the real bugs in the state,
	 * but rather to the failed reflection action. 
	 */
	@Test
	public void launchingTest() {
		for (Substate substate : Substate.values()) {
			try { Substate.printStateStackTrace(substate.getStateClass()); }
			catch(Exception e) {
				e.printStackTrace();
				Assert.fail();
			}
		}
	}
}