import java.awt.Color;
import java.awt.DisplayMode;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.Window;
import java.awt.event.KeyEvent;
import java.awt.geom.AffineTransform;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Random;

import javax.swing.ImageIcon;

public class Asteroids {

	private static CollisionDetection cDetection;

	public static void main(String args[]) {
		Asteroids asteroidsGame = new Asteroids();
		cDetection = new CollisionDetection();
		cDetection.start();
		asteroidsGame.run();
	}

	public static final String HIGHSCORE_FILENAME = "HighScore.txt";
	private static int NUM_ASTEROIDS = 3;
	private static int CURRENT_LEVEL = 1;
	private static int ALIENSHIP_BULLET_FIRE_INTERVAL = 5000;
	private static long previousAlienBulletTime = System.currentTimeMillis();
	private static int playerBulletCount = 0;
	private static final int ALIENSHIP_SPAWN_INTERVAL = 1;
	private static final int ALIENSHIP_HEIGHT = 75;
	public static boolean ALIENSHIP_ALIVE = true;
	private static int ALIENSHIP_SPEED = 2;
	public static boolean MULTIPLAYER_ACTIVE = false;
	public static boolean GRAVITATIONAL_OBJECT_ACTIVE = false;
	public static boolean GRAVITATIONAL_OBJECT_VISIBLE = false;
	public static int GRAVITATIONAL_OBJECT_STRENGTH = 4;
	public static final float BASE_ASTEROID_SPEED = 0.05f;
	public static boolean SAVE_GAME = false;
	public static boolean LOAD_GAME = false;
	public static String filename = "gameFile.hv";
	private static ScreenUpdate screen;
	public static boolean UNLIMITED_LIVES = false;
	static Image backgroundImage = loadImage("images/nebulaBackground.png");
	static Image asteroidImage = loadImage("images/asteroid.png");
	static Image childAsteroidImage = loadImage("images/childrenAsteroid.png");
	static Image playerImage = loadImage("images/playerSpaceship.png");
	static Image alienImage = loadImage("images/alienSpaceship.png");
	Image bulletImage = loadImage("images/bullets.png");
	static Image gravitationObjectImage = loadImage("images/blackhole.png");
	private static final int playerSpeed = 11;
	private static final int bulletSpeed = 21;

	// contains references to all the asteroid and player objects
	public static LinkedList<Sprite> asteroidSprites = new LinkedList<Sprite>();
	public static LinkedList<Player> playerSprites = new LinkedList<Player>();
	public static LinkedList<Bullet> bulletSprites = new LinkedList<Bullet>();
	public static Sprite alienSprite;
	public static Sprite gravitationalObjectSprite;

	static int[] directionList = new int[] { 1, -1 };

	// the following variables are all for getting the keyboard events and
	// pausing the game itself
	protected GameAction turnLeft;
	protected GameAction turnRight;
	protected GameAction moveForward;
	protected GameAction moveBackward;
	protected GameAction turnLeftPlayer2;
	protected GameAction turnRightPlayer2;
	protected GameAction moveForwardPlayer2;
	protected GameAction moveBackwardPlayer2;

	protected GameAction fireBulletsShip0;
	protected GameAction fireBulletsShip1;
	protected GameAction exit;
	protected GameAction pause;
	protected GameAction pauseEnter;
	protected static InputUpdate inputUpdate;
	private boolean paused = true;
	public static boolean exitGame;
	public int[] scoreArray = new int[10];

	// Menu
	private String[] buttonNames = { "Continue", "Save", "Load",
			"Gravitation Exist: ", "Gravitation Visible: ",
			"Unlimited Lives: ", "Num Of Astroids: ", "Reset Scoreboard",
			"Starting Level: ", "Multiplayer: ", "Quit" };

	private int pauseMenuIndex = 0;
	public Map<String, Rectangle> buttonFrame = new HashMap<String, Rectangle>();
	private static Color buttonBackgroundColor = Color.black;
	private static Color buttonTextColor = Color.white;

	// HighScore
	public static HighScoreArrayObject highScoreObj = new HighScoreArrayObject();

	
	public static void loadAsteroidImages() {

		asteroidSprites.clear();

		// create Sprites
		for (int i = 0; i < NUM_ASTEROIDS; i++) {
			Animation anim = new Animation();
			anim.addFrame(asteroidImage, 250);
			asteroidSprites.add(new Sprite(anim, "parent"));

			// select random starting location
			asteroidSprites.get(i).setX(
					(float) Math.random()
							* (screen.getWidth() - asteroidSprites.get(i)
									.getWidth()));
			asteroidSprites.get(i).setY(
					(float) Math.random()
							* (screen.getHeight() - asteroidSprites.get(i)
									.getHeight()));

			// select random velocity
			int index = new Random().nextInt(directionList.length);

			Random random = new Random();

			asteroidSprites
					.get(i)
					.setVelocityX(
							directionList[index]
									* (BASE_ASTEROID_SPEED + (CURRENT_LEVEL * 0.01f))
									* (float) ((random.nextBoolean() ? 1 : -1) * Math.sin(Math
											.toRadians(Math.random() * 1000 % 360))));
			asteroidSprites
					.get(i)
					.setVelocityY(
							directionList[index]
									* (BASE_ASTEROID_SPEED + (CURRENT_LEVEL * 0.01f))
									* (float) ((random.nextBoolean() ? 1 : -1) * Math.cos(Math
											.toRadians(Math.random() * 1000 % 360))));

		}

	}

