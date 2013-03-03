import java.awt.event.*;
public class AcknowledgementTimerEventGenerator implements ActionListener
{
	private SWE swe;
	
	public AcknowledgementTimerEventGenerator ( SWE swe )
	{
		this.swe = swe;
	}
	
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
		swe.generate_acktimeout_event();
	}
}