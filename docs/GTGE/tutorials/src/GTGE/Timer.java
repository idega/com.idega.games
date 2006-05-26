package com.golden.gamedev.object;


/**
 * Class to manage timing in GTGE Frame Work to create game independent of frame
 * rate. <code>Timer</code> is usually used to create sprite behaviour, such as
 * used in sprite animation. <p>
 *
 * Example how to use timer in conjunction with sprite in order to make the
 * sprite do an action every 1 second :
 * <pre>
 * public class DummySprite extends Sprite {
 *
 *     // 1000 ms = 1 sec
 *     Timer timer = new Timer(1000);
 *
 *     public void update(long elapsedTime) {
 *       if (timer.action(elapsedTime)) {
 *         // do an action!! this always called every 1 second
 *       }
 *     }
 *
 *   }
 *
 * }
 * </pre>
 */
public class Timer implements java.io.Serializable {


 /*************************** TIMER VARIABLES ********************************/

    private boolean active = true;
	private long 	delay;			// action delay
	private long 	currentTick;


 /****************************************************************************/
 /******************************* CONSTRUCTOR ********************************/
 /****************************************************************************/

    /**
     * Creates new <code>Timer</code> with specified delay time in milliseconds.
     *
     * @param delay	delay time in milliseconds.
     */
	public Timer(int delay) {
		this.delay = delay;
    }


 /****************************************************************************/
 /***************************** MAIN-FUNCTION ********************************/
 /****************************************************************************/

	/**
	 * Returns true, if the timer delay time has been elapsed, thus the action
	 * need to be performed.
	 *
	 * @param elapsedTime	time elapsed since last update
	 */
	public boolean action(long elapsedTime) {
		if (active) {
			currentTick += elapsedTime;
   		 	if (currentTick >= delay) {
				// time elapsed!

				// currentTick = 0;
				// synch the current tick to make the next tick accurate
				currentTick -= delay;

				return true;
			}
		}

		return false;
	}

	/**
	 * Refreshs the timer counter (current tick).
	 */
	public void refresh() {
		currentTick = 0;
	}

    /**
     * Makes this timer state equals with other timer, this include active state,
     * delay time, and timer current tick.
     */
	public void setEquals(Timer other) {
	    active = other.active;
	    delay = other.delay;
	    currentTick = other.currentTick;
	}


 /****************************************************************************/
 /************************** TIMER VARIABLES *********************************/
 /****************************************************************************/

    /**
     * Returns active state of this timer, inactive timer won't do any action.
     */
	public boolean isActive() {
		return active;
	}

    /**
     * Sets active state of this timer, inactive timer won't do any action.
     */
	public void setActive(boolean b) {
		active = b;
		refresh();
	}

	/**
	 * Returns timer delay time in milliseconds.
	 */
	public long getDelay() {
		return delay;
	}

	/**
	 * Sets timer delay time in milliseconds.
	 */
	public void setDelay(long i) {
		delay = i;
		refresh();
	}

	/**
	 * Returns timer current tick. <p>
	 *
	 * If current tick is exceeded timer {@linkplain #getDelay() delay time}, the
	 * {@linkplain #action(long) action(elapsedTime)} method will return true.
	 */
	public long getCurrentTick() {
		return currentTick;
	}

	/**
	 * Sets timer current tick. <p>
	 *
	 * If current tick is exceeded timer {@linkplain #getDelay() delay time}, the
	 * {@linkplain #action(long) action(elapsedTime)} method will return true.
	 */
	public void setCurrentTick(long tick) {
		this.currentTick = tick;
	}

}