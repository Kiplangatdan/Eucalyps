/*
 *                 [[ Frozen-Bubble ]]
 *
 * Copyright (c) 2000-2003 Guillaume Cottenceau.
 * Java sourcecode - Copyright (c) 2003 Glenn Sanson.
 *
 * This code is distributed under the GNU General Public License
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * version 2, as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 675 Mass Ave, Cambridge, MA 02139, USA.
 *
 *
 * Artwork:
 *    Alexis Younes <73lab at free.fr>
 *      (everything but the bubbles)
 *    Amaury Amblard-Ladurantie <amaury at linuxfr.org>
 *      (the bubbles)
 *
 * Soundtrack:
 *    Matthias Le Bidan <matthias.le_bidan at caramail.com>
 *      (the three musics and all the sound effects)
 *
 * Design & Programming:
 *    Guillaume Cottenceau <guillaume.cottenceau at free.fr>
 *      (design and manage the project, whole Perl sourcecode)
 *
 * Java version:
 *    Glenn Sanson <glenn.sanson at free.fr>
 *      (whole Java sourcecode, including JIGA classes
 *             http://glenn.sanson.free.fr/jiga/)
 *
 * Android port:
 *    Pawel Aleksander Fedorynski <pfedor@fuw.edu.pl>
 *    Copyright (c) Google Inc.
 *
 *          [[ http://glenn.sanson.free.fr/fb/ ]]
 *          [[ http://www.frozen-bubble.org/   ]]
 *          * This game was modified in order to increase knowledge of forest issues in Kenya.
 * All thanks to original developers. I just modified the code to make it more fitting
 * in order to serve our purposes. Also the graphics were changed.
 */

package net.gamecloud.sushishooter;



import java.util.Vector;

import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Looper;

public class BubbleSprite extends Sprite
{
	
  //static final int MAX_TREES = 16;
 // static BubbleSprite[][] bubbleMatrix = new BubbleSprite[MAX_TREES][MAX_TREES];
  //static ArrayList<BubbleSprite> omaTietorakenne = new ArrayList<BubbleSprite>();
  
  
  static final int AXE = 4;
  static final int BUBBLE = 3;
  static final int TREE = 2;
  static final int FIRE = 1;
  
  private static double FALL_SPEED = 1.;
  private static double MAX_BUBBLE_SPEED = 8.;
  private static double MINIMUM_DISTANCE = 841.;
  
  public int typeOfBubble = 0;
  private static int shotCounter = 0;
  private boolean isBubble = true;
  public boolean isOnFire = false;
  
  private BmpWrap tree;
  private BmpWrap waterBubble;
  private BmpWrap axe;
  private BmpWrap fire;
  
  private int color;
  private BmpWrap bubbleFace;
  private BmpWrap bubbleBlindFace;
  private BmpWrap frozenFace;
  private BmpWrap bubbleBlink;
  private BmpWrap[] bubbleFixed;
  private FrozenGame frozen;
  private BubbleManager bubbleManager;
  public double moveX, moveY;
  public double realX, realY;

  private boolean fixed;
  private boolean blink;
  private boolean released;

  private boolean checkJump;
  private boolean checkFall;

  private int fixedAnim;

  private SoundManager soundManager;

  public void saveState(Bundle map, Vector savedSprites) {
    if (getSavedId() != -1) {
      return;
    }
    super.saveState(map, savedSprites);
    map.putInt(String.format("%d-color", getSavedId()), color);
    map.putDouble(String.format("%d-moveX", getSavedId()), moveX);
    map.putDouble(String.format("%d-moveY", getSavedId()), moveY);
    map.putDouble(String.format("%d-realX", getSavedId()), realX);
    map.putDouble(String.format("%d-realY", getSavedId()), realY);
    map.putBoolean(String.format("%d-fixed", getSavedId()), fixed);
    map.putBoolean(String.format("%d-blink", getSavedId()), blink);
    map.putBoolean(String.format("%d-released", getSavedId()), released);
    map.putBoolean(String.format("%d-checkJump", getSavedId()), checkJump);
    map.putBoolean(String.format("%d-checkFall", getSavedId()), checkFall);
    map.putInt(String.format("%d-fixedAnim", getSavedId()), fixedAnim);
    map.putBoolean(String.format("%d-frozen", getSavedId()),
                   bubbleFace == frozenFace ? true : false);
    
  }

  public int getTypeId()
  {
    return Sprite.TYPE_BUBBLE;
  }