	public static void addAsteroidsInPlaceOfKilledAsteroid(Sprite s,
			int numOfAsteroids) {
		for (int i = 0; i < numOfAsteroids; i++) {
			Animation anim = new Animation();
			anim.addFrame(childAsteroidImage, 250);
			Sprite newAsteroid = new Sprite(anim, "child");

			newAsteroid.setX(s.getX());
			newAsteroid.setY(s.getY());

			Random random = new Random();

			// select random velocity
			int index = new Random().nextInt(directionList.length);
			newAsteroid.setVelocityX(directionList[index]
					* (i + 1)
					* (BASE_ASTEROID_SPEED + (CURRENT_LEVEL * 0.01f))
					* (float) ((random.nextBoolean() ? 1 : -1) * Math.sin(Math
							.toRadians(Math.random() * 1000 % 360))));
			newAsteroid.setVelocityY(directionList[index]
					* (i + 1)
					* (BASE_ASTEROID_SPEED + (CURRENT_LEVEL * 0.01f))
					* (float) ((random.nextBoolean() ? 1 : -1) * Math.sin(Math
							.toRadians(Math.random() * 1000 % 360))));

			asteroidSprites.add(newAsteroid);
		}
	}

	private static Image loadImage(String fileName) {
		return new ImageIcon(fileName).getImage();
	}

	public static void initilization() {

		inputUpdate.resetAllGameActions();

		playerSprites.clear();

		Animation playerAnimation = new Animation();
		playerAnimation.addFrame(playerImage, 250);
		playerSprites.add(new Player(playerAnimation, 3));

		// random starting location
		playerSprites
				.get(0)
				.setX((float) Math.random()
						* (screen.getWidth() - playerSprites.get(0).getWidth()));
		playerSprites.get(0).setY(
				(float) Math.random()
						* (screen.getHeight() - playerSprites.get(0)
								.getHeight()));
		loadAsteroidImages();

	}

	public void run() {
		screen = new ScreenUpdate();
		try {
			DisplayMode displayMode = screen.getCurrentDisplayMode();
			screen.setFullScreen(displayMode);
			Window window = screen.getFullScreenWindow();
			inputUpdate = new InputUpdate(window);

			for (int i = 0; i < buttonNames.length; i++) {
				Rectangle temp = new Rectangle(screen.getWidth() / 2 - 75,
						200 + i * 50, 180, 0);
				buttonFrame.put(buttonNames[i], temp);
			}

			inputUpdate.resetAllGameActions();
			playerSprites.clear();
			// new animation
			Animation anim = new Animation();
			anim.addFrame(playerImage, 250);
			playerSprites.add(new Player(anim, 3));

			// random starting location
			playerSprites.get(0).setX(
					(float) Math.random()
							* (screen.getWidth() - playerSprites.get(0)
									.getWidth()));
			playerSprites.get(0).setY(
					(float) Math.random()
							* (screen.getHeight() - playerSprites.get(0)
									.getHeight()));
			loadAsteroidImages();

			// loadHighScore
			try {
				File fileObject = new File(HIGHSCORE_FILENAME);
				FileReader fileReader = new FileReader(fileObject);
				BufferedReader bufferReader = new BufferedReader(fileReader);
				String str;
				int j = 0;
				while ((str = bufferReader.readLine()) != null) {
					String name = str.split(" ")[0];
					String score = str.split(" ")[1];

					scoreArray[j] = Integer.parseInt(score);

					highScoreObj.add(name, Integer.parseInt(score));
					j++;
				}
				fileReader.close();
			} catch (Exception e) {
			}

			// create alienShip
			Animation alienAnimation = new Animation();
			alienAnimation.addFrame(alienImage, 250);
			alienSprite = new Sprite(alienAnimation);
			alienSprite.setX(10);
			alienSprite.setY(ALIENSHIP_HEIGHT);

			// create gravitation
			Animation anim2 = new Animation();
			anim2.addFrame(gravitationObjectImage, 250);
			gravitationalObjectSprite = new Sprite(anim2);
			gravitationalObjectSprite.setX((screen.getWidth() / 2)
					- gravitationalObjectSprite.getWidth() / 2);
			gravitationalObjectSprite.setY((screen.getHeight() / 2)
					- gravitationalObjectSprite.getHeight() / 2);

			createGameActions();
			gameLoop();
		} finally {
			screen.restoreScreen();
		}
	}

