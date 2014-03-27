import java.awt.Graphics2D;

import javax.swing.JOptionPane;


public class CollisionDetection extends Thread{
	public CollisionDetection() {
		
	}
	
	private void playerDied()
	{
		//Asteroids.soundExplosion.play();
		Asteroids.sounds.play("bomb");
		System.out.println("DIE");
	}
	
	private void highScore()
	{
		playerDied();
		int p1Score = Asteroids.playerSprites.get(0).getPlayerScore();
		Asteroids.highScoreObj.writeHighScore(p1Score, 1);
		
		if (Asteroids.MULTIPLAYER_ACTIVE)
		{	
			int p2Score = Asteroids.playerSprites.get(1).getPlayerScore();
			Asteroids.highScoreObj.writeHighScore(p2Score,2);
		}
		Asteroids.highScoreObj.showScoreBoard();
	}
	
	private void gameEnd()
	{
		Asteroids.inputManager.resetAllGameActions();
		Asteroids.asteroidSprites.clear();
		highScore();	
		Asteroids.initilization();
	}
	
	public void run() {
		while(!Asteroids.exitGame) {
			
			try{
				// Check for Asteroid-Player Collision
				for(int i = 0; i < Asteroids.asteroidSprites.size(); i++) {
					for(int j = 0; j < Asteroids.playerSprites.size(); j++) {
						if(isPlayerWithinAsteroidsRange(Asteroids.asteroidSprites.get(i), Asteroids.playerSprites.get(j))) {
								if(Asteroids.playerSprites.get(j).getPlayerLife() > 0) {  // decrement player life if there's lives remaining and there's a collision
									Asteroids.playerSprites.get(j).setPlayerLife(Asteroids.playerSprites.get(j).getPlayerLife() - 1);
									
									playerDied();
									Asteroids.respawnSpaceship(j);
									
									//System.out.println("life decremented");
								}	
								else { // player has no more lives and there's a collision, so end game!
									//Asteroids.exitGame = true;
									// Asteroids.highScoreObj.showScoreBoard()
									
	
									gameEnd();
									
									break;
									
									//System.out.println("ending game");
								}
								//System.out.println("Lives Remaining => " + Asteroids.playerSprites.get(j).getPlayerLife());							
						}
					}
				}
				
				// Check for Bullet-Asteroid Collision
				for(int j = 0; j < Asteroids.asteroidSprites.size(); j++) {
					for(int i = 0; i < Asteroids.bulletSprites.size(); i++) {
						if(bulletTouchingAsteroid(Asteroids.bulletSprites.get(i), Asteroids.asteroidSprites.get(j))) { // if collision exists
							if(Asteroids.asteroidSprites.get(j).getAsteroidType().equals("parent")) { // check if asteroid is a parent, in which case spawn three new asteroids before killing this one
								Asteroids.addAsteroidsInPlaceOfKilledAsteroid(Asteroids.asteroidSprites.get(j), 3);							
							}
							int playerIndex = Asteroids.bulletSprites.get(i).getPlayerIndex();
							Asteroids.playerSprites.get(playerIndex).setPlayerScore(Asteroids.playerSprites.get(playerIndex).getPlayerScore() + 5);
							Asteroids.asteroidSprites.remove(j); // kill asteroid
							Asteroids.bulletSprites.remove(i); // kill bullet
							
	
							break;
						}
					}
				}
				// Check for player bullets colliding with alienship
				if(Asteroids.ALIENSHIP_ALIVE) {
					for(int i = 0; i < Asteroids.bulletSprites.size(); i++) {
						if(bulletTouchingAlienship(Asteroids.bulletSprites.get(i), Asteroids.alienSprite)) { // if collision exists
							if(Asteroids.alienSprite.getAlienLives() > 0) {
								//	System.out.println("alien hit");
								Asteroids.alienSprite.setAlienLives(Asteroids.alienSprite.getAlienLives() - 1);
								int playerIndex = Asteroids.bulletSprites.get(i).getPlayerIndex();
								Asteroids.playerSprites.get(playerIndex).setPlayerScore(Asteroids.playerSprites.get(playerIndex).getPlayerScore() + 100);
							}
							else {
								//	System.out.println("alien killed");
								Asteroids.ALIENSHIP_ALIVE = false;
								Asteroids.alienSprite.setAlienLives(3);							
							}
							Asteroids.bulletSprites.remove(i); // kill bullet
						}
					}
				}
				// Check for player and alien bullet collision
				for(int j = 0; j < Asteroids.playerSprites.size(); j++) {
					for(int i = 0; i < Asteroids.bulletSprites.size(); i++) {
						if(alienBulletTouchingPlayer(Asteroids.bulletSprites.get(i), Asteroids.playerSprites.get(j))) { // if collision exists
							//System.out.println("alien bullet hit you");
							if(Asteroids.playerSprites.get(j).getPlayerLife() > 0) {  // decrement player life if there's lives remaining and there's a collision
								Asteroids.playerSprites.get(j).setPlayerLife(Asteroids.playerSprites.get(j).getPlayerLife() - 1);
								playerDied();
								Asteroids.respawnSpaceship(j);
								//System.out.println("life decremented");
							}	
							else { // player has no more lives and there's a collision, so end game!
	
								gameEnd();
								//Asteroids.exitGame = true;
								break;
								//System.out.println("ending game");
							}
						}
					}
				}
				// check for player-alienship collision
				if(Asteroids.ALIENSHIP_ALIVE) {
					for(int j = 0; j < Asteroids.playerSprites.size(); j++) {
						if(isPlayerTouchingAlien(Asteroids.playerSprites.get(j), Asteroids.alienSprite)) {
								//System.out.println("Player touching alien");
								if(Asteroids.playerSprites.get(j).getPlayerLife() > 0) {  // decrement player life if there's lives remaining and there's a collision
									Asteroids.playerSprites.get(j).setPlayerLife(Asteroids.playerSprites.get(j).getPlayerLife() - 1);
									playerDied();
									Asteroids.respawnSpaceship(j);
									//System.out.println("life decremented");
								}	
								else { // player has no more lives and there's a collision, so end game!
									//Asteroids.exitGame = true;
									gameEnd();
									break;
									//System.out.println("ending game");
								}
								//System.out.println("Lives Remaining => " + Asteroids.playerSprites.get(j).getPlayerLife());							
						}
					}
				}
				checkBulletDistanceTravelled(); // To delete bullets that have travelled more than the screen.width()
				
				System.out.print(""); // for some reason all the above code only works if this print statement is in here, WTF?
			}
			catch (Exception e){ }
		}
	}
	