  public BubbleSprite(Rect area, int color, double moveX, double moveY,
                      double realX, double realY, boolean fixed, boolean blink,
                      boolean released, boolean checkJump, boolean checkFall,
                      int fixedAnim, BmpWrap bubbleFace,
                      BmpWrap bubbleBlindFace, BmpWrap frozenFace,
                      BmpWrap[] bubbleFixed, BmpWrap bubbleBlink,
                      BubbleManager bubbleManager, SoundManager soundManager,
                      FrozenGame frozen)
  {
    super(area);
    this.color = color;
    this.moveX = moveX;
    this.moveY = moveY;
    this.realX = realX;
    this.realY = realY;
    this.fixed = fixed;
    this.blink = blink;
    this.released = released;
    this.checkJump = checkJump;
    this.checkFall = checkFall;
    this.fixedAnim = fixedAnim;
    this.bubbleFace = bubbleFace;
    this.bubbleBlindFace = bubbleBlindFace;
    this.frozenFace = frozenFace;
    this.bubbleFixed = bubbleFixed;
    this.bubbleBlink = bubbleBlink;
    this.bubbleManager = bubbleManager;
    this.soundManager = soundManager;
    this.frozen = frozen;
    
    
    
    
    // images for bubbles
    this.axe = frozen.getBubbleImages()[0];
    this.fire = frozen.getBubbleImages()[1];
    this.waterBubble = frozen.getBubbleImages()[2];
    this.tree = frozen.getBubbleImages()[3];
  
    if (BubbleSprite.shotCounter % 4 != 0) {
    	this.typeOfBubble = BUBBLE;
    	this.isBubble = true;
    }
    else if (BubbleSprite.shotCounter % 4 == 0) {
    	this.typeOfBubble = AXE;
    	this.isBubble = false;
    }
    frozenify();
    //BubbleSprite.shotCounter++;
    
    //for (int i = 0; i < bubbleFixed.length; i++)
    	//System.out.println(bubbleFixed[i].toString());
    //System.out.println(bubbleFixed.length);
    
  }

  public BubbleSprite(Rect area, int direction, int color, BmpWrap bubbleFace,
                      BmpWrap bubbleBlindFace, BmpWrap frozenFace,
                      BmpWrap[] bubbleFixed, BmpWrap bubbleBlink,
                      BubbleManager bubbleManager, SoundManager soundManager,
                      FrozenGame frozen)
  {
    super(area);

    this.color = color;
    this.bubbleFace = bubbleFace;
    this.bubbleBlindFace = bubbleBlindFace;
    this.frozenFace = frozenFace;
    this.bubbleFixed = bubbleFixed;
    this.bubbleBlink = bubbleBlink;
    this.bubbleManager = bubbleManager;
    this.soundManager = soundManager;
    this.frozen = frozen;
    
   // BubbleSprite.bubbleMatrix = frozen.getGrid();
    
    this.moveX = MAX_BUBBLE_SPEED * -Math.cos(direction * Math.PI / 40.);
    this.moveY = MAX_BUBBLE_SPEED * -Math.sin(direction * Math.PI / 40.);
    this.realX = area.left;
    this.realY = area.top;
    
    this.axe = frozen.getBubbleImages()[0];
    this.fire = frozen.getBubbleImages()[1];
    this.waterBubble = frozen.getBubbleImages()[2];
    this.tree = frozen.getBubbleImages()[3];

    fixed = false;
    fixedAnim = -1;
    
    if (BubbleSprite.shotCounter % 4 != 0) {
    	typeOfBubble = BUBBLE;
    	isBubble = true;
    }
    else if (BubbleSprite.shotCounter % 4 == 0) {
    	typeOfBubble = AXE;
    	isBubble = false;
    }
    frozenify();
    BubbleSprite.shotCounter++;
    
    
    /*for (int i = 0; i < bubbleFixed.length; i++)
    	System.out.println(bubbleFixed[i].toString());
    System.out.println(bubbleFixed.length);
    */
   // this.isBubble = LaunchBubbleSprite.isBubble;
  }