	public void gameLoop() {
		long startTime = System.currentTimeMillis();
		long currentTime = startTime;

		while (!exitGame) {
			// write high score
			highScoreObj.writeHighScore(playerSprites.get(0).getPlayerScore(),
					1);

			if (playerSprites.get(0).getPlayerLife() == 0) {
				initilization();
				paused = true;
			}
			// if all sprites are destroyed
			if (asteroidSprites.size() == 0) {
				for (int i = 0; i < playerSprites.size(); i++) {
					Asteroids.playerSprites.get(i).setPlayerScore(
							Asteroids.playerSprites.get(i).getPlayerScore()
									+ CURRENT_LEVEL * 100);
				}
				CURRENT_LEVEL++;
				NUM_ASTEROIDS = 2 * CURRENT_LEVEL + 1;
				// System.out.println("Starting Level => " + CURR_LEVEL);
				loadAsteroidImages();
			}
			if (MULTIPLAYER_ACTIVE && playerSprites.size() == 1) {
				Animation anim = new Animation();
				anim.addFrame(playerImage, 250);
				playerSprites.add(new Player(anim, 3));

				// select random starting location
				playerSprites.get(1).setX(
						(float) Math.random()
								* (screen.getWidth() - playerSprites.get(1)
										.getWidth()));
				playerSprites.get(1).setY(
						(float) Math.random()
								* (screen.getHeight() - playerSprites.get(1)
										.getHeight()));
			}
			if (SAVE_GAME) {
				SAVE_GAME = false;
				saveObjectsToFile();
				cDetection = new CollisionDetection();
				cDetection.start();
			}
			if (LOAD_GAME) {
				LOAD_GAME = false;
				readObjectsFromFile();
				cDetection = new CollisionDetection();
				cDetection.start();
			}
			long elapsedTime = System.currentTimeMillis() - currentTime;
			currentTime += elapsedTime;

			// update the sprites
			update(elapsedTime);

			// draw and update screen
			Graphics2D g = screen.getGraphics();
			
			g.setBackground(Color.black); // set Background color
			g.clearRect(0, 0, screen.getWidth(), screen.getHeight()); 

			updateScoreAndLivesOnScreen(g);

			// draw graph
			draw(g);
			if (isPaused()) {
				// check user inputs
				checkGameInputPause();
			}
			g.dispose();
			screen.update(); // update screen

			try {
				Thread.sleep(20);
			} catch (InterruptedException ex) {
			}
		}

	}

	public void update(long elapsedTime) {
		// check input that can happen whether paused or not
		checkSystemInput();

		if (!isPaused()) {
			// check game input
			pauseMenuIndex = 0;
			checkGameInput();

			for (int i = 0; i < asteroidSprites.size(); i++) {

				Sprite s = asteroidSprites.get(i);

				// check sprite bounds
				if (s.getX() < 0.) {
					s.setX(screen.getWidth());
				} else if (s.getX() >= screen.getWidth()) {
					s.setX(0);
				}
				if (s.getY() < 0) {
					s.setY(screen.getHeight());
				} else if (s.getY() >= screen.getHeight()) {
					s.setY(0);
				}

				// update sprite
				s.update(elapsedTime);
			}

			for (int i = 0; i < playerSprites.size(); i++) {

				Player p = playerSprites.get(i);

				// check sprite bounds
				if (p.getX() < 0.) {
					p.setX(screen.getWidth());
				} else if (p.getX() >= screen.getWidth()) {
					p.setX(0);
				}
				if (p.getY() < 0) {
					p.setY(screen.getHeight());
				} else if (p.getY() >= screen.getHeight()) {
					p.setY(0);
				}

				// update sprite
				p.update(elapsedTime);
			}

			for (int i = 0; i < bulletSprites.size(); i++) {

				try {
					Bullet b = bulletSprites.get(i);
					b.setX(b.getX()
							+ (float) (bulletSpeed * Math.sin(Math.toRadians(b
									.getAngle()))));
					b.setY(b.getY()
							- (float) (bulletSpeed * Math.cos(Math.toRadians(b
									.getAngle()))));
					b.updateBulletDistance((int) Math.sqrt(2 * Math.pow(
							bulletSpeed, 2)));

					// check sprite bounds
					if (b.getX() < 0.) {
						b.setX(screen.getWidth());
					} else if (b.getX() >= screen.getWidth()) {
						b.setX(0);
					}
					if (b.getY() < 0) {
						b.setY(screen.getHeight());
					} else if (b.getY() >= screen.getHeight()) {
						b.setY(0);
					}
				}

				catch (Exception e) {
				}
				// b.update(elapsedTime);
			}

			// if alienship alive make it move
			if (ALIENSHIP_ALIVE) {
				if (alienSprite.getX() > screen.getWidth()) {
					alienSprite.setX(0);
				} else {
					alienSprite.setX(alienSprite.getX()
							+ (CURRENT_LEVEL / ALIENSHIP_SPAWN_INTERVAL)
							* ALIENSHIP_SPEED);
				}

				addAlienBulletsToScreen();
			}

			if (GRAVITATIONAL_OBJECT_ACTIVE) {
				for (int i = 0; i < playerSprites.size(); i++) {
					if (playerSprites.get(i).getX() < gravitationalObjectSprite
							.getX()) {
						playerSprites.get(i).setX(
								playerSprites.get(i).getX()
										+ GRAVITATIONAL_OBJECT_STRENGTH);
					} else {
						playerSprites.get(i).setX(
								playerSprites.get(i).getX()
										- GRAVITATIONAL_OBJECT_STRENGTH);
					}
					if (playerSprites.get(i).getY() < gravitationalObjectSprite
							.getY()) {
						playerSprites.get(i).setY(
								playerSprites.get(i).getY()
										+ GRAVITATIONAL_OBJECT_STRENGTH);
					} else {
						playerSprites.get(i).setY(
								playerSprites.get(i).getY()
										- GRAVITATIONAL_OBJECT_STRENGTH);
					}
				}
			}
		}
	}

