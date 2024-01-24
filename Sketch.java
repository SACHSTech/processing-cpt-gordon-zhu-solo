import java.util.Deque;
import java.util.LinkedList;
import java.util.Timer;
import java.util.Iterator;

import processing.core.PApplet;
import processing.core.PImage;

//Main Sketch Class
public class Sketch extends PApplet {

  // Initializing variables
  float fltPlayerX = 600;
  float fltPlayerY = 740;
  boolean boolInitialize = true;;
  float mapPosX = fltPlayerX;
  float mapPosY = fltPlayerY;
  int intSpeedX = 0;
  int intSpeedY = 0;
  int intborderX = 300;
  int intborderY = 220;

  int lives = 30;

  // initialization for other methods
  long startTime = System.currentTimeMillis();
  boolean boolMouse = false;

  PImage imgPlayer;
  PImage imgPlayer2;
  PImage imgBackground;
  PImage imgPermBackground;
  PImage imglvl1;
  PImage imglvl2;
  PImage imgFriendlyProjectile;
  PImage imgEnemyProjectile;
  PImage imgWinScreen;
  PImage imgLooseScreen;

  // initialization for enemies
  int[] enemyPosX = new int[10];
  int[] enemyPosY = new int[10];
  int[] enemyLives = new int[10];
  long[] enemyLastHitTime = new long[10];
  PImage imgEnemy1;
  PImage imgEnemy2;

  // movement
  boolean boolUp = false;
  boolean boolLeft = false;
  boolean boolDown = false;
  boolean boolRight = false;

  int intCurrentLevel = 0;

  // Projectiles to be drawn are added to a list
  Deque<Float> projectileX = new LinkedList<>();
  Deque<Float> projectileY = new LinkedList<>();
  Deque<Integer> projectileType = new LinkedList<>(); // player or enemy bullet

  Deque<Float> angles = new LinkedList<>();
  Deque<Integer> timeOfSpawn = new LinkedList<>();
  // Keeps track of the map shift[(x, y)location] when the projectile was drawn
  Deque<Integer> dqMapX = new LinkedList<>();
  Deque<Integer> dqMapY = new LinkedList<>();

  //Speed of the projectile 
  Deque<Float> speedX = new LinkedList<>();
  Deque<Float> speedY = new LinkedList<>();

  // Time
  // Timer mouseCheck = new Timer();
  int lastTime = 0;
  int lastTime2 = 0;
  long lastHitTime = 0;
  Timer projectileCheck = new Timer();

  public void settings() {
    // put your size call here
    size(1000, 667);
  }

  /**
   * Called once at the beginning of execution. Add initial set up
   * values here i.e background, stroke, fill etc.
   */
  public void setup() {

    imgPlayer = loadImage("imgPlayer.png");
    imgPlayer2 = loadImage("imgPlayer2.png");

    imgBackground = loadImage("loadingScreen.jpg");
    imglvl1 = loadImage("lvl1.png");
    imgPermBackground = loadImage("permanentBackground.jpg");

    imgFriendlyProjectile = loadImage("Projectile.png");
    imgEnemyProjectile = loadImage("Projectile2.png");

    imgEnemy1 = loadImage("Enemy.png");
    imgEnemy2 = loadImage("Enemy2.png");

    imgWinScreen = loadImage("winScreen.jpg");
    imgLooseScreen = loadImage("looseScreen.jpg");

    manualEnemySpawn();

  }

  /**
   * Called repeatedly, anything drawn to the screen goes here
   */
  public void draw() {

    // loading screen --> 1 levels
    // --> each level has scrolling map
    // --> wall detection 

    if (intCurrentLevel == 0) {
      image(imgBackground, 0, 0);
      if (keyCode == ENTER)
        intCurrentLevel = 1;
    } else if (intCurrentLevel == 1) {
      if (boolInitialize) {
        fltPlayerX = 750;
        fltPlayerY = 500;
        mapPosX = 400;
        mapPosY = 100;
        boolInitialize = false;
      }

      drawMap(imglvl1);
      movement();
      if (mousePressed) {
        ifMousePressed(); 
      }
      drawEnemy();
      enemyProjectileSpawn();
      drawProjectile();

    }
    // display lives, each square = 10 lives
    noStroke();
    fill(242, 10, 25);
    if (lives >= 0)
      rect(950, 20, 30, 30);
    if (lives >= 10)
      rect(900, 20, 30, 30);
    if (lives >= 20)
      rect(850, 20, 30, 30);
    

    // game end
    if (lives <= 0) {
      image(imgLooseScreen, 0, 0);
    }

    //check if the game ended and won the game
    boolean endGame = true; 
    for(int i = 0 ; i< 10; i ++){
        if(enemyLives[i]>0)
        endGame = false; 
    }
    if(endGame){
      image(imgWinScreen, 0 , 0);
    }


  }

