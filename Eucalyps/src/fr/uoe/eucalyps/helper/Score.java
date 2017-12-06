package fr.uoe.eucalyps.helper;

import java.io.Serializable;

public class Score implements Serializable {
   private static final long serialVersionUID = -1234484684145953196L;
   public static final String SCORE="playerScore";
   public int playerId;
   public int playerScore;

   public String maxmumScore;
   public String minimuScore;

   public Score() {
      super();
   }
   public Score(final int playerScore) {
      super();
      this.playerScore = playerScore;
   }
   public Score(final String maxmumScore, final String minimuScore) {
      super();
      this.maxmumScore = maxmumScore;
      this.minimuScore = minimuScore;
   }
}