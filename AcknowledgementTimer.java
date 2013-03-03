import javax.swing.Timer;
import java.awt.event.*;
public class AcknowledgementTimer
{
	private Timer timer;
	private AcknowledgementTimerEventGenerator ateg;
	public AcknowledgementTimer ( int msec, SWE swe )
	{
		ateg = new AcknowledgementTimerEventGenerator (swe);
		timer = new Timer (msec, ateg);
	}
	
}