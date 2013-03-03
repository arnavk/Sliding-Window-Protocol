import java.awt.event.*;

/*********************************************************
	This class generates time-out events for FrameTimer
 *********************************************************/

public class FrameTimerEventGenerator implements ActionListener
{
	//the sequence number of associated frame
	private int seqNr;

	//a reference to the environment to generate the timeout event
	private SWE swe;
	
	public FrameTimerEventGenerator (SWE swe, int seqNr)
	{	//constructor
		this.seqNr = seqNr;
		this.swe = swe;
	}
	
	//Accessiblity methods - setter and getter for seqNr
	public void setSeqNr (int seqNr)
	{
		this.seqNr = seqNr;
	}
	
	public int getSeqNr ( )
	{
		return this.seqNr;
	}
	
	//Accessiblity methods - setter and getter for swe
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
		swe.generate_timeout_event (seqNr);
	}
}
