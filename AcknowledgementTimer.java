import javax.swing.Timer;
import java.awt.event.*;
public class AcknowledgementTimer
{
	private Timer timer;
	private AcknowledgementTimerEventGenerator ateg;
	public AcknowledgementTimer ( int msec, SWE swe )
	{
		ateg = new AcknowledgementTimerEventGenerator (swe);
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