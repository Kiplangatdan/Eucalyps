package eucalyps;

import java.util.ArrayList;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.media.AudioManager;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.text.util.Linkify;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.KeyEvent;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import fr.uoe.eucalyps.helper.Question;
import eucalyps.mine.R;

public class ImageQuiz extends MainActivity {
   private Bundle b = null;
   private Question currentQuestion = null;
   private final ArrayList<ImageView> imgViews = new ArrayList<ImageView>();
   private ImageView image_01;
   private ImageView image_02;
   private ImageView image_03;
   private ImageView image_04;
   private TextView storyText;
   private TextView instructionText;
   private TextView currentScore;
   private int score;
   protected String TAREYOUSURE;
   protected String TYES, TNO;
   protected String TCORRECT, TWRONG;
   protected String TSELECT;

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
      setContentView(R.layout.image_quize_layout);
      b = getIntent().getExtras();
      setUpView();
      loadQuizImages();
   }

   private void setUpView() {
      if (b.containsKey("SCORE"))
         score = b.getInt("SCORE");
      final Resources res = this.getResources();
      currentScore = (TextView) findViewById(R.id.current_scores);
      currentScore.setText("Scores :" + score);
      image_01 = (ImageView) findViewById(R.id.quizimage1);
      image_02 = (ImageView) findViewById(R.id.quizimage2);
      image_03 = (ImageView) findViewById(R.id.quizimage3);
      image_04 = (ImageView) findViewById(R.id.quizimage4);
      storyText = (TextView) findViewById(R.id.storytext);
      imgViews.add(image_01);
      imgViews.add(image_02);
      imgViews.add(image_03);
      imgViews.add(image_04);
      instructionText = (TextView) findViewById(R.id.instruction);
      TCORRECT = res.getString(R.string.correctAnswer);
      TWRONG = res.getString(R.string.wrongAnswer);
      TSELECT = res.getString(R.string.select_msg);
      TAREYOUSURE = res.getString(R.string.dialog_question);
      TYES = res.getString(R.string.dialog_yes);
      TNO = res.getString(R.string.dialog_no);
   }

   private void loadQuizImages() {
      currentQuestion = (Question) b.getSerializable("IMG");
      currentQuestion.value = currentQuestion.choices.size();
      storyText.setText(Html.fromHtml(currentQuestion.storyText));
      Linkify.addLinks(storyText, Linkify.WEB_URLS);
      instructionText.setText(Html.fromHtml(currentQuestion.question));
      Linkify.addLinks(instructionText, Linkify.WEB_URLS);
      // set image, if available
      if(!imgViews.isEmpty()) {
         for (int i = 0; i < currentQuestion.choices.size(); i++){
            if(imgViews.get(i) != null && currentQuestion.choices.get(i) != null)
               setImages(imgViews.get(i), currentQuestion.choices.get(i));
         }
      }
   }

   private void setImages(final ImageView img, final String imgUrl) {
      if (imgUrl != null && imgUrl.trim().length() > 0) {
         final int id = getResources().getIdentifier(
               "drawable/" + imgUrl.trim(), null, getPackageName());
         if (id == 0) {
            img.setVisibility(View.INVISIBLE);
            return;
         }
         img.setImageResource(id);
         img.setVisibility(View.VISIBLE);
         img.setOnTouchListener(new OnTouchListener () {
            public boolean onTouch(View v, MotionEvent event) {
               if (event.getAction() != MotionEvent.ACTION_DOWN)
                  return false;
               
               sound.play(click, 1, 1, 1, 0, 1f);
               int indx = imgViews.indexOf((ImageView)v);
               if (indx == currentQuestion.correctIdx) {
                  // the correct one:
                  if (currentQuestion.ent != null) { // If ent has something to
                     // say:
                     showDialog(ENT_DIALOG);
                     currentScore.setText("Scores :"
                           + String.valueOf(score + currentQuestion.value));
                  } else { // Otherwise just go forward
                     toast(TCORRECT);
                     setResult(currentQuestion.value);
                     finish();
                  }
               }
               else {
                  if (currentQuestion.value > 0)
                     currentQuestion.value -= 1;
                  toast(TWRONG);
               }
               return true;
            }
         });
      } else {
         img.setVisibility(View.GONE);
      }
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
               currentQuestion = null;
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
            d = new Dialog(this, android.R.style.Theme_Light);            
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
}