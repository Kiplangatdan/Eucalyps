package eucalyps;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Resources;
import android.graphics.Color;
import android.media.AudioManager;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import eucalyps.mine.R;

public class Wordsearch extends MainActivity {
   public static final int MAX_SCORE = 20;
   public LinearLayout foundWords;
   public TextView currentWord;
   private String TAREYOUSURE;
   private String TYES;
   private String TNO;
   private TextView clues;
   public TextView counter;
   private WordPanel game;
   private Button giveupBtn;
   public TextView currentScores;
   public int score=0;

   @Override
   protected void onCreate(final Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
      //Remove title bar:
      requestWindowFeature(Window.FEATURE_NO_TITLE);
      //Remove the system status-bar, and keep the screen on always
      getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
      setContentView(R.layout.word_find);
      setUpViews();
      callClues();
      takeKeyEvents(true);
      if (savedInstanceState != null)
         onRestoreInstanceState(savedInstanceState);
   }
   
   @Override
   public void onSaveInstanceState(final Bundle save) {
      super.onSaveInstanceState(save);
      save.putInt("score", score);
      for (int i = 0; i < game.wordAnswers.size(); i++) {
         final TextView t = (TextView)foundWords.getChildAt(i+1);
         save.putString("ans"+i, t.getText().toString());
      }
      game.onSaveState(save);
   }

   @Override
   public void onRestoreInstanceState(final Bundle bundle) {
      super.onRestoreInstanceState(bundle);
      score = bundle.getInt("score");
      game.onRestoreState(bundle);
      for (int i = 0; i < game.wordAnswers.size(); i++) {
         final TextView t = (TextView)foundWords.getChildAt(i+1);
         t.setText(bundle.getString("ans"+i));
      }
   }

   private void callClues() {
      final Resources res = getResources();
      for (int i = 1; i <= 10; i++)
         clues.append(""+ i + ". " + res.getString(res.getIdentifier("string/hint"+i, null, getPackageName())) + "\n");
   }

   private void setUpViews() {
      final Resources res = getResources();
      Bundle b=getIntent().getExtras();
      if(b!=null)
       score=b.getInt("SCORE");
      TAREYOUSURE = res.getString(R.string.dialog_question);
      TYES = res.getString(R.string.dialog_yes);
      TNO = res.getString(R.string.dialog_no);
      game = (WordPanel) findViewById(R.id.gridview);
      foundWords = (LinearLayout) findViewById(R.id.foundWords);
      counter = (TextView) findViewById(R.id.counter);
      counter.setText("0 / 10");
      clues = (TextView) findViewById(R.id.clues);
      currentScores=(TextView)findViewById(R.id.current_scores);
      currentScores.append(""+score);
      currentWord = (TextView) findViewById(R.id.current);
      //This button is for testing only:to allow moving
      giveupBtn = (Button) findViewById(R.id.giveup_btn);
      giveupBtn.setOnClickListener(new View.OnClickListener() {
         public void onClick(final View v) {
            sound.play(click, 1, 1, 1, 0, 1f);
            showDialog(2);
         }
      });

      final TextView t = new TextView(getBaseContext());
      t.setTextColor(Color.GREEN);
      t.setText("THE CORRECT WORDS FOUND\n=======================");
      foundWords.addView(t);
      for (int i = 1; i <= 10; i++) {
         final TextView temp = new TextView(getBaseContext());
         temp.setText(""+i+".");
         temp.setTextColor(Color.GREEN);
         foundWords.addView(temp);
      }
   }

   @Override
   protected Dialog onCreateDialog(final int id) {
      AlertDialog.Builder dlgAlert;
      dlgAlert = new AlertDialog.Builder(this);
      if (id == 1) {
         dlgAlert.setTitle("Congratulations");
         dlgAlert.setMessage("You found all the words");
         dlgAlert.setPositiveButton(R.string.cont, new DialogInterface.OnClickListener() {
            public void onClick(final DialogInterface dialog, final int which) {
               sound.play(click, 1, 1, 1, 0, 1f);
               setResult(MAX_SCORE);
              finish();
              Intent i = new Intent(Wordsearch.this,DiversionActivity.class);
              //i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
              startActivity(i);
            }
         });
         dlgAlert.setCancelable(false);
      }
      else if (id == -1) {
         dlgAlert.setMessage(TAREYOUSURE);
         dlgAlert.setPositiveButton(TYES, new DialogInterface.OnClickListener() {
            public void onClick(final DialogInterface dialog, final int id) {
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
      else if (id == 2) {
         dlgAlert.setMessage("Are you sure you want to give up and continue?");
         dlgAlert.setPositiveButton(TYES, new DialogInterface.OnClickListener() {
            public void onClick(final DialogInterface dialog, final int id) {
               sound.play(click, 1, 1, 1, 0, 1f);
               setResult((int)((float)game.counter/(float)game.wordAnswers.size()*(float)MAX_SCORE));
               finish();
            {
               Intent i = new Intent(Wordsearch.this,DiversionActivity.class);
            	  startActivity(i);
            }
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
   
   //By overriding these methods we get to tell the system that we are handling (or blocking, in this case) the buttons,
   //and do not want the system to do anything when a button is pressed.
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
      return true;
   }

   @Override
   public boolean onKeyUp(final int keyCode, final KeyEvent msg) {
      return true;
   }




}
