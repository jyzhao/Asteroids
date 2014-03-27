public class Bullet extends Sprite {
	private int bulletDistanceTravelled;

	private int playerIndex; // first playerShip 0 second playerShip 1 alienShip 2

	//Bullet constructor animation object,
	
	public Bullet(Animation anim, int width, int index) {
		super(anim);
		bulletDistanceTravelled = 0;
		playerIndex = index;
	}

	public void updateBulletDistance(int newDistance) {
		bulletDistanceTravelled += newDistance;
	}

	public int getBulletDistanceTravelled() {
		return bulletDistanceTravelled;
	}

	public int getPlayerIndex() {
		return playerIndex;
	}
}