  public BubbleSprite(Rect area, int color, BmpWrap bubbleFace,
                      BmpWrap bubbleBlindFace, BmpWrap frozenFace,
                      BmpWrap bubbleBlink, BubbleManager bubbleManager,
                      SoundManager soundManager, FrozenGame frozen)
  {
    super(area);

    this.color = color;
    this.bubbleFace = bubbleFace;
    this.bubbleBlindFace = bubbleBlindFace;
    this.frozenFace = frozenFace;
    this.bubbleBlink = bubbleBlink;
    this.bubbleManager = bubbleManager;
    this.soundManager = soundManager;
    this.frozen = frozen;
    
   // BubbleSprite.bubbleMatrix = frozen.getGrid();
    
    this.realX = area.left;
    this.realY = area.top;
    
    this.axe = frozen.getBubbleImages()[0];
    this.fire = frozen.getBubbleImages()[1];
    this.waterBubble = frozen.getBubbleImages()[2];
    this.tree = frozen.getBubbleImages()[4];

    fixed = true;
    fixedAnim = -1;
    bubbleManager.addBubble(bubbleFace);
    
    typeOfBubble = TREE;
    frozenify();
    /*
    for (int i = 0; i < bubbleFixed.length; i++)
    	System.out.println(bubbleFixed[i].toString());
    System.out.println(bubbleFixed.length);
    */
 //   this.isBubble = LaunchBubbleSprite.isBubble;
  }

  Point currentPosition()
  {
    int posY = (int)Math.floor((realY-28.-frozen.getMoveDown())/28.);
    int posX = (int)Math.floor((realX-174.)/32. + 0.5*(posY%2));

    if (posX>7) {
      posX = 7;
    }

    if (posX<0) {
      posX = 0;
    }

    if (posY<0) {
      posY = 0;
    }

    return new Point(posX, posY);
  }

  public void removeFromManager()
  {
    bubbleManager.removeBubble(bubbleFace);
  }

  public boolean fixed()
  {
    return fixed;
  }

  public boolean checked()
  {
    return checkFall;
  }

  public boolean released()
  {
    return released;
  }

  public void moveDown()
  {
    if (fixed) {
      realY += 28.;
    }

    super.absoluteMove(new Point((int)realX, (int)realY));
  }
  