	public void draw(Graphics2D g) {

		AffineTransform transform = new AffineTransform();

		if (GRAVITATIONAL_OBJECT_ACTIVE && GRAVITATIONAL_OBJECT_VISIBLE) {
			transform.setToTranslation(gravitationalObjectSprite.getX(),
					gravitationalObjectSprite.getY());
			if (gravitationalObjectSprite.getVelocityX() < 0) {
				transform.scale(-1, 1);
				transform.translate(-gravitationalObjectSprite.getWidth(), 0);
			}
			g.drawImage(gravitationalObjectSprite.getImage(), transform, null);
		}

		for (int i = 0; i < asteroidSprites.size(); i++) {
			Sprite sprite = asteroidSprites.get(i);

			// translate the sprite
			transform.setToTranslation(sprite.getX(), sprite.getY());

			// if the sprite is moving left, flip the image
			if (sprite.getVelocityX() < 0) {
				transform.scale(-1, 1);
				transform.translate(-sprite.getWidth(), 0);
			}

			// draw it
			g.drawImage(sprite.getImage(), transform, null);
		}

		for (int i = 0; i < playerSprites.size(); i++) {
			Player player = playerSprites.get(i);
			// translate the sprite
			transform.setToTranslation(player.getX(), player.getY());

			// Player rotation
			transform.rotate(Math.toRadians(player.getAngle()));

			// if the sprite is moving left, flip the image
			if (player.getVelocityX() < 0) {
				transform.scale(-1, 1);
				transform.translate(-player.getWidth(), 0);
			}
			g.drawImage(player.getImage(), transform, null);
		}

		for (int i = 0; i < bulletSprites.size(); i++) {
			Bullet bullet = bulletSprites.get(i);
			// translate the sprite
			transform.setToTranslation(bullet.getX(), bullet.getY());

			// if the sprite is moving left, flip the image
			if (bullet.getVelocityX() < 0) {
				transform.scale(-1, 1);
				transform.translate(-bullet.getWidth(), 0);
			}
			g.drawImage(bullet.getImage(), transform, null);
		}

		if (ALIENSHIP_ALIVE) { // spawn alienship
			transform.setToTranslation(alienSprite.getX(), alienSprite.getY());
			// if the sprite is moving left, flip the image
			if (alienSprite.getVelocityX() < 0) {
				transform.scale(-1, 1);
				transform.translate(-alienSprite.getWidth(), 0);
			}
			g.drawImage(alienSprite.getImage(), transform, null);
		}

		if (isPaused()) {

			Font font = new Font("Tahoma", Font.PLAIN, 15);
			for (int i = 0; i < buttonFrame.size(); i++) {
				g.setFont(font);
				g.setColor(buttonBackgroundColor);
				// System.out.println(buttons.get(buttonNames[i]).x);
				g.fillRect(buttonFrame.get(buttonNames[i]).x,
						buttonFrame.get(buttonNames[i]).y,
						buttonFrame.get(buttonNames[i]).width,
						buttonFrame.get(buttonNames[i]).height);
				if (pauseMenuIndex == i)
					g.setColor(Color.red);
				else
					g.setColor(buttonTextColor);

				String optionString = buttonNames[i];

				if (optionString == "Gravitation Exist: ")
					optionString += GRAVITATIONAL_OBJECT_ACTIVE;
				else if (optionString == "Gravitation Visible: ")
					optionString += GRAVITATIONAL_OBJECT_VISIBLE;
				else if (optionString == "Unlimited Lives: ")
					optionString += UNLIMITED_LIVES;
				else if (optionString == "Num Of Astroids: ")
					optionString += NUM_ASTEROIDS;
				else if (optionString == "Starting Level: ")
					optionString += CURRENT_LEVEL;
				else if (optionString == "Multiplayer: ")
					optionString += MULTIPLAYER_ACTIVE;
				g.drawString(optionString,
						buttonFrame.get(buttonNames[i]).x + 15,
						buttonFrame.get(buttonNames[i]).y + 15);

				String delims = "\\n";
				String[] tokens = highScoreObj.getHighScoreString().split(
						delims);
				int newline = g.getFont().getSize() + 5;
				int y = buttonFrame.get(buttonNames[7]).y;
				int x = buttonFrame.get(buttonNames[7]).x;
				for (int i1 = 0; i1 < tokens.length; i1++) {
					g.drawString(tokens[i1], x + 300, y += newline);
				}
			}

		}

	}

