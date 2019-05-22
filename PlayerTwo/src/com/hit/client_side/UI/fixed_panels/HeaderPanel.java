package com.hit.client_side.UI.fixed_panels;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JLabel;
import javax.swing.JPanel;

import com.hit.client_side.UI.Window;
import com.hit.utility.files.FontHandler;
import com.hit.utility.files.FontHandler.FontStyle;
import com.hit.utility.math.Percentage;

public class HeaderPanel extends FixedPanel
{
	private static class SouthPanel extends JPanel
	{
		private static final long serialVersionUID = 2930008400989203718L;
		private static final double HEIGHT_PERCENT = 15;
		
		public SouthPanel(JPanel container) {
			setPreferredSize(Percentage.createDimension(container.getPreferredSize(), 100, HEIGHT_PERCENT));
		}
		
		@Override
		protected void paintComponent(Graphics g) {
			super.paintComponent(g);
			
			g.setColor(Color.GRAY.brighter());
			g.drawLine(0, 1, getPreferredSize().width, 1);
		}
	}
	
	private static final long serialVersionUID = -1247312216293213191L;
	protected static final Font FONT = FontHandler.load("GloriaHallelujah", FontStyle.PLAIN, 23);
	private static final int MAX_LINES = 2;
	public static final double HEIGHT_PERCENT = 15;
	
	private JPanel mainPanel;
	private Color foreground;
	
	public HeaderPanel(Window window) {
		super(window, HEIGHT_PERCENT);
		setBackground(Color.ORANGE);
		
		constraints.insets = new Insets(0, 5, 0, 5);
		constraints.gridy = 0;
		
		this.foreground = Color.WHITE;
		
		this.mainPanel = new JPanel(new GridBagLayout());
		mainPanel.setPreferredSize(Percentage.createDimension(getPreferredSize(), 100, 100 - SouthPanel.HEIGHT_PERCENT));
		mainPanel.setBackground(COLOR);
		add(mainPanel, BorderLayout.CENTER);
		
		SouthPanel southAssist = new SouthPanel(this);
		southAssist.setBackground(window.getColor());
		add(southAssist, BorderLayout.SOUTH);
	}
	
	public void addTitleLine(String title) {
		if (constraints.gridy == MAX_LINES) return;
		
		JLabel titleLab = new JLabel(title);
		titleLab.setForeground(foreground);
		titleLab.setFont(FONT);
		
		mainPanel.add(titleLab, constraints);
		
		//get ready for the next line
		constraints.insets.top = -13;
		constraints.gridy++;
	}
	
	/**
	 * @param color - The new color
	 */
	public void setForeground(Color color) { foreground = color; }
}