	private boolean isPlayerTouchingAlien(Sprite p, Sprite alien) {
		double rangeOfAlien = Math.sqrt(Math.pow(alien.getHeight()/2, 2) + Math.pow(alien.getWidth()/2, 2));
		double distanceBetweenAlienAndPlayer = Math.sqrt(Math.pow(alien.getCenterX() - p.getCenterX(), 2) + Math.pow(alien.getCenterY() - p.getCenterY(), 2));
		if(distanceBetweenAlienAndPlayer < rangeOfAlien) {
			//System.out.println("true");
			return true;
		}
		return false;
	}
	
	private boolean alienBulletTouchingPlayer(Bullet b, Sprite s) {
		if(b.getPlayerIndex() != 2) {
			return false;
		}
		double rangeOfPlayership = Math.sqrt(Math.pow(s.getHeight()/2, 2) + Math.pow(s.getWidth()/2, 2));
		double distanceBetweenBulletAndPlayer = Math.sqrt(Math.pow(s.getCenterX() - b.getCenterX(), 2) + Math.pow(s.getCenterY() - b.getCenterY(), 2));
		if(distanceBetweenBulletAndPlayer < rangeOfPlayership) {
			return true;
		}
		return false;
	}
	
	private boolean bulletTouchingAlienship(Bullet b, Sprite s) {
		if(b.getPlayerIndex() == 2) {
			return false;
		}
		double rangeOfAlienship = Math.sqrt(Math.pow(s.getHeight()/2, 2) + Math.pow(s.getWidth()/2, 2));
		double distanceBetweenBulletAndAlien = Math.sqrt(Math.pow(s.getCenterX() - b.getCenterX(), 2) + Math.pow(s.getCenterY() - b.getCenterY(), 2));
		if(distanceBetweenBulletAndAlien < rangeOfAlienship) {
			return true;
		}
		return false;
	}
	
	private void checkBulletDistanceTravelled() {
		for(int i = 0; i < Asteroids.bulletSprites.size(); i++) {	
			Bullet b = Asteroids.bulletSprites.get(i);
			if(b.getBulletDistanceTravelled() > Asteroids.getScreen().getWidth()) {
				Asteroids.bulletSprites.remove(i);
			}
		}
	}
	
	private boolean bulletTouchingAsteroid(Bullet b, Sprite s) {
		if(b.getPlayerIndex() == 2) {
			return false;
		}
		double rangeOfAsteroid = Math.sqrt(Math.pow(s.getHeight()/2, 2) + Math.pow(s.getWidth()/2, 2));
		double distanceBetweenBulletAndPlayer = Math.sqrt(Math.pow(s.getCenterX() - b.getCenterX(), 2) + Math.pow(s.getCenterY() - b.getCenterY(), 2));
		if(distanceBetweenBulletAndPlayer < rangeOfAsteroid) {
			//System.out.println("bullet-Asteroid collision");
			return true;
		}
		return false;
	}
	
	private boolean isPlayerWithinAsteroidsRange(Sprite s, Player p) {
		double rangeOfAsteroid = Math.sqrt(Math.pow(s.getHeight()/2, 2) + Math.pow(s.getWidth()/2, 2));
		double distanceBetweenAsteroidAndPlayer = Math.sqrt(Math.pow(s.getCenterX() - p.getCenterX(), 2) + Math.pow(s.getCenterY() - p.getCenterY(), 2));
		//System.out.println("range of Asteroid => " + rangeOfAsteroid + " distance between A-P => " + distanceBetweenAsteroidAndPlayer);
		if(distanceBetweenAsteroidAndPlayer < rangeOfAsteroid) {
			//System.out.println("true");
			return true;
		}
		//System.out.println("false");
		return false;
	}
}