	/**
	 * Tests whether the game is paused or not.
	 */
	public boolean isPaused() {
		return paused;
	}

	/**
	 * Sets the paused state.
	 */
	public void setPaused(boolean p) {
		if (paused != p) {
			this.paused = p;
			inputUpdate.resetAllGameActions();
		}
	}

	/**
	 * Checks input from GameActions that can be pressed regardless of whether
	 * the game is paused or not.
	 */
	public void checkSystemInput() {
		if (pause.isPressed()) {
			setPaused(!isPaused());
		}
		if (exit.isPressed()) {
			exitGame = true;
		}
	}

	/**
	 * Checks input from GameActions that can be pressed only when the game is
	 * not paused.
	 */
	public void checkGameInput() {
		if (turnRight.isPressed()) {
			playerSprites.get(0).setAngle(
					playerSprites.get(0).getAngle() + Math.toRadians(225));

		}
		if (turnLeft.isPressed()) {
			playerSprites.get(0)
					.setAngle(
							playerSprites.get(0).getAngle()
									- Math.toRadians(225) + 360);
		}
		if (moveForward.isPressed()) {
			playerSprites
					.get(0)
					.setX(playerSprites.get(0).getX()
							+ (float) (playerSpeed * Math.sin(Math
									.toRadians(playerSprites.get(0).getAngle()))));
			playerSprites
					.get(0)
					.setY(playerSprites.get(0).getY()
							- (float) (playerSpeed * Math.cos(Math
									.toRadians(playerSprites.get(0).getAngle()))));

		}
		if (moveBackward.isPressed()) {
			playerSprites
					.get(0)
					.setX(playerSprites.get(0).getX()
							- (float) (playerSpeed * Math.sin(Math
									.toRadians(playerSprites.get(0).getAngle()))));
			playerSprites
					.get(0)
					.setY(playerSprites.get(0).getY()
							+ (float) (playerSpeed * Math.cos(Math
									.toRadians(playerSprites.get(0).getAngle()))));

		}
		if (fireBulletsShip0.isPressed()) {
			addBulletsToScreen(0);
		}
		if (MULTIPLAYER_ACTIVE) {
			if (turnRightPlayer2.isPressed()) {
				playerSprites.get(1).setAngle(
						playerSprites.get(1).getAngle() + Math.toRadians(225));
			}
			if (turnLeftPlayer2.isPressed()) {
				playerSprites.get(1).setAngle(
						playerSprites.get(1).getAngle() - Math.toRadians(225)
								+ 360);
			}
			if (moveForwardPlayer2.isPressed()) {
				playerSprites.get(1).setX(
						playerSprites.get(1).getX()
								+ (float) (playerSpeed * Math.sin(Math
										.toRadians(playerSprites.get(1)
												.getAngle()))));
				playerSprites.get(1).setY(
						playerSprites.get(1).getY()
								- (float) (playerSpeed * Math.cos(Math
										.toRadians(playerSprites.get(1)
												.getAngle()))));
			}
			if (moveBackwardPlayer2.isPressed()) {
				playerSprites.get(1).setX(
						playerSprites.get(1).getX()
								- (float) (playerSpeed * Math.sin(Math
										.toRadians(playerSprites.get(1)
												.getAngle()))));
				playerSprites.get(1).setY(
						playerSprites.get(1).getY()
								+ (float) (playerSpeed * Math.cos(Math
										.toRadians(playerSprites.get(1)
												.getAngle()))));
			}
			if (fireBulletsShip1.isPressed()) {
				addBulletsToScreen(1);
			}
		}
	}

