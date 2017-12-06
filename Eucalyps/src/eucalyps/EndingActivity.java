package eucalyps;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.content.res.Resources;
import android.media.AudioManager;
import android.os.Bundle;
import android.text.Html;
import android.text.util.Linkify;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import eucalyps.mine.R;
import fr.uoe.eucalyps.helper.Player;
import fr.uoe.eucalyps.helper.Story;

public class EndingActivity extends MainActivity {
   // Quit-tekstit:
   protected String TAREYOUSURE;
   protected String TYES, TNO;
   // Toast-tekstit:
   protected String TCORRECT, TWRONG;
   protected String TSELECT;

   @Override
   public void onCreate(final Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
      //Remove title bar:
      requestWindowFeature(Window.FEATURE_NO_TITLE);
      //Remove the system status-bar, and keep the screen on always
      getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
      setContentView(R.layout.end);
      setUpViews();
   }

   private void setUpViews() {
      final Resources res = this.getResources();
      TCORRECT = res.getString(R.string.correctAnswer);
      TWRONG = res.getString(R.string.wrongAnswer);
      TSELECT = res.getString(R.string.select_msg);
      TAREYOUSURE = res.getString(R.string.dialog_question);
      TYES = res.getString(R.string.dialog_yes);
      TNO = res.getString(R.string.dialog_no);
      final Story story = (Story)getIntent().getExtras().getSerializable("STORY");
      // for convenient access
      final TextView shortText = (TextView) findViewById(R.id.story_shorttext);
      shortText.setText(Html.fromHtml(story.short_text));
      Linkify.addLinks(shortText, Linkify.WEB_URLS);
      final TextView longText = (TextView) findViewById(R.id.story_longtext);
      longText.setText(Html.fromHtml(story.long_text));
      Linkify.addLinks(longText, Linkify.WEB_URLS);
      final Button continueBtn = (Button) findViewById(R.id.story_continue);
      continueBtn.setOnClickListener(new OnClickListener() {
         public void onClick(final View v) {
            sound.play(click, 1, 1, 1, 0, 1f);
            setResult(0);
            finish();
         }
      });
      //Score text:
      final TextView scoreText = (TextView) findViewById(R.id.score_text);
      final int score = getIntent().getExtras().getInt("SCORE");
      final float per = (float)score/(float)Player.MAX_SCORE;
      if (per >= 0.9)
         scoreText.setText("You are an ULTIMATE FORESTER. " + score + " points!");
      else if (per >= 0.8)
    	  scoreText.setText("Consider yourself being a Forester. " + score + " points!");
      else if (per >= 0.7)
    	  scoreText.setText("You're ranked a Forest Guide. " + score + " points!");
      else if (per >= 0.6)
    	  scoreText.setText("You're ranked a Forest Ranger. " + score + " points!");
      else if (per >= 0.5)
    	  scoreText.setText("You've got your basic knowledge in Forestry. " + score + " points!");
      else 
    	  scoreText.setText("You've been sleeping too much in class! " + score + " points!");
   }

   @Override
   protected Dialog onCreateDialog(final int id) {
      AlertDialog.Builder dlgAlert;
      dlgAlert = new AlertDialog.Builder(this);
      if (id == QUIT) {
         dlgAlert.setMessage(TAREYOUSURE);
         dlgAlert.setPositiveButton(TYES,
               new DialogInterface.OnClickListener() {
            public void onClick(final DialogInterface dialog,
                  final int id) {
               System.gc();
               sound.play(click, 1, 1, 1, 0, 1f);
               setResult(-1);
               finish();
            }
         });
         dlgAlert.setNegativeButton(TNO, new DialogInterface.OnClickListener() {
            public void onClick(final DialogInterface dialog, final int id) {
               sound.play(click, 1, 1, 1, 0, 1f);
            }
         });
         dlgAlert.setCancelable(false);
      }
      return dlgAlert.create();
   }

   @Override
   public boolean onKeyDown(final int keyCode, final KeyEvent msg) {
      if (keyCode == KeyEvent.KEYCODE_BACK) {
         sound.play(click, 1, 1, 1, 0, 0.5f);
         showDialog(QUIT);
         return true;
      } 
      else if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) {
         audio.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_LOWER, AudioManager.FLAG_SHOW_UI);
         sound.play(click, 1, 1, 1, 0, 0.75f);
         return true;
      }
      else if (keyCode == KeyEvent.KEYCODE_VOLUME_UP) {
         audio.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_RAISE, AudioManager.FLAG_SHOW_UI);
         sound.play(click, 1, 1, 1, 0, 1.25f);
         return true;
      } else
         return super.onKeyDown(keyCode, msg);
   }
}