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
  float fltPlayerY = 750;
  boolean boolInitialize = true;;
  float mapPosX = fltPlayerX;
  float mapPosY = fltPlayerY;
  int intSpeedX = 0;
  int intSpeedY = 0;
  int intborderX = 300;
  int intborderY = 220;

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

  Deque<Float> speedX = new LinkedList<>();
  Deque<Float> speedY = new LinkedList<>();

  // Time
  // Timer mouseCheck = new Timer();
  int lastTime = 0;
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

  }

  /**
   * Called repeatedly, anything drawn to the screen goes here
   */
  public void draw() {

    // loading screen --> 3 levels, one locked after another, first one tutorial
    // --> each level has scrolling map
    // --> wall detection --> reach the "door" to go to next level.

    if (intCurrentLevel == 0) {
      image(imgBackground, 0, 0);
      if (keyCode == ENTER)
        intCurrentLevel = 1;
    } else if (intCurrentLevel == 1) {
      if (boolInitialize) {
        fltPlayerX = 800;
        fltPlayerY = 500;
        mapPosX = 500;
        mapPosY = 200;
        boolInitialize = false;
      }

      drawMap(imglvl1);
      movement();
      if (mousePressed) {
        ifMousePressed();
      }
      drawProjectile();

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
      // checks the average pixel colour moving upwards and allows the character to
      // move upwards if its not a light or dark gray color
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
      // checks the average pixel colour moving upwards and allows the character to
      // move upwards if its not a light or dark gray color
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
      //so that when the map scrolls, the projectiles remain at their relative positions
      int shiftX = iteratorMapX.next();
      shiftX -= mapPosX;
      int shiftY = iteratorMapY.next();
      shiftY -= mapPosY;

      int temp = (int) (System.currentTimeMillis() - startTime - iteratorSpawn.next());

      // drawing the actual projectile
      pushMatrix();
      translate(x + (temp * speedXValue / 5)-shiftX, y + (temp * speedYValue / 5) -shiftY);
      rotate(angle - PI / 2);
      translate(-12, -15);
      if (intProjectileType == 1)
        image(imgFriendlyProjectile,0 ,0);
      else if (intProjectileType == 2)
        image(imgEnemyProjectile, 0, 0);

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
      dqMapX.addLast((int)mapPosX);
      dqMapY.addLast((int)mapPosY);


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

}