  /**
   * Description: draws the scrolling map onto the screen taking into account of
   * player position
   * including player and all other entities
   * 
   * @param : PI Image (map to draw)
   *          No return
   * 
   * @author: Gordon Z
   */
  void drawMap(PImage imgMap) {

    if (Math.abs(width - fltPlayerX) < intborderX) {
      mapPosX -= intSpeedX;
      mapPosX = Math.max(width - 850, mapPosX);
    }
    if (Math.abs(height - fltPlayerY) < intborderY) {
      mapPosY -= intSpeedY;
      mapPosY = Math.max(height - 800, mapPosY);
    }
    if (fltPlayerX < intborderX) {
      mapPosX -= intSpeedX;
      mapPosX = Math.min(850, mapPosX);
    }
    if (fltPlayerY < intborderY) {
      mapPosY -= intSpeedY;
      mapPosY = Math.min(800, mapPosY);
    }
    image(imgPermBackground, 0, 0);
    image(imgMap, mapPosX - 750, mapPosY - 750);
    if (intSpeedX >= 0) {
      image(imgPlayer, fltPlayerX - 30, fltPlayerY - 30);
    } else {
      image(imgPlayer2, fltPlayerX - 30, fltPlayerY - 30);
    }
  }

  /**
   * Description: detects movement from keys to move the character. Also accounts
   * for wall collision
   * 
   * No param
   * No return
   * 
   * @author: Gordon Z
   */
  public void movement() {
    intSpeedX = 0;
    intSpeedY = 0;
    int pixelColor = 0;
    int[] sumRGB = new int[3];
    // ----------------------------------------------------------------------------------

    if (boolUp) {
      // if you want to go up, grab the average pixel color of a 5x5 grid upwards of
      // the character
      for (int i = 0; i < 5; i++)
        for (int j = 0; j < 5; j++) {
          pixelColor = get((int) fltPlayerX + i - 3, (int) fltPlayerY - 37 + j);
          // According to documentation, bit shift is faster than "get" method
          sumRGB[0] += (pixelColor >> 16) & 0xFF; // Red component
          sumRGB[1] += (pixelColor >> 8) & 0xFF; // Green component
          sumRGB[2] += pixelColor & 0xFF; // Blue component
        }
      int avgR = sumRGB[0] / 25;
      int avgG = sumRGB[1] / 25;
      int avgB = sumRGB[2] / 25;
      sumRGB[0] = 0;
      sumRGB[1] = 0;
      sumRGB[2] = 0;
      // checks the average pixel colour moving upwards and allows the character to
      // move upwards if its not a light or dark gray color
      if (!((avgR <= 250 && avgR >= 210 && avgB <= 230 && avgB >= 155
          && avgG <= 255 && avgG >= 220)
          || (avgR <= 70 && avgR >= 30 && avgB <= 120 && avgB >= 90
              && avgG <= 100 && avgG >= 70))) {
        intSpeedY = -4;
        fltPlayerY += intSpeedY;
        fltPlayerY = Math.max(intborderY - 20, fltPlayerY);
      }
    }
    // ----------------------------------------------------------------------------------
    if (boolLeft) {
      // if you want to go left, grab the average pixel color of a 5x5 grid to the
      // left
      for (int i = 0; i < 5; i++)
        for (int j = 0; j < 5; j++) {
          pixelColor = get((int) fltPlayerX + i - 37, (int) fltPlayerY - 3 + j);
          // According to documentation, bit shift is faster than "get" method
          sumRGB[0] += (pixelColor >> 16) & 0xFF; // Red component
          sumRGB[1] += (pixelColor >> 8) & 0xFF; // Green component
          sumRGB[2] += pixelColor & 0xFF; // Blue component
        }

      int avgR = sumRGB[0] / 25;
      int avgG = sumRGB[1] / 25;
      int avgB = sumRGB[2] / 25;
      sumRGB[0] = 0;
      sumRGB[1] = 0;
      sumRGB[2] = 0;
      // checks the average pixel colour moving to the left and allows the character to
      // move if its not a light or dark gray color
      if (!((avgR <= 250 && avgR >= 210 && avgB <= 230 && avgB >= 155
          && avgG <= 255 && avgG >= 220)
          || (avgR <= 70 && avgR >= 30 && avgB <= 120 && avgB >= 90
              && avgG <= 100 && avgG >= 70))) {
        intSpeedX = -4;
        fltPlayerX += intSpeedX;
        fltPlayerX = Math.max(intborderX - 20, fltPlayerX);
      }
    }
    // ----------------------------------------------------------------------------------
    if (boolDown) {
      // if you want to go down, grab the average pixel color of a 5x5 grid downwards
      for (int i = 0; i < 5; i++)
        for (int j = 0; j < 5; j++) {
          pixelColor = get((int) fltPlayerX + i - 3, (int) fltPlayerY + 31 + j);
          // According to documentation, bit shift is faster than "get" method
          sumRGB[0] += (pixelColor >> 16) & 0xFF; // Red component
          sumRGB[1] += (pixelColor >> 8) & 0xFF; // Green component
          sumRGB[2] += pixelColor & 0xFF; // Blue component
        }
      int avgR = sumRGB[0] / 25;
      int avgG = sumRGB[1] / 25;
      int avgB = sumRGB[2] / 25;
      sumRGB[0] = 0;
      sumRGB[1] = 0;
      sumRGB[2] = 0;
      // checks the average pixel colour moving downwards and allows the character to
      // move  if its not a light or dark gray color
      if (!((avgR <= 250 && avgR >= 210 && avgB <= 230 && avgB >= 155
          && avgG <= 255 && avgG >= 220)
          || (avgR <= 70 && avgR >= 30 && avgB <= 120 && avgB >= 90
              && avgG <= 100 && avgG >= 70))) {
        intSpeedY = 4;
        fltPlayerY += intSpeedY;
        fltPlayerY = Math.min(height - intborderY + 20, fltPlayerY);
      }
    }
    // ----------------------------------------------------------------------------------
    if (boolRight) {
      // if you want to go right, grab the average pixel color of a 5x5 grid to the
      // right
      for (int i = 0; i < 5; i++)
        for (int j = 0; j < 5; j++) {
          pixelColor = get((int) fltPlayerX + i + 31, (int) fltPlayerY - 3 + j);
          // According to documentation, bit shift is faster than "get" method
          sumRGB[0] += (pixelColor >> 16) & 0xFF; // Red component
          sumRGB[1] += (pixelColor >> 8) & 0xFF; // Green component
          sumRGB[2] += pixelColor & 0xFF; // Blue component
        }
      int avgR = sumRGB[0] / 25;
      int avgG = sumRGB[1] / 25;
      int avgB = sumRGB[2] / 25;
      sumRGB[0] = 0;
      sumRGB[1] = 0;
      sumRGB[2] = 0;
      // checks the average pixel colour moving upwards and allows the character to
      // move upwards if its not a light or dark gray color
      if (!((avgR <= 250 && avgR >= 210 && avgB <= 230 && avgB >= 155
          && avgG <= 255 && avgG >= 220)
          || (avgR <= 70 && avgR >= 30 && avgB <= 120 && avgB >= 90
              && avgG <= 100 && avgG >= 70))) {
        intSpeedX = 4;
        fltPlayerX += intSpeedX;
        fltPlayerX = Math.min(width - intborderX + 20, fltPlayerX);
      }
    }

  }

