
public class GameAction {


	public static final int NORMAL = 0;

	public static final int DETECT_INITAL_PRESS_ONLY = 1;

	private static final int STATE_RELEASED = 0;
	private static final int STATE_PRESSED = 1;
	private static final int STATE_WAITING_FOR_RELEASE = 2;

	private String name;
	private int behavior;
	private int amount;
	private int state;

	//constructor
	public GameAction(String name) {
		this(name, NORMAL);
	}

	//constructor with behavior
	public GameAction(String name, int behavior) {
		this.name = name;
		this.behavior = behavior;
		reset();
	}

	 // Gets the name of this GameAction.
	public String getName() {
		return name;
	}

	// Resets this GameAction so that it appears like it hasn't been pressed.
	public void reset() {
		state = STATE_RELEASED;
		amount = 0;
	}

	// Taps this GameAction. Same as calling press() followed by release().
	public synchronized void tap() {
		press();
		release();
	}

	// Signals that the key was pressed.
	public synchronized void press() {
		press(1);
	}

	public synchronized void press(int amount) {
		if (state != STATE_WAITING_FOR_RELEASE) {
			this.amount += amount;
			state = STATE_PRESSED;
		}

	}
    
	//whether is released
	public synchronized void release() {
		state = STATE_RELEASED;
	}

	public synchronized boolean isPressed() {
		return (getAmount() != 0);
	}
	
	public synchronized int getAmount() {
		int retVal = amount;
		if (retVal != 0) {
			if (state == STATE_RELEASED) {
				amount = 0;
			} else if (behavior == DETECT_INITAL_PRESS_ONLY) {
				state = STATE_WAITING_FOR_RELEASE;
				amount = 0;
			}
		}
		return retVal;
	}
}
