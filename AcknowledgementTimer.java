import javax.swing.Timer;
import java.awt.event.*;

/********************************************************************
	This class is used to implement the acknowledgement timer that
	is used to decide if a separate acknowledgement frame should be
	sent if there is no reverse traffic.
 *******************************************************************/


public class AcknowledgementTimer
{
	private Timer timer;
	private AcknowledgementTimerEventGenerator ateg;//the class to generate the associated timeout events for the SWE.
	public AcknowledgementTimer ( int msec, SWE swe )
	{
		ateg = new AcknowledgementTimerEventGenerator (swe);
		this.timer = new Timer (msec, ateg);
	}
	
	public void startTimer ( )
	{	
		if ( timer.isRunning() )
			//restarting if it is already running.
			timer.restart();
		else
			//if it is not running, starting it.
			timer.start();
	}
	
	public void stopTimer ( )
	{
		//stopping the timer
		timer.stop();
	}
}