  /**
   * Description: draws all projectiles currently in the list,
   * also removes projectiles which has been exsisting for too long
   * 
   * No param
   * No return
   * 
   * @author: Gordon Z
   */

  public void drawProjectile() {

    // remove projectiles which are spawned for too long
    Iterator<Integer> iteratorSpawn = timeOfSpawn.iterator();
    while (iteratorSpawn.hasNext()) {
      long spawnTime = iteratorSpawn.next();

      // Check if the element should be removed
      if (System.currentTimeMillis() - startTime - spawnTime > 2500) {
        iteratorSpawn.remove(); // Remove using the iterator's remove method
        // Also remove corresponding elements from other Deques

        projectileX.pop();
        projectileY.pop();
        projectileType.pop();
        dqMapX.pop();
        dqMapY.pop();
        angles.pop();
        speedX.pop();
        speedY.pop();
      }
    }

    // draw rest of projectiles
    Iterator<Float> iteratorX = projectileX.iterator();
    Iterator<Float> iteratorY = projectileY.iterator();
    Iterator<Float> iteratorAngles = angles.iterator();
    Iterator<Float> iteratorSpeedX = speedX.iterator();
    Iterator<Float> iteratorSpeedY = speedY.iterator();

    Iterator<Integer> iteratorprojectileType = projectileType.iterator();
    Iterator<Integer> iteratorMapX = dqMapX.iterator();
    Iterator<Integer> iteratorMapY = dqMapY.iterator();
    iteratorSpawn = timeOfSpawn.iterator();

    while (iteratorX.hasNext()) {
      float x = iteratorX.next();
      float y = iteratorY.next();
      float angle = iteratorAngles.next();
      int intProjectileType = iteratorprojectileType.next();
      float speedXValue = iteratorSpeedX.next();
      float speedYValue = iteratorSpeedY.next();
      // so that when the map scrolls, the projectiles remain at their relative
      // positions
      int shiftX = iteratorMapX.next();
      shiftX -= mapPosX;
      int shiftY = iteratorMapY.next();
      shiftY -= mapPosY;

      int temp = (int) (System.currentTimeMillis() - startTime - iteratorSpawn.next());

      // drawing the actual projectile
      pushMatrix();
      if (intProjectileType == 1) {
        translate(x + (temp * speedXValue / 5) - shiftX, y + (temp * speedYValue / 5) - shiftY);
        rotate(angle - PI / 2);
        translate(-12, -15);

        image(imgFriendlyProjectile, 0, 0);

        // bullet collision with enemy
        float tempX = x + (temp * speedXValue / 5) - shiftX - 12;
        float tempY = y + (temp * speedYValue / 5) - shiftY - 15;
        for (int i = 0; i < 10; i++) {
          // enemies get 0.5 second I-frame
          if (dist(tempX, tempY, enemyPosX[i] - 400 + mapPosX, enemyPosY[i] - 100 + mapPosY) < 20
              && System.currentTimeMillis() - enemyLastHitTime[i] > 500) {
            enemyLives[i] -= 1;
            enemyLastHitTime[i] = System.currentTimeMillis() ;
          }
        }

      } else if (intProjectileType == 2) {
        float tempX = x + (temp * speedXValue / 5) - shiftX;
        float tempY = y + (temp * speedYValue / 5) - shiftY;
        //player get 1 sec i-frame
        if (dist(fltPlayerX, fltPlayerY, tempX, tempY) < 20 && System.currentTimeMillis() - lastHitTime > 1000) {
          lives--;
          lastHitTime = System.currentTimeMillis();
        }
        image(imgEnemyProjectile, x + (temp * speedXValue / 5) - shiftX, y + (temp * speedYValue / 5) - shiftY);
      }

      popMatrix();

    }
  }

