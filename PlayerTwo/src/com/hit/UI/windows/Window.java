package com.hit.UI.windows;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import javax.swing.JFrame;
import javax.swing.JPanel;

import com.hit.UI.states.State;

public abstract class Window extends JFrame implements Mutable
{
	private static final long serialVersionUID = 8576706402811297251L;
	public static final Color COLOR = new Color(80, 80, 80);
	
	protected JPanel mainPanel;
	
	public Window(String title, Dimension dim) {
		super(title);
		setResizable(false);
		setSize(dim);
		
		//panel
		this.mainPanel = new JPanel(new BorderLayout());
		mainPanel.setPreferredSize(dim);
		mainPanel.setBackground(getColor());
		
		add(mainPanel);
		setVisible(true);
		pack();
		setLocationRelativeTo(null);
	}
	
	@Override
	public void insertPanel(JPanel panel, Object location) {
		try { mainPanel.add(panel, (String) location); }
		catch(IllegalArgumentException | ClassCastException e) {
			System.err.println("The location argument (\"" + location + "\") must be a BorderLayout constant.");
		}
	}
	
	@Override
	public void removePanel(JPanel panel) {
		mainPanel.remove(panel);
	}
	
	@Override
	public void applyState(State currentState, State newState) {
		//remove previous state if needed
		if (currentState != null) {
			JPanel[] oldPanes = currentState.getPanes();
			
			for (int i = 0; i < oldPanes.length; i++)
				removePanel(oldPanes[i]);
		}
		
		//apply new state
		newState.insertPanels();
		setVisible(true);
	}
	
	@Override
	public Dimension getDimension() { return mainPanel.getPreferredSize(); }
	
	/**
	 * @return the background color of the window.
	 */
	public abstract Color getColor();
}