package fr.uoe.eucalyps.helper;

import java.util.ArrayList;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import eucalyps.ItemLocatorGame;
import eucalyps.Wordsearch;



import android.content.Context;
import android.content.res.Configuration;
import android.os.Build;



public class XMLHandlerStory extends DefaultHandler {
   public ArrayList<Component> screens = new ArrayList<Component>(); //This arraylist holds the parsed story
   private final StringBuilder currentValue = new StringBuilder(); //This stringbuilder collects the texts between the tags
   private boolean isTablet = true;
   //The following variables are temporary values:
   private byte type;
   public String long_text;
   public String bgId;
   private String image;
   private String hint;
   private ArrayList<String> choices = new ArrayList<String>();
   private String story_text;
   private String question;
   private int correct_answer; //The correct answer index
   private EntPopup ent;

   public XMLHandlerStory(final Context context) {
      super();
      /***if (Build.VERSION.SDK_INT >= 9 && (context.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) ==
            Configuration.SCREENLAYOUT_SIZE_XLARGE) { //If we're running on a tablet:
         isTablet = true;
      }**/
      Player.MAX_SCORE = 0; //Reset the max_score counter, this will be updated during the reading process
   }

   // Called when tag starts
   @Override
   public void startElement(final String uri, final String localName, final String qName,
         final Attributes attributes) throws SAXException {
      //Check for HTML tags:
      if (localName.equalsIgnoreCase("b") || localName.equalsIgnoreCase("i") || localName.equalsIgnoreCase("u")) {
         currentValue.append("<" + localName + ">"); //We will leave this into the text
         return;
      }
      else if (localName.equalsIgnoreCase("screen")) { //new story screen
         //These temp values HAVE TO be reset manually, because otherwise they might get passed on from previous tags
         image = null;
         bgId = null;
         hint = null;
         choices = new ArrayList<String>();
         story_text = null;
         long_text = null;
         question = null;
         correct_answer = 0;

         //What kind of screen are we dealing with:
         final String type_s = attributes.getValue("type");
         if (type_s != null) {
            if (type_s.equalsIgnoreCase("quize")) {
               type = Component.TYPE_QUIZ;
            }
            else if (type_s.equalsIgnoreCase("imagequiz")) {
               type = Component.TYPE_IMAGEQUIZ;
            }
            else if (type_s.equalsIgnoreCase("item_locator")) {
               type = Component.TYPE_ITEM;
            }
            else if (type_s.equalsIgnoreCase("puzzle")) {
               type = Component.TYPE_PUZZLE;
            }
            else if (type_s.equalsIgnoreCase("word_find")) {
               type = Component.TYPE_WORD;
            }
            else if (type_s.equalsIgnoreCase("story")) {
               type = Component.TYPE_STORY;
            }/**
            else if (type_s.equalsIgnoreCase("droplet")) {
               type = Component.TYPE_DROPLET;
            }**/
            else if (type_s.equalsIgnoreCase("ending")) {
               type = Component.TYPE_ENDING;
            }
         }
      }
      //If there's a ent-popup in this screen:
      else if (localName.equalsIgnoreCase("ent_diag"))
         ent = new EntPopup();
   }

   /** Called when tag closing ( ex:- <name>AndroidPeople</name>
    * -- </name> )*/
   @Override
   public void endElement(final String uri, final String localName, final String qName)
         throws SAXException {
      //Check for HTML tags:
      if (localName.equalsIgnoreCase("b") || localName.equalsIgnoreCase("i") || localName.equalsIgnoreCase("u")) {
         currentValue.append("</" + localName + ">");
         return;
      }
      else if (localName.contains("br")) { //Line break
         currentValue.append("<" + localName + "/>");
         return;
      }
      /** set value */
      if (localName.equalsIgnoreCase("image")) { //Name of the image
         image = currentValue.toString().trim();
      }
      else if (localName.equalsIgnoreCase("story_text")) {
         story_text = currentValue.toString().trim();
      }
      else if (localName.equalsIgnoreCase("long_text")) {
         long_text = currentValue.toString().trim();
      }
      else if (localName.equalsIgnoreCase("background")) {
         bgId = currentValue.toString().trim();
      }
      else if (localName.equalsIgnoreCase("question")) {
         question = currentValue.toString().trim();
      }
      else if (localName.toLowerCase().startsWith("choice")) {
         choices.add(currentValue.toString().trim());
      }
      else if (localName.equalsIgnoreCase("correct_answer")) {
         correct_answer = Integer.parseInt(currentValue.toString().trim());
      }
      else if (localName.equalsIgnoreCase("answer_hint")) {
         hint = currentValue.toString().trim();
      }
      else if (localName.equalsIgnoreCase("ent_text")) {
         ent.text = currentValue.toString().trim();
      }
      else if (localName.equalsIgnoreCase("ent_image")) {
         ent.imgId = currentValue.toString().trim();
      }
      else if (localName.equalsIgnoreCase("screen")) { //end tag
         Component comp = null;
         int value = 0;
         //Figure out the screen type:
         if (type == Component.TYPE_ITEM) {
            comp = new Item(story_text, image, hint, correct_answer);
            value = ItemLocatorGame.MAX_SCORE;
         }
         else if (type == Component.TYPE_QUIZ) {
            comp = new Question(choices, story_text, question, hint, 1, image, correct_answer);
            value = choices.size();
         }
         else if (type == Component.TYPE_IMAGEQUIZ) {
            comp = new Question(choices, story_text, question, hint, 1, image, correct_answer);
            comp.type = Component.TYPE_IMAGEQUIZ;
            value = choices.size();
         }
         else if (type == Component.TYPE_PUZZLE) {
            comp = new Component();
            comp.type = Component.TYPE_PUZZLE;
            //value = PuzzleActivity.MAX_SCORE;
         }
        
         else if (type == Component.TYPE_WORD) {
           if (isTablet) { //If we're running on a tablet:
               comp = new Component();
               comp.type = Component.TYPE_WORD;
               value = Wordsearch.MAX_SCORE;
            }
            else {
               comp = null; //Word game will not be added
               value = 0;
            }
         }/**
         else if (type == Component.TYPE_DROPLET) {
            comp = new Component();
            comp.type = Component.TYPE_DROPLET;
            value = DropletActivity.MAX_SCORE;
         }**/
         else if (type == Component.TYPE_STORY) {
            if (!isTablet) //If we're not running a tablet, we will not allow background customization:
               bgId = null;
            comp = new Story(bgId, image, story_text, long_text);
            value = 0;
         }
         else if (type == Component.TYPE_ENDING) {
            comp = new Story(bgId, image, story_text, long_text);
            comp.type = Component.TYPE_ENDING;
            value = 0;
         }
         //Check that we actually made sense of the component:
         if (comp != null) {
            //Check if there was a ent-popup:
            if (ent != null)
               comp.ent = ent;
            screens.add(comp);
            Player.MAX_SCORE += value; //Only update the max_score if the screen was added in
         }

         ent = null; //Delete our local reference to ent-popup, so it wont "leak" to other screens
      }
      currentValue.delete(0, currentValue.length());
   }

   /** Called to get tag characters ( ex:- <name>AndroidPeople</name>
    * -- to get AndroidPeople Character ) */
   @Override
   public void characters(final char[] ch, final int start, final int length)
         throws SAXException {
      currentValue.append(ch, start, length);
   }
}