  /*private void changeBubbleImage(BubbleSprite bubble) {
	  	
		Bitmap fireIcon = Bitmap.createBitmap(d.getIntrinsicWidth(), d.getIntrinsicHeight(), Config.ARGB_8888);
		Canvas canvas = new Canvas();
		canvas.setBitmap(fireIcon);
		bubble.paint(canvas, 30., (int)bubble.realX, (int)bubble.realY);
	
	}
  */
  // this is the method, where destroying bubbles happens. This has to
  // be changed...
  public void move()
  {	  
	  //checkWin();
	  //typeOfBubble = 1;
	  /*
	   * 
	   * laita vaihtamaan kuvat oikein (tulta ei oikein jostain syystä vaihdeta???)
	   * 
	   */
	 // final Point bubblePoint = FrozenGame.bubblePlay[(int) (Math.random() * 2)][(int) (Math.random() * 2)].getSpritePosition();
	  
	  try {
	  	int randomNumber = (int) (Math.random() * 100);
	  	if (randomNumber % 30 == 0) {
	  		new Thread(new Runnable() {
	  			@Override
	  			public void run() {
	  				Looper.prepare();
	  				int randomBubbleIndex = (int) (Math.random() * FrozenGame.allBubbles.size());
	  				BubbleSprite b = FrozenGame.allBubbles.get(randomBubbleIndex);
	  				if (b == null)
	  					return;
	  				else if (!b.isOnFire) {
	  					b.isOnFire = true;
	  					b.typeOfBubble = FIRE;
	  					b.frozenify();
	  					//Point p = b.getSpritePosition();
	  					//System.out.println(p.x);
	  					//System.out.println(p.y);
	  				}
	  				/*int countFireTrees = 0;
	  				
	  				for (int i = 0; i < FrozenGame.bubblePlay.length; i++) {
	  					for (int j = 0; j < FrozenGame.bubblePlay[0].length; j++) {
	  						BubbleSprite current = FrozenGame.bubblePlay[i][j];
	  						if (current == null)
	  							continue;
	  						else if (current.isOnFire)
	  							countFireTrees++;
	  					}
	  				}
	  				
	  				if (countFireTrees == 0) {
	  					int randomBubbleIndexX = (int) (Math.random() * FrozenGame.bubblePlay.length);
	  					int randomBubbleIndexY = (int) (Math.random() * FrozenGame.bubblePlay[0].length);
	  					
	  					BubbleSprite bubbleToHandle = FrozenGame.bubblePlay[randomBubbleIndexX][randomBubbleIndexY];
	  					if (bubbleToHandle == null)
	  						return;
	  					bubbleToHandle.isOnFire = true;
	  					bubbleToHandle.typeOfBubble = FIRE;
	  					bubbleToHandle.frozenify();
	  					//Point p = bubbleToHandle.getSpritePosition();
	  					//System.out.println(p.x);
	  					//System.out.println(p.y);
	  					return;
	  				}
	  				
	  				else if (countFireTrees > 0) {
	  					for (int i = 0; i < FrozenGame.bubblePlay.length; i++) {
	  						for (int j = 0; j < FrozenGame.bubblePlay[0].length; j++) {
	  							BubbleSprite current = FrozenGame.bubblePlay[i][j];
	  							if (current == null)
	  								continue;
	  							if (current != null)
	  								System.out.println(current.getSpritePosition().x + " " + current.getSpritePosition().y);
	  							if (current.isOnFire) {
	  								Point p = current.getSpritePosition();
	  						//		System.out.println(p.x + " vituillaa x");
	  						//		System.out.println(p.y + " vituillaa y");
	  								//if (p.x > FrozenGame.bubblePlay.length || p.y > FrozenGame.bubblePlay[0].length)
	  									//continue;
	  								Vector v = current.getNeighbors(bubblePoint);
	  								for (int k = 0; k < v.size(); k++) {
	  									BubbleSprite b = (BubbleSprite) v.get(k);
	  									if (b == null)
	  										continue;
	  									else {
	  										if (!b.isOnFire) {
	  											b.isOnFire = true;
	  											b.typeOfBubble = FIRE;
	  											b.frozenify();
	  											return;
	  										}
	  									}
	  								}
	  							}
	  						}
	  					}
	  				}
	  				*/
	  				
	  				/*
	  				for (int i = 0; i < FrozenGame.allBubbles.size(); i++) {
	  					BubbleSprite current = FrozenGame.allBubbles.get(i);
	  					if (current == null)
	  						continue;
	  					else if (current.isOnFire)
	  						countFireTrees++;
	  				}
	  //				System.out.println(countFireTrees + " fired trees");
	  				if (countFireTrees == 0) {
	  					int randomBubbleIndex = (int) (Math.random() * FrozenGame.allBubbles.size());
	  					BubbleSprite bubbleToHandle = FrozenGame.allBubbles.get(randomBubbleIndex);
	  					bubbleToHandle.isOnFire = true;
	  					bubbleToHandle.typeOfBubble = FIRE;
	  					bubbleToHandle.frozenify();
	  					return;
	  				}
	  				
	  				else if (countFireTrees > 0) {
	  				//	System.out.println("menty oikeaan");
	  					for (int i = 0; i < FrozenGame.allBubbles.size(); i++) {
	  						BubbleSprite current = FrozenGame.allBubbles.get(i);
	  						if (current == null)
	  							continue;
	  						if (current.isOnFire) {
	  						//	System.out.println("loydetty tulessa oleva");
	  							Point p = current.getSpritePosition();
	  							System.out.println("x: " + p.x);
	  							System.out.println("y:" + p.y);
	  							Vector v = current.getNeighbors(p);
	  							//System.out.println(v.size() + " naapureiden koko (ei saisi olla 0)");
	  							for (int j = 0; j < v.size(); j++) {
	  								BubbleSprite b = (BubbleSprite) v.get(j);
	  								if (b == null)
	  									continue;
	  								if (!b.isOnFire) {
	  									b.isOnFire = true;
	  									b.typeOfBubble = FIRE;
	  									b.frozenify();
	  								}
	  							}
	  						}
	  					}
	  				}
	  				*/
	  				/*
	  				for (int i = 0; i < FrozenGame.allBubbles.size(); i++) {
	  					BubbleSprite current = FrozenGame.allBubbles.get(i);
	  					if (current == null)
	  						return;
	  					else if (current.isOnFire) {
	  						countFireTrees++;
	  						if (countFireTrees >= 1)
	  							break;
	  					}
	  					else {
	  						int randomBubbleIndex = (int) (Math.random() * FrozenGame.allBubbles.size());
	  						BubbleSprite bubbleToHandle = FrozenGame.allBubbles.get(randomBubbleIndex);
	  						bubbleToHandle.isOnFire = true;
	  						bubbleToHandle.typeOfBubble = FIRE;
	  						bubbleToHandle.frozenify();
	  						return;
	  						}
	  					}
	  				for (int i = 0; i < FrozenGame.allBubbles.size(); i++) {
	  					BubbleSprite current = FrozenGame.allBubbles.get(i);
	  					if (current.isOnFire) {
	  						Point p = current.getSpritePosition();
	  						Vector v = current.getNeighbors(p);
	  						for (int j = 0; j < v.size(); j++) {
	  							BubbleSprite b = (BubbleSprite) v.get(j);
	  							if (!b.isOnFire)
	  								b.isOnFire = true;
	  								b.typeOfBubble = FIRE;
	  								b.frozenify();
	  						}
	  						break;
	  					}
	  				}
	  				*/
	  				/*
	  				int randomBubbleSpriteIndex = (int) (Math.random() * FrozenGame.allBubbles.size());
	  				BubbleSprite bubbleTohandle = FrozenGame.allBubbles.get(randomBubbleSpriteIndex);
	  				if (!bubbleTohandle.isOnFire && bubbleTohandle != null) {
	  					bubbleTohandle.isOnFire = true;
	  					bubbleTohandle.typeOfBubble = FIRE;
	  					bubbleTohandle.frozenify();
	  					//System.out.println(bubbleTohandle.typeOfBubble);
	  					//System.out.println("Fire has been setted and the image has been changed");
	  				}
	  			*/
	  				
	  			}
	  		}).start();
	  		
	  		
	  	}
//	  	System.out.println("Is bubble (should change over time): " + BubbleSprite.isBubble);
	  	//BubbleSprite[][] grid = frozen.getGrid();
    realX += moveX;

    if (realX>=414.) {
      moveX = -moveX;
      realX += (414. - realX);
      soundManager.playSound(FrozenBubble.SOUND_REBOUND);
    } else if (realX<=190.) {
      moveX = -moveX;
      realX += (190. - realX);
      soundManager.playSound(FrozenBubble.SOUND_REBOUND);
    }

    realY += moveY;
    
    
    
    
    
    // collision has happened
    Point currentPosition = currentPosition();
    Vector neighbors = getNeighbors(currentPosition);

    if (checkCollision(neighbors) || realY < 44.+frozen.getMoveDown()) {
      realX = 190.+currentPosition.x*32-(currentPosition.y%2)*16;
      realY = 44.+currentPosition.y*28+frozen.getMoveDown();

      fixed = true;
      // shooted bubble is a bubble and check if its neighbors are on fire
      if (isBubble) {
      loop1:
      for (int i = 0; i < neighbors.size(); i++) {
    	  BubbleSprite current = (BubbleSprite) neighbors.get(i);
    	  if (current != null) {
    	  if (current.isOnFire) {
    		  moveX = 0;
    		  moveY = 0;
    		  fixedAnim = 0;
    		  //this.isOnFire = false;
    		  //BubbleSprite.isBubble = false;
    		  //current.typeOfBubble = TREE;
    		  //current.frozenify();
    		  //this.removeFromManager();
    		  
    		  FrozenGame.allBubbles.remove(this);
    		  FrozenGame.allBubbles.remove(current);
    		  frozen.removeSprite(this);
    		  frozen.deleteJumpingBubble(this);
    		  frozen.removeSprite(current);
    		  frozen.deleteJumpingBubble(current);
    		  Point pos = current.currentPosition();
    		  FrozenGame.bubblePlay[pos.x][pos.y] = null;
    		  
    		  Vector checkJump = new Vector();
    	      this.checkJump(checkJump, neighbors);
    	      loop2:
    	      for (int j = 0; j < checkJump.size(); j++) {
    	    	  BubbleSprite vittu = (BubbleSprite) checkJump.get(j);
    	    	  FrozenGame.allBubbles.remove(vittu);
    	    	  //FrozenGame.allBubbles.remove(this);
    	    	  frozen.removeSprite(this);
    	    	  frozen.removeSprite(vittu);
    	    	  Point posi = vittu.currentPosition();
    	    	  FrozenGame.bubblePlay[pos.x][pos.y] = null;
    	    	  
    	      }
    	      FrozenGame.allBubbles.remove(this);
    	      break loop1;
    		  
    		  //frozen.removeSprite(current);
    		  //frozen.addJumpingBubble(current);
    		  //current.removeFromManager();
    		  //current = null;
    		  //break;
    	  }
    	  else if (!current.isOnFire) {
    		  bubbleManager.addBubble(this.bubbleFace);
    		  frozen.addSprite(this);
    		  FrozenGame.allBubbles.addElement(this);
    		  moveX = 0;
    		  moveY = 0;
    		  fixedAnim = 0;
    		  isOnFire = false;
    		  //isBubble = false;
    		  FrozenGame.bubblePlay[currentPosition.x][currentPosition.y] = this;
    		  this.typeOfBubble = TREE;
    		  this.frozenify();
    		  break;
    		  
    	  }
    	  }
      }
      }
      // shooted bubble is an axe and check if its neighbors are healthy trees
      else if (!isBubble) {
    	  for (int i = 0; i < neighbors.size(); i++) {
    		  BubbleSprite current = (BubbleSprite) neighbors.get(i);
    		  if (current != null)
    			  if (!current.isOnFire) {
    				  //current.frozenify();
    				  //this.frozenify();
    				  FrozenGame.allBubbles.remove(current);
    				  FrozenGame.allBubbles.remove(this);
    				  current.removeFromManager();
    				  this.removeFromManager();
    				  frozen.removeSprite(this);
    				  frozen.removeSprite(current);
    				  frozen.deleteJumpingBubble(this);
    				  frozen.deleteJumpingBubble(current);
    				  Point pos = current.currentPosition();
    				  FrozenGame.bubblePlay[pos.x][pos.y] = null;
    				  
    				  //current.frozenify();
    				  //frozen.addJumpingBubble(current);
    				  //current.removeFromManager();
    				  //current = null;
    				  
    				  
    				  break;
    			  }
    			  else if (current.isOnFire) {
    				  this.frozenify();
    				  this.removeFromManager();
    				  frozen.removeSprite(this);
    				  frozen.deleteJumpingBubble(this);
    				  break;
    			  }
    	  }
      }
      
      /*
      Vector checkJump = new Vector();
      this.checkJump(checkJump, neighbors);

      BubbleSprite[][] grid = frozen.getGrid();

      if (checkJump.size() >= 3) {
        released = true;

        for (int i=0 ; i<checkJump.size() ; i++) {
          BubbleSprite current = (BubbleSprite)checkJump.elementAt(i);
          Point currentPoint = current.currentPosition();
          if (current.isOnFire) {
        	  frozen.addJumpingBubble(current);
        	  current.removeFromManager();
        	  current = null;
        	  FrozenGame.allBubbles.remove(current);
        	  
        	    bubbleManager.addBubble(bubbleFace);
        	    grid[currentPosition.x][currentPosition.y] = this;
        	    moveX = 0.;
        	    moveY = 0.;
        	    fixedAnim = 0;
        	    break;
        	  
          }
          frozen.addJumpingBubble(current); // pudottaa alas kuplat
          if (i>0) {
            current.removeFromManager(); // poistaa tippuvat -> peli päättyy
          }
          grid[currentPoint.x][currentPoint.y] = null; 
        }

        for (int i=0 ; i<8 ; i++) {
          if (grid[i][0] != null) {
            grid[i][0].checkFall();
          }
        }

        for (int i= grid.length ; i<8 ; i++) {
          for (int j=0 ; j< grid[0].length ; j++) {
            if (grid[i][j] != null) {
              if (!grid[i][j].checked()) {
                frozen.addFallingBubble(grid[i][j]);
            	  // distinguishes the fire
            	  if (grid[i][j].isOnFire) {
                grid[i][j].removeFromManager();
                grid[i][j] = null;
            	  }
            	  
              }
            }
          }
        }
        for (int i = 0; i < grid.length; i++) {
        	for (int j = 0; j < grid[0].length; j++) {
        		if (grid[i][j] != null) {
        			if (grid[i][j].isOnFire) {
        			grid[i][j].removeFromManager();
        			grid[i][j] = null;
        			}
        		}
        	}
        }
        soundManager.playSound(FrozenBubble.SOUND_DESTROY);
      }*/ /*else {
    	// if the the bubbles were not destroyed, the current will stick
    	  // and change it's color to the tree (must be implemented...)
    	  bubbleManager.addBubble(bubbleFace);
    	  grid[currentPosition.x][currentPosition.y] = this;
    	  moveX = 0.;
    	  moveY = 0.;
    	  fixedAnim = 0;
    	  
    	 */ 
     /*
    	bubbleManager.addBubble(bubbleFace);
        grid[currentPosition.x][currentPosition.y] = this;
        moveX = 0.;
        moveY = 0.;
        fixedAnim = 0;
        soundManager.playSound(FrozenBubble.SOUND_STICK);
     
    
      }
    
    
*/	
    }
    super.absoluteMove(new Point((int)realX, (int)realY));
    checkWin();
	  }
	  catch (ArrayIndexOutOfBoundsException e) {
		  System.err.println(e.getMessage());
	  }
	}
	
  
 
  
  
  