	/**
	 * Creates GameActions and maps them to keys.
	 */
	public void createGameActions() {
		fireBulletsShip0 = new GameAction("fireBullets",
				GameAction.DETECT_INITAL_PRESS_ONLY);
		exit = new GameAction("exit", GameAction.DETECT_INITAL_PRESS_ONLY);
		turnLeft = new GameAction("turnLeft");
		turnRight = new GameAction("turnRight");
		moveForward = new GameAction("moveForward");
		moveBackward = new GameAction("moveBackward");
		pause = new GameAction("pause", GameAction.DETECT_INITAL_PRESS_ONLY);

		fireBulletsShip1 = new GameAction("fireBullets1",
				GameAction.DETECT_INITAL_PRESS_ONLY);
		turnLeftPlayer2 = new GameAction("turnLeftPlayer2");
		turnRightPlayer2 = new GameAction("turnRightPlayer2");
		moveForwardPlayer2 = new GameAction("moveForwardPlayer2");
		moveBackwardPlayer2 = new GameAction("moveBackwardPlayer2");
		pauseEnter = new GameAction("pauseEnter");

		inputUpdate.mapToKey(pause, KeyEvent.VK_ESCAPE);
		inputUpdate.mapToKey(exit, KeyEvent.VK_E);

		// move with the arrow keys... player 1
		inputUpdate.mapToKey(turnLeft, KeyEvent.VK_LEFT);
		inputUpdate.mapToKey(turnRight, KeyEvent.VK_RIGHT);
		inputUpdate.mapToKey(moveForward, KeyEvent.VK_UP);
		inputUpdate.mapToKey(moveBackward, KeyEvent.VK_DOWN);

		inputUpdate.mapToKey(fireBulletsShip0, KeyEvent.VK_ALT);

		// // move with the arrow keys...
		inputUpdate.mapToKey(turnLeftPlayer2, KeyEvent.VK_A);
		inputUpdate.mapToKey(turnRightPlayer2, KeyEvent.VK_D);
		inputUpdate.mapToKey(moveForwardPlayer2, KeyEvent.VK_W);
		inputUpdate.mapToKey(moveBackwardPlayer2, KeyEvent.VK_S);

		inputUpdate.mapToKey(fireBulletsShip1, KeyEvent.VK_SPACE);

		inputUpdate.mapToKey(pauseEnter, KeyEvent.VK_ENTER);

	}

	private void addAlienBulletsToScreen() {
		if (System.currentTimeMillis() - previousAlienBulletTime > (ALIENSHIP_BULLET_FIRE_INTERVAL / (CURRENT_LEVEL))) {
			previousAlienBulletTime = System.currentTimeMillis();
			for (int i = 0; i < 4; i++) {
				// System.out.println("adding bullet");
				Animation anim = new Animation();
				anim.addFrame(bulletImage, 250);
				bulletSprites.add(new Bullet(anim, screen.getWidth(), 2));

				// set bullet starting location
				bulletSprites.get(bulletSprites.size() - 1).setX(
						alienSprite.getCenterX());
				bulletSprites.get(bulletSprites.size() - 1).setY(
						alienSprite.getCenterY() + i * i
								* bulletSprites.get(0).getHeight());

				// set velocity
				bulletSprites.get(bulletSprites.size() - 1).setVelocityX(0.6f);
				bulletSprites.get(bulletSprites.size() - 1).setVelocityY(0.6f);
				bulletSprites.get(bulletSprites.size() - 1).setAngle(180);
			}
		}

	}

	// Creates bullets whenever spacebar is pressed
	public void addBulletsToScreen(int playerIndex) {
		if (playerBulletCount == 4) {
			playerBulletCount = 0;
			return;
		} else {
			playerBulletCount++;

		}

		Animation anim = new Animation();
		anim.addFrame(bulletImage, 250);
		bulletSprites.add(new Bullet(anim, screen.getWidth(), playerIndex));

		// set bullet starting location
		bulletSprites.get(bulletSprites.size() - 1).setX(
				playerSprites.get(playerIndex).getX());
		bulletSprites.get(bulletSprites.size() - 1).setY(
				playerSprites.get(playerIndex).getY());

		// set velocity
		bulletSprites.get(bulletSprites.size() - 1).setVelocityX(0.5f);
		bulletSprites.get(bulletSprites.size() - 1).setVelocityY(0.5f);

		bulletSprites.get(bulletSprites.size() - 1).setAngle(
				playerSprites.get(playerIndex).getAngle());
	}

	public static void respawnSpaceship(int playerIndex) {
		playerSprites.get(playerIndex).setX(
				(float) Math.random()
						* (screen.getWidth() - playerSprites.get(playerIndex)
								.getWidth()));
		playerSprites.get(playerIndex).setY(
				(float) Math.random()
						* (screen.getHeight() - playerSprites.get(playerIndex)
								.getHeight()));
	}

	public static ScreenUpdate getScreen() {
		return screen;
	}

	public void updateScoreAndLivesOnScreen(Graphics2D g) {
		Font font = new Font("Arial", Font.PLAIN, 15);
		g.setFont(font);
		g.setColor(Color.white);

		g.drawString("Level : " + CURRENT_LEVEL, 50, 50);

		for (int i = 0; i < playerSprites.size(); i++) {
			g.drawString(
					"Player " + (i + 1) + " Score : "
							+ playerSprites.get(i).getPlayerScore(),
					((i + 1) * 200) + (i * 400), 50);
			if (UNLIMITED_LIVES == true) {
				g.drawString("Player " + (i + 1) + " Lives Remaining : "
						+ "\u221E", ((i + 2) * 200) + (i * 400), 50);
			} else
				g.drawString("Player " + (i + 1) + " Lives Remaining : "
						+ playerSprites.get(i).getPlayerLife(), ((i + 2) * 200)
						+ (i * 400), 50);
		}

		g.setFont(null);

	}

