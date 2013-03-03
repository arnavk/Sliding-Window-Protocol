import javax.swing.Timer;
import java.awt.event.*;
public class FrameTimer
{
	private Timer timer;
	private FrameTimerEventGenerator fteg;
	public FrameTimer ( int msec, SWE swe )
	{
		fteg = new FrameTimerEventGenerator (swe);
		this.timer = new Timer (msec, ateg);
	}
	
	public void startTimer ( )
	{	//verify this.
		if ( timer.isRunning() )
			timer.restart();
		else
			timer.start();
	}
	
	public void stopTimer ( )
	{
		timer.stop();
	}
}