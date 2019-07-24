package com.hit.game_session_control.countdown;
import javax.swing.JLabel;
import javaNK.util.math.Range;

public class CountdownFacility extends JLabel
{
	private static final long serialVersionUID = -8179804105153999217L;
	
	protected int defCount, count;
	
	public CountdownFacility(Range<Integer> range) {
		this((int) range.generate());
	}
	
	public CountdownFacility(int count) {
		super("" + count);
		this.count = defCount = count;
	}
	
	public void incremenet() {
		count++;
		setText("" + count);
	}
	
	public void decremenet() {
		count--;
		setText("" + count);
	}
	
	public void reset() { count = defCount; }
	
	public void reset(Range<Integer> countdownTime) {
		count = defCount = (int) countdownTime.generate();
	}
	
	public boolean isOver() { return count == 0; }
	
	public int getCount() { return count; }
}