	public void checkGameInputPause() {
		if (moveForward.isPressed() && pauseMenuIndex > 0) {
			pauseMenuIndex--;
			moveForward.reset();
			System.out.println(pauseMenuIndex);
		}
		if (moveBackward.isPressed() && pauseMenuIndex < buttonNames.length - 1) {
			moveBackward.reset();
			pauseMenuIndex++;
			System.out.println(pauseMenuIndex);
		}
		if (turnLeft.isPressed()) {
			turnLeft.reset();
			if (pauseMenuIndex == 6 && NUM_ASTEROIDS > 3) {
				NUM_ASTEROIDS--;
				loadAsteroidImages();
			}
			if (pauseMenuIndex == 8 && CURRENT_LEVEL > 1) {
				playerSprites.get(0).setPlayerLife(3);
				asteroidSprites.clear();
				CURRENT_LEVEL--;
				NUM_ASTEROIDS = 2 * CURRENT_LEVEL + 1;
				loadAsteroidImages();
			}

		}
		if (turnRight.isPressed()) {
			turnRight.reset();
			if (pauseMenuIndex == 6) {
				NUM_ASTEROIDS++;
				loadAsteroidImages();
			}
			if (pauseMenuIndex == 8 && CURRENT_LEVEL < 10) {
				playerSprites.get(0).setPlayerLife(3);
				asteroidSprites.clear();
				CURRENT_LEVEL++;
				NUM_ASTEROIDS = 2 * CURRENT_LEVEL + 1;
				loadAsteroidImages();
			}
		}
		if (pauseEnter.isPressed()) {
			pauseEnter.reset();
			//menu options
			switch (pauseMenuIndex) {
			case 0:
				pause.press();
				break;
			case 1:
				SAVE_GAME = true;
				break;
			case 2:
				LOAD_GAME = true;
				break;
			case 3:
				GRAVITATIONAL_OBJECT_ACTIVE = !GRAVITATIONAL_OBJECT_ACTIVE;
				break;
			case 4:
				GRAVITATIONAL_OBJECT_VISIBLE = !GRAVITATIONAL_OBJECT_VISIBLE;
				break;
			case 5:
				UNLIMITED_LIVES = !UNLIMITED_LIVES;
				if (UNLIMITED_LIVES == true) {
					for (int i = 0; i < playerSprites.size(); i++)
						playerSprites.get(i).setPlayerLife(9999);
				} else {
					for (int i = 0; i < playerSprites.size(); i++)
						playerSprites.get(i).setPlayerLife(3);
				}
				break;
			case 6:
				break;
			case 7:
				/*
				 * if (JOptionPane.showConfirmDialog(null,
				 * "Are you sure you want to delete highscore?",
				 * "Reset high score", JOptionPane.YES_NO_OPTION) ==
				 * JOptionPane.YES_OPTION) {
				 */
				highScoreObj.reset();
				// }
				break;
			case 8:
				break;
			case 9:
				MULTIPLAYER_ACTIVE = !MULTIPLAYER_ACTIVE;
				if (!MULTIPLAYER_ACTIVE)
					playerSprites.remove(1);
				break;
			case 10:
				exit.press();
				break;
			}
		}

	}

