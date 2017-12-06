package eucalyps;

import java.util.ArrayList;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;


import eucalyps.HomeActivity;
import eucalyps.mine.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

public class EucalypsActivity extends Activity {
	 public int QUIT = -1;
   public static final int ASK_LANGUAGE = 100;
   public static final int UNSET = -1;
   public static final int PORTUGUESE = 0;
   public static final int ENGLISH = 1;

   private ArrayList<StoryScreen> story; //Contains the story
   private int storyIndex = 0;
   private int language;

   /** Called when the activity is first created. */
   @Override
   public void onCreate(final Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      story = parseXML(); //Parse the story from XML
      //If we are resuming from previous instance:
      if (savedInstanceState != null)
         onRestoreInstanceState(savedInstanceState);
      else //If not, use defaults:
         //language = UNSET;
    	  run(storyIndex);
   }
   /**
   @Override
   public void onResume() {
      super.onResume();
      if (language == UNSET)
         showDialog(ASK_LANGUAGE);
   }
   
   @Override
   public void onSaveInstanceState(final Bundle save) {
      save.putInt("language", language);
      super.onSaveInstanceState(save);
   }

   @Override
   public void onRestoreInstanceState(final Bundle bundle) {
      language = bundle.getInt("language");
   }
    **/

   protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
      if (resultCode == RESULT_OK){
         storyIndex = story.get(storyIndex).next_screen[0];
      } else if(resultCode == RESULT_CANCELED){
         storyIndex = StoryScreen.THE_END;
      } else if (resultCode == ChoiceActivity.TRUE_ENDING){
         storyIndex = story.get(storyIndex).next_screen[1];
         System.out.println("storyIndexTRUE " + storyIndex);
      } else if (resultCode == ChoiceActivity.FICTIVE_ENDING){
         storyIndex = story.get(storyIndex).next_screen[0];
         System.out.println("storyIndexFALSE " + storyIndex);
      }


      if (storyIndex != StoryScreen.THE_END)
         run(storyIndex);
      else {
    	  Intent intent = new Intent(EucalypsActivity.this, Game.class);
    	  intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
    	  startActivity(intent);
    	  finish();
    	  //Intent i = new Intent(EucalypsActivity.this, Game.class);
    	  //startActivity(i);
    	  
      }
   }

   public void run(final int storyIndex){
      Intent intent = new Intent();
      if(story.get(storyIndex).type == StoryScreen.STORY_SCREEN)
         intent = new Intent(EucalypsActivity.this, StoriActivity.class);
      else if(story.get(storyIndex).type == StoryScreen.CHOICE_SCREEN)
         intent = new Intent(EucalypsActivity.this, ChoiceActivity.class);
      else if(story.get(storyIndex).type == StoryScreen.STATIC_SCREEN)
         intent = new Intent(EucalypsActivity.this, StaticActivity.class);

      final Bundle b = new Bundle();
      b.putInt("image", story.get(storyIndex).image);
      //b.putString("text", story.get(storyIndex).getText(language));
      b.putInt("audio", story.get(storyIndex).audio);
      intent.putExtras(b);
      startActivityForResult(intent,1);
   }

   private ArrayList<StoryScreen> parseXML() {
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
         xr.parse(new InputSource(getResources().openRawResource(R.raw.stori)));
      } catch (final Exception e) {
         e.printStackTrace();
         return null;
      }
      return XMLHandler.screens;
   }
   /**
   @Override
   protected Dialog onCreateDialog(final int id) {
      Dialog dialog = null;
      if (id == ASK_LANGUAGE) {
         final AlertDialog.Builder dlgAlert = new AlertDialog.Builder(this);
         dlgAlert.setMessage("Select language");
         dlgAlert.setPositiveButton("Swahili", new DialogInterface.OnClickListener() {
            public void onClick(final DialogInterface dialog, final int id) {
               language = PORTUGUESE;
               run(storyIndex);
            }
         });
         dlgAlert.setNegativeButton("English", new DialogInterface.OnClickListener() {
            public void onClick(final DialogInterface dialog, final int id) {
               language = ENGLISH;
               run(storyIndex);
            }
         });
         dlgAlert.setCancelable(false);
         dialog = dlgAlert.create();
      }
      return dialog;
   }
   public void goHome(Context context) {
		final Intent intent = new Intent(context, HomeActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		context.startActivity(intent);
	}
**/
	/**
	 * Use the activity label to set the text in the activity's title text view.
	 * The argument gives the name of the view.
	 * 
	 * <p>
	 * This method is needed because we have a custom title bar rather than the
	 * default Android title bar. See the theme definitons in styles.xml.
	 * 
	 * @param textViewId
	 *            int
	 * @return void
	 */

	public void setTitleFromActivityLabel(int textViewId) {
		TextView tv = (TextView) findViewById(textViewId);
		if (tv != null)
			tv.setText(getTitle());
	} // end setTitleText

	/**
	 * Show a string on the screen via Toast.
	 * 
	 * @param msg
	 *            String
	 * @return void
	 */

	public void toast(String msg) {
		Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
	} // end toast

	/**
	 * Send a message to the debug log and display it using Toast.
	 */
	public void trace(String msg) {
		Log.d("Demo", msg);
		toast(msg);
	}

} // end class


