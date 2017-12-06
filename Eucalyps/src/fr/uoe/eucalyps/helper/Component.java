package fr.uoe.eucalyps.helper;

import java.io.Serializable;


public class Component implements Serializable {
   private static final long serialVersionUID = 1L;
   //Screen types:
   public static final byte TYPE_STORY = 1;
   public static final byte TYPE_QUIZ = 2;
   public static final byte TYPE_ITEM = 3;
   public static final byte TYPE_PUZZLE = 4;
   public static final byte TYPE_WORD = 5;
   /**public static final byte TYPE_DROPLET = 6;**/
   public static final byte TYPE_IMAGEQUIZ = 6;
   public static final byte TYPE_ENDING = 7;
   
   public byte type; //What kind of screen is this?
   public String imgId;
   public EntPopup ent; //The possible ent-info popup at the end of this screen

   public Component() {}
}