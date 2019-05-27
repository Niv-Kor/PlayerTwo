package com.hit.UI.windows;
import java.awt.Dimension;
import javax.swing.JPanel;

import com.hit.UI.states.State;

public interface Mutable
{
	/**
	 * Insert a panel to the requested locaion in the window.
	 * The 'location' parameter will accept a BorderLayout constant if the panel uses 'BorderLayout' layout,
	 * or a GridBagConstraints object if the panel uses 'GridBagLayout' layout.
	 * @param panel - The panel to insert
	 * @param location - Where should it be inserted
	 */
	void insertPanel(JPanel panel, Object location);
	
	/**
	 * Remove a panel from the window.
	 * @param panel - The panel to remove
	 */
	void removePanel(JPanel panel);
	
	/**
	 * Change the state in the window.
	 * @param currentState - The current state to remove
	 * @param newState - The new state to apply
	 */
	void applyState(State currentState, State newState);
	
	/**
	 * @return dimensions of the window
	 */
	Dimension getDimension();
}