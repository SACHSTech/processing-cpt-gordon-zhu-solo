import processing.core.PApplet;
import processing.core.PImage;

//Main Sketch Class
public class Sketch extends PApplet {

  // Initializing variables
  float fltPlayerX = 500;
  float fltPlayerY = 650;
  boolean boolInitialize = true;;
  float mapPosX = fltPlayerX;
  float mapPosY = fltPlayerY;
  int intSpeedX = 0;
  int intSpeedY = 0;
  int intborderX = 200;
  int intborderY = 160;

  int[][] collisionMap1 = new int[3001][3001];
  int[][] collisionMap2 = new int[3001][3001];
  int[][] collisionMap3 = new int[3001][3001];

  PImage imgPlayer;
  PImage imgBackground;
  PImage imglvl1;
  PImage imglvl2;

  // movement
  boolean boolUp = false;
  boolean boolLeft = false;
  boolean boolDown = false;
  boolean boolRight = false;

  int intCurrentLevel = 0;

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

    imgBackground = loadImage("loadingScreen.jpg");
    imglvl1 = loadImage("lvl1.png");

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
        fltPlayerY = 300;
        mapPosX = 800;
        mapPosY = 300;
        boolInitialize = false;
      }
      movement();
      drawMap(imglvl1);

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
      mapPosX = Math.max(width - 750, mapPosX);
    }
    if (Math.abs(height - fltPlayerY) < intborderY) {
      mapPosY -= intSpeedY;
      mapPosY = Math.max(height - 750, mapPosY);
    }
    if (fltPlayerX < intborderX) {
      mapPosX -= intSpeedX;
      mapPosX = Math.min(750, mapPosX);
    }
    if (fltPlayerY < intborderY) {
      mapPosY -= intSpeedY;
      mapPosY = Math.min(750, mapPosY);
    }

    image(imgMap, mapPosX - 750, mapPosY - 750);
    image(imgPlayer, fltPlayerX - 25, fltPlayerY - 37);
  }

  /**
   * Description: detects movement from keys to move the character
   * 
   * No param
   * No return
   * 
   * @author: Gordon Z
   */
  public void movement() {
    intSpeedX = 0;
    intSpeedY = 0;
    if (boolUp) {
      intSpeedY = -4;
      fltPlayerY += intSpeedY;
      fltPlayerY = Math.max(intborderY - 10, fltPlayerY);
    }
    if (boolLeft) {
      intSpeedX = -4;
      fltPlayerX += intSpeedX;
      fltPlayerX = Math.max(intborderX - 10, fltPlayerX);
    }

    if (boolDown) {
      intSpeedY = 4;
      fltPlayerY += intSpeedY;
      fltPlayerY = Math.min(height - intborderY + 10, fltPlayerY);
    }

    if (boolRight) {
      intSpeedX = 4;
      fltPlayerX += intSpeedX;
      fltPlayerX = Math.min(width - intborderX + 10, fltPlayerX);
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
}