  Vector getNeighbors(Point p)
  {	
    BubbleSprite[][] grid = FrozenGame.bubblePlay;
    int maxX = grid.length;
    int maxY = grid[0].length;
    
    Vector list = new Vector();
    if (p.y - 1 > maxY || p.x - 1 > maxX)
    	return list;
    if ((p.y % 2) == 0) {
      if (p.x > 0) {
        list.addElement(grid[p.x-1][p.y]);
      }

      if (p.x < 7) {
        list.addElement(grid[p.x+1][p.y]);

        if (p.y > 0) {
          list.addElement(grid[p.x][p.y-1]);
          list.addElement(grid[p.x+1][p.y-1]);
        }

        if (p.y < 12) {
          list.addElement(grid[p.x][p.y+1]);
          list.addElement(grid[p.x+1][p.y+1]);
        }
      } else {
        if (p.y > 0) {
          list.addElement(grid[p.x][p.y-1]);
        }

        if (p.y < 12) {
          list.addElement(grid[p.x][p.y+1]);
        }
      }
    } else {
      if (p.x < 7) {
        list.addElement(grid[p.x+1][p.y]);
      }

      if (p.x > 0) {
        list.addElement(grid[p.x-1][p.y]);

        if (p.y > 0) {
          list.addElement(grid[p.x][p.y-1]);
          list.addElement(grid[p.x-1][p.y-1]);
        }

        if (p.y < 12) {
          list.addElement(grid[p.x][p.y+1]);
          list.addElement(grid[p.x-1][p.y+1]);
        }
      } else {
        if (p.y > 0) {
          list.addElement(grid[p.x][p.y-1]);
        }

        if (p.y < 12) {
          list.addElement(grid[p.x][p.y+1]);
        }
      }
    }/*
    for (int i = 0; i < list.size(); i++) {
    	BubbleSprite current = (BubbleSprite) list.get(i);
    	if (current != null) {
    	if (!current.isOnFire)
    		list.remove(current);
    }
    	
  //  System.out.println(list.size());
    }*/
    
    return list;
  }

