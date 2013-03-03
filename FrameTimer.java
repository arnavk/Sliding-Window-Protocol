import javax.swing.Timer;
import java.awt.event.*;

/***********************************************************************
	This class is used to implement the timer associated with each
	frame that is sent so that it can decide if retransmission is required.
 **********************************************************************/

public class FrameTimer
{
	private Timer timer;
	private FrameTimerEventGenerator fteg;//the class to generate the associated timeout events for the SWE.
	public FrameTimer ( int msec, SWE swe, int seqNr )
	{	//constructor
		fteg = new FrameTimerEventGenerator (swe, seqNr);
		this.timer = new Timer (msec, fteg);
	}
	
	public void startTimer ( int seqNr )
	{	
		//starts a timer with the associated sequence number
		
		fteg.setSeqNr (seqNr); // setting (or resetting) the sequnce number associated.
		
		if ( timer.isRunning() )
			//restarting if it is already running.
			timer.restart();
		else
			//if it is not running, starting it.
			timer.start();
	}
	
	public void stopTimer ( )
	{
		//to stop the timer
		timer.stop();
	}
}