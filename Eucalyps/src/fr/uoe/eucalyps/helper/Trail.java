package fr.uoe.eucalyps.helper;

import java.io.Serializable;

public class Trail implements Serializable {
   private static final long serialVersionUID = 1L;

   public static float SHRINK_SPEED;

   public float x, y;
   public float r;
   public boolean isAlive = true;

   public Trail() {

   }

   public Trail(final float x, final float y) {
      this.x = x;
      this.y = y;
   }

   public static void calculateConstants(final float player_r) {
      SHRINK_SPEED = player_r*0.0015f; 
   }

   public void update(final double delta) {
      if (!isAlive) //If the trail is dead
         return;

      r -= SHRINK_SPEED * delta;
      if (r <= 0)
         isAlive = false;
   }
}