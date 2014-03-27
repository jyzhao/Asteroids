
import java.awt.Component;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.List;


public class InputUpdate implements KeyListener {

	private static final int NUM_KEY_CODES = 600;

	private GameAction[] keyActions = new GameAction[NUM_KEY_CODES];

	//command update
	public InputUpdate(Component comp) {
		// register key and mouse listeners
		comp.addKeyListener(this);
		comp.setFocusTraversalKeysEnabled(false);
	}

	//map game actions to keys
	public void mapToKey(GameAction gameAction, int keyCode) {
		keyActions[keyCode] = gameAction;
	}

	//clear game actions
	public void clearMap(GameAction gameAction) {
		for (int i = 0; i < keyActions.length; i++) {
			if (keyActions[i] == gameAction) {
				keyActions[i] = null;
			}
		}

		gameAction.reset();
	}

	//return game actions
	public List<String> getMaps(GameAction gameCode) {
		ArrayList<String> list = new ArrayList<String>();

		for (int i = 0; i < keyActions.length; i++) {
			if (keyActions[i] == gameCode) {
				list.add(getKeyName(i));
			}
		}

		return list;
	}

	//Resets all GameActions so they appear like they haven't been pressed.
	public void resetAllGameActions() {
		for (int i = 0; i < keyActions.length; i++) {
			if (keyActions[i] != null) {
				keyActions[i].reset();
			}
		}

	}

	
	 // Gets the name of a key code.
	public static String getKeyName(int keyCode) {
		return KeyEvent.getKeyText(keyCode);
	}

	private GameAction getKeyAction(KeyEvent e) {
		int keyCode = e.getKeyCode();
		if (keyCode < keyActions.length) {
			return keyActions[keyCode];
		} else {
			return null;
		}
	}

	// from the KeyListener interface
	public void keyPressed(KeyEvent e) {
		GameAction gameAction = getKeyAction(e);
		if (gameAction != null) {
			gameAction.press();
		}
		// make sure the key isn't processed for anything else
		e.consume();
	}

	// from the KeyListener interface
	public void keyReleased(KeyEvent e) {
		GameAction gameAction = getKeyAction(e);
		if (gameAction != null) {
			gameAction.release();
		}
		// make sure the key isn't processed for anything else
		e.consume();
	}

	// from the KeyListener interface
	public void keyTyped(KeyEvent e) {
		// make sure the key isn't processed for anything else
		e.consume();
	}
}