	private void saveObjectsToFile() {
		try {
			PrintWriter out = new PrintWriter(filename);
			out.println("#AsteroidSprites" + "#" + asteroidSprites.size());
			for (int i = 0; i < asteroidSprites.size(); i++) {
				Sprite s = asteroidSprites.get(i);
				String spriteDetails = s.getX() + "#" + s.getY() + "#"
						+ s.getVelocityX() + "#" + s.getVelocityY() + "#"
						+ s.getAngle() + "#" + s.getAsteroidType() + "#"
						+ s.getAlienLives();
				out.println(spriteDetails);
			}

			out.println("#AlienSprite");
			Sprite s = alienSprite;
			String spriteDetails = s.getX() + "#" + s.getY() + "#"
					+ s.getVelocityX() + "#" + s.getVelocityY() + "#"
					+ s.getAngle() + "#" + s.getAsteroidType() + "#"
					+ s.getAlienLives() + "#" + ALIENSHIP_ALIVE;
			out.println(spriteDetails);

			out.println("#PlayerSprites" + "#" + playerSprites.size());
			for (int i = 0; i < playerSprites.size(); i++) {
				Player s1 = playerSprites.get(i);
				String spriteDetails1 = s1.getX() + "#" + s1.getY() + "#"
						+ s1.getVelocityX() + "#" + s1.getVelocityY() + "#"
						+ s1.getAngle() + "#" + s1.getPlayerLife() + "#"
						+ s1.getPlayerScore();
				out.println(spriteDetails1);
			}

			out.println("#GravitationalObjectActive");
			out.println(GRAVITATIONAL_OBJECT_ACTIVE);

			out.println("#GravitationalObjectVisible");
			out.println(GRAVITATIONAL_OBJECT_VISIBLE);

			out.println("#MultiplayerActive");
			out.println(MULTIPLAYER_ACTIVE);

			out.println("#NumberOfAsteroids");
			out.println(NUM_ASTEROIDS);

			out.println("#CurrentLevel");
			out.println(CURRENT_LEVEL);

			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void readObjectsFromFile() {
		asteroidSprites = new LinkedList<Sprite>();
		playerSprites = new LinkedList<Player>();

		try {
			File file = new File(filename);
			FileReader fileReader = new FileReader(file);
			BufferedReader bufferedReader = new BufferedReader(fileReader);
			StringBuffer stringBuffer = new StringBuffer();
			String line;
			while ((line = bufferedReader.readLine()) != null) {
				stringBuffer.append(line);
				stringBuffer.append("\n");
				// System.out.println(line);
				if (line.contains("#AsteroidSprites")) {
					NUM_ASTEROIDS = new Integer(line.split("#")[2]).intValue();
					for (int i = 0; i < NUM_ASTEROIDS; i++) {
						line = bufferedReader.readLine();
						String[] tempList = line.split("#");

						Animation anim = new Animation();
						if (tempList[5].equals("parent")) {
							anim.addFrame(asteroidImage, 250);
						} else {
							anim.addFrame(childAsteroidImage, 250);
						}
						asteroidSprites.add(new Sprite(anim, tempList[5]));

						// select random starting location
						asteroidSprites.get(asteroidSprites.size() - 1).setX(
								new Float(tempList[0]).floatValue());
						asteroidSprites.get(asteroidSprites.size() - 1).setY(
								new Float(tempList[1]).floatValue());

						asteroidSprites.get(asteroidSprites.size() - 1)
								.setVelocityX(
										new Float(tempList[2]).floatValue());
						asteroidSprites.get(asteroidSprites.size() - 1)
								.setVelocityY(
										new Float(tempList[3]).floatValue());

						asteroidSprites
								.get(asteroidSprites.size() - 1)
								.setAngle(new Double(tempList[4]).doubleValue());
						asteroidSprites.get(asteroidSprites.size() - 1)
								.setAlienLives(
										new Integer(tempList[6]).intValue());
					}
				} else if (line.equals("#AlienSprite")) {
					line = bufferedReader.readLine();
					String[] tempList = line.split("#");
					alienSprite.setX(new Float(tempList[0]).floatValue());
					alienSprite.setY(new Float(tempList[1]).floatValue());
				} else if (line.contains("#PlayerSprites")) {
					int numPlayers = new Integer(line.split("#")[2]).intValue();
					for (int i = 0; i < numPlayers; i++) {
						line = bufferedReader.readLine();
						// System.out.println(line);
						String[] tempList = line.split("#");

						Animation anim = new Animation();
						anim.addFrame(playerImage, 250);
						playerSprites.add(new Player(anim, new Integer(
								tempList[5]).intValue()));
						playerSprites.get(i).setX(
								new Float(tempList[0]).floatValue());
						playerSprites.get(i).setY(
								new Float(tempList[1]).floatValue());
					}
				} else if (line.equals("#GravitationalObjectActive")) {
					line = bufferedReader.readLine();
					// System.out.println(line);
					if (line.equals("false")) {
						GRAVITATIONAL_OBJECT_ACTIVE = false;
					} else {
						GRAVITATIONAL_OBJECT_ACTIVE = true;
					}
				} else if (line.equals("#GravitationalObjectVisible")) {
					line = bufferedReader.readLine();
					// System.out.println(line);
					if (line.equals("false")) {
						GRAVITATIONAL_OBJECT_VISIBLE = false;
					} else {
						GRAVITATIONAL_OBJECT_VISIBLE = true;
					}
				} else if (line.equals("#MultiplayerActive")) {
					line = bufferedReader.readLine();
					// System.out.println(line);
					if (line.equals("false")) {
						MULTIPLAYER_ACTIVE = false;
					} else {
						MULTIPLAYER_ACTIVE = true;
					}
				} else if (line.equals("#NumberOfAsteroids")) {
					line = bufferedReader.readLine();
					// System.out.println(line);
					NUM_ASTEROIDS = new Integer(line).intValue();
				} else if (line.equals("#CurrentLevel")) {
					line = bufferedReader.readLine();
					// System.out.println(line);
					CURRENT_LEVEL = new Integer(line).intValue();
				}
			}
			fileReader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