  void checkJump(Vector jump)
  {
    if (checkJump) {
      return;
    }
    checkJump = true;

    
      checkJump(jump, this.getNeighbors(this.currentPosition()));
    
  }

  void checkJump(Vector jump, Vector neighbors)
  {
    jump.addElement(this);

    for (int i=0 ; i<neighbors.size() ; i++) {
      BubbleSprite current = (BubbleSprite)neighbors.elementAt(i);

      if (current != null) {
    	  // take only closest neighbors -> comment off the recursion
    	  if (current.isOnFire)
    		  current.checkJump(jump);
      }
    }
  }

  public void checkFall()
  {
    if (checkFall) {
      return;
    }
    checkFall = true;

    Vector v = this.getNeighbors(this.currentPosition());

    for (int i=0 ; i<v.size() ; i++) {
      BubbleSprite current = (BubbleSprite)v.elementAt(i);

      if (current != null) {
        current.checkFall();
      }
    }
  }

  boolean checkCollision(Vector neighbors)
  {
    for (int i=0 ; i<neighbors.size() ; i++) {
      BubbleSprite current = (BubbleSprite)neighbors.elementAt(i);

      if (current != null) {
        if (checkCollision(current)) {
          return true;
        }
      }
    }

    return false;
  }

  boolean checkCollision(BubbleSprite sprite)
  {
    double value =
        (sprite.getSpriteArea().left - this.realX) *
        (sprite.getSpriteArea().left - this.realX) +
        (sprite.getSpriteArea().top - this.realY) *
        (sprite.getSpriteArea().top - this.realY);

    return (value < MINIMUM_DISTANCE);
  }

