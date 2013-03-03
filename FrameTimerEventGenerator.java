import java.awt.event.*;

public class FrameTimerEventGenerator implements ActionListener
{
	private int seqNr;
	private SWE swe;
	
	public FrameTimerEventGenerator (SWE swe, int seqNr)
	{
		this.seqNr = seqNr;
		this.swe = swe;
	}
	
	public void setSeqNr (int seqNr)
	{
		this.seqNr = seqNr;
	}
	
	public int getSeqNr ( )
	{
		return this.seqNr;
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
		swe.generate_timeout_event (seqNr);
	}
}
