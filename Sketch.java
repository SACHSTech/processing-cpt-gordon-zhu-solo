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
  int intborderX = 300;
  int intborderY = 220;

  PImage imgPlayer;
  PImage imgBackground;
  PImage imgPermBackground;
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
    imgPermBackground = loadImage("permanentBackground.jpg");

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
        mapPosX = 500;
        mapPosY = 200;
        boolInitialize = false;
      }

      drawMap(imglvl1);
      movement();

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
    image(imgPlayer, fltPlayerX - 30, fltPlayerY - 30);
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
    if (boolUp) {
      // if you want to go up, grab the average pixel color of a 5x5 grid upwards of
      // the character
      for (int i = 0; i < 5; i++) {
        for (int j = 0; j < 5; j++) {
          pixelColor = get((int) fltPlayerX + i - 3, (int) fltPlayerY - 37 + j);
          // According to documentation, bit shift is faster than "get" method
          sumRGB[0] += (pixelColor >> 16) & 0xFF; // Red component
          sumRGB[1] += (pixelColor >> 8) & 0xFF; // Green component
          sumRGB[2] += pixelColor & 0xFF; // Blue component
        }
      }
      int avgR = sumRGB[0] /25;
      int avgG = sumRGB[1] / 25;
      int avgB = sumRGB[2] / 25;

      println(pixelColor);
      println(red(pixelColor));
      println(green(pixelColor));

      println(blue(pixelColor));
      println(pixelColor & 0xFF);

      // checks the average pixel colour moving upwards and allows the character to move upwards if its not a gray color
      if (!(avgR <= 250 && avgR >= 230 && avgB <= 210 && avgB >= 185
          && avgG <= 250 && avgG >= 230)) {
        intSpeedY = -4;
        fltPlayerY += intSpeedY;
        fltPlayerY = Math.max(intborderY - 20, fltPlayerY);
      }
    }
    if (boolLeft) {
      intSpeedX = -4;
      fltPlayerX += intSpeedX;
      fltPlayerX = Math.max(intborderX - 20, fltPlayerX);
    }

    if (boolDown) {
      intSpeedY = 4;
      fltPlayerY += intSpeedY;
      fltPlayerY = Math.min(height - intborderY + 20, fltPlayerY);
    }

    if (boolRight) {
      intSpeedX = 4;
      fltPlayerX += intSpeedX;
      fltPlayerX = Math.min(width - intborderX + 20, fltPlayerX);
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