import java.util.concurrent.locks.Condition;

/**
 * Class Monitor
 * To synchronize dining philosophers.
 *
 * @author Serguei A. Mokhov, mokhov@cs.concordia.ca
 */
public class Monitor
{
	/*
	 * ------------
	 * Data members
	 * ------------
	 */
	enum State {THINKING, EATING, HUNGRY};
	private State[] states;
	private Object[] self;
	private boolean IsTalking;


	/**
	 * Constructor
	 */
	public Monitor(int piNumberOfPhilosophers)
	{
		// TODO: set appropriate number of chopsticks based on the # of philosophers

		states= new State[piNumberOfPhilosophers];
		self=new Condition[piNumberOfPhilosophers];

		for(int i=0; i<piNumberOfPhilosophers; i++)
		{
			states[i]=State.THINKING;
			self[i]=new Object();
			
		}
		IsTalking=false;
		
	}

	/*
	 * -------------------------------
	 * User-defined monitor procedures
	 * -------------------------------
	 */

	/**
	 * Grants request (returns) to eat when both chopsticks/forks are available.
	 * Else forces the philosopher to wait()
	 */
	public synchronized void pickUp(final int piTID)
	{
		// ...
		states[piTID-1]=State.HUNGRY;
		test(piTID);
		if (states[piTID]!=State.EATING)
		try{
			self[piTID].wait();
			
		} catch (Exception e) {
			Thread.currentThread().interrupt();
		}
	}

	/**
	 * When a given philosopher's done eating, they put the chopstiks/forks down
	 * and let others know they are available.
	 */
	public synchronized void putDown(final int piTID)
	{
		// ...
		states[piTID-1]=State.THINKING;
		test((piTID+1) % states.length);
		test((piTID+(states.length-1)) % states.length);
		
	}
	public synchronized void test (final int i)
	{
		if (states[(i+1) % states.length] != State.EATING && states[i] == State.HUNGRY && states[(i+(states.length-1)) % states.length] != State.EATING)
		{ 
			states[i]=State.EATING;
			self[i].notify();
		}
	}

	/**
	 * Only one philopher at a time is allowed to philosophy
	 * (while she is not eating).
	 */
	public synchronized void requestTalk()
	{
		if(IsTalking) {
			try {
				wait();
				requestTalk();
			} 

			catch(InterruptedException e) {
				System.out.println("A philosopher is currently speaking something very useful. Please wait to philosophy");
			}

			// the philosopher is talking
			IsTalking = true;
		}
	}


	/**
	 * When one philosopher is done talking stuff, others
	 * can feel free to start talking.
	 */
	public synchronized void endTalk()
	{
		// ...
		IsTalking=false;
		notifyAll();
	}
}

// EOF
