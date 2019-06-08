package com.hit.UI.windows;
import java.awt.Color;
import java.awt.Dimension;
import com.hit.UI.fixed_panels.FixedPanel;

public class Matchmaking extends Window
{
	private static final long serialVersionUID = 2163641360410863619L;
	private static final Dimension DIM = new Dimension(500, 400);

	public Matchmaking() {
		super("", DIM);
		setAlwaysOnTop(true);
	}

	@Override
	public Color getColor() { return FixedPanel.COLOR; }
}