  public void jump()
  {
    if (fixed) {
      moveX = -6. + frozen.getRandom().nextDouble() * 12.;
      moveY = -5. - frozen.getRandom().nextDouble() * 10. ;

      fixed = false;
    }

    moveY += FALL_SPEED;
    realY += moveY;
    realX += moveX;

    super.absoluteMove(new Point((int)realX, (int)realY));

    if (realY >= 680.) {
      frozen.deleteJumpingBubble(this);
    }
  }

  public void fall()
  {
    if (fixed) {
      moveY = frozen.getRandom().nextDouble()* 5.;
    }

    fixed = false;

    moveY += FALL_SPEED;
    realY += moveY;

    super.absoluteMove(new Point((int)realX, (int)realY));

    if (realY >= 680.) {
      frozen.deleteFallingBubble(this);
    }
  }

  public void blink()
  {
    blink = true;
  }

  public void frozenify()
  {	
	
    changeSpriteArea(new Rect(getSpritePosition().x-1, getSpritePosition().y-1,
                              34, 42));
    switch (typeOfBubble) {
    	case (FIRE):
    		bubbleFace = fire;
    		break;
    	case (BUBBLE):
    		bubbleFace = waterBubble;
    		break;
    	case (TREE):
    		bubbleFace = tree;
    		break;
    	case (AXE):
    		bubbleFace = axe;
    		break;
    	default:
    		break;
    		// WTF??
    }
    
    /*if (isOnFire)
    	bubbleFace = fire;
    else if (!isOnFire)
    	bubbleFace = tree;
    if (isBubble)
    	bubbleFace = waterBubble;
    else if (!isBubble)
    	bubbleFace = axe;*/
    //bubbleFace = frozenFace;
  }
  
  
  
