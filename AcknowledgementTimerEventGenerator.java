import java.awt.event.*;

/*********************************************
	This class generates time-out events
	for AcknowledgementTimer
 *********************************************/
public class AcknowledgementTimerEventGenerator implements ActionListener
{
	//a reference to the environment to generate the acktimeout_event
	private SWE swe; 
	
	public AcknowledgementTimerEventGenerator ( SWE swe )
	{	//constructor

		this.swe = swe;
	}
	
	//accessibilty methods - setter and getter.
	public void setSWE ( SWE swe )
	{
		this.swe = swe;
	}
	
	public SWE getSWE ()
	{
		return this.swe;
	}
	
	public void actionPerformed ( ActionEvent e )
	{
		//notifying swe about the timeout.
		swe.generate_acktimeout_event();
	}
}