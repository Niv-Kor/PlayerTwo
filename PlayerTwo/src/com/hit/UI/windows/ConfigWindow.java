package com.hit.UI.windows;
import java.awt.Color;
import java.awt.Dimension;
import javax.swing.JFrame;

public class ConfigWindow extends Window
{
	private static final long serialVersionUID = 4546728318227374132L;
	private static final String NAME = "Player2 Games";
	public static final Dimension DIM = new Dimension(333, 700);
	
	public ConfigWindow() {
		super(NAME, DIM);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
	
	@Override
	public Color getColor() { return Window.COLOR; }
}