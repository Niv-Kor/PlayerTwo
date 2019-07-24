package com.hit.game_session_control.countdown;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import javaNK.util.math.Range;
import javaNK.util.threads.DiligentThread;

public class CountdownClock extends CountdownFacility
{
	private static class Ticker extends DiligentThread
	{
		private CountdownClock clock;
		
		public Ticker(CountdownClock clock) {
			super(Long.MAX_VALUE);
			this.clock = clock;
		}
		
		@Override
		protected void diligentFunction() throws Exception {
			if (!clock.isOver()) clock.decremenet();
			else clock.announce();
		}
	}
	
	private static final long serialVersionUID = 8779173633924715948L;
	
	private PropertyChangeSupport propertyChange;
	private Ticker ticker;
	
	public CountdownClock(Range<Integer> range) {
		super(range);
	}
	
	public CountdownClock(int count) {
		super(count);
		this.ticker = new Ticker(this);
		this.propertyChange = new PropertyChangeSupport(this);
		ticker.start();
		ticker.pause(true);
		ticker.setRestTime(1);
	}
	
	private void announce() {
		propertyChange.firePropertyChange("clock over", false, true);
	}
	
	public void subscribePropertyChange(PropertyChangeListener listener) {
		propertyChange.addPropertyChangeListener(listener);
	}
	
	public void run() {
		reset();
		ticker.pause(false);
	}
	
	public void pause() {
		reset();
		ticker.pause(true);
	}
	
	public void stop() {
		ticker.kill();
	}

	public void hide(boolean flag) {
		if (flag) setText("-");
		else setText("" + count);
	}
}