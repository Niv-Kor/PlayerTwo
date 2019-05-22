package com.hit.client_side.UI.states;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.LayoutManager;

import javax.swing.JPanel;

import com.hit.client_side.UI.Window;
import com.hit.utility.files.FontHandler;
import com.hit.utility.files.FontHandler.FontStyle;

public abstract class State
{
	public static final Font LABEL_FONT = FontHandler.load("Comfortaa", FontStyle.PLAIN, 17);
	
	protected JPanel[] panes;
	private int paneIndex;
	protected Window window;
	
	public State(Window window, int panesAmount) {
		this.panes = new JPanel[panesAmount];
		this.paneIndex = 0;
		this.window = window;
	}
	
	/**
	 * Create a panel of the state.
	 * @param layout - The panel's layout model
	 * @param dim - The dimensions of the panel
	 * @param color - The panel's background color (if null, panel is transparent)
	 */
	protected void createPanel(LayoutManager layout, Dimension dim, Color color) {
		panes[paneIndex] = new JPanel(layout);
		panes[paneIndex].setPreferredSize(dim);

		if (color != null) panes[paneIndex].setBackground(color);
		else panes[paneIndex].setOpaque(false);
		
		paneIndex++;
	}
	
	/**
	 * Insert the state's panels to the window that contains it.
	 * This method should normally be called by the window itself.
	 */
	public abstract void insertPanels();
	
	/**
	 * @return an array of the state's JPanels.
	 */
	public JPanel[] getPanes() { return panes; }
	
	/**
	 * @return the window this state is applied on.
	 */
	public Window getWindow() { return window; }
}