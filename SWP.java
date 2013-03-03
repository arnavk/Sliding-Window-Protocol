/*===============================================================*
 *  File: SWP.java                                               *
 *                                                               *
 *  This class implements the sliding window protocol            *
 *  Used by VMach class					         *
 *  Uses the following classes: SWE, Packet, PFrame, PEvent,     *
 *                                                               *
 *  Author: Professor SUN Chengzheng                             *
 *          School of Computer Engineering                       *
 *          Nanyang Technological University                     *
 *          Singapore 639798                                     *
 *===============================================================*/

public class SWP {

/*========================================================================*
 the following are provided, do not change them!!
 *========================================================================*/
   //the following are protocol constants.
   public static final int MAX_SEQ = 7; 
   public static final int NR_BUFS = (MAX_SEQ + 1)/2;

   // the following are protocol variables
   private int oldest_frame = 0;
   private PEvent event = new PEvent();  
   private Packet out_buf[] = new Packet[NR_BUFS];
	
   //the following are used for simulation purpose only
   private SWE swe = null;
   private String sid = null;  

	//additional variables added by us
	private boolean no_nak = true; 

	//input buffer for incoming transmission
	private Packet in_buf []  = new Packet[NR_BUFS];
	
	//AcknoledgementTimer object
	private AcknowledgementTimer ackTimer;

	//Array of FrameTimer
	private FrameTimer [] frameTimer = new FrameTimer [NR_BUFS];
	
	
   //Constructor
   public SWP(SWE sw, String s){
      swe = sw;
      sid = s;
   }

   //the following methods are all protocol related
   private void init()
   {
   	//All the relavant initializations are done in this method.

      for (int i = 0; i < NR_BUFS; i++)
      {
	   	//Initializing the output buffer
	   	out_buf[i] = new Packet();
	   	in_buf [i]  = new Packet ();

	   	//Initializing each of the FrameTimer array elements.
			frameTimer [i] = new FrameTimer (150, swe, i);
      }
		//Initializing the AcknowledgementTimer
		ackTimer = new AcknowledgementTimer (50, swe);
   }

   private void wait_for_event(PEvent e){
      swe.wait_for_event(e); //may be blocked
      oldest_frame = e.seq;  //set timeout frame seq
   }

   private void enable_network_layer(int nr_of_bufs) {
   //network layer is permitted to send if credit is available
	swe.grant_credit(nr_of_bufs);
   }

   private void from_network_layer(Packet p) {
      swe.from_network_layer(p);
   }

   private void to_network_layer(Packet packet) {
	swe.to_network_layer(packet);
   }

   private void to_physical_layer(PFrame fm)  {
      System.out.println("SWP: Sending frame: seq = " + fm.seq + 
			    " ack = " + fm.ack + " kind = " + 
			    PFrame.KIND[fm.kind] + " info = " + fm.info.data );
      System.out.flush();
      swe.to_physical_layer(fm);
   }

   private void from_physical_layer(PFrame fm) {
      PFrame fm1 = swe.from_physical_layer(); 
	fm.kind = fm1.kind;
	fm.seq = fm1.seq; 
	fm.ack = fm1.ack;
	fm.info = fm1.info;
   }


/*===========================================================================*
 	implement your Protocol Variables and Methods below: 
	[Note - Private variables declared on top and the init() has been modified]
 *==========================================================================*/
 	private boolean between (int a, int b, int c)
	{
		//This method is used to check if a <= b < c in a cyclic way.
		return ( ( a <= b ) && ( b < c ) ) || ( (c < a ) && ( a <= b ) ) || ( ( b < c ) && ( c < a ) );
	}  
	
	private int inc (int frame_number)
	{
		// Increments (cyclically) the frame number passed to it and returns it.
		return ((frame_number + 1) % (MAX_SEQ + 1));
	}
	
