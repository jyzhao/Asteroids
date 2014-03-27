import java.awt.*;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.lang.reflect.InvocationTargetException;
import javax.swing.JFrame;


public class ScreenUpdate {

	private GraphicsDevice device;

	//Creates a new ScreenManager object.
	public ScreenUpdate() {
		GraphicsEnvironment environment = GraphicsEnvironment
				.getLocalGraphicsEnvironment();
		device = environment.getDefaultScreenDevice();
	}

	// Returns a list of compatible display modes for the default device on the
	
	public DisplayMode[] getCompatibleDisplayModes() {
		return device.getDisplayModes();
	}

	//Returns the first compatible mode in a list of modes. Returns null if no
	public DisplayMode findFirstCompatibleMode(DisplayMode modes[]) {
		DisplayMode goodModes[] = device.getDisplayModes();
		for (int i = 0; i < modes.length; i++) {
			for (int j = 0; j < goodModes.length; j++) {
				if (displayModesMatch(modes[i], goodModes[j])) {
					return modes[i];
				}
			}

		}

		return null;
	}

	//return current mode
	public DisplayMode getCurrentDisplayMode() {
		return device.getDisplayMode();
	}

//whether screens match
	public boolean displayModesMatch(DisplayMode mode1, DisplayMode mode2)

	{
		if (mode1.getWidth() != mode2.getWidth()
				|| mode1.getHeight() != mode2.getHeight()) {
			return false;
		}

		if (mode1.getBitDepth() != DisplayMode.BIT_DEPTH_MULTI
				&& mode2.getBitDepth() != DisplayMode.BIT_DEPTH_MULTI
				&& mode1.getBitDepth() != mode2.getBitDepth()) {
			return false;
		}

		if (mode1.getRefreshRate() != DisplayMode.REFRESH_RATE_UNKNOWN
				&& mode2.getRefreshRate() != DisplayMode.REFRESH_RATE_UNKNOWN
				&& mode1.getRefreshRate() != mode2.getRefreshRate()) {
			return false;
		}

		return true;
	}

//set full screen mode
	public void setFullScreen(DisplayMode displayMode) {
		final JFrame frame = new JFrame();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setUndecorated(true);
		frame.setIgnoreRepaint(true);
		frame.setResizable(false);

		device.setFullScreenWindow(frame);

		if (displayMode != null && device.isDisplayChangeSupported()) {
			try {
				device.setDisplayMode(displayMode);
			} catch (IllegalArgumentException ex) {
			}
			// fix for mac os x
			frame.setSize(displayMode.getWidth(), displayMode.getHeight());
		}
		// avoid potential deadlock in 1.4.1_02
		try {
			EventQueue.invokeAndWait(new Runnable() {
				public void run() {
					frame.createBufferStrategy(2);
				}
			});
		} catch (InterruptedException ex) {
			// ignore
		} catch (InvocationTargetException ex) {
			// ignore
		}
	}

// get graphics to display
	public Graphics2D getGraphics() {
		Window window = device.getFullScreenWindow();
		if (window != null) {
			BufferStrategy strategy = window.getBufferStrategy();
			return (Graphics2D) strategy.getDrawGraphics();
		} else {
			return null;
		}
	}

	// Updates the display.
	public void update() {
		Window window = device.getFullScreenWindow();
		if (window != null) {
			BufferStrategy strategy = window.getBufferStrategy();
			if (!strategy.contentsLost()) {
				strategy.show();
			}
		}
		Toolkit.getDefaultToolkit().sync();
	}

	// Returns the window currently used in full screen mode. Returns null if
	 
	public JFrame getFullScreenWindow() {
		return (JFrame) device.getFullScreenWindow();
	}

	// Returns the width of the window currently used in full screen mode.
	 
	public int getWidth() {
		Window window = device.getFullScreenWindow();
		if (window != null) {
			return window.getWidth();
		} else {
			return 0;
		}
	}

	// Returns the height of the window currently used in full screen mode
	 
	public int getHeight() {
		Window window = device.getFullScreenWindow();
		if (window != null) {
			return window.getHeight();
		} else {
			return 0;
		}
	}

	//Restores the screen's display mode.
	 
	public void restoreScreen() {
		Window window = device.getFullScreenWindow();
		if (window != null) {
			window.dispose();
		}
		device.setFullScreenWindow(null);
	}

	//Creates an image compatible with the current display.
	 
	public BufferedImage createCompatibleImage(int w, int h, int transparancy) {
		Window window = device.getFullScreenWindow();
		if (window != null) {
			GraphicsConfiguration gc = window.getGraphicsConfiguration();
			return gc.createCompatibleImage(w, h, transparancy);
		}
		return null;
	}
}
