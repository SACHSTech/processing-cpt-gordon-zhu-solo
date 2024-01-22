import processing.core.PApplet;
import processing.core.PImage;


//Main Sketch Class
public class Sketch extends PApplet {
	
	
  //Initializing variables
  float fltPlayerX = 500;
  float fltPlayerY = 650;

  int[][] collisionMap1 = new int[6001][6001]; 
  int[][] collisionMap2 = new int[6001][6001]; 
  int[][] collisionMap3 = new int[6001][6001]; 

  PImage imgPlayer; 
  PImage  imgBackground;


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
   * Called once at the beginning of execution.  Add initial set up
   * values here i.e background, stroke, fill etc.
   */
  public void setup() {

    //imgPlayer = loadImage("loading");
    if(intCurrentLevel==0){
    imgBackground = loadImage("loadingScreen.jpg");
    }
    else if(intCurrentLevel==1){
      
    }
  }

  /**
   * Called repeatedly, anything drawn to the screen goes here
   */
  public void draw() {
	  
    //loading screen --> 3-5 levels, one locked after another, first one tutorial --> each level has scrolling map 
    //--> wall detection --> reach a "door" to go to next level. 

    image(imgBackground, 0, 0);
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

    if (boolUp) {
      fltPlayerY -= 3;
      fltPlayerY = Math.max(0, fltPlayerY);
    }
    if (boolLeft) {
      fltPlayerX -= 3;
      fltPlayerX = Math.max(0, fltPlayerX);
    }

    if (boolDown) {
      fltPlayerY += 3;
      fltPlayerY = Math.min(height, fltPlayerY);
    }

    if (boolRight) {
      fltPlayerX += 3;
      fltPlayerX = Math.min(width, fltPlayerX);
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
    
}}