  public final void paint(Canvas c, double scale, int dx, int dy)
  {
    checkJump = false;
    checkFall = false;

    Point p = getSpritePosition();
    
    	drawImage(bubbleFace, p.x, p.y, c, scale, dx, dy);
    /*
    if (isOnFire) {
    	drawImage(fire, p.x, p.y, c, scale, dx, dy);
    }
    else if (!isOnFire) {
    	drawImage(tree, p.x, p.y, c, scale, dx, dy);
    }
    if (isBubble) {
    	drawImage(waterBubble, p.x, p.y, c, scale, dx, dy);
    }
    else if (!isBubble) {
    	drawImage(axe, p.x, p.y, c, scale, dx, dy);
    }
    */
    /*
    
    if (blink && bubbleFace != frozenFace) {
      blink = false;
      drawImage(bubbleBlink, p.x, p.y, c, scale, dx, dy);
    } else {
    	if (bubbleFace == null)
    		return;
      if (FrozenBubble.getMode() == FrozenBubble.GAME_NORMAL ||
          bubbleFace == frozenFace) {
        drawImage(bubbleFace, p.x, p.y, c, scale, dx, dy);
      } else {
        drawImage(bubbleFace, p.x, p.y, c, scale, dx, dy);
      }
    }
*/
    if (fixedAnim != -1) {
      drawImage(bubbleFixed[fixedAnim], p.x, p.y, c, scale, dx, dy);
      fixedAnim++;
      if (fixedAnim == 6) {
        fixedAnim = -1;
      }
    }
  }
  
   void checkWin() {
	  //System.out.println("voittoa tarkastetaan");
	  //int nullCounter = 0;
	  int fireCounter = 0;
	  int treeCounter = 0;
	 // System.out.println("bubblepituus:" + FrozenGame.bubblePlay.length);
	 // System.out.println("toinenpituus: " + FrozenGame.bubblePlay[0].length);
	/*  for (int i = 0; i < FrozenGame.bubblePlay.length; i++)
		  for (int j = 0; j < FrozenGame.bubblePlay[0].length; j++) {
			  BubbleSprite current = FrozenGame.bubblePlay[i][j];
			  if (current == null)
				  return; // should not happen
			  System.out.println(current.typeOfBubble + " typeofbubble");
			  if (current.typeOfBubble == BubbleSprite.FIRE)
				  fireCounter++;
			  else if (current.typeOfBubble != BubbleSprite.TREE)
				  treeCounter++;
				  
		  }*/
	  for (int i = 0; i < FrozenGame.allBubbles.size(); i++) {
		  BubbleSprite current = FrozenGame.allBubbles.get(i);
		  if (current == null)
			  return;
		  else if (current.typeOfBubble == BubbleSprite.FIRE)
			  fireCounter++;
		  else if (current.typeOfBubble == BubbleSprite.TREE)
			  treeCounter++;
	  }
	//  System.out.println("firecounter: " + fireCounter);
	//  System.out.println("treecounter: " + treeCounter);
	  if (treeCounter > 30 && fireCounter == 0) {
		  frozen.penguin.updateState(PenguinSprite.STATE_GAME_WON);
		  frozen.addSprite(new ImageSprite(new Rect(152, 190, 337, 116),
                             frozen.gameWon));
		  frozen.levelCompleted = true;
		  frozen.endOfGame = true;
	  }
	  else if (fireCounter > treeCounter) { // the whole shit's on fire
		  frozen.penguin.updateState(PenguinSprite.STATE_GAME_LOST);
		  frozen.addSprite(new ImageSprite(new Rect(152, 190, 337, 116),
				  frozen.gameLost));
		  frozen.levelCompleted = true;
		  frozen.endOfGame = true;
	  }
	  else
		  return;
		  
	  
	 
  }
}
