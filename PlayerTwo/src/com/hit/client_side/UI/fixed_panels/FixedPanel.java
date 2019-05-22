package com.hit.client_side.UI.fixed_panels;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridBagConstraints;
import javax.swing.JPanel;

import com.hit.client_side.UI.Window;
import com.hit.utility.math.Percentage;

public abstract class FixedPanel extends JPanel
{
	private static final long serialVersionUID = -763327169442617828L;
	public static final Color COLOR = new Color(127, 93, 128);
	
	protected GridBagConstraints constraints;
	
	public FixedPanel(Window window, double sizePercent) {
		super(new BorderLayout());
		
		setPreferredSize(Percentage.createDimension(window.getDimension(), 100, sizePercent));
		this.constraints = new GridBagConstraints();
	}
}