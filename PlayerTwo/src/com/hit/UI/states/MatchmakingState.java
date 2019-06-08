package com.hit.UI.states;
import java.awt.BorderLayout;
import com.hit.UI.windows.Window;

public class MatchmakingState extends State
{
	public MatchmakingState(Window window) {
		super(window, 3);
	}

	@Override
	public void insertPanels() {
		window.add(panes[0], BorderLayout.CENTER);
	}
}
