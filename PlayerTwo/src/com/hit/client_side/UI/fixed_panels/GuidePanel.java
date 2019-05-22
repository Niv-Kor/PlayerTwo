package com.hit.client_side.UI.fixed_panels;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridBagLayout;
import java.util.concurrent.Callable;

import javax.swing.JLabel;
import javax.swing.JPanel;

import com.hit.client_side.UI.Window;
import com.hit.client_side.UI.launcher.Launcher;
import com.hit.client_side.UI.launcher.Launcher.Substate;
import com.hit.utility.OptionLabel;
import com.hit.utility.files.FontHandler;
import com.hit.utility.files.FontHandler.FontStyle;
import com.hit.utility.math.Percentage;

public class GuidePanel extends FixedPanel
{
	private static class NorthPanel extends JPanel
	{
		private static final long serialVersionUID = 2930008400989203718L;
		private static final double HEIGHT_PERCENT = 15;
		
		public NorthPanel(JPanel container) {
			setPreferredSize(Percentage.createDimension(container.getPreferredSize(), 100, HEIGHT_PERCENT));
		}
		
		@Override
		protected void paintComponent(Graphics g) {
			super.paintComponent(g);
			
			int bottomHeight = getPreferredSize().height;
			int rightWidth = getPreferredSize().width;
			
			g.setColor(Color.GRAY.brighter());
			g.drawLine(0, bottomHeight - 2, rightWidth, bottomHeight - 2);
		}
	}
	
	public static enum Flow {
		NEXT, BACK;
	}
	
	private static final long serialVersionUID = 863397519383394939L;
	private static final Font FONT = FontHandler.load("Comfortaa", FontStyle.PLAIN, 14);
	public static final double HEIGHT_PERCENT = 10;
	
	private OptionLabel back, next;
	private JPanel mainPanel;
	private JLabel delimeter;
	
	public GuidePanel(Window window) {
		super(window, HEIGHT_PERCENT);
		
		NorthPanel northAssist = new NorthPanel(this);
		northAssist.setBackground(window.getColor());
		add(northAssist, BorderLayout.NORTH);
		
		this.mainPanel = new JPanel(new GridBagLayout());
		mainPanel.setPreferredSize(Percentage.createDimension(getPreferredSize(), 100, 100 - NorthPanel.HEIGHT_PERCENT));
		mainPanel.setBackground(COLOR);
		add(mainPanel, BorderLayout.CENTER);
		
		this.back = new OptionLabel("BACK");
		back.setForeground(Color.WHITE);
		back.setFont(FONT);
		back.enableSelectionColor(false);
		
		this.delimeter = new JLabel(" | ");
		delimeter.setForeground(Color.WHITE.darker());
		delimeter.setFont(new Font(FONT.getFontName(), Font.PLAIN, FONT.getSize() + 3));
		
		this.next = new OptionLabel("NEXT");
		next.setForeground(Color.WHITE);
		next.setFont(FONT);
		next.enableSelectionColor(false);
		
		enableBack(true);
		enableNext(true);
	}
	
	/**
	 * Set the target of a button.
	 * @param flow - The button to edit
	 * @param window - The window that contains the buttons
	 * @param landingState - The state to land at as a target
	 */
	public void setTarget(Flow flow, Window window, Substate landingState) {
		OptionLabel label = null;
		
		switch(flow) {
			case BACK: label = back; break;
			case NEXT: label = next; break;
			default: return;
		}
		
		label.setFunction(new Callable<Void>() {
			public Void call() throws Exception {
				Launcher.setState(window, landingState);
				return null;
			}
		});
	}
	
	/**
	 * @param flow - The button
	 * @return the button as an OptionLabel object
	 */
	public OptionLabel getLabel(Flow flow) {
		switch(flow) {
			case BACK: return back;
			case NEXT: return next;
			default: return null;
		}
	}
	
	/**
	 * Enable or disable the functionality of a button.
	 * @param flow - The button
	 * @param flag - True to enable or false to disable
	 */
	public void enable(Flow flow, boolean flag) {
		switch(flow) {
			case BACK: enableBack(flag); break;
			case NEXT: enableNext(flag); break;
		}
	}
	
	public void rename(Flow flow, String name) {
		switch(flow) {
			case BACK: back.setText(name); break;
			case NEXT: next.setText(name); break;
		}
	}
	
	private void enableBack(boolean flag) {
		if (flag) {
			constraints.gridx = 0;
			mainPanel.add(back, constraints);
			
			constraints.gridx = 1;
			mainPanel.add(delimeter, constraints);
		}
		else {
			mainPanel.remove(back);
			mainPanel.remove(delimeter);
		}
	}
	
	private void enableNext(boolean flag) {
		if (flag) {
			constraints.gridx = 1;
			mainPanel.add(delimeter, constraints);
			
			constraints.gridx = 2;
			mainPanel.add(next, constraints);
		}
		else {
			mainPanel.remove(next);
			mainPanel.remove(delimeter);
		}
	}
}