	private void send_frame (int fk, int frame_nr, int frame_expected, Packet [] buffer)
	{
		/*
		  The following method prepares a frame based on information found and 
		  sends the frame to the physical layer.
		  Upon sending the frame, if it is of the 'DATA' type, the timer is started
		  for that frame number.
		*/
		PFrame s = new PFrame();
		s.kind = fk;
		if (fk == PFrame.DATA)
			s.info = buffer [frame_nr % NR_BUFS];
		s.seq = frame_nr;
		s.ack = (frame_expected + MAX_SEQ) % (MAX_SEQ + 1);
		if (fk == PFrame.NAK)
			//changed to false since a negative acknowledgement is being sent.
			no_nak = false;
		to_physical_layer(s);
		if (fk == PFrame.DATA)
			start_timer (frame_nr); 
			// We are not %ing by NR_BUFS here because we are doing that later in the start_timer() method 
		stop_ack_timer();
	}
	
	
	public void protocol6() 
	{
		/* ack_expected is the frame number of expected acknowledgement.
    	It forms the lower edge of the sender's window. 
    	*/
		int ack_expected;

		/*
	    next_frame_to_send signifies the upper edge of the sending window+1.
	 	Also the next outgoing frame.
		*/
		int next_frame_to_send;

		/*
		The frame expected on the receiver's side. Forms the lower edge of
		the receiver's window.
	    */
		int frame_expected;

		/*
		Upper edge of the receiver's window + 1. Signifies the position where
		the receiver can receive no more frames.
		*/
		int too_far;

		//loop-counter
		int i;

		//  r is a scratch variable used during processing
		PFrame r = new PFrame();
		
		//An array to keep track of the arrivals in the inbound buffer 
		boolean []arrived = new boolean[NR_BUFS];
				
      	init(); 
		enable_network_layer(NR_BUFS);
		
		//carrying out initializations of the local variables
		ack_expected = 0;
		next_frame_to_send = 0;
		frame_expected = 0;
		too_far = NR_BUFS;
		
		for ( i = 0; i < NR_BUFS; i++ )
		{
			//initializing the arrived array to denote that nothing has arrived yet.
			arrived[i] = false;
		}
		
		while(true) 
		{
			wait_for_event(event);
		   switch(event.type) 
			{
				//Accept, save and transmit new frame
		      case (PEvent.NETWORK_LAYER_READY):
		      		//fetch new packet
					from_network_layer (out_buf[next_frame_to_send % NR_BUFS]);
					
					//send new frame
					send_frame (PFrame.DATA, next_frame_to_send, frame_expected, out_buf);
					
					//advance the upper window edge
					next_frame_to_send = inc (next_frame_to_send);
					break; 
					
		      case (PEvent.FRAME_ARRIVAL):
		      	//Frame is arriving

		      	//fetch incoming frame
			   	from_physical_layer (r);

					if (r.kind == PFrame.DATA)
					{	//The frame has data.

						//An undamaged frame has arrived

						if ((r.seq != frame_expected)  && no_nak)
							//if its not the expected frame and there are no negative acks.
							send_frame (PFrame.NAK, 0, frame_expected, out_buf);
						else
							//Checking for seperate acknowledgement.
							start_ack_timer();


						if (between (frame_expected, r.seq, too_far) && (arrived[r.seq % NR_BUFS] == false))
						{
							//The frame is within the receiver's window.

							//Frames may be accepted in any order
							//Accept into inbound stream
							arrived[r.seq % NR_BUFS] = true;
							in_buf[r.seq % NR_BUFS]  = r.info;
							
							while (arrived [frame_expected % NR_BUFS])
							{
								//Pass frames to the netowrk layer
								to_network_layer (in_buf[frame_expected % NR_BUFS]);
								no_nak = true;
								arrived [frame_expected % NR_BUFS] = false;

								//Advancing the window.
								frame_expected = inc (frame_expected);
								too_far = inc (too_far);//advance upper edge
								start_ack_timer();//check for separate acknowledgement
							}
						}
					}
					
					//If it is a negative acknowledgement, resend the frame.
					else if ((r.kind == PFrame.NAK)  && between (ack_expected, (r.ack+1) % (MAX_SEQ + 1), next_frame_to_send))
						send_frame (PFrame.DATA, (r.ack+1) % (MAX_SEQ + 1), frame_expected, out_buf);
					

					//For every frame that is successfully sent to the network layer,
					//allow the next packet to be accepted
					while (between(ack_expected, r.ack, next_frame_to_send))
					{

						enable_network_layer(1);
						stop_timer (ack_expected % NR_BUFS);
						ack_expected = inc (ack_expected);//because frame was received
					}
					
					break;
						   
				case (PEvent.CKSUM_ERR):
					//if no negative acknowledgement has yet been sent for the damaged frame
					if (no_nak)
						send_frame (PFrame.NAK, 0, frame_expected, out_buf);
					break;
				case (PEvent.TIMEOUT):
					// Re-transmitting the frame.
					send_frame (PFrame.DATA, oldest_frame, frame_expected, out_buf);
					break; 
		      case (PEvent.ACK_TIMEOUT):
		      		// sending a separate acknowledgement frame rather than
		      		// waiting for another frame to piggyback on.
					send_frame (PFrame.ACK, 0, frame_expected, out_buf);
					break; 
				default: 
			   	System.out.println("SWP: undefined event type = " 
                                       + event.type); 
			   System.out.flush();
	   	}
      }      
   }

 /* Note: when start_timer() and stop_timer() are called, 
    the "seq" parameter must be the sequence number, rather 
    than the index of the timer array, 
    of the frame associated with this timer, 
   */
 
   private void start_timer(int seq) 
	{
		//staring the timer for the relavant frame.
 		frameTimer [seq % NR_BUFS].startTimer (seq);    
   }

   private void stop_timer(int seq) 
	{
		//stopping the frame timer for the relavant frame.
		frameTimer [seq % NR_BUFS].stopTimer();
   }

   private void start_ack_timer( ) 
	{
		//starting the acknowledgement timer
		ackTimer.startTimer();    
   }

   private void stop_ack_timer() 
   {
   		//stopping the acknowledgement timer
     	ackTimer.stopTimer();
   }

}//End of class

/* Note: In class SWE, the following two public methods are available:
   . generate_acktimeout_event() and
   . generate_timeout_event(seqnr).

   To call these two methods (for implementing timers),
   the "swe" object should be referred as follows:
     swe.generate_acktimeout_event(), or
     swe.generate_timeout_event(seqnr).
*/
