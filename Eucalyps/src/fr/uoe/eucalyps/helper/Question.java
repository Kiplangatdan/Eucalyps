package fr.uoe.eucalyps.helper;

import java.io.Serializable;
import java.util.ArrayList;

public class Question extends Component implements Serializable {
   private static final long serialVersionUID = 1L;
   public ArrayList<String> choices;
   public String storyText;
   public String question;
   public String hint;
   public int value = 1; // how many POINTS this question gives?
   public int correctIdx; //The correct answer index

   public Question(final ArrayList<String> choices, final String storyText,
         final String question, final String hint, final int value,
         final String img, final int correctIdx) {
      super();
      this.choices = choices;
      this.storyText = storyText;
      this.question = question;
      this.hint = hint;
      this.value = value;
      this.imgId = img;
      this.correctIdx = correctIdx;
      type = Component.TYPE_QUIZ;
   }
}