  /**
   * Description: when keys are pressed, respective keys will have their
   * associated movement boolean changed to true
   * 
   * No param
   * No return
   * 
   * @author: Gordon Z
   */
  public void keyPressed() {
    if (keyPressed) {
      if (key == 'w') {
        boolUp = true;
      }
      if (key == 'a') {
        boolLeft = true;
      }
      if (key == 's') {
        boolDown = true;
      }
      if (key == 'd') {
        boolRight = true;
      }
    }
  }

  /**
   * Description: when key is released, change movement boolean to false.
   * 
   * No param
   * No return
   * 
   * @author: Gordon Z
   */
  public void keyReleased() {
    if (key == 'w') {
      boolUp = false;
    }
    if (key == 'a') {
      boolLeft = false;
    }
    if (key == 's') {
      boolDown = false;
    }
    if (key == 'd') {
      boolRight = false;
    }

  }

  /**
   * Description: After each mouse clicked, it will check if 70 milliseconds have
   * passed. If so, it will push information into the queue for projectile to be
   * drawn later on.
   * 
   * No param
   * No return
   * 
   * @author: Gordon Z
   */
  public void ifMousePressed() {
    // check if 70 milliseconds has passed so there aren't too much projectiles.
    if (System.currentTimeMillis() - startTime - lastTime > 300) {

      // calculations of distance from mouse to center of wizard player.
      float fltCurrentHorizontal = (mouseX - (fltPlayerX));
      float fltCurrentVertical = (mouseY - (fltPlayerY));
      float fltHyp = (float) Math.sqrt(fltCurrentHorizontal * fltCurrentHorizontal
          + fltCurrentVertical * fltCurrentVertical);

      // Adding details for the projectile to be drawn later
      projectileX.addLast(fltPlayerX);
      projectileY.addLast(fltPlayerY);
      projectileType.addLast(1);
      speedX.addLast(fltCurrentHorizontal / fltHyp);
      speedY.addLast(fltCurrentVertical / fltHyp);
      dqMapX.addLast((int) mapPosX);
      dqMapY.addLast((int) mapPosY);

      /*
       * given the triangle formed by the position of player,
       * mouse position, and the line y = playerX, calculate the angle.
       * since arcsin only returns angles of [-pi/2, pi/2], additional modification
       * is required to account for full 2pi rotation.
       */
      float fltAng = asin(fltCurrentVertical / fltHyp);
      if (mouseX < fltPlayerX + 40) {
        // if the mouse is to the left of center of wizard, modify the angle
        fltAng = (float) (3.14159265358979323846 - fltAng);
      }
      angles.addLast(fltAng);

      // push the time of new projectile spawn and update the last time a projectile
      // has spawned
      timeOfSpawn.addLast((int) (System.currentTimeMillis() - startTime));
      lastTime = (int) (System.currentTimeMillis() - startTime);
    }
  }

  /**
   * Description: called to update enemy projectiles
   * 
   * No param
   * No return
   * 
   * @author: Gordon Z
   */
  public void enemyProjectileSpawn() {
    // check if 500 milliseconds has passed so there aren't too much projectiles.
    if (System.currentTimeMillis() - startTime - lastTime2 > 500) {

      for (int i = 0; i < 10; i++) {
        if (enemyLives[i] > 0) {
          // calculations of distance from mouse to center of wizard player.
          float fltCurrentHorizontal = (enemyPosX[i] - 400 + mapPosX - (fltPlayerX));
          float fltCurrentVertical = (enemyPosY[i] - 100 + mapPosY - (fltPlayerY));
          float fltHyp = (float) Math.sqrt(fltCurrentHorizontal * fltCurrentHorizontal
              + fltCurrentVertical * fltCurrentVertical);

          // Adding details for the projectile to be drawn later
          projectileX.addLast(enemyPosX[i] - 400 + mapPosX);
          projectileY.addLast(enemyPosY[i] - 100 + mapPosY);
          projectileType.addLast(2);
          speedX.addLast(-fltCurrentHorizontal / fltHyp);
          speedY.addLast(-fltCurrentVertical / fltHyp);
          dqMapX.addLast((int) mapPosX);
          dqMapY.addLast((int) mapPosY);

          /*
           * given the triangle formed by the position of player,
           * mouse position, and the line y = playerX, calculate the angle.
           * since arcsin only returns angles of [-pi/2, pi/2], additional modification
           * is required to account for full 2pi rotation.
           */
          float fltAng = asin(fltCurrentVertical / fltHyp);
          if (mouseX < fltPlayerX + 40) {
            // if the mouse is to the left of center of wizard, modify the angle
            fltAng = (float) (3.14159265358979323846 - fltAng);
          }
          angles.addLast(fltAng);

          // push the time of new projectile spawn and update the last time a projectile
          // has spawned
          timeOfSpawn.addLast((int) (System.currentTimeMillis() - startTime));
          lastTime2 = (int) (System.currentTimeMillis() - startTime);
        }
      }
    }

  }

  /**
   * Description: draws enemy accounting for scrolling map shift and enemy health
   * 
   * No param
   * No return
   * 
   * @author: Gordon Z
   */
  void drawEnemy() {
    for (int i = 0; i < 10; i++) {
      if (enemyLives[i] > 0) {
        if (enemyPosX[i] - 400 + mapPosX > fltPlayerX) {
          image(imgEnemy2, enemyPosX[i] - 400 + mapPosX, enemyPosY[i] - 100 + mapPosY);
        } else {
          image(imgEnemy1, enemyPosX[i] - 400 + mapPosX, enemyPosY[i] - 100 + mapPosY);
        }
      }
    }
  }

  /**
   * Description: Manually spawns in the enemies positions at the start of game,
   * put in a method to save space and look cleaner
   * 
   * No param
   * No return
   * 
   * @author: Gordon Z
   */
  void manualEnemySpawn() {
    for (int i = 0; i < 10; i++) {
      enemyLives[i] = 3;
    }
    // Set specific positions for each enemy up to index 9
    enemyPosX[0] = 100;
    enemyPosY[0] = 100;

    enemyPosX[1] = 750;
    enemyPosY[1] = 200;

    enemyPosX[2] = 250;
    enemyPosY[2] = 50;

    enemyPosX[3] = 600;
    enemyPosY[3] = 300;

    enemyPosX[4] = 800;
    enemyPosY[4] = 250;

    enemyPosX[5] = 900;
    enemyPosY[5] = 50;

    enemyPosX[6] = 780;
    enemyPosY[6] = 150;

    enemyPosX[7] = 150;
    enemyPosY[7] = 350;

    enemyPosX[8] = 150;
    enemyPosY[8] = 600;

    enemyPosX[9] = 650;
    enemyPosY[9] = 500;
  }

}