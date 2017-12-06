package eucalyps;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Color;
import android.media.AudioManager;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.text.util.Linkify;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import eucalyps.mine.R;
import fr.uoe.eucalyps.helper.Player;
import fr.uoe.eucalyps.helper.Question;

public class Quiz extends MainActivity {
   private Question currentQuestion = null;
   private TextView text;
   private TextView question;
   private Toast toast;
   // Quit-tekstit:
   protected String TAREYOUSURE;
   protected String TYES, TNO;
   // Toast-tekstit:
   protected String TCORRECT, TWRONG;
   protected String TSELECT;
   Player player = new Player();
   // NB: image paths should be stored in the database.
   private ImageView img;
   private TextView currentScore;
   private int score;
   private Bundle b = null;

   @Override
   public void onCreate(final Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
      // Remove title bar:
      requestWindowFeature(Window.FEATURE_NO_TITLE);
      // Remove the system status-bar, and keep the screen on always
      getWindow().addFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN
            | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
      setContentView(R.layout.quize_layout);
      b = getIntent().getExtras();
      setUpViews();
      loadQuestion();
   }

   private void setUpViews() {
      final Resources res = this.getResources();
      if (b.containsKey("SCORE"))
         score = b.getInt("SCORE");
      TCORRECT = res.getString(R.string.correctAnswer);
      TWRONG = res.getString(R.string.wrongAnswer);
      TSELECT = res.getString(R.string.select_msg);
      TAREYOUSURE = res.getString(R.string.dialog_question);
      TYES = res.getString(R.string.dialog_yes);
      TNO = res.getString(R.string.dialog_no);
      toast = Toast.makeText(this, "", Toast.LENGTH_LONG);
      currentScore = (TextView) findViewById(R.id.current_scores);
      currentScore.setText("Scores :" + String.valueOf(score));

      // for convenient access
      text = (TextView) findViewById(R.id.storytext);
      question = (TextView) findViewById(R.id.question);
      img = (ImageView) findViewById(R.id.quizimage);
      final Button selectBtn = (Button) findViewById(R.id.quiz_select);
      selectBtn.setOnClickListener(new OnClickListener() {
         public void onClick(final View v) {
            sound.play(click, 1, 1, 1, 0, 1f);
            sendAnswer();
         }
      });

      final Button hintBtn = (Button) findViewById(R.id.quiz_hint);
      hintBtn.setOnClickListener(new OnClickListener() {
         public void onClick(final View v) {
            sound.play(click, 1, 1, 1, 0, 1f);
            showHint();
         }
      });

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
               toast.cancel();
               currentQuestion = null;
               text = null;
               question = null;
               toast = null;
               sound.play(click, 1, 1, 1, 0, 1f);
               System.gc();
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
         return dlgAlert.create();
      } else if (id == ENT_DIALOG) {
         final View v = getLayoutInflater().inflate(R.layout.ent_dialog,
               null);
         final TextView t = (TextView) v.findViewById(R.id.ent_text);
         t.setText(Html.fromHtml(currentQuestion.ent.text));
         Linkify.addLinks(t, Linkify.WEB_URLS);
         // set image, if available
         if (currentQuestion.ent.imgId != null
               && currentQuestion.ent.imgId.trim().length() > 0) {
            final int imgid = getResources().getIdentifier(
                  "drawable/" + currentQuestion.ent.imgId.trim(), null,
                  getPackageName());
            ((ImageView) v.findViewById(R.id.ent_image))
            .setImageResource(imgid);
         }
         final Button cont = (Button) v.findViewById(R.id.ent_button);
         cont.setOnClickListener(new OnClickListener() {
            public void onClick(final View arg0) {
               sound.play(click, 1, 1, 1, 0, 1f);
               setResult(currentQuestion.value);
               finish();
            }
         });
         Dialog d = null;
         if (Build.VERSION.SDK_INT >= 11) { //If we are running Honeycomb or newer:
            d = new Dialog(this, android.R.style.Theme_Dialog);            
         }
         else { //Older (Holo theme is not available): 
            d = new Dialog(this, android.R.style.Theme_Dialog);
         }
         //Check if tablet or phone:
         if (Build.VERSION.SDK_INT >= 9 && (getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) ==
               Configuration.SCREENLAYOUT_SIZE_LARGE) { //If we're running on a tablet:
            d.setTitle(R.string.correctAnswer); //Tablets use standard layout with built-in title  
         }
         else
            d.requestWindowFeature(Window.FEATURE_NO_TITLE); //We'll add the title ourselves in the layout
         d.setContentView(v);
         d.setCancelable(false);
         return d;
      }
      return null;
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

   private void showHint() {
      toast.setText("");
      toast.setText(currentQuestion.hint);
      toast.show();
   }

   private void sendAnswer() {
      final RadioGroup rg = (RadioGroup) findViewById(R.id.quiz_radiogroup);
      final int rbId = rg.getCheckedRadioButtonId();
      if (rbId == -1) {
         toast.setText("");
         toast.setText(TSELECT);
         toast.show();
         return;
      }

      if (currentQuestion != null) {
         // Get the index of the selected radiobutton:
         final View radioButton = rg.findViewById(rbId);
         final int idx = rg.indexOfChild(radioButton);

         if (idx == currentQuestion.correctIdx) { // If the given answer is
            // the correct one:
            if (currentQuestion.ent != null) { // If ent has something to
               // say:
               showDialog(ENT_DIALOG);
               currentScore.setText("Scores :"
                     + String.valueOf(score + currentQuestion.value));
            } else { // Otherwise just go forward
               toast.setText("");
               toast.setText(TCORRECT);
               toast.show();
               setResult(currentQuestion.value);
               finish();
            }
         } else {
            if (currentQuestion.value > 0)
               currentQuestion.value -= 1;
            toast.setText("");
            toast.setText(TWRONG);
            toast.show();
         }
         return;
      }
   }

   private void loadQuestion() {
      currentQuestion = (Question) b.getSerializable("QUIZ");
      currentQuestion.value = currentQuestion.choices.size();
      text.setText(Html.fromHtml(currentQuestion.storyText));
      Linkify.addLinks(text, Linkify.WEB_URLS);
      question.setText(Html.fromHtml(currentQuestion.question));
      Linkify.addLinks(question, Linkify.WEB_URLS);
      // set image, if available
      if (currentQuestion.imgId != null
            && currentQuestion.imgId.trim().length() > 0) {
         final int id = getResources().getIdentifier(
               "drawable/" + currentQuestion.imgId.trim(), null,
               getPackageName());
         img.setImageResource(id);
         img.setVisibility(View.VISIBLE);
      } else {
         img.setVisibility(View.GONE);
      }

      // choices
      final RadioGroup rg = (RadioGroup) this
            .findViewById(R.id.quiz_radiogroup);

      rg.clearCheck();
      rg.removeAllViews();
      RadioButton rb;
      for (final String c : currentQuestion.choices) {
         rb = new RadioButton(rg.getContext());
         rb.setText(Html.fromHtml(c));
         Linkify.addLinks(rb, Linkify.WEB_URLS);
         rb.setOnClickListener(new OnClickListener() {
            public void onClick(final View v) {
               sound.play(click, 1, 1, 1, 0, 1f);
            }
         });
         rb.setTextColor(Color.BLACK);
         rb.setTextSize(22f);
         rg.addView(rb);
      }
      rg.invalidate();
   }
}