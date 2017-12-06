package eucalyps;

import java.util.ArrayList;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import eucalyps.mine.R;
import fr.uoe.eucalyps.helper.Component;
import fr.uoe.eucalyps.helper.Player;
import fr.uoe.eucalyps.helper.XMLHandlerStory;

public class Game extends Activity {
   public static final int EXIT = -1;
   private ArrayList<Component> story; //This ArrayList contains the story (order, content, etc)
   public Player player; //This wrapper class contains the player data
   private int storyIndex; //This is the index of the current story screen
   private boolean otherActivityRunning; //This value keeps track if a minigame (quiz, itemlocator etc.) is currently running

   /** Called when the activity is first created. */
   @Override
   public void onCreate(final Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      story = parseXML(); //Parse the story from XML
      //If we are resuming from previous instance:
      if (savedInstanceState != null)
         onRestoreInstanceState(savedInstanceState);
      else { //If not, use defaults:
         player = new Player();
         storyIndex = 0;
         otherActivityRunning = false;
      }
   }

   @Override
   public void onResume() {
      super.onResume();
      //If a minigame is NOT currently running, that means that we are either starting the game or it was paused between the minigames.
      //Either way, we need to start/continue the game:
      if (!otherActivityRunning && storyIndex != EXIT)
         run(storyIndex);
   }

   @Override
   public void onSaveInstanceState(final Bundle save) {
      save.putInt("storyIndex", storyIndex); //Save our current state
      save.putSerializable("player", player); //Save player stats
      save.putBoolean("otherActivityRunning", otherActivityRunning);
      super.onSaveInstanceState(save);
   }

   @Override
   public void onRestoreInstanceState(final Bundle bundle) {
      storyIndex = bundle.getInt("storyIndex"); //Retrieve our previous state
      player = (Player)bundle.getSerializable("player");
      otherActivityRunning = bundle.getBoolean("otherActivityRunning");
      super.onRestoreInstanceState(bundle);
   }

   /**
    * This method parses the story.xml
    * @return The parsed contents of the XML. Contents must be always cast before use.
    */
   private ArrayList<Component> parseXML() {
      XMLHandlerStory XMLHandler;
      //Parse through xml and save the stuff we need
      try {
         /** Handling XML */
         final SAXParserFactory spf = SAXParserFactory.newInstance();
         final SAXParser sp = spf.newSAXParser();
         final XMLReader xr = sp.getXMLReader();

         /** Create handler to handle XML Tags ( extends DefaultHandler ) */
         XMLHandler = new XMLHandlerStory(this);
         xr.setContentHandler(XMLHandler);
         xr.parse(new InputSource(getResources().openRawResource(R.raw.story)));
      } catch (final Exception e) {
         e.printStackTrace();
         return null;
      }
      return XMLHandler.screens;
   }

   protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
      otherActivityRunning = false;
      if (resultCode >= 0) { //If the activity returned a valid player score:
         storyIndex++; //Proceed to the next screen
         player.points += resultCode; //Update player score
      }
      else if (resultCode < 0) //The activity returned negative number, i.e. player wants to quit:
         storyIndex = EXIT; //This is just a random value for now

      if (storyIndex != story.size() && storyIndex != EXIT) //If we have not reached the end:
         run(storyIndex); //Run the next index
      else { //If we reached the end OR the user requested exit
         storyIndex = EXIT; //We want to exit
         if (resultCode >= 0) { //If this is the end of the game:
            final Intent intent = new Intent(getApplicationContext(), DiversionActivity.class);
            final Bundle b = new Bundle();
            b.putInt("SCORE", player.points);
            intent.putExtras(b);
            startActivity(intent); //Launch high scores activity
         }
         finish(); //End this activity. This will show the previous activity in the stack, which is the main menu
      }
   }

   public void run(final int storyIndex) {
      otherActivityRunning = true; //This line might have to be moved after the launching of the activity. Maybe.
      Intent intent = new Intent();
      final Bundle b = new Bundle(); //We use Bundle to transfer the data from this activity to the next
      final Component current = story.get(storyIndex);
      //Determine the type of activity to launch: (this could be done by using keyword 'instanceof', but that would require additional classes.)
      if (current.type == Component.TYPE_STORY) {
         intent = new Intent(Game.this, StoryActivity.class);
         b.putSerializable("STORY", current); //Put the question object inside the bundle
      }
      else if (current.type == Component.TYPE_ENDING) {
         intent = new Intent(Game.this, EndingActivity.class);
         b.putSerializable("STORY", current); //Put the question object inside the bundle
         b.putInt("SCORE", player.points);
      }
      else if (current.type == Component.TYPE_QUIZ) { //This is a quiz-screen
         intent = new Intent(Game.this, Quiz.class);
         b.putSerializable("QUIZ", current);
         b.putInt("SCORE", player.points);
      }/**
      else if (current.type == Component.TYPE_IMAGEQUIZ) { //This is a imagequiz-screen
         intent = new Intent(Game.this, ImageQuiz.class);
         b.putSerializable("IMG", current);
         b.putInt("SCORE", player.points);
      }**/
      else if (current.type == Component.TYPE_ITEM) {
         intent = new Intent(Game.this, ItemLocatorGame.class);
         b.putSerializable("ITEM", current);//Put the FactsInfo object inside the bundle
         b.putInt("SCORE", player.points);//Put the point gained inside bundle to be displayed in the next activity
      }
      else if (current.type == Component.TYPE_PUZZLE) { //This is a puzzle
         intent = new Intent(Game.this, DiversionActivity.class);
         b.putInt("SCORE", player.points);
      }
      
      else if (current.type == Component.TYPE_WORD) { //This is a word-game
         intent = new Intent(Game.this, Wordsearch.class);
         b.putInt("SCORE", player.points);
      }
      /**
      else if (current.type == Component.TYPE_DROPLET) { //This is a droplet-game
         intent = new Intent(Game.this, DropletActivity.class);
         b.putInt("SCORE", player.points);
      }
      **/
      intent.putExtras(b);
      startActivityForResult(intent,1);
   }
}