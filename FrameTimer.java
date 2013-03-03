import javax.swing.Timer;
import java.awt.event.*;
public class FrameTimer
{
	private Timer timer;
	private FrameTimerEventGenerator fteg;
	public FrameTimer ( int msec, SWE swe, int seqNr )
	{
		fteg = new FrameTimerEventGenerator (swe, seqNr);
		this.timer = new Timer (msec, ateg);
	}
	
	public void startTimer ( int seqNr )
	{	//verify this.
		
		fteg.setSeqNr (seqNr);
		
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