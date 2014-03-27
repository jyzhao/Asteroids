import java.awt.Image;
import java.util.ArrayList;

//The Animation class manages frames and the amount of time to display each frame.
public class Animation {

	public Animation(long animationTime, int currentFrameIndex,
			ArrayList<Frame> frames, long totalDuration) {
		super();
		this.animationTime = animationTime;
		this.currentFrameIndex = currentFrameIndex;
		this.frames = frames;
		this.totalDuration = totalDuration;
	}

	private class Frame {

		long endTime;
		Image image;

		public Frame(Image image, long endTime) {
			this.image = image;
			this.endTime = endTime;
		}
	}

	private long animationTime;
	private int currentFrameIndex;
	private ArrayList<Frame> frames;

	private long totalDuration;

	public Animation() {
		frames = new ArrayList<Frame>();
		totalDuration = 0;
		start();
	}

	// add frame
	public synchronized void addFrame(Image image, long duration) {
		totalDuration += duration;
		frames.add(new Frame(image, totalDuration));
	}

	// get frame
	private Frame getFrame(int i) {
		return frames.get(i);
	}

	// get image
	public synchronized Image getImage() {
		if (frames.size() == 0) {
			return null;
		} else {
			return getFrame(currentFrameIndex).image;
		}
	}

	// start frame
	public synchronized void start() {
		animationTime = 0;
		currentFrameIndex = 0;
	}

	// update current image
	public synchronized void update(long elapsedTime) {
		if (frames.size() > 1) {
			animationTime += elapsedTime;

			if (animationTime >= totalDuration) {
				animationTime = animationTime % totalDuration;
				currentFrameIndex = 0;
			}

			while (animationTime > getFrame(currentFrameIndex).endTime) {
				currentFrameIndex++;
			}
		}
	}
}
