import processing.core.PApplet;

//Main Sketch Class
public class Sketch extends PApplet {
	
	
  //Initializing variables
  float fltPlayerX = 500;
  float fltPlayerY = 650;


  // movement
  boolean boolUp = false;
  boolean boolLeft = false;
  boolean boolDown = false;
  boolean boolRight = false;



  public void settings() {
	// put your size call here
    size(1275, 720);
  }

  /** 
   * Called once at the beginning of execution.  Add initial set up
   * values here i.e background, stroke, fill etc.
   */
  public void setup() {
    background(210, 255, 173);
  }

  /**
   * Called repeatedly, anything drawn to the screen goes here
   */
  public void draw() {
	  
	// sample code, delete this stuff
    stroke(128);
    line(150, 25, 270, 350);  

    stroke(255);
    line(50, 125, 70, 50);  
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