package eucalyps;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Arrays;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.media.AudioManager;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import eucalyps.mine.R;
import fr.uoe.eucalyps.helper.Player;

/**
 * This is the activity for feature 2 in the dashboard application. It displays
 * some text and provides a way to get back to the home activity.
 * 
 */
@SuppressLint("NewApi")
public class ScoreActivity extends MainActivity {
   private String name;
   private int score_value;
   private ArrayList<Player> scoreslist = new ArrayList<Player>();
   private ListView list;

   @Override
   protected void onCreate(final Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
      //Remove title bar:
      requestWindowFeature(Window.FEATURE_NO_TITLE);
      //Remove the system status-bar, and keep the screen on always
      getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
      setContentView(R.layout.table);
      list = (ListView) findViewById(R.id.listtable);
      list.addHeaderView(setHeader());
      final Button playAgain_btn = (Button)findViewById(R.id.playagain_btn);
      final TextView startAgain=(TextView)findViewById(R.id.start_againtext);
      final boolean isSetScore = readScoreTable();
      final Bundle b = getIntent().getExtras();
      if (!isSetScore) {
         storeScore();
      }
      if (b != null) {
         score_value = b.getInt("SCORE");
         if (scoreslist.size() < 10 || score_value >= scoreslist.get(scoreslist.size()-1).points)
            showRegisterPointDialog();
         startAgain.setText("Do you want to retry the game? Click play again button");
      }
      else{
         playAgain_btn.setVisibility(View.GONE);
         startAgain.setVisibility(View.GONE);
      }

      list.setAdapter(new ScoreAdapter(this,
            android.R.layout.simple_list_item_1, scoreslist));
      playAgain_btn.setOnClickListener(new View.OnClickListener() {
         public void onClick(final View v) {
            sound.play(click, 1, 1, 1, 0, 1f);
            finish();
            startActivity(new Intent(ScoreActivity.this,Game.class));
         }
      });
   }//end Oncreate method

   private void showRegisterPointDialog() {
      final EditText input_name = new EditText(this);
      final AlertDialog d = new AlertDialog.Builder(this)
      .setTitle("Save Scores")
      .setView(input_name)
      .setMessage("Save your scores on high score table")
      .setCancelable(false)
      .setPositiveButton(android.R.string.ok,
            new Dialog.OnClickListener() {
         public void onClick(final DialogInterface d, final int which) {
            // Do nothing here. We override the onclick
         }
      })
      .create();

      d.setOnShowListener(new DialogInterface.OnShowListener() {

         public void onShow(final DialogInterface dialog) {

            final Button b = d.getButton(AlertDialog.BUTTON_POSITIVE);
            b.setOnClickListener(new View.OnClickListener() {
               public void onClick(final View view) {
                  sound.play(click, 1, 1, 1, 0, 1f);
                  name = input_name.getText().toString().trim();
                  if (name.length() > 0) {
                     sortAndSave();
                     d.dismiss();
                     list.invalidate();
                     list.invalidateViews();
                  }
                  else {
                     toast("Please enter your name");
                     input_name.setText("");
                  }
               }
            });
         }
      });
      d.show();
   }

   private void sortAndSave() {
      scoreslist.add(0, new Player(name, score_value));
      final Object[] players = scoreslist.toArray();
      Arrays.sort(players);
      scoreslist.clear();
      for (int i = 0; i < players.length; i++) {
         scoreslist.add((Player) players[i]);
      }

      if (scoreslist.size() > 10) 
         scoreslist.remove(scoreslist.size()-1);
      
      storeScore();
   }

   @SuppressWarnings("unchecked")
   public boolean readScoreTable() {
      try {
         final ObjectInputStream objectInput = new ObjectInputStream(
               openFileInput("EntPlayerScores.dat"));
         scoreslist = (ArrayList<Player>) (objectInput.readObject());
         objectInput.close();
      } catch (final NullPointerException virhe) { // File not found
         //virhe.printStackTrace();
         return false;
      } catch (final ClassNotFoundException cnfe) {
         //cnfe.printStackTrace();
      } catch (final IOException virhe) {
         //virhe.printStackTrace();
         return false;
      }
      return true;
   }

   public void storeScore() {
      try {
         // The old data is overridden:
         final ObjectOutputStream objectOutput = new ObjectOutputStream(
               openFileOutput("EntPlayerScores.dat", Context.MODE_PRIVATE));
         // Save stuff in binary:
         objectOutput.writeObject(scoreslist);
         // Close the stream:
         objectOutput.close();
      } // try
      catch (final IOException e) {
         System.out.println("IOE");
      }
   }

   private View setHeader() {
      final LayoutInflater vi = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
      final View v = vi.inflate(R.layout.tableheader, null);
      final TextView nrow = (TextView) v.findViewById(R.id.nrohead);
      nrow.setText("#");
      final TextView name = (TextView) v.findViewById(R.id.namehead);
      name.setText("Player Name");
      final TextView points = (TextView) v.findViewById(R.id.scorehead);
      points.setText("Score");
      return v;

   }

   class ScoreAdapter extends ArrayAdapter<Player> {
      private final ArrayList<Player> score;

      public ScoreAdapter(final Context context, final int textViewResourceId,
            final ArrayList<Player> score) {
         super(context, textViewResourceId, score);
         this.score = score;
      }

      @Override
      public View getView(final int position, final View convertView, final ViewGroup parent) {
         View v = convertView;
         if (v == null) {
            final LayoutInflater vi = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = vi.inflate(R.layout.row, null);
         }

         final Player currentScore = score.get(position);
         if (currentScore != null) {
            final TextView nrow = (TextView) v.findViewById(R.id.nro);
            final TextView name = (TextView) v.findViewById(R.id.name);
            final TextView points = (TextView) v.findViewById(R.id.rowscores);
            if (nrow != null)
               nrow.setText(String.valueOf(position + 1));
            if (name != null)
               name.setText(currentScore.name);
            if (points != null)
               points.setText(String.valueOf(currentScore.points));
         }
         return v;
      }
   }
   
   @Override
   public boolean onKeyDown(final int keyCode, final KeyEvent msg) {
      if (keyCode == KeyEvent.KEYCODE_BACK) {
         sound.play(click, 1, 1, 1, 0, 0.5f);
         finish();
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
} // end class
