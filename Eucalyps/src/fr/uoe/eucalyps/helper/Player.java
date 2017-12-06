package fr.uoe.eucalyps.helper;

import java.io.Serializable;

public class Player extends Object implements Serializable, Comparable<Player> {
   private static final long serialVersionUID = 1L;
   public static int MAX_SCORE; //This value is updated ONLY in the XML-reader and should not be touched from anywhere else
   public String name = "Ent";
   public int points = 0;
   public Player() {
      super();
   }
   public Player(final String name, final int points) {
      super();
      this.name = name;
      this.points = points;
   }
   public int compareTo(final Player p) {
      if (points < p.points)
         return 1;
      else if (points > p.points)
         return -1;
      